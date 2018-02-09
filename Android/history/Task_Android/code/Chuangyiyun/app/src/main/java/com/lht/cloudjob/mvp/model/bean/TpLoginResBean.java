package com.lht.cloudjob.mvp.model.bean;

import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.TpLoginModel;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.bean
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> TpLoginResBean
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/15.
 * <p>
 * to see Model at{@link TpLoginModel}
 * to see API at{@link IRestfulApi.TpLoginApi}
 */
public class TpLoginResBean {
    /**
     * 返回示例和返回说明:
     * "username": "86130962",
     * "id": "11543",
     * "nickname": "86130962",
     * "email": "",
     * "mobile": "",
     * "status": "1",//是否激活，一般用不到
     * "password": "672430bf5ac362eeca84029134fbcda4",
     * "logined": "1",
     * "isnewpwd": "2", // 是否需要重置密码（1=>不需要，2=>需要），用于三方
     * "avatar": "http://static.vsochina.com/data/avatar/000/01/15/43_avatar_middle.jpg",
     * "uid": "11543",
     * "vso_token": "dfb5795fe276e46ec36d133255cf42ad"
     */

    private String username;

    private String id;

    private String nickname;

    private String email;

    private String mobile;

    private int isnewpwd;

    private String avatar;

    private String uid;

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

    public boolean isResetNeed() {
        return isnewpwd == 2;
    }

    public static LoginResBean copy2LoginResBean(TpLoginResBean tpLoginResBean) {
        LoginResBean bean = new LoginResBean();
        if (tpLoginResBean == null) {
            return bean;
        }
        bean.setAvatar(tpLoginResBean.getAvatar());
        bean.setEmail(tpLoginResBean.getEmail());
        bean.setId(Integer.parseInt(tpLoginResBean.getId()));
        bean.setIsnewpwd(tpLoginResBean.getIsnewpwd());
        bean.setLogined(1);
        bean.setMobile(tpLoginResBean.getMobile());
        bean.setNickname(tpLoginResBean.getNickname());
        bean.setStatus(-2);//未知类型，不占用已有值
        bean.setUid(Integer.parseInt(tpLoginResBean.getUid()));
        bean.setUsername(tpLoginResBean.getUsername());
        bean.setVso_token(tpLoginResBean.getVso_token());
        return bean;
    }
}
