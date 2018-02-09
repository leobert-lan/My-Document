package com.lht.pan_android;

import com.lht.pan_android.activity.asyncProtected.AsyncProtectedActivity;
import com.lht.pan_android.util.CloudBoxApplication;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.tauth.Tencent;

import android.os.Bundle;

public abstract class ThirdPartyLoginActivity extends AsyncProtectedActivity {

	protected static Tencent mTencent;

	protected static IWXAPI mWeChat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (mTencent == null) {
			mTencent = CloudBoxApplication.getmTencent();
		}

		if (mWeChat == null) {
			mWeChat = CloudBoxApplication.getmWeChat();
		}
	}

}
