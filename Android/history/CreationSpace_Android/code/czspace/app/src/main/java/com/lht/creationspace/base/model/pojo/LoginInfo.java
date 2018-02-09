package com.lht.creationspace.base.model.pojo;

import com.lht.creationspace.module.user.login.model.pojo.LoginResBean;
import com.lht.creationspace.util.string.StringUtil;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.model.pojo
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> VerifyInfo
 * <p><b>Description</b>: 身份认证信息
 * Created by leobert on 2016/5/5.
 */
public class LoginInfo {
    //最基本的信息
    private String username;
    private String accessToken;
    private String accessId;
    private String avatar;
    private String nickname;
    private boolean hasChooseRole;


//    private LoginType type;


//    //用户基本但不敏感的信息
//    private BasicInfoResBean basicUserInfo;

    //登录返回data，保留备用
    private LoginResBean loginResBean;


    public LoginInfo() {
    }


    public LoginInfo(String username, String accessToken, String accessId) {
        this.username = username;
        this.accessToken = accessToken;
        this.accessId = accessId;
    }

    public boolean isHasChooseRole() {
        return hasChooseRole;
    }

    public void setHasChooseRole(boolean hasChooseRole) {
        this.hasChooseRole = hasChooseRole;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccessId() {
        return accessId;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String toString() {
        return "user:" + getUsername() + ",id:" + getAccessId() + ",token:" + getAccessToken();
    }


    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public LoginResBean getLoginResBean() {
        return loginResBean;
    }

    public void setLoginResBean(LoginResBean loginResBean) {
        this.loginResBean = loginResBean;
    }

    public boolean isLogin() {
        //非严格判断
        return !StringUtil.isEmpty(username);
    }

    public boolean isUserInfoCreated() {
        //非严格判断
        boolean b = StringUtil.isEmpty(nickname) || nickname.equals(username);
        return !b;
    }


    public void copy(LoginInfo info) {
        if (info == null) {
            copy(new LoginInfo());
            return;
        }
        this.setUsername(info.getUsername());
        this.setAccessId(info.getAccessId());
        this.setAccessToken(info.getAccessToken());
//        this.setType(info.getType());
        this.setNickname(info.getNickname());
        this.setAvatar(info.getAvatar());
//        this.setBasicUserInfo(info.getBasicUserInfo());
        this.setHasChooseRole(info.isHasChooseRole());
        this.setLoginResBean(info.getLoginResBean());
    }


//    public static String generateSession(LoginInfo info) {
//        String sess = VsoUtil.createVsoSessionCode(info.getUsername(),
//                info.getAccessId());
//        return sess;
//    }
}
