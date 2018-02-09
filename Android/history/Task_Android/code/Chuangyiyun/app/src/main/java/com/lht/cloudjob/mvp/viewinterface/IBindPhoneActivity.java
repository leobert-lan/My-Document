package com.lht.cloudjob.mvp.viewinterface;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.viewinterface
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IBindPhoneActivity
 * <p><b>Description</b>: TODO
 * <p>Created by Administrator on 2016/8/31.
 */

public interface IBindPhoneActivity extends IActivityAsyncProtected {

    String getVerifyCode();

    void showErrorMsg(String message);
    String getPhone();

    /**
     * 初始化get-verify-code button
     * @param resid string-res-id to display on button
     */
    void initVCGetter(int resid);

    void showCDRemaining(String formatTime);

    void setVCGetterActiveStatus(boolean isActive);

}
