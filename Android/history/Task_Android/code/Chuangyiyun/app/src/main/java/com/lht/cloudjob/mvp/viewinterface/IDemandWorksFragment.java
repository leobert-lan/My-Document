package com.lht.cloudjob.mvp.viewinterface;

import android.app.Activity;

import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.cloudjob.mvp.model.bean.DemandInfoResBean;
import com.lht.cloudjob.mvp.model.pojo.PreviewFileEntity;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.viewinterface
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IDemandWorksFragment
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/8/29
 */
public interface IDemandWorksFragment extends IAsyncProtectedFragent{
    String getTaskBn();

    void setListData(ArrayList<DemandInfoResBean.Work> data);

    void addListData(ArrayList<DemandInfoResBean.Work> data);

    void finishRefresh();

    Activity getActivity();

    void updateCount(int count);

    void UpdateProgress(PreviewFileEntity entity, long current, long total);

    void hidePreviewProgress();

    void showPreviewProgress(PreviewFileEntity entity);

    void showMobileDownloadAlert(CustomPopupWindow.OnPositiveClickListener onPositiveClickListener);

    void showLargeSizeDownloadAlert(long fileSize,CustomPopupWindow.OnPositiveClickListener onPositiveClickListener);
}
