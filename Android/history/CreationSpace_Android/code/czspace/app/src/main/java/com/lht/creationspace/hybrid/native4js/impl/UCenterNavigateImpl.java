package com.lht.creationspace.hybrid.native4js.impl;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.module.user.info.ui.ac.HybridUCenterActivity;
import com.lht.creationspace.hybrid.native4js.Native4JsExpandAPI;
import com.lht.creationspace.hybrid.native4js.expandreqbean.NF_UCenterReqBean;
import com.lht.creationspace.util.string.StringUtil;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;
import com.lht.lhtwebviewlib.base.model.BridgeNativeFunction;
import com.lht.lhtwebviewlib.business.bean.BaseResponseBean;
import com.lht.lhtwebviewlib.business.impl.ABSApiImpl;


/**
 * 用户中心跳转
 */
public class UCenterNavigateImpl extends ABSApiImpl<NF_UCenterReqBean> implements
        Native4JsExpandAPI.UCenterNavigateHandler {
    public UCenterNavigateImpl(Context context) {
        super(context);
    }

    @Override
    public void handler(String s, CallBackFunction callBackFunction) {
        try {
            NF_UCenterReqBean bean = JSON.parseObject(s, NF_UCenterReqBean.class);
            boolean b = isBeanError(bean);
            if (b) {
                if (callBackFunction != null) {
                    callBackFunction.onCallBack(JSON.toJSONString(newFailureResBean()));
                }
            } else {
                HybridUCenterActivity.getLauncher(mContext)
                        .injectData(transData(bean))
                        .launch();
                if (callBackFunction != null)
                    callBackFunction.onCallBack(JSON.toJSONString(newSuccessResBean()));
            }

        } catch (Exception e) {
            if (callBackFunction != null)
                callBackFunction.onCallBack(JSON.toJSONString(newFailureResBean()));
        }
    }

    private HybridUCenterActivity.HybridUCenterActivityData transData(NF_UCenterReqBean bean) {
        HybridUCenterActivity.HybridUCenterActivityData data = new HybridUCenterActivity.HybridUCenterActivityData();
        data.setUrl(bean.getUrl());
        data.setTargetUser(bean.getTarget_user());
        return data;
    }

    private BaseResponseBean<String> newFailureResBean() {
        BaseResponseBean<String> bean = new BaseResponseBean<>();
        bean.setData(null);
        bean.setStatus(BaseResponseBean.STATUS_FAILURE);
        bean.setMsg(BaseResponseBean.MSG_DEFAULT);
        return bean;
    }

    private BaseResponseBean<String> newSuccessResBean() {
        BaseResponseBean<String> bean = new BaseResponseBean<>();
        bean.setData(null);
        bean.setStatus(BaseResponseBean.STATUS_SUCCESS);
        bean.setMsg(BaseResponseBean.MSG_DEFAULT);
        return bean;
    }

    @Override
    protected boolean isBeanError(NF_UCenterReqBean bean) {
//        NF_UCenterReqBean bean = (NF_UCenterReqBean) o;
        if (StringUtil.isEmpty(bean.getUrl())
                || StringUtil.isEmpty(bean.getTarget_user()))
            return BEAN_IS_ERROR;
        return BEAN_IS_CORRECT;
    }

    public static BridgeNativeFunction newInstance(Context context) {

        return new BridgeNativeFunction(API_NAME, new UCenterNavigateImpl(context));
    }
}
