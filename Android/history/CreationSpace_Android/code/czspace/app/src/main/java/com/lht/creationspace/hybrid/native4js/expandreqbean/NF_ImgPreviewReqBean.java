package com.lht.creationspace.hybrid.native4js.expandreqbean;

import java.util.ArrayList;

/**
 * Created by leobert on 2017/3/1.
 */

public class NF_ImgPreviewReqBean extends NF_GeneralNavigateReqBean {

    /**
     * 当前需要呈现的图片在数组中的下标，start from 0
     */
    private int index;

    /**
     * 所有可预览的缩略图图片数组
     */
    private ArrayList<ImgEntity> imgs;

    public static class ImgEntity {
        /**
         * 原图url
         */
        private String url_origin;

        private String url_preview;


        public String getUrl_origin() {
            return url_origin;
        }

        public void setUrl_origin(String url_origin) {
            this.url_origin = url_origin;
        }

        public String getUrl_preview() {
            return url_preview;
        }

        public void setUrl_preview(String url_preview) {
            this.url_preview = url_preview;
        }

    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ArrayList<ImgEntity> getImgs() {
        return imgs;
    }

    public void setImgs(ArrayList<ImgEntity> imgs) {
        this.imgs = imgs;
    }
}
