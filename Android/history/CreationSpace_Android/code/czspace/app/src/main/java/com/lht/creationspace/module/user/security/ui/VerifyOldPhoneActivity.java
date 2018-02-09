package com.lht.creationspace.module.user.security.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme1;
import com.lht.creationspace.module.user.security.VerifyOldPhoneActivityPresenter;
import com.lht.creationspace.module.user.security.model.SendSmsByUserModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 验证老手机号
 * <p>
 * 账号管理-->显示老手机号-->验证老手机号-->绑定新手机
 */
public class VerifyOldPhoneActivity extends AsyncProtectedActivity
        implements IVerifyOldPhoneActivity, View.OnClickListener {

    private static final String PAGENAME = "VerifyOldPhoneActivity";
    private ToolbarTheme1 titleBar;
    private ProgressBar progressBar;
    private TextView tvOldPhone;
    private EditText etVerifyCode;
    private Button btnGetVerifyCode;
    private Button btnVerify;
    private String bindedPhone;
    private VerifyOldPhoneActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_old_phone);
        EventBus.getDefault().register(this);
        initView();
        initVariable();
        initEvent();
    }

    @Subscribe
    public void onEventMainThread(AppEvent.PhoneBindEvent event) {
        finishWithoutOverrideAnim();
    }

    @Override
    protected String getPageName() {
        return VerifyOldPhoneActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return VerifyOldPhoneActivity.this;
    }

    @Override
    protected void initView() {
        titleBar = (ToolbarTheme1) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        tvOldPhone = (TextView) findViewById(R.id.tv_old_phone);
        etVerifyCode = (EditText) findViewById(R.id.et_verifycode);
        btnGetVerifyCode = (Button) findViewById(R.id.btn_getverifycode);
        btnVerify = (Button) findViewById(R.id.btn_verify);
    }

    @Override
    protected void initVariable() {
        try {
            bindedPhone = IVerifyHolder.mLoginInfo.getLoginResBean().getMobile();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        presenter = new VerifyOldPhoneActivityPresenter(this);
    }

    @Override
    protected void initEvent() {
        titleBar.setTitle(getString(R.string.v1000_title_activity_verify_phone));
        titleBar.setDefaultOnBackListener(this);

        setSupportActionBar(titleBar);

        tvOldPhone.setText(bindedPhone);
        btnGetVerifyCode.setOnClickListener(this);
        btnVerify.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.resumeTimer();
    }

    @Override
    protected IApiRequestPresenter getApiRequestPresenter() {
        return presenter;
    }

    @Override
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_getverifycode:
                SendSmsByUserModel.SendSmsByUserData data = new SendSmsByUserModel.SendSmsByUserData();
                data.setUser(IVerifyHolder.mLoginInfo.getUsername());
                data.setTarget(tvOldPhone.getText().toString());
                presenter.callSendSmsVerifyCode(data);
                break;
            case R.id.btn_verify:
                presenter.callVerifyOldPhone(IVerifyHolder.mLoginInfo.getUsername(), etVerifyCode.getText().toString().trim());
                break;
            default:
                break;
        }
    }

    @Override
    public void initVCGetter(int s) {
        btnGetVerifyCode.setText(s);
        btnGetVerifyCode.setClickable(true);
    }

    @Override
    public void showCDRemaining(String s) {
        btnGetVerifyCode.setText(s);
    }

    @Override
    public void setVCGetterActiveStatus(boolean isEnable) {
        btnGetVerifyCode.setClickable(isEnable);
    }

    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
    }

    public static class Launcher extends AbsActivityLauncher<Void> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, VerifyOldPhoneActivity.class);
        }

        @Override
        public AbsActivityLauncher<Void> injectData(Void data) {
            return this;
        }
    }
}
