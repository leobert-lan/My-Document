package com.lht.creationspace.hybrid.native4js.impl;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.module.topic.ui.HybridCircleDetailActivity;
import com.lht.creationspace.hybrid.native4js.Native4JsExpandAPI;
import com.lht.creationspace.hybrid.native4js.expandreqbean.NF_CircleDetailNavigateReqBean;
import com.lht.creationspace.util.debug.DLog;
import com.lht.creationspace.util.string.StringUtil;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;
import com.lht.lhtwebviewlib.base.model.BridgeNativeFunction;
import com.lht.lhtwebviewlib.business.bean.BaseResponseBean;
import com.lht.lhtwebviewlib.business.impl.ABSApiImpl;

/**
 * Created by leobert on 2017/3/1.
 */

public class CircleDetailNavigateImpl extends ABSApiImpl<NF_CircleDetailNavigateReqBean>
        implements Native4JsExpandAPI.CircleDetailNavigateHandler {

    public CircleDetailNavigateImpl(Context ctx) {
        super(ctx);
    }

    @Override
    public void handler(String s, CallBackFunction callBackFunction) {
        try {
            NF_CircleDetailNavigateReqBean bean = JSON.parseObject(s, NF_CircleDetailNavigateReqBean.class);
            boolean b = isBeanError(bean);
            if (b) {
                if (callBackFunction != null)
                    callBackFunction.onCallBack(JSON.toJSONString(newFailureResBean()));
                DLog.d(getClass(), "跳转参数异常：" + s);
            } else {
                HybridCircleDetailActivity.getLauncher(mContext)
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

    private HybridCircleDetailActivity.CircleDetailActivityData transData(NF_CircleDetailNavigateReqBean bean) {
        HybridCircleDetailActivity.CircleDetailActivityData data = new HybridCircleDetailActivity.CircleDetailActivityData();
        data.setUrl(bean.getUrl());
        data.setTitle(bean.getTitle());
        data.setOid(bean.getOid());
        return data;
    }


    /*for test*/
//    public
    private BaseResponseBean<String> newSuccessResBean() {
        BaseResponseBean<String> bean = new BaseResponseBean<>();
        bean.setData(null);
        bean.setStatus(BaseResponseBean.STATUS_SUCCESS);
        bean.setMsg(BaseResponseBean.MSG_DEFAULT);
        return bean;
    }

    /*for test*/
//    public
    private BaseResponseBean<String> newFailureResBean() {
        BaseResponseBean<String> bean = new BaseResponseBean<>();
        bean.setData(null);
        bean.setStatus(BaseResponseBean.STATUS_FAILURE);
        bean.setMsg(BaseResponseBean.MSG_DEFAULT);
        return bean;
    }


    @Override
    protected boolean isBeanError(NF_CircleDetailNavigateReqBean bean) {
//        NF_CircleDetailRedirectReqBean bean = (NF_CircleDetailRedirectReqBean) o;
        if (StringUtil.isEmpty(bean.getUrl())
                || StringUtil.isEmpty(bean.getOid()))
            return BEAN_IS_ERROR;
        return BEAN_IS_CORRECT;
    }

    public static BridgeNativeFunction newInstance(Context context) {
        return new BridgeNativeFunction(API_NAME, new CircleDetailNavigateImpl(context));
    }
}
