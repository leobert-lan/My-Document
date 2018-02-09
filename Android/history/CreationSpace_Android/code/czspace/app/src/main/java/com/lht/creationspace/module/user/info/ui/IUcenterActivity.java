package com.lht.creationspace.module.user.info.ui;

import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;

/**
 * <p><b>Package:</b> com.lht.creationspace.mvp.viewinterface </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> IUcenterActivity </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/3/14.
 */

public interface IUcenterActivity extends IActivityAsyncProtected {
    void manualSetSubscribeState(boolean isSubscribed);

    void setSubscribeEnable(boolean enabled);
}
