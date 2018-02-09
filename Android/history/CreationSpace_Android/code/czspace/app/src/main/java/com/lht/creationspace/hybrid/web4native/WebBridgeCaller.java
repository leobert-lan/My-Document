package com.lht.creationspace.hybrid.web4native;

import com.lht.lhtwebviewlib.BridgeWebView;

/**
 * Created by leobert on 2017/3/1.
 */

public class WebBridgeCaller {

    private final BridgeWebView bridgeWebView;

    private WebBridgeCaller(BridgeWebView bridgeWebView) {
        this.bridgeWebView = bridgeWebView;
    }

    public static WebBridgeCaller with(BridgeWebView bridgeWebView) {
        return new WebBridgeCaller(bridgeWebView);
    }

    public void call(IWebReq request) {
        bridgeWebView.callHandler(request.getReqWebHandlerName(), request.getReqData(),
                request.getOnWebRespNativeCallback());
    }

}
