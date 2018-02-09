package com.lht.creationspace.module.pub.model.pojo;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.model.bean
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> MediaCenterUploadResBean
 * <p><b>Description</b>: cbs 媒体中心上传返回返回数据
 * <p> Create by Leobert on 2016/9/12
 */
public class MediaCenterUploadResBean {
    private String id;

    private String origin_name;

    private String key;

    private String bucket;

    private String token;

    private String created_at;

    private String updated_at;

    private String vcs_url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrigin_name() {
        return origin_name;
    }

    public void setOrigin_name(String origin_name) {
        this.origin_name = origin_name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getVcs_url() {
        return vcs_url;
    }

    public void setVcs_url(String vcs_url) {
        this.vcs_url = vcs_url;
    }
}
