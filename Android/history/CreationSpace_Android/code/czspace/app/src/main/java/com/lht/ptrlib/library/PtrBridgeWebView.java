package com.lht.ptrlib.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;

import com.lht.creationspace.R;
import com.lht.creationspace.hybrid.webclient.OnWebReceivedTitleListener;
import com.lht.creationspace.util.debug.DLog;
import com.lht.creationspace.util.time.TimeUtil;
import com.lht.creationspace.hybrid.webclient.MWebChromeClient;
import com.lht.lhtwebviewlib.BridgeWebView;
import com.lht.lhtwebviewlib.base.Interface.IFileChooseSupport;
import com.lht.ptrlib.library.internal.AbsLoadingLayout;

/**
 * <p><b>Package:</b> com.lht.creationspace.customview.ptrextend </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> PtrBridgeWebView </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/3/20.
 */

public class PtrBridgeWebView extends PullToRefreshBase<BridgeWebView> {

    private static long TIME_OUT_SECONDS = 5000;

    public static void setTimeOutSeconds(long timeOutSeconds) {
        TIME_OUT_SECONDS = timeOutSeconds;
    }

    private final OnRefreshListener<BridgeWebView> defaultOnRefreshListener =
            new OnRefreshListener<BridgeWebView>() {

                @Override
                public void onRefresh(PullToRefreshBase<BridgeWebView> refreshView) {
                    BridgeWebView bridgeWebView = refreshView.getRefreshableView();
                    if (bridgeWebView instanceof IRefreshStateHolder) {
                        ((IRefreshStateHolder) bridgeWebView).setManualRefreshFlag(true);
                    } else {
                        DLog.d(DLog.Lmsg.class, new DLog.LogLocation(), "error type");
                    }
                    refreshView.getRefreshableView().reload();
                    refreshView.getRefreshableView().postDelayed(onTimeOut, TIME_OUT_SECONDS);
                }
            };


//    private WeakReference<IUrlGetter> iUrlGetterWeakReference;
//
//    public void setiUrlGetter(IUrlGetter iUrlGetter) {
//        this.iUrlGetterWeakReference = new WeakReference<>(iUrlGetter);
//    }

    private final Runnable onTimeOut = new Runnable() {
        @Override
        public void run() {
            if (isRefreshing()) {
                getRefreshableView().stopLoading();
                onRefreshComplete();
            }
            getRefreshableView().removeCallbacks(this);
        }
    };

    public PtrBridgeWebView(Context context) {
        super(context);
        this.setOnRefreshListener(defaultOnRefreshListener);
        init(context);
    }

