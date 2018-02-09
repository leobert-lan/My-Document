package com.lht.creationspace.module.setting.ui;

import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;
import com.lht.creationspace.customview.popup.CustomPopupWindow;
import com.yalantis.ucrop.entity.LocalMedia;

import java.util.List;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.viewinterface
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> IFeedbackAcitivty
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/5/11.
 */
public interface IFeedbackActivity extends IActivityAsyncProtected {
    void notifyOverLength();

    void showDialog(int contentResid, int positiveResid,
                    CustomPopupWindow.OnPositiveClickListener positiveClickListener);

    void updateSelectedMedias(List<LocalMedia> selectedMedias);
}
