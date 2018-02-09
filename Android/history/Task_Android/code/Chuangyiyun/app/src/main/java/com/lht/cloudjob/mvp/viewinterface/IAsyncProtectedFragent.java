package com.lht.cloudjob.mvp.viewinterface;

import android.content.res.Resources;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.viewinterface
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IAsyncProtectedFragent
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/8/23
 */
public interface IAsyncProtectedFragent {
    /**
     * desc: 显示等待窗
     *
     * @param isProtectNeed 是否需要屏幕防击穿保护
     */
    void showWaitView(boolean isProtectNeed);

    /**
     * desc: 取消等待窗
     */
    void cancelWaitView();

    Resources getAppResource();

    void showMsg(String msg);

    void showErrorMsg(String msg);
}
