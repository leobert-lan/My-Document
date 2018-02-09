package com.lht.creationspace.module.projchapter.ui.model.bean;

/**
 * Created by chhyu on 2017/7/28.
 */

public class ProjUpdateResBean {
    private String title;
    private String content;
    private String projectId;
    private String author;
    private int articleType;
    private String updated_at;
    private String created_at;
    private int id;
    private String to_url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getArticleType() {
        return articleType;
    }

    public void setArticleType(int articleType) {
        this.articleType = articleType;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTo_url() {
        return to_url;
    }

    public void setTo_url(String to_url) {
        this.to_url = to_url;
    }
}
