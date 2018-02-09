package com.lht.creationspace.module.user.social;

import android.content.Context;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.hybrid.AbsHybridActivityBase;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.customview.MaskView;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme1;
import com.lht.creationspace.hybrid.IHybridPagesCollection;
import com.lht.creationspace.hybrid.native4js.impl.DownloadImpl;
import com.lht.lhtwebviewlib.BridgeWebView;
import com.lht.ptrlib.library.PtrBridgeWebView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class HybridMyArticleActivity extends AbsHybridActivityBase {
    private static final String PAGENAME = "HybridMyArticleActivity";
    private ToolbarTheme1 titleBar;
    private ProgressBar progressBar;
    private PtrBridgeWebView ptrBridgeWebView;
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
        return progressBar;
    }

    @Override
    protected String getPageName() {
        return HybridMyArticleActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return HybridMyArticleActivity.this;
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.activity_hybrid_my_article;
    }

    @Override
    protected void initView() {
        titleBar = (ToolbarTheme1) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        ptrBridgeWebView = (PtrBridgeWebView) findViewById(R.id.ptr_web_view);
        maskView = (MaskView) findViewById(R.id.mask);
    }

    @Override
    protected void initVariable() {

    }

    @Override
    protected void initEvent() {
        EventBus.getDefault().register(this);
        titleBar.setTitle(getString(R.string.v1000_default_homepage_text_my_article));
        titleBar.setDefaultOnBackListener(this);

        setSupportActionBar(titleBar);
    }


    @Override
    protected String getUrl() {
        return new IHybridPagesCollection.HybridMyArticle().getPageUrl();
    }

    @Override
    public void onWebViewReceivedTitle(WebView view, String title) {

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
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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
            return new LhtActivityLauncherIntent(context, HybridMyArticleActivity.class);
        }

        @Override
        public AbsActivityLauncher<Void> injectData(Void data) {
            return Launcher.this;
        }
    }
}
