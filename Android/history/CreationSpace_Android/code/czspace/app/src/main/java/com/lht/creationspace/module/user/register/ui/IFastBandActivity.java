package com.lht.creationspace.module.user.register.ui;

import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;
import com.lht.creationspace.customview.popup.CustomPopupWindow;

/**
 * Created by chhyu on 2017/2/17.
 */

public interface IFastBandActivity extends IActivityAsyncProtected {

    void initVCGetter(int id);

    void showCDRemaining(String s);

    void setVCGetterActiveStatus(boolean b);

    void showPhoneConflictDialog(String message, String s, CustomPopupWindow.OnPositiveClickListener onPositiveClickListener);

    void jump2RoleChoose();

    //不需要跳转，直接关闭
//    void jump2LoginActivity();

}
