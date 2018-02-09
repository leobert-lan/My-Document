package com.lht.creationspace.module.pub.model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import com.lht.creationspace.base.model.IFileDownloadCallbacks;
import com.lht.creationspace.base.model.pojo.DownloadEntity;
import com.lht.creationspace.util.internet.HttpUtil;
import com.lht.creationspace.util.time.TimeUtil;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;

import org.apache.http.Header;

import java.io.File;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.model
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> DownloadModel
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/11/10.
 */

public class DownloadModel {

    private IFileDownloadCallbacks callbacks;

    private DownloadEntity entity;

    private File parentDir;

    private static final int NETWORK_ERROR = -1;

    public DownloadModel(DownloadEntity entity, File parentDir, IFileDownloadCallbacks callbacks) {
        this.callbacks = callbacks;
        this.entity = entity;
        this.parentDir = parentDir;
    }

    public void doRequest(Context context) {
        //检查本地空间是否足够
        if (entity.getFileSize() > getLocalSpace()) {
            callbacks.onNoEnoughSpace();
            return;
        }

        int type = checkNetWork(context);
        if (type == NETWORK_ERROR) {
            //无网络访问
            callbacks.onNoInternet();
            return;
        }
        if (type == ConnectivityManager.TYPE_MOBILE) {
            //当用户使用数据流量的情况,提示用户
            callbacks.onMobileNet();
            return;
        }

        download(context);
    }

    public void forceStart(Context context) {
        download(context);
    }

    public boolean cancelDownload() {
        if (downloadHandler == null) {
            return false;
        }
        if (downloadHandler.isCancelled()||downloadHandler.isFinished()) {
            return false;
        }
        callbacks.onDownloadCancel();
        downloadHandler.cancel(true);
        return true;
    }

    private RequestHandle downloadHandler;

    /**
     * 下载文件
     */
    private void download(Context context) { //参数按需求补充
        HttpUtil httpUtil = HttpUtil.getInstance();
        callbacks.onDownloadStart(entity);
        downloadHandler = httpUtil.getClient().get(context, entity.getFileUrl(),
                new FileResponseHandler(getCacheFile()));
    }

    /**
     * 获取本地空间大小
     *
     * @return
     */
    private long getLocalSpace() {
        long localSpace = 0;
        if (!Environment.getExternalStorageDirectory().equals(Environment.MEDIA_MOUNTED)) {
            localSpace = parentDir.getFreeSpace();
        }
        return localSpace;
    }

    private File cacheFile;

    private File getCacheFile() {
        if (cacheFile == null) {
            cacheFile = new File(parentDir, entity.getFileName());
        }
        return cacheFile;
    }

    /**
     * 检查网络状态
     */
    private int checkNetWork(Context mContext) {
        int type = -1;
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo == null) {
            //无网络
            callbacks.onNoInternet();
            return type;
        }
        return networkInfo.getType();
    }

    private class FileResponseHandler extends FileAsyncHttpResponseHandler {
        private long startTime;

        public FileResponseHandler(File file) {
            super(file, false, true);
            startTime = TimeUtil.getCurrentTimeInLong();
        }

        @Override
        public void onFailure(int i, Header[] headers, Throwable throwable, File file) {
            long currentTime = TimeUtil.getCurrentTimeInLong();
            long _time = currentTime - startTime;
            if (_time >= 2000) {
                handleOnFailureResult(404, headers, throwable, file);
                return;
            }
            handleOnFailureResult(i, headers, throwable, file);
        }

        @Override
        public void onSuccess(int i, Header[] headers, File file) {
            if (downloadHandler.isCancelled()) {
                //发现小米特例
//                callbacks.onDownloadCancel(); //调用cancel时处理回调
                return;
            }
            callbacks.onDownloadSuccess(entity,file);
        }

        @Override
        public void onProgress(long bytesWritten, long totalSize) {
            super.onProgress(bytesWritten, totalSize);
            //callback
            callbacks.onDownloading(entity, bytesWritten, totalSize);
        }

        /**
         * 处理下载失败的结果
         *
         * @param i
         * @param headers
         * @param throwable
         * @param file
         */
        private void handleOnFailureResult(int i, Header[] headers, Throwable throwable, File file) {
            switch (i) {
                case 404:
                    //服务端文件不存在
                    callbacks.onFileNotFoundOnServer();
                    break;
                case 0:
                    //服务器故障
                    callbacks.onNoInternet();
                    break;
                default:
                    //默认告诉用户文件下载失败,并删除可能已经下载了部分的文件
                    callbacks.downloadFailure();
                    if (file != null) {
                        file.delete();
                    }
                    break;
            }
        }
    }


}
