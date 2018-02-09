package com.lht.cloudjob.mvp.viewinterface;

import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.customwidgetlib.actionsheet.OnActionSheetItemClickListener;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.viewinterface
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IImagePreviewActivity
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/11/7.
 */

public interface IImagePreviewActivity extends IActivityAsyncProtected {

    /**
     * 显示下载询问操作表单
     * @param data 表单数据
     * @param listener 回调
     */
    void showDownloadAlertActionsheet(String[] data, OnActionSheetItemClickListener listener);

    void showMobileDownloadAlert(CustomPopupWindow.OnPositiveClickListener positiveClickListener);
}
