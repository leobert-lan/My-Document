package com.lht.cloudjob.activity.asyncprotected;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.customview.TitleBar;
import com.lht.cloudjob.interfaces.IPublicConst;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.presenter.ResetPwdVerifyPresenter;
import com.lht.cloudjob.mvp.viewinterface.IResetPwdVerifyActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 忘记密码-验证手机号
 */
public class ResetPwdVerifyActivity extends AsyncProtectedActivity
        implements IResetPwdVerifyActivity, View.OnClickListener {

    private static final String PAGENAME = "ResetPwdVerifyActivity";

    private TitleBar titleBar;

    private ResetPwdVerifyPresenter presenter;

    private ProgressBar progressBar;

    private Button btnVerify, btnGetVerifyCode;

    private EditText etAccount, etCode;
    private TextView tvContactService;

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
    public UMengActivity getActivity() {
        return ResetPwdVerifyActivity.this;
    }

    @Override
    protected void initView() {
        titleBar = (TitleBar) findViewById(R.id.titlebar);
        tvContactService = (TextView) findViewById(R.id.resetpwd_tv_contact_service);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        btnVerify = (Button) findViewById(R.id.resetpwd_btn_verify);
        btnGetVerifyCode = (Button) findViewById(R.id.resetpwd_btn_getcode);

        etAccount = (EditText) findViewById(R.id.resetpwd_et_account);
        etCode = (EditText) findViewById(R.id.resetpwd_et_verifycode);
    }

    @Override
    protected void initVariable() {
        presenter = new ResetPwdVerifyPresenter(this);
    }

    @Override
    protected void initEvent() {
        titleBar.setTitle(R.string.title_activity_resetpwdverify);
        titleBar.setDefaultOnBackListener(getActivity());

        btnVerify.setOnClickListener(this);
        btnGetVerifyCode.setOnClickListener(this);

        tvContactService.setOnClickListener(this);
        presenter.watchLength(etAccount,11);
    }

    @Override
    public void showErrorMsg(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onEventMainThread(AppEvent.PwdResettedEvent event) {
        getActivity().finish();
    }

    @Override
    public void jump2SetPwd(String account) {
        Intent i = new Intent(getActivity(), ResetPwdActivity.class);
        i.putExtra(ResetPwdActivity.KEY_ACCOUNT, account);
        startActivity(i);
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
            case R.id.resetpwd_btn_getcode:
                presenter.callSendSmsVerifyCode(etAccount.getText().toString());
                break;
            case R.id.resetpwd_btn_verify:
                presenter.callCheck(etAccount.getText().toString(), etCode.getText().toString());
                break;
            case R.id.resetpwd_tv_contact_service:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                String telPhone = IPublicConst.TEL;
                Uri uri = Uri.parse("tel:" + telPhone);
                intent.setData(uri);
                startActivity(intent);

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
}
