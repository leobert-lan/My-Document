package com.lht.creationspace.module.topic.ui;

import android.content.Context;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.hybrid.AbsHybridActivityBase;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.customview.MaskView;
import com.lht.creationspace.customview.popup.CustomPopupWindow;
import com.lht.creationspace.customview.popup.dialog.CustomDialog;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme2;
import com.lht.creationspace.hybrid.native4js.impl.DownloadImpl;
import com.lht.creationspace.hybrid.webclient.LhtWebviewClient;
import com.lht.creationspace.listener.ICallback;
import com.lht.creationspace.module.article.ui.ArticlePublishActivity;
import com.lht.creationspace.module.topic.HybridCircleDetailActivityPresenter;
import com.lht.creationspace.module.topic.model.CircleJoinStateModel;
import com.lht.creationspace.util.string.StringUtil;
import com.lht.lhtwebviewlib.BridgeWebView;
import com.lht.lhtwebviewlib.base.LhtWebViewNFLoader;
import com.lht.ptrlib.library.PtrBridgeWebView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class HybridCircleDetailActivity extends AbsHybridActivityBase
        implements IHybridCircleDetailActivity {

    private String circleId;

    private String url;

    private static final String PAGENAME = "HybridCircleDetailActivity";

    private PtrBridgeWebView ptrBridgeWebView;

    private ProgressBar pageProgressBar;

    private MaskView maskView;

    private ToolbarTheme2 titleBar;

    private String title;
    private HybridCircleDetailActivityPresenter presenter;

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
        return HybridCircleDetailActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return HybridCircleDetailActivity.this;
    }

    @Override
    protected void initView() {
        ptrBridgeWebView = (PtrBridgeWebView) findViewById(R.id.ptr_web_view);
        pageProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        maskView = (MaskView) findViewById(R.id.mask);
        titleBar = (ToolbarTheme2) findViewById(R.id.titlebar);
    }

    @Override
    protected void initVariable() {
        CircleDetailActivityData data = AbsActivityLauncher.parseData(getIntent(), CircleDetailActivityData.class);
        url = data.getUrl();
        circleId = data.getOid();
        title = data.getTitle();
        presenter = new HybridCircleDetailActivityPresenter(this);
    }

    @Override
    protected void initEvent() {
        EventBus.getDefault().register(this);
        titleBar.setTitle(title);
        titleBar.setDefaultOnBackListener(getActivity());
        titleBar.setBackground(R.color.cyy_h9);

        titleBar.setiOperateCallback(new ICallback() {
            @Override
            public void onCallback() {
                if (hasLogin()) {
                    checkUserJoinState();
                } else {
                    presenter.callLogin();
                }
            }
        });

        setSupportActionBar(titleBar);
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.activity_hybrid_circle_detail;
    }

    @Override
    protected String getUrl() {
        return url;
    }

    @Override
    public void onWebViewReceivedTitle(WebView view, String title) {
        if (StringUtil.isEmpty(this.title))
            titleBar.setTitle(title);
    }

    @Override
    @Subscribe
    public void onEventMainThread(AppEvent.LoginSuccessEvent event) {
        presenter.identifyTrigger(event);

        invokeOnEventMainThread(event);
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
    public void jump2ArticlePublishActivity() {
        ArticlePublishActivity.StartConfig config = new ArticlePublishActivity.StartConfig();

        config.setHasSetCircle(true);
        config.setCircleId(circleId);

        ArticlePublishActivity.getLauncher(getActivity())
                .injectData(config)
                .launch();
    }

    /**
     * 检查用户的加入状态
     */
    @Override
    public void checkUserJoinState() {
        CircleJoinStateModel.CheckUserIsJoinedCircleData data = new CircleJoinStateModel.CheckUserIsJoinedCircleData();
        data.setUsername(IVerifyHolder.mLoginInfo.getUsername());
        data.setId(circleId);
        presenter.checkUserIsJoined(data);
    }

    @Override
    public void showJoinCircleAlert(CustomPopupWindow.OnPositiveClickListener listener) {
        CustomDialog dialog = new CustomDialog(this);
        dialog.setContent(R.string.v1000_default_circle_detail_join_circle_remind);
        dialog.setNegativeButton(R.string.v1000_default_circle_detail_join_circle_cancle);
        dialog.setPositiveButton(R.string.v1000_default_circle_detail_join_circle_join);
        dialog.setPositiveClickListener(listener);
        dialog.show();
    }

    @Override
    public void notifyCircleJoined(LhtWebviewClient.OnLoadFinishListener listener) {
        BridgeWebView webView = getBridgeWebView();
        if (webView instanceof PtrBridgeWebView.IRefreshStateHolder) {
            ((PtrBridgeWebView.IRefreshStateHolder) webView).setManualRefreshFlag(true);
            getPTRBase().setRefreshing();
        }
        lhtWebviewClient.addOnLoadFinishListener(listener);
        webView.reload();
    }


    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
    }

    public static class Launcher extends AbsActivityLauncher<CircleDetailActivityData> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, HybridCircleDetailActivity.class);
        }

        @Override
        public AbsActivityLauncher<CircleDetailActivityData> injectData(CircleDetailActivityData data) {
            launchIntent.putExtra(data.getClass().getSimpleName(), JSON.toJSONString(data));
            return this;
        }
    }

    /**
     * Created by chhyu on 2017/5/9.
     */

    public static class CircleDetailActivityData {
        private String url;

        private String oid;

        private String title;

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

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
