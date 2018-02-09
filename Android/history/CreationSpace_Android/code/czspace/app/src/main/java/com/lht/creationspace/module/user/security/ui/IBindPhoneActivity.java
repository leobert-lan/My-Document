package com.lht.creationspace.module.user.security.ui;

import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.viewinterface
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> IBindPhoneActivity
 * <p><b>Description</b>: TODO
 * <p>Created by Administrator on 2016/8/31.
 */

public interface IBindPhoneActivity extends IActivityAsyncProtected {

    String getVerifyCode();

    String getOldVerifyCode();

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
