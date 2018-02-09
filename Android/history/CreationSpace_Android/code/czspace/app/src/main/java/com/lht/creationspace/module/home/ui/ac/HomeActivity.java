package com.lht.creationspace.module.home.ui.ac;

import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.BadgeNumberManager;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.MainApplication;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.fragment.hybrid.AbsHybridFragmentActivity;
import com.lht.creationspace.base.fragment.hybrid.AbsHybridFragmentBase;
import com.lht.creationspace.base.keyback.Exit;
import com.lht.creationspace.base.model.pojo.LoginInfo;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.customview.popup.IPopupHolder;
import com.lht.creationspace.customview.tab.TabManager;
import com.lht.creationspace.module.home.HomeActivityPresenter;
import com.lht.creationspace.module.home.ui.IHomeActivity;
import com.lht.creationspace.module.home.ui.fg.FgHomeIndex;
import com.lht.creationspace.module.home.ui.fg.FgHomeMine;
import com.lht.creationspace.module.home.ui.fg.FgHomeProject;
import com.lht.creationspace.module.home.ui.fg.FgHomeVsoActivity;
import com.lht.creationspace.module.topic.ui.FgHomeTopic;
import com.lht.creationspace.module.user.info.model.pojo.BasicInfoResBean;
import com.lht.creationspace.util.toast.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import individual.leobert.uilib.numbadge.NumBadge;

