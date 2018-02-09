package com.lht.creationspace.hybrid.web4native.ucenter;

import com.lht.creationspace.hybrid.web4native.AbsSimpleWebRequest;

/**
 * 打开用户中心模块-某页面的批量编辑状态
 * Created by leobert on 2017/3/1.
 */
public class UCenterOpenBatchOpReq extends AbsSimpleWebRequest
        implements IJSFuncCollection.IOpenUcenterBatchOpReq {
    @Override
    public String getReqWebHandlerName() {
        return API_NAME;
    }
}
