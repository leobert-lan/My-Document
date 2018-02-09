package com.lht.pan_android.activity.asyncProtected;

import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.activity.innerWeb.ShowProtocolActivity;
import com.lht.pan_android.clazz.TimeClock;
import com.lht.pan_android.clazz.TimeClock.OnTimeLapseListener;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.DLog.LogLocation;
import com.lht.pan_android.util.ToastUtil;
import com.lht.pan_android.util.ToastUtil.Duration;
import com.lht.pan_android.util.activity.RegisterUtil;
import com.lht.pan_android.util.activity.RegisterUtil.OnPasswordResetListener;
import com.lht.pan_android.util.activity.RegisterUtil.OnSmsSendRequestSuccessListener;
import com.lht.pan_android.util.activity.RegisterUtil.OnVerifyCheckedListerner;
import com.lht.pan_android.util.string.StringUtil;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class RegisterActivity extends AsyncProtectedActivity implements OnClickListener {

	private final static String mPageName = "registerActivity";

	private EditText txtMobile;

	private EditText txtVerify;

	private Button btnGetVerify;

	private TextView backToLogin;

	private CheckBox cbReadPact;

	private TextView txtPact;

	private Button btnSubmit, btnResetSubmit;

	private TimeClock mTimeClock;

	private EditText txtNewPwd, txtCheckPwd;

	/**
	 * timerTag:计时器记录的tag
	 */
	private final String timerTag = "register";

	private RegisterUtil mRegisterUtil;

	// private VerifyAutoGetUtil mVerifyAutoGetUtil;

	private LinearLayout viewOne, viewTwo;

	private ProgressBar mProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);

		initView();
		mRegisterUtil = new RegisterUtil(this);

		mTimeClock = new TimeClock(this, timerTag);
		mTimeClock.setTimeLapseListener(new OnTimeLapseListener() {

			@Override
			public void onFinish() {
				btnGetVerify.setText(R.string.register_string_verify);
				btnGetVerify.setClickable(true);
				btnGetVerify.setBackgroundResource(R.drawable.corners_bg_blue);
			}

			@Override
			public void onTick(long millisUntilFinished) {
				btnGetVerify.setBackgroundResource(R.drawable.corners_bg_gray);
				btnGetVerify.setClickable(false);
				btnGetVerify.setText(millisUntilFinished / 1000 + "");
			}
		});

		initEvent();

	}

	@Override
	protected void onResume() {
		super.onResume();
		mTimeClock.getTimeClock(1000).start();
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
		txtMobile = (EditText) findViewById(R.id.register_et_mobile);
		txtVerify = (EditText) findViewById(R.id.register_et_verify);
		btnGetVerify = (Button) findViewById(R.id.register_get_verify);
		backToLogin = (TextView) findViewById(R.id.register_backtologin);
		cbReadPact = (CheckBox) findViewById(R.id.register_cb_readpact);
		txtPact = (TextView) findViewById(R.id.register_tv_pact);
		btnSubmit = (Button) findViewById(R.id.register_submit);
		viewOne = (LinearLayout) findViewById(R.id.register_linear_one);
		viewTwo = (LinearLayout) findViewById(R.id.register_linear_two);
		txtNewPwd = (EditText) findViewById(R.id.register_et_newpwd);
		txtCheckPwd = (EditText) findViewById(R.id.register_et_checkpwd);
		btnResetSubmit = (Button) findViewById(R.id.register_btn_resetsubmit);
		mProgressBar = (ProgressBar) findViewById(R.id.register_pb_wait);
	}

	private void initEvent() {
		btnGetVerify.setOnClickListener(this);
		backToLogin.setOnClickListener(this);
		txtPact.setOnClickListener(this);
		btnSubmit.setOnClickListener(this);
		btnResetSubmit.setOnClickListener(this);
		mRegisterUtil.setOnSmsSendRequestSuccessListener(new OnSmsSendRequestSuccessListener() {

			@Override
			public void OnSmsSendRequestSuccess() {
				// 禁止恶意刷短信
				mTimeClock.updateTimeStamp();
				mTimeClock.getTimeClock(1000).start();

				// 省的你修改手机号
				txtMobile.setFocusable(false);
				txtMobile.setEnabled(false);

				// mVerifyAutoGetUtil = new VerifyAutoGetUtil(
				// RegisterActivity.this,
				// new OnVerifyCodeGetListener() {
				//
				// @Override
				// public void OnVerifyCodeGet(
				// String verifyCode) {
				// txtVerify.setText(verifyCode);
				// }
				// }, getSmsServerPhone(mobile));
				// /*
				// * TODO 2.6.0 bug
				// * issue: http://192.168.2.90:8080/issues/8946
				// * overview:too many tunnel of sms to catch the
				// correct one.
				// * so,i remove this function
				// */
				// 2016年4月22日15:16:39 绑定唯一通道
				// 2016-4-22 16:26:13 通道不唯一，不启用功能
				//
				// mVerifyAutoGetUtil.startWork();

			}
		});

		mRegisterUtil.setOnVerifyCheckedListerner(new OnVerifyCheckedListerner() {

			@Override
			public void onVerifyChecked(boolean isVerifyed, String usr) {
				if (isVerifyed) {
					username = usr;
					changeViewToReset();
				}

				else {
					ToastUtil.show(getApplicationContext(), R.string.register_verifyfailure, Duration.s);
				}
			}
		});

		mRegisterUtil.setOnPasswordResetListener(new OnPasswordResetListener() {

			@Override
			public void onPasswordReset(boolean isSuccess) {
				if (isSuccess) {
					Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
					startActivity(i);
					RegisterActivity.this.finish();
				} else {
					DLog.d(getClass(), new LogLocation(), "reset failure");
				}
			}
		});

		txtCheckPwd.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					btnResetSubmit.performClick();
				}
				return false;
			}
		});
	}

	protected String getSmsServerPhone(String s) {
		// TODO Auto-generated method stub
		// 电信号段:133/153/180/181/189/177；
		// 联通号段:130/131/132/155/156/185/186/145/176
		// 移动号段：134/135/136/137/138/139/150/151/152/157/158/159/182/183/184/187/188/147/178
		String[] dianxin = { "133", "153", "180", "181", "189", "177" };
		String[] liantong = { "130", "131", "132", "155", "156", "185", "186", "145", "176" };
		String[] yidong = { "134", "135", "136", "137", "138", "139", "150", "151", "152", "157", "158", "159", "182",
				"183", "184", "187", "188", "147", "178" };
		for (String suf : dianxin) {
			if (checkStart(s, suf))
				return IKeyManager.SMSProps.PHONENO_CT;
		}
		for (String suf : liantong) {
			if (checkStart(s, suf))
				return IKeyManager.SMSProps.PHONENO_CU;
		}
		for (String suf : yidong) {
			if (checkStart(s, suf))
				return IKeyManager.SMSProps.PHONENO_CMCC;
		}
		return null;
	}

	private boolean checkStart(String res, String suf) {
		return res.startsWith(suf);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.register_backtologin:
			startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
			finish();
			break;
		case R.id.register_get_verify:
			getVerify();
			break;
		case R.id.register_tv_pact:
			// 打开pact
			showPact();
			break;
		case R.id.register_submit:
			submit();
			break;
		case R.id.register_btn_resetsubmit:
			reset();
		default:
			break;
		}
	}

	private void showPact() {
		Intent i = new Intent(this, ShowProtocolActivity.class);
		startActivity(i);
	}

	/**
	 * @Title: reset
	 * @Description: 重设pwd
	 * @author: leobert.lan
	 */
	private void reset() {
		String str1 = txtNewPwd.getText().toString();
		String str2 = txtCheckPwd.getText().toString();
		if (StringUtil.isEmpty(str1)) {
			ToastUtil.show(this, R.string.register_reset_pwdnull, Duration.s);
			return;
		}

		if (!str1.equals(str2)) {
			ToastUtil.show(this, R.string.register_pwdnotequal, Duration.s);
		} else {
			mRegisterUtil.resetPassword(username, txtVerify.getText().toString(), str1);
		}

	}

	private String mobile;

	private void getVerify() {
		/* String */mobile = txtMobile.getText().toString();
		if (StringUtil.isEmpty(mobile))
			ToastUtil.show(this, R.string.register_mobilenull, Duration.s);
		else
			mRegisterUtil.getVerifyCode(mobile);
	}

	private String username;

	/**
	 * @Title: submit
	 * @Description: 确认键执行逻辑
	 * @author: leobert.lan
	 */
	private void submit() {
		if (!cbReadPact.isChecked()) {
			// 告知阅读用户协议
			ToastUtil.show(this, R.string.register_notreadpact, Duration.l);
		} else {
			if (isVerifyEmpty()) {
				ToastUtil.show(this, R.string.register_verifynull, Duration.s);
			} else {
				mRegisterUtil.checkVerifyCode(txtMobile.getText().toString(), txtVerify.getText().toString());
			}
		}
	}

	private void changeViewToReset() {
		viewOne.setVisibility(View.GONE);
		viewTwo.setVisibility(View.VISIBLE);
	}

	private boolean isVerifyEmpty() {
		return StringUtil.isEmpty(txtVerify.getText().toString());
	}

	@Override
	protected ProgressBar getProgressBar() {
		return this.mProgressBar;
	}

	@Override
	protected String getPageName() {
		return RegisterActivity.mPageName;
	}

	@Override
	protected UMengActivity getActivity() {
		return RegisterActivity.this;
	}

}
