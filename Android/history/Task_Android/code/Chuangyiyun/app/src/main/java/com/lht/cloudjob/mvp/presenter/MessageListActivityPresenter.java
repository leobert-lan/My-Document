package com.lht.cloudjob.mvp.presenter;

import android.content.Context;
import android.view.View;

import com.lht.cloudjob.R;
import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.DeleteMsgModel;
import com.lht.cloudjob.mvp.model.MarkMsgReadModel;
import com.lht.cloudjob.mvp.model.MessageListModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.MessageListItemResBean;
import com.lht.cloudjob.mvp.viewinterface.IMessageListActivity;
import com.lht.cloudjob.util.internet.HttpUtil;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> MessageListActivityPresenter
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/8/19
 */
public class MessageListActivityPresenter implements IApiRequestPresenter {
    private IMessageListActivity iMessageListActivity;

    private String username;

    public MessageListActivityPresenter(String username, IMessageListActivity
            iMessageListActivity) {
        this.iMessageListActivity = iMessageListActivity;
        this.username = username;
    }

    private boolean isRefreshOperate;

    public void callRefreshMessages(IRestfulApi.MessageListApi.MessageType type) {
        isRefreshOperate = true;
        iMessageListActivity.showWaitView(true);
        IApiRequestModel model = new MessageListModel(username, 0, type, new
                MessageListModelCallback());

        model.doRequest(iMessageListActivity.getActivity());
    }

    public void callAddMessages(int offset, IRestfulApi.MessageListApi.MessageType type) {
        isRefreshOperate = false;
        iMessageListActivity.showWaitView(true);
        IApiRequestModel model = new MessageListModel(username, offset, type, new
                MessageListModelCallback());

        model.doRequest(iMessageListActivity.getActivity());
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

    public void callMarkMsgRead(ArrayList<String> ids) {
        if (ids == null || ids.isEmpty()) {

            iMessageListActivity.showMsg(iMessageListActivity.getActivity().getString(R.string.v1010_dialog_message_select_message));
            return;
        }
        iMessageListActivity.showWaitView(true);
        IApiRequestModel model = new MarkMsgReadModel(ids, new MarkMsgReadModelCallback());
        model.doRequest(iMessageListActivity.getActivity());
    }

    public void callDeleteMsg(final ArrayList<String> ids) {
        if (ids == null || ids.isEmpty()) {

            iMessageListActivity.showMsg(iMessageListActivity.getActivity().getString(R.string.v1010_dialog_message_select_message));
            return;
        }

        iMessageListActivity.showDeleteAlert(new CustomPopupWindow.OnPositiveClickListener() {
            @Override
            public void onPositiveClick() {
                iMessageListActivity.showWaitView(true);
                IApiRequestModel model = new DeleteMsgModel(ids, new DeleteMsgModelCallback());
                model.doRequest(iMessageListActivity.getActivity());
            }
        });
    }

    private class MessageListModelCallback implements
            ApiModelCallback<ArrayList<MessageListItemResBean>> {

        @Override
        public void onSuccess(BaseBeanContainer<ArrayList<MessageListItemResBean>> beanContainer) {
            iMessageListActivity.cancelWaitView();
            ArrayList<MessageListItemResBean> data = beanContainer.getData();
            if (data == null || data.isEmpty()) {
                iMessageListActivity.showEmptyView();
            } else {
                iMessageListActivity.showCompiletoggle();
                if (isRefreshOperate) {
                    iMessageListActivity.setMessageListData(data);
                } else {
                    iMessageListActivity.addMessageListData(data);
                }
            }
            iMessageListActivity.finishRefresh();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iMessageListActivity.cancelWaitView();

            if (isRefreshOperate) {
//                iMessageListActivity.setMessageListData(new ArrayList<MessageListItemResBean>());
                iMessageListActivity.showEmptyView();
            } else {
                //已经到底了
                iMessageListActivity.showMsg(iMessageListActivity.getActivity().getString(R.string.v1010_toast_list_all_data_added));
            }
            iMessageListActivity.finishRefresh();
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iMessageListActivity.cancelWaitView();
            iMessageListActivity.finishRefresh();
            iMessageListActivity.showRetryView(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callRefreshMessages(iMessageListActivity.askMessageType());
                }
            });
        }
    }

    private class MarkMsgReadModelCallback implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iMessageListActivity.cancelWaitView();
            iMessageListActivity.showMsg(beanContainer.getData().getMessage());
            iMessageListActivity.finishModify();
            callRefreshMessages(iMessageListActivity.askMessageType());
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iMessageListActivity.cancelWaitView();
            iMessageListActivity.showMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iMessageListActivity.cancelWaitView();
            iMessageListActivity.finishRefresh();
        }
    }

    private class DeleteMsgModelCallback implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iMessageListActivity.cancelWaitView();
            iMessageListActivity.showMsg(beanContainer.getData().getMessage());
            iMessageListActivity.finishModify();
            callRefreshMessages(iMessageListActivity.askMessageType());
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iMessageListActivity.cancelWaitView();
            iMessageListActivity.showMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iMessageListActivity.cancelWaitView();
            iMessageListActivity.finishRefresh();
        }
    }
}
