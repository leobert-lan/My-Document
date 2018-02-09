package com.lht.cloudjob.mvp.viewinterface;

import android.content.SharedPreferences;

/**
 * @author leobert.lan
 * @version 1.0
 * @date 2016/4/25
 */
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

    void jump2Main(boolean isLogined);

    void jump2Guide();

    void showErrorMsg(String msg);

    void finishActivity();
}
