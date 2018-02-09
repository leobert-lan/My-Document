package com.lht.creationspace.module.user.login.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.MainApplication;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.base.launcher.ITriggerCompare;
import com.lht.creationspace.base.model.pojo.LoginInfo;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.cfg.SPConstants;
import com.lht.creationspace.customview.DrawableCenterTextView;
import com.lht.creationspace.customview.PwdEditTextViewGroup;
import com.lht.creationspace.customview.popup.CustomPopupWindow;
import com.lht.creationspace.customview.popup.dialog.CustomDialog;
import com.lht.creationspace.module.home.ui.ac.HomeActivity;
import com.lht.creationspace.module.user.info.ui.ac.UserInfoCreateActivity;
import com.lht.creationspace.module.user.login.LoginActivityPresenter;
import com.lht.creationspace.module.user.register.ui.ac.RegisterActivity;
import com.lht.creationspace.module.user.register.ui.ac.RoleChooseActivity;
import com.lht.creationspace.module.user.security.ui.ResetPwdVerifyActivity;
import com.lht.creationspace.social.oauth.QQOAuthListener;
import com.lht.creationspace.social.oauth.SinaConstants;
import com.lht.creationspace.social.oauth.TPOauthUserBean;
import com.lht.creationspace.umeng.IUmengEventKey;
import com.lht.creationspace.util.toast.ToastUtils;
import com.lht.creationspace.util.ui.PressEffectUtils;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class LoginActivity extends AsyncProtectedActivity implements ILoginActivity, View
        .OnClickListener {

    private static final String PAGENAME = "LoginActivity";

    private LoginActivityPresenter presenter;

    public static final String TRIGGERKEY = "trigger";

    public static final String KEY_START_HOME_ON_FINISH = "KEY_START_HOME_ON_FINISH";

    /**
     * 重置密码后需使用
     */
    private boolean shouldStartHomeOnFinish = false;

    private TextView btnLogin;

    private ProgressBar mProgressBar;

    private EditText etAccount;

    private ImageButton btnClose;


    private PwdEditTextViewGroup pwdEditTextViewGroup;

    private int thirdPlatform = 0;

    private static final int qq = 1;

    private static final int sina = 2;

    private static final int wechat = 3;

    private SharedPreferences sharedPreferences;

    private ImageButton iBtnClearAccount;

    private DrawableCenterTextView mQQAccess;

    private DrawableCenterTextView mSinaAccess;

    private DrawableCenterTextView mWechatAccess;

    /**
     * 新用户注册
     */
    private TextView tvNewClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences(SPConstants.Token.SP_TOKEN, MODE_PRIVATE);
        EventBus.getDefault().register(this);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected boolean needTransparentStatusBar() {
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.e("lmsg", "login on new intent");
        clearLoginChainIfNecessary();
        setIntent(intent);
        super.onNewIntent(intent);
    }

    /**
     * 在有必要的情况下关闭登录事件链上的其他页面
     * 往往是推送通知，打开新页面，页面中触发的操作打乱了原来的事件链
     * 需要合理的关闭
     */
    private void clearLoginChainIfNecessary() {

        EventBus.getDefault().post(new AppEvent.LoginInterruptedEvent());
    }

    /**
     * 注册后台登录成功事件回调
     *
     * @param event 注册后台登录成功事件
     */
    @Subscribe
    public void onEventMainThread(AppEvent.RegisterBackgroundLoginSuccessEvent event) {
        IVerifyHolder.mLoginInfo.copy(event.getLoginInfo());
        AppEvent.LoginSuccessEvent loginSuccessEvent = new AppEvent.LoginSuccessEvent(event.getLoginInfo());
        loginSuccessEvent.setTrigger((ITriggerCompare) getLoginTrigger());
        EventBus.getDefault().post(loginSuccessEvent);
        doFinish();
    }

    /**
     * 三方注册后台登录成功事件回调
     */
    @Subscribe
    public void onEventMainThread(AppEvent.TpRegSilentLoginSuccessEvent event) {
        IVerifyHolder.mLoginInfo.copy(event.getLoginInfo());
        AppEvent.LoginSuccessEvent loginSuccessEvent = new AppEvent.LoginSuccessEvent(event.getLoginInfo());
        loginSuccessEvent.setTrigger((ITriggerCompare) getLoginTrigger());
        EventBus.getDefault().post(loginSuccessEvent);
        finishActivity(); //三方-绑定已有的需要走检查逻辑
    }


    @Subscribe
    public void onEventMainThread(AppEvent.AuthSetAccountEvent event) {
        etAccount.setText(event.getAccount());
    }

    @Override
    protected String getPageName() {
        return LoginActivity.PAGENAME;
    }

    @Override
    protected IApiRequestPresenter getApiRequestPresenter() {
        return presenter;
    }

    @Override
    public BaseActivity getActivity() {
        return LoginActivity.this;
    }

    @Override
    protected void initView() {
        btnLogin = (TextView) findViewById(R.id.login_btn_login);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        etAccount = (EditText) findViewById(R.id.login_et_account);
        btnClose = (ImageButton) findViewById(R.id.login_iv_back);
        iBtnClearAccount = (ImageButton) findViewById(R.id.login_ibtn_clearaccount);
        tvNewClient = (TextView) findViewById(R.id.login_tv_newclient);
        pwdEditTextViewGroup = (PwdEditTextViewGroup) findViewById(R.id.login_rl_pwd);

        mQQAccess = (DrawableCenterTextView) findViewById(R.id.login_btn_qqlogin);
        mSinaAccess = (DrawableCenterTextView) findViewById(R.id.login_btn_sinalogin);
        mWechatAccess = (DrawableCenterTextView) findViewById(R.id.login_btn_wechatlogin);
    }

    private AuthInfo mAuthInfo;
    /**
     * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
     */
    private SsoHandler sinaSsoHandler;

    @Override
    protected void initVariable() {
        presenter = new LoginActivityPresenter(this);
        qqLoginListener = new QQOAuthListener(MainApplication.getTencent(), presenter);

        mAuthInfo = new AuthInfo(getActivity(), SinaConstants.APP_KEY,
                SinaConstants.REDIRECT_URL, SinaConstants.SCOPE);
        sinaSsoHandler = new SsoHandler(getActivity(), mAuthInfo);
        shouldStartHomeOnFinish = getIntent().getBooleanExtra(KEY_START_HOME_ON_FINISH, false);
    }

    @Override
    protected void initEvent() {
        //统计 打开登录页面 -计数
        reportCountEvent(IUmengEventKey.KEY_USER_LOGIN_PAGE);

        etAccount.setText(sharedPreferences.getString(SPConstants.Token.KEY_ACCOUNT, ""));
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInputPanel();
                presenter.callLogin(etAccount.getText().toString(), pwdEditTextViewGroup.getInput());
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelLoginByHand();
                finish();
            }
        });


        findViewById(R.id.view_background).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInputPanel();
            }
        });


        findViewById(R.id.login_tv_forgetpwd).setOnClickListener(this);
        mQQAccess.setOnClickListener(this);
        mSinaAccess.setOnClickListener(this);
        mWechatAccess.setOnClickListener(this);

        PressEffectUtils.bindDefaultPressEffect(mQQAccess);
        PressEffectUtils.bindDefaultPressEffect(mSinaAccess);
        PressEffectUtils.bindDefaultPressEffect(mWechatAccess);

        tvNewClient.setOnClickListener(this);

        pwdEditTextViewGroup.getPwdEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnLogin.performClick();
                    return true;
                }
                return false;
            }
        });

        iBtnClearAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etAccount.setText(null);
            }
        });
    }


    @Override
    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    @Override
    public void jump2RegisterActivity() {
        Intent intent = new Intent(getActivity(), RegisterActivity.class);
        intent.putExtra(LoginActivity.TRIGGERKEY, getIntent().getSerializableExtra(TRIGGERKEY));
        startActivity(intent);
    }

    @Override
    public void jump2ResetPwdActivity() {
        ResetPwdVerifyActivity.getLauncher(getActivity()).launch();
    }

    @Override
    public SharedPreferences getTokenPreferences() {
        if (sharedPreferences == null) {
            sharedPreferences = getSharedPreferences(SPConstants.Token.SP_TOKEN, MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    @Override
    public void finishActivity() {
        LoginInfo info = IVerifyHolder.mLoginInfo;
        if (info.isUserInfoCreated()) {
            //信息完善，查看是否需要引流
            if (!info.isHasChooseRole()) {
                //需要引流，source1
//                Intent intent = new Intent(getActivity(), RoleChooseActivity.class);
//                intent.putExtra(RoleChooseActivity.KEY_DATA, RoleChooseActivity.VALUE_SOURCE_LOGIN);
//                startActivity(intent);
                presenter.jump2RoleChooseActivty(RoleChooseActivity.VALUE_SOURCE_LOGIN);
            }
            //ignore
        } else {
            //信息不完善，进入设置用户信息页面
//            Intent intent = new Intent(getActivity(), UserInfoCreateActivity.class);
//            intent.putExtra(UserInfoCreateActivity.KEY_DATA, UserInfoCreateActivity.VALUE_SOURCE_LOGIN);
//            startActivity(intent);
            presenter.jump2UserInfoCreateActivity(UserInfoCreateActivity.VALUE_SOURCE_LOGIN);
        }
        doFinish();
    }

    private void doFinish() {
        checkInterruptedVsoAcNotify();
        finish();
    }

    @Override
    public Object getLoginTrigger() {
        return getIntent().getSerializableExtra(TRIGGERKEY);
    }

    @Override
    public void showErrorMsg(String msg) {
        ToastUtils.show(getActivity(), msg, ToastUtils.Duration.s);
    }

    @Override
    public void showRegisterGuideDialog() {
        CustomDialog registerGuideDialog = new CustomDialog(this);
        registerGuideDialog.setContent(R.string.v1010_dialog_login_unexist);
        registerGuideDialog.setNegativeButton(R.string.v1010_dialog_login_nagetice_reinput);
        registerGuideDialog.setNegativeClickListener(new CustomPopupWindow.OnNegativeClickListener() {
            @Override
            public void onNegativeClick() {
                etAccount.setText("");
            }
        });

        registerGuideDialog.setPositiveButton(R.string.v1010_dialog_login_positive_register);
        registerGuideDialog.setPositiveClickListener(new CustomPopupWindow.OnPositiveClickListener() {
            @Override
            public void onPositiveClick() {
                jump2RegisterActivity();
            }
        });

        registerGuideDialog.show();
    }

//    @Override
//    public void jump2AccountCombineActivity(TPOauthUserBean oauthBean) {
//        presenter.jump2AccountCombineActivity(oauthBean);
//    }

    @Override
    public void cancelSoftInputPanel() {
        hideSoftInputPanel();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_tv_newclient:
                //新用户注册
                jump2RegisterActivity();
                break;
            case R.id.login_tv_forgetpwd:
                jump2ResetPwdActivity();
                break;
            case R.id.login_btn_qqlogin:
                //统计 使用qq登录 -计数
                reportCountEvent(IUmengEventKey.KEY_USER_LOGIN_BYQQ);
                thirdPlatform = qq;
                presenter.callQQLogin(getActivity(), qqLoginListener);
                break;
            case R.id.login_btn_sinalogin:
                //统计 使用微博登录
                reportCountEvent(IUmengEventKey.KEY_USER_LOGIN_BYWB);

                thirdPlatform = sina;
                presenter.callSinaLogin(getActivity(), sinaSsoHandler);
                break;
            case R.id.login_btn_wechatlogin:
                //统计 使用微信登录
                reportCountEvent(IUmengEventKey.KEY_USER_LOGIN_BYWX);

                thirdPlatform = wechat;
                presenter.callWechatLogin(getActivity());
                break;
            default:
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN
                || requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data,
                    qqLoginListener);
        }

        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        if (sinaSsoHandler != null && thirdPlatform == sina) {
            sinaSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private IUiListener qqLoginListener;

    @Override
    protected void onDestroy() {
        presenter.cancelRequestOnFinish(getActivity());
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void cancelLoginByHand() {
        AppEvent.LoginCancelEvent event = new AppEvent.LoginCancelEvent();
        event.setTrigger((ITriggerCompare) getLoginTrigger());
        EventBus.getDefault().post(event);
    }

    @Override
    public void onBackPressed() {
        cancelLoginByHand();
        if (shouldStartHomeOnFinish) {
            start(HomeActivity.class);
        }
        super.onBackPressed();
    }

    @Override
    protected void onLastCallInKeyBackChain() {
        checkInterruptedVsoAcNotify();
        super.onLastCallInKeyBackChain();
    }

    private void checkInterruptedVsoAcNotify() {
        //// TODO: 2017/2/23 可能需要区分页面
//        if (getLoginTrigger() != null) {
//            if (VsoLoginImpl.LoginTrigger.Bridge.equals(getLoginTrigger())) {
//                start(BannerInfoActivity.class);
//            }
//        }
    }

    @Override
    protected void displayFinishAnim() {
        overridePendingTransition(0, R.anim.slide_bottom_out);
    }
}
