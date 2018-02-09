package com.lht.creationspace.module.user.info.model.pojo;

/**
 * Created by chhyu on 2017/3/10.
 * 用户三方信息列表
 */

public class ThirdInfoListResBean {

    private AuthInfo qq;
    private AuthInfo weibo;
    private AuthInfo weixin;

    public AuthInfo getQq() {
        return qq;
    }

    public void setQq(AuthInfo qq) {
        this.qq = qq;
    }

    public AuthInfo getWeibo() {
        return weibo;
    }

    public void setWeibo(AuthInfo weibo) {
        this.weibo = weibo;
    }

    public AuthInfo getWeixin() {
        return weixin;
    }

    public void setWeixin(AuthInfo weixin) {
        this.weixin = weixin;
    }

    /**
     * QQ 信息
     */
    public class AuthInfo {
        private String oauth_uid;
        private String oauth_screen_name;
        private String oauth_image;
        private String oauth_access_token;
        private String oauth_location;
        private String create_time;

        public String getOauth_uid() {
            return oauth_uid;
        }

        public void setOauth_uid(String oauth_uid) {
            this.oauth_uid = oauth_uid;
        }

        public String getOauth_screen_name() {
            return oauth_screen_name;
        }

        public void setOauth_screen_name(String oauth_screen_name) {
            this.oauth_screen_name = oauth_screen_name;
        }

        public String getOauth_image() {
            return oauth_image;
        }

        public void setOauth_image(String oauth_image) {
            this.oauth_image = oauth_image;
        }

        public String getOauth_access_token() {
            return oauth_access_token;
        }

        public void setOauth_access_token(String oauth_access_token) {
            this.oauth_access_token = oauth_access_token;
        }

        public String getOauth_location() {
            return oauth_location;
        }

        public void setOauth_location(String oauth_location) {
            this.oauth_location = oauth_location;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }
    }

}
