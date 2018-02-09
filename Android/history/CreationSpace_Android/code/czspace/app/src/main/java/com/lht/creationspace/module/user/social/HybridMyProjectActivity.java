package com.lht.creationspace.module.user.social;

import android.content.Context;
import android.webkit.WebView;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.hybrid.HybridGeneralActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.hybrid.IHybridPagesCollection;
import com.lht.creationspace.hybrid.native4js.impl.DownloadImpl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 我的项目
 */
public class HybridMyProjectActivity extends HybridGeneralActivity {

    private static final String PAGENAME = "MyProjectActivity";

    @Override
    protected String getPageName() {
        return HybridMyProjectActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return HybridMyProjectActivity.this;
    }

    @Override
    protected String getUrl() {
        return new IHybridPagesCollection.HybridMyProject().getPageUrl();
    }

    @Override
    protected void initEvent() {
        titleBar.setTitle(getString(R.string.v1000_default_homepage_text_my_project));
        titleBar.setDefaultOnBackListener(this);
        EventBus.getDefault().register(this);
        titleBar.hideTitleBottomDividerLine();

        setSupportActionBar(titleBar);
    }

    @Override
    protected IApiRequestPresenter getApiRequestPresenter() {
        return null;
    }


    @Override
    public void onWebViewReceivedTitle(WebView view, String title) {

    }

    @Override
    @Subscribe
    public void onEventMainThread(AppEvent.LoginSuccessEvent event) {
        invokeOnEventMainThread(event);
    }

    @Override
    @Subscribe
    public void onEventMainThread(DownloadImpl.VsoBridgeDownloadEvent event) {
        invokeOnEventMainThread(event);
    }

    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
    }

    public static final class Launcher extends AbsActivityLauncher<Void> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, HybridMyProjectActivity.class);
        }

        @Override
        public AbsActivityLauncher<Void> injectData(Void data) {
            return Launcher.this;
        }
    }
}
