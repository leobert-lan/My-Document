package com.lht.cloudjob.native4js.expandresbean;

/**
 * <p><b>Package</b> com.lht.cloudjob.native4js.expandresbean
 * <p><b>Project</b> Jsbridge_lib
 * <p><b>Classname</b> NF_VsoAuthInfoResBean
 * <p><b>Description</b>: 桥接业务：获取当前用户的token信息，返回数据data模型
 * <p>Created by leobert on 2016/12/1.
 */

public class NF_VsoAuthInfoResBean {
    private String auth_username;
    private String auth_token;

    public String getAuth_username() {
        return auth_username;
    }

    public void setAuth_username(String auth_username) {
        this.auth_username = auth_username;
    }

    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }
}
