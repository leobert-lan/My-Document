package com.lht.cloudjob.mvp.presenter;

import android.content.Intent;
import android.view.View;
import com.lht.cloudjob.R;
import com.lht.cloudjob.clazz.LoginIntentFactory;
import com.lht.cloudjob.interfaces.ITriggerCompare;
import com.lht.cloudjob.interfaces.net.IPagedApiRequestModel;
import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.OrderedTaskListModel;
import com.lht.cloudjob.mvp.model.RecommendTaskListModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.OrderedTaskResBean;
import com.lht.cloudjob.mvp.model.bean.RecommendTaskResBean;
import com.lht.cloudjob.mvp.model.pojo.DemandItemData;
import com.lht.cloudjob.mvp.viewinterface.IOrderedTaskFragment;
import com.lht.cloudjob.util.string.StringUtil;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> OrderedTaskFragmentPresenter
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/8/24
 */
public class OrderedTaskFragmentPresenter extends ABSVerifyNeedPresenter {
    private IOrderedTaskFragment iOrderedTaskFragment;
    private IPagedApiRequestModel orderedListModel;
    private IPagedApiRequestModel recommendTaskListModel;

    public OrderedTaskFragmentPresenter(IOrderedTaskFragment iOrderedTaskFragment) {
        this.iOrderedTaskFragment = iOrderedTaskFragment;
        orderedListModel = new OrderedTaskListModel(new OrderedTaskListModelCallback());
        recommendTaskListModel = new RecommendTaskListModel(new RecommendTaskListModelCallback());
    }

    private boolean isRefreshOperate = false;

    public void callRefreshListData(String usr, IRestfulApi.OrderedTaskListApi.Type type) {
        if (!isLogin()) {
            iOrderedTaskFragment.finishRefresh();
            return;
        }
        iOrderedTaskFragment.showWaitView(true);
        isRefreshOperate = true;
        ((OrderedTaskListModel) orderedListModel).setParams(usr, type, 0);
        orderedListModel.doRequest(iOrderedTaskFragment.getActivity());

    }

    public void callAddListData(String usr, IRestfulApi.OrderedTaskListApi.Type type, int offset) {
        if (!isLogin()) {
            iOrderedTaskFragment.finishRefresh();
            return;
        }
        iOrderedTaskFragment.showWaitView(true);
        isRefreshOperate = false;
        ((OrderedTaskListModel) orderedListModel).setParams(usr, type, offset);
        orderedListModel.doRequest(iOrderedTaskFragment.getActivity());
    }

    /**
     * desc: 页面接收到订阅事件后，调用presenter#identifyTrigger，执行逻辑，需要区分触发事件是不是登录事件
     *
     * @param trigger an  interface to identify trigger,use equal(ITriggerCompare compare)
     */
    @Override
    public void identifyTrigger(ITriggerCompare trigger) {
        //always handle
        callRefreshListData(iOrderedTaskFragment.getLoginInfo().getUsername(),
                iOrderedTaskFragment.getCurrentType());
    }

    /**
     * desc: check if login
     *
     * @return true while login,false otherwise
     */
    @Override
    protected boolean isLogin() {
        return !StringUtil.isEmpty(iOrderedTaskFragment.getLoginInfo().getUsername());
    }


    /**
     * desc: update status,implement the method with an appropriate design
     *
     * @param isLogin
     */
    @Override
    public void setLoginStatus(boolean isLogin) {
        //ignore
    }

