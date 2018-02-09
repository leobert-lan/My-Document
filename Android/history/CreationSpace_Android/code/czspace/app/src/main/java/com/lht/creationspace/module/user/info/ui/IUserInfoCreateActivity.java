package com.lht.creationspace.module.user.info.ui;

import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;
import com.lht.creationspace.customview.popup.CustomPopupWindow;

/**
 * Created by chhyu on 2017/2/20.
 */

public interface IUserInfoCreateActivity extends IActivityAsyncProtected {
    void jump2RoleSelect();


    void showDialog(int contentResid, int positiveResid, CustomPopupWindow.OnPositiveClickListener
            onPositiveClickListener);

    void updateLocalAvatar(String url);
}
