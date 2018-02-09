package com.lht.cloudjob.mvp.model.bean;

import com.lht.cloudjob.mvp.model.pojo.LoginType;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.bean
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> BasicInfoResBean
 * <p><b>Description</b>: 查询基本信息
 * 错误码 => 错误信息：
 * "9012" => "缺少用户名"
 * "9013" => "用户名不存在"
 * "13020" => "查询用户资料成功"
 * <p/>
 *
 * to see model at {@link com.lht.cloudjob.mvp.model.BasicInfoModel}
 * to see API at{@link com.lht.cloudjob.interfaces.net.IRestfulApi.BasicInfoApi}
 * Created by leobert on 2016/7/26.
 */
public class BasicInfoResBean {


    private String vip;


    /**
     * 用户名
     */
    private String username;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 性别（0=>保密，1=>男性，2=>女性）
     */
    private int sex;

    /**
     * 用户所属行业编号,未设置的默认是0
     */
    private int indus_pid;
    /**
     * 用户所属行业名称
     */
    private String indus_name;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 领域标签，未设置的是""
     */
    private String lable;
    /**
     * 企业认证（0=>待审核，1=>通过，2=>驳回，3=>草稿）
     */
    private int enterprise_auth_status;
    /**
     * 实名认证（0=>待审核，1=>通过，2=>驳回，3=>草稿）
     */
    private int realname_auth_status;
    /**
     * 手机认证（0=>待审核，1=>通过，2=>驳回）
     */
    private int mobile_auth_status;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getIndus_pid() {
        return indus_pid;
    }

    public void setIndus_pid(int indus_pid) {
        this.indus_pid = indus_pid;
    }

    public String getIndus_name() {
        return indus_name;
    }

    public void setIndus_name(String indus_name) {
        this.indus_name = indus_name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

    public int getEnterprise_auth_status() {
        return enterprise_auth_status;
    }

    public void setEnterprise_auth_status(int enterprise_auth_status) {
        this.enterprise_auth_status = enterprise_auth_status;
    }

    public int getRealname_auth_status() {
        return realname_auth_status;
    }

    public void setRealname_auth_status(int realname_auth_status) {
        this.realname_auth_status = realname_auth_status;
    }

    public int getMobile_auth_status() {
        return mobile_auth_status;
    }

    /**
     * 自定义的属性，不是成功的状态都可以直接绑定手机。
     * @return true if bind success;
     */
    public boolean isPhoneBinded() {
        return mobile_auth_status == 1;
    }

    public void setMobile_auth_status(int mobile_auth_status) {
        this.mobile_auth_status = mobile_auth_status;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
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


//    /**
//     * 自添加的内容，当且仅当indus 和label都设置过才认为已完成设置
//     *
//     * @return
//     */
//    public boolean isFieldSet() {
//        return getIndus_pid() != 0 && !StringUtil.isEmpty(getLable());
//    }
}