package com.lht.creationspace.util.permission;

import com.lht.creationspace.R;
import com.lht.creationspace.customview.popup.dialog.CustomDialog;
import com.lht.creationspace.customview.popup.CustomPopupWindow;
import com.lht.creationspace.customview.popup.IPopupHolder;
import com.lht.creationspace.util.phonebasic.SettingUtil;

/**
 * <p><b>Package</b> com.lht.vsocyy.util.permission
 * <p><b>Project</b> VsoCyy
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
