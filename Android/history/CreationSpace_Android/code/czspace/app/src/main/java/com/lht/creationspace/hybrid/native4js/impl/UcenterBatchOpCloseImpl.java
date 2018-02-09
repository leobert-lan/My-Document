package com.lht.creationspace.hybrid.native4js.impl;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.hybrid.native4js.Native4JsExpandAPI;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;
import com.lht.lhtwebviewlib.base.model.BridgeNativeFunction;
import com.lht.lhtwebviewlib.business.bean.BaseResponseBean;
import com.lht.lhtwebviewlib.business.impl.ABSApiImpl;

import java.lang.ref.WeakReference;

/**
 * Created by chhyu on 2017/3/1.
 */

public class UcenterBatchOpCloseImpl extends ABSApiImpl implements Native4JsExpandAPI.UcenterBatchOpCloseHandler {

    private final WeakReference<OnUCenterBatchCloseListener> batchCloseListenerRef;

    public UcenterBatchOpCloseImpl(OnUCenterBatchCloseListener batchCloseListener) {
        this.batchCloseListenerRef = new WeakReference<>(batchCloseListener);
    }

    @Override
    protected boolean isBeanError(Object o) {
        return BEAN_IS_CORRECT;
    }

    @Override
    public void handler(String s, CallBackFunction callBackFunction) {
        BaseResponseBean<String> bean = new BaseResponseBean<>();
        bean.setData("success");
        bean.setStatus(BaseResponseBean.STATUS_SUCCESS);
        callBackFunction.onCallBack(JSON.toJSONString(bean));
    }

    public static BridgeNativeFunction newInstance(OnUCenterBatchCloseListener listener) {
        return new BridgeNativeFunction(API_NAME, new UcenterBatchOpCloseImpl(listener));
    }

    /**
     *
     */
    public interface OnUCenterBatchCloseListener {
        /**
         *
         */
        void onUCenterBatchClose();
    }
}
