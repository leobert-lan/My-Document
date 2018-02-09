package com.lht.cloudjob.mvp.model.interfaces;

import com.lht.cloudjob.mvp.model.pojo.PreviewFileEntity;

import java.io.File;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.interfaces
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IFilePreviewCallbacks
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/11/2.
 */

public interface IFilePreviewCallbacks {

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
    void onDownloadStart(PreviewFileEntity entity);

    /**
     * 取消了下载
     */
    void onDownloadCancel();

    /**
     * 文件就绪,可能是下载的，可能是从缓存区拿出来的
     *
     * @param previewFile hint：按照设计，该文件的预览事件是在模块内处理的，此处先给出，但未必使用
     */
    void onFileReady(File previewFile);

    /**
     * 超过下载警告阈值
     *
     * @param netType 网络类型
     * @param max     最大上限 byte
     * @param actual  实际值 byte
     */
    void onLargeSize(int netType, long max, long actual);

    /**
     * 下载过程中、进度更新
     *
     * @param entity  预览文件实体
     * @param current 当前量 byte
     * @param total   总量 byte
     */
    void onDownloading(PreviewFileEntity entity, long current, long total);

    /**
     * 没有足够空间下载
     */
    void onNoEnoughSpace();

    /**
     * 本地缓存的文件已修改
     */
    void onLocalCacheChanged();

    /**
     * 没有第三方APP能处理预览
     */
    void onNoAppCanPreview();

    /**
     * 文件下载失败
     */
    void downloadFailure();

}
