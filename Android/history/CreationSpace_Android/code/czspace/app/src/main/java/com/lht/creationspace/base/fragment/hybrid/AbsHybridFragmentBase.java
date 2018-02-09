package com.lht.creationspace.base.fragment.hybrid;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.customview.popup.CustomProgressView;
import com.lht.creationspace.customview.MaskView;
import com.lht.creationspace.base.fragment.BaseFragment;
import com.lht.creationspace.hybrid.native4js.impl.ProjChapterDetailNavigateImpl;
import com.lht.creationspace.hybrid.native4js.impl.PublishProjChapterNavigateImpl;
import com.lht.creationspace.hybrid.webclient.OnWebReceivedTitleListener;
import com.lht.creationspace.cfg.IPublicConst;
import com.lht.creationspace.base.vinterface.IAsyncProtectedFragment;
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
 * <p><b>Package</b> com.lht.vsocyy.fragment.hybrid
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> AbsHybridFragmentBase
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2017/2/10.
 */

public abstract class AbsHybridFragmentBase extends BaseFragment
        implements IAsyncProtectedFragment, OnWebReceivedTitleListener {

    protected AbsHybridFragmentActivity parent;

    protected void setParent(AbsHybridFragmentActivity parent) {
        this.parent = parent;
    }

    public static <T extends AbsHybridFragmentBase> T newInstance(Class<T> clazz, AbsHybridFragmentActivity parent) {
        T t = null;
        try {
            t = clazz.newInstance();
            t.setParent(parent);
        } catch (java.lang.InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return t;
    }

    private View contentView;

    protected LhtWebviewClient lhtWebviewClient;

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater,
                                   @Nullable ViewGroup container,
                                   @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(getContentLayoutRes(), container, false);
        onContentViewBeenSet(contentView);
        return contentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        boolean b = parent == null;
        if (context instanceof AbsHybridFragmentActivity)
            parent = (AbsHybridFragmentActivity) context;
        DLog.d(DLog.Lmsg.class, getClass().getSimpleName() + "  has parent been destroyed:" + b);
        if (b) {
            if (getPTRBase() != null)
                load();
            else {
                DLog.w(getClass(), "webview is null on fragment attach");
            }

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BridgeWebView webView = getBridgeWebView();
        if (webView == null) {
            DLog.e(getClass(), "webview is null on fragment detach");
            return;
        }

        webView.stopLoading();
        webView.removeAllViews();
        webView.destroy();
    }

    @Override
    public void showWaitView(boolean isProtectNeed) {
        parent.showWaitView(isProtectNeed);
    }

    @Override
    public void cancelWaitView() {
        parent.cancelWaitView();
    }

    @Override
    public Resources getAppResource() {
        return parent.getAppResource();
    }

    @Override
    public void showMsg(String msg) {
        parent.showMsg(msg);
    }

    @Override
    public void showErrorMsg(String msg) {
        parent.showMsg(msg);
    }


    protected abstract BridgeWebView getBridgeWebView();

    protected abstract PtrBridgeWebView getPTRBase();

    protected abstract MaskView getWebMask();

    protected abstract ProgressBar getPageProtectPbar();

    protected abstract int getContentLayoutRes();

    protected final void onContentViewBeenSet(View contentView) {
        initView(contentView);
        initVariable();
        initWebView();
        initEvent(); // eventbus regist in initEvent
//        getPTRBase().setiUrlGetter(new PtrBridgeWebView.IUrlGetter() {
//            @Override
//            public String getPageUrl() {
//                return getUrl();
//            }
//        });
        load();
    }

    private IFileChooseSupport.DefaultFileChooseSupportImpl getDefaultFileChooseSupport() {
        if (parent != null)
            return parent.getDefaultFileChooseSupport();
        else
            return null;
    }

    @Override
    public void onRestrictResume() {
        super.onRestrictResume();

        DLog.d(DLog.Lmsg.class, getPageName() + "fg resume");
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
    public void onRestrictPause() {
        super.onRestrictPause();
        getBridgeWebView().onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (getDefaultFileChooseSupport() != null)
            getDefaultFileChooseSupport().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        WebSettings webSettings = getBridgeWebView().getSettings();

        String originUA = webSettings.getUserAgentString();
        String myUA = originUA + " " + IPublicConst.USER_AGENT;
        webSettings.setUserAgentString(myUA);

        webSettings.setJavaScriptEnabled(true);
        // 不使用cache
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        setUpWebViewDefaults(getBridgeWebView());

        //忽略安全问题
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        MWebChromeClient webChromeClient =
                new MWebChromeClient(getDefaultFileChooseSupport(), this);

        getBridgeWebView().setDefaultHandler(new DefaultHandler());
        lhtWebviewClient = new LhtWebviewClient(getBridgeWebView(),
                getWebMask(), getUrl());
        getBridgeWebView().setWebViewClient(lhtWebviewClient);
        getBridgeWebView().setWebChromeClient(webChromeClient);

        getPTRBase().setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        getPTRBase().setWebChromeClient(getDefaultFileChooseSupport(), this);

        LhtWebViewNFLoader.with(getBridgeWebView())
                .equip(VsoAuthInfoImpl.newInstance())//Auth信息
                .equip(VsoLoginImpl.newInstance(getActivity()))//Vso登录
                .equip(MakePhoneCallImpl.newInstance(getActivity()))//makePhoneCall
                .equip(DownloadImpl.newInstance(getActivity()))//download
                .equip(VsoTPShareImpl.newInstance(parent)) //三方分享
                .equip(SendMessageImpl.newInstance(parent)) //发送短信
                .equip(ArticleDetailNavigateImpl.newInstance(parent)) //文章详情跳转-共用
                .equip(ProjectDetailNavigateImpl.newInstance(parent)) //项目详情跳转-共用
                .equip(CircleDetailNavigateImpl.newInstance(parent)) //圈子详情跳转-共用
                .equip(UCenterNavigateImpl.newInstance(parent)) //用户中心跳转
                .equip(VsoAcDetailNavigateImpl.newInstance(parent)) //活动详情跳转
                .equip(InputIntentImpl.newInstance(parent)) //输入意图弹窗
                .equip(CommentListNavigateImpl.newInstance(parent)) //评论列表跳转
                .equip(CustomToastImpl.newInstance(parent)) //自定义toast
                .equip(GeneralNavigateImpl.newInstance(parent)) // 通用跳转
                .equip(PublishArticleNavigateImpl.newInstance(parent)) //发布文章跳转
                .equip(PublishProjectNavigateImpl.newInstance(parent)) //发布项目跳转
                .equip(PublishProjChapterNavigateImpl.newInstance(parent)) //发布项目更新跳转
                .equip(ProjChapterDetailNavigateImpl.newInstance(parent)) //项目更新的详情页跳转
                .equip(ImagePreviewImpl.newInstance(parent, parent.REQ_IMAGE_PREVIEW))
                .load();
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

    protected void load() {
        String url = getUrl();
        DLog.i(DLog.Tmsg.class, "fg load url:" + getPageName() + "   " + url
                +"\r\n"+System.currentTimeMillis());
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

        //单例页面可能要单独处理，但fragment暂时没有必要
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
                downloadProgress = new CustomProgressView(parent);
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

    protected boolean onBackPressed() {
        return false;
    }

}
