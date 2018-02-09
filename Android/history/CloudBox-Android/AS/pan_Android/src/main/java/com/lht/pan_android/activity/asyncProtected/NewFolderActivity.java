package com.lht.pan_android.activity.asyncProtected;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class NewFolderActivity extends AsyncProtectedActivity
		implements IKeyManager.Token, OnClickListener, IUrlManager.CreateNewFolder {

	private final static String mPageName = "createNewFolderActivity";

	private HttpUtil httpUtil;

	private TextView cancel;

	private TextView submit;

	private EditText name;

	private SharedPreferences sp;

	private Context mContext;

	private String username = "";

	private String accessId = "";

	private String accessToken = "";

	private String currentPath = "";

	private ProgressBar mProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		CloudBoxApplication.addActivity(this);
		setContentView(R.layout.activity_new_folder);

		currentPath = getIntent().getStringExtra(IKeyManager.UserFolderList.PARAM_PATH);

		sp = getSharedPreferences(SP_TOKEN, MODE_PRIVATE);
		httpUtil = new HttpUtil();
		mContext = this;
		username = sp.getString(KEY_USERNAME, "");
		accessId = sp.getString(KEY_ACCESS_ID, "");
		accessToken = sp.getString(KEY_ACCESS_TOKEN, "");

		cancel = (TextView) findViewById(R.id.newfolder_cancel);
		submit = (TextView) findViewById(R.id.newfolder_submit);
		name = (EditText) findViewById(R.id.newfolder_name);
		mProgressBar = (ProgressBar) findViewById(R.id.newfolder_progress);

		cancel.setOnClickListener(this);
		submit.setOnClickListener(this);

		name.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					submit.performClick();
				}
				return false;
			}
		});

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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.newfolder_cancel:
			NewFolderActivity.this.finish();
			break;
		case R.id.newfolder_submit:
			submit(name.getText().toString());
			reportCountEvent(COUNT_CB_ADD_FOLDER);
			break;
		default:
			break;
		}
	}

	private void submit(String s) {

		if (!StringUtil.JudgeFileName(NewFolderActivity.this, s)) {
			return;
		}

		showWaitView(true);
		httpUtil.postWithEntity(mContext, getUrl(), getEntity(s), "application/json", new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				setResult(Activity.RESULT_OK);
				NewFolderActivity.this.finish();
				cancelWaitView();
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				ToastUtil.show(mContext, R.string.string_folder_create_failure, Duration.l);
				HttpRequestFailureUtil failureUtil = new HttpRequestFailureUtil(mContext);
				failureUtil.handleFailureWithCode(arg0, false);
				cancelWaitView();
			}
		});

	}

	private String getUrl() {
		String url = DOMAIN + ADDRESS + username + FUNCTION;
		url += "?access_token=" + accessToken + "&access_id=" + accessId;
		return url;
	}

	private StringEntity getEntity(String s) {
		JSONObject jObj = new JSONObject();
		String path = null;
		if (currentPath.endsWith("/"))
			path = currentPath + s;
		else
			path = currentPath + "/" + s;
		jObj.put("path", path);
		StringEntity ret = null;
		try {
			ret = new StringEntity(jObj.toJSONString(), HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	protected String getPageName() {
		return NewFolderActivity.mPageName;
	}

	@Override
	protected UMengActivity getActivity() {
		return NewFolderActivity.this;
	}

	@Override
	protected ProgressBar getProgressBar() {
		return mProgressBar;
	}

}
