package com.lht.cloudjob.mvp.presenter;

import android.content.Context;
import android.view.View;

import com.lht.cloudjob.R;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.VsoActivitiesModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.VsoActivitiesResBean;
import com.lht.cloudjob.mvp.viewinterface.IVsoAcListActivity;
import com.lht.cloudjob.util.internet.HttpUtil;

import java.util.ArrayList;

/**
 * Created by chhyu on 2016/12/16.
 */

public class VsoAcListActivityPresenter implements IApiRequestPresenter {
    private IVsoAcListActivity iVsoAcListActivity;
    private VsoActivitiesModel model;

    public VsoAcListActivityPresenter(IVsoAcListActivity iVsoAcListActivity) {
        this.iVsoAcListActivity = iVsoAcListActivity;
        model = new VsoActivitiesModel(new VsoAcListCallback());
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    private boolean isRefreshOperate;

    public void callRefreshMessages() {
        isRefreshOperate = true;
        iVsoAcListActivity.showWaitView(true);
        model.setParams(0);
        model.doRequest(iVsoAcListActivity.getActivity());
    }

    /**
     * 上拉加载更多
     *
     * @param offset
     */
    public void callAddMessages(int offset) {
        isRefreshOperate = false;
        iVsoAcListActivity.showWaitView(true);
        model.setParams(offset);
        model.doRequest(iVsoAcListActivity.getActivity());
    }

    private class VsoAcListCallback implements ApiModelCallback<ArrayList<VsoActivitiesResBean>> {

        @Override
        public void onSuccess(BaseBeanContainer<ArrayList<VsoActivitiesResBean>> beanContainer) {
            iVsoAcListActivity.cancelWaitView();
            ArrayList<VsoActivitiesResBean> data = beanContainer.getData();
            if (data == null || data.isEmpty()) {
                iVsoAcListActivity.showEmptyView();
            } else {
                if (isRefreshOperate) {
                    iVsoAcListActivity.setMessageListData(data);
                } else {
                    iVsoAcListActivity.addMessageListData(data);
                }
            }
            iVsoAcListActivity.finishRefresh();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iVsoAcListActivity.cancelWaitView();
            if (isRefreshOperate) {
                iVsoAcListActivity.showEmptyView();
            } else {
                //已经到底了
                iVsoAcListActivity.showMsg(iVsoAcListActivity.getActivity().getString(R.string.v1010_toast_list_all_data_added));
            }
            iVsoAcListActivity.finishRefresh();
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iVsoAcListActivity.cancelWaitView();
            iVsoAcListActivity.finishRefresh();
            iVsoAcListActivity.showRetryView(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callRefreshMessages();
                }
            });
        }
    }

}
