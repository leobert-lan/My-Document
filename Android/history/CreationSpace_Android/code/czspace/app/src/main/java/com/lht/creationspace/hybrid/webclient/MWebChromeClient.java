package com.lht.creationspace.hybrid.webclient;

import android.support.annotation.NonNull;
import android.webkit.WebView;

import com.lht.lhtwebviewlib.FileChooseBridgeWebChromeClient;
import com.lht.lhtwebviewlib.base.Interface.IFileChooseSupport;

import java.lang.ref.WeakReference;

/**
 * <p><b>Package:</b> com.lht.creationspace.webclient </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> MWebChromeClient </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/3/28.
 */

public class MWebChromeClient extends FileChooseBridgeWebChromeClient {

    private final WeakReference<OnWebReceivedTitleListener> onWebReceivedRef;

    public MWebChromeClient(@NonNull IFileChooseSupport iFileChooseSupport,
                            OnWebReceivedTitleListener onWebReceivedTitleListener) {
        super(iFileChooseSupport);
        onWebReceivedRef = new WeakReference<>(onWebReceivedTitleListener);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        if (onWebReceivedRef.get() != null)
            onWebReceivedRef.get().onWebViewReceivedTitle(view, title);
    }
}
