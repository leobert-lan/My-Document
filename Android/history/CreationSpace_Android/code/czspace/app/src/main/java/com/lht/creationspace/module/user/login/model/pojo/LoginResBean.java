package com.lht.creationspace.module.user.login.model.pojo;

import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.util.string.StringUtil;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.model.bean
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> LoginResBean
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/11.
 */
public class LoginResBean {

    public static final int RET_UNEXIST = 13003;

    public static final int RET_ERROR_PWD = 13004;

    private String username;

//    private int id;

    private String nickname;

    private String email;

    private String mobile;

//    private int status;//"status": "1",

//    private String password; 不解析

    private int logined;//        "logined": "1",

    /**
     * 是否需要重置密码（1=>不需要，2=>需要），用于三方
     */
    private int isnewpwd;

    private int choose_role_source;//用户分流（0=>未分流，1=>注册时，2=>登录，3=>后台）

    /**
     * 头像url
     */
    private String avatar;

    /**
     * uc数据库，鬼知道是什么鬼
     */
    private String uid;

    /**
     * redis中存储用户信息的key
     */
    private String vso_token;//

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }

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

    public int getLogined() {
        return logined;
    }

    public void setLogined(int logined) {
        this.logined = logined;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getVso_token() {
        return vso_token;
    }

    public void setVso_token(String vso_token) {
        this.vso_token = vso_token;
    }

    public int getChoose_role_source() {
        return choose_role_source;
    }

    public void setChoose_role_source(int choose_role_source) {
        this.choose_role_source = choose_role_source;
    }

    public boolean hasChooseRole() {
        return choose_role_source != 0;
    }

    public int getIsnewpwd() {
        return isnewpwd;
    }

    public void setIsnewpwd(int isnewpwd) {
        this.isnewpwd = isnewpwd;
    }

    public boolean hasPwdManualSet() {
        return isnewpwd != 2;
    }

    public static String parseMsgByRet(int ret, BaseVsoApiResBean bean) {
        String msg = null;
        switch (ret) {
            case RET_ERROR_PWD:
                msg = "密码错误，请重新输入";
                break;
            case RET_UNEXIST:
                msg = "账号不存在"; //实际上优先检查该情况进行注册引导
                break;
            default:
                if (bean != null)
                    msg = bean.getMessage();
                if (StringUtil.isEmpty(msg))
                    msg = "登录失败，请重新登录";
                break;
        }
        return msg;
    }
}
