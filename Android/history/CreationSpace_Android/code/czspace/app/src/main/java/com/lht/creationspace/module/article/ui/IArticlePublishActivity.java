package com.lht.creationspace.module.article.ui;

import com.lht.creationspace.customview.popup.CustomPopupWindow;
import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;
import com.yalantis.ucrop.entity.LocalMedia;

import java.util.List;

/**
 * Created by chhyu on 2017/3/3.
 */

public interface IArticlePublishActivity extends IActivityAsyncProtected {
    void updateCurrentTitleLength(int currentCount);

    void updateSelectedImages(List<LocalMedia> selectedImages);

    void showDialog(int contentResId, int positiveBtnResId,
                    CustomPopupWindow.OnPositiveClickListener onPositiveClickListener);

    boolean hasCircleSet();

    String getCircleId();
}
