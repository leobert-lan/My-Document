package com.lht.pan_android.activity.asyncProtected;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.bean.ReportBean;
import com.lht.pan_android.clazz.Events.ReportItemClickEvent;
import com.lht.pan_android.util.CloudBoxApplication;
import com.lht.pan_android.util.ToastUtil;
import com.lht.pan_android.util.ToastUtil.Duration;
import com.lht.pan_android.util.internet.HttpRequestFailureUtil;
import com.lht.pan_android.util.internet.HttpUtil;
import com.lht.pan_android.view.popupwins.CustomDialog;
import com.lht.pan_android.view.popupwins.CustomPopupWindow.OnPositiveClickListener;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ShareReportActivity extends AsyncProtectedActivity {

	private final static String PAGENAME = "ShareReportActivity";

	private ReportBean reportBean;

	private ListView listviewReasons;

	private ProgressBar pb;

	private Button submit;

	private MAdapter adapter;

	private CustomDialog dialog;

	private EditText etContact;

	private HttpUtil mHttpUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_report);

		CloudBoxApplication.addActivity(this);
		mHttpUtil = new HttpUtil();
		EventBus.getDefault().register(this);
		pb = (ProgressBar) findViewById(R.id.report_progress);
		submit = (Button) findViewById(R.id.report_btn_report);
		etContact = (EditText) findViewById(R.id.report_et_contact);

		listviewReasons = (ListView) findViewById(R.id.report_list_desc);
		adapter = new MAdapter(getLayoutInflater(), generateData());
		listviewReasons.setAdapter(adapter);

		findViewById(R.id.report_btn_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ShareReportActivity.this.finish();
			}
		});

		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				reportBean.setContact(etContact.getText().toString());
				report(reportBean);
			}
		});

		dialog = new CustomDialog(this, this);
	}

	OnPositiveClickListener successPcl = new OnPositiveClickListener() {

		@Override
		public void onPositiveClick() {
			ShareReportActivity.this.finish();
		}
	};

	OnPositiveClickListener failurePcl = new OnPositiveClickListener() {

		@Override
		public void onPositiveClick() {
			submit.performClick();
		}
	};

	@Override
	protected void onDestroy() {
		mHttpUtil.getClient().cancelRequests(this, true);
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Subscribe
	public void onEventMainThread(ReportItemClickEvent event) {
		submit.setEnabled(adapter.getSelectedCount() > 0);
	}

	protected void report(ReportBean bean) {
		showWaitView(true);
		JSONArray jArray = adapter.getSelectedReason();
		bean.setReport_desc(JSON.toJSONString(jArray));

		// TODO
		// String url = "http://192.168.1.16:3000/v3/report/share";
		String url = IUrlManager.ShareReport.DOMAIN + IUrlManager.ShareReport.FUNCTION;
		mHttpUtil.postWithEntity(this, url, getEntity(bean), "application/json", new AsyncHttpResponseHandler() {

			long start = System.currentTimeMillis();

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				cancelWaitView();
				dialog.setContent(R.string.dialog_report_content_success);
				dialog.setPositiveButton(R.string.dialog_report_ok);
				dialog.setPositiveClickListener(successPcl);
				dialog.changeView2Single();
				dialog.show();
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				// TODO Auto-generated method stub
				cancelWaitView();
				if (arg0 == 0) {
					if (System.currentTimeMillis() - start > 3000) {
						dialog.setContent(R.string.dialog_report_content_failure);
						dialog.setPositiveButton(R.string.dialog_report_retry);
						dialog.setPositiveClickListener(failurePcl);
						dialog.changeView2Single();
						dialog.show();
					} else {
						ToastUtil.show(getActivity(), R.string.no_internet, Duration.s);
					}
				} else {
					HttpRequestFailureUtil util = new HttpRequestFailureUtil(getActivity());
					util.handleFailureWithCode(arg0, true);
				}

			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		String s = getIntent().getStringExtra("data");
		reportBean = JSON.parseObject(s, ReportBean.class);
	}

	@Override
	protected ProgressBar getProgressBar() {
		return pb;
	}

	@Override
	protected String getPageName() {
		return ShareReportActivity.PAGENAME;
	}

	@Override
	protected UMengActivity getActivity() {
		return ShareReportActivity.this;
	}

	private StringEntity getEntity(ReportBean bean) {
		StringEntity entity = null;
		try {
			entity = new StringEntity(JSON.toJSONString(bean), HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return entity;
	}

	private ArrayList<DataModel> generateData() {
		ArrayList<DataModel> ret = new ArrayList<ShareReportActivity.DataModel>();
		ret.add(new DataModel("色情"));
		ret.add(new DataModel("政治谣言"));
		ret.add(new DataModel("常识性谣言"));
		ret.add(new DataModel("封建迷信"));
		ret.add(new DataModel("非法出版"));
		ret.add(new DataModel("盗用原版"));
		ret.add(new DataModel("其他侵权类"));
		return ret;
	}

	private static class DataModel {

		String reason;
		boolean isSelected;

		DataModel(String reason) {
			this(reason, false);
		}

		DataModel(String reason, boolean b) {
			this.reason = reason;
			this.isSelected = b;
		}
	}

	private static class MAdapter extends BaseAdapter {

		class ViewHolder {
			TextView tv;
			CheckBox cb;
		}

		private ArrayList<DataModel> mLiData;

		private LayoutInflater inflater;

		MAdapter(LayoutInflater inflater, ArrayList<DataModel> mLiData) {
			this.mLiData = mLiData;
			this.inflater = inflater;
		}

		@Override
		public int getCount() {
			return mLiData.size();
		}

		@Override
		public Object getItem(int position) {
			return mLiData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.list_item_reportreason, parent, false);
				holder = new ViewHolder();

				holder.cb = (CheckBox) convertView.findViewById(R.id.report_listitem_cb_choose);
				holder.tv = (TextView) convertView.findViewById(R.id.report_listitem_txt_reason);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// ============code section :set========================
			DataModel model = (DataModel) getItem(position);

			holder.cb.setChecked(model.isSelected);
			holder.cb.setTag(position);
			holder.tv.setText(model.reason);

			holder.cb.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;
					Integer p = (Integer) cb.getTag();
					final boolean b = ((DataModel) getItem(p)).isSelected;
					cb.setChecked(!b);
					((DataModel) getItem(p)).isSelected = !b;
					EventBus.getDefault().post(new ReportItemClickEvent());
				}
			});

			holder.tv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (v.getParent() != null && v.getParent() instanceof ViewGroup) {
						ViewGroup vp = (ViewGroup) v.getParent();
						vp.findViewById(R.id.report_listitem_cb_choose).performClick();
					}
				}
			});

			return convertView;
		}

		public JSONArray getSelectedReason() {
			JSONArray jArray = new JSONArray();
			for (int i = 0; i < getCount(); i++) {
				DataModel model = (DataModel) getItem(i);
				if (model.isSelected)
					jArray.add(model.reason);
			}
			return jArray;
		}

		public int getSelectedCount() {
			int count = 0;
			for (int i = 0; i < getCount(); i++) {
				DataModel model = (DataModel) getItem(i);
				if (model.isSelected)
					count++;
			}
			return count;

		}

	}
}
