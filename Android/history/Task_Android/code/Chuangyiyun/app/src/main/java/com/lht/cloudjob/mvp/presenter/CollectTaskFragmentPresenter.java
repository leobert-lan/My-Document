package com.lht.cloudjob.mvp.presenter;

import android.content.Context;
import android.content.Intent;

import com.lht.cloudjob.R;
import com.lht.cloudjob.clazz.LoginIntentFactory;
import com.lht.cloudjob.customview.FgDemandTitleBar;
import com.lht.cloudjob.interfaces.ITriggerCompare;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.interfaces.net.IPagedApiRequestModel;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.CollectedTaskListModel;
import com.lht.cloudjob.mvp.model.DiscollectTaskModel;
import com.lht.cloudjob.mvp.model.RecommendTaskListModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.CollectedTaskResBean;
import com.lht.cloudjob.mvp.model.bean.RecommendTaskResBean;
import com.lht.cloudjob.mvp.model.pojo.DemandItemData;
import com.lht.cloudjob.mvp.viewinterface.ICollectTaskFragment;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.lht.cloudjob.util.string.StringUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> CollectTaskFragmentPresenter
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/8/23
 */
public class CollectTaskFragmentPresenter extends ABSVerifyNeedPresenter implements
        IApiRequestPresenter {

    private ICollectTaskFragment iCollectTaskFragment;

    private IPagedApiRequestModel model;

    private IPagedApiRequestModel recommendTaskListModel;

    private IApiRequestModel discollectTaskModel;

    public CollectTaskFragmentPresenter(ICollectTaskFragment iCollectTaskFragment) {
        this.iCollectTaskFragment = iCollectTaskFragment;
        model = new CollectedTaskListModel(new CollectedTaskListModelCallback());
        recommendTaskListModel = new RecommendTaskListModel(new RecommendTaskListModelCallback());
    }

    private boolean isRefreshOperate;

    public void callDiscollectTasks(String username, ArrayList<String> tasks) {
        discollectTaskModel = new DiscollectTaskModel(username, tasks, new
                DiscollectTaskModelCallback());
        EventBus.getDefault().post(new FgDemandTitleBar.CloseModifyModeEvent());
        discollectTaskModel.doRequest(iCollectTaskFragment.getActivity());
    }

    public void callGetRecommendTaskList(String user) {
        iCollectTaskFragment.showWaitView(true);
        isRefreshOperate = true;
        recommendTaskListModel.setParams(user, 0);
        recommendTaskListModel.doRequest(iCollectTaskFragment.getActivity());
    }

    public void callAddRecommendTaskList(String user, int offset) {
        iCollectTaskFragment.showWaitView(true);
        isRefreshOperate = false;
        recommendTaskListModel.setParams(user, offset);
        recommendTaskListModel.doRequest(iCollectTaskFragment.getActivity());
    }

    public void callGetCollectedTaskList(String user) {
        if (StringUtil.isEmpty(user)) {
            DLog.i(getClass(), new DLog.LogLocation(), "call get collected task while unlogin");
            return;
        }
        iCollectTaskFragment.showWaitView(true);
        isRefreshOperate = true;
        model.setParams(user, 0);
        model.doRequest(iCollectTaskFragment.getActivity());
    }

    public void callLogin(LoginTrigger loginTrigger) {
        Intent intent = LoginIntentFactory.create(iCollectTaskFragment.getActivity(), loginTrigger);
        iCollectTaskFragment.getActivity().startActivity(intent);
    }

    public void callAddCollectedTaskList(String user, int offset) {
        if (StringUtil.isEmpty(user)) {
            DLog.i(getClass(), new DLog.LogLocation(), "call get collected task while unlogin");
            return;
        }
        iCollectTaskFragment.showWaitView(true);
        isRefreshOperate = false;
        model.setParams(user, offset);
        model.doRequest(iCollectTaskFragment.getActivity());
    }

    /**
     * 页面结束时取消所有相关的回调
     *
     * @param context
     */
    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    /**
     * desc: 页面接收到订阅事件后，调用presenter#identifyTrigger，执行逻辑，需要区分触发事件是不是登录事件
     *
     * @param trigger an  interface to identify trigger,use equal(ITriggerCompare compare)
     */
    @Override
    public void identifyTrigger(ITriggerCompare trigger) {
        //一旦检测到登录就查询，
//        if (LoginTrigger.Mask.equals(trigger)) {
//            callGetCollectedTaskList(iCollectTaskFragment.getLoginInfo().getUsername());
//        }
        callGetCollectedTaskList(iCollectTaskFragment.getLoginInfo().getUsername());
        return;
    }

    private boolean isLogin;

    /**
     * desc: check if login
     *
     * @return true while login,false otherwise
     */
    @Override
    protected boolean isLogin() {
        return isLogin;
    }

    /**
     * desc: update status,implement the method with an appropriate design
     *
     * @param isLogin
     */
    @Override
    public void setLoginStatus(boolean isLogin) {
        this.isLogin = isLogin;
    }

    private class CollectedTaskListModelCallback implements
            ApiModelCallback<ArrayList<CollectedTaskResBean>> {

        @Override
        public void onSuccess(BaseBeanContainer<ArrayList<CollectedTaskResBean>> beanContainer) {
            ArrayList<CollectedTaskResBean> data = beanContainer.getData();
            if (data == null || data.isEmpty()) {
                iCollectTaskFragment.showEmptyView();
            } else {
                iCollectTaskFragment.hideEmptyView();
                ArrayList<DemandItemData> liData = new ArrayList<>();
                for (CollectedTaskResBean bean : data) {
                    DemandItemData itemData = new DemandItemData();
                    bean.copyTo(itemData);
                    liData.add(itemData);
                }
                if (isRefreshOperate) {
                    iCollectTaskFragment.setCollectedListItems(liData);
                } else {
                    iCollectTaskFragment.addCollectedListItems(liData);
                }
                iCollectTaskFragment.finishRefresh();
            }
            iCollectTaskFragment.cancelWaitView();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iCollectTaskFragment.cancelWaitView();

            if (isRefreshOperate) {
                iCollectTaskFragment.showEmptyView();
            } else {
                //已经到底了
                iCollectTaskFragment.showErrorMsg(iCollectTaskFragment.getActivity().getString(R.string.v1010_toast_list_all_data_added));
            }
            iCollectTaskFragment.finishRefresh();
            iCollectTaskFragment.checkListData();
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iCollectTaskFragment.cancelWaitView();
            iCollectTaskFragment.finishRefresh();
            // TODO: 2016/8/22 list done
        }
    }

    private class RecommendTaskListModelCallback implements
            ApiModelCallback<ArrayList<RecommendTaskResBean>> {

        @Override
        public void onSuccess(BaseBeanContainer<ArrayList<RecommendTaskResBean>> beanContainer) {
            ArrayList<DemandItemData> liData = new ArrayList<>();
            ArrayList<RecommendTaskResBean> data = beanContainer.getData();
            if (data == null) {
                data = new ArrayList<>();
            }
            for (RecommendTaskResBean bean : data) {
                DemandItemData itemData = new DemandItemData();
                bean.copyTo(itemData);
                liData.add(itemData);
            }
            if (isRefreshOperate) {
                iCollectTaskFragment.setRecommendListItems(liData);
            } else {
                iCollectTaskFragment.addRecommentListItems(liData);
            }
            iCollectTaskFragment.finishRefresh2();
            iCollectTaskFragment.cancelWaitView();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iCollectTaskFragment.cancelWaitView();
            if (isRefreshOperate) {
                //ignore
            } else {
                //已经到底了
                iCollectTaskFragment.showErrorMsg(iCollectTaskFragment.getActivity().getString(R.string.v1010_toast_list_all_data_added));
            }

            iCollectTaskFragment.finishRefresh2();
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iCollectTaskFragment.cancelWaitView();
            iCollectTaskFragment.finishRefresh2();
            // TODO: 2016/8/22 list done
            iCollectTaskFragment.finishRefresh();
        }
    }

    private class DiscollectTaskModelCallback implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            callGetCollectedTaskList(iCollectTaskFragment.getLoginInfo().getUsername());
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iCollectTaskFragment.cancelWaitView();
            iCollectTaskFragment.showMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iCollectTaskFragment.cancelWaitView();

            // TODO: 2016/8/25
        }
    }

    /**
     * 我的收藏页面登录触发者
     */
    public enum LoginTrigger implements ITriggerCompare {
        Mask(1);

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
