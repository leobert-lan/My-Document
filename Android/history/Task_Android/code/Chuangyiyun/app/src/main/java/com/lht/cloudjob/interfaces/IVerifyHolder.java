package com.lht.cloudjob.interfaces;

import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.mvp.model.pojo.LoginInfo;

import org.greenrobot.eventbus.Subscribe;

/**
 * <p><b>Package</b> com.lht.cloudjob.interfaces
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IVerifyHolder
 * <p><b>Description</b>: 注册订阅者规范接口，注意：不要覆盖对象。
 * Created by leobert on 2016/5/5.
 */
public interface IVerifyHolder {
    LoginInfo mLoginInfo = new LoginInfo();

    /**
     * desc: 主线程回调登录成功
     * 需要处理：成员对象的更新、界面的更新
     *
     * @param event 登录成功事件，包含信息
     */
    @Subscribe
    void onEventMainThread(AppEvent.LoginSuccessEvent event);

    /**
     * desc: 未进行登录的事件订阅
     *
     * @param event 手动关闭登录页事件
     */
    @Subscribe
    void onEventMainThread(AppEvent.LoginCancelEvent event);

    LoginInfo getLoginInfo();

    //    /**
//     * 设计用于更新cache或者db内容
//     * @param username
//     * @param accessId
//     * @param accessToken
//     */
//    void updateVerifyInfo(String username, String accessId, String accessToken);


}
