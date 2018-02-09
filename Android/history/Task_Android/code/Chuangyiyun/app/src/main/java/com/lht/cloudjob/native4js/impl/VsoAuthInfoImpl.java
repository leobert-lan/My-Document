package com.lht.cloudjob.native4js.impl;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.mvp.model.pojo.LoginInfo;
import com.lht.cloudjob.native4js.Native4JsExpandAPI;
import com.lht.cloudjob.native4js.expandresbean.NF_VsoAuthInfoResBean;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;
import com.lht.lhtwebviewlib.base.model.BridgeNativeFunction;
import com.lht.lhtwebviewlib.business.bean.BaseResponseBean;
import com.lht.lhtwebviewlib.business.impl.ABSApiImpl;

/**
 * <p><b>Package</b> com.lht.cloudjob.native4js.impl
 * <p><b>Project</b> Jsbridge_lib
 * <p><b>Classname</b> VsoAuthInfoImpl
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/12/1.
 */

public class VsoAuthInfoImpl extends ABSApiImpl implements Native4JsExpandAPI.VsoAuthInfoHandler {

//    private static final String MSG_SUCCESS = "Success";
//
//    private static final String MSG_FAILURE = "Failure";

    @Override
    public void handler(String s, CallBackFunction callBackFunction) {
        LoginInfo loginInfo = IVerifyHolder.mLoginInfo;
        if (loginInfo.isLogin()) {
            NF_VsoAuthInfoResBean bean = new NF_VsoAuthInfoResBean();
            bean.setAuth_token(loginInfo.getAccessToken());
            bean.setAuth_username(loginInfo.getUsername());
            BaseResponseBean<NF_VsoAuthInfoResBean> responseBean = newSuccessResBean(bean);
            callBackFunction.onCallBack(JSON.toJSONString(responseBean));
        } else {
            BaseResponseBean responseBean = newFailureResBean(0, BaseResponseBean.MSG_DEFAULT);
            callBackFunction.onCallBack(JSON.toJSONString(responseBean));
        }

    }


    /*for test*/
//    public
    private BaseResponseBean<NF_VsoAuthInfoResBean> newSuccessResBean(NF_VsoAuthInfoResBean data) {
        BaseResponseBean<NF_VsoAuthInfoResBean> bean = new BaseResponseBean<>();
        bean.setData(data);
        bean.setStatus(BaseResponseBean.STATUS_SUCCESS);
        bean.setMsg(BaseResponseBean.MSG_DEFAULT);
        return bean;
    }

    /*for test*/
//    public
    private BaseResponseBean<NF_VsoAuthInfoResBean> newFailureResBean(int ret, String msg) {
        BaseResponseBean<NF_VsoAuthInfoResBean> bean = new BaseResponseBean<>();
        bean.setStatus(BaseResponseBean.STATUS_FAILURE);
        bean.setRet(ret);
        bean.setMsg(msg);
        bean.setData(new NF_VsoAuthInfoResBean()); // give an empty object rather than null
        return bean;
    }


    @Override
    protected boolean isBeanError(Object o) {
        return BEAN_IS_CORRECT;
    }

    public static BridgeNativeFunction newInstance() {
        return new BridgeNativeFunction(API_NAME,new VsoAuthInfoImpl());
    }
}
