package com.lht.creationspace.module.pub.ui;

import android.content.SharedPreferences;

import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;


public interface ISplashActivity extends IActivityAsyncProtected {
    /**
     * 获取token相关的存储文件
     *
     * @return
     */
    SharedPreferences getTokenPreferences();

    /**
     * 获取app基础信息的存储文件
     *
     * @return
     */
    SharedPreferences getBasicPreferences();

    void jump2Main();

    void jump2Guide();

    void showErrorMsg(String msg);

    void finishActivity();
}
