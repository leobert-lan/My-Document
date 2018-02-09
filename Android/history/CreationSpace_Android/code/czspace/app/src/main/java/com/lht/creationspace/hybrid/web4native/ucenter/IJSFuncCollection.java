package com.lht.creationspace.hybrid.web4native.ucenter;

import com.lht.creationspace.hybrid.web4native.IWebReq;

/**
 * Created by leobert on 2017/3/1.
 */

/*local access public*/ interface IJSFuncCollection {

    interface IOpenUcenterBatchOpReq extends IWebReq {
        /**
         * API_NAME:调用web，刷新webUI，打开批量操作
         */
        String API_NAME = "APP_W_CZSPACE_UCENTER_BATCHOP_OPEN";
    }

    interface ICloseUcenterBatchOpReq extends IWebReq {
        /**
         * API_NAME:调用web，刷新UI，关闭批量操作
         */
        String API_NAME = "APP_W_CZSPACE_UCENTER_BATCHOP_CLOSE";
    }

}

