package com.lht.lhtwebviewlib.base;

import android.util.Log;

import com.lht.lhtwebviewlib.BridgeWebView;
import com.lht.lhtwebviewlib.base.Interface.ILhtWebViewNFLoader;
import com.lht.lhtwebviewlib.base.model.BridgeNativeFunction;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.lhtwebviewlib.base
 * <p><b>Project</b> Jsbridge_lib
 * <p><b>Classname</b> LhtWebViewNFLoader
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/12/2.
 */

public class LhtWebViewNFLoader implements ILhtWebViewNFLoader {

    private static final String TAG = "LhtWebViewNFLoader";

    private final BridgeWebView bridgeWebView;

    private final ArrayList<BridgeNativeFunction> functions;

    private LhtWebViewNFLoader(BridgeWebView bridgeWebView) {
        this.bridgeWebView = bridgeWebView;
        functions = new ArrayList<>();
    }

    public static ILhtWebViewNFLoader with(BridgeWebView bridgeWebView) {
        //不要使用单例
        return new LhtWebViewNFLoader(bridgeWebView);
    }

    public BridgeWebView getBridgeWebView() {
        return bridgeWebView;
    }

    @Override
    public ILhtWebViewNFLoader equip(BridgeNativeFunction nativeFunction) {
        functions.add(nativeFunction);
        return this;
    }

    @Override
    public void load() {
        Log.d(TAG, "load native functions,size:" + functions.size() + getWebViewDebugInfo());
        for (int i = 0; i < functions.size(); i++) {
            BridgeNativeFunction function = functions.get(i);
            Log.d(TAG, "load native function:" + function.getNativeFunctionName() + getWebViewDebugInfo());
            bridgeWebView.registerHandler(function.getNativeFunctionName(), function.getHandler());
        }
        Log.d(TAG, "load native functions complete" + getWebViewDebugInfo());
    }

    private String getWebViewDebugInfo() {
        if (bridgeWebView != null) {
            return "\rbridgeWebView id is:" + bridgeWebView.getId();
        } else {
            return "\rbridgeWebView id is null";
        }
    }
}
