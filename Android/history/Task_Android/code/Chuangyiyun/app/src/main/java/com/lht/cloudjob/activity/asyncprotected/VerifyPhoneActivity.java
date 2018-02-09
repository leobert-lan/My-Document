package com.lht.cloudjob.activity.asyncprotected;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.customview.TitleBar;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.bean.BasicInfoResBean;
import com.lht.cloudjob.mvp.presenter.VerifyPhoneActivityPresenter;
import com.lht.cloudjob.mvp.viewinterface.IVerifyPhoneActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 绑定新手机-校验老手机页面
 */
public class VerifyPhoneActivity extends AsyncProtectedActivity implements View.OnClickListener,
        IVerifyPhoneActivity {

    private static final String PAGENAME = "VerifyPhoneActivity";
    public static final String KEY_DATA = "_data";
    private TitleBar titleBar;
    private ProgressBar progressBar;
    private TextView tvPhoneBinded;
    private TextView tvCallUs;
    private EditText etVerifyCode;
    private Button btnGetVerifyCode;
    private Button btnVerify;
    private VerifyPhoneActivityPresenter presenter;
    private BasicInfoResBean basicInfoResBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        EventBus.getDefault().register(this);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected String getPageName() {
        return VerifyPhoneActivity.PAGENAME;
    }

    @Override
    public UMengActivity getActivity() {
        return VerifyPhoneActivity.this;
    }

    @Subscribe
    public void onEventMainThread(AppEvent.PhoneBindEvent event) {
        finish();
    }

    @Override
    protected void initView() {
        titleBar = (TitleBar) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        tvPhoneBinded = (TextView) findViewById(R.id.verifyphone_tv_phonenumber);
        etVerifyCode = (EditText) findViewById(R.id.verifyphone_et_verifycode);
        btnGetVerifyCode = (Button) findViewById(R.id.verifyphone_btn_getcode);
        btnVerify = (Button) findViewById(R.id.verifyphone_btn_verify);
        tvCallUs = (TextView) findViewById(R.id.verifyphone_tv_call_us);
    }

    @Override
    protected void initVariable() {
        presenter = new VerifyPhoneActivityPresenter(this);
        String _data = getIntent().getStringExtra(KEY_DATA);
        basicInfoResBean = JSON.parseObject(_data, BasicInfoResBean.class);
        tvPhoneBinded.setText(basicInfoResBean.getMobile());
    }

    @Override
    protected void initEvent() {
        titleBar.setDefaultOnBackListener(getActivity());
        titleBar.setTitle(getString(R.string.v1010_default_verifyphone_verifyphone));

        btnGetVerifyCode.setOnClickListener(this);
        btnVerify.setOnClickListener(this);
        tvCallUs.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.resumeTimer();
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
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.verifyphone_btn_getcode:
                presenter.callSendSmsVerifyCode(basicInfoResBean.getMobile());
                break;
            case R.id.verifyphone_btn_verify:
                presenter.callVerify(basicInfoResBean.getMobile());
                break;
            case R.id.verifyphone_tv_call_us:
                presenter.callUs();
                break;
            default:
                break;
        }
    }

    @Override
    public void showErrorMsg(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public String getVerifyCode() {
        return etVerifyCode.getText().toString().trim();
    }

    /**
     * 初始化get-verify-code button
     *
     * @param resid string-res-id to display on button
     */
    @Override
    public void initVCGetter(int resid) {
        btnGetVerifyCode.setText(resid);
        btnGetVerifyCode.setClickable(true);
    }


    @Override
    public void setVCGetterActiveStatus(boolean isEnable) {
        btnGetVerifyCode.setClickable(isEnable);
    }

    @Override
    public void jump2BindPhone() {
        Intent intent = new Intent(getActivity(), BindPhoneActivity.class);
        intent.putExtra(BindPhoneActivity.KEY_DATA, JSON.toJSONString
                (basicInfoResBean));
        intent.putExtra(BindPhoneActivity.KEY_ISUPDATE, true);
        startActivity(intent);
    }

    @Override
    public void showCDRemaining(String formatTime) {
        btnGetVerifyCode.setText(formatTime);
    }


}
