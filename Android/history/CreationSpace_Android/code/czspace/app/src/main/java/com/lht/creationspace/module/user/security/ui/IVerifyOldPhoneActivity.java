package com.lht.creationspace.module.user.security.ui;

import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;

/**
 * Created by chhyu on 2017/2/22.
 */

public interface IVerifyOldPhoneActivity extends IActivityAsyncProtected {

    void initVCGetter(int s);

    void showCDRemaining(String s);

    void setVCGetterActiveStatus(boolean b);

//    void jump2BindPhoneActivity(String validCode);

}
