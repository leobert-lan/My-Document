package com.lht.creationspace.module.user.security.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.R;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme1;
import com.lht.creationspace.module.user.security.BindPhoneActivityPresenter;
import com.lht.creationspace.util.toast.ToastUtils;


/**
 * 绑定新手机页面
 */
public class BindPhoneActivity extends AsyncProtectedActivity
        implements View.OnClickListener,
        IBindPhoneActivity {

    private static final String PAGENAME = "BindPhoneActivity";

    private ToolbarTheme1 titleBar;
    private EditText etPhoneNumber;
    private EditText etVerifyCode;
    private Button btnGetVerifyCode;
    private Button btnSubmit;
    private ProgressBar progressBar;

    private BindPhoneActivityPresenter presenter;

    private BindPhoneActivityData launchData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);

        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected String getPageName() {
        return BindPhoneActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return BindPhoneActivity.this;
    }

    @Override
    protected void initView() {
        titleBar = (ToolbarTheme1) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        etPhoneNumber = (EditText) findViewById(R.id.bindphone_et_phonenumber);
        etVerifyCode = (EditText) findViewById(R.id.bindphone_et_verifycode);
        btnGetVerifyCode = (Button) findViewById(R.id.bindphone_btn_getcode);
        btnSubmit = (Button) findViewById(R.id.bindphone_btn_submit);

    }

    @Override
    protected void initVariable() {
        launchData = AbsActivityLauncher.parseData(getIntent(), BindPhoneActivityData.class);
        presenter = new BindPhoneActivityPresenter(this);
    }

    @Override
    protected void initEvent() {
        titleBar.setDefaultOnBackListener(getActivity());
        titleBar.setTitle(getString(R.string.v1010_default_bindphone_bindphone));

        setSupportActionBar(titleBar);

        btnGetVerifyCode.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
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
            case R.id.bindphone_btn_getcode:
                presenter.callSendSmsVerifyCode(etPhoneNumber.getText().toString(),
                        IVerifyHolder.mLoginInfo.getUsername());
                break;
            case R.id.bindphone_btn_submit:
                presenter.callBindPhone(IVerifyHolder.mLoginInfo.getUsername(), isUpdate());
                break;
        }
    }

    private boolean isUpdate() {
        return launchData.isUpdate();
    }

    @Override
    public String getVerifyCode() {
        return etVerifyCode.getText().toString().trim();
    }

    @Override
    public String getOldVerifyCode() {
        return AbsActivityLauncher.parseData(getIntent(), BindPhoneActivityData.class).getVerifyCode();
    }

    @Override
    public void showErrorMsg(String message) {
        ToastUtils.show(getActivity(), message, ToastUtils.Duration.s);
    }

    @Override
    public String getPhone() {
        return etPhoneNumber.getText().toString().trim();
    }


    @Override
    protected void onResume() {
        super.onResume();
        presenter.resumeTimer();
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
    public void showCDRemaining(String formatTime) {
        btnGetVerifyCode.setText(formatTime);
    }

    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
    }

    public static class Launcher extends AbsActivityLauncher<BindPhoneActivityData> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, BindPhoneActivity.class);
        }

        @Override
        public AbsActivityLauncher<BindPhoneActivityData> injectData(BindPhoneActivityData data) {
            launchIntent.putExtra(data.getClass().getSimpleName(), JSON.toJSONString(data));
            return this;
        }
    }

    /**
     * Created by chhyu on 2017/5/8.
     * 绑定手机号页面所需数据
     */

    public static class BindPhoneActivityData {

    //    /**
    //     * 页面来源Tag（1.已经绑定过手机号；2、未绑定过手机号进行绑定）
    //     */
    //    private String sourceTag;

        /**
         *
         */
        private boolean isUpdate;

        /**
         *
         */
        private String verifyCode;

    //    public String getSourceTag() {
    //        return sourceTag;
    //    }
    //
    //    public void setSourceTag(String sourceTag) {
    //        this.sourceTag = sourceTag;
    //    }

        public boolean isUpdate() {
            return isUpdate;
        }

        public void setUpdate(boolean update) {
            isUpdate = update;
        }

        public String getVerifyCode() {
            return verifyCode;
        }

        public void setVerifyCode(String verifyCode) {
            this.verifyCode = verifyCode;
        }
    }
}
