package com.lht.pan_android.activity.ViewPagerItem;

import com.lht.pan_android.activity.UMengActivity;

import android.os.Bundle;

public abstract class ViewPagerItemActivity extends UMengActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public abstract boolean back();
}
