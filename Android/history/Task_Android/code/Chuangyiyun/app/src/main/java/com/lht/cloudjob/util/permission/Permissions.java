package com.lht.cloudjob.util.permission;

import com.lht.cloudjob.R;
import com.lht.cloudjob.customview.CustomDialog;
import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.cloudjob.interfaces.custompopupwins.IPopupHolder;
import com.lht.cloudjob.util.phonebasic.SettingUtil;

/**
 * <p><b>Package</b> com.lht.cloudjob.util.permission
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> Permissions
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/11/24.
 */

public enum Permissions implements IAlertCreator {
    CAMERA(R.string.v1022_dialog_permission_camera),

    STORAGE(R.string.v1022_dialog_permission_sd);

    private final int alertContentResId;

    Permissions(int alertContentResId) {
        this.alertContentResId = alertContentResId;
    }

    public CustomDialog newPermissionGrantReqAlert(final IPopupHolder iPopupHolder) {
        CustomDialog dialog = new CustomDialog(iPopupHolder);
        dialog.setContent(alertContentResId);
        dialog.setPositiveButton(R.string.v1022_dialog_permission_pbtn);
        dialog.setNegativeButton(R.string.v1022_dialog_permission_nbtn);


        dialog.setPositiveClickListener(new CustomPopupWindow.OnPositiveClickListener() {
            @Override
            public void onPositiveClick() {
                SettingUtil.startAppSettings(iPopupHolder.getHolderActivity());
            }
        });

        return dialog;
    }
}

interface IAlertCreator {
    CustomDialog newPermissionGrantReqAlert(IPopupHolder iPopupHolder);
}
