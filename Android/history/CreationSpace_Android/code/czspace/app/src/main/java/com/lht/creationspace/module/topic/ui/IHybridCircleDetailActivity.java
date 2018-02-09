package com.lht.creationspace.module.topic.ui;

import com.lht.creationspace.customview.popup.CustomPopupWindow;
import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;
import com.lht.creationspace.hybrid.webclient.LhtWebviewClient;

/**
 * Created by chhyu on 2017/3/31.
 */

public interface IHybridCircleDetailActivity extends IActivityAsyncProtected {
    void jump2ArticlePublishActivity();


    void checkUserJoinState();

    void showJoinCircleAlert(CustomPopupWindow.OnPositiveClickListener listener);

    void notifyCircleJoined(LhtWebviewClient.OnLoadFinishListener listener);
}
