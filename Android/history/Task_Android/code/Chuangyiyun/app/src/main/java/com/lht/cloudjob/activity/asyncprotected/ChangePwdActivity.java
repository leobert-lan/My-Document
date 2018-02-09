package com.lht.cloudjob.activity.asyncprotected;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.MainApplication;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.clazz.LoginIntentFactory;
import com.lht.cloudjob.customview.TitleBar;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.presenter.ChangePwdActivityPresenter;
import com.lht.cloudjob.mvp.presenter.SplashActivityPresenter;
import com.lht.cloudjob.mvp.viewinterface.IChangePwdActivity;
import com.lht.cloudjob.util.debug.DLog;

import org.greenrobot.eventbus.EventBus;

/**
 * 个人信息->修改密码页面
 */
public class ChangePwdActivity extends AsyncProtectedActivity implements IChangePwdActivity,
        View.OnClickListener {

    private static final String PAGENAME = "ChangePwdActivity";
    public static final String KEY_DATA = "_data";

    private ProgressBar progressBar;

    private String username;

    private TitleBar titleBar;

    private ChangePwdActivityPresenter presenter;

    private EditText etOld,etNew,etCheck;

    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        username = getIntent().getStringExtra(KEY_DATA);
        DLog.e(getClass(), "check username;" + username);
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
        return ChangePwdActivity.PAGENAME;
    }

    @Override
    public UMengActivity getActivity() {
        return ChangePwdActivity.this;
    }

    @Override
    protected void initView() {
        titleBar = (TitleBar) findViewById(R.id.titlebar);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        etOld = (EditText) findViewById(R.id.changepwd_et_oldpwd);
        etNew = (EditText) findViewById(R.id.changepwd_et_newpwd);
        etCheck = (EditText) findViewById(R.id.changepwd_et_checkpwd);

        btnSubmit = (Button) findViewById(R.id.changepwd_btn_submit);
    }

    @Override
    protected void initVariable() {
        presenter = new ChangePwdActivityPresenter(this);
    }

    @Override
    protected void initEvent() {
        titleBar.setTitle(R.string.title_activity_changepwd);

        titleBar.setDefaultOnBackListener(getActivity());

        btnSubmit.setOnClickListener(this);
    }

    @Override
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    public void showErrorMsg(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onResetSuccess() {
        Intent reLogin = LoginIntentFactory.create(getActivity(), SplashActivityPresenter.LoginTrigger.BackgroundLogin);
        reLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        reLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        reLogin.putExtra(LoginActivity.KEY_START_HOME_ON_FINISH,true);
        EventBus.getDefault().post(new AppEvent.LogoutEvent());
        MainApplication.getOurInstance().startActivity(reLogin);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.changepwd_btn_submit:
                presenter.callChangePwd(username,etOld.getText().toString(),
                        etNew.getText().toString(),etCheck.getText().toString());
                break;
            default:
                break;
        }
    }
}
