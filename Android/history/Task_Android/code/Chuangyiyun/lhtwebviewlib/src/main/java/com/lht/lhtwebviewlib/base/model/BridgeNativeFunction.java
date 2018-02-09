package com.lht.lhtwebviewlib.base.model;

import com.lht.lhtwebviewlib.base.Interface.BridgeHandler;

/**
 * <p><b>Package</b> com.lht.lhtwebviewlib.base.model
 * <p><b>Project</b> Jsbridge_lib
 * <p><b>Classname</b> BridgeNativeFunction
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/12/2.
 */

public class BridgeNativeFunction {
    private final String mNativeFunctionName;

    private final BridgeHandler mHandler;

    public BridgeNativeFunction(String mNativeFunctionName, BridgeHandler mHandler) {
        this.mNativeFunctionName = mNativeFunctionName;
        this.mHandler = mHandler;
    }

    public String getNativeFunctionName() {
        return mNativeFunctionName;
    }

    public BridgeHandler getHandler() {
        return mHandler;
    }
}
