package com.lht.creationspace.test;

import android.webkit.WebView;
import android.widget.ProgressBar;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.hybrid.AbsHybridActivityBase;
import com.lht.creationspace.customview.MaskView;
import com.lht.creationspace.hybrid.native4js.impl.DownloadImpl;
import com.lht.lhtwebviewlib.BridgeWebView;
import com.lht.ptrlib.library.PtrBridgeWebView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class TestHybrid extends AbsHybridActivityBase {

    private static final String PAGENAME = "TestHybrid";

    private PtrBridgeWebView ptrBridgeWebView;

    private ProgressBar pageProgressBar;
    private MaskView maskView;

    @Override
    protected BridgeWebView getBridgeWebView() {
        return ptrBridgeWebView.getRefreshableView();
    }

    @Override
    protected PtrBridgeWebView getPTRBase() {
        return ptrBridgeWebView;
    }

    @Override
    protected MaskView getWebMask() {
        return maskView;
    }

    @Override
    protected ProgressBar getPageProtectPbar() {
        return pageProgressBar;
    }

    @Override
    protected String getPageName() {
        return TestHybrid.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return TestHybrid.this;
    }

    @Override
    protected void initView() {
        ptrBridgeWebView = (PtrBridgeWebView) findViewById(R.id.ptr_web_view);
        pageProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        maskView = (MaskView) findViewById(R.id.mask);
    }

    @Override
    protected void initVariable() {

    }

    @Override
    protected void initEvent() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.activity_hybrid_article_detail;
    }

    @Override
    protected String getUrl() {
        return "http://172.16.23.100/test/test.html";
//        return new IHybridPagesCollection.HybridTest().getPageUrl();
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
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
