package com.lht.creationspace.module.pub.ui;

import android.content.Context;
import android.webkit.WebView;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.hybrid.HybridGeneralActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.hybrid.native4js.impl.DownloadImpl;

public class UserAgreementActivity extends HybridGeneralActivity {

    private static final String PAGENAME = "UserAgreementActivity";

    private String title;


    @Override
    protected String getPageName() {
        return UserAgreementActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return UserAgreementActivity.this;
    }

    @Override
    protected void initVariable() {
        super.initVariable();
        UserAgreementActivityData data = AbsActivityLauncher.parseData(getIntent(), UserAgreementActivityData.class);
        title = data.getTitle();
        url = data.getUserAgreementUrl();
    }

    @Override
    protected void initEvent() {
        titleBar.setDefaultOnBackListener(getActivity());
        titleBar.setTitle(title);

        setSupportActionBar(titleBar);
    }

    @Override
    public void onWebViewReceivedTitle(WebView view, String title) {

    }

    @Override
    public void onEventMainThread(AppEvent.LoginSuccessEvent event) {
    }

    @Override
    public void onEventMainThread(DownloadImpl.VsoBridgeDownloadEvent event) {
    }

    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
    }

    public static class Launcher extends AbsActivityLauncher<UserAgreementActivityData> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, UserAgreementActivity.class);
        }

        @Override
        public AbsActivityLauncher<UserAgreementActivityData> injectData(UserAgreementActivityData data) {
            launchIntent.putExtra(data.getClass().getSimpleName(), JSON.toJSONString(data));
            return this;
        }
    }

    /**
     * Created by chhyu on 2017/5/9.
     */

    public static class UserAgreementActivityData {

        /**
         * 用户协议url
         */
        private String userAgreementUrl;

        private String title;

        public String getUserAgreementUrl() {
            return userAgreementUrl;
        }

        public void setUserAgreementUrl(String userAgreementUrl) {
            this.userAgreementUrl = userAgreementUrl;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
