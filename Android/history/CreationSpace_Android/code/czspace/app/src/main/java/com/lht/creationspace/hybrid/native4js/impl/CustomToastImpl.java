package com.lht.creationspace.hybrid.native4js.impl;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.customview.toast.HeadUpToast;
import com.lht.creationspace.hybrid.native4js.Native4JsExpandAPI;
import com.lht.creationspace.hybrid.native4js.expandreqbean.NF_CustomToastReqBean;
import com.lht.creationspace.util.debug.DLog;
import com.lht.creationspace.util.string.StringUtil;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;
import com.lht.lhtwebviewlib.base.model.BridgeNativeFunction;
import com.lht.lhtwebviewlib.business.bean.BaseResponseBean;
import com.lht.lhtwebviewlib.business.impl.ABSApiImpl;

/**
 * Created by leobert on 2017/3/1.
 */

public class CustomToastImpl extends ABSApiImpl<NF_CustomToastReqBean>
        implements Native4JsExpandAPI.CustomToastHandler {

    public CustomToastImpl(Context ctx) {
        super(ctx);
    }

    @Override
    public void handler(String s, CallBackFunction callBackFunction) {
        try {
            NF_CustomToastReqBean bean = JSON.parseObject(s, NF_CustomToastReqBean.class);
            boolean b = isBeanError(bean);
            if (b) {
                if (callBackFunction != null)
                    callBackFunction.onCallBack(JSON.toJSONString(newFailureResBean()));
                DLog.e(getClass(), "自定义toast未包含文字内容：" + s);
            } else {
                HeadUpToast headUpToast = new HeadUpToast(mContext);
                headUpToast.setContent(bean.getType(),bean.getContent());
                headUpToast.show();
                if (callBackFunction != null)
                    callBackFunction.onCallBack(JSON.toJSONString(newSuccessResBean()));
            }

        } catch (Exception e) {
            if (callBackFunction != null)
                callBackFunction.onCallBack(JSON.toJSONString(newFailureResBean()));
        }
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
        bean.setData("请求参数异常");
        bean.setStatus(BaseResponseBean.STATUS_FAILURE);
        bean.setMsg(BaseResponseBean.MSG_DEFAULT);
        return bean;
    }


    @Override
    protected boolean isBeanError(NF_CustomToastReqBean bean) {
//        NF_CustomToastReqBean bean = (NF_CustomToastReqBean) o;
        if (StringUtil.isEmpty(bean.getContent()))
            return BEAN_IS_ERROR;
        return BEAN_IS_CORRECT;
    }

    public static BridgeNativeFunction newInstance(Context context) {
        return new BridgeNativeFunction(API_NAME, new CustomToastImpl(context));
    }
}
