package com.lht.cloudjob.native4js.impl;

import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.clazz.LoginIntentFactory;
import com.lht.cloudjob.interfaces.ITriggerCompare;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.mvp.model.bean.LoginResBean;
import com.lht.cloudjob.mvp.model.pojo.LoginInfo;
import com.lht.cloudjob.native4js.Native4JsExpandAPI;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;
import com.lht.lhtwebviewlib.base.model.BridgeNativeFunction;
import com.lht.lhtwebviewlib.business.bean.BaseResponseBean;
import com.lht.lhtwebviewlib.business.impl.ABSApiImpl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.lang.ref.WeakReference;

/**
 * <p><b>Package</b> com.lht.cloudjob.native4js.impl
 * <p><b>Project</b> Jsbridge_lib
 * <p><b>Classname</b> VsoLoginImpl
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/12/1.
 */

public class VsoLoginImpl extends ABSApiImpl implements Native4JsExpandAPI.VsoLoginHandler,IVerifyHolder {
    private final WeakReference<Context> contextRef;

    private CallBackFunction mFunction;

    private final String MSG_SUCCESS = "Vso Login success";

    private final String MSG_FAILURE = "Vso Login canceled";

    public VsoLoginImpl(Context context) {
        contextRef = new WeakReference<>(context);
        EventBus.getDefault().register(this);
    }

    @Override
    @Subscribe
    public void onEventMainThread(AppEvent.LoginSuccessEvent event) {
        mLoginInfo.copy(event.getLoginInfo());
        BaseResponseBean<LoginResBean> resBean= newSuccessResBean(mLoginInfo.getLoginResBean());
        if (mFunction == null) {
            DLog.e(Native4JsExpandAPI.class,API_NAME+"----callback is null");
            return;
        }
        mFunction.onCallBack(JSON.toJSONString(resBean));
    }


    @Subscribe
    public void onEventMainThread(AppEvent.RegisterBackgroundLoginSuccessEvent event) {
        mLoginInfo.copy(event.getLoginInfo());
        BaseResponseBean<LoginResBean> resBean= newSuccessResBean(mLoginInfo.getLoginResBean());
        if (mFunction == null) {
            DLog.e(Native4JsExpandAPI.class,API_NAME+"----callback is null");
            return;
        }
        mFunction.onCallBack(JSON.toJSONString(resBean));
    }

    /**
     * desc: 未进行登录的事件订阅
     *
     * @param event 手动关闭登录页事件
     */
    @Override
    @Subscribe
    public void onEventMainThread(AppEvent.LoginCancelEvent event) {
        mLoginInfo.copy(new LoginInfo());
        BaseResponseBean<LoginResBean> resBean= newFailureResBean(0,MSG_FAILURE);
        if (mFunction == null) {
            DLog.e(Native4JsExpandAPI.class,API_NAME+"----callback is null");
            return;
        }
        mFunction.onCallBack(JSON.toJSONString(resBean));
    }

    /*for test*/
//    public
    private BaseResponseBean<LoginResBean> newSuccessResBean(LoginResBean data) {
        BaseResponseBean<LoginResBean> bean = new BaseResponseBean<>();
        bean.setData(data);
        bean.setStatus(BaseResponseBean.STATUS_SUCCESS);
        bean.setMsg(MSG_SUCCESS);
        return bean;
    }

    /*for test*/
//    public
    private BaseResponseBean<LoginResBean> newFailureResBean(int ret, String msg) {
        BaseResponseBean<LoginResBean> bean = new BaseResponseBean<>();
        bean.setStatus(BaseResponseBean.STATUS_FAILURE);
        bean.setRet(ret);
        bean.setMsg(msg);
        return bean;
    }

    @Override
    public LoginInfo getLoginInfo() {
        return mLoginInfo;
    }

    @Override
    public void handler(String s, CallBackFunction callBackFunction) {
        mFunction = callBackFunction;
        Context ctx = contextRef.get();
        if (ctx == null) {
            DLog.i(Native4JsExpandAPI.class,"onHandler:"+API_NAME+"----context ref has bean gc");
            BaseResponseBean<LoginResBean> resBean = newFailureResBean(0,"context ref has bean gc");
            callBackFunction.onCallBack(JSON.toJSONString(resBean));
            return;
        }
        Intent intent = LoginIntentFactory.create(ctx, LoginTrigger.Bridge);
        ctx.startActivity(intent);
    }



    @Override
    protected boolean isBeanError(Object o) {
        return BEAN_IS_CORRECT;
    }

    @Override
    protected void finalize() throws Throwable {
        //unregister the subscription when GC
        EventBus.getDefault().unregister(this);
        super.finalize();
    }

    public enum LoginTrigger implements ITriggerCompare {
        Bridge(1);

        private final int tag;

        LoginTrigger(int i) {
            tag = i;
        }

        @Override
        public boolean equals(ITriggerCompare compare) {
            if (compare == null) {
                return false;
            }
            boolean b1 = compare.getClass().getName().equals(getClass().getName());
            boolean b2 = compare.getTag().equals(getTag());
            return b1 & b2;
        }

        @Override
        public Object getTag() {
            return tag;
        }

        @Override
        public Serializable getSerializable() {
            return this;
        }

    }

    public static BridgeNativeFunction newInstance(Context context) {
        return new BridgeNativeFunction(API_NAME,new VsoLoginImpl(context));
    }
}
