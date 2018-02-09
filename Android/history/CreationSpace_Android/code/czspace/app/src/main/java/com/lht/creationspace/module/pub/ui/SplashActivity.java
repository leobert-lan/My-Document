package com.lht.creationspace.module.pub.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.lht.creationspace.R;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.cfg.SPConstants;
import com.lht.creationspace.module.home.ui.ac.HomeActivity;
import com.lht.creationspace.module.pub.SplashActivityPresenter;
import com.lht.creationspace.util.toast.ToastUtils;

public class SplashActivity extends AsyncProtectedActivity implements ISplashActivity {

    private static final String PAGENAME = "SplashActivity";
    private SharedPreferences mBasicPreferences = null;
    private SharedPreferences mTokenPreferences = null;
    private SplashActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
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
        return SplashActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return SplashActivity.this;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initVariable() {
        presenter = new SplashActivityPresenter(this);
    }

    @Override
    protected void initEvent() {
        presenter.startSplash(1500);
    }


    @Override
    public SharedPreferences getTokenPreferences() {
        if (mTokenPreferences == null) {
            mTokenPreferences = getSharedPreferences(SPConstants.Token.SP_TOKEN, Activity
                    .MODE_PRIVATE);
        }
        return mTokenPreferences;
    }

    @Override
    public SharedPreferences getBasicPreferences() {
        if (mBasicPreferences == null) {
            mBasicPreferences = getSharedPreferences(SPConstants.Basic.SP_NAME, Activity
                    .MODE_PRIVATE);
        }
        return mBasicPreferences;
    }

    @Override
    public void jump2Main() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        startActivity(intent);
    }


    @Override
    public void jump2Guide() {
        GuideActivity.getLauncher(this).launch();
    }

    @Override
    public void showErrorMsg(String msg) {
        ToastUtils.show(getActivity(), msg, ToastUtils.Duration.s);
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    protected void displayFinishAnim() {
        //   super.displayFinishAnim(); escape anim
    }

    @Override
    public ProgressBar getProgressBar() {
        return null;
    }

    @Override
    public void showWaitView(boolean isProtectNeed) {
        //        super.showWaitView(isProtectNeed);
        //empty no need to show
    }

    @Override
    public void cancelWaitView() {
        //        super.cancelWaitView();
        //        empty ,nothing to cancel
    }

    @Override
    public void onBackPressed() {
    }
}
