package com.lht.creationspace.base.keyback;

import android.support.annotation.NonNull;

import com.lht.creationspace.customview.popup.IPopupHolder;

import java.lang.ref.WeakReference;

/**
 * <p><b>Package</b> com.lht.vsocyy.clazz.keyback
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> PopupWinsCloseHandler
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2017/1/20.
 */

public class PopupWinsCloseHandler implements IKeyBackHandler {
    private final WeakReference<IPopupHolder> popupHolderRef;

    public PopupWinsCloseHandler(@NonNull IPopupHolder iPopupHolder) {
        this.popupHolderRef = new WeakReference<>(iPopupHolder);
    }

    @Override
    public boolean handle() {
        IPopupHolder iPopupHolder = popupHolderRef.get();
        if (iPopupHolder == null) {
            return false;
        } else if (!iPopupHolder.isPopupShowing()) {
            return false;
        } else {
            iPopupHolder.closePopupWindow();
            return true;
        }
    }
}
