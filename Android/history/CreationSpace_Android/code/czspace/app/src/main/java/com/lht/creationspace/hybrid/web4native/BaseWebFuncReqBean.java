package com.lht.creationspace.hybrid.web4native;

/**
 * <p><b>Package:</b> com.lht.creationspace.web4native </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> BaseWebFuncReqBean </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/3/9.
 */

public class BaseWebFuncReqBean {
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
