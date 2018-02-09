package com.lht.creationspace.test;

import android.webkit.WebView;
import android.widget.ProgressBar;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.hybrid.AbsHybridActivityBase;
import com.lht.creationspace.customview.MaskView;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme1;
import com.lht.creationspace.hybrid.native4js.impl.DownloadImpl;
import com.lht.lhtwebviewlib.BridgeWebView;
import com.lht.ptrlib.library.PtrBridgeWebView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class TestPtrWebAc extends AbsHybridActivityBase {
    private static final String PAGE_NAME = "TestPtrWebAc";

    private ProgressBar progressBar;
    //    private BridgeWebView bridgeWebView;
    private MaskView maskView;
    protected ToolbarTheme1 titleBar;
    private PtrBridgeWebView ptrBridgeWebView;

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
        return progressBar;
    }


    @Override
    protected String getPageName() {
        return TestPtrWebAc.PAGE_NAME;
    }

    @Override
    public BaseActivity getActivity() {
        return TestPtrWebAc.this;
    }

    @Override
    protected void initView() {
        titleBar = (ToolbarTheme1) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        maskView = (MaskView) findViewById(R.id.mask);
        ptrBridgeWebView = (PtrBridgeWebView) findViewById(R.id.ptr_web_view);
    }

    @Override
    protected void initVariable() {

    }

    @Override
    protected void initEvent() {
        ptrBridgeWebView.setWebChromeClient(getDefaultFileChooseSupport(), this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.test_ptr_web_activity;
    }

    @Override
    protected String getUrl() {
//        return "https:www.baidu.com";
        return "https://m.vsochina.com/maker/module/prodetail.html?project_id=1";
//        return new IHybridPagesCollection.HybridHomeIndexRecommend().getPageUrl();
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
}
