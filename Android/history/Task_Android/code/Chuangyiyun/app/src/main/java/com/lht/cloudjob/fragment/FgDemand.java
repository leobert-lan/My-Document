package com.lht.cloudjob.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.R;
import com.lht.cloudjob.customview.FgDemandTitleBar;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.interfaces.umeng.IUmengEventKey;
import com.lht.cloudjob.mvp.model.pojo.LoginInfo;
import com.lht.cloudjob.util.debug.DLog;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class FgDemand extends AvatarBarFragment implements RadioGroup.OnCheckedChangeListener,
        IVerifyHolder {

    private static final String PAGENAME = "FgDemand";

    public FgDemand() {
    }

    private FgDemandTitleBar titleBar;

    private FgDemandCollected fgDemandCollected = new FgDemandCollected();
    private FgDemandOrdered fgDemandOrdered = new FgDemandOrdered();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_demand, container, false);

        titleBar = (FgDemandTitleBar) view.findViewById(R.id.titlebar);

        //setup the listener of toggle
        titleBar.setOnToggleListener(this);

        //setup the listener of 'tab'
        titleBar.setOnCheckedChangeListener(this);

        //setup the listener of de-/selectAll
        titleBar.setSelectOnToggleListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //全选
                if (isChecked) {
                    fgDemandCollected.selectedAll();
                } else {
                    fgDemandCollected.deSelectedAll();
                }
            }
        });

        String path = getFragmentInteractionListener().onPreAvatarLoad();
        loadAvatar(path);

        initSubFragment();

        return view;
    }

    /**
     * 初始化子fragment
     */
    private void initSubFragment() {
        fgDemandCollected.updateLoginInfo(mLoginInfo);
        fgDemandOrdered.updateLoginInfo(mLoginInfo);
        switchFragment(R.id.fgdemand_subfg, fgDemandOrdered);
    }

    @Override
    protected ImageView getAvatarView() {
        if (titleBar == null)
            return null;
        return titleBar.getAvatarView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void initView(View contentView) {

    }

    @Override
    protected void initVariable() {

    }

    @Override
    protected void initEvent() {

    }

    @Subscribe
    public void onEventMainThread(FgDemandTitleBar.CloseModifyModeEvent event) {
        titleBar.closeModifyMode();
    }

    @Subscribe
    public void onEventMainThread(FgDemandTitleBar.TryEnableRightOperateEvent event) {
        // TODO: 2016/11/24
        changeNavigationByLoginState();
        changeNavigationByCollections();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected String getPageName() {
        return PAGENAME;
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.fgdemand_rb_ordered:
                switchFragment(R.id.fgdemand_subfg, fgDemandOrdered);
                break;
            case R.id.fgdemand_rb_collected:
                //统计 切换tab查看收藏的任务 -计数
                MobclickAgent.onEvent(getActivity(), IUmengEventKey.KEY_VIEW_TASK_FAVO);

                switchFragment(R.id.fgdemand_subfg, fgDemandCollected);
                titleBar.setOpOnToggleListener(fgDemandCollected);
                changeNavigationByLoginState();
                changeNavigationByCollections();
                break;

            default:
                break;
        }
    }

    private void changeNavigationByLoginState() {
        if (!mLoginInfo.isLogin()) {
            titleBar.disableRightOperation();
        }
    }

    private void changeNavigationByCollections() {
        if (fgDemandCollected.hasCollections()) {
            titleBar.enableRightOperation();
        } else {
            titleBar.disableRightOperation();
        }
    }

    public void updateLoginInfo(LoginInfo loginInfo) {
        mLoginInfo.copy(loginInfo);
        if (fgDemandCollected != null) {
            fgDemandCollected.updateLoginInfo(mLoginInfo);
        }

        if (fgDemandOrdered != null) {
            fgDemandOrdered.updateLoginInfo(mLoginInfo);
        }

    }

    /**
     * desc: 主线程回调登录成功
     * 需要处理：成员对象的更新、界面的更新
     *
     * @param event 登录成功事件，包含信息
     */
    @Override
    @Subscribe
    public void onEventMainThread(AppEvent.LoginSuccessEvent event) {
        mLoginInfo.copy(event.getLoginInfo());
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

    @Override
    public LoginInfo getLoginInfo() {
        return mLoginInfo;
    }
}
