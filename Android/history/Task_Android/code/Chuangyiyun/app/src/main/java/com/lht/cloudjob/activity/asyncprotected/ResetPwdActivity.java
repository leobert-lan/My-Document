package com.lht.cloudjob.activity.asyncprotected;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.customview.TitleBar;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.presenter.ResetPwdPresenter;
import com.lht.cloudjob.mvp.viewinterface.IResetPwdActivity;

/**
 * 忘记密码-验证后的重置页面
 */
public class ResetPwdActivity extends AsyncProtectedActivity implements IResetPwdActivity {

    private static final String PAGENAME = "ResetPwdVerifyActivity";

    private TitleBar titleBar;

    private ProgressBar progressBar;

    private Button btnSubmit;

    private ResetPwdPresenter presenter;

    private String account;

    private EditText etPwd, etCheck;

    public static final String KEY_ACCOUNT = "data_account";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected IApiRequestPresenter getApiRequestPresenter() {
        return presenter;
    }

    @Override
    protected String getPageName() {
        return ResetPwdActivity.PAGENAME;
    }

    @Override
    public UMengActivity getActivity() {
        return ResetPwdActivity.this;
    }

    @Override
    protected void initView() {
        titleBar = (TitleBar) findViewById(R.id.titlebar);


        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        btnSubmit = (Button) findViewById(R.id.resetpwd_btn_submit);

        etPwd = (EditText) findViewById(R.id.resetpwd_et_pwd);
        etCheck = (EditText) findViewById(R.id.resetpwd_et_checkpwd);
    }

    @Override
    protected void initVariable() {
        presenter = new ResetPwdPresenter(this);
        account = getIntent().getStringExtra(KEY_ACCOUNT);
    }

    @Override
    protected void initEvent() {
        titleBar.setTitle(R.string.title_activity_resetpwd);
        titleBar.setDefaultOnBackListener(getActivity());

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.callResetPwd(account, etPwd.getText().toString(),
                        etCheck.getText().toString());
            }
        });

    }

    @Override
    public void showErrorMsg(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public ProgressBar getProgressBar() {
        return progressBar;
    }
}
