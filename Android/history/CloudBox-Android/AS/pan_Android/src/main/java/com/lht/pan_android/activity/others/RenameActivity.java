package com.lht.pan_android.activity.others;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.bean.DirItemBean;
import com.lht.pan_android.util.CloudBoxApplication;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.DLog.LogLocation;
import com.lht.pan_android.util.ToastUtil;
import com.lht.pan_android.util.ToastUtil.Duration;
import com.lht.pan_android.util.internet.HttpRequestFailureUtil;
import com.lht.pan_android.util.internet.HttpUtil;
import com.lht.pan_android.util.string.StringUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class RenameActivity extends UMengActivity
		implements IKeyManager.Token, IKeyManager.UserFolderList, OnClickListener {

	private final static String mPageName = "renameActivity";

	private DirItemBean item;

	private Button tvCancel;
	private Button tvSure;
	private EditText editName;
	private ImageView imgPic;
	private ImageView imgCancel;

	private HttpUtil httpUtil = new HttpUtil();

	private String username = "";

	private String accessId = "";

	private String accessToken = "";

	public final static String MSG = "msg";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CloudBoxApplication.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_folder_rename);
		String msg = getIntent().getStringExtra(MSG);
		item = JSON.parseObject(msg, DirItemBean.class);

		initView();
		initEvent();

		imgPic.setBackgroundDrawable(null);
		getImgIcon(item.getIcon());

	}

	private void initEvent() {
		tvCancel.setOnClickListener(this);
		tvSure.setOnClickListener(this);
		imgCancel.setOnClickListener(this);
		imgPic.setOnClickListener(this);

		editName.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					tvSure.performClick();
				}
				return false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.linear_rename_cancel:
			RenameActivity.this.finish();
			break;
		case R.id.linear_rename_ensure:
			judgeFileName(editName.getText().toString());
			break;
		case R.id.tableRow_img_cancel:
			editName.setText("");
			break;
		default:
			break;
		}
	}

	private void initView() {

		tvCancel = (Button) findViewById(R.id.linear_rename_cancel);
		tvSure = (Button) findViewById(R.id.linear_rename_ensure);
		imgPic = (ImageView) findViewById(R.id.linear_img_pic);
		editName = (EditText) findViewById(R.id.tableRow_edit_name);
		imgCancel = (ImageView) findViewById(R.id.tableRow_img_cancel);

		httpUtil = new HttpUtil();
		SharedPreferences sp = getSharedPreferences(SP_TOKEN, Context.MODE_PRIVATE);
		username = sp.getString(KEY_USERNAME, "");
		accessId = sp.getString(KEY_ACCESS_ID, "");
		accessToken = sp.getString(KEY_ACCESS_TOKEN, "");

		if (item.getType().equals(TYPE_FILE)) {
			String nameString = item.getName().substring(0, item.getName().lastIndexOf("."));
			editName.setText(nameString);
		} else {
			editName.setText(item.getName());
		}
	}

	private void judgeFileName(String s) {

		if (StringUtil.JudgeFileName(RenameActivity.this, s)) {
			reName(item);
		} else {
			return;
		}

	}

	private String getOriginalName() {
		if (item.getType().equals(TYPE_FILE)) {
			return item.getName().substring(0, item.getName().lastIndexOf("."));
		} else {
			return item.getName();
		}
	}

	/**
	 * @Title: reName
	 * @Description: 重命名方法体
	 * @author: zhangbin
	 * @param item
	 */
	private void reName(DirItemBean item) {
		SharedPreferences sp = getSharedPreferences(IKeyManager.Token.SP_TOKEN, Context.MODE_PRIVATE);
		username = sp.getString(IKeyManager.Token.KEY_USERNAME, "");
		accessId = sp.getString(IKeyManager.Token.KEY_ACCESS_ID, "");
		accessToken = sp.getString(IKeyManager.Token.KEY_ACCESS_TOKEN, "");

		String url = getRenameUrl(item.getType());

		StringEntity entity = getRenameEntity(item);
		httpUtil.postWithParams(this, url, entity, "application/json", new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				RenameActivity.this.finish();
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				if (arg0 == 404) {
					ToastUtil.show(RenameActivity.this, R.string.rename_err_confilt, Duration.l);
				} else {
					HttpRequestFailureUtil failureUtil = new HttpRequestFailureUtil(RenameActivity.this);
					failureUtil.handleFailureWithCode(arg0, true);
				}
			}
		});
	}

	/**
	 * @Title: getRenameEntity
	 * @Description: 重命名消息体
	 * @author: zhangbin
	 * @param item
	 * @return
	 */
	protected StringEntity getRenameEntity(DirItemBean item) {

		JSONObject jObj = new JSONObject();
		StringEntity ret = null;
		String path = item.getPath();
		try {
			if (item.getType().equals(TYPE_FILE)) {
				jObj.put("name", editName.getText().toString() + "." + path.substring(path.lastIndexOf(".") + 1));
				jObj.put("path", path);
			} else {
				jObj.put("name", editName.getText().toString());
				jObj.put("path", path);
			}
			ret = new StringEntity(jObj.toString(), HTTP.UTF_8);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		DLog.d(getClass(), new LogLocation(), jObj.toString());
		return ret;
	}

	/**
	 * @Title: getRenameUrl
	 * @Description: 获取重命名的URL
	 * @author: zhangbin
	 * @param type
	 * @return
	 */
	private String getRenameUrl(String type) {
		String url = IUrlManager.Rename.DOMAIN + IUrlManager.Rename.ADDRESS + username;
		if (type.equals(TYPE_FILE))
			url += IUrlManager.Rename.FUNCTION_FILE;
		else
			url += IUrlManager.Rename.FUNCTION_FOLDER;
		url += IUrlManager.Rename.FUNCTION + "?access_token=" + accessToken + "&access_id=" + accessId;
		DLog.d(getClass(), new LogLocation(), "url" + url);
		return url;
	}

	private void getImgIcon(String headImgUrl) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(headImgUrl, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				if (statusCode == 200) {
					Bitmap bitmap = BitmapFactory.decodeByteArray(responseBody, 0, responseBody.length);
					imgPic.setImageBitmap(bitmap);
					imgPic.setScaleType(ImageView.ScaleType.CENTER_CROP);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				error.printStackTrace();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		// MobclickAgent.onPageStart(mPageName);
		// MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// MobclickAgent.onPageEnd(mPageName);
		// MobclickAgent.onPause(this);
	}

	@Override
	protected String getPageName() {
		return RenameActivity.mPageName;
	}

	@Override
	protected UMengActivity getActivity() {
		return RenameActivity.this;
	}

}