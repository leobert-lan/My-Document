package com.lht.lhtwebviewapi.business.API;


import com.lht.lhtwebviewlib.base.Interface.BridgeHandler;

/**
 * @author leobert.lan
 * @version 1.0
 * @ClassName: API
 * @Description: TODO
 * @date 2016年2月17日 下午3:40:49
 */
public interface API {

    interface Demo extends BridgeHandler {
        /**
         * API_NAME:测试
         */
        String API_NAME = "NATIVE_FUNCTION_DEMO";
    }

    @Deprecated
    interface openFile extends BridgeHandler {
        /**
         * API_NAME:TODO 测试打开文件
         */
        String API_NAME = "openFile";
    }

    interface GPSHandler extends BridgeHandler {
        /**
         * API_NAME:定位，优先GPS
         */
        String API_NAME = "NATIVE_FUNCTION_OPENGPS";
    }

    interface TestLTRHandler extends BridgeHandler {
        /**
         * API_NAME:TODO 耗时任务测试接口
         */
        String API_NAME = "NATIVE_FUNCTION_LTDEMO";
    }

    interface CallTelHandler extends BridgeHandler {

        /**
         * API_NAME:拨号
         */
        String API_NAME = "APP_N_TOOLS_CALLTEL";
    }

    interface SendToClipBoardHandler extends BridgeHandler {
        /**
         * API_NAME:发送内容到剪切板
         */
        String API_NAME = "NATIVE_FUNCTION_SENDTOCLIPBOARD";
    }

    interface GetClipBoardContentHandler extends BridgeHandler {
        /**
         * API_NAME:获取剪切板内容
         */
        String API_NAME = "NATIVE_FUNCTION_GETFROMCLIPBOARD";
    }

    /**
     * 发送短信
     */
    interface SendMessageHandler extends BridgeHandler {
        /**
         * API_NAME:发送短息
         */
        String API_NAME = "APP_N_TOOLS_SEND_MESSAGE";
    }

    interface SendEmailHandler extends BridgeHandler {
        /**
         * API_NAME:发送邮件
         */
        String API_NAME = "NATIVE_FUNCTION_SENDEMAIL";
    }

    interface ScanCodeHandler extends BridgeHandler {
        /**
         * API_NAME:扫码
         */
        String API_NAME = "NATIVE_FUNCTION_OPENCAMERA_SCAN";
    }
}
