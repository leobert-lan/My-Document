package com.lht.creationspace.hybrid.web4native;

import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;

/**
 * 基础请求，只调用方法，无参数，无回调
 * Created by leobert on 2017/3/1.
 */

public abstract class AbsSimpleWebRequest implements IWebReq {

    @Override
    public String getReqData() {
        return null;
    }

    @Override
    public CallBackFunction getOnWebRespNativeCallback() {
        return null;
    }
}
