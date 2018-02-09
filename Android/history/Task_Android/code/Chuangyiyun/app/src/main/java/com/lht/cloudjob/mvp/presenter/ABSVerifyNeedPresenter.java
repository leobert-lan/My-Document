package com.lht.cloudjob.mvp.presenter;

import com.lht.cloudjob.interfaces.ITriggerCompare;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> ABSVerifyNeedPresenter
 * <p><b>Description</b>: 该抽象类定义了登录相关的内容，需要区分登录未登录的页面的presenter需要继承之，
 * 登录状态更新部分在Activity、Fragment中实现，若确实出现了共同的处理，修改模板方法并在此处实现共同部分
 * <p/>
 * 注意，只有会触发登录的页面的P才有必要继承
 * <p/>
 * Created by leobert on 2016/5/5.
 */
public abstract class ABSVerifyNeedPresenter {

    /**
     * desc: 页面接收到订阅事件后，调用presenter#identifyTrigger，执行逻辑，需要区分触发事件是不是登录事件
     *
     * @param trigger an interface to identify trigger,use equal(ITriggerCompare compare)
     */
    public abstract void identifyTrigger(ITriggerCompare trigger);

    /**
     * desc: 登录可能被用户手动取消，此时发出LoginCancelEvent，订阅的页面需要特殊处理<p>
     * 并不是所有的登录相关页面都需要处理该逻辑，所以#ABSVerifyNeedPresenter中进行空实现
     *
     * @param trigger an interface to identify trigger,use equal(ITriggerCompare compare)
     */
    public void identifyCanceledTrigger(ITriggerCompare trigger) {

    }

    /**
     * desc: check if login
     *
     * @return true while login,false otherwise
     */
    protected abstract boolean isLogin();

    /**
     * desc: update status,implement the method with an appropriate design
     *
     * @param isLogin
     */
    public abstract void setLoginStatus(boolean isLogin);



}
