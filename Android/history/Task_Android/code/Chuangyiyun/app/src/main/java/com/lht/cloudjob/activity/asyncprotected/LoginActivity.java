package com.lht.cloudjob.activity.asyncprotected;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.MainApplication;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.customview.CustomDialog;
import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.cloudjob.customview.TitleBar;
import com.lht.cloudjob.customview.TitleBarWithOp;
import com.lht.cloudjob.interfaces.ITriggerCompare;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.interfaces.keys.SPConstants;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.interfaces.umeng.IUmengEventKey;
import com.lht.cloudjob.mvp.model.pojo.LoginInfo;
import com.lht.cloudjob.mvp.presenter.LoginActivityPresenter;
import com.lht.cloudjob.mvp.viewinterface.ILoginActivity;
import com.lht.cloudjob.native4js.impl.VsoLoginImpl;
import com.lht.cloudjob.tplogin.QQOAuthListener;
import com.lht.cloudjob.tplogin.SinaConstants;
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
     * 首次进入等情况下，可能放弃了登录，点击返回等情况需要进入首页
     */
    private boolean shouldStartHomeOnFinish = false;

    private Button btnLogin;

    private ProgressBar mProgressBar;

    private EditText etAccount, etPwd;

    private TitleBarWithOp titleBarWithOp;

    private int thirdPlatform = 0;

    private static final int qq = 1;

    private static final int sina = 2;

    private static final int wechat = 3;

    private SharedPreferences sharedPreferences;

    private ImageButton iBtnClearAccount;

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
    protected void onNewIntent(Intent intent) {
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
        finishActivity();
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
    public UMengActivity getActivity() {
        return LoginActivity.this;
    }

    @Override
    protected void initView() {
        btnLogin = (Button) findViewById(R.id.login_btn_login);

        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);

        etAccount = (EditText) findViewById(R.id.login_et_account);

        etPwd = (EditText) findViewById(R.id.login_et_pwd);

        titleBarWithOp = (TitleBarWithOp) findViewById(R.id.titlebar);

        iBtnClearAccount = (ImageButton) findViewById(R.id.login_ibtn_clearaccount);
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
                presenter.callLogin(getActivity(), etAccount.getText().toString(), etPwd.getText
                        ().toString());
            }
        });

//        if (GuideActivity.LoginTrigger.GuideAccess.equals((ITriggerCompare) getLoginTrigger())) {
//            //empty
//            // 1.0.42 已不从引导页进入
//        } else {
        //显示导航栏返回建，设置顶部返回键事件
        titleBarWithOp.setOnBackListener(new TitleBar.ITitleBackListener() {
            @Override
            public void onTitleBackClick() {
                onBackPressed();
            }
        });
//        }

        titleBarWithOp.setTitle(R.string.title_activity_login);
        titleBarWithOp.setOpText(R.string.title_activity_register);
        titleBarWithOp.setOpOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jump2RegisterActivity();
            }
        });
        findViewById(R.id.login_tv_forgetpwd).setOnClickListener(this);
        findViewById(R.id.login_btn_qqlogin).setOnClickListener(this);
        findViewById(R.id.login_btn_sinalogin).setOnClickListener(this);
        findViewById(R.id.login_btn_wechatlogin).setOnClickListener(this);

        etPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
    public void jump2MainActivity(LoginInfo info) {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.putExtra(HomeActivity.KEY_ISLOGIN, true);
        intent.putExtra(HomeActivity.KEY_DATA, JSON.toJSONString(info));
        startActivity(intent);
    }

    @Override
    public void jump2ResetPwdActivity() {
        start(ResetPwdVerifyActivity.class);
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
        checkInterruptedVsoAcNotify();
        finish();
    }

    @Override
    public Object getLoginTrigger() {
        return getIntent().getSerializableExtra(TRIGGERKEY);
    }

    @Override
    public void showErrorMsg(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_tv_forgetpwd:
                jump2ResetPwdActivity();
                break;
            case R.id.login_btn_qqlogin:
                //统计 使用qq登录 -计数
                reportCountEvent(IUmengEventKey.KEY_USER_LOGIN_BYQQ);

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
    protected void onLastChainInBackPressedWillCall() {
        checkInterruptedVsoAcNotify();
        super.onLastChainInBackPressedWillCall();
    }

    private void checkInterruptedVsoAcNotify() {
        if (getLoginTrigger() != null) {
            if (VsoLoginImpl.LoginTrigger.Bridge.equals(getLoginTrigger())) {
                start(BannerInfoActivity.class);
            }
        }
    }
}
