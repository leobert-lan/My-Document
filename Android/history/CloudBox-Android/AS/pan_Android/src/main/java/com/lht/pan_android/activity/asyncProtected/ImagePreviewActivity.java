package com.lht.pan_android.activity.asyncProtected;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.IPreventPenetrate;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.adapter.ImagePreviewViewPagerAdapter;
import com.lht.pan_android.bean.DirItemBean;
import com.lht.pan_android.bean.PreviewImgInfosBean;
import com.lht.pan_android.bean.ShareItemBean;
import com.lht.pan_android.util.CloudBoxApplication;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.DLog.LogLocation;
import com.lht.pan_android.util.internet.HttpRequestFailureUtil;
import com.lht.pan_android.util.internet.HttpUtil;
import com.lht.pan_android.view.popupwins.CustomDialog;
import com.lht.pan_android.view.popupwins.CustomPopupWindow.OnPositiveClickListener;
import com.lht.pan_android.view.popupwins.SharePopupWindow;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @ClassName: ImagePreviewActivity
 * @Description: shit! 警告！重设计
 * @date 2016年1月29日 上午11:27:34
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class ImagePreviewActivity extends AsyncProtectedActivity implements OnClickListener {

	private final static String mPageName = "imagePreviewActivity";

	private ImageView imgBack;
	private TextView tvPicName;
	private LinearLayout imgLinearShare;
	private LinearLayout imgLinearDownload;
	private LinearLayout imgLinearDelete;
	public LinearLayout imgLinearBar;
	public RelativeLayout imgRelatTitle;

	private PreviewImgInfosBean bean;
	private SharePopupWindow sharePopupWindow;
	private HttpUtil httpUtil = new HttpUtil();
	private String username = "";
	private String accessId = "";
	private String accessToken = "";
	private int index = 0;

	// private ViewPager3D mViewPager;

	private ViewPager mViewPager;

	private ImagePreviewViewPagerAdapter<PagerAdapter> mAdapter;

	private ArrayList<DirItemBean> itemBeans = new ArrayList<DirItemBean>();

	private ArrayList<ShareItemBean> itemBeans2 = new ArrayList<ShareItemBean>();

	private String tag = "imagepreview";
	private Context mContext;
	public String flag = "";
	private DisplayMetrics dm = new DisplayMetrics();
	private ArrayList<String> paths = new ArrayList<String>();
	private ArrayList<String> ids = new ArrayList<String>();
	private ArrayList<String> owners = new ArrayList<String>();
	public final static String INTENT_DATA = "Extra";

	public final static String INTENT_FLAG = "flag";

	public final static String FLAG_SHARE = "share";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CloudBoxApplication.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_image_preview);
		mContext = this;

		initView();
		initEvent();
		reportCountEvent(COUNT_CB_OPEN_IMAGE_URL);

		String beanString = getIntent().getStringExtra(INTENT_DATA);
		flag = getIntent().getStringExtra(INTENT_FLAG);
		// DLog.d(getClass(), new LogLocation(), "flag"+flag);
		bean = JSON.parseObject(beanString, PreviewImgInfosBean.class);

		getWindowManager().getDefaultDisplay().getMetrics(dm);

		mAdapter = new ImagePreviewViewPagerAdapter<PagerAdapter>(mContext, dm);
		mAdapter.setType(flag);
		mViewPager.setAdapter(mAdapter);

		String[] items = bean.getDirBeans();

		if (flag != null && flag.equals(FLAG_SHARE)) {
			for (int i = 0; i < items.length; i++) {
				ShareItemBean bean = JSON.parseObject(items[i], ShareItemBean.class);
				itemBeans2.add(bean);
				try {
					paths.add(URLEncoder.encode(bean.getPath(), HTTP.UTF_8));
					ids.add(bean.getShareId());
					owners.add(bean.getOwner());
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

		} else {
			for (int i = 0; i < items.length; i++) {
				DirItemBean bean = JSON.parseObject(items[i], DirItemBean.class);
				itemBeans.add(bean);
				try {
					paths.add(URLEncoder.encode(bean.getPath(), HTTP.UTF_8));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}

		mAdapter.add(paths, ids, owners);
		mAdapter.getCurrentPicture(bean.getCposition());

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				mAdapter.getCurrentPicture(arg0);
				if (isShare())
					tvPicName.setText(itemBeans2.get(arg0).getName());
				else
					tvPicName.setText(itemBeans.get(arg0).getName());
				index = arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				if (isShare())
					tvPicName.setText(itemBeans2.get(arg0).getName());
				else
					tvPicName.setText(itemBeans.get(arg0).getName());
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				imgLinearBar.setVisibility(View.GONE);
				imgRelatTitle.setVisibility(View.GONE);
			}
		});

		mViewPager.setCurrentItem(bean.getCposition());
	}

	protected boolean isShare() {
		return (flag != null && flag.equals(FLAG_SHARE));
	}

	private void initView() {
		imgBack = (ImageView) findViewById(R.id.image_preview_title_back);
		tvPicName = (TextView) findViewById(R.id.image_preview_name);
		mViewPager = (ViewPager) findViewById(R.id.image_preview_viewPager);
		imgRelatTitle = (RelativeLayout) findViewById(R.id.image_preview_rl_title);
		imgLinearBar = (LinearLayout) findViewById(R.id.image_preview_bottombar);
		imgLinearShare = (LinearLayout) findViewById(R.id.image_preview_share);
		imgLinearDelete = (LinearLayout) findViewById(R.id.image_preview_delete);
		imgLinearDownload = (LinearLayout) findViewById(R.id.image_preview_download);
	}

	private void initEvent() {
		imgLinearBar.setVisibility(View.GONE);
		imgRelatTitle.setVisibility(View.GONE);
		imgBack.setOnClickListener(this);
		imgLinearShare.setOnClickListener(this);
		imgLinearDelete.setOnClickListener(this);
		imgLinearDownload.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.image_preview_title_back:
			finish();
			break;
		case R.id.image_preview_share:
			String sharePath = null;
			try {
				sharePath = URLDecoder.decode(paths.get(index), "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sharePopupWindow = new SharePopupWindow((Activity) mContext, sharePath, null);
			sharePopupWindow.setGravity(Gravity.BOTTOM);
			sharePopupWindow.setAnimationStyle(R.style.iOSActionSheet);
			sharePopupWindow.setOutsideClickDismiss();
			sharePopupWindow.show();
			reportCountEvent(CALC_CB_SHARE);
			break;
		case R.id.image_preview_download:
			reportCountEvent(CALC_CB_DOWNLOAD);
			download();
			break;
		case R.id.image_preview_delete:
			reportCountEvent(CALC_CB_DEL);
			deleteAsk(bean);
			break;
		default:
			break;
		}
	}

	private void deleteAsk(PreviewImgInfosBean bean2) {
		CustomDialog deleteAlert = new CustomDialog(this, new IPreventPenetrate() {

			@Override
			public void preventPenetrate(Activity activity, boolean isProtectNeed) {
				if (activity instanceof ImagePreviewActivity)
					setActiveStateOfDispatchOnTouch(!isProtectNeed);
			}
		});
		deleteAlert.setGravity(Gravity.CENTER);
		deleteAlert.setContent(R.string.string_delete_title);
		deleteAlert.setPositiveButton(R.string.string_sure);
		deleteAlert.setNegativeButton(R.string.string_cancel);
		deleteAlert.setPositiveClickListener(new OnPositiveClickListener() {

			@Override
			public void onPositiveClick() {
				delete(bean);
			}
		});
		deleteAlert.show();
	}

	/**
	 * @Title: download
	 * @Description: 图片预览下载方法
	 * @author: zhangbin
	 */
	private void download() {
		ArrayList<DirItemBean> items = new ArrayList<DirItemBean>();
		items.add(itemBeans.get(index));
		getDownServiceBinder().startDownLoadJob(items, getIndividualFolder().toString());
	}

	/**
	 * @Title: delete
	 * @Description: 图片预览删除方法
	 * @author: zhangbin
	 * @param item
	 */
	private void delete(PreviewImgInfosBean item) {
		SharedPreferences sp = getSharedPreferences(IKeyManager.Token.SP_TOKEN, Context.MODE_PRIVATE);
		username = sp.getString(IKeyManager.Token.KEY_USERNAME, "");
		accessId = sp.getString(IKeyManager.Token.KEY_ACCESS_ID, "");
		accessToken = sp.getString(IKeyManager.Token.KEY_ACCESS_TOKEN, "");
		String url = IUrlManager.Delete.DOMAIN + IUrlManager.Delete.ADDRESS + username
				+ IUrlManager.Delete.FUNCTION_FILE + "?access_token=" + accessToken + "&access_id=" + accessId;
		StringEntity entity = getDeleteItemEntity(item);

		httpUtil.delete(mContext, url, entity, "application/json", new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				DLog.d(getClass(), new LogLocation(), new String(arg2));
				mAdapter = new ImagePreviewViewPagerAdapter<PagerAdapter>(mContext, dm);
				mAdapter.setType(flag);
				// TODO
				paths.remove(index);
				itemBeans.remove(index);
				if (paths.size() > 0) {
					mAdapter.add(paths, ids, owners);
					mViewPager.setAdapter(mAdapter);
					mViewPager.setCurrentItem(index);
					mAdapter.getCurrentPicture(index);
					Toast.makeText(mContext, R.string.string_delete_success, Toast.LENGTH_SHORT).show();
				} else {
					// 删完了
					ImagePreviewActivity.this.finish();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				if (arg2 != null && arg2.length > 0) {
					DLog.d(getClass(), new LogLocation(), new String(arg2));
				}
				HttpRequestFailureUtil failureUtil = new HttpRequestFailureUtil(mContext);
				failureUtil.handleFailureWithCode(arg0, true);
			}
		});
	}

	@Override
	public void onBackPressed() {
		if (pw != null) {
			pw.dismiss();
			return;
		}
		super.onBackPressed();
	}

	/**
	 * @Title: getDeleteItemEntity
	 * @Description: 图片预览删除消息体
	 * @author: zhangbin
	 * @param item
	 * @return
	 */
	protected StringEntity getDeleteItemEntity(PreviewImgInfosBean item) {

		JSONObject jObj = new JSONObject();
		JSONArray jArray = new JSONArray();
		StringEntity ret = null;
		try {
			jArray.add(itemBeans.get(index).getPath());
			jObj.put("path", jArray);
			ret = new StringEntity(jObj.toString(), HTTP.UTF_8);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		DLog.d(getClass(), new LogLocation(), jObj.toString());
		return ret;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected String getPageName() {
		return ImagePreviewActivity.mPageName;
	}

	@Override
	protected UMengActivity getActivity() {
		return ImagePreviewActivity.this;
	}

	@Override
	protected ProgressBar getProgressBar() {
		return null;
	}
}
