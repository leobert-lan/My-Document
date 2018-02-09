package com.lht.creationspace.hybrid.web4native;

import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;

/**
 * Created by leobert on 2017/3/1.
 */

public interface IWebReq {
    /**
     * web方法名
     */
    String getReqWebHandlerName();

    /**
     * 所需参数，已序列化
     */
    String getReqData();

    /**
     * web响应后的callback
     */
    CallBackFunction getOnWebRespNativeCallback();
}
