package com.lht.creationspace.module.msg.ui;

import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;

/**
 * Created by chhyu on 2017/3/3.
 */

public interface IMessageActivity extends IActivityAsyncProtected {

    void jump2CommentListActivity();

    void jump2RemindListActivity();

    void jump2NotificationListActivity();
}
