package com.lht.creationspace.module.user.register.ui;

import android.content.SharedPreferences;

import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;

/**
 * <p><b>Package:</b> com.lht.creationspace.mvp.viewinterface </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> IAccountCombineActivity </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/3/22.
 */

public interface IAccountCombineActivity extends IActivityAsyncProtected {
    SharedPreferences getTokenPreferences();
}
