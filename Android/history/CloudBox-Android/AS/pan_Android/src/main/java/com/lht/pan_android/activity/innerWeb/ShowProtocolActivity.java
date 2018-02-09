package com.lht.pan_android.activity.innerWeb;

import com.lht.pan_android.R;

import android.annotation.SuppressLint;
import android.os.Bundle;

public class ShowProtocolActivity extends InnerWebActivity {

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected String getUrl() {
		return "file:///android_asset/protocol.htm";
	}

	@Override
	protected int getMyTitle() {
		return R.string.title_activity_show_protocol;
	}

}
