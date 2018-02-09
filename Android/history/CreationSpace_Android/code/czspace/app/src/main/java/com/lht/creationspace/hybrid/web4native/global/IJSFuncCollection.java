package com.lht.creationspace.hybrid.web4native.global;

import com.lht.creationspace.hybrid.web4native.IWebReq;

/**
 * <p><b>Package:</b> com.lht.creationspace.web4native.project </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> IJSFunctionCollection </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/3/9.
 */

interface IJSFuncCollection {

    interface IGetPageShareData extends IWebReq {

        /**
         * API_NAME:获取页面分享的分享数据
         */
        String API_NAME = "APP_W_PUBLIC_GETSHAREDATA";
    }
}
