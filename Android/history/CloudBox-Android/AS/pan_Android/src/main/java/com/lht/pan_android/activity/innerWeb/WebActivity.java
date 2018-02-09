package com.lht.pan_android.activity.innerWeb;

import com.alibaba.fastjson.JSON;
import com.lht.pan_android.R;
import com.lht.pan_android.bean.AdsBean;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class WebActivity extends InnerWebActivity {
	private AdsBean bean;

	public static final String KEY_DATA = "_data";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		String s = getIntent().getStringExtra(KEY_DATA);
		bean = JSON.parseObject(s, AdsBean.class);
		super.onCreate(savedInstanceState);

		ImageView back = (ImageView) findViewById(R.id.activity_protocol_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected String getUrl() {
		return bean.getJumpLink();
	}

	@Override
	protected int getMyTitle() {
		return 0;
	}

	@Override
	protected String getTitleStr() {
		return bean.getTitle();
	}
}
