package com.lht.cloudjob.mvp.viewinterface;

import android.app.Activity;

import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.cloudjob.mvp.model.pojo.PreviewFileEntity;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.viewinterface
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IDemandRequireFragment
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/9/5
 */
public interface IDemandRequireFragment extends IAsyncProtectedFragent {

    String getPublisher();

    Activity getActivity();

    void setPublisherFollowed(boolean isFollowed);

    void showPreviewProgress(PreviewFileEntity entity);

    void hidePreviewProgress();

    void UpdateProgress(PreviewFileEntity entity, long current, long total);

    void showMobileDownloadAlert(CustomPopupWindow.OnPositiveClickListener onPositiveClickListener);

    void showLargeSizeDownloadAlert(long fileSize,CustomPopupWindow.OnPositiveClickListener onPositiveClickListener);
}

