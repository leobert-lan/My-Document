package com.lht.creationspace.module.cache;

import java.io.File;

/**
 * <p><b>Package:</b> com.lht.creationspace.interfaces.cache </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> ICacheController </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/4/17.
 */

public interface ICacheController {

    void notifyUserChanged(String user);

    void registerCacheChangedListener(OnCacheChangedListener listener);


    File getSystemImageDir();

    File getLocalDownloadCacheDir();

    File getSystemDownloadDir();

    File getLocalThumbnailCacheDir();

    File getLocalPreviewCacheDir();

    /**
     * @return the bytes count that the current user used
     */
    long getUserLocalCacheSize();

    interface OnCacheChangedListener {
        void onCacheChanged(ICacheController cacheController);
    }
}