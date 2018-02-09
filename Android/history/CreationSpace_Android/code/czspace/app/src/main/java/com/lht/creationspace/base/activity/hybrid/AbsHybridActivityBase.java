package com.lht.creationspace.base.activity.hybrid;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.base.activity.UMengActivity;
import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.customview.popup.CustomProgressView;
import com.lht.creationspace.customview.MaskView;
import com.lht.creationspace.hybrid.native4js.impl.ProjChapterDetailNavigateImpl;
import com.lht.creationspace.hybrid.native4js.impl.PublishProjChapterNavigateImpl;
import com.lht.creationspace.hybrid.webclient.OnWebReceivedTitleListener;
import com.lht.creationspace.cfg.IPublicConst;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.hybrid.native4js.impl.ArticleDetailNavigateImpl;
import com.lht.creationspace.hybrid.native4js.impl.CircleDetailNavigateImpl;
import com.lht.creationspace.hybrid.native4js.impl.CommentListNavigateImpl;
import com.lht.creationspace.hybrid.native4js.impl.CustomToastImpl;
import com.lht.creationspace.hybrid.native4js.impl.DownloadImpl;
import com.lht.creationspace.hybrid.native4js.impl.GeneralNavigateImpl;
import com.lht.creationspace.hybrid.native4js.impl.ImagePreviewImpl;
import com.lht.creationspace.hybrid.native4js.impl.InputIntentImpl;
import com.lht.creationspace.hybrid.native4js.impl.ProjectDetailNavigateImpl;
import com.lht.creationspace.hybrid.native4js.impl.PublishArticleNavigateImpl;
import com.lht.creationspace.hybrid.native4js.impl.PublishProjectNavigateImpl;
import com.lht.creationspace.hybrid.native4js.impl.UCenterNavigateImpl;
import com.lht.creationspace.hybrid.native4js.impl.VsoAcDetailNavigateImpl;
import com.lht.creationspace.hybrid.native4js.impl.VsoAuthInfoImpl;
import com.lht.creationspace.hybrid.native4js.impl.VsoLoginImpl;
import com.lht.creationspace.hybrid.native4js.impl.VsoTPShareImpl;
import com.lht.creationspace.util.debug.DLog;
import com.lht.creationspace.hybrid.webclient.LhtWebviewClient;
import com.lht.creationspace.hybrid.webclient.MWebChromeClient;
import com.lht.lhtwebviewapi.Interface.IJSFuncCollection;
import com.lht.lhtwebviewapi.business.impl.MakePhoneCallImpl;
import com.lht.lhtwebviewapi.business.impl.SendMessageImpl;
import com.lht.lhtwebviewlib.BridgeWebView;
import com.lht.lhtwebviewlib.DefaultHandler;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;
import com.lht.lhtwebviewlib.base.Interface.IFileChooseSupport;
import com.lht.lhtwebviewlib.base.LhtWebViewNFLoader;
import com.lht.ptrlib.library.PtrBridgeWebView;
import com.lht.ptrlib.library.PullToRefreshBase;

import org.greenrobot.eventbus.EventBus;

/**
 * <p><b>Package</b> com.lht.vsocyy.activity.hybrid
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> AbsHybirdActivityBase
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2017/2/10.
 */

public abstract class AbsHybridActivityBase extends AsyncProtectedActivity
        implements OnWebReceivedTitleListener {

    protected final int REQ_IMAGE_PREVIEW = 1000;

    protected abstract BridgeWebView getBridgeWebView();

    protected abstract PtrBridgeWebView getPTRBase();

    protected abstract MaskView getWebMask();

    protected abstract ProgressBar getPageProtectPbar();

    protected LhtWebviewClient lhtWebviewClient;

    protected IFileChooseSupport.DefaultFileChooseSupportImpl defaultFileChooseSupport;

    public IFileChooseSupport.DefaultFileChooseSupportImpl getDefaultFileChooseSupport() {
        initFileChooseSupport();
        return defaultFileChooseSupport;
    }

    private void initFileChooseSupport() {
        if (defaultFileChooseSupport == null) {
            defaultFileChooseSupport = new IFileChooseSupport.DefaultFileChooseSupportImpl(this);
        }
    }

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DLog.d(DLog.Tmsg.class,"hybrid on create,"+System.currentTimeMillis());

        setContentView(getContentLayoutRes());
        onContentViewBeenSet();
    }

    protected abstract int getContentLayoutRes();

    protected final void onContentViewBeenSet() {
        initView();
        initVariable();
        initFileChooseSupport();
        initWebView();
        initEvent(); // eventbus regist in initEvent
        load();
    }

