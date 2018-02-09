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
import com.lht.pan_android.util.CloudBoxApplication;
import com.lht.pan_android.util.ToastUtil;
import com.lht.pan_android.util.ToastUtil.Duration;
import com.lht.pan_android.util.internet.HttpRequestFailureUtil;
import com.lht.pan_android.util.internet.HttpUtil;
import com.lht.pan_android.util.string.StringUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class FeedbackActivity extends UMengActivity
		implements OnClickListener, IUrlManager.AdviceFeedback, IKeyManager.Token {

	private final static String mPageName = "feedbackActivity";

	private ImageView btnBack;

	private Button btnSubmit;
	private EditText txtContact;

	private EditText txtAdviceContent;

	private HttpUtil httpUtil;

	private String url;

	private String versionName;

	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = this;
		CloudBoxApplication.addActivity(this);
		setContentView(R.layout.activity_mine_advice);
		httpUtil = new HttpUtil();
		SharedPreferences token = getSharedPreferences(SP_TOKEN, MODE_PRIVATE);
		String username = token.getString(KEY_USERNAME, "");
		String accessToken = token.getString(KEY_ACCESS_TOKEN, "");
		String accessId = token.getString(KEY_ACCESS_ID, "");
		url = DOMAIN + ADDRESS + username + FUNCTION + "?" + KEY_ACCESSTOKEN + "=" + accessToken + "&" + KEY_ACCESSID
				+ "=" + accessId;

		String pkName = this.getPackageName();
		try {
			versionName = this.getPackageManager().getPackageInfo(pkName, 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		initView();
		initEvent();
	}

	private void initView() {
		btnBack = (ImageView) findViewById(R.id.activity_mine_advice_back);

		btnSubmit = (Button) findViewById(R.id.feedback_submit);

		txtContact = (EditText) findViewById(R.id.feedback_contact);

		txtAdviceContent = (EditText) findViewById(R.id.feedback_advice);
	}

	private void initEvent() {
		btnBack.setOnClickListener(this);
		btnSubmit.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_mine_advice_back:
			FeedbackActivity.this.finish();
			break;
		case R.id.feedback_submit:
			submitAdvice();
			break;
		default:
			break;
		}

	}

	private void submitAdvice() {
		String advice = txtAdviceContent.getText().toString();
		if (StringUtil.isEmpty(advice)) {
			ToastUtil.show(this, R.string.setting_activity_feedback_null, Duration.l);
			return;
		}
		httpUtil.postWithEntity(this, url, getEntity(), "application/json", new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				ToastUtil.show(mContext, R.string.setting_activity_feedback_submited, Duration.s);
				FeedbackActivity.this.finish();
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				HttpRequestFailureUtil failureUtil = new HttpRequestFailureUtil(mContext);
				failureUtil.handleFailureWithCode(arg0, true);
			}
		});

	}

	private StringEntity getEntity() {
		StringEntity entity = null;
		JSONObject jObj = new JSONObject();
		jObj.put("app", "YPANDROID");
		jObj.put("version", versionName);
		jObj.put("contact", txtContact.getText().toString());
		jObj.put("content", txtAdviceContent.getText().toString());
		try {
			entity = new StringEntity(JSON.toJSONString(jObj), HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return entity;
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
		return FeedbackActivity.mPageName;
	}

	@Override
	protected UMengActivity getActivity() {
		return FeedbackActivity.this;
	}
}