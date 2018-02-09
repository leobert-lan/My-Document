package com.lht.creationspace.hybrid.native4js.impl;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.module.proj.ui.HybridProjectDetailActivity;
import com.lht.creationspace.hybrid.native4js.Native4JsExpandAPI;
import com.lht.creationspace.hybrid.native4js.expandreqbean.NF_ProjectDetailNavigateReqBean;
import com.lht.creationspace.util.string.StringUtil;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;
import com.lht.lhtwebviewlib.base.model.BridgeNativeFunction;
import com.lht.lhtwebviewlib.business.bean.BaseResponseBean;
import com.lht.lhtwebviewlib.business.impl.ABSApiImpl;

/**
 * Created by chhyu on 2017/3/2.
 */

public class ProjectDetailNavigateImpl extends ABSApiImpl<NF_ProjectDetailNavigateReqBean> implements
        Native4JsExpandAPI.ProjectDetailNavigateHandler {
    public ProjectDetailNavigateImpl(Context context) {
        super(context);
    }

    @Override
    public void handler(String s, CallBackFunction callBackFunction) {
        try {
            NF_ProjectDetailNavigateReqBean bean =
                    JSON.parseObject(s, NF_ProjectDetailNavigateReqBean.class);
            boolean b = isBeanError(bean);
            if (b) {
                if (callBackFunction != null) {
                    callBackFunction.onCallBack(JSON.toJSONString(newFailureResBean()));
                }
            } else {
                HybridProjectDetailActivity.getLauncher(mContext)
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

    private HybridProjectDetailActivity.LaunchData transData(NF_ProjectDetailNavigateReqBean bean) {
        HybridProjectDetailActivity.LaunchData data = new HybridProjectDetailActivity.LaunchData();
        data.setUrl(bean.getUrl());
        data.setOid(bean.getOid());
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
    protected boolean isBeanError(NF_ProjectDetailNavigateReqBean bean) {
//        NF_ProjectDetailRedirectReqBean bean = (NF_ProjectDetailRedirectReqBean) o;
        if (StringUtil.isEmpty(bean.getUrl()) || StringUtil.isEmpty(bean.getOid()))
            return BEAN_IS_ERROR;
        return BEAN_IS_CORRECT;
    }

    public static BridgeNativeFunction newInstance(Context context) {

        return new BridgeNativeFunction(API_NAME, new ProjectDetailNavigateImpl(context));
    }
}
