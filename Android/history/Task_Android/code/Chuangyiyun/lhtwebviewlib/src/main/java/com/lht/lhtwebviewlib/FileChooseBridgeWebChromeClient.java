package com.lht.lhtwebviewlib;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.lht.lhtwebviewlib.base.Interface.IFileChooseSupport;

/**
 * <p><b>Package</b> com.lht.lhtwebviewlib
 * <p><b>Project</b> Jsbridge_lib
 * <p><b>Classname</b> FileChooseBridgeWebChromeClient
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/12/6.
 */

public class FileChooseBridgeWebChromeClient extends BridgeWebChromeClient implements IFileChooseSupport{

    private final IFileChooseSupport iFileChooseSupport;

    public FileChooseBridgeWebChromeClient(@NonNull IFileChooseSupport iFileChooseSupport) {
        this.iFileChooseSupport = iFileChooseSupport;
    }

    @Override
    public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
        iFileChooseSupport.openFileChooser(uploadMsg, acceptType);
    }

    @Override
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        iFileChooseSupport.openFileChooser(uploadMsg, acceptType,capture);
    }

    @Override
    public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback,
                              WebChromeClient.FileChooserParams fileChooserParams){
        return iFileChooseSupport.onShowFileChooser(mWebView,filePathCallback,fileChooserParams);
    }

    @Override
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        iFileChooseSupport.openFileChooser(uploadMsg);
    }
}
