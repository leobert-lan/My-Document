package com.lht.cloudjob.mvp.viewinterface;

import android.view.View;

import com.lht.cloudjob.mvp.model.bean.VsoActivitiesResBean;

import java.util.ArrayList;

/**
 * Created by chhyu on 2016/12/16.
 */

public interface IVsoAcListActivity extends IActivityAsyncProtected {

    void showEmptyView();

    void setMessageListData(ArrayList<VsoActivitiesResBean> data);

    void addMessageListData(ArrayList<VsoActivitiesResBean> data);

    void finishRefresh();

    void showRetryView(View.OnClickListener onClickListener);
}
