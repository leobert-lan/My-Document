package com.lht.creationspace.base.model.pojo;

import com.lht.creationspace.module.setting.model.CheckVersionUpdateModel;
import com.lht.creationspace.hybrid.native4js.expandreqbean.NF_DownloadReqBean;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.model.pojo
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> DownloadEntity
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/11/10.
 */

public class DownloadEntity {

    /**
     * the real name of the file
     */
    private String fileName;

    /**
     * bytes of the attachment
     */
    private long fileSize;

    /**
     * url of the attachment
     */
    private String fileUrl;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public static DownloadEntity copyFromPreviewImage(PreviewImage previewImage) {
        DownloadEntity entity = new DownloadEntity();
        entity.setFileName(previewImage.getName());
        entity.setFileUrl(previewImage.getResUrl());
        entity.setFileSize(previewImage.getFileSize());
        return entity;
    }

    public static DownloadEntity copyFromVersionResBean(CheckVersionUpdateModel.VersionResBean.VersionInfoData versionInfoData) {
        DownloadEntity entity = new DownloadEntity();
        int index = versionInfoData.getUrl().lastIndexOf("/");
        versionInfoData.setFileName(versionInfoData.getUrl().substring(index + 1));
        entity.setFileName(versionInfoData.getFileName());
//        entity.setFileSize(versionResBean.getFileSize());
        entity.setFileUrl(versionInfoData.getUrl());
        return entity;
    }

    public static DownloadEntity copyFromDownloadBean(NF_DownloadReqBean bean) {
        DownloadEntity entity = new DownloadEntity();
        entity.setFileUrl(bean.getUrl_download());
        entity.setFileName(bean.getFile_name());
        entity.setFileSize(bean.getFile_size());
        return entity;
    }
}
