package com.lht.cloudjob.mvp.viewinterface;

import android.view.View;

import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.bean.MessageListItemResBean;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.viewinterface
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IMessageListActivity
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/8/19
 */
public interface IMessageListActivity extends IActivityAsyncProtected {

    void setMessageListData(ArrayList<MessageListItemResBean> data);

    void addMessageListData(ArrayList<MessageListItemResBean> data);

    void showErrorMsg(String msg);

    void showEmptyView();

    void finishModify();

    void showRetryView(View.OnClickListener listener);

    IRestfulApi.MessageListApi.MessageType askMessageType();


    /**
     * 显示删除询问
     * @param positiveClickListener    确认时的回掉
     */
    void showDeleteAlert(CustomPopupWindow.OnPositiveClickListener positiveClickListener);

    void finishRefresh();

    void showCompiletoggle();

}
