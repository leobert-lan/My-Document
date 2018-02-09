package com.lht.creationspace.module.user.login.model.pojo;

import com.lht.creationspace.module.user.login.model.TpLoginModel;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.model.bean
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> TpLoginResBean
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/15.
 * <p>
 * to see Model at{@link TpLoginModel}
 */
public class TpLoginResBean {

    public static final int RET_ERROR_UNREGISTER = 10010;

    private String username;

    private String id;

    private String nickname;

    private String email;

    private String mobile;

    private int isnewpwd;

    /**
     * 用户状态（1=>已经激活，2=>未激活，3=>注销，4=>禁用）
     */
    private int status;


    private String avatar;

    private String uid;

    /**
     * 用户分流（0=>未分流，1=>注册时，2=>登录，3=>后台）
     */
    private int choose_role_source;

    private String vso_token;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public static LoginResBean copy2LoginResBean(TpLoginResBean tpLoginResBean) {
        LoginResBean bean = new LoginResBean();
        if (tpLoginResBean == null) {
            return bean;
        }
        bean.setAvatar(tpLoginResBean.getAvatar());
        bean.setEmail(tpLoginResBean.getEmail());
//        bean.setId(Integer.parseInt(tpLoginResBean.getId()));
        bean.setIsnewpwd(tpLoginResBean.getIsnewpwd());
        bean.setLogined(1);
        bean.setMobile(tpLoginResBean.getMobile());
        bean.setNickname(tpLoginResBean.getNickname());
//        bean.setStatus(tpLoginResBean.getStatus());
        bean.setUid(tpLoginResBean.getUid());
        bean.setUsername(tpLoginResBean.getUsername());
        bean.setVso_token(tpLoginResBean.getVso_token());
        bean.setChoose_role_source(tpLoginResBean.getChoose_role_source());
        return bean;
    }
}
