package com.lht.creationspace.module.proj.ui;

import android.webkit.WebView;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.hybrid.HybridGeneralActivity;
import com.lht.creationspace.hybrid.native4js.impl.DownloadImpl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class HybridProjPubProgressActivity extends HybridGeneralActivity {

    private static final String PAGENAME = "HybridProjPubProgressActivity";

    @Override
    public void onWebViewReceivedTitle(WebView view, String title) {
        titleBar.setTitle(title);
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
    protected String getPageName() {
        return HybridProjPubProgressActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return HybridProjPubProgressActivity.this;
    }

    @Override
    protected void initEvent() {
        EventBus.getDefault().register(this);
        titleBar.setDefaultOnBackListener(getActivity());

        setSupportActionBar(titleBar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
