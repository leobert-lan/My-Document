package com.lht.creationspace.hybrid.web4native.circle;

import com.lht.creationspace.hybrid.web4native.IWebReq;

/**
 * <p><b>Package:</b> com.lht.creationspace.web4native.project </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> IJSFunctionCollection </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/3/9.
 */

interface IJSFuncCollection {

    interface INotifyCircleJoined extends IWebReq {

        /**
         * API_NAME:通知圈子已加入
         */
        String API_NAME = "APP_W_CZSPACE_CIRCLE_JOINED";

    }
}
