package com.lht.creationspace.module.msg.ui;

import android.content.Context;
import android.webkit.WebView;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.hybrid.HybridGeneralActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.hybrid.IHybridPagesCollection;
import com.lht.creationspace.hybrid.native4js.impl.DownloadImpl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 提到我的，即：对我文章、项目、评价的评价
 */
public class HybridMentionMeListActivity extends HybridGeneralActivity {
    private static final String PAGENAME = "HybridMentionMeListActivity";


    @Override
    protected void initEvent() {
        titleBar.setTitle("评论");
        titleBar.setTitleTextColor(R.color.black);
        titleBar.setDefaultOnBackListener(this);

        setSupportActionBar(titleBar);

        EventBus.getDefault().register(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected String getUrl() {
        return new IHybridPagesCollection.HybridMentionMeList()
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
        return HybridMentionMeListActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return HybridMentionMeListActivity.this;
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
            return new LhtActivityLauncherIntent(context, HybridMentionMeListActivity.class);
        }

        @Override
        public AbsActivityLauncher<Void> injectData(Void data) {
            return Launcher.this;
        }
    }

}
