package com.lht.creationspace.hybrid.web4native;

/**
 * <p><b>Package:</b> com.lht.creationspace.web4native </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> BaseWebResBean </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/3/22.
 */

public class BaseWebResBean {

    private int ret;
    private int status;
    private String msg = "";
    private String data;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
