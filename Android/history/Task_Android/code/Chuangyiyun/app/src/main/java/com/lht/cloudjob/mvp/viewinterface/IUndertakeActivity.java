package com.lht.cloudjob.mvp.viewinterface;

import android.content.SharedPreferences;

import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.customwidgetlib.actionsheet.OnActionSheetItemClickListener;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.viewinterface
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IThrowLetterActivity
 * <p><b>Description</b>: TODO
 * <p>Created by Administrator on 2016/9/2.
 */

public interface IUndertakeActivity extends IActivityAsyncProtected {
    /**
     * 获取token相关的存储文件
     *
     * @return
     */
    SharedPreferences getTokenPreferences();

    void finishActivity();


    void showImageGetActionSheet(String[] data, OnActionSheetItemClickListener
            onActionSheetItemClickListener, boolean isBrokenable);

    void addWorkPicture(String imageFilePath);

    void showDialog(int contentResid, int positiveResid, CustomPopupWindow.OnPositiveClickListener
            onPositiveClickListener);

    void notifyOverLength();
}
