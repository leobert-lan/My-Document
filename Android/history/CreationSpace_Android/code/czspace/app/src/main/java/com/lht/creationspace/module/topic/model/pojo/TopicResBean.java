package com.lht.creationspace.module.topic.model.pojo;

import java.util.ArrayList;

/**
 * Created by chhyu on 2017/3/3.
 * 发布文章选择圈子
 */

public class TopicResBean {
    private long total;

    private ArrayList<TopicDetailInfoResBean> circles;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public ArrayList<TopicDetailInfoResBean> getCircles() {
        return circles;
    }

    public void setCircles(ArrayList<TopicDetailInfoResBean> circles) {
        this.circles = circles;
    }

    public static class TopicDetailInfoResBean {
        private String id;

        private String circle_name;

        private String circle_desc;

        private String circle_admin;

        private String circle_icon;

        private int is_recommend;

        private String created_at;

        private String updated_at;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCircle_name() {
            return circle_name;
        }

        public void setCircle_name(String circle_name) {
            this.circle_name = circle_name;
        }

        public String getCircle_desc() {
            return circle_desc;
        }

        public void setCircle_desc(String circle_desc) {
            this.circle_desc = circle_desc;
        }

        public String getCircle_admin() {
            return circle_admin;
        }

        public void setCircle_admin(String circle_admin) {
            this.circle_admin = circle_admin;
        }

        public String getCircle_icon() {
            return circle_icon;
        }

        public void setCircle_icon(String circle_icon) {
            this.circle_icon = circle_icon;
        }

        public int getIs_recommend() {
            return is_recommend;
        }

        public void setIs_recommend(int is_recommend) {
            this.is_recommend = is_recommend;
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
    }
}
