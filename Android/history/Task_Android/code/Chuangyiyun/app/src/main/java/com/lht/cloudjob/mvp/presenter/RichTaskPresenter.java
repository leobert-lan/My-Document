package com.lht.cloudjob.mvp.presenter;

import android.content.Context;
import android.view.View;

import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.RichTaskListModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.RichTaskResBean;
import com.lht.cloudjob.mvp.model.pojo.DemandItemData;
import com.lht.cloudjob.mvp.viewinterface.IRichTaskActivity;
import com.lht.cloudjob.util.internet.HttpUtil;

import java.util.ArrayList;

/**
 * @author
 * @version 1.0
 * @date 2016/8/17
 */
public class RichTaskPresenter implements IApiRequestPresenter {

    private IRichTaskActivity iRichTaskActivity;

    public RichTaskPresenter(IRichTaskActivity iRichTaskActivity) {
        this.iRichTaskActivity = iRichTaskActivity;
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    public void callRefreshList() {
        iRichTaskActivity.showWaitView(true);
        IApiRequestModel model = new RichTaskListModel(new RichTaskListModelCallback());
        model.doRequest(iRichTaskActivity.getActivity());

    }

    private class RichTaskListModelCallback implements ApiModelCallback<ArrayList<RichTaskResBean
            >> {

        @Override
        public void onSuccess(BaseBeanContainer<ArrayList<RichTaskResBean>> beanContainer) {
            ArrayList<RichTaskResBean> data = beanContainer.getData();
            if (data == null || data.isEmpty()) {
                iRichTaskActivity.showEmptyView();
            } else {
                ArrayList<DemandItemData> liData = new ArrayList<>();
                for (RichTaskResBean bean : data) {
                    DemandItemData itemData = new DemandItemData();
                    bean.copyTo(itemData);
                    liData.add(itemData);
                }
                iRichTaskActivity.updateList(liData);
            }
            iRichTaskActivity.cancelWaitView();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iRichTaskActivity.cancelWaitView();
            iRichTaskActivity.showErrorMsg(beanContainer.getData().getMessage());
            iRichTaskActivity.showEmptyView();
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iRichTaskActivity.cancelWaitView();
            iRichTaskActivity.finishRefresh();
            iRichTaskActivity.showRetryView(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callRefreshList();
                }
            });
        }
    }
}
