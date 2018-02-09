package com.lht.pan_android.activity.innerWeb;

import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IUrlManager;

import android.os.Bundle;

public class WebFindPwdActivity extends InnerWebActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected String getUrl() {
		return IUrlManager.WebUrlTemp.FINDPWD;
	}

	@Override
	protected int getMyTitle() {
		return R.string.title_activity_web_find_pwd;
	}

}