package com.lht.cloudjob.mvp.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.mvp.model.interfaces.IFilePreview;
import com.lht.cloudjob.mvp.model.interfaces.IFilePreviewCallbacks;
import com.lht.cloudjob.mvp.model.pojo.PreviewFileEntity;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.lht.cloudjob.util.time.TimeUtil;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;

import org.apache.http.Header;

import java.io.File;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> FilePreviewModel
 * <p><b>Description</b>: TODO  降低一下难度，仅针对单文件，不针对整个系统
 * <p>Created by leobert on 2016/11/2.
 */

public class FilePreviewModel implements IFilePreview {

    private final PreviewFileEntity entity;
    private final IFilePreviewCallbacks callbacks;
    private Context mContext;
    private String requestUser;
    private final File cacheDir;

    private static final long FILE_MAX_SIZE = 2 * 1024 * 1024; //2M
    private final int NETWORK_ERROR = -1;

    /**
     * @param entity    预览文件实体
     * @param cacheDir  缓存目录
     * @param callbacks 回调接口实现类实例
     */
    public FilePreviewModel(Context context, PreviewFileEntity entity, String requestUser,
                            File cacheDir, IFilePreviewCallbacks callbacks) {
        //校验 cacheDir
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        if (!cacheDir.isDirectory()) {
            DLog.e(getClass(), "the path you give to save the preview cache is not a dir");
        }
        this.requestUser = requestUser;
        this.mContext = context;
        this.entity = entity;
        this.callbacks = callbacks;
        this.cacheDir = cacheDir;
        DLog.d(getClass(), "checkPreviewFileEntity\r\n" + JSON.toJSONString(entity));
    }


    /**
     * 检查网络状态
     */
    private int checkNetWork() {
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


    /**
     * 检查本地是否能够处理此类文件
     *
     * @return
     */
    private boolean checkExecutable() {
        //mime intent是否能被处理
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);

        intent.setDataAndType(null, entity.getMimeType());
        PackageManager pm = mContext.getPackageManager();
        ComponentName cn = intent.resolveActivity(pm);
        return !(cn == null);
    }

    private File cacheFile;

    private File getCacheFile() {
        if (cacheFile == null) {
            cacheFile = new File(cacheDir, entity.getUniqueName(requestUser));
        }
        return cacheFile;
    }

    /**
     * 检查缓存文件是否存在并校验文件完整性
     *
     * @return
     */
    private boolean checkExist() {
        //file size 问题
        File file = getCacheFile();
        if (file.exists()) {
            long size = file.length();
            if (size == entity.getFileSize()) {
                return true;
            } else {
                if (file != null) {
                    //文件不全可能是因为本地缓存文件已修改
                    callbacks.onLocalCacheChanged();
                    file.delete();
                }
                return false;
            }
        }
        return false;
    }

    @Override
    public void forceStart() { //强制下载
        download();
    }

    @Override
    public void cancelDownload(boolean deleteTemps) {
        if (downloadHandle == null) {
            //程序流有问题
            return;
        }
        downloadHandle.cancel(true);
        if (deleteTemps) {
            getCacheFile().delete();
        }
        //cancel 的动作
        //callback
        callbacks.onDownloadCancel();
    }

    @Override
    public void doRequest() {
        //checkExecutable if false callback and return
        if (!checkExecutable()) {
            //手机上没有应用可以打开此类文件
            callbacks.onNoAppCanPreview();
            return;
        }
        //cache exist  if true dopreview  检查本地缓存文件并校验文件完整性
        if (checkExist()) {
            //本地缓存文件存在，直接预览
            callbacks.onFileReady(cacheFile);
            doPreview(cacheFile);
            return;
        }
        //检查本地空间是否足够
        if (entity.getFileSize() > getLocalSpace()) {
            callbacks.onNoEnoughSpace();
            return;
        }
        //check maxsize nettype
        int type = checkNetWork();
        if (type == NETWORK_ERROR) {
            //无网络访问
            callbacks.onNoInternet();
            return;
        }
        if (type == ConnectivityManager.TYPE_MOBILE) {
            //当用户使用数据流量的情况,提示用户
            if (entity.getFileSize() > FILE_MAX_SIZE) {
                callbacks.onLargeSize(type, FILE_MAX_SIZE, entity.getFileSize());
                return;
            } else {
                //按照需求 不提示
//                callbacks.onMobileNet();
//                return;
            }
        }
        download();
    }

    /**
     * 获取本地空间大小
     *
     * @return
     */
    private long getLocalSpace() {
        long localSpace = 0;
        if (!Environment.getExternalStorageDirectory().equals(Environment.MEDIA_MOUNTED)) {
            localSpace = cacheDir.getFreeSpace();
        }
        return localSpace;
    }

    private RequestHandle downloadHandle;

    /**
     * 下载文件
     */
    private void download() { //参数按需求补充
        HttpUtil httpUtil = HttpUtil.getInstance();
        callbacks.onDownloadStart(entity);
        downloadHandle = httpUtil.getClient().get(mContext, entity.getFileUrl(), new FileResponseHandler(getCacheFile()));
    }

    /**
     * 预览
     */
    private void doPreview(File previewFile) {
        String type = entity.getMimeType();
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(previewFile), type);
        mContext.startActivity(intent);
    }

    private class FileResponseHandler extends FileAsyncHttpResponseHandler {
        private long startTime;

        public FileResponseHandler(File file) {
            super(file);
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
            //走预览
            if (file == null || !file.exists()) {
//                    ||file.length()<entity.getFileSize()) {
                //发现小米特例
                callbacks.onDownloadCancel();
                return;
            }
            callbacks.onFileReady(file);
            doPreview(file);
        }


        @Override
        public void onProgress(long bytesWritten, long totalSize) {
            super.onProgress(bytesWritten, totalSize);
            //callback
            callbacks.onDownloading(entity, bytesWritten, totalSize);
        }
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
