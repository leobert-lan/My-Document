package com.lht.pan_android.wxapi;

import com.lht.pan_android.util.CloudBoxApplication;
import com.lht.pan_android.util.thirdPartyLogin.WeChatOAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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
			api = CloudBoxApplication.getmWeChat();
		}
		Log.e(TAG, "onCreate");
		api.handleIntent(getIntent(), new WeChatOAuth());
		// if (isLoginIntent)
		finish();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		// api.handleIntent(intent, this);
		Log.e(TAG, "onNewIntent");
		api.handleIntent(intent, new WeChatOAuth());
		finish();
	}

}
