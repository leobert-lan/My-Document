package com.lht.pan_android.activity.asyncProtected;

import com.alibaba.fastjson.JSON;
import com.lht.pan_android.ForgetPwdActivity;
import com.lht.pan_android.R;
import com.lht.pan_android.ThirdPartyLoginActivity;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.activity.innerWeb.WebFindPwdActivity;
import com.lht.pan_android.activity.innerWeb.WebRegisterActivity;
import com.lht.pan_android.util.Base64Utils;
import com.lht.pan_android.util.CloudBoxApplication;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.DLog.LogLocation;
import com.lht.pan_android.util.SPUtil;
import com.lht.pan_android.util.activity.LoginUtil;
import com.lht.pan_android.util.activity.LoginUtil.ILoginCallBack;
import com.lht.pan_android.util.thirdPartyLogin.ITPLoginViewPresenter;
import com.lht.pan_android.util.thirdPartyLogin.QQOAuth;
import com.lht.pan_android.util.thirdPartyLogin.QQOAuthListener;
import com.lht.pan_android.util.thirdPartyLogin.SinaConstants;
import com.lht.pan_android.util.thirdPartyLogin.SinaOAuth;
import com.lht.pan_android.util.thirdPartyLogin.TPLoginVerifyBean;
import com.lht.pan_android.util.thirdPartyLogin.WeChatOAuth;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class LoginActivity extends ThirdPartyLoginActivity implements OnClickListener, ITPLoginViewPresenter {

	private final static String mPageName = "loginActivity";

	private final static boolean isWebInterfaceReady = true;

	private EditText editAccount;
	private EditText editPassword;
	// private ImageView imgQQLogin;
	// private ImageView imgBlogLogin;
	private Button btnLogin;
	private TextView txtRegister;
	private TextView txtForgetPwd;
	private ProgressBar mProgressBar;

	private String username;
	private SharedPreferences sharedPreferences;

	private LoginUtil mLoginUtil;

	private ImageView qlogin;

	private int thirdPlatform = 0;

	private final static int qq = 1;

	private final static int sina = 2;

	private final static int wechat = 3;

	private AuthInfo mAuthInfo;

	/** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
	private SsoHandler sinaSsoHandler;

	/**
	 * api:微信api
	 */
	// private IWXAPI api;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_login);
		CloudBoxApplication.addActivity(this);

		reportCountEvent(COUNT_CB_LOGIN_SHOW);

		initView();
		initEvent();

		mLoginUtil = new LoginUtil(LoginActivity.this);
		mLoginUtil.setCallBack(new ILoginCallBack() {

			@Override
			public void onSuccess() {
				startActivity(new Intent(LoginActivity.this, MainActivity.class));
				finishWithoutOverrideAnim();
				reportCountEvent(COUNT_CB_LOGIN_SUCCESS);
			}
		});

		mAuthInfo = new AuthInfo(this, SinaConstants.APP_KEY, SinaConstants.REDIRECT_URL, SinaConstants.SCOPE);
		sinaSsoHandler = new SsoHandler(this, mAuthInfo);
	}

	@Override
	protected void onResume() {

		if (sharedPreferences == null) {
			sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
		}
		username = Base64Utils.GetFromBASE64(sharedPreferences.getString("userCache", ""));
		editAccount.setText(username);
		MobclickAgent.onPageStart(mPageName);
		MobclickAgent.onResume(this);
		super.onResume();
	}

	private void initView() {
		editAccount = (EditText) findViewById(R.id.login_edit_username);
		editPassword = (EditText) findViewById(R.id.login_edit_password);
		// imgQQLogin = (ImageView) findViewById(R.id.login_qq_login);
		// imgBlogLogin = (ImageView) findViewById(R.id.login_blog_login);
		btnLogin = (Button) findViewById(R.id.login_btn_login);
		txtRegister = (TextView) findViewById(R.id.login_txt_register);
		txtForgetPwd = (TextView) findViewById(R.id.login_txt_forget_password);
		mProgressBar = (ProgressBar) findViewById(R.id.login_progress);

		// 2.3.0内容
		// qlogin = (ImageView) findViewById(R.id.test_qqlogin);
		// qlogin.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// qqLogin();
		// }
		// });
		// findViewById(R.id.test_sinalogin).setOnClickListener(
		// new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// sinaLogin();
		// }
		// });
		// findViewById(R.id.test_wechatlogin).setOnClickListener(
		// new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// wechatLogin();
		// }
		// });
	}

	// TODO test login//////////////////////////////////////////////

	private void qqLogin() {
		thirdPlatform = qq;
		QQOAuth.login(LoginActivity.this, logincallback, this);
	}

	protected void sinaLogin() {
		thirdPlatform = sina;
		SinaOAuth.login(LoginActivity.this, sinaSsoHandler, this);
	}

	protected void wechatLogin() {
		thirdPlatform = wechat;
		WeChatOAuth.login(this, this);
	}

	boolean toggle = false;

	private void initEvent() {
		// imgQQLogin.setOnClickListener(this);
		// imgBlogLogin.setOnClickListener(this);
		btnLogin.setOnClickListener(this);
		txtRegister.setOnClickListener(this);
		txtForgetPwd.setOnClickListener(this);

		// 2.6.0
		editPassword.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					btnLogin.performClick();
				}
				return false;
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		// case R.id.login_qq_login:
		// break;
		// case R.id.login_blog_login:
		// break;
		case R.id.login_btn_login:
			writeToCache();
			mLoginUtil.login(editAccount.getText().toString(), editPassword.getText().toString());
			reportCountEvent(COUNT_CB_LOGIN_LOGIN);
			break;
		case R.id.login_txt_register:
			if (isWebInterfaceReady)
				jumpToNativeRegister();
			else
				jumpToWebRegister();
			break;
		case R.id.login_txt_forget_password:
			if (isWebInterfaceReady)
				jumpToNativeForgetPwd();
			else
				jumpToWebForgetPwd();
			break;
		default:
			break;
		}
	}

	/**
	 * @Title: jumpToWebRegister
	 * @Description: 内置浏览器跳转注册
	 * @author: leobert.lan
	 */
	private void jumpToWebRegister() {
		Intent i = new Intent(LoginActivity.this, WebRegisterActivity.class);
		startActivity(i);
	}

	/**
	 * @Title: jumpToWebForgetPwd
	 * @Description: 内置浏览器跳转找回密码
	 * @author: leobert.lan
	 */
	private void jumpToWebForgetPwd() {
		Intent i = new Intent(LoginActivity.this, WebFindPwdActivity.class);
		startActivity(i);
	}

	/**
	 * @Title: jumpToNativeForgetPwd
	 * @Description: 原生的忘记密码
	 * @author: leobert.lan
	 */
	private void jumpToNativeForgetPwd() {
		startActivity(new Intent(LoginActivity.this, ForgetPwdActivity.class));
	}

	/**
	 * @Title: jumpToNativeRegister
	 * @Description: 原生的注册
	 * @author: leobert.lan
	 */
	private void jumpToNativeRegister() {
		startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
	}

	// 数据缓存
	private void writeToCache() {
		if (sharedPreferences == null)
			sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
		SPUtil.modifyString(sharedPreferences, "userCache", Base64Utils.GetBASE64(editAccount.getText().toString()));
	}

	@Override
	protected void onDestroy() {
		// 取消相关的所有请求
		mLoginUtil.destroy();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(mPageName);
		MobclickAgent.onPause(this);
	}

	@Override
	protected ProgressBar getProgressBar() {
		return mProgressBar;
	}

	@Override
	protected String getPageName() {
		return LoginActivity.mPageName;
	}

	@Override
	protected UMengActivity getActivity() {
		return LoginActivity.this;
	}

	IUiListener logincallback = new QQOAuthListener(mTencent, this);

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR) {
			Tencent.onActivityResultData(requestCode, resultCode, data, logincallback);
		}

		// SSO 授权回调
		// 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
		if (sinaSsoHandler != null && thirdPlatform == sina) {
			sinaSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onTPPullUp() {
		showWaitView(true);
	}

	@Override
	public void onTPPullUpFinish() {
		cancelWaitView();
	}

	@Override
	public void onVarifyDecoded(TPLoginVerifyBean bean) {
		// TODO 此处应该进行后台登录
		DLog.d(getClass(), new LogLocation(), "thirdPartyLogin varified info:" + JSON.toJSONString(bean));
	}
}
