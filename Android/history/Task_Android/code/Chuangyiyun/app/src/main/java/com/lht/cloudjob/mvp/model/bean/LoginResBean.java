package com.lht.cloudjob.mvp.model.bean;

import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.LoginModel;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.bean
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> LoginResBean
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/11.
 * to see Model at{@link LoginModel}
 * to see API at{@link IRestfulApi.LoginApi}
 */
public class LoginResBean {

    public static final int RET_UNEXIST = 13003;

    private String username;

    private int id;

    private String nickname;

    private String email;

    private String mobile;

    private int status;//"status": "1",

//    private String password; 不解析

    private int logined;//        "logined": "1",

    /**
     *  是否需要重置密码（1=>不需要，2=>需要），用于三方
     */
    private int isnewpwd;

    /**
     * 头像url
     */
    private String avatar;

    /**
     * uc数据库，鬼知道是什么鬼
     */
    private int uid;

    /**
     *  redis中存储用户信息的key
     */
    private String vso_token;//

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }

    public int getLogined() {
        return logined;
    }

    public void setLogined(int logined) {
        this.logined = logined;
    }

    public int getIsnewpwd() {
        return isnewpwd;
    }

    public void setIsnewpwd(int isnewpwd) {
        this.isnewpwd = isnewpwd;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getVso_token() {
        return vso_token;
    }

    public void setVso_token(String vso_token) {
        this.vso_token = vso_token;
    }
}
