package com.lht.cloudjob.mvp.model.pojo;

import com.lht.cloudjob.mvp.model.bean.DemandInfoResBean;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.pojo
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> PreviewImage
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/11/3.
 */

public class PreviewImage {
    private String previewUrl;

    private String resUrl;

    private String name;

    private long fileSize;

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getResUrl() {
        return resUrl;
    }

    public void setResUrl(String resUrl) {
        this.resUrl = resUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public static PreviewImage copyFromAttachmentExt(DemandInfoResBean.AttachmentExt ext) {
        PreviewImage image = new PreviewImage();
        if (ext == null) {
            return image;
        }
        image.setFileSize(ext.getFile_size());
        image.setName(ext.getFile_name());
        image.setPreviewUrl(ext.getUrl_preview());
        image.setResUrl(ext.getUrl_download());
        return image;
    }

    public static ArrayList<PreviewImage> copyFromAttachmentExtArrayList(ArrayList<DemandInfoResBean.AttachmentExt> exts) {
        ArrayList<PreviewImage> ret = new ArrayList<>();
        if (exts == null || exts.isEmpty()) {
            return ret;
        }

        for (DemandInfoResBean.AttachmentExt ext : exts) {
            ret.add(copyFromAttachmentExt(ext));
        }
        return ret;
    }
}
