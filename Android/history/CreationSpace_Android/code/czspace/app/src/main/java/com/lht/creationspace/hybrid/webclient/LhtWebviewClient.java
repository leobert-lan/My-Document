package com.lht.creationspace.hybrid.webclient;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.webkit.ClientCertRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.customview.MaskView;
import com.lht.creationspace.listener.ICallback;
import com.lht.creationspace.util.debug.DLog;
import com.lht.lhtwebviewlib.BridgeWebView;
import com.lht.lhtwebviewlib.BridgeWebViewClient;
import com.lht.ptrlib.library.PtrBridgeWebView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * <p><b>Package:</b> com.lht.creationspace.webclient </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> LhtWebviewClient </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/3/28.
 */

public class LhtWebviewClient extends BridgeWebViewClient {

    public static final long TIME_OUT_SECONDS = 10000; //首次10秒

    private boolean isHybridPageFail = false;

    private final WeakReference<MaskView> maskViewRef;

    private String originUrl;

    private final TimeOutHandler timeOutHandler;

    private boolean isLoading = false;

    private boolean imagePreviewEnabled = false;

    private BridgeWebView bridgeWebView;

    public void onLoading() {
        isLoading = true;
    }

    public LhtWebviewClient(BridgeWebView webView, MaskView maskView, String originUrl) {
        super(webView);
        this.bridgeWebView = webView;
        maskViewRef = new WeakReference<>(maskView);
        this.originUrl = originUrl;
        this.timeOutHandler = new TimeOutHandler(maskView, webView, originUrl);
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
        timeOutHandler.setUrl(originUrl);
    }

    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
        DLog.v(DLog.Tmsg.class, "load res:" + url);
    }

    /**
     * @return the state of imagepreview
     */
    public boolean hasImagePreviewEnabled() {
        return imagePreviewEnabled;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onReceivedError(WebView view, WebResourceRequest request,
                                WebResourceError error) {
        super.onReceivedError(view, request, error);
        DLog.i(BridgeWebViewClient.class, "received error on new android");

        if (request.isForMainFrame()) {
//            ||isLhtMainFrameError(request)) {
            DLog.d(BridgeWebViewClient.class, "handled");
            isHybridPageFail = true;
            showLoadingFailed(view);
            tryRemoveTimeoutHandler(view);
        } else {
            DLog.w(BridgeWebViewClient.class, "not for main frame\r\n" + "WebResourceRequest:\r\n"
                    + JSON.toJSONString(request)
                    + "\r\nWebResourceError:" + JSON.toJSONString(error));
        }
    }

    private void showLoadingFailed(final WebView webview) {
        if (maskViewRef.get() != null) {
            maskViewRef.get().showLoadFailMask(new ICallback() {
                @Override
                public void onCallback() {
                    webview.loadUrl(originUrl);
                }
            });
        }

    }

    @Override
    public void onReceivedError(WebView view, int errorCode,
                                String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        DLog.i(BridgeWebViewClient.class, "received error on old-android,code:"
                + errorCode + "url:" + failingUrl + "\r\n desc:" + description);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return;
        }
        String pageUrl = originUrl;
        boolean shouldHandle = pageUrl.equalsIgnoreCase(failingUrl);
//                || isLhtMainFrameError(failingUrl);
        if (shouldHandle) {
            isHybridPageFail = true;
            showLoadingFailed(view);
            tryRemoveTimeoutHandler(view);
        }
    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request,
                                    WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
        String failureUrl = request.getUrl().getScheme() + ":" + request.getUrl().getSchemeSpecificPart();
        String pageUrl = originUrl;
        boolean shouldHandle = request.isForMainFrame()
                || pageUrl.equalsIgnoreCase(failureUrl);
//                || isLhtMainFrameError(request);
        if (shouldHandle) {
            isHybridPageFail = true;
            showLoadingFailed(view);
            tryRemoveTimeoutHandler(view);
        }

        DLog.i(BridgeWebViewClient.class, "onReceivedHttpError:\r\n" + "furl:  " + failureUrl
                + "\r\npageurl:  " + pageUrl);
        DLog.d(BridgeWebViewClient.class, "onReceivedHttpError\r\nreq:" + JSON.toJSONString(request)
                + "\r\nres:" + JSON.toJSONString(errorResponse) + "\r\nshould native handle:" + shouldHandle);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed();//接受证书
    }

    @Override
    public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
        super.onReceivedClientCertRequest(view, request);
        //暂无证书
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        DLog.d(DLog.Tmsg.class, "load page start" + System.currentTimeMillis() + "\r\n" + url);

        isHybridPageFail = false;
        isLoading = true;
        if (view instanceof PtrBridgeWebView.IRefreshStateHolder) {
            boolean isManual = ((PtrBridgeWebView.IRefreshStateHolder) view).isManualRefresh();
            //manual ignore cover
            if (!isManual && maskViewRef.get() != null) {
                maskViewRef.get().showLoadingMask();
            }
        } else {
            // may be error,just for safety
            DLog.d(DLog.Lmsg.class, new DLog.LogLocation(), "error type");
            if (maskViewRef.get() != null)
                maskViewRef.get().showLoadingMask();
        }
        timeOutHandler.reset();
        view.postDelayed(timeOutHandler, TIME_OUT_SECONDS);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        tryRemoveTimeoutHandler(view);
        isLoading = false;
        notifyFinishListeners();
        DLog.i(BridgeWebViewClient.class, "onPageFinished" + url);
        DLog.d(DLog.Tmsg.class, "load finish," + System.currentTimeMillis() + "\r\n" + url);

        if (isHybridPageFail) {
            showLoadingFailed(view);
            return;
        }
        if (timeOutHandler.isTimeout()) {
            showLoadingFailed(view);
            return;
        }
        if (maskViewRef.get() != null)
            maskViewRef.get().close();
    }

    private void notifyFinishListeners() {
        for (int i = 0; i < onLoadFinishListeners.size(); i++) {
            OnLoadFinishListener listener = onLoadFinishListeners.get(i);
            if (listener != null) {
                listener.onLoadFinish();

                if (listener instanceof OneTimeOnLoadFinishListener)
                    onLoadFinishListeners.remove(i);
            }
        }
    }

    private void tryRemoveTimeoutHandler(WebView view) {
        try {
            view.removeCallbacks(timeOutHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class TimeOutHandler implements Runnable {

        private BridgeWebView bridgeWebView;

        private final WeakReference<MaskView> maskRef;

        private String url;

        private boolean isTimeout;

        public void reset() {
            isTimeout = false;
        }

        public boolean isTimeout() {
            return isTimeout;
        }

        public TimeOutHandler(MaskView maskView, BridgeWebView bridgeWebView,
                              String url) {
            maskRef = new WeakReference<>(maskView);
            this.bridgeWebView = bridgeWebView;
            this.url = url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            isTimeout = true;
            bridgeWebView.stopLoading();
            if (maskRef.get() != null) {
                maskRef.get().showLoadFailMask(new ICallback() {
                    @Override
                    public void onCallback() {
                        bridgeWebView.loadUrl(url);
                    }
                });
            } else {
                DLog.w(DLog.Lmsg.class, "mask ref get null");
            }
//            bridgeWebView.removeCallbacks(this);
        }
    }


    private ArrayList<OnLoadFinishListener> onLoadFinishListeners
            = new ArrayList<>();

    public void addOnLoadFinishListener(OnLoadFinishListener listener) {
        onLoadFinishListeners.add(listener);
    }


    public interface OnLoadFinishListener {
        void onLoadFinish();
    }

    public static abstract class OneTimeOnLoadFinishListener
            implements OnLoadFinishListener {
    }
}