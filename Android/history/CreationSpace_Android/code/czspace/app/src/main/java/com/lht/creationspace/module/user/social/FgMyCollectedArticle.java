package com.lht.creationspace.module.user.social;

import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.fragment.hybrid.AbsHybridFragmentBase;
import com.lht.creationspace.customview.MaskView;
import com.lht.creationspace.hybrid.IHybridPagesCollection;
import com.lht.creationspace.module.home.ui.IHybridBatchOpFragment;
import com.lht.creationspace.hybrid.native4js.impl.DownloadImpl;
import com.lht.creationspace.hybrid.native4js.impl.UcenterBatchOpCloseImpl;
import com.lht.creationspace.hybrid.web4native.WebBridgeCaller;
import com.lht.creationspace.hybrid.web4native.ucenter.UCenterOpenBatchOpReq;
import com.lht.creationspace.hybrid.web4native.ucenter.UcenterCloseBatchOpReq;
import com.lht.lhtwebviewlib.BridgeWebView;
import com.lht.lhtwebviewlib.base.LhtWebViewNFLoader;
import com.lht.ptrlib.library.OnPtrWebRefreshListener;
import com.lht.ptrlib.library.PtrBridgeWebView;
import com.lht.ptrlib.library.PullToRefreshBase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by chhyu on 2017/2/28.
 */

public class FgMyCollectedArticle extends AbsHybridFragmentBase implements IHybridBatchOpFragment {
    private static final String PAGENAME = "FgMyCollectionedArticle";
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
    protected int getContentLayoutRes() {
        return R.layout.fg_my_collection_article;
    }

    @Override
    protected String getUrl() {
        return new IHybridPagesCollection.HybridMyCollectionArticle().getPageUrl();
    }

    @Override
    public void onWebViewReceivedTitle(WebView view, String title) {

    }

    @Override
    protected void initView(View contentView) {
        progressBar = (ProgressBar) contentView.findViewById(R.id.progressbar);
        maskView = (MaskView) contentView.findViewById(R.id.mask);
        ptrBridgeWebView = (PtrBridgeWebView) contentView.findViewById(R.id.ptr_web_view);
    }

    @Override
    protected void initVariable() {
    }

    @Override
    protected void initEvent() {
        EventBus.getDefault().register(this);
        LhtWebViewNFLoader.with(getBridgeWebView())
                .equip(UcenterBatchOpCloseImpl.newInstance(getOnUCenterBatchCloseListener()))
                .load();

        getPTRBase().setOnRefreshListener(new OnPtrWebRefreshListener(getPTRBase(), new OnPtrWebRefreshListener.IUrlGetter() {
            @Override
            public String getPageUrl() {
                return getUrl();
            }
        }) {
            @Override
            public void onRefresh(PullToRefreshBase<BridgeWebView> refreshView) {
                super.onRefresh(refreshView);
                EventBus.getDefault().post(new HybridMyCollectionActivity.NotifyUCenterBatchCloseEvent());
            }
        });
    }

    private UcenterBatchOpCloseImpl.OnUCenterBatchCloseListener getOnUCenterBatchCloseListener() {
        if (parent instanceof UcenterBatchOpCloseImpl.OnUCenterBatchCloseListener) {
            return (UcenterBatchOpCloseImpl.OnUCenterBatchCloseListener) parent;
        } else {
            return null;
        }
    }

    @Override
    public void closeBatchOpState() {
        WebBridgeCaller.with(getBridgeWebView()).call(new UcenterCloseBatchOpReq());
    }

    @Override
    public void openBatchOpState() {
        WebBridgeCaller.with(getBridgeWebView()).call(new UCenterOpenBatchOpReq());
    }

    @Subscribe
    @Override
    public void onEventMainThread(AppEvent.LoginSuccessEvent event) {
        invokeOnEventMainThread(event);
    }

    @Override
    protected String getPageName() {
        return FgMyCollectedArticle.PAGENAME;
    }

    @Subscribe
    @Override
    public void onEventMainThread(DownloadImpl.VsoBridgeDownloadEvent event) {
        invokeOnEventMainThread(event);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}