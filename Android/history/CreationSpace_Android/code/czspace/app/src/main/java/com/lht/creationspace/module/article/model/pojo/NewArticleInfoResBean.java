package com.lht.creationspace.module.article.model.pojo;

/**
 * Created by chhyu on 2017/3/7.
 * 新建文章的返回数据
 */

public class NewArticleInfoResBean {

    private String circle_id;

    private String article_title;

    private String article_content;

    private int is_recommend;

    private int article_type;

    private String article_brief;

    private String article_thumb;

    private String article_author;

    private String updated_at;

    private String created_at;

    private String id;

    private String circle_detail_url;

    public String getArticle_title() {
        return article_title;
    }

    public void setArticle_title(String article_title) {
        this.article_title = article_title;
    }

    public String getArticle_content() {
        return article_content;
    }

    public void setArticle_content(String article_content) {
        this.article_content = article_content;
    }

    public int getIs_recommend() {
        return is_recommend;
    }

    public void setIs_recommend(int is_recommend) {
        this.is_recommend = is_recommend;
    }

    public int getArticle_type() {
        return article_type;
    }

    public void setArticle_type(int article_type) {
        this.article_type = article_type;
    }

    public String getArticle_brief() {
        return article_brief;
    }

    public void setArticle_brief(String article_brief) {
        this.article_brief = article_brief;
    }

    public String getArticle_thumb() {
        return article_thumb;
    }

    public void setArticle_thumb(String article_thumb) {
        this.article_thumb = article_thumb;
    }

    public String getArticle_author() {
        return article_author;
    }

    public void setArticle_author(String article_author) {
        this.article_author = article_author;
    }

    public String getCircle_id() {
        return circle_id;
    }

    public void setCircle_id(String circle_id) {
        this.circle_id = circle_id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCircle_detail_url() {
        return circle_detail_url;
    }

    public void setCircle_detail_url(String circle_detail_url) {
        this.circle_detail_url = circle_detail_url;
    }
}
