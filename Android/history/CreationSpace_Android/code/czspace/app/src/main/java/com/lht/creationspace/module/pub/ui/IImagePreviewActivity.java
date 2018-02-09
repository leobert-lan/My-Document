package com.lht.creationspace.module.pub.ui;

import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;
import com.lht.creationspace.customview.share.ThirdPartySharePopWins;

import individual.leobert.uilib.actionsheet.OnActionSheetItemClickListener;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.viewinterface
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> IImagePreviewActivity
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/11/7.
 */

public interface IImagePreviewActivity extends IActivityAsyncProtected {

//    /**
//     * 显示下载询问操作表单
//     * @param data 表单数据
//     * @param listener 回调
//     */
//    void showDownloadAlertActionsheet(String[] data, OnActionSheetItemClickListener listener);

    void showMenuActionSheet(String[] data, OnActionSheetItemClickListener listener);

//    void showMobileDownloadAlert(CustomPopupWindow.OnPositiveClickListener positiveClickListener);

    void notifySaveSuccess();

    void notifySaveFailure();

    void showSharePopwins(ThirdPartySharePopWins.ImageShareData imageShareData);
}
