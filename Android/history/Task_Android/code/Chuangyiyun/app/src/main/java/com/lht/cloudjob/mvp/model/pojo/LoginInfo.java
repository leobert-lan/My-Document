package com.lht.cloudjob.mvp.model.pojo;

import com.lht.cloudjob.mvp.model.bean.BasicInfoResBean;
import com.lht.cloudjob.mvp.model.bean.LoginResBean;
import com.lht.cloudjob.util.string.StringUtil;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.pojo
 * <p><b>Project</b> Chuangyiyun
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


    private LoginType type;


    //用户基本但不敏感的信息
    private BasicInfoResBean basicUserInfo;

    //登录返回data，保留备用
    private LoginResBean loginResBean;


    public LoginInfo() {
    }


    public LoginInfo(String username, String accessToken, String accessId) {
        this.username = username;
        this.accessToken = accessToken;
        this.accessId = accessId;
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

    public LoginType getType() {
        return type;
    }

    public void setType(LoginType type) {
        this.type = type;
    }

    public BasicInfoResBean getBasicUserInfo() {
        return basicUserInfo;
    }

    public void setBasicUserInfo(BasicInfoResBean basicUserInfo) {
        this.basicUserInfo = basicUserInfo;
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


    public void copy(LoginInfo info) {
        if (info == null) {
            copy(new LoginInfo());
            return;
        }
        this.setUsername(info.getUsername());
        this.setAccessId(info.getAccessId());
        this.setAccessToken(info.getAccessToken());
        this.setType(info.getType());
        this.setNickname(info.getNickname());
        this.setAvatar(info.getAvatar());
        this.setBasicUserInfo(info.getBasicUserInfo());
        this.setLoginResBean(info.getLoginResBean());
    }
}
