package com.lht.cloudjob.mvp.viewinterface;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.viewinterface
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IVerifyPhoneActivity
 * <p><b>Description</b>: TODO
 * <p>Created by Administrator on 2016/8/31.
 */

public interface IVerifyPhoneActivity extends IActivityAsyncProtected {

    void showErrorMsg(String message);

    String getVerifyCode();

    /**
     * 初始化get-verify-code button
     * @param resid string-res-id to display on button
     */
    void initVCGetter(int resid);

    void showCDRemaining(String formatTime);

    void setVCGetterActiveStatus(boolean isActive);

    void jump2BindPhone();
}
