package com.lht.creationspace.module.projchapter.ui;

import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;
import com.lht.creationspace.customview.popup.CustomPopupWindow;
import com.yalantis.ucrop.entity.LocalMedia;

import java.util.List;

/**
 * Created by chhyu on 2017/7/26.
 */

public interface IProjChapterPublishActivity extends IActivityAsyncProtected {

    void updateCurrentTitleLength(int currentCount);

    void updateSelectedImages(List<LocalMedia> selectedMedias);

    void showDialog(int contentResId, int positiveBtnResId,
                    CustomPopupWindow.OnPositiveClickListener onPositiveClickListener);
}
