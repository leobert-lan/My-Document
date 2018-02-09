package com.lht.cloudjob.activity.asyncprotected;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.MainApplication;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.clazz.BadgeNumberManager;
import com.lht.cloudjob.clazz.GlobalLifecycleMonitor;
import com.lht.cloudjob.customview.CustomProgressView;
import com.lht.cloudjob.customview.TitleBar;
import com.lht.cloudjob.interfaces.IPublicConst;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.native4js.impl.DownloadImpl;
import com.lht.cloudjob.native4js.impl.TaskCenterRestrictImpl;
import com.lht.cloudjob.native4js.impl.VsoAuthInfoImpl;
import com.lht.cloudjob.native4js.impl.VsoDemandInfoRestrictImpl;
import com.lht.cloudjob.native4js.impl.VsoLoginImpl;
import com.lht.cloudjob.native4js.impl.VsoTPShareImpl;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.string.StringUtil;
import com.lht.lhtwebviewapi.Interface.IJSFuncCollection;
import com.lht.lhtwebviewapi.business.impl.MakePhoneCallImpl;
import com.lht.lhtwebviewapi.business.impl.SendMessageImpl;
import com.lht.lhtwebviewlib.BridgeWebView;
import com.lht.lhtwebviewlib.BridgeWebViewClient;
import com.lht.lhtwebviewlib.DefaultHandler;
import com.lht.lhtwebviewlib.FileChooseBridgeWebChromeClient;
import com.lht.lhtwebviewlib.base.IMediaTransImpl;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;
import com.lht.lhtwebviewlib.base.Interface.IFileChooseSupport;
import com.lht.lhtwebviewlib.base.LhtWebViewNFLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class BannerInfoActivity extends AsyncProtectedActivity implements
        GlobalLifecycleMonitor.ISingletonActivityFriendlyOptimize{

    public static final String KEY_DATA = "_key_url";

    private static final String PAGENAME = "BannerInfoActivity";

    public static final String KEY_HASEXCUTEBADGE = "_key_hasexcutebadge";

    private boolean hasExcuteBadge;

    private TitleBar titleBar;

    private BridgeWebView bridgeWebView;

    private ProgressBar webLoadProgressBar;

    private ProgressBar pageProtectPbar;

    private IFileChooseSupport.DefaultFileChooseSupportImpl defaultFileChooseSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_info);
        initView();
        initVariable();
        initEvent(); // eventbus regist in initEvent
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getBooleanExtra(UMengActivity.KEY_IS_RESTRICT_INTENT,false)) {
            super.onNewIntent(intent);
            return;
        }
        setIntent(intent);
        super.onNewIntent(intent);
        load();
    }

    @Override
    protected String getPageName() {
        return BannerInfoActivity.PAGENAME;
    }

    @Override
    public UMengActivity getActivity() {
        return BannerInfoActivity.this;
    }

    @Override
    public void onBackPressed() {
        if (bridgeWebView.canGoBack()) {
            bridgeWebView.goBack();
        } else
            super.onBackPressed();
    }

    @Override
    protected void onLastChainInBackPressedWillCall() {
        super.onLastChainInBackPressedWillCall();
        MainApplication.getOurInstance().startHomeIfNecessary();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bridgeWebView.onResume();
        bridgeWebView.callHandler(IJSFuncCollection.JF_ONRESUME, null, new CallBackFunction() {
            @Override
            public void onCallBack(String s) {
                DLog.d(BannerInfoActivity.class,"web page consume onResume");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        bridgeWebView.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        defaultFileChooseSupport.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && bridgeWebView.canGoBack()) {
            bridgeWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void initView() {
        bridgeWebView = (BridgeWebView) findViewById(R.id.innerweb_webview);
        webLoadProgressBar = (ProgressBar) findViewById(R.id.innerweb_pb);
        titleBar = (TitleBar) findViewById(R.id.titlebar);
        pageProtectPbar = (ProgressBar) findViewById(R.id.progressbar);

    }

    @Override
    protected void initVariable() {
        defaultFileChooseSupport = new IFileChooseSupport.DefaultFileChooseSupportImpl(this);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initEvent() {
        titleBar.setOnBackListener(new TitleBar.ITitleBackListener() {
            @Override
            public void onTitleBackClick() {
                onBackPressed();
            }
        });
        titleBar.setTitle(R.string.title_activity_banner_info);

        WebSettings webSettings = bridgeWebView.getSettings();

        String originUA = webSettings.getUserAgentString();
        String myUA = originUA + " " + IPublicConst.USER_AGENT;
        DLog.d(getClass(), "new web ua:" + myUA);
        webSettings.setUserAgentString(myUA);

        webSettings.setJavaScriptEnabled(true);
        // 不使用cache
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        setUpWebViewDefaults(bridgeWebView);

        MWebChromeClient webChromeClient = new MWebChromeClient(defaultFileChooseSupport);
        webChromeClient.setIMediaTrans(new IMediaTransImpl(getActivity(),null));

        bridgeWebView.setDefaultHandler(new DefaultHandler());
        bridgeWebView.setWebViewClient(new WebViewClient(bridgeWebView));
        bridgeWebView.setWebChromeClient(webChromeClient);

        LhtWebViewNFLoader.with(bridgeWebView)
                .equip(VsoAuthInfoImpl.newInstance())//Auth信息
                .equip(VsoLoginImpl.newInstance(getActivity()))//Vso登录
                .equip(MakePhoneCallImpl.newInstance(getActivity()))//makePhoneCall
                .equip(DownloadImpl.newInstance(getActivity()))//download
                .equip(VsoDemandInfoRestrictImpl.newInstance(getActivity())) //需求详情
                .equip(VsoTPShareImpl.newInstance(getActivity())) //三方分享
                .equip(SendMessageImpl.newInstance(getActivity())) //发送短信
                .equip(TaskCenterRestrictImpl.newInstance(getActivity())) //需求大厅跳转
                .load();
        EventBus.getDefault().register(this);

        load();
    }

    /**
     * Convenience method to set some generic defaults for a
     * given WebView
     *
     * @param webView
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setUpWebViewDefaults(WebView webView) {
        WebSettings settings = webView.getSettings();

        // Enable Javascript
        settings.setJavaScriptEnabled(true);

        // Use WideViewport and Zoom out if there is no viewport defined
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        // Enable pinch to zoom without the zoom buttons
        settings.setBuiltInZoomControls(true);

        // Allow use of Local Storage
        settings.setDomStorageEnabled(true);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            settings.setDisplayZoomControls(false);
        }

        // Enable remote debugging via chrome://inspect
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    @Override
    protected IApiRequestPresenter getApiRequestPresenter() {
        return null;
    }

    @Override
    public ProgressBar getProgressBar() {
        return pageProtectPbar;
    }

    protected void load() {
        String url = getUrl();
        DLog.d(getClass(), "url is:" + url);
        bridgeWebView.loadUrl(url);
        hasExcuteBadge = getIntent().getBooleanExtra(KEY_HASEXCUTEBADGE,true);
        //默认都是处理过的，只有从通知栏等进的需要处理一下
        if (!hasExcuteBadge) {
            hasExcuteBadge = true;
            BadgeNumberManager.getInstance().resetVsoActivityNotify();
        }
    }

    private String getUrl() {
        return getIntent().getStringExtra(KEY_DATA);
    }

    private class MWebChromeClient extends FileChooseBridgeWebChromeClient {

        public MWebChromeClient(@NonNull IFileChooseSupport iFileChooseSupport) {
            super(iFileChooseSupport);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (StringUtil.isEmpty(title)) {
                titleBar.setTitle(R.string.title_activity_banner_info);
            } else {
                titleBar.setTitle(title);
            }
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                webLoadProgressBar.setVisibility(View.GONE);
            } else {
                if (webLoadProgressBar.getVisibility() != View.VISIBLE) {
                    webLoadProgressBar.setVisibility(View.VISIBLE);
                    webLoadProgressBar.bringToFront();
                }
                webLoadProgressBar.setProgress(newProgress);
            }
        }
    }

    private class WebViewClient extends BridgeWebViewClient {

        public WebViewClient(BridgeWebView webView) {
            super(webView);
        }

        // 重写shouldOverrideUrlLoading方法，使点击链接后不使用其他的浏览器打开。
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (consumeTel(url)) {
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

    }

    private boolean consumeTel(String url) {
        if (StringUtil.isEmpty(url)) {
            return false;
        }
        try {
            Uri uri = Uri.parse(url);
            if (uri.getScheme().toLowerCase().equals("tel")) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(uri);
                getActivity().startActivity(intent);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }


    private CustomProgressView downloadProgress;

    /**
     * 注意只维护一个下载，注意控制页面
     */
    private String TAG_DOWNLOADING = "";

    @Subscribe
    public void onEventMainThread(AppEvent.LoginSuccessEvent event) {
        if (event == null) {
            return;
        }

        if (VsoLoginImpl.LoginTrigger.Bridge.equals(event.getTrigger())) {
            start(BannerInfoActivity.class);
        }
    }

    @Subscribe
    public void onEventMainThread(DownloadImpl.VsoBridgeDownloadEvent event) {
        int status = event.getStatus();
        TAG_DOWNLOADING = event.getUniqueTag(); //

        switch (status) {
            case DownloadImpl.VsoBridgeDownloadEvent.STATUS_ONSTART:
                //显示进度条
                downloadProgress = new CustomProgressView(getActivity());
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

    @Override
    public void restrictBackStack() {
        Intent intent = getIntent();
        if (intent!= null) {
            String s = MainApplication.getOurInstance().getMainStackTopActivityPath();
            intent.putExtra(UMengActivity.KEY_ORIGINCLASS_SHOULDRESTRICTONFINISH, s);
            setIntent(intent);
        } else {
            DLog.e(getClass(),"intent is null! onRestrictBackStack");
        }
    }

    @Subscribe
    @Override
    public void onEventMainThread(GlobalLifecycleMonitor.EventApplicationStartOrResumeFromHome event) {
        DLog.d(getClass(), "receive app start or resume");
        restrictBackStack();
    }

}
