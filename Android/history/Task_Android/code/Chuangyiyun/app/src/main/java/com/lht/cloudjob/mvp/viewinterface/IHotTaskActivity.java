package com.lht.cloudjob.mvp.viewinterface;

import android.view.View;

import com.lht.cloudjob.mvp.model.pojo.DemandItemData;

import java.util.ArrayList;

/**
 * @author
 * @version 1.0
 * @date 2016/8/17
 */
public interface IHotTaskActivity extends IActivityAsyncProtected {

    void showErrorMsg(String msg);

    void updateList(ArrayList<DemandItemData> datas);

    void showRetryView(View.OnClickListener listener);

    void showEmptyView();

    void finishRefresh();
}
