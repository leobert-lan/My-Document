package com.lht.creationspace.module.cache;

import android.os.Environment;

import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.util.string.StringUtil;

import java.io.File;

/**
 * <p><b>Package:</b> com.lht.creationspace.clazz.cache </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> CacheController </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/4/17.
 */

public class CacheController implements ICacheController {

    private final String localCacheRoot;

    //本地预览缓存目录
    private File localPreviewCacheDir = null;

    //本地缩略图缓存目录
    private File localThumbnailCacheDir = null;

    //本地下载目录
    private File localDownloadCacheDir = null;

    //系统相册目录
    private File systemImageDir = null;

    //系统下载目录
    private File systemDownloadDir = null;

    public CacheController(String localCacheRoot) {
        this.localCacheRoot = localCacheRoot;
        createOrLoadUserCache();
    }

    private String currentUser;

    private OnCacheChangedListener onCacheChangedListener;

    private static final String DEFAULT_USER = "default";

    @Override
    public void notifyUserChanged(String user) {
        if (StringUtil.isEmpty(user)) {
            currentUser = DEFAULT_USER;
        } else {
            currentUser = user;
        }
        releaseOldUserCache();
        createOrLoadUserCache();

        if (onCacheChangedListener != null)
            onCacheChangedListener.onCacheChanged(this);
    }

    private synchronized void releaseOldUserCache() {
        localDownloadCacheDir = null;
        localPreviewCacheDir = null;
        localThumbnailCacheDir = null;
    }

    private synchronized void createOrLoadUserCache() {
        localDownloadCacheDir = getLocalDownloadCacheDir();
        localPreviewCacheDir = getLocalPreviewCacheDir();
        localThumbnailCacheDir = getLocalThumbnailCacheDir();
        safeMkdirs(localDownloadCacheDir);
        safeMkdirs(localPreviewCacheDir);
        safeMkdirs(localThumbnailCacheDir);
    }

    public synchronized void reloadCache() {
        createOrLoadUserCache();
    }

    @Override
    public void registerCacheChangedListener(OnCacheChangedListener listener) {
        this.onCacheChangedListener = listener;
    }

    @Override
    public synchronized File getSystemImageDir() {
        if (systemImageDir == null) {
            final String path = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera";
            systemImageDir = new File(path);
        }
        return systemImageDir;
    }

    @Override
    public synchronized File getLocalDownloadCacheDir() {
        if (localDownloadCacheDir == null) {
            localDownloadCacheDir = new File(getLocalStorageRoot() + getCurrentUser() + "/download");
            safeMkdirs(localDownloadCacheDir);
        }
        return localDownloadCacheDir;
    }

    @Override
    public synchronized File getSystemDownloadDir() {
        if (systemDownloadDir == null) {
            final String path = Environment.getExternalStorageDirectory().getPath() + "/Download";
            systemDownloadDir = new File(path);
        }
        return systemDownloadDir;
    }

    @Override
    public File getLocalThumbnailCacheDir() {
        if (localThumbnailCacheDir == null) {
            localThumbnailCacheDir = new File(getLocalStorageRoot() + getCurrentUser() + "/thumbnail");
            safeMkdirs(localThumbnailCacheDir);
        }
        return localThumbnailCacheDir;
    }

    @Override
    public File getLocalPreviewCacheDir() {
        if (localPreviewCacheDir == null) {
            localPreviewCacheDir = new File(getLocalStorageRoot() + getCurrentUser() + "/preview");
            safeMkdirs(localPreviewCacheDir);
        }
        return localPreviewCacheDir;
    }

    private String getLocalStorageRoot() {
        return localCacheRoot;
    }

    /**
     * it is an sync operate!
     */
    @Override
    public synchronized long getUserLocalCacheSize() {
        File[] locals = new File[]{
                localDownloadCacheDir, localPreviewCacheDir, localThumbnailCacheDir
        };
        long total = 0;
        for (File f : locals) {
            total += getFileSize(f);
        }
        return total;
    }

    public String getCurrentUser() {
        if (StringUtil.isEmpty(currentUser)) {
            currentUser = getUsername();
        }
        return currentUser;
    }

    private String getUsername() {
        String usr = IVerifyHolder.mLoginInfo.getUsername();
        if (StringUtil.isEmpty(usr)) {
            return DEFAULT_USER;
        } else {
            return usr;
        }
    }

    private void safeMkdirs(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private long getFileSize(File f) {
        long size = 0;
        if (!f.exists())
            return size;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSize(flist[i]);
            } else {
                size = size + flist[i].length();
            }
        }
        return size;
    }
}
