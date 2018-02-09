package com.lht.creationspace.module.projchapter.ui;

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
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme2;
import com.lht.creationspace.hybrid.native4js.impl.DownloadImpl;
import com.lht.creationspace.hybrid.web4native.WebBridgeCaller;
import com.lht.creationspace.hybrid.web4native.global.QueryPageShareData;
import com.lht.creationspace.listener.ICallback;
import com.lht.creationspace.module.proj.presenter.ProjectDetailActivityPresenter;
import com.lht.creationspace.module.proj.ui.IProjectDetailActivity;
import com.lht.lhtwebviewlib.BridgeWebView;
import com.lht.ptrlib.library.PtrBridgeWebView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 项目更新详情
 */
public class HybridProjChapterDetailActivity extends AbsHybridActivityBase implements IProjectDetailActivity {

    private static final String PAGENAME = "HybridProjChapterDetailActivity";
    private PtrBridgeWebView ptrBridgeWebView;
    private ProgressBar progressBar;
    private MaskView maskView;

    private ToolbarTheme2 titleBar;

    private LaunchData startData;

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
        return R.layout.activity_hybrid_proj_chapter_detail;
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

        titleBar = (ToolbarTheme2) findViewById(R.id.titlebar);
        titleBar.setRightImageResource(R.drawable.v1000_drawable_fenxh);
    }

    @Override
    protected void initVariable() {
        startData = AbsActivityLauncher.parseData(getIntent(), LaunchData.class);
    }

    @Override
    protected void initEvent() {
        EventBus.getDefault().register(this);
        titleBar.setTitle("");
        setSupportActionBar(titleBar);
        titleBar.setDefaultOnBackListener(getActivity());

        titleBar.setiOperateCallback(new ICallback() {
            @Override
            public void onCallback() {
                WebBridgeCaller.with(getBridgeWebView())
                        .call(new QueryPageShareData(getActivity()));
            }
        });
    }

    @Subscribe
    @Override
    public void onEventMainThread(AppEvent.LoginSuccessEvent event) {
        invokeOnEventMainThread(event);
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
        return HybridProjChapterDetailActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return HybridProjChapterDetailActivity.this;
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


    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
    }

    @Override
    public void manualSetCollectState(boolean isCollected) {

    }

    public static class Launcher extends AbsActivityLauncher<LaunchData> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, HybridProjChapterDetailActivity.class);
        }

        @Override
        public AbsActivityLauncher<LaunchData> injectData(LaunchData data) {
            launchIntent.putExtra(data.getClass().getSimpleName(), JSON.toJSONString(data));
            return this;
        }
    }

    //temp
    public static class LaunchData {

        private String url;

        private String title;

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

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