    public PtrBridgeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnRefreshListener(defaultOnRefreshListener);
        init(context);
    }

    public PtrBridgeWebView(Context context, Mode mode) {
        super(context, mode);
        this.setOnRefreshListener(defaultOnRefreshListener);
        init(context);
    }

    public PtrBridgeWebView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
        this.setOnRefreshListener(defaultOnRefreshListener);
        init(context);

    }

    private void init(Context context) {
        setBackgroundColor(ContextCompat.getColor(context, R.color.primary_background));
        getRefreshableViewWrapper().setBackgroundResource(R.color.primary_background);
        getRefreshableView().setBackgroundResource(R.color.primary_background);
        getRefreshableView().setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //prevent the copy
                final WebView webview = (WebView) v;
                final WebView.HitTestResult result = webview.getHitTestResult();
                if (result.getType() == WebView.HitTestResult.IMAGE_TYPE) {

                    // 1. the picture must be focused, so we simulate a DPAD enter event to trigger the hyperlink
                    KeyEvent event1 = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_CENTER);
                    webview.dispatchKeyEvent(event1);
                    KeyEvent event2 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_CENTER);
                    webview.dispatchKeyEvent(event2);
                    // 3. now you can do something with the anchor url (and then clear the mUrl for future usage)
                    String extra = result.getExtra();
                    Log.d("lmsg", "hit test extra:" + extra);
                }
                return true;
            }
        });
    }

    public void setWebChromeClient(@NonNull IFileChooseSupport iFileChooseSupport,
                                   OnWebReceivedTitleListener onWebReceivedTitleListener) {
        this.mRefreshableView.setWebChromeClient(new MWebChromeClient(iFileChooseSupport,
                onWebReceivedTitleListener) {

            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    PtrBridgeWebView.this.onRefreshComplete();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("最近更新:");
                    stringBuilder.append(TimeUtil.getCurrentTimeInString());
                    setLastUpdatedLabel(stringBuilder.toString());

                    if (view instanceof IRefreshStateHolder) {
                        ((IRefreshStateHolder) view).setManualRefreshFlag(false);
                    }
                    getRefreshableView().removeCallbacks(onTimeOut);
                }
            }
        });
    }

    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    protected BridgeWebView createRefreshableView(Context context, AttributeSet attrs) {
        Object webView;
        if (Build.VERSION.SDK_INT >= 9) {
            webView = new PtrBridgeWebView.InternalWebViewSDK9(context, attrs);
        } else {
//            webView = new BridgeWebView(context, attrs) ;
            webView = new IRefreshStateHolder.BridgeWebViewImpl(context, attrs);
        }

        ((BridgeWebView) webView).setId(com.lht.ptrlib.R.id.webview);
        return (BridgeWebView) webView;
    }

    protected boolean isReadyForPullStart() {
        return this.mRefreshableView.getScrollY() == 0;
    }

    protected boolean isReadyForPullEnd() {
        float exactContentHeight =
                (float) Math.floor((double) ((float) this.mRefreshableView.getContentHeight()
                        * this.mRefreshableView.getScale()));
        return (float) this.mRefreshableView.getScrollY() >=
                exactContentHeight - (float) this.mRefreshableView.getHeight();
    }

    protected void onPtrRestoreInstanceState(Bundle savedInstanceState) {
        super.onPtrRestoreInstanceState(savedInstanceState);
        this.mRefreshableView.restoreState(savedInstanceState);
    }

    protected void onPtrSaveInstanceState(Bundle saveState) {
        super.onPtrSaveInstanceState(saveState);
        this.mRefreshableView.saveState(saveState);
    }

    @TargetApi(9)
    final class InternalWebViewSDK9 extends BridgeWebView implements IRefreshStateHolder {
        static final int OVERSCROLL_FUZZY_THRESHOLD = 2;
        static final float OVERSCROLL_SCALE_FACTOR = 1.5F;

        public InternalWebViewSDK9(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        protected boolean overScrollBy(int deltaX, int deltaY,
                                       int scrollX, int scrollY,
                                       int scrollRangeX, int scrollRangeY,
                                       int maxOverScrollX, int maxOverScrollY,
                                       boolean isTouchEvent) {

            boolean returnValue = super.overScrollBy(deltaX, deltaY,
                    scrollX, scrollY, scrollRangeX, scrollRangeY,
                    maxOverScrollX, maxOverScrollY, isTouchEvent);

            OverscrollHelper.overScrollBy(PtrBridgeWebView.this,
                    deltaX, scrollX, deltaY, scrollY, this.getScrollRange(),
                    OVERSCROLL_FUZZY_THRESHOLD, OVERSCROLL_SCALE_FACTOR, isTouchEvent);

            return returnValue;
        }

        private int getScrollRange() {
            return (int) Math.max(0.0D,
                    Math.floor((double) ((float) PtrBridgeWebView.this.mRefreshableView
                            .getContentHeight() * PtrBridgeWebView.this.mRefreshableView.getScale()))
                            - (double) (this.getHeight() - this.getPaddingBottom() - this.getPaddingTop()));
        }

        private boolean onManualRefresh;


        @Override
        public void setManualRefreshFlag(boolean onManual) {
            this.onManualRefresh = onManual;
        }

        @Override
        public boolean isManualRefresh() {
            return onManualRefresh;
        }
    }

    @Override
    protected AbsLoadingLayout createLoadingLayout(Context context, PullToRefreshBase.Mode mode, TypedArray attrs) {

        AbsLoadingLayout layout =
                new PtrLoadingLayoutTheme1(context, mode, getPullToRefreshScrollDirection(), attrs);

        layout.setVisibility(INVISIBLE);
        return layout;
    }

    public interface IRefreshStateHolder {
        void setManualRefreshFlag(boolean onManual);

        boolean isManualRefresh();

        class BridgeWebViewImpl extends BridgeWebView implements IRefreshStateHolder {

            public BridgeWebViewImpl(Context context, AttributeSet attrs) {
                super(context, attrs);
            }

            public BridgeWebViewImpl(Context context, AttributeSet attrs, int defStyle) {
                super(context, attrs, defStyle);
            }

            public BridgeWebViewImpl(Context context) {
                super(context);
            }

            private boolean onManualRefresh;


            @Override
            public void setManualRefreshFlag(boolean onManual) {
                this.onManualRefresh = onManual;
            }

            @Override
            public boolean isManualRefresh() {
                return onManualRefresh;
            }
        }
    }
}
