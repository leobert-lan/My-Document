package com.lht.cloudjob.mvp.viewinterface;

import android.content.SharedPreferences;

import com.lht.cloudjob.mvp.model.pojo.LoginInfo;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.viewinterface
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> ILoginActivity
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/5/5.
 */
public interface ILoginActivity extends IActivityAsyncProtected{
    void jump2RegisterActivity();

    void jump2MainActivity(LoginInfo info);

    void jump2ResetPwdActivity();

    SharedPreferences getTokenPreferences();

    void finishActivity();

    Object getLoginTrigger();

    void showErrorMsg(String msg);

    void showRegisterGuideDialog();
}
