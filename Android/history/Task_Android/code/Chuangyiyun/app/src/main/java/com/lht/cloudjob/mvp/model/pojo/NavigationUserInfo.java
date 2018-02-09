package com.lht.cloudjob.mvp.model.pojo;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.pojo
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> NavigationUserInfo
 * <p><b>Description</b>: 侧边栏用户信息pojo
 * Created by leobert on 2016/7/8.
 */
public class NavigationUserInfo {

    String avatarUrl;

    String username;

    String nickname;

    int vipImgResId = -1;

    int type;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getVipImgResId() {
        return vipImgResId;
    }

    public void setVipImgResId(int vipImgResId) {
        this.vipImgResId = vipImgResId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isEnterprise() {
        return type == 1;
    }
}
