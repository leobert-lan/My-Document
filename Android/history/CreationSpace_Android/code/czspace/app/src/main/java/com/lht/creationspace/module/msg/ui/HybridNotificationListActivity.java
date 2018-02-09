package com.lht.creationspace.module.msg.ui;

import android.content.Context;
import android.webkit.WebView;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.hybrid.HybridGeneralActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.hybrid.IHybridPagesCollection;
import com.lht.creationspace.hybrid.native4js.impl.DownloadImpl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class HybridNotificationListActivity extends HybridGeneralActivity {
    private static final String PAGENAME = "HybridNotificationListActivity";

    @Override
    protected void initEvent() {
        titleBar.setTitle("通知");
        EventBus.getDefault().register(this);
        titleBar.setDefaultOnBackListener(this);

        setSupportActionBar(titleBar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected String getUrl() {
        return new IHybridPagesCollection.HybridNotificationList()
                .getPageUrlWithQueryString(IVerifyHolder.mLoginInfo.getUsername());
    }

    @Override
    public void onWebViewReceivedTitle(WebView view, String title) {

    }

    @Subscribe
    @Override
    public void onEventMainThread(AppEvent.LoginSuccessEvent event) {
        invokeOnEventMainThread(event);
    }

    @Subscribe
    @Override
    public void onEventMainThread(DownloadImpl.VsoBridgeDownloadEvent event) {
        invokeOnEventMainThread(event);
    }

    @Override
    protected String getPageName() {
        return HybridNotificationListActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return HybridNotificationListActivity.this;
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
            return new LhtActivityLauncherIntent(context, HybridNotificationListActivity.class);
        }

        @Override
        public AbsActivityLauncher<Void> injectData(Void data) {
            return Launcher.this;
        }
    }
}
