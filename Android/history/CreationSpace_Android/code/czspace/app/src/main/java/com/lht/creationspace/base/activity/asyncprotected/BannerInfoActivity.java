package com.lht.creationspace.base.activity.asyncprotected;

import android.webkit.WebView;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.base.MainApplication;
import com.lht.creationspace.R;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.hybrid.HybridGeneralActivity;
import com.lht.creationspace.base.BadgeNumberManager;
import com.lht.creationspace.hybrid.native4js.impl.DownloadImpl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

@Deprecated //暂时不要删除
public class BannerInfoActivity extends HybridGeneralActivity {
//        implements
//        GlobalLifecycleMonitor.ISingletonActivityFriendlyOptimize {

    public static final String KEY_DATA = "_key_url";

    private static final String PAGENAME = "BannerInfoActivity";

    public static final String KEY_HASEXCUTEBADGE = "_key_hasexcutebadge";

    private boolean hasExcuteBadge;


    @Override
    protected String getPageName() {
        return BannerInfoActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return BannerInfoActivity.this;
    }


    @Override
    protected void onLastCallInKeyBackChain() {
        super.onLastCallInKeyBackChain();
        MainApplication.getOurInstance().startHomeIfNecessary();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void initEvent() {
        titleBar.setDefaultOnBackListener(getActivity());
        titleBar.setTitle(R.string.title_activity_banner_info);
        EventBus.getDefault().register(this);
    }


    @Override
    protected void load() {
        super.load();
        hasExcuteBadge = getIntent().getBooleanExtra(KEY_HASEXCUTEBADGE, true);
        //默认都是处理过的，只有从通知栏等进的需要处理一下
        if (!hasExcuteBadge) {
            hasExcuteBadge = true;
            BadgeNumberManager.getInstance().resetVsoActivityNotify();
        }
    }

    @Override
    public String getUrl() {
        return getIntent().getStringExtra(KEY_DATA);
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


//    @Override
//    public void redirectBackStack() {
//        Intent intent = getIntent();
//        if (intent != null) {
//            String s = MainApplication.getOurInstance().getMainStackTopActivityPath();
//            intent.putExtra(UMengActivity.KEY_ORIGINCLASS_SHOULDREDIRECTONFINISH, s);
//            setIntent(intent);
//        } else {
//            DLog.e(getClass(), "intent is null! onRestrictBackStack");
//        }
//    }

//    @Subscribe
//    @Override
//    public void onEventMainThread(GlobalLifecycleMonitor.EventApplicationStartOrResumeFromHome event) {
//        DLog.d(getClass(), "receive app start or resume");
//        redirectBackStack();
//    }

}
