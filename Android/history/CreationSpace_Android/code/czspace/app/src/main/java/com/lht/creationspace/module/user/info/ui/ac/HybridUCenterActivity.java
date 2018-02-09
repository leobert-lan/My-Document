package com.lht.creationspace.module.user.info.ui.ac;

import android.content.Context;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.hybrid.AbsHybridActivityBase;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.customview.MaskView;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme5;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme5Async;
import com.lht.creationspace.hybrid.native4js.impl.DownloadImpl;
import com.lht.creationspace.module.user.info.UCenterActivityPresenter;
import com.lht.creationspace.module.user.info.ui.IUcenterActivity;
import com.lht.lhtwebviewlib.BridgeWebView;
import com.lht.ptrlib.library.PtrBridgeWebView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class HybridUCenterActivity extends AbsHybridActivityBase
        implements IUcenterActivity {

    private static final String PAGENAME = "HybridUCenterActivity";
    private PtrBridgeWebView ptrBridgeWebView;
    private ProgressBar progressBar;
    private MaskView maskView;
    private String targetUser;
    private String url;

    private UCenterActivityPresenter presenter;

    private ToolbarTheme5Async titlebar;

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
        return R.layout.activity_hybrid_ucenter;
    }

    @Override
    protected String getUrl() {
        return url;
    }

    @Override
    public void onWebViewReceivedTitle(WebView view, String title) {

    }

    @Override
    protected void initView() {
        ptrBridgeWebView = (PtrBridgeWebView) findViewById(R.id.ptr_web_view);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        maskView = (MaskView) findViewById(R.id.mask);
        titlebar = (ToolbarTheme5Async) findViewById(R.id.titlebar);
    }

    @Override
    protected void initVariable() {
        presenter = new UCenterActivityPresenter(this);
        HybridUCenterActivityData data = AbsActivityLauncher.parseData(getIntent(), HybridUCenterActivityData.class);
        url = data.getUrl();
        targetUser = data.getTargetUser();
    }

    @Override
    protected void initEvent() {
        EventBus.getDefault().register(this);

        titlebar.setDefaultOnBackListener(getActivity());
        titlebar.useISurfaceConfig(new ToolbarTheme5.ISurfaceConfig() {
            @Override
            public Config getConfigByState(boolean checked) {
                Config config = new Config();
                config.setEnableDrawable(false);
                config.setEnableText(true);
                if (checked) {
                    config.setText("取消关注");
                } else {
                    config.setText("+关注");
                }
                return config;
            }
        });
        titlebar.setRightOpListener(new ToolbarTheme5.ICbOperateListener() {
            @Override
            public void onStateWillBeTrue() {
                presenter.callSubscribe(targetUser);
            }

            @Override
            public void onStateWillBeFalse() {
                presenter.deSubscribe(targetUser);
            }
        });

        titlebar.setSubscribeEnable(true);//maybe change after query,false for init
        setSupportActionBar(titlebar);

        presenter.querySubscribeState(targetUser, false);
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
        if (UCenterActivityPresenter.LoginTrigger.SubscribeUser.equals(event.getTrigger())) {
            manualSetSubscribeState(false);
        }
    }

    @Override
    protected String getPageName() {
        return HybridUCenterActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return HybridUCenterActivity.this;
    }

    @Subscribe
    @Override
    public void onEventMainThread(DownloadImpl.VsoBridgeDownloadEvent event) {
        invokeOnEventMainThread(event);
    }

    protected IApiRequestPresenter getApiRequestPresenter() {
        return presenter;
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void manualSetSubscribeState(boolean isSubscribed) {
        titlebar.manualSetRightOpStatus(isSubscribed);
    }

    @Override
    public void setSubscribeEnable(boolean enabled) {
        titlebar.setSubscribeEnable(enabled);
    }

    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
    }

    public static class Launcher extends AbsActivityLauncher<HybridUCenterActivityData> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, HybridUCenterActivity.class);
        }

        @Override
        public AbsActivityLauncher<HybridUCenterActivityData> injectData(HybridUCenterActivityData data) {
            launchIntent.putExtra(data.getClass().getSimpleName(), JSON.toJSONString(data));
            return this;
        }
    }

    /**
     * Created by chhyu on 2017/5/9.
     */

    public static class HybridUCenterActivityData {

        private String url;

        private String targetUser;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTargetUser() {
            return targetUser;
        }

        public void setTargetUser(String targetUser) {
            this.targetUser = targetUser;
        }
    }
}
