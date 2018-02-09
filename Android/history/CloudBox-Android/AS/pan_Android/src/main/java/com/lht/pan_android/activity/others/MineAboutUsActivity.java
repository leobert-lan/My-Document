package com.lht.pan_android.activity.others;

import com.lht.pan_android.R;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.activity.innerWeb.ShowProtocolActivity;
import com.lht.pan_android.util.CloudBoxApplication;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class MineAboutUsActivity extends UMengActivity implements OnClickListener {

	private final static String mPageName = "aboutUsActivity";

	private ImageView btnBack;

	private String versionName;

	private TextView txtVersion;

	private TextView phone1, phone2;

	private TextView protocol;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		CloudBoxApplication.addActivity(this);
		setContentView(R.layout.activity_mine_about);

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
		btnBack = (ImageView) findViewById(R.id.activity_mine_about_back);
		txtVersion = (TextView) findViewById(R.id.about_version);
		phone1 = (TextView) findViewById(R.id.about_no1);
		phone2 = (TextView) findViewById(R.id.about_no2);
		protocol = (TextView) findViewById(R.id.about_protocol);
	}

	private void initEvent() {
		btnBack.setOnClickListener(this);
		txtVersion.setText(versionName);
		phone1.setOnClickListener(this);
		phone2.setOnClickListener(this);
		protocol.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_mine_about_back:
			MineAboutUsActivity.this.finish();
			break;
		case R.id.about_no1:
			call(((TextView) v).getText().toString());
			break;
		case R.id.about_no2:
			call(((TextView) v).getText().toString());
			break;
		case R.id.about_protocol:
			viewProtocol();
			break;
		default:
			break;
		}

	}

	private void viewProtocol() {
		Intent intent = new Intent(MineAboutUsActivity.this, ShowProtocolActivity.class);
		startActivity(intent);
	}

	private void call(String phone) {
		Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
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
		return MineAboutUsActivity.mPageName;
	}

	@Override
	protected UMengActivity getActivity() {
		return MineAboutUsActivity.this;
	}
}