public class HomeActivity extends AbsHybridFragmentActivity
        implements TabManager.OnTabSelectedListener,
        IHomeActivity, IVerifyHolder, IPopupHolder {

    //UI Objects
    private LinearLayout rg_tab_bar;
    /**
     * 首页
     */
    private RadioButton rbTabIndex;
    /**
     * 项目
     */
    private RadioButton rbTabProject;

    /**
     * 圈子
     */
    private RadioButton rbTabTopic;

    /**
     * 活动
     */
    private RadioButton rbTabActivity;

    /**
     * 我的
     */
    private RadioButton rbTabMine;

    private static final String PAGENAME = "HomeActivity";

    private ProgressBar mProgressBar;

    private HomeActivityPresenter presenter;

    private NumBadge messageBadge;

    //    private AbsHybridFragmentBase fgIndex;

    private FgHomeIndex fgHomeIndex;
    //    private FgHomeIndexScrollType fgHomeIndex;  无法优雅的处理滑动冲突
    private FgHomeTopic fgHomeTopic;
    private FgHomeProject fgHomeProject;
    private FgHomeVsoActivity fgHomeVsoActivities;
    private FgHomeMine fgHomeMine;

    private AbsHybridFragmentActivity parent;

    @Override
    public ProgressBar getPageProtectPbar() {
        return mProgressBar;
    }

    @Override
    protected boolean needTransparentStatusBar() {
        return true;
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.activity_main;
    }

    private boolean flagRestart = false;

    @Override
    protected void onRestart() {
        super.onRestart();
        flagRestart = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!flagRestart) { //stop后恢复的不重新处理权限问题
            doJobsAboutPermissions();
        }
        flagRestart = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        BadgeNumberManager.getInstance().notifyChangesToAll();
        //查询是否有未读消息
        presenter.queryUnreadMsg();
    }

    /**
     * 检查版本更新
     */
    private void checkVersionUpdate() {
        presenter.checkVersionUpdate();
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        presenter.onFinish();
        super.onDestroy();
    }

    @Override
    protected IApiRequestPresenter getApiRequestPresenter() {
        return presenter;
    }

    @Override
    protected String getPageName() {
        return HomeActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return HomeActivity.this;
    }

    @Override
    public void jump2ArticlePublishActivity() {
        presenter.jump2ArticlePublishActivity();
    }

    @Override
    public void jump2ProjectPublishActivity() {
        presenter.jump2ProjectPublishActivity();
    }

    @Override
    public void updateTabMineNotifyBadge(boolean needNotify) {
        if (needNotify) {
            messageBadge.updateWithPointMode();
        } else {
            messageBadge.clear();
        }
    }

    @Override
    protected void initView() {
        parent = (AbsHybridFragmentActivity) getActivity();
        rg_tab_bar = (LinearLayout) findViewById(R.id.rg_tab_bar);
        rbTabIndex = (RadioButton) findViewById(R.id.rb_index);
        rbTabProject = (RadioButton) findViewById(R.id.rb_project);
        rbTabTopic = (RadioButton) findViewById(R.id.rb_topic);
        rbTabActivity = (RadioButton) findViewById(R.id.rb_activity);
        rbTabMine = (RadioButton) findViewById(R.id.rb_mine);
        divider = findViewById(R.id.div_tab_bar);

        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        messageBadge = (NumBadge) findViewById(R.id.home_message_badge);

//        fgIndex = AbsHybridFragmentBase.newInstance(FgHomeIndexRecommend.class, this);

        fgHomeMine = new FgHomeMine();
        fgHomeIndex = new FgHomeIndex();

        fgHomeVsoActivities = AbsHybridFragmentBase.newInstance(FgHomeVsoActivity.class, parent);
        fgHomeTopic = AbsHybridFragmentBase.newInstance(FgHomeTopic.class, parent);
        fgHomeProject = AbsHybridFragmentBase.newInstance(FgHomeProject.class, parent);

    }

    @Override
    protected void initVariable() {
        presenter = new HomeActivityPresenter(this);
    }

    @Override
    protected void initEvent() {
        EventBus.getDefault().register(this);
//        if (isLogin()) {
//            String _data = getIntent().getStringExtra(KEY_DATA);
//            LoginInfo info = JSON.parseObject(_data, LoginInfo.class);
//            AppEvent.LoginSuccessEvent event = new AppEvent.LoginSuccessEvent(info);
//            //注意，引导页对应的trigger这里没有用到，若关联到业务，需要对逻辑扩展
//            event.setTrigger(SplashActivityPresenter.LoginTrigger.BackgroundLogin);
//            EventBus.getDefault().post(event);
//        } else {
//            mLoginInfo.copy(new LoginInfo());
//        }
        TabManager.init(this, rbTabIndex, rbTabProject, rbTabTopic, rbTabActivity, rbTabMine);
        rbTabIndex.performClick();
        checkVersionUpdate();
    }


    private View divider;


    @Override
    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    @Override
    public void onBackPressed() {
//        if (mNavigationDrawerFragment != null && mNavigationDrawerFragment.isDrawerOpen()) {
//            mNavigationDrawerFragment.closeDrawer();
//        } else

        if (isPopupShowing()) {
            closePopupWindow();
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            pressAgainExit();
        } else {
            pressAgainExit();
        }
    }

    private Exit exit = new Exit();

    /**
     * 再次点击返回退出
     */
    private void pressAgainExit() {
        if (exit.isExit()) {
            MainApplication.getOurInstance().finishAll();
        } else {
            ToastUtils.show(getActivity(), R.string.v1000_toast_press_again_exit, ToastUtils
                    .Duration.s);
            exit.doExitInTwoSecond();
        }
    }

    @Override
    @Subscribe(sticky = true)
    public void onEventMainThread(AppEvent.LoginSuccessEvent event) {
        LoginInfo info = event.getLoginInfo();
        mLoginInfo.copy(info);
        presenter.identifyTrigger(event.getTrigger());
    }

    @Subscribe
    public void onEventMainThread(AppEvent.UserInfoUpdatedEvent event) {
        BasicInfoResBean bean = event.getBasicInfoResBean();
        if (bean == null) {
            return;
        }
        mLoginInfo.setAvatar(bean.getAvatar());
        mLoginInfo.setNickname(bean.getNickname());

        mLoginInfo.getLoginResBean().setAvatar(bean.getAvatar());
        mLoginInfo.getLoginResBean().setNickname(bean.getNickname());
//        mLoginInfo.setBasicUserInfo(bean);
    }

    /**
     * desc: 未进行登录的事件订阅
     *
     * @param event 手动关闭登录页事件
     */
    @Override
    @Subscribe
    public void onEventMainThread(AppEvent.LoginCancelEvent event) {
        //empty
    }

    @Subscribe
    public void onEventMainThread(BadgeNumberManager.BadgeNumberChangedEvent event) {
        // TODO: 2016/12/15 xiugai角标数量
//        int vsoSnNum = event.getNumSystemNotify();
//        int vsoAcNum = event.getNumVsoActivityNotify();
//        Log.d("lmsg", "badgenumber:" + JSON.toJSONString(event));
//        if (vsoSnNum > 0) {
//            messageBadge.updateWithFriendlyMode(vsoSnNum, NumBadge.RECOMMEND_MAX);
//        } else if (vsoAcNum > 0) {
//            messageBadge.updateWithPointMode();
//        } else {
//            messageBadge.clear();
//        }

    }


    @Subscribe
    public void onEventMainThread(AppEvent.HomeTabDisplayEvent event) {
        if (event.isShown()) {
            rg_tab_bar.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);
        } else {
            rg_tab_bar.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }

    }

    @Override
    public LoginInfo getLoginInfo() {
        return mLoginInfo;
    }


    @Override
    public void onTabSelect(CompoundButton selectedTab) {
        int checkedId = selectedTab.getId();
        switch (checkedId) {
            case R.id.rb_index:
                switchFragment(R.id.fragment, fgHomeIndex);
                break;
            case R.id.rb_project:
                switchFragment(R.id.fragment, fgHomeProject);
                break;
            case R.id.rb_topic:
                switchFragment(R.id.fragment, fgHomeTopic);
                break;
            case R.id.rb_activity:
                switchFragment(R.id.fragment, fgHomeVsoActivities);
                break;
            case R.id.rb_mine:
                switchFragment(R.id.fragment, fgHomeMine);
                break;

            default:
                break;
        }
    }

    public void callPublishProject() {
        presenter.callPublishProject();
    }

    public void callPublishArticle() {
        presenter.callPublishArticle();
    }
}
