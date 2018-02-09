package com.lht.creationspace.module.article.ui;

import android.content.Context;
import android.content.Intent;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.hybrid.AbsHybridActivityBase;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.customview.MaskView;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme3;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme3Async;
import com.lht.creationspace.listener.ICallback;
import com.lht.creationspace.module.article.ArticleDetailActivityPresenter;
import com.lht.creationspace.hybrid.native4js.impl.DownloadImpl;
import com.lht.creationspace.hybrid.web4native.WebBridgeCaller;
import com.lht.creationspace.hybrid.web4native.global.QueryPageShareData;
import com.lht.lhtwebviewlib.BridgeWebView;
import com.lht.ptrlib.library.PtrBridgeWebView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 文章详情页
 */
public class HybridArticleDetailActivity extends AbsHybridActivityBase
        implements IArticleDetailActivity {

    private static final String PAGENAME = "HybridArticleDetailActivity";

    private PtrBridgeWebView ptrBridgeWebView;

    private ProgressBar pageProgressBar;

    private String articleId;

    private MaskView maskView;

    private String url;

    private ToolbarTheme3Async titleBar;

    private ArticleDetailActivityPresenter presenter;

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
        return pageProgressBar;
    }

    @Override
    protected String getPageName() {
        return HybridArticleDetailActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return HybridArticleDetailActivity.this;
    }

    @Override
    protected void initView() {
        ptrBridgeWebView = (PtrBridgeWebView) findViewById(R.id.ptr_web_view);
        pageProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        maskView = (MaskView) findViewById(R.id.mask);

        titleBar = (ToolbarTheme3Async) findViewById(R.id.titlebar);
    }

    @Override
    protected void initVariable() {
        presenter = new ArticleDetailActivityPresenter(this);
        HybridArticleDetailActivityData detailActivityData = AbsActivityLauncher.parseData(getIntent(), HybridArticleDetailActivityData.class);
        url = detailActivityData.getUrl();
        articleId = detailActivityData.getOid();
//        title = getIntent().getStringExtra(KEY_TITLE);
//        webImagePreviewComponent = new WebImagePreview.WebImagePreviewComponent(
//                new WebImagePreview.OnWebImageClickListener.DefaultOnWebImageClickListenerIMPL(getActivity(), REQ_IMAGE_PREVIEW),
//                new WebImagePreview.OnWebScrollImpl(getBridgeWebView()));
    }

    @Override
    protected void initEvent() {
        EventBus.getDefault().register(this);
        titleBar.setDefaultOnBackListener(getActivity());

        setSupportActionBar(titleBar);

        presenter.queryCollectState(articleId, false);

        titleBar.setOnShareClickListener(new ICallback() {
            @Override
            public void onCallback() {
                WebBridgeCaller.with(getBridgeWebView())
                        .call(new QueryPageShareData(getActivity()));
            }
        });

        titleBar.useISurfaceConfig(new ToolbarTheme3.ISurfaceConfig() {
            @Override
            public Config getConfigByState(boolean checked) {
                ToolbarTheme3.ISurfaceConfig.Config config = new ToolbarTheme3.ISurfaceConfig.Config();
                config.setEnableDrawable(true);

                if (checked) {
                    config.setDrawableRes(R.drawable.v1000_drawable_xingx_selected);
                } else {
                    config.setDrawableRes(R.drawable.v1000_drawable_xinx2);
                }
                return config;
            }
        });

        titleBar.setRightOpListener(new ToolbarTheme3.ICbOperateListener() {
            @Override
            public void onStateWillBeTrue() {
                presenter.callCollect(articleId);
            }

            @Override
            public void onStateWillBeFalse() {
                presenter.disCollect(articleId);
            }
        });
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.activity_hybrid_article_detail;
    }

    @Override
    protected String getUrl() {
        return url;
    }

    @Override
    public void onWebViewReceivedTitle(WebView view, String title) {
//        if (StringUtil.isEmpty(this.title))
//            titleBar.setTitle(title);
    }

    @Override
    @Subscribe
    public void onEventMainThread(AppEvent.LoginSuccessEvent event) {
        invokeOnEventMainThread(event);
        presenter.handleLoginSuccessEvent(event);
    }

    @Subscribe
    public void onEventMainThread(AppEvent.LoginCancelEvent event) {
        //登录取消的情况下需要回滚UI状态
        if (ArticleDetailActivityPresenter.LoginTrigger.CollectArticle
                .equals(event.getTrigger())) {
            manualSetCollectState(false);
        }
    }

    @Override
    @Subscribe
    public void onEventMainThread(DownloadImpl.VsoBridgeDownloadEvent event) {
        invokeOnEventMainThread(event);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void manualSetCollectState(boolean isCollected) {
        titleBar.manualSetRightOpStatus(isCollected);
    }

    //close preview funtion // TODO: 2017/4/20
//    @Override
//    protected WebImagePreview.WebImagePreviewComponent getWebImagePreviewComponent() {
//        return webImagePreviewComponent;
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //todo  close this to debug bridge,
//        if (requestCode == REQ_IMAGE_PREVIEW) {
//            if (resultCode == RESULT_OK) {
//                int position = data.getIntExtra(ImagePreviewActivity.RET_EXT_INDEX,
//                        webImagePreviewComponent.getPositionHolder());
//                String id = webImagePreviewComponent.getIdsHolder()[position];
//                WebViewJsOffsetHelper.scrollToElement(getBridgeWebView(), id);
//            }
//        }
    }

    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
    }

    public static class Launcher extends AbsActivityLauncher<HybridArticleDetailActivityData> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, HybridArticleDetailActivity.class);
        }

        @Override
        public AbsActivityLauncher<HybridArticleDetailActivityData> injectData(HybridArticleDetailActivityData data) {
            launchIntent.putExtra(data.getClass().getSimpleName(), JSON.toJSONString(data));
            return this;
        }
    }

    /**
     * Created by chhyu on 2017/5/9.
     */

    public static class HybridArticleDetailActivityData {

        private String url;

        private String oid;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getOid() {
            return oid;
        }

        public void setOid(String oid) {
            this.oid = oid;
        }
    }
}
