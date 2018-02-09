package com.lht.cloudjob.activity.others;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.cloudjob.activity.asyncprotected.HomeActivity;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.interfaces.keys.SPConstants;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.presenter.SplashActivityPresenter;
import com.lht.cloudjob.mvp.viewinterface.ISplashActivity;

public class SplashActivity extends AsyncProtectedActivity implements ISplashActivity {

    private static final String PAGENAME = "SplashActivity";
    private SharedPreferences mBasicPreferences = null;

    private SharedPreferences mTokenPreferences = null;

    private ImageView ivSplash;
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
    public UMengActivity getActivity() {
        return SplashActivity.this;
    }

    @Override
    protected void initView() {
        ivSplash = (ImageView) findViewById(R.id.splash_iv_splash);
    }

    @Override
    protected void initVariable() {
        presenter = new SplashActivityPresenter(this);
    }

    @Override
    protected void initEvent() {
        presenter.startSplash(1500);
//        YoYo.with(Techniques.FadeIn).duration(3000).playOn(ivSplash);
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
    public void jump2Main(boolean isLogined) {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.putExtra(HomeActivity.KEY_ISLOGIN, isLogined);
        intent.putExtra(HomeActivity.KEY_DATA, JSON.toJSONString(IVerifyHolder.mLoginInfo));
        startActivity(intent);
    }


    @Override
    public void jump2Guide() {
        start(GuideActivity.class);
    }

    @Override
    public void showErrorMsg(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    protected void restoreFinishAnim() {
        //   super.restoreFinishAnim(); escape anim
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
    public void onBackPressed() {}
}
