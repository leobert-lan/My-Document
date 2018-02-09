package com.lht.cloudjob.mvp.model.interfaces;


import com.lht.cloudjob.mvp.model.pojo.DownloadEntity;

import java.io.File;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.interfaces
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IFileDownloadCallbacks
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/11/10.
 */

public interface IFileDownloadCallbacks {
    /**
     * 无网络、下载中断线
     */
    void onNoInternet();

    /**
     * 数据流量连接
     */
    void onMobileNet();

    /**
     * 服务端文件不存在
     */
    void onFileNotFoundOnServer();

    /**
     * 开始下载
     */
    void onDownloadStart(DownloadEntity entity);

    /**
     * 取消了下载
     */
    void onDownloadCancel();

    /**
     * 下载成功
     */
    void onDownloadSuccess(DownloadEntity entity, File file);

    /**
     * 下载过程中、进度更新
     *
     * @param entity  预览文件实体
     * @param current 当前量 byte
     * @param total   总量 byte
     */
    void onDownloading(DownloadEntity entity, long current, long total);

    /**
     * 没有足够空间下载
     */
    void onNoEnoughSpace();

    /**
     * 文件下载失败
     */
    void downloadFailure();
}
