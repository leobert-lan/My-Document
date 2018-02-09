package com.lht.creationspace.test;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.customview.MaskView;
import com.lht.creationspace.base.fragment.hybrid.AbsHybridFragmentBase;
import com.lht.creationspace.hybrid.native4js.impl.DownloadImpl;
import com.lht.creationspace.util.debug.DLog;
import com.lht.lhtwebviewlib.BridgeWebView;
import com.lht.ptrlib.library.PtrBridgeWebView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * A simple {@link Fragment} subclass.
 */
public class TestFragment extends AbsHybridFragmentBase {
    private PtrBridgeWebView ptrBridgeWebView;

    private MaskView maskView;


    public TestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

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
        return parent.getPageProtectPbar();
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.test_fragment;
    }

    @Override
    protected String getUrl() {
        return DummyData.HYBRID_TEST_URL;
    }

    @Override
    public void onWebViewReceivedTitle(WebView view, String title) {
        //ignore
    }

    @Override
    @Subscribe
    public void onEventMainThread(AppEvent.LoginSuccessEvent event) {
        invokeOnEventMainThread(event);
    }

    @Override
    @Subscribe
    public void onEventMainThread(DownloadImpl.VsoBridgeDownloadEvent event) {
        DLog.d(DLog.Lmsg.class, "receive download progress");
        invokeOnEventMainThread(event);
    }

    @Override
    protected void initView(View contentView) {
        ptrBridgeWebView = (PtrBridgeWebView) contentView.findViewById(R.id.ptr_web_view);
        maskView = (MaskView) contentView.findViewById(R.id.mask);
    }

    @Override
    protected void initVariable() {

    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected String getPageName() {
        return "TestFragment";
    }
}
