package com.lht.cloudjob.mvp.model.pojo;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.pojo
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> ImagePreviewActivityData
 * <p><b>Description</b>: 图片预览页面元数据
 * <p>Created by leobert on 2016/11/4.
 */

public class ImagePreviewActivityData {
    private ArrayList<PreviewImage> previewImages;

    /**
     * start with zero
     */
    private int currentIndex;

    public ArrayList<PreviewImage> getPreviewImages() {
        return previewImages;
    }

    public void setPreviewImages(ArrayList<PreviewImage> previewImages) {
        this.previewImages = previewImages;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }
}
