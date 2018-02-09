package com.lht.cloudjob.mvp.model.pojo;

import android.net.Uri;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.pojo
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> CaptureUriCompat
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2017/2/22.
 */

public class CaptureUriCompat {
    final Uri uri;
    final String filePath;

    public CaptureUriCompat(Uri uri, String filePath) {
        this.uri = uri;
        this.filePath = filePath;
    }

    public Uri getUri() {
        return uri;
    }

    public String getFilePath() {
        return filePath;
    }
}
