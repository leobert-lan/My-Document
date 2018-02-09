package com.lht.cloudjob.activity.asyncprotected;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.MainApplication;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.clazz.BadgeNumberManager;
import com.lht.cloudjob.clazz.Exit;
import com.lht.cloudjob.clazz.OnCheckedChangeListenerImpl;
import com.lht.cloudjob.customview.TPSPWCreater;
import com.lht.cloudjob.customview.ThirdPartyShareItemClickListenerImpl;
import com.lht.cloudjob.customview.ThirdPartySharePopWins;
import com.lht.cloudjob.fragment.AvatarBarFragment;
import com.lht.cloudjob.fragment.FgDemand;
import com.lht.cloudjob.fragment.FgIndex;
import com.lht.cloudjob.fragment.FgMessage;
import com.lht.cloudjob.fragment.sidebar.NavigationDrawerFragment;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.interfaces.bars.OnNavigationDrawerItemSelectedListener;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.bean.BasicInfoResBean;
import com.lht.cloudjob.mvp.model.pojo.LoginInfo;
import com.lht.cloudjob.mvp.model.pojo.LoginType;
import com.lht.cloudjob.mvp.model.pojo.NavigationUserInfo;
import com.lht.cloudjob.mvp.presenter.HomeActivityPresenter;
import com.lht.cloudjob.mvp.presenter.SplashActivityPresenter;
import com.lht.cloudjob.mvp.viewinterface.IHomeActivity;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.toast.ToastUtils;
import com.lht.customwidgetlib.text.NumBadge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeActivity extends AsyncProtectedActivity implements CompoundButton.OnCheckedChangeListener,
        OnNavigationDrawerItemSelectedListener, AvatarBarFragment.OnFragmentInteractionListener,
        IHomeActivity, IVerifyHolder {

    public static final String KEY_ISLOGIN = "isLogin";
    public static final String KEY_DATA = "_data_LoginInfo";

    //UI Objects
    private LinearLayout rg_tab_bar;
    private RadioButton rbTabIndex;
    private RadioButton rbTabDemand;
    private RadioButton rbTabMessage;

    //代表页面的常量
    public static final int PAGE_ONE = 0;
    public static final int PAGE_TWO = 1;
    public static final int PAGE_THREE = 2;

    private static final String PAGENAME = "HomeActivity";

    private ProgressBar mProgressBar;

    public FgIndex fgIndex = new FgIndex();

    public FgDemand fgDemand = new FgDemand();

    public FgMessage fgMessage = new FgMessage();

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private DrawerLayout draw;

    private HomeActivityPresenter presenter;

    private NumBadge messageBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main_topdrawer);
        presenter = new HomeActivityPresenter(this);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        doJobsAboutPermissions();
        checkVersionUpdate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BadgeNumberManager.getInstance().notifyChangesToAll();
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
    public UMengActivity getActivity() {
        return HomeActivity.this;
    }

    @Override
    protected void initView() {
        rg_tab_bar = (LinearLayout) findViewById(R.id.rg_tab_bar);
        rbTabIndex = (RadioButton) findViewById(R.id.rb_index);
        rbTabMessage = (RadioButton) findViewById(R.id.rb_message);
        rbTabDemand = (RadioButton) findViewById(R.id.rb_demand);
        divider = findViewById(R.id.div_tab_bar);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_drawer);
        draw = (DrawerLayout) findViewById(R.id.drawer);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        messageBadge = (NumBadge) findViewById(R.id.home_message_badge);

    }

    private Fragment saveFg;

    private View divider;

    protected void switchFragment(final int rid, Fragment to) {
        if (to == null) {
            // may throw you a IllegalArgumentException be better
            return;
        }
        if (saveFg != to) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (!to.isAdded()) {    // 先判断是否被add过
                if (saveFg != null) {
                    ft.hide(saveFg);
                }
                ft.add(rid, to).show(to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                if (saveFg != null) {
                    ft.hide(saveFg);
                }
                ft.show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
            saveFg = to;
        }

    }

    @Override
    protected void initVariable() {
    }

    /**
     * @return true if has login on background
     */
    private boolean isLogin() {
        return getIntent().getBooleanExtra(KEY_ISLOGIN, false);
    }

    @Override
    protected void initEvent() {
        if (isLogin()) {
            String _data = getIntent().getStringExtra(KEY_DATA);
            LoginInfo info = JSON.parseObject(_data, LoginInfo.class);
            AppEvent.LoginSuccessEvent event = new AppEvent.LoginSuccessEvent(info);
            //注意，引导页对应的trigger这里没有用到，若关联到业务，需要对逻辑扩展
            event.setTrigger(SplashActivityPresenter.LoginTrigger.BackgroundLogin);
            EventBus.getDefault().post(event);
        } else {
            mLoginInfo.copy(new LoginInfo());
        }

        ArrayList<RadioButton> tabs = new ArrayList<>();
        tabs.add(rbTabIndex);
        tabs.add(rbTabDemand);
        tabs.add(rbTabMessage);

        initTab(tabs);

        rbTabIndex.performClick();

        mNavigationDrawerFragment.setup(R.id.fragment_drawer,
                (DrawerLayout) findViewById(R.id.drawer), getToolbar());
        mNavigationDrawerFragment.setDrawerLayout(draw);

        fgMessage.updateLoginInfo(mLoginInfo);
        fgDemand.updateLoginInfo(mLoginInfo);
        updateSideBar(mLoginInfo);

        mNavigationDrawerFragment.closeDrawer();
    }

    /**
     * 初始化tab
     */
    private void initTab(ArrayList<RadioButton> tabs) {
        HashMap<RadioButton, CompoundButton.OnCheckedChangeListener> map = new HashMap<>();
        for (RadioButton rb : tabs) {
            map.put(rb, this);
        }
        View.OnClickListener listener = new OnCheckedChangeListenerImpl(map);
        for (RadioButton rb : tabs) {
            rb.setOnClickListener(listener);
        }
    }


    @Override
    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    /**
     * 侧边栏选取回调事件
     *
     * @param position
     */
    @Override
    public boolean onNavigationDrawerItemSelected(LoginType type, int position) {
        DLog.d(getClass(), "select position:" + position);
        return presenter.onDrawerItemSelect(type, position);
    }

    @Override
    public void onSearchClick() {
        start(SearchHistoryActivity.class);
    }

    @Override
    public void onToolbarToggle(boolean isToggle) {
        if (isToggle) {
            mNavigationDrawerFragment.openDrawer();
        } else {
            mNavigationDrawerFragment.closeDrawer();
        }
    }

    @Override
    public String onPreAvatarLoad() {
        return mLoginInfo.getAvatar();
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment != null && mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
        } else if (pw != null) {
            pw.dismiss();
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
            ToastUtils.show(getActivity(), R.string.v1010_toast_press_again_exit, ToastUtils
                    .Duration.s);
            exit.doExitInTwoSecond();
        }
    }

    @Override
    @Subscribe
    public void onEventMainThread(AppEvent.LoginSuccessEvent event) {
        LoginInfo info = event.getLoginInfo();
        mLoginInfo.copy(info);
        updateSideBar(info);

        loadTitleBarAvatar(info.getAvatar());
    }

    @Subscribe
    public void onEventMainThread(AppEvent.UserInfoUpdatedEvent event) {
        BasicInfoResBean bean = event.getBasicInfoResBean();
        if (bean == null) {
            return;
        }
        mLoginInfo.setAvatar(bean.getAvatar());
        mLoginInfo.setNickname(bean.getNickname());
        mLoginInfo.setType(bean.getLoginType());
        mLoginInfo.setBasicUserInfo(bean);
        updateSideBar(mLoginInfo);

        loadTitleBarAvatar(bean.getAvatar());
    }

    private void loadTitleBarAvatar(String avatarUrl) {
        if (fgIndex != null) {
            fgIndex.loadAvatar(avatarUrl);
        }

        if (fgDemand != null) {
            fgDemand.loadAvatar(avatarUrl);
        }

        if (fgMessage != null) {
            fgMessage.loadAvatar(avatarUrl);
        }
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
        int vsoSnNum = event.getNumSystemNotify();
        int vsoAcNum = event.getNumVsoActivityNotify();
        if (vsoSnNum > 0) {
            messageBadge.updateWithFriendlyMode(vsoSnNum, NumBadge.RECOMMEND_MAX);
        } else if (vsoAcNum > 0) {
            messageBadge.updateWithPointMode();
        } else {
            messageBadge.clear();
        }

    }

    private void updateSideBar(LoginInfo info) {
        if (mNavigationDrawerFragment != null) {
            mNavigationDrawerFragment.updateType(info.getType());

            NavigationUserInfo navigationUserInfo = new NavigationUserInfo();

            navigationUserInfo.setAvatarUrl(info.getAvatar());
            // TODO: 2016/8/5 暂无vip信息
//          navigationUserInfo.setVipImgResId(R.drawable.v1010_drawable_icon_lvt);
            navigationUserInfo.setNickname(info.getNickname());
            navigationUserInfo.setUsername(info.getUsername());
            mNavigationDrawerFragment.updateUserInfo(navigationUserInfo);
        }

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
    public void jumpPersonalInfo() {
        start(PersonalInfoActivity.class, PersonalInfoActivity.KEY_DATA, mLoginInfo.getUsername());
    }

    @Override
    public void jumpMineAttention() {
        start(AttentionActivity.class, AttentionActivity.KEY_DATA, mLoginInfo.getUsername());
    }

//    @Override
//@TempVersion(TempVersionEnum.DEPRECATED)
//    public void jumpPersonalAuthenticate() {
//        start(PersonalAuthenticateActivity.class, PersonalAuthenticateActivity.KEY_DATA,
//                mLoginInfo.getUsername());
//    }
//
//    @Override
//    @TempVersion(TempVersionEnum.DEPRECATED)
//    public void jumpEnterpriseAuthenticate() {
//        start(EnterpriseAuthenticateActivity.class, EnterpriseAuthenticateActivity.KEY_DATA, mLoginInfo.getUsername());
//    }

    @Override
    public void jumpSetting() {
        start(SettingActivity.class, SettingActivity.KEY_DATA, JSON.toJSONString(mLoginInfo));
    }

    @Override
    public void showRecomendActionSheet() {
        //TODO
    }

    @Override
    public void showSharePopwins(String content, ThirdPartyShareItemClickListenerImpl
            itemClickListener) {
        ThirdPartySharePopWins wins = TPSPWCreater.create(getActivity());
        wins.setShareContent(content);
        wins.removeItem(wins.getItemCount() - 1);
        wins.setOnThirdPartyShareItemClickListener(itemClickListener);
        wins.show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isChecked) {
            return;
        }
        int checkedId = buttonView.getId();
        switch (checkedId) {
            case R.id.rb_index:
                switchFragment(R.id.fragment, fgIndex);
                break;
            case R.id.rb_demand:
                switchFragment(R.id.fragment, fgDemand);
                break;
            case R.id.rb_message:
                switchFragment(R.id.fragment, fgMessage);
                break;
            default:
                break;
        }
    }
}
