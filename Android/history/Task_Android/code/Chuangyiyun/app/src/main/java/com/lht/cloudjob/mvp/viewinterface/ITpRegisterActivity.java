package com.lht.cloudjob.mvp.viewinterface;

import com.lht.cloudjob.mvp.model.pojo.LoginInfo;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.viewinterface
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> ITpRegisterActivity
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/9/23
 */
public interface ITpRegisterActivity extends IActivityAsyncProtected {

    Object getLoginTrigger();

//    void jump2MainActivity(LoginInfo info);

    void jump2SubscribeActivity(LoginInfo verifyInfo);
}
