package com.lht.pan_android;

import com.lht.pan_android.activity.asyncProtected.LoginActivity;
import com.lht.pan_android.clazz.TimeClock;
import com.lht.pan_android.clazz.TimeClock.OnTimeLapseListener;
import com.lht.pan_android.util.CloudBoxApplication;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

@Deprecated
public class ForgetPwdActivity extends Activity implements OnClickListener {

	private final static String mPageName = "findPwdActivity";

	private ImageView forgetPwdImgBack;
	private Button forgetPwdVerificate;
	private Button forgetPwdNext;
	private Button forgetPwdComplete;
	private LinearLayout forgetPwdOne;
	private LinearLayout forgetPwdTwo;

	private boolean isShowPrevious = true;
	private Context mContext;
	private TimeClock mTimeClock;

	private final static String timerTag = "fpwd";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		CloudBoxApplication.addActivity(this);
		setContentView(R.layout.activity_forget_pwd);
		mContext = this;
		mTimeClock = new TimeClock(mContext, timerTag);

		initView();
		initEvent();
		mTimeClock.setTimeLapseListener(new OnTimeLapseListener() {

			@Override
			public void onFinish() {
				forgetPwdVerificate.setText(R.string.forget_pwd_getverificate);
				forgetPwdVerificate.setClickable(true);
				forgetPwdVerificate.setBackgroundResource(R.drawable.corners_bg_blue);
			}

			@Override
			public void onTick(long millisUntilFinished) {
				Log.d("amsg", "on tick");
				forgetPwdVerificate.setBackgroundResource(R.drawable.corners_bg_gray);
				forgetPwdVerificate.setClickable(false);
				forgetPwdVerificate.setText(millisUntilFinished / 1000 + "");
			}
		});
	}

	@Override
	protected void onResume() {
		mTimeClock.getTimeClock(1000).start();
		super.onResume();
		MobclickAgent.onPageStart(mPageName);
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(mPageName);
		MobclickAgent.onPause(this);
	}

	private void initView() {
		forgetPwdImgBack = (ImageView) findViewById(R.id.forget_pwd_back);
		forgetPwdVerificate = (Button) findViewById(R.id.forget_pwd_btn_verificate);
		forgetPwdNext = (Button) findViewById(R.id.forget_pwd_btn_next);
		forgetPwdComplete = (Button) findViewById(R.id.forget_pwd_btn_complete);
		forgetPwdOne = (LinearLayout) findViewById(R.id.forget_pwd_linear_one);
		forgetPwdTwo = (LinearLayout) findViewById(R.id.forget_pwd_linear_two);

	}

	private void initEvent() {
		forgetPwdImgBack.setOnClickListener(this);
		forgetPwdVerificate.setOnClickListener(this);
		forgetPwdNext.setOnClickListener(this);
		forgetPwdComplete.setOnClickListener(this);
		forgetPwdOne.setOnClickListener(this);
		forgetPwdTwo.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.forget_pwd_back:
			if (isShowPrevious) {
				startActivity(new Intent(ForgetPwdActivity.this, LoginActivity.class));
				finish();
				isShowPrevious = false;
			} else {
				forgetPwdOne.setVisibility(View.VISIBLE);
				forgetPwdTwo.setVisibility(View.GONE);
				isShowPrevious = true;
			}
			break;
		case R.id.forget_pwd_btn_next:
			forgetPwdOne.setVisibility(View.GONE);
			forgetPwdTwo.setVisibility(View.VISIBLE);
			isShowPrevious = false;
			break;
		case R.id.forget_pwd_btn_complete:
			startActivity(new Intent(ForgetPwdActivity.this, LoginActivity.class));
			finish();
			break;
		case R.id.forget_pwd_btn_verificate:
			Log.d("amsg", "on click");
			mTimeClock.updateTimeStamp();
			mTimeClock.getTimeClock(1000).start();
		default:
			break;
		}
	}
}
