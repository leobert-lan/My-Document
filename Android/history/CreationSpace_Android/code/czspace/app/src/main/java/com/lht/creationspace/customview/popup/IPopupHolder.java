package com.lht.creationspace.customview.popup;

import com.lht.creationspace.base.activity.BaseActivity;

/**
 * <p><b>Package</b> com.lht.vsocyy.interfaces.custompopupwins
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> IPopupHolder
 * <p><b>Description</b>: 持有者
 * Created by leobert on 2016/5/11.
 */
public interface IPopupHolder extends IPenetrateController{
    BaseActivity getHolderActivity();

    void setLatestPopupWindow(CustomPopupWindow latestPopupWindow);

    CustomPopupWindow getLatestPopupWindow();

    void onActionSheetDismiss();

    boolean isPopupShowing();

    void closePopupWindow();
}
