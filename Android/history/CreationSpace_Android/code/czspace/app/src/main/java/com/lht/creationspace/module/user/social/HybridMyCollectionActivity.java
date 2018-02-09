package com.lht.creationspace.module.user.social;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import com.lht.creationspace.R;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.fragment.hybrid.AbsHybridFragmentActivity;
import com.lht.creationspace.base.fragment.hybrid.AbsHybridFragmentBase;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.customview.tab.TabManager;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme5;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme5Sync;
import com.lht.creationspace.hybrid.native4js.impl.UcenterBatchOpCloseImpl;
import com.lht.creationspace.module.home.ui.IHybridBatchOpFragment;
import com.lht.creationspace.module.home.ui.fg.FgHomeIndexRecommend;
import com.lht.creationspace.util.debug.DLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Set;

/**
 * 我的收藏
 */
public class HybridMyCollectionActivity extends AbsHybridFragmentActivity implements
        TabManager.OnTabSelectedListener, UcenterBatchOpCloseImpl.OnUCenterBatchCloseListener {

    private static final String PAGENAME = "MyCollectionActivity";
    private RadioButton rbProject;
    private RadioButton rbArticle;
    private View indicator1;
    private View indicator2;
    private FgMyCollectedArticle fgArticle;
    private FgMyCollectedProject fgProject;
    private ToolbarTheme5Sync titleBar;
    private ProgressBar progressBar;
    private AbsHybridFragmentActivity parent;

    @Override
    public ProgressBar getPageProtectPbar() {
        return progressBar;
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.activity_my_collection;
    }

    @Override
    protected String getPageName() {
        return HybridMyCollectionActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return HybridMyCollectionActivity.this;
    }

    @Override
    protected void initView() {
        parent = (AbsHybridFragmentActivity) getActivity();

        titleBar = (ToolbarTheme5Sync) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        rbProject = (RadioButton) findViewById(R.id.rb_project);
        rbArticle = (RadioButton) findViewById(R.id.rb_article);
        indicator1 = findViewById(R.id.mycollection_indicator1);
        indicator2 = findViewById(R.id.mycollection_indicator2);
        AbsHybridFragmentBase.newInstance(FgHomeIndexRecommend.class, parent);
        fgProject = AbsHybridFragmentBase.newInstance(FgMyCollectedProject.class, parent);
        fgArticle = AbsHybridFragmentBase.newInstance(FgMyCollectedArticle.class, parent);
    }

    HashMap<RadioButton, View> hashMap = new HashMap<>();

    @Override
    protected void initVariable() {
        hashMap.put(rbProject, indicator1);
        hashMap.put(rbArticle, indicator2);
    }

    @Override
    protected void initEvent() {
        EventBus.getDefault().register(this);
        titleBar.setTitle(getString(R.string.v1000_default_homepage_text_my_collection));
        titleBar.setDefaultOnBackListener(this);
        titleBar.hideTitleBottomDividerLine();
        TabManager.init(this, rbProject, rbArticle);

        titleBar.useISurfaceConfig(new ToolbarTheme5.ISurfaceConfig() {
            @Override
            public Config getConfigByState(boolean checked) {
                Config config = new Config();
                if (checked) {
                    config.setEnableText(true);
                    config.setText("完成");
                    config.setEnableDrawable(false);
                } else {
                    config.setEnableText(false);
                    config.setDrawableRes(R.drawable.v1000_drawable_grzybj);
                    config.setEnableDrawable(true);
                }
                return config;
            }
        });

        titleBar.setRightOpListener(new ToolbarTheme5.ICbOperateListener() {
            @Override
            public void onStateWillBeTrue() {
                IHybridBatchOpFragment fragment = getCurrentHybridBatchOpFragment();
                if (fragment == null) return;
                fragment.openBatchOpState();
            }

            @Override
            public void onStateWillBeFalse() {
                IHybridBatchOpFragment fragment = getCurrentHybridBatchOpFragment();
                if (fragment == null) return;
                fragment.closeBatchOpState();
            }
        });

        setSupportActionBar(titleBar);

        rbProject.performClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private IHybridBatchOpFragment getCurrentHybridBatchOpFragment() {
        Fragment currentFg = getCurrentChildFragment();
        if (currentFg instanceof IHybridBatchOpFragment) {
            return (IHybridBatchOpFragment) currentFg;
        } else {
            DLog.e(getClass(), "fragment需要实现IHybridBatchOpFragment，且当前的saveFg不应当为null");
            return null;
        }
    }


    @Override
    protected IApiRequestPresenter getApiRequestPresenter() {
        return null;
    }

    @Override
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    public void onTabSelect(CompoundButton selectedTab) {
        int id = selectedTab.getId();
        refreshTab(id);
        switch (selectedTab.getId()) {
            case R.id.rb_project:
                switchFragment(R.id.childfragment, fgProject);
                //检查状态，并做相应处理
                if (titleBar.getCheckedState()) {
                    fgArticle.closeBatchOpState();
                }
                titleBar.manualSetRightOpStatus(false);
                break;
            case R.id.rb_article:
                switchFragment(R.id.childfragment, fgArticle);
                if (titleBar.getCheckedState()) {
                    fgProject.closeBatchOpState();
                }
                titleBar.manualSetRightOpStatus(false);
                break;
        }
    }

    private void refreshTab(int selectedId) {
        if (hashMap == null || hashMap.isEmpty()) {
            return;
        }
        Set<RadioButton> keys = hashMap.keySet();
        for (RadioButton radioButton : keys) {
            if (radioButton.getId() == selectedId) {
                //字体效果，线
                radioButton.getPaint().setFakeBoldText(true);
                hashMap.get(radioButton).setVisibility(View.VISIBLE);
            } else {
                //字体效果，线
                radioButton.getPaint().setFakeBoldText(false);
                hashMap.get(radioButton).setVisibility(View.INVISIBLE);
            }
        }
    }


    /**
     * 编辑完成，恢复原样
     */
    @Override
    public void onUCenterBatchClose() {
        titleBar.manualSetRightOpStatus(false);
    }

    @Subscribe
    public void onEventMainThread(NotifyUCenterBatchCloseEvent event) {
        titleBar.manualSetRightOpStatus(false);
    }

    public static class NotifyUCenterBatchCloseEvent {
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
            return new LhtActivityLauncherIntent(context, HybridMyCollectionActivity.class);
        }

        @Override
        public AbsActivityLauncher<Void> injectData(Void data) {
            return Launcher.this;
        }
    }
}
