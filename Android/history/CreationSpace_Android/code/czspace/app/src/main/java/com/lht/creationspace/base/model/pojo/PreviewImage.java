package com.lht.creationspace.base.model.pojo;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.model.pojo
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> PreviewImage
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/11/3.
 */

public class PreviewImage {
    private String previewUrl;

    private String resUrl;

    private String name;

    private long fileSize;

    boolean isLoaded;

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

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }
}
