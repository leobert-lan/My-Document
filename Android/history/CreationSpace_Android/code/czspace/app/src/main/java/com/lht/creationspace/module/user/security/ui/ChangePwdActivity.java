package com.lht.creationspace.module.user.security.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.MainApplication;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.base.launcher.LoginIntentFactory;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme1;
import com.lht.creationspace.module.home.ui.ac.HomeActivity;
import com.lht.creationspace.module.pub.SplashActivityPresenter;
import com.lht.creationspace.module.user.login.ui.LoginActivity;
import com.lht.creationspace.module.user.security.ChangePwdActivityPresenter;
import com.lht.creationspace.util.toast.ToastUtils;
import com.lht.creationspace.util.ui.EditTextUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * 个人信息->修改密码页面
 */
public class ChangePwdActivity extends AsyncProtectedActivity implements IChangePwdActivity, CompoundButton.OnCheckedChangeListener {

    private static final String PAGENAME = "ChangePwdActivity";

    private ProgressBar progressBar;

    private ToolbarTheme1 titleBar;

    private ChangePwdActivityPresenter presenter;

    private EditText etOld, etNew, etNewPwdAgain;

    private Button btnSubmit;
    private CheckBox cbSeeCorrentPwd;
    private CheckBox cbSeeNewPwd;
    private CheckBox cbSeeNewPwdAgain;
    private LinearLayout llOldLoginPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
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
    public BaseActivity getActivity() {
        return ChangePwdActivity.this;
    }

    @Override
    protected void initView() {
        titleBar = (ToolbarTheme1) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        llOldLoginPwd = (LinearLayout) findViewById(R.id.ll_old_pwd);

        etOld = (EditText) findViewById(R.id.et_old_pwd);
        etNew = (EditText) findViewById(R.id.et_new_pwd);
        etNewPwdAgain = (EditText) findViewById(R.id.et_newpwd_again);

        btnSubmit = (Button) findViewById(R.id.changepwd_btn_submit);

        //密码可见与不可见
        cbSeeCorrentPwd = (CheckBox) findViewById(R.id.cb_see_current_pwd);
        cbSeeNewPwd = (CheckBox) findViewById(R.id.cb_see_newpwd);
        cbSeeNewPwdAgain = (CheckBox) findViewById(R.id.cb_see_newpwd_again);

    }

    @Override
    protected void initVariable() {
        presenter = new ChangePwdActivityPresenter(this);
    }

    @Override
    protected void initEvent() {
        titleBar.setTitle(R.string.v1000_title_activity_change_password);
        titleBar.setDefaultOnBackListener(this);

        setSupportActionBar(titleBar);

        try {
            if (IVerifyHolder.mLoginInfo.getLoginResBean().hasPwdManualSet()) {  //  ==1
                llOldLoginPwd.setVisibility(View.VISIBLE);
            } else {  // ==2
                llOldLoginPwd.setVisibility(View.GONE);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        cbSeeCorrentPwd.setOnCheckedChangeListener(this);
        cbSeeNewPwd.setOnCheckedChangeListener(this);
        cbSeeNewPwdAgain.setOnCheckedChangeListener(this);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.callChangePwd(IVerifyHolder.mLoginInfo.getLoginResBean().hasPwdManualSet(), IVerifyHolder.mLoginInfo.getUsername(), etOld.getText().toString(),
                        etNew.getText().toString(), etNewPwdAgain.getText().toString());
            }
        });

        etNewPwdAgain.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                presenter.callChangePwd(IVerifyHolder.mLoginInfo.getLoginResBean().hasPwdManualSet(), IVerifyHolder.mLoginInfo.getUsername(), etOld.getText().toString(),
                        etNew.getText().toString(), etNewPwdAgain.getText().toString());
                return false;
            }
        });
    }

    @Override
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    public void showErrorMsg(String msg) {
        ToastUtils.show(getActivity(), msg, ToastUtils.Duration.s);
    }


    @Override
    public void onResetSuccess() {
        Intent[] intents = new Intent[2];
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        Intent reLogin = LoginIntentFactory.create(getActivity(), SplashActivityPresenter.LoginTrigger.BackgroundLogin);
//        reLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        reLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        reLogin.putExtra(LoginActivity.KEY_START_HOME_ON_FINISH, true);
        EventBus.getDefault().post(new AppEvent.LogoutEvent());

        intents[0] = intent;
        intents[1] = reLogin;

        MainApplication.getOurInstance().startActivities(intents);
//        MainApplication.getOurInstance().startActivity(intents[1]);
        finish();
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.cb_see_current_pwd:
                EditTextUtils.togglePwdDisplay(b, etOld);
                break;
            case R.id.cb_see_newpwd:
                EditTextUtils.togglePwdDisplay(b, etNew);
                break;
            case R.id.cb_see_newpwd_again:
                EditTextUtils.togglePwdDisplay(b, etNewPwdAgain);
                break;
            default:
                break;
        }
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
            return new LhtActivityLauncherIntent(context, ChangePwdActivity.class);
        }

        @Override
        public AbsActivityLauncher<Void> injectData(Void data) {
            return Launcher.this;
        }
    }

}
