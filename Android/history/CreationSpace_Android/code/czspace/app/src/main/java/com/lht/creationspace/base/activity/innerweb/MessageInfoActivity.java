package com.lht.creationspace.base.activity.innerweb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.lht.creationspace.base.MainApplication;
import com.lht.creationspace.R;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.UMengActivity;
import com.lht.creationspace.base.BadgeNumberManager;
import com.lht.creationspace.base.GlobalLifecycleMonitor;
import com.lht.creationspace.util.debug.DLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MessageInfoActivity extends InnerWebActivity implements
        GlobalLifecycleMonitor.ISingletonActivityFriendlyOptimize {

    public static final String KEY_DATA = "_key_url";
    private static final String PAGENAME = "MessageInfoActivity";

    /**
     * 正常页面跳转进入的，计数已经处理，如果是收到推送，则没有处理过打开的阅读计数，
     * 要在页面内进行处理
     */
    public static final String KEY_HAS_MARKREADED = "_key_has_markreaded";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        initView();
        initVariable();
        initEvent();
        load();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        super.onNewIntent(intent);
        load();
    }

    @Override
    protected void load() {
        super.load();
        boolean hasMarkRead = getIntent().getBooleanExtra(KEY_HAS_MARKREADED, false);
        if (!hasMarkRead) {
            Log.i("amsg", "read message");
            BadgeNumberManager.getInstance().removeSystemNotify(1);
        }
    }

    @Override
    protected String getPageName() {
        return MessageInfoActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return MessageInfoActivity.this;
    }

    @Override
    protected void initVariable() {

    }

    @Override
    protected WebView provideWebView() {
        return null;
    }

    @Override
    protected ProgressBar provideProgressBar() {
        return null;
    }

    @Override
    protected String getUrl() {
        return getIntent().getStringExtra(KEY_DATA);
    }

    @Override
    protected int getMyTitle() {
        return R.string.title_activity_messageinfo;
    }

    @Override
    public void redirectBackStack() {
        Intent intent = getIntent();
        if (intent != null) {
            String s = MainApplication.getOurInstance().getMainStackTopActivityPath();
            intent.putExtra(UMengActivity.KEY_ORIGINCLASS_SHOULDREDIRECTONFINISH, s);
            setIntent(intent);
        } else {
            DLog.e(getClass(), "intent is null! onRestrictBackStack");
        }
    }

    @Subscribe
    @Override
    public void onEventMainThread(GlobalLifecycleMonitor.EventApplicationStartOrResumeFromHome event) {
        DLog.d(getClass(), "receive app start or resume");
        redirectBackStack();
    }
}
