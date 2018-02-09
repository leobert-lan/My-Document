package com.lht.creationspace.module.topic.model.pojo;

/**
 * Created by chhyu on 2017/3/31.
 */

public class CircleJoinStateResBean {

    private boolean is_joined;
    private JoinedInfo joined;

    public boolean is_joined() {
        return is_joined;
    }

    public void setIs_joined(boolean is_joined) {
        this.is_joined = is_joined;
    }

    public JoinedInfo getJoined() {
        return joined;
    }

    public void setJoined(JoinedInfo joined) {
        this.joined = joined;
    }

    /**
     * 用户加入的信息（已加入，未加入的为null）
     */
    public class JoinedInfo {
        private int id;
        private int circle_id;
        private String member_username;
        private int is_owner;
        private String created_at;
        private String updated_at;
        private int is_show;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getCircle_id() {
            return circle_id;
        }

        public void setCircle_id(int circle_id) {
            this.circle_id = circle_id;
        }

        public String getMember_username() {
            return member_username;
        }

        public void setMember_username(String member_username) {
            this.member_username = member_username;
        }

        public int getIs_owner() {
            return is_owner;
        }

        public void setIs_owner(int is_owner) {
            this.is_owner = is_owner;
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

        public int getIs_show() {
            return is_show;
        }

        public void setIs_show(int is_show) {
            this.is_show = is_show;
        }
    }
}
