package com.lht.lhtwebviewlib.base;

import com.alibaba.fastjson.JSON;
import com.lht.lhtwebviewlib.base.Interface.BridgeHandler;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;
import com.lht.lhtwebviewlib.business.bean.BaseResponseBean;

/**
 * <p><b>Package</b> com.lht.lhtwebviewlib.base
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> NotFoundHandler
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/12/8.
 */

public class NotFoundHandler implements BridgeHandler {
    private static final int RET_404 = 404;

    @Override
    public void handler(String data, CallBackFunction function) {
        BaseResponseBean<String> responseBean = new BaseResponseBean<>();
        responseBean.setRet(RET_404);
        responseBean.setStatus(BaseResponseBean.STATUS_FAILURE);
        String msg = "NoHandlerName:" + data;
        responseBean.setMsg(msg);
        function.onCallBack(JSON.toJSONString(responseBean));
    }
}
