package com.lht.creationspace.hybrid.web4native.ucenter;

import com.lht.creationspace.hybrid.web4native.AbsSimpleWebRequest;

/**
 * 关闭用户中心模块-某页面的批量编辑状态
 * Created by leobert on 2017/3/1.
 */
public class UcenterCloseBatchOpReq extends AbsSimpleWebRequest
        implements IJSFuncCollection.ICloseUcenterBatchOpReq {
    @Override
    public String getReqWebHandlerName() {
        return API_NAME;
    }
}
