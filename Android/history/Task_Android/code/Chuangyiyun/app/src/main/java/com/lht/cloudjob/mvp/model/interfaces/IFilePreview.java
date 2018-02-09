package com.lht.cloudjob.mvp.model.interfaces;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.interfaces
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IFilePreview
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/11/3.
 */

public interface IFilePreview {
    /**
     * 强制进行下载
     */
    void forceStart();

    /**
     * @param deleteTemps 是否删除本地文件
     */
    void cancelDownload(boolean deleteTemps);

    /**
     * 进行请求
     */
    void doRequest();
}
