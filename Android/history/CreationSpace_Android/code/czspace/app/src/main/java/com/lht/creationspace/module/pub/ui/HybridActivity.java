package com.lht.creationspace.module.pub.ui;

import android.content.Context;
import android.webkit.WebView;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.hybrid.HybridGeneralActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.hybrid.native4js.impl.DownloadImpl;
import com.lht.creationspace.util.string.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class HybridActivity extends HybridGeneralActivity {

    private static final String PAGENAME = "HybridActivity";

    private String mTitle;

    @Override
    protected void initVariable() {
        super.initVariable();
        HybridActivityData data = AbsActivityLauncher.parseData(getIntent(), HybridActivityData.class);
        mTitle = data.getTitle();
        url = data.getUrl();
    }

    @Override
    protected void initEvent() {
        if (!StringUtil.isEmpty(mTitle))
            titleBar.setTitle(mTitle);
        titleBar.setTitleTextColor(R.color.black);
        titleBar.setDefaultOnBackListener(this);

        setSupportActionBar(titleBar);
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onWebViewReceivedTitle(WebView view, String title) {
        if (StringUtil.isEmpty(mTitle))// 未指定title
            titleBar.setTitle(title);
    }

    @Subscribe
    @Override
    public void onEventMainThread(AppEvent.LoginSuccessEvent event) {
        invokeOnEventMainThread(event);
    }

    @Subscribe
    @Override
    public void onEventMainThread(DownloadImpl.VsoBridgeDownloadEvent event) {
        invokeOnEventMainThread(event);
    }

    @Override
    protected String getPageName() {
        return HybridActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return HybridActivity.this;
    }


    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
    }

    public static class Launcher extends AbsActivityLauncher<HybridActivityData> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected AbsActivityLauncher.LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, HybridActivity.class);
        }

        @Override
        public AbsActivityLauncher<HybridActivityData> injectData(HybridActivityData data) {
            launchIntent.putExtra(data.getClass().getSimpleName(), JSON.toJSONString(data));
            return this;
        }
    }

    /**
     * Created by chhyu on 2017/5/9.
     */

    public static class HybridActivityData {

        private String url;

        private String title;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
