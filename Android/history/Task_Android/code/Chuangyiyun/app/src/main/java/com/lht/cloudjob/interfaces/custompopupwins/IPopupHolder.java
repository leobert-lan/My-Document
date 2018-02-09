package com.lht.cloudjob.interfaces.custompopupwins;

import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.customview.CustomPopupWindow;

/**
 * <p><b>Package</b> com.lht.cloudjob.interfaces.custompopupwins
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IPopupHolder
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/5/11.
 */
public interface IPopupHolder extends IPenetrateController{
    UMengActivity getHolderActivity();

    void setLatestPopupWindow(CustomPopupWindow latestPopupWindow);

    void onActionSheetDismiss();
}
