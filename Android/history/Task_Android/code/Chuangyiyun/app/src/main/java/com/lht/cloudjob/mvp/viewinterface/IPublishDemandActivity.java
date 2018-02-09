package com.lht.cloudjob.mvp.viewinterface;

import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.customwidgetlib.actionsheet.OnActionSheetItemClickListener;
import com.yalantis.ucrop.entity.LocalMedia;

import java.util.List;
import java.util.Set;

/**
 * Created by chhyu on 2017/2/6.
 */

public interface IPublishDemandActivity extends IActivityAsyncProtected {

    void notifyOverLength();

    void updateCurrentLength(int currentCount);

    void showPicSelectActionsheet(String[] data, OnActionSheetItemClickListener listener);

    void updateCurrentPicCount(int current, int max);

    void addFeedbackImage(String imageFilePath);

    void updateSelectedPic(List<LocalMedia> media, Set<LocalMedia> removed);

    void showDialog(int contentResid, int positiveResid,
                    CustomPopupWindow.OnPositiveClickListener positiveClickListener);

    void showDialog(int titleResId, int contentResid, int positiveResid,
                    CustomPopupWindow.OnPositiveClickListener positiveClickListener);
}