//    protected final void enablePtrFromTop() {
//        getPTRBase().setMode(PullToRefreshBase.Mode.PULL_FROM_START);
//    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getBooleanExtra(UMengActivity.KEY_IS_RESTRICT_INTENT, false)) {
            super.onNewIntent(intent);
            return;
        }
        setIntent(intent);
        super.onNewIntent(intent);
        load();
    }

    @Override
    public void onBackPressed() {
        if (getBridgeWebView().canGoBack()) {
            getBridgeWebView().goBack();
        } else
            super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBridgeWebView().onResume();
        if (getPTRBase().isRefreshing() || lhtWebviewClient.isLoading()) {
            DLog.i(DLog.Lmsg.class, "ignore call jf onresume,because is loading");
            return;
        }
        DLog.d(DLog.Lmsg.class,"call jf onResume");
        getBridgeWebView().callHandler(IJSFuncCollection.JF_ONRESUME, null, new CallBackFunction() {
            @Override
            public void onCallBack(String s) {
                DLog.d(DLog.Lmsg.class, getPageName() + "  web page consume onResume");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        getBridgeWebView().onPause();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        BridgeWebView webView = getBridgeWebView();
        webView.stopLoading();
        webView.removeAllViews();
        webView.destroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        defaultFileChooseSupport.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && getBridgeWebView().canGoBack()) {
            getBridgeWebView().goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        WebSettings webSettings = getBridgeWebView().getSettings();

        String originUA = webSettings.getUserAgentString();
        String myUA = originUA + " " + IPublicConst.USER_AGENT;
        DLog.d(getClass(), "new web ua:" + myUA);
        webSettings.setUserAgentString(myUA);

        webSettings.setJavaScriptEnabled(true);
        // 不使用cache
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        setUpWebViewDefaults(getBridgeWebView());

        //忽略安全问题
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }

        MWebChromeClient webChromeClient =
                new MWebChromeClient(getDefaultFileChooseSupport(), this);
//        webChromeClient.setIMediaTrans(new IMediaTransImpl(getActivity(), null));

        getBridgeWebView().setDefaultHandler(new DefaultHandler());
        lhtWebviewClient =
                new LhtWebviewClient(getBridgeWebView(), getWebMask(), getUrl());
        getBridgeWebView().setWebViewClient(lhtWebviewClient);
        getBridgeWebView().setWebChromeClient(webChromeClient);

//        if (getWebImagePreviewComponent() != null)
//            lhtWebviewClient.enableImagePreview(getWebImagePreviewComponent());

        //override the WebChromeClient
        getPTRBase().setWebChromeClient(getDefaultFileChooseSupport(), this);

        LhtWebViewNFLoader.with(getBridgeWebView())
                .equip(VsoAuthInfoImpl.newInstance())//Auth信息
                .equip(VsoLoginImpl.newInstance(getActivity()))//Vso登录
                .equip(MakePhoneCallImpl.newInstance(getActivity()))//makePhoneCall
                .equip(DownloadImpl.newInstance(getActivity()))//download
                .equip(VsoTPShareImpl.newInstance(this)) //三方分享
                .equip(SendMessageImpl.newInstance(this)) //发送短信
                .equip(ArticleDetailNavigateImpl.newInstance(getActivity())) //文章详情跳转-共用
                .equip(ProjectDetailNavigateImpl.newInstance(getActivity())) //项目详情跳转-共用
                .equip(CircleDetailNavigateImpl.newInstance(getActivity())) //圈子详情跳转-共用
                .equip(UCenterNavigateImpl.newInstance(getActivity())) //用户中心跳转
                .equip(VsoAcDetailNavigateImpl.newInstance(getActivity())) //活动详情跳转
                .equip(InputIntentImpl.newInstance(getActivity())) //输入意图弹窗
                .equip(CommentListNavigateImpl.newInstance(getActivity())) //评论列表跳转
                .equip(CustomToastImpl.newInstance(getActivity())) //自定义toast
                .equip(GeneralNavigateImpl.newInstance(getActivity())) // 通用跳转
                .equip(PublishArticleNavigateImpl.newInstance(getActivity())) //发布文章跳转
                .equip(PublishProjectNavigateImpl.newInstance(getActivity())) //发布项目跳转
                .equip(PublishProjChapterNavigateImpl.newInstance(getActivity())) //发布项目更新跳转
                .equip(ProjChapterDetailNavigateImpl.newInstance(getActivity())) //项目更新的详情页跳转
                .equip(ImagePreviewImpl.newInstance(getActivity(), REQ_IMAGE_PREVIEW)) //图片预览
                .load();

        //暂不开放功能
        getPTRBase().setMode(PullToRefreshBase.Mode.PULL_FROM_START);
    }

    /**
     * Convenience method to set some generic defaults for a
     * given WebView
     *
     * @param webView
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("SetJavaScriptEnabled")
    private void setUpWebViewDefaults(WebView webView) {
        WebSettings settings = webView.getSettings();
        // Enable Javascript
        settings.setJavaScriptEnabled(true);

        // Use WideViewport and Zoom out if there is no viewport defined
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        // Enable pinch to zoom without the zoom buttons
        settings.setBuiltInZoomControls(true);

//        String cacheDirPath = getLocalPreviewCacheDir().getPath();
//        //设置 应用 缓存目录
//        settings.setAppCachePath(cacheDirPath);
        //开启 DOM 存储功能
        settings.setDomStorageEnabled(true);
//        //开启 数据库 存储功能
//        settings.setDatabaseEnabled(true);
//        //开启 应用缓存 功能
//        settings.setAppCacheEnabled(true);

        // Hide the zoom controls for HONEYCOMB+
        settings.setDisplayZoomControls(false);
        WebView.setWebContentsDebuggingEnabled(true);
    }

    @Override
    protected IApiRequestPresenter getApiRequestPresenter() {
        return null;
    }

    @Override
    public ProgressBar getProgressBar() {
        return getPageProtectPbar();
    }

    protected void load() {
        String url = getUrl();
        DLog.d(DLog.Tmsg.class,"hybrid start load:"+System.currentTimeMillis());
        DLog.i(DLog.Tmsg.class, "load url:" + getPageName() + "   " + url);
        lhtWebviewClient.onLoading();
        getBridgeWebView().loadUrl(url);
    }

    protected abstract String getUrl();

    public abstract void onWebViewReceivedTitle(WebView view, String title);


    private CustomProgressView downloadProgress;

    /**
     * 注意只维护一个下载，注意控制页面
     */
    private String TAG_DOWNLOADING = "";

    /**
     * 订阅并实现调用
     */
    public abstract void onEventMainThread(AppEvent.LoginSuccessEvent event);

    public final void invokeOnEventMainThread(AppEvent.LoginSuccessEvent event) {
        if (event == null) {
            return;
        }

//        if (VsoLoginImpl.LoginTrigger.Bridge.equals(event.getTrigger())) {
//            start(BannerInfoActivity.class);
//        }
    }

    /**
     * 订阅并实现调用
     */
    public abstract void onEventMainThread(DownloadImpl.VsoBridgeDownloadEvent event);


    protected final void invokeOnEventMainThread(DownloadImpl.VsoBridgeDownloadEvent event) {
        int status = event.getStatus();
        TAG_DOWNLOADING = event.getUniqueTag(); //

        switch (status) {
            case DownloadImpl.VsoBridgeDownloadEvent.STATUS_ONSTART:
                //显示进度条
                downloadProgress = new CustomProgressView(this);
                downloadProgress.addOnDissmissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        EventBus.getDefault().post(new DownloadImpl.VsoBridgeDownloadCancelEvent(TAG_DOWNLOADING));
                    }
                });
                downloadProgress.show();
                downloadProgress.setProgress(0, 100);

                break;
            case DownloadImpl.VsoBridgeDownloadEvent.STATUS_DOWNLOADING:
                //更新进度
                updateProgress(event);
                break;
            case DownloadImpl.VsoBridgeDownloadEvent.STATUS_SUCCESS:
                //隐藏进度条
                hideDownloadProgress();

                break;
            case DownloadImpl.VsoBridgeDownloadEvent.STATUS_ERROR:
            case DownloadImpl.VsoBridgeDownloadEvent.STATUS_CANCEL:
                hideDownloadProgress();
                break;
            default:
                break;
        }
    }

    private void updateProgress(DownloadImpl.VsoBridgeDownloadEvent event) {
        if (downloadProgress == null) {
            return;
        }
        if (!downloadProgress.isShowing()) {
            downloadProgress.show();
        }
        long current = event.getCurrentSize();
        long total = event.getTotalSize();

        downloadProgress.setProgress(current, total);
    }

    private void hideDownloadProgress() {
        if (downloadProgress == null) {
            return;
        }
        downloadProgress.dismiss();
        downloadProgress = null;
    }


}