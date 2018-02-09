package com.lht.cloudjob.activity.asyncprotected;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.customview.TitleBar;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.bean.BasicInfoResBean;
import com.lht.cloudjob.mvp.presenter.BindPhoneActivityPresenter;
import com.lht.cloudjob.mvp.viewinterface.IBindPhoneActivity;


/**
 * 绑定新手机页面
 */
public class BindPhoneActivity extends AsyncProtectedActivity implements View.OnClickListener,
        IBindPhoneActivity {
    /**
     * only username used
     */
    public static final String KEY_DATA = "_data_basicInfoResBean";

    public static final String KEY_ISUPDATE = "_data_isupdate";

    private static final String PAGENAME = "BindPhoneActivity";

    private TitleBar titleBar;
    private EditText etPhoneNumber;
    private EditText etVerifyCode;
    private Button btnGetVerifyCode;
    private Button btnSubmit;
    private ProgressBar progressBar;

    private BindPhoneActivityPresenter presenter;

    private BasicInfoResBean basicInfoResBean;


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
    public UMengActivity getActivity() {
        return BindPhoneActivity.this;
    }

    @Override
    protected void initView() {
        titleBar = (TitleBar) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        etPhoneNumber = (EditText) findViewById(R.id.bindphone_et_phonenumber);
        etVerifyCode = (EditText) findViewById(R.id.bindphone_et_verifycode);
        btnGetVerifyCode = (Button) findViewById(R.id.bindphone_btn_getcode);
        btnSubmit = (Button) findViewById(R.id.bindphone_btn_submit);

    }

    @Override
    protected void initVariable() {
        presenter = new BindPhoneActivityPresenter(this);
        String _data = getIntent().getStringExtra(KEY_DATA);
        basicInfoResBean = JSON.parseObject(_data, BasicInfoResBean.class);
    }

    @Override
    protected void initEvent() {
        titleBar.setDefaultOnBackListener(getActivity());
        titleBar.setTitle(getString(R.string.v1010_default_bindphone_bindphone));


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
                presenter.callSendSmsVerifyCode(etPhoneNumber.getText().toString().trim(),
                        basicInfoResBean.getUsername(),isUpdate());
                break;
            case R.id.bindphone_btn_submit:
                presenter.callBindPhone(basicInfoResBean.getUsername(),isUpdate());
                break;
        }
    }

    private boolean isUpdate() {
        return getIntent().getBooleanExtra(KEY_ISUPDATE,false);
    }

    @Override
    public String getVerifyCode() {
        return etVerifyCode.getText().toString().trim();
    }

    @Override
    public void showErrorMsg(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
}
