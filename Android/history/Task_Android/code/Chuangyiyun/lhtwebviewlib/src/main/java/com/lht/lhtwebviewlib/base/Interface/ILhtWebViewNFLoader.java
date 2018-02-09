package com.lht.lhtwebviewlib.base.Interface;

import com.lht.lhtwebviewlib.base.model.BridgeNativeFunction;

/**
 * <p><b>Package</b> com.lht.lhtwebviewlib.base.Interface
 * <p><b>Project</b> Jsbridge_lib
 * <p><b>Classname</b> ILhtWebViewNFLoader
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/12/2.
 */

public interface ILhtWebViewNFLoader {
    ILhtWebViewNFLoader equip(BridgeNativeFunction nativeFunction);

    void load();
}
