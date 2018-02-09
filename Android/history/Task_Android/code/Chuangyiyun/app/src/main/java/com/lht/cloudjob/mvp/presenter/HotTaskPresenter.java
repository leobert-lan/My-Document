package com.lht.cloudjob.mvp.presenter;

import android.content.Context;
import android.view.View;

import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.HotTaskListModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.HotTaskResBean;
import com.lht.cloudjob.mvp.model.pojo.DemandItemData;
import com.lht.cloudjob.mvp.viewinterface.IHotTaskActivity;
import com.lht.cloudjob.util.internet.HttpUtil;

import java.util.ArrayList;

/**
 * @author
 * @version 1.0
 * @date 2016/8/17
 */
public class HotTaskPresenter implements IApiRequestPresenter {
    
    private IHotTaskActivity iHotTaskActivity;

    public HotTaskPresenter(IHotTaskActivity iHotTaskActivity) {
        this.iHotTaskActivity = iHotTaskActivity;
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    public void callRefreshList() {
        iHotTaskActivity.showWaitView(true);
        IApiRequestModel model = new HotTaskListModel(new HotTaskListModelCallback());
        model.doRequest(iHotTaskActivity.getActivity());
        
    }

    private class HotTaskListModelCallback implements ApiModelCallback<ArrayList<HotTaskResBean>> {

        @Override
        public void onSuccess(BaseBeanContainer<ArrayList<HotTaskResBean>> beanContainer) {
            ArrayList<HotTaskResBean> data  = beanContainer.getData();
            if (data == null || data.isEmpty()) {
                iHotTaskActivity.showEmptyView();
            } else {
                ArrayList<DemandItemData> liData = new ArrayList<>();
                for (HotTaskResBean bean :data) {
                    DemandItemData itemData = new DemandItemData();
                    bean.copyTo(itemData);
                    liData.add(itemData);
                }
                iHotTaskActivity.updateList(liData);
            }
            iHotTaskActivity.cancelWaitView();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iHotTaskActivity.cancelWaitView();
            iHotTaskActivity.showErrorMsg(beanContainer.getData().getMessage());
            iHotTaskActivity.showEmptyView();
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iHotTaskActivity.cancelWaitView();
            iHotTaskActivity.finishRefresh();
            iHotTaskActivity.showRetryView(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callRefreshList();
                }
            });
        }
    }
}
