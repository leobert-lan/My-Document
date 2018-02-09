package com.lht.creationspace.base.activity;

import android.app.Activity;

import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.base.keyback.PopupWinsCloseHandler;
import com.lht.creationspace.customview.popup.CustomPopupWindow;
import com.lht.creationspace.customview.popup.IPopupHolder;

/**
 * <p><b>Package</b> com.lht.vsocyy.activity
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> ExclusivePopupWinsActivity
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2017/1/20.
 */

public abstract class ExclusivePopupWinsActivity extends BaseActivity
        implements IPopupHolder {

    protected CustomPopupWindow exclusivePopupWins;

    @Override
    public BaseActivity getHolderActivity() {
        return getActivity();
    }

    @Override
    protected void equipKeyBackHandlerChain() {
        getIKeyBackHandlerChain().next(new PopupWinsCloseHandler(this));
        super.equipKeyBackHandlerChain();
    }

    @Override
    public void onActionSheetDismiss() {
    }

    @Override
    public void setLatestPopupWindow(CustomPopupWindow latestPopupWindow) {
        this.exclusivePopupWins = latestPopupWindow;
    }

    @Override
    public CustomPopupWindow getLatestPopupWindow() {
        return exclusivePopupWins;
    }

    @Override
    public void setPenetrable(Activity activity, boolean isProtectNeed) {
        if (activity instanceof AsyncProtectedActivity) {
            ((AsyncProtectedActivity) activity).setActiveStateOfDispatchOnTouch(!isProtectNeed);
        }
    }

    @Override
    public boolean isPopupShowing() {
        if (exclusivePopupWins == null)
            return false;
        return exclusivePopupWins.isShowing();
    }

    @Override
    public void closePopupWindow() {
        if (exclusivePopupWins != null) {
            exclusivePopupWins.dismiss();
            exclusivePopupWins = null;
        }
    }
}
