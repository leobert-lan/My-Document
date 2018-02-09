package com.lht.ptrlib.library;

import com.lht.creationspace.util.debug.DLog;
import com.lht.lhtwebviewlib.BridgeWebView;

import java.lang.ref.WeakReference;

import static com.lht.creationspace.hybrid.webclient.LhtWebviewClient.TIME_OUT_SECONDS;

/**
 * Created by chhyu on 2017/3/31.
 */

public class OnPtrWebRefreshListener implements PullToRefreshBase.OnRefreshListener<BridgeWebView> {

    public interface IUrlGetter {
        String getPageUrl();
    }

    private final PtrBridgeWebView ptrBridgeWebView;

    private final WeakReference<IUrlGetter> iUrlGetterWeakReference;

    public OnPtrWebRefreshListener(PtrBridgeWebView ptrBridgeWebView, IUrlGetter iUrlGetter) {
        this.ptrBridgeWebView = ptrBridgeWebView;
        iUrlGetterWeakReference = new WeakReference<>(iUrlGetter);
    }

    private final Runnable onTimeOut = new Runnable() {
        @Override
        public void run() {
            if (ptrBridgeWebView.isRefreshing()) {
                ptrBridgeWebView.getRefreshableView().stopLoading();
                ptrBridgeWebView.onRefreshComplete();
            }
            ptrBridgeWebView.getRefreshableView().removeCallbacks(this);
        }
    };

    @Override
    public void onRefresh(PullToRefreshBase<BridgeWebView> refreshView) {
        BridgeWebView bridgeWebView = refreshView.getRefreshableView();
        if (bridgeWebView instanceof PtrBridgeWebView.IRefreshStateHolder) {
            ((PtrBridgeWebView.IRefreshStateHolder) bridgeWebView).setManualRefreshFlag(true);
        } else
            DLog.d(DLog.Lmsg.class, new DLog.LogLocation(), "error type");
        if (iUrlGetterWeakReference != null && iUrlGetterWeakReference.get() != null) {
            refreshView.getRefreshableView().loadUrl(iUrlGetterWeakReference.get().getPageUrl());
        } else {
            refreshView.getRefreshableView().reload();
        }
        refreshView.getRefreshableView().postDelayed(onTimeOut, TIME_OUT_SECONDS);
    }

}
