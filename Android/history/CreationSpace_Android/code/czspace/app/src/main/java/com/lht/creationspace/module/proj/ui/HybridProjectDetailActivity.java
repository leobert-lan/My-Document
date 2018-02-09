package com.lht.creationspace.module.proj.ui;

import android.content.Context;
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
import com.lht.creationspace.hybrid.native4js.impl.DownloadImpl;
import com.lht.creationspace.hybrid.web4native.WebBridgeCaller;
import com.lht.creationspace.hybrid.web4native.global.QueryPageShareData;
import com.lht.creationspace.listener.ICallback;
import com.lht.creationspace.module.proj.presenter.ProjectDetailActivityPresenter;
import com.lht.lhtwebviewlib.BridgeWebView;
import com.lht.ptrlib.library.PtrBridgeWebView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 项目详情
 */
public class HybridProjectDetailActivity extends AbsHybridActivityBase
        implements IProjectDetailActivity {
    private static final String PAGENAME = "HybridProjectDetailActivity";
    private PtrBridgeWebView ptrBridgeWebView;
    private ProgressBar progressBar;
    private MaskView maskView;

    private ToolbarTheme3Async titleBar;

    private LaunchData startData;

    private ProjectDetailActivityPresenter presenter;

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
        return R.layout.activity_hybrid_project_detail;
    }

    @Override
    protected String getUrl() {
        return startData.getUrl();
    }

    @Override
    public void onWebViewReceivedTitle(WebView view, String title) {

    }

    @Override
    protected void initView() {
        ptrBridgeWebView = (PtrBridgeWebView) findViewById(R.id.ptr_web_view);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        maskView = (MaskView) findViewById(R.id.mask);

        titleBar = (ToolbarTheme3Async) findViewById(R.id.titlebar);
    }

    @Override
    protected void initVariable() {
        presenter = new ProjectDetailActivityPresenter(this);
        startData = AbsActivityLauncher.parseData(getIntent(),
                LaunchData.class);
    }

    @Override
    protected void initEvent() {
        EventBus.getDefault().register(this);
        titleBar.setTitle("项目详情");
        setSupportActionBar(titleBar);
        titleBar.setDefaultOnBackListener(getActivity());
        presenter.queryCollectState(startData.getOid(), false);

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
                presenter.callCollect(startData.getOid());
            }

            @Override
            public void onStateWillBeFalse() {
                presenter.disCollect(startData.getOid());
            }
        });
    }

    @Subscribe
    @Override
    public void onEventMainThread(AppEvent.LoginSuccessEvent event) {
        invokeOnEventMainThread(event);
        presenter.handleLoginSuccessEvent(event);
    }

    @Subscribe
    public void onEventMainThread(AppEvent.LoginCancelEvent event) {
        //登录取消的情况下需要回滚UI状态
        if (ProjectDetailActivityPresenter.LoginTrigger.CollectProject.equals(event.getTrigger())) {
            manualSetCollectState(false);
        }
    }

    @Override
    protected String getPageName() {
        return HybridProjectDetailActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return HybridProjectDetailActivity.this;
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

    @Override
    public void manualSetCollectState(boolean isCollected) {
        titleBar.manualSetRightOpStatus(isCollected);
    }

    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
    }

    public static class Launcher extends AbsActivityLauncher<LaunchData> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, HybridProjectDetailActivity.class);
        }

        @Override
        public AbsActivityLauncher<LaunchData> injectData(LaunchData data) {
            launchIntent.putExtra(data.getClass().getSimpleName(), JSON.toJSONString(data));
            return this;
        }
    }

    /**
     * Created by chhyu on 2017/5/9.
     */

    public static class LaunchData {

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
