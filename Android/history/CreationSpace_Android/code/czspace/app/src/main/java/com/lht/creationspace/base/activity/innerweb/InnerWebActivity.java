package com.lht.creationspace.base.activity.innerweb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.lht.creationspace.R;
import com.lht.creationspace.base.activity.UMengActivity;
import com.lht.creationspace.customview.toolBar.AbsNavigationToolBar;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme1;
import com.lht.creationspace.util.string.StringUtil;

public abstract class InnerWebActivity extends UMengActivity {

    private String url;

    private WebView mWebView;

    private ProgressBar progressbar;

    protected ToolbarTheme1 titleBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_web);
    }

    protected void load() {
        url = getUrl();

        mWebView.loadUrl(url);
    }

    @Override
    protected void initView() {
        WebView webview = provideWebView();
        mWebView = webview != null ? webview : (WebView) findViewById(R.id.innerweb_webview);
        ProgressBar pBar = provideProgressBar();
        progressbar = pBar != null ? pBar : (ProgressBar) findViewById(R.id.innerweb_pb);
        titleBar = (ToolbarTheme1) findViewById(R.id.titlebar);
    }

    @Override
    protected void initEvent() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // 不使用cache
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.setWebViewClient(new webViewClient());
        mWebView.setWebChromeClient(new ChromeView());

        titleBar.setTitle(getMyTitle());
        titleBar.setOnBackListener(new AbsNavigationToolBar.ITitleBackListener() {
            @Override
            public void onTitleBackClick() {
                onBackPressed();
            }
        });

    }

    protected abstract WebView provideWebView();

    protected abstract ProgressBar provideProgressBar();


    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else
            super.onBackPressed();
    }

    private class ChromeView extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                progressbar.setVisibility(View.GONE);
            } else {
                if (progressbar.getVisibility() != View.VISIBLE) {
                    progressbar.setVisibility(View.VISIBLE);
                    progressbar.bringToFront();
                }
                progressbar.setProgress(newProgress);
            }
        }
    }

    private class webViewClient extends WebViewClient {

        // 重写shouldOverrideUrlLoading方法，使点击链接后不使用其他的浏览器打开。
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (consumeTel(url)) {
                return true;
            }
            view.loadUrl(url);
            // 如果不需要其他对点击链接事件的处理返回true，否则返回false
            return true;
        }

    }

    private boolean consumeTel(String url) {
        if (StringUtil.isEmpty(url)) {
            return  false;
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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected abstract String getUrl();

    protected abstract int getMyTitle();


}
