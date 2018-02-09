package com.lht.cloudjob.mvp.presenter;

import android.content.Context;
import android.content.Intent;

import com.lht.cloudjob.clazz.LoginIntentFactory;
import com.lht.cloudjob.interfaces.ITriggerCompare;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.UnreadMessageModel;
import com.lht.cloudjob.mvp.model.VsoActivitiesModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.UnreadMsgResBean;
import com.lht.cloudjob.mvp.model.bean.VsoActivitiesResBean;
import com.lht.cloudjob.mvp.viewinterface.IMessageFragment;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.internet.HttpUtil;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> MessageFragmentPrsenter
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/8/18.
 */
public class MessageFragmentPresenter extends ABSVerifyNeedPresenter implements IApiRequestPresenter {

    private IMessageFragment iMessageFragment;

    public MessageFragmentPresenter(IMessageFragment iMessageFragment) {
        this.iMessageFragment = iMessageFragment;
    }

    public void callLogin() {
        Intent intent = LoginIntentFactory.create(iMessageFragment.getActivity(), LoginTrigger
                .SystemMessage);
        iMessageFragment.getActivity().startActivity(intent);
    }

    @Override
    public void identifyTrigger(ITriggerCompare trigger) {
        //empty,because anytime user logged in, we should get message list,if fragment is not
        // initialized,event will not be sent to fragment.
        return;
    }

    @Override
    protected boolean isLogin() {
        return isLogin;
    }

    private boolean isLogin;

    @Override
    public void setLoginStatus(boolean isLogin) {
        this.isLogin = isLogin;
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    public void callGetMessage(String username) {
        IApiRequestModel model = new UnreadMessageModel(username, new UnreadMessageModelCallback());
        model.doRequest(iMessageFragment.getActivity());
    }

    private class UnreadMessageModelCallback implements ApiModelCallback<UnreadMsgResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<UnreadMsgResBean> beanContainer) {
            iMessageFragment.updateMessage(beanContainer.getData());
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            DLog.e(getClass(), "消息集成接口：" +
                    beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            //empty
        }
    }

    /**
     * 获取精彩活动信息
     */
    public void callGetVsoActivities() {
        VsoActivitiesModel model = new VsoActivitiesModel(new VsoActivitiesModelCallback());
        model.setParams(0);
        model.doRequest(iMessageFragment.getActivity());
    }

    private class VsoActivitiesModelCallback implements ApiModelCallback<ArrayList<VsoActivitiesResBean>> {

        @Override
        public void onSuccess(BaseBeanContainer<ArrayList<VsoActivitiesResBean>> beanContainer) {
            //取出第一条
            ArrayList<VsoActivitiesResBean> data = beanContainer.getData();
            if (data != null && data.size() > 0) {
                iMessageFragment.updateVsoActivitiesMsg(data.get(0));
            }
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iMessageFragment.updateVsoActivitiesMsg(null);
        }

        @Override
        public void onHttpFailure(int httpStatus) {
//ignore
        }
    }


    /**
     * 系统消息页面登录触发者
     */
    public enum LoginTrigger implements ITriggerCompare {
        SystemMessage(1);

        private final int tag;

        LoginTrigger(int i) {
            tag = i;
        }

        @Override
        public boolean equals(ITriggerCompare compare) {
            boolean b1 = compare.getClass().getName().equals(getClass().getName());
            boolean b2 = compare.getTag().equals(getTag());
            return b1 & b2;
        }

        @Override
        public Object getTag() {
            return tag;
        }

        @Override
        public Serializable getSerializable() {
            return this;
        }

    }
}
