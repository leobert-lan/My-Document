package com.lht.cloudjob.mvp.presenter;

import android.content.Context;

import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.asyncprotected.AttentionActivity;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.FavorListModel;
import com.lht.cloudjob.mvp.model.UnfollowModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.FavorListItemResBean;
import com.lht.cloudjob.mvp.viewinterface.IAttentionActivity;
import com.lht.cloudjob.util.internet.HttpUtil;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> AttentionActivityPresenter
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/8/11.
 */
public class AttentionActivityPresenter implements IApiRequestPresenter {
    private IAttentionActivity iAttentionActivity;

    private String username;

    private FavorListModel favorListModel;

    public AttentionActivityPresenter(IAttentionActivity iAttentionActivity) {
        this.iAttentionActivity = iAttentionActivity;
        username = iAttentionActivity.getActivity().getIntent().getStringExtra(AttentionActivity.KEY_DATA);
        favorListModel = new FavorListModel(new FavorListModelCallback());
    }

    private boolean isRefreshOperate = false;

    public void callRefreshList() {
        isRefreshOperate = true;
        iAttentionActivity.showWaitView(true);
        favorListModel.setParams(username, 0);
        favorListModel.doRequest(iAttentionActivity.getActivity());
    }

    public void callAddNextPage(int offset) {
        isRefreshOperate = false;
        iAttentionActivity.showWaitView(true);
        favorListModel.setParams(username, offset);
        favorListModel.doRequest(iAttentionActivity.getActivity());
    }

    public void callUnfollow(int index,FavorListItemResBean bean) {
        iAttentionActivity.showWaitView(true);
        IApiRequestModel model = new UnfollowModel(username,
                bean.getObj_name(),new UnfollowModelCallback(index,bean));
        model.doRequest(iAttentionActivity.getActivity());
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    private final class FavorListModelCallback implements ApiModelCallback<ArrayList<FavorListItemResBean>> {

        @Override
        public void onSuccess(BaseBeanContainer<ArrayList<FavorListItemResBean>> beanContainer) {
            // TODO: 2016/8/11 校验
            iAttentionActivity.cancelWaitView();
            iAttentionActivity.finishListFresh();
            ArrayList<FavorListItemResBean> items = beanContainer.getData();
            if (items == null ||items.size() == 0) {
                if (isRefreshOperate) {
                    iAttentionActivity.showEmptyView();
                } else {
                    iAttentionActivity.showErrorMsg("all added");
                }
            } else {
                if (isRefreshOperate) {
                    iAttentionActivity.setFavorListItems(items);
                } else {
                    iAttentionActivity.addFavorListItems(items);
                }
            }
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iAttentionActivity.cancelWaitView();
            iAttentionActivity.finishListFresh();
            if (beanContainer.getData().getRet() == FavorListItemResBean.NULL_IN_INTERVAL) {
                //修改接口后，应该是此处逻辑真实生效，success中的保留，
                if (isRefreshOperate) {
                    iAttentionActivity.showEmptyView();
                } else {
                    iAttentionActivity.showErrorMsg(iAttentionActivity.getActivity().getString(R.string.v1010_toast_list_all_data_added));
                }
            } else {
                iAttentionActivity.showErrorMsg(beanContainer.getData().getMessage());
            }
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iAttentionActivity.cancelWaitView();
            iAttentionActivity.finishListFresh();

            // TODO: 2016/8/11
        }
    }

    private final class UnfollowModelCallback implements ApiModelCallback<BaseVsoApiResBean> {
        private int index;

        private FavorListItemResBean listItemBean;

        public UnfollowModelCallback(int index,FavorListItemResBean bean) {
            this.index = index;
            this.listItemBean = bean;
        }

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iAttentionActivity.cancelWaitView();

            iAttentionActivity.removeItem(index,listItemBean);
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iAttentionActivity.cancelWaitView();

            iAttentionActivity.showErrorMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iAttentionActivity.cancelWaitView();

            // TODO: 2016/8/11
        }
    }
}
