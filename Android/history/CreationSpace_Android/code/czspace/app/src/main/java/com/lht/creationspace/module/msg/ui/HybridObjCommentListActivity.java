package com.lht.creationspace.module.msg.ui;

import android.content.Context;
import android.webkit.WebView;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.hybrid.HybridGeneralActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.hybrid.native4js.impl.DownloadImpl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 评论列表
 */
public class HybridObjCommentListActivity extends HybridGeneralActivity {

    private static final String PAGENAME = "HybridObjCommentListActivity";

    @Override
    protected String getPageName() {
        return HybridObjCommentListActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return HybridObjCommentListActivity.this;
    }

    @Override
    protected void initVariable() {
        url = AbsActivityLauncher.parseData(getIntent(), HybridObjCommentListActivityData.class).getUrl();
    }

    @Override
    protected void initEvent() {
        EventBus.getDefault().register(this);
        titleBar.setTitle("评论");
        titleBar.setDefaultOnBackListener(getActivity());

        setSupportActionBar(titleBar);
    }

    @Override
    public void onWebViewReceivedTitle(WebView view, String title) {

    }

    @Override
    @Subscribe
    public void onEventMainThread(AppEvent.LoginSuccessEvent event) {
        invokeOnEventMainThread(event);
    }

    @Override
    @Subscribe
    public void onEventMainThread(DownloadImpl.VsoBridgeDownloadEvent event) {
        invokeOnEventMainThread(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
    }

    public static class Launcher extends AbsActivityLauncher<HybridObjCommentListActivityData> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, HybridObjCommentListActivity.class);
        }

        @Override
        public AbsActivityLauncher<HybridObjCommentListActivityData> injectData(HybridObjCommentListActivityData data) {
            launchIntent.putExtra(data.getClass().getSimpleName(), JSON.toJSONString(data));
            return this;
        }
    }

    /**
     * Created by chhyu on 2017/5/9.
     */

    public static class HybridObjCommentListActivityData {

        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
