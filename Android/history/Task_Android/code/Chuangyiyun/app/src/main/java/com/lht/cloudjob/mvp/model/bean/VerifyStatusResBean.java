package com.lht.cloudjob.mvp.model.bean;

import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.pojo.LoginType;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.bean
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> VerifyStatusResBean
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/12.
 *
 * to see ResBean at{@link com.lht.cloudjob.mvp.model.bean.VerifyStatusResBean}
 * to see API at{@link IRestfulApi.AuthStatusApi}
 */
public class VerifyStatusResBean {
    //(0:未认证,1认证通过)
    /**
     * 支付认证
     */
    private int bank_auth_status;

    /**
     * 邮箱认证
     */
    private int email_auth_status;
    /**
     * 企业认证
     */
    private int enterprise_auth_status;

    /**
     * 手机认证
     */
    private int mobile_auth_status;

    /**
     * 实名认证
     */
    private int realname_auth_status;

    /**
     * 校园认证
     */
    private int university_auth_status;

    public int getBank_auth_status() {
        return bank_auth_status;
    }

    public void setBank_auth_status(int bank_auth_status) {
        this.bank_auth_status = bank_auth_status;
    }

    public int getEmail_auth_status() {
        return email_auth_status;
    }

    public void setEmail_auth_status(int email_auth_status) {
        this.email_auth_status = email_auth_status;
    }

    public int getEnterprise_auth_status() {
        return enterprise_auth_status;
    }

    public void setEnterprise_auth_status(int enterprise_auth_status) {
        this.enterprise_auth_status = enterprise_auth_status;
    }

    public int getMobile_auth_status() {
        return mobile_auth_status;
    }

    public void setMobile_auth_status(int mobile_auth_status) {
        this.mobile_auth_status = mobile_auth_status;
    }

    public int getRealname_auth_status() {
        return realname_auth_status;
    }

    public void setRealname_auth_status(int realname_auth_status) {
        this.realname_auth_status = realname_auth_status;
    }

    public int getUniversity_auth_status() {
        return university_auth_status;
    }

    public void setUniversity_auth_status(int university_auth_status) {
        this.university_auth_status = university_auth_status;
    }

    public LoginType getLoginType() {
        if (enterprise_auth_status == 1) {
            return LoginType.EnterpriseVerified;
        } else if (realname_auth_status == 1) {
            return LoginType.PersonalVerified;
        } else {
            return LoginType.UnVerified;
        }
    }
}
