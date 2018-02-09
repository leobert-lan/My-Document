package com.lht.creationspace.module.user.security.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme1;
import com.lht.creationspace.module.user.security.ResetPwdVerifyPresenter;
import com.lht.creationspace.util.toast.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 忘记密码-重置密码--验证手机号
 */
public class ResetPwdVerifyActivity extends AsyncProtectedActivity
        implements IResetPwdVerifyActivity, View.OnClickListener {

    private static final String PAGENAME = "ResetPwdVerifyActivity";

    private ToolbarTheme1 titleBar;

    private ResetPwdVerifyPresenter presenter;

    private ProgressBar progressBar;

    private Button btnVerify, btnGetVerifyCode;

    private EditText etPhone, etCode;
    private ImageButton ibtnClearAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd_verify);
        EventBus.getDefault().register(this);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected String getPageName() {
        return ResetPwdVerifyActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return ResetPwdVerifyActivity.this;
    }

    @Override
    protected void initView() {
        titleBar = (ToolbarTheme1) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        btnVerify = (Button) findViewById(R.id.resetpwd_btn_verify);
        btnGetVerifyCode = (Button) findViewById(R.id.resetpwd_btn_getverifycode);

        etPhone = (EditText) findViewById(R.id.resetpwd_et_phone);
        etCode = (EditText) findViewById(R.id.resetpwd_et_verifycode);
        ibtnClearAccount = (ImageButton) findViewById(R.id.resetpwd_ibtn_clearaccount);
    }

    @Override
    protected void initVariable() {
        presenter = new ResetPwdVerifyPresenter(this);
    }

    @Override
    protected void initEvent() {
        titleBar.setTitle(R.string.title_activity_resetpwdverify);
        titleBar.setDefaultOnBackListener(getActivity());

        setSupportActionBar(titleBar);

        btnVerify.setOnClickListener(this);
        btnGetVerifyCode.setOnClickListener(this);
        ibtnClearAccount.setOnClickListener(this);

        presenter.watchLength(etPhone, 11);
    }

    @Override
    public void showErrorMsg(String msg) {
        ToastUtils.show(getActivity(), msg, ToastUtils.Duration.s);
    }

    @Override
    public void jump2SetPwd(String account, String validCode) {
        presenter.jump2ResetPwdActivity(account, validCode);
    }

    @Subscribe
    public void onEventMainThread(AppEvent.PwdResettedEvent event) {
        getActivity().finish();
    }


    @Override
    public void initVCGetter(int resid) {
        btnGetVerifyCode.setClickable(true);
        btnGetVerifyCode.setText(resid);
    }

    @Override
    public void showCDRemaining(String formatTime) {
        btnGetVerifyCode.setClickable(false);
        btnGetVerifyCode.setText(formatTime);
    }

    @Override
    public void setVCGetterActiveStatus(boolean isEnabled) {
        btnGetVerifyCode.setClickable(isEnabled);
    }

    @Override
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected IApiRequestPresenter getApiRequestPresenter() {
        return presenter;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.resetpwd_btn_getverifycode:
                presenter.callSendSmsVerifyCode(etPhone.getText().toString());
                break;
            case R.id.resetpwd_btn_verify:
                presenter.callCheck(etPhone.getText().toString(), etCode.getText().toString());
                break;
            case R.id.resetpwd_ibtn_clearaccount:
                etPhone.setText(null);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.resumeTimer();
    }

    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
    }

    public static final class Launcher extends AbsActivityLauncher<Void> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, ResetPwdVerifyActivity.class);
        }

        @Override
        public AbsActivityLauncher<Void> injectData(Void data) {
            return Launcher.this;
        }
    }

}
