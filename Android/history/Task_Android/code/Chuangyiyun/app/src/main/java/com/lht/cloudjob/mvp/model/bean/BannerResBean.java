package com.lht.cloudjob.mvp.model.bean;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.bean
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> BannerResBean
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/9/12
 */
public class BannerResBean {
    /**
     * 跳转
     */
    private String link;

    /**
     * 打开位置
     */
    private String target;

    /**
     * 图片
     */
    private String image;

    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }


    public String getTarget() {
        return this.target;
    }

    public void setTarget(String target) {
        this.target = target;
    }


    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
