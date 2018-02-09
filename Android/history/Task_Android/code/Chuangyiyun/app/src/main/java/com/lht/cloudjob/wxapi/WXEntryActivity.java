package com.lht.cloudjob.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.lht.cloudjob.MainApplication;
import com.lht.cloudjob.tplogin.WeChatOAuth;
import com.lht.cloudjob.util.debug.DLog;
import com.tencent.mm.sdk.openapi.IWXAPI;

public class WXEntryActivity extends Activity {
	// implements IWXAPIEventHandler {

	// private String access_token;
	// private String openId;
	private IWXAPI api;
	private static final String TAG = "wechat";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (api == null) {
			api = MainApplication.getWechat();
		}
		DLog.e(getClass(), "onCreate");
		api.handleIntent(getIntent(), new WeChatOAuth());
		// if (isLoginIntent)
		finish();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		// api.handleIntent(intent, this);
		DLog.e(getClass(), "onNewIntent");
		api.handleIntent(intent, new WeChatOAuth());
		finish();
	}

}
