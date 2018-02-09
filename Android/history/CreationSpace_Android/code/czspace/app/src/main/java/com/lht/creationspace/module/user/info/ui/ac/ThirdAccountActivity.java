package com.lht.creationspace.module.user.info.ui.ac;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lht.creationspace.R;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.MainApplication;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme1;
import com.lht.creationspace.module.user.info.ThirdAccountActivityPresenter;
import com.lht.creationspace.module.user.info.model.pojo.ThirdInfoListResBean;
import com.lht.creationspace.module.user.info.ui.IThirdAccountActivity;
import com.lht.creationspace.social.oauth.QQOAuthListener;
import com.lht.creationspace.social.oauth.SinaConstants;
import com.lht.creationspace.social.oauth.TPOauthUserBean;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

public class ThirdAccountActivity extends AsyncProtectedActivity
        implements View.OnClickListener, IThirdAccountActivity {

    private static final String PAGENAME = "ThirdAccountActivity";
    private ToolbarTheme1 titleBar;
    private ProgressBar progressBar;
    private TextView tvBindWechat;
    private TextView tvBindWeibo;
    private TextView tvBindQQ;
    private ThirdAccountActivityPresenter presenter;
    private LinearLayout llBindWechat;
    private LinearLayout llBindWeibo;
    private LinearLayout llBindQQ;

    private int thirdPlatform = 0;

    private static final int qq = 1;

    private static final int sina = 2;

    private static final int wechat = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_account);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected String getPageName() {
        return ThirdAccountActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return ThirdAccountActivity.this;
    }

    @Override
    protected void initView() {
        titleBar = (ToolbarTheme1) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        llBindWechat = (LinearLayout) findViewById(R.id.thirdaccount_ll_bindwechat);
        llBindWeibo = (LinearLayout) findViewById(R.id.thirdaccount_ll_bindweibo);
        llBindQQ = (LinearLayout) findViewById(R.id.thirdaccount_ll_bindqq);

        tvBindWechat = (TextView) findViewById(R.id.thirdaccount_tv_bindwechat);
        tvBindWeibo = (TextView) findViewById(R.id.thirdaccount_tv_bindweibo);
        tvBindQQ = (TextView) findViewById(R.id.thirdaccount_tv_bindqq);
    }

    private IUiListener qqLoginListener;

    private AuthInfo mAuthInfo;
    /**
     * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
     */
    private SsoHandler sinaSsoHandler;

    @Override
    protected void initVariable() {
        presenter = new ThirdAccountActivityPresenter(this);
        qqLoginListener = new QQOAuthListener(MainApplication.getTencent(), presenter);

        mAuthInfo = new AuthInfo(getActivity(), SinaConstants.APP_KEY,
                SinaConstants.REDIRECT_URL, SinaConstants.SCOPE);
        sinaSsoHandler = new SsoHandler(getActivity(), mAuthInfo);
    }

    @Override
    protected void initEvent() {
        titleBar.setTitle(R.string.v1000_title_activity_thirdaccount);
        titleBar.setDefaultOnBackListener(this);

        setSupportActionBar(titleBar);

        llBindQQ.setOnClickListener(this);
        llBindWechat.setOnClickListener(this);
        llBindWeibo.setOnClickListener(this);

        presenter.getThirdInfoList(IVerifyHolder.mLoginInfo.getUsername());
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.thirdaccount_ll_bindwechat:
                thirdPlatform = wechat;
                presenter.callBindWeChatAcccount(getActivity());
                break;
            case R.id.thirdaccount_ll_bindweibo:
                thirdPlatform = sina;
                presenter.callBindSinaAcccount(getActivity(), sinaSsoHandler);
                break;
            case R.id.thirdaccount_ll_bindqq:
                thirdPlatform = qq;
                presenter.callBindQqAcccount(getActivity(), qqLoginListener);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN
                || requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data,
                    qqLoginListener);
        }

        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        if (sinaSsoHandler != null && thirdPlatform == sina) {
            sinaSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void enableRow(TextView tv, LinearLayout parent) {
        tv.setText(getString(R.string.v1000_default_thirdaccount_text_to_bind));
        tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.main_green_dark));
        parent.setClickable(true);
    }

    private void disableRow(TextView tv, LinearLayout parent) {
        tv.setText(getString(R.string.v1000_default_thirdaccount_text_binded));
        tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_gray_content));
        parent.setClickable(false);
    }

    /**
     * 更新页面绑定状态的显示
     *
     * @param data
     */
    @Override
    public void updateBindStateDisplay(ThirdInfoListResBean data) {
        if (data.getQq() == null) {
            enableRow(tvBindQQ, llBindQQ);
        } else {
            disableRow(tvBindQQ, llBindQQ);
        }

        if (data.getWeixin() == null) {
            enableRow(tvBindWechat, llBindWechat);
        } else {
            disableRow(tvBindWechat, llBindWechat);
        }

        if (data.getWeibo() == null) {
            enableRow(tvBindWeibo, llBindWeibo);
        } else {
            disableRow(tvBindWeibo, llBindWeibo);
        }
    }

    @Override
    public void updateView(int type) {
        switch (type) {
            case TPOauthUserBean.TYPE_QQ:
                disableRow(tvBindQQ, llBindQQ);
                break;
            case TPOauthUserBean.TYPE_SINA:
                disableRow(tvBindWeibo, llBindWeibo);
                break;
            case TPOauthUserBean.TYPE_WECHAT:
                disableRow(tvBindWechat, llBindWechat);
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
            return new LhtActivityLauncherIntent(context, ThirdAccountActivity.class);
        }

        @Override
        public AbsActivityLauncher<Void> injectData(Void data) {
            return Launcher.this;
        }
    }
}
