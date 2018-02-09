package com.lht.cloudjob.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.asyncprotected.SysMsgListActivity;
import com.lht.cloudjob.activity.asyncprotected.VsoAcListActivity;
import com.lht.cloudjob.clazz.BadgeNumberManager;
import com.lht.cloudjob.customview.FgMessageTitleBar;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.mvp.model.bean.JpushMessage;
import com.lht.cloudjob.mvp.model.bean.UnreadMsgResBean;
import com.lht.cloudjob.mvp.model.bean.VsoActivitiesResBean;
import com.lht.cloudjob.mvp.model.pojo.LoginInfo;
import com.lht.cloudjob.mvp.presenter.MessageFragmentPresenter;
import com.lht.cloudjob.mvp.viewinterface.IMessageFragment;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.string.StringUtil;
import com.lht.cloudjob.util.time.TimeUtil;
import com.lht.customwidgetlib.text.NumBadge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class FgMessage extends AvatarBarFragment implements View.OnClickListener,
        IVerifyHolder, IMessageFragment {

    private static final String PAGENAME = "FgMessage";

    private FgMessageTitleBar titleBar;

    private NumBadge sysMsgBadge;

    private NumBadge vsoAcMsgBadge;

    private TextView tvSysSummary;

    private TextView tvVsoAcSummary;

    private TextView tvSysTime;

    private TextView tvVsoAcTime;

    private MessageFragmentPresenter presenter;


    public FgMessage() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_message, container, false);

        initView(view);
        initVariable();
        initEvent();

        return view;
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

        //刷新精彩活动
        if (presenter != null) {
            refreshActivitiesMsg();
        }

        if (presenter != null && isLogin()) {
            refreshData();
        }
    }

    //刷新精彩活动，不刷新消息
    private void refreshActivitiesMsg() {
        presenter.callGetVsoActivities();
    }

    private void refreshData() {
        presenter.callGetMessage(mLoginInfo.getUsername());
    }

    private boolean isLogin() {
        return !StringUtil.isEmpty(mLoginInfo.getUsername());
    }

    @Override
    protected void initView(View view) {
        titleBar = (FgMessageTitleBar) view.findViewById(R.id.titlebar);
        view.findViewById(R.id.row1).setOnClickListener(this);
        view.findViewById(R.id.row2).setOnClickListener(this);
        vsoAcMsgBadge = (NumBadge) view.findViewById(R.id.message_numbadge_appmsg);
        sysMsgBadge = (NumBadge) view.findViewById(R.id.message_numbadge_sysmsg);
        tvSysSummary = (TextView) view.findViewById(R.id.message_tv_syssummary);
        tvVsoAcTime = (TextView) view.findViewById(R.id.message_tv_vsoactime);
        tvVsoAcSummary = (TextView) view.findViewById(R.id.message_tv_vsoacsummary);
        tvSysTime = (TextView) view.findViewById(R.id.message_tv_systime);
    }

    @Override
    protected void initVariable() {
        presenter = new MessageFragmentPresenter(this);
    }

    @Override
    protected void initEvent() {
        titleBar.setOnToggleListener(this);
        String path = getFragmentInteractionListener().onPreAvatarLoad();
        loadAvatar(path);
        BadgeNumberManager.getInstance().notifyChangesToAll();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        presenter.cancelRequestOnFinish(getActivity());
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
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.row1:
                boolean b = checkLoginState();
                if (b) {
                    //已经登录，跳转到消息列表
                    intent = new Intent(getActivity(), SysMsgListActivity.class);
                    intent.putExtra(SysMsgListActivity.KEY_DATA, mLoginInfo.getUsername());
                    startActivity(intent);
                } else {
                    presenter.callLogin();
                }
                break;
            case R.id.row2:
                BadgeNumberManager.getInstance().resetVsoActivityNotify();
                //跳转到活动列表
                intent = new Intent(getActivity(), VsoAcListActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private boolean checkLoginState() {
        if (isLogin()) {
            refreshData();
            return true;
        }
        return false;
    }

    public void updateLoginInfo(LoginInfo loginInfo) {
        this.mLoginInfo.copy(loginInfo);
        if (presenter != null) {
            presenter.setLoginStatus(!StringUtil.isEmpty(mLoginInfo.getUsername()));
        }
//        checkLoginState();
    }

    @Override
    @Subscribe
    public void onEventMainThread(AppEvent.LoginSuccessEvent event) {
        LoginInfo info = event.getLoginInfo();
        updateLoginInfo(info);
        presenter.identifyTrigger(event.getTrigger());
    }

    @Subscribe
    public void onEventMainThread(BadgeNumberManager.BadgeNumberChangedEvent event) {
        if (event == null) {
            return;
        }
        int vsoAcNum = event.getNumVsoActivityNotify();
        int sysNum = event.getNumSystemNotify();

        switch (event.getChangeType()) {
            case VsoAcNum:
                updateAllBadgeInThisFragment(sysNum, vsoAcNum);
                JpushMessage.MessageExtra extra = event.getChangeType().getExtra();
                tvVsoAcSummary.setText(extra.getContent());
                tvVsoAcTime.setText(TimeUtil.getTime(extra.getSend_time(), format));
                break;
            case VsoSysNum:
                updateAllBadgeInThisFragment(sysNum, vsoAcNum);
                if (presenter != null && isLogin()) {
                    refreshData();
                }
                break;
            case VsoSysNumReset:
                // 只更新badge
                updateAllBadgeInThisFragment(sysNum, vsoAcNum);
                break;
            case Unknown:
                // 只更新badge
                updateAllBadgeInThisFragment(sysNum, vsoAcNum);
                break;
            case VsoAcNumReset:
                updateAllBadgeInThisFragment(sysNum, vsoAcNum);
                break;
        }
    }

    private void updateAllBadgeInThisFragment(int sysNum, int acNum) {
        updateSysBadge(sysNum);
        updateVsoAcBadge(acNum);
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

    @Override
    public void updateMessage(UnreadMsgResBean bean) {
        if (bean == null)
            return;
        //只更新站内信形式的系统消息
        if (bean.getSystem() != null) {
            updateSysMsg(bean.getSystem());
        }
    }


    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE);

    /**
     * 更新活动第一条信息
     *
     * @param bean
     */
    @Override
    public void updateVsoActivitiesMsg(VsoActivitiesResBean bean) {
        DLog.i(getClass(), "update vsoActivity:" + JSON.toJSONString(bean));
        if (bean == null) {
            tvVsoAcSummary.setText(null);
            tvVsoAcTime.setText(null);
            return;
        }
        tvVsoAcSummary.setText(bean.getContent());
        tvVsoAcTime.setText(TimeUtil.getTime(bean.getSend_time(), format));
    }


    private void updateSysMsg(UnreadMsgResBean.MsgGroup system) {
        if (system == null) {
            system = new UnreadMsgResBean.MsgGroup();
        }
        BadgeNumberManager.getInstance().resetSystemNotifyCount(system.getCount());
        updateSysBadge(system);
        showSummary(tvSysSummary, system);
        showTime(tvSysTime, system);
    }

    /**
     * 处理通知时间
     *
     * @param tv       显示控件
     * @param msgGroup 数据模型
     */
    private void showTime(TextView tv, UnreadMsgResBean.MsgGroup msgGroup) {
        if (msgGroup.getMessage() != null && msgGroup.getMessage().length > 0) {
            UnreadMsgResBean.VsoMessage vsoMessage = msgGroup.getMessage()[0];
            long ts;
            if (vsoMessage != null) {
                ts = vsoMessage.getOn_time();
            } else {
//                tv.setText(null); 保留之前的内容
                return;
            }
            long todayBeginning = TimeUtil.getTodayBeginning();
            if (ts >= todayBeginning) {
                int hour = TimeUtil.getHour(ts);
                int minute = TimeUtil.getMinute(ts);
                String format = String.format("%d:%d", hour, minute);
                tv.setText(format);
            } else {
                TimeUtil.DateTransformer transformer = TimeUtil.newDateTransformer(vsoMessage
                        .getOn_time());
                tv.setText(transformer.getDefaultFormat());
            }
        }
    }

    /**
     * 处理最后消息概述
     *
     * @param tv       显示控件
     * @param msgGroup 数据模型
     */
    private void showSummary(TextView tv, UnreadMsgResBean.MsgGroup msgGroup) {
        if (msgGroup.getMessage() != null && msgGroup.getMessage().length > 0) {
            UnreadMsgResBean.VsoMessage vsoMessage = msgGroup.getMessage()[0];
            if (vsoMessage != null) {
                tv.setText(Html.fromHtml(StringUtil.nullStrToEmpty(vsoMessage.getContent())).toString());
            } else {
//                tv.setText(null);  保留之前的内容
                return;
            }
        }
    }


    /**
     * 更新系统通知的角标
     */
    private void updateSysBadge(UnreadMsgResBean.MsgGroup msgGroup) {
        if (msgGroup == null) {
            sysMsgBadge.clear();
        } else {
            updateSysBadge(msgGroup.getCount());
        }
    }

    private void updateSysBadge(int num) {
        if (num > 0) {
            sysMsgBadge.updateWithFriendlyMode(num, NumBadge.RECOMMEND_MAX);
        } else {
            sysMsgBadge.clear();
        }
    }

    /**
     * 更新系统通知的角标
     */
    private void updateVsoAcBadge(int acNum) {
        if (acNum > 0) {
            vsoAcMsgBadge.updateWithPointMode();
        } else {
            vsoAcMsgBadge.clear();
        }
    }

}