package com.lht.creationspace.module.user.security.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.R;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme1;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.module.user.security.ResetPwdPresenter;
import com.lht.creationspace.util.debug.DLog;
import com.lht.creationspace.util.string.StringUtil;
import com.lht.creationspace.util.toast.ToastUtils;

/**
 * 忘记密码-验证后的重置页面
 */
public class ResetPwdActivity extends AsyncProtectedActivity implements IResetPwdActivity {

    private static final String PAGENAME = "ResetPwdVerifyActivity";

    private ToolbarTheme1 titleBar;

    private ProgressBar progressBar;

    private Button btnSubmit;

    private ResetPwdPresenter presenter;

    private EditText etPwd;

    private ResetPwdActivityData activityData;

    private static final String BUNDLE_KEY_DATA = "key_1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);
        initView();
        initVariable();
        initEvent();

        DLog.d(DLog.Lmsg.class, getPageName() + " [afterInit]:"
                + JSON.toJSONString(activityData));

        if (savedInstanceState != null) {
            DLog.d(DLog.Lmsg.class, getPageName() + " onCreate from savedIs");
            String _data = savedInstanceState.getString(BUNDLE_KEY_DATA);
            if (!StringUtil.isEmpty(_data)) {
                activityData = JSON.parseObject(_data, ResetPwdActivityData.class);
                DLog.d(DLog.Lmsg.class, getPageName() + " [onCreate] get data savedIs success:"
                        + JSON.toJSONString(activityData));
            } else {
                DLog.d(DLog.Lmsg.class, getPageName() + " [onCreate] get data savedIs failed");
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(BUNDLE_KEY_DATA, JSON.toJSONString(activityData));
        // etc.
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String _data = savedInstanceState.getString(BUNDLE_KEY_DATA);
        if (!StringUtil.isEmpty(_data)) {
            activityData = JSON.parseObject(_data, ResetPwdActivityData.class);
        }
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
    public BaseActivity getActivity() {
        return ResetPwdActivity.this;
    }

    @Override
    protected void initView() {
        titleBar = (ToolbarTheme1) findViewById(R.id.titlebar);


        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        btnSubmit = (Button) findViewById(R.id.resetpwd_btn_restpwd);

        etPwd = (EditText) findViewById(R.id.resetpwd_et_pwd);
    }

    @Override
    protected void initVariable() {
        presenter = new ResetPwdPresenter(this);

        activityData = AbsActivityLauncher.parseData(getIntent(), ResetPwdActivityData.class);
        DLog.d(DLog.Lmsg.class, getPageName() + " [initVariable]:" + getIntent().getStringExtra(ResetPwdActivityData.class.getSimpleName()));

//        account = getIntent().getStringExtra(KEY_ACCOUNT);
//        validCode = getIntent().getStringExtra(KEY_VALID_CODE);
    }

    @Override
    protected void initEvent() {
        titleBar.setTitle(R.string.title_activity_resetpwd);
        titleBar.setDefaultOnBackListener(getActivity());

        setSupportActionBar(titleBar);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.callResetPwd(activityData.getAccount(),
                        etPwd.getText().toString(),
                        activityData.getValidCode());
            }
        });

        etPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnSubmit.performClick();
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void showErrorMsg(String msg) {
        ToastUtils.show(getActivity(), msg, ToastUtils.Duration.s);
    }

    @Override
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
    }

    public static class Launcher extends AbsActivityLauncher<ResetPwdActivityData> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, ResetPwdActivity.class);
        }

        @Override
        public AbsActivityLauncher<ResetPwdActivityData> injectData(ResetPwdActivityData data) {
            launchIntent.putExtra(data.getClass().getSimpleName(), JSON.toJSONString(data));
            return this;
        }
    }

    /**
     * Created by chhyu on 2017/5/8.
     */

    public static class ResetPwdActivityData {

        private String account;

        private String validCode;

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getValidCode() {
            return validCode;
        }

        public void setValidCode(String validCode) {
            this.validCode = validCode;
        }
    }
}
