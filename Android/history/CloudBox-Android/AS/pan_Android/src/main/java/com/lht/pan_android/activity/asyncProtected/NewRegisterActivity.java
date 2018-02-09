package com.lht.pan_android.activity.asyncProtected;

import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.activity.innerWeb.ShowProtocolActivity;
//import com.lht.pan_android.clazz.TimeClock;
//import com.lht.pan_android.clazz.TimeClock.OnTimeLapseListener;
import com.lht.pan_android.mpv.presenter.RegisterActivityPresenter;
import com.lht.pan_android.mpv.viewinterface.IRegisterActiviterView;
import com.lht.pan_android.util.ToastUtil;
import com.lht.pan_android.util.ToastUtil.Duration;
import com.lht.pan_android.util.string.StringUtil;
import com.lht.pan_android.view.popupwins.CustomDialog;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class NewRegisterActivity extends AsyncProtectedActivity implements OnClickListener, IRegisterActiviterView {

	private final static String mPageName = "registerActivity";

	private EditText txtMobile;

	private EditText txtVerify;

	private EditText txtPassword;

	private Button btnGetVerify;

	private TextView backToLogin;

	private CheckBox cbReadPact;

	private TextView txtPact;

	private Button btnSubmit;
	// , btnResetSubmit;

	// private RegisterUtil mRegisterUtil;
	//
	// private VerifyAutoGetUtil mVerifyAutoGetUtil;

	private ProgressBar mProgressBar;

	private RegisterActivityPresenter presenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.new_activity_register);

		initView();
		// mRegisterUtil = new RegisterUtil(this);

		presenter = new RegisterActivityPresenter(this);

		// mTimeClock = new TimeClock(this, timerTag);
		// mTimeClock.setTimeLapseListener(new OnTimeLapseListener() {
		//
		// @Override
		// public void onFinish() {
		// btnGetVerify.setText(R.string.register_string_verify);
		// btnGetVerify.setClickable(true);
		// btnGetVerify.setBackgroundResource(R.drawable.corners_bg_blue);
		// }
		//
		// @Override
		// public void onTick(long millisUntilFinished) {
		// btnGetVerify.setBackgroundResource(R.drawable.corners_bg_gray);
		// btnGetVerify.setClickable(false);
		// btnGetVerify.setText(millisUntilFinished / 1000 + "");
		// }
		// });

		initEvent();

	}

	@Override
	protected void onResume() {
		super.onResume();
		// mTimeClock.getTimeClock(1000).start();
		presenter.resumeTimer();
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
		// viewOne = (LinearLayout) findViewById(R.id.register_linear_one);
		// viewTwo = (LinearLayout) findViewById(R.id.register_linear_two);
		// txtNewPwd = (EditText) findViewById(R.id.register_et_newpwd);
		// txtCheckPwd = (EditText) findViewById(R.id.register_et_checkpwd);
		// btnResetSubmit = (Button)
		// findViewById(R.id.register_btn_resetsubmit);
		mProgressBar = (ProgressBar) findViewById(R.id.register_pb_wait);
	}

	private void initEvent() {
		btnGetVerify.setOnClickListener(this);
		backToLogin.setOnClickListener(this);
		txtPact.setOnClickListener(this);
		btnSubmit.setOnClickListener(this);
		// btnResetSubmit.setOnClickListener(this);
		// mRegisterUtil
		// .setOnSmsSendRequestSuccessListener(new
		// OnSmsSendRequestSuccessListener() {
		//
		// @Override
		// public void OnSmsSendRequestSuccess() {
		// // 禁止恶意刷短信
		// mTimeClock.updateTimeStamp();
		// mTimeClock.getTimeClock(1000).start();
		//
		// // 省的你修改手机号
		// txtMobile.setFocusable(false);
		// txtMobile.setEnabled(false);
		//
		// // mVerifyAutoGetUtil = new VerifyAutoGetUtil(
		// // RegisterActivity.this,
		// // new OnVerifyCodeGetListener() {
		// //
		// // @Override
		// // public void OnVerifyCodeGet(
		// // String verifyCode) {
		// // txtVerify.setText(verifyCode);
		// // }
		// // }, getSmsServerPhone(mobile));
		// // /*
		// // * TODO 2.6.0 bug
		// // * issue: http://192.168.2.90:8080/issues/8946
		// // * overview:too many tunnel of sms to catch the
		// // correct one.
		// // * so,i remove this function
		// // */
		// // 2016年4月22日15:16:39 绑定唯一通道
		// // 2016-4-22 16:26:13 通道不唯一，不启用功能
		// //
		// // mVerifyAutoGetUtil.startWork();
		//
		// }
		// });
		//
		// mRegisterUtil
		// .setOnVerifyCheckedListerner(new OnVerifyCheckedListerner() {
		//
		// @Override
		// public void onVerifyChecked(boolean isVerifyed, String usr) {
		// if (isVerifyed) {
		// username = usr;
		// changeViewToReset();
		// }
		//
		// else {
		// ToastUtil
		// .show(getApplicationContext(),
		// R.string.register_verifyfailure,
		// Duration.s);
		// }
		// }
		// });
		//
		// mRegisterUtil.setOnPasswordResetListener(new
		// OnPasswordResetListener() {
		//
		// @Override
		// public void onPasswordReset(boolean isSuccess) {
		// if (isSuccess) {
		// Intent i = new Intent(RegisterActivity.this,
		// LoginActivity.class);
		// startActivity(i);
		// RegisterActivity.this.finish();
		// } else {
		// DLog.d(getClass(), new LogLocation(), "reset failure");
		// }
		// }
		// });

		// txtCheckPwd.setOnEditorActionListener(new OnEditorActionListener() {
		//
		// @Override
		// public boolean onEditorAction(TextView v, int actionId,
		// KeyEvent event) {
		// if (actionId == EditorInfo.IME_ACTION_DONE) {
		// btnResetSubmit.performClick();
		// }
		// return false;
		// }
		// });
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.register_backtologin:
			startActivity(new Intent(NewRegisterActivity.this, LoginActivity.class));
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
		default:
			break;
		}
	}

	private void showPact() {
		Intent i = new Intent(this, ShowProtocolActivity.class);
		startActivity(i);
	}

	private String mobile;

	private void getVerify() {
		/* String */mobile = txtMobile.getText().toString();
		if (StringUtil.isEmpty(mobile))
			ToastUtil.show(this, R.string.register_mobilenull, Duration.s);
		else {
			// TODO
			presenter.updateTimer();
			presenter.resumeTimer();
		}
		// mRegisterUtil.getVerifyCode(mobile);
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
				// mRegisterUtil.checkVerifyCode(txtMobile.getText().toString(),
				// txtVerify.getText().toString());
			}
		}
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
		return NewRegisterActivity.mPageName;
	}

	@Override
	protected UMengActivity getActivity() {
		return NewRegisterActivity.this;
	}

	@Override
	public void showErrorMsg(String errMsg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enableVerifyCodeGetter() {
		btnGetVerify.setText(R.string.register_string_verify);
		btnGetVerify.setClickable(true);
		btnGetVerify.setBackgroundResource(R.drawable.corners_bg_blue);
	}

	@Override
	public void disableVerifyCodeGetter() {
		// TODO Auto-generated method stub
		btnGetVerify.setBackgroundResource(R.drawable.corners_bg_gray);
		btnGetVerify.setClickable(false);
	}

	@Override
	public void disableRegister() {
		// TODO Auto-generated method stub

	}

	@Override
	public void enableRegister() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showRegisteredNumDialog(CustomDialog dialog) {
		// TODO Auto-generated method stub

	}

	@Override
	public CustomDialog newRegisteredNumDialog() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void freshCoolingTime(int seconds) {
		btnGetVerify.setText(seconds + "");
	}

	@Override
	public void back2Login() {
		finish();
	}

	@Override
	public Resources getRes() {
		return getResources();
	}

	private SharedPreferences coolStoreSp;

	@Override
	public SharedPreferences getTimmerSp() {
		if (coolStoreSp == null)
			coolStoreSp = getSharedPreferences(IKeyManager.Timer.SP_TIMER, Context.MODE_PRIVATE);
		return coolStoreSp;
	}

}
