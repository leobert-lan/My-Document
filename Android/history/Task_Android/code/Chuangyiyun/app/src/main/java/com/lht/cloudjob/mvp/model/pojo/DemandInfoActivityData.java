package com.lht.cloudjob.mvp.model.pojo;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.pojo
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> DemandInfoActivityData
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/8/23
 */
public class DemandInfoActivityData {
    private String demandId;

    private LoginInfo loginInfo;

    public String getDemandId() {
        return demandId;
    }

    public void setDemandId(String demandId) {
        this.demandId = demandId;
    }

    public LoginInfo getLoginInfo() {
        return loginInfo;
    }

    public void setLoginInfo(LoginInfo loginInfo) {
        this.loginInfo = loginInfo;
    }
}