    public View.OnClickListener getOnOperateListener(final DemandItemData item) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag == null || !(tag instanceof Integer)) {
                    return;
                }
                doOperate(item, (Integer) tag);
            }
        };
    }

    private void doOperate(DemandItemData item, Integer op) {
        switch (op) {
            case 1://再次投稿
                iOrderedTaskFragment.reContribute(item);
                break;
            case 3://联系ta
                iOrderedTaskFragment.callPublisher(item);
                break;
            case 4://签署协议
                iOrderedTaskFragment.signAgreement(item);
                break;
            case 12://评价
                iOrderedTaskFragment.jump2Evaluate(item);
                break;
            default:
                break;
        }
    }

    public void callLogin(LoginTrigger loginTrigger) {
        Intent intent = LoginIntentFactory.create(iOrderedTaskFragment.getActivity(), loginTrigger);
        iOrderedTaskFragment.getActivity().startActivity(intent);
    }

    public void callGetRecommendTaskList(String user) {
        iOrderedTaskFragment.showWaitView(true);
        isRefreshOperate = true;
        recommendTaskListModel.setParams(user, 0);
        recommendTaskListModel.doRequest(iOrderedTaskFragment.getActivity());
    }

    public void callAddRecommendTaskList(String user, int offset) {
        iOrderedTaskFragment.showWaitView(true);
        isRefreshOperate = false;
        recommendTaskListModel.setParams(user, offset);
        recommendTaskListModel.doRequest(iOrderedTaskFragment.getActivity());
    }

    /**
     * 承接需求列表获取回调实现类
     */
    private class OrderedTaskListModelCallback implements
            ApiModelCallback<ArrayList<OrderedTaskResBean>> {

        @Override
        public void onSuccess(BaseBeanContainer<ArrayList<OrderedTaskResBean>> beanContainer) {
            ArrayList<OrderedTaskResBean> data = beanContainer.getData();
            if (data == null || data.isEmpty()) {
                iOrderedTaskFragment.showEmptyView();
            } else {
                iOrderedTaskFragment.hideEmptyView();
                ArrayList<DemandItemData> liData = new ArrayList<>();
                for (OrderedTaskResBean bean : data) {
                    DemandItemData itemData = new DemandItemData();
                    bean.copyTo(itemData);
                    liData.add(itemData);
                }
                if (isRefreshOperate) {
                    iOrderedTaskFragment.setOrderedListItems(liData);
                } else {
                    iOrderedTaskFragment.addOrderedListItems(liData);
                }
            }
            iOrderedTaskFragment.cancelWaitView();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iOrderedTaskFragment.cancelWaitView();
//            iOrderedTaskFragment.showErrorMsg(beanContainer.getData().getMessage());
            iOrderedTaskFragment.finishRefresh();
            iOrderedTaskFragment.finishRefresh2();
            if (isRefreshOperate) {
                iOrderedTaskFragment.showEmptyView();
            } else {
                //ignore all data added;
                //已经到底了
                iOrderedTaskFragment.showMsg(iOrderedTaskFragment.getActivity().getString(R.string.v1010_toast_list_all_data_added));
            }
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iOrderedTaskFragment.cancelWaitView();
            // TODO: 2016/9/23 list done
            iOrderedTaskFragment.finishRefresh();
            iOrderedTaskFragment.finishRefresh2();
            // TODO: 2016/8/22 清空数据？
            iOrderedTaskFragment.setOrderedListItems(new ArrayList<DemandItemData>());

        }
    }

    /**
     * 推荐列表回调
     */
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
                iOrderedTaskFragment.setRecommendListItems(liData);
            } else {
                iOrderedTaskFragment.addRecommendListItems(liData);
            }
            iOrderedTaskFragment.finishRefresh2();
            iOrderedTaskFragment.cancelWaitView();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iOrderedTaskFragment.cancelWaitView();
            if (isRefreshOperate) {
                //ignore

            } else {
                //已经到底了
                iOrderedTaskFragment.showErrorMsg(iOrderedTaskFragment.getActivity().getString(R.string.v1010_toast_list_all_data_added));
            }

            iOrderedTaskFragment.finishRefresh2();
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iOrderedTaskFragment.cancelWaitView();
            iOrderedTaskFragment.finishRefresh2();
        }
    }

    /**
     * 已接需求页面登录触发者
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
