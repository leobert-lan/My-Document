package com.lht.cloudjob.mvp.model.bean;

import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.EAuthenticQueryModel;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.bean
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> EAuthenticQueryResBean
 * <p><b>Description</b>: TODO
 * <p>Created by Administrator on 2016/9/5.
 * <p/>
 * to see API at{@link IRestfulApi.QueryEAuthenticApi}
 * to see Model at{@link EAuthenticQueryModel}
 */
public class EAuthenticQueryResBean {
    //* 返回码 => 错误信息：
//        * "13820" => "企业认证信息查询成功"
//        * "13862" => "数据不存在" 说明没有进行过认证
    public static final int RET_UNSUBMIT = 13862;
    public static final int STATE_WAIT = 0;

    public static final int STATE_PASS = 1;
    /**
     * 用户名
     */
    private String username;

    /**
     * 认证状态
     */
    private int auth_status;

    /**
     * 认证地区（0=>大陆，1=>台湾，2=>香港，3=>澳门，默认为0）
     */
    private int auth_area;

    /**
     * 企业名称
     */
    private String company;
    /**
     * 企业法人
     */
    private String legal;
    /**
     * 法人身份证号
     */
    private String legal_id_card;
    /**
     * 注册号
     */
    private String licen_num;
    /**
     * 营业执照
     */
    private String licen_pic;
    /**
     * 主营范围
     */
    private String turnover;

    /**
     * 联系电话
     */
    private String telephone;

    /**
     * 企业所属省
     */
    private String area_prov;

    /**
     * 企业所属市
     */
    private String area_city;
    /**
     * 企业所属区
     */
    private String area_dist;

    /**
     * 成立日期
     */
    private long ent_start_time;

    /**
     * 营业截止日期
     */
    private long ent_end_time;

    /**
     * 用户所属地区
     */
    private String residency;

    /**
     * 详细地址
     */
    private String licen_address;

    /**
     * 用户所属行业编号
     */
    private int indus_pid;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAuth_status() {
        return auth_status;
    }

    public int getAuth_area() {
        return auth_area;
    }

    public void setAuth_area(int auth_area) {
        this.auth_area = auth_area;
    }

    public void setAuth_status(int auth_status) {
        this.auth_status = auth_status;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLegal() {
        return legal;
    }

    public void setLegal(String legal) {
        this.legal = legal;
    }

    public String getLegal_id_card() {
        return legal_id_card;
    }

    public void setLegal_id_card(String legal_id_card) {
        this.legal_id_card = legal_id_card;
    }

    public String getLicen_num() {
        return licen_num;
    }

    public void setLicen_num(String licen_num) {
        this.licen_num = licen_num;
    }

    public String getLicen_pic() {
        return licen_pic;
    }

    public void setLicen_pic(String licen_pic) {
        this.licen_pic = licen_pic;
    }

    public String getTurnover() {
        return turnover;
    }

    public void setTurnover(String turnover) {
        this.turnover = turnover;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getArea_prov() {
        return area_prov;
    }

    public void setArea_prov(String area_prov) {
        this.area_prov = area_prov;
    }

    public String getArea_city() {
        return area_city;
    }

    public void setArea_city(String area_city) {
        this.area_city = area_city;
    }

    public String getArea_dist() {
        return area_dist;
    }

    public void setArea_dist(String area_dist) {
        this.area_dist = area_dist;
    }

    public long getEnt_end_time() {
        return ent_end_time;
    }

    public void setEnt_end_time(long ent_end_time) {
        this.ent_end_time = ent_end_time * 1000;
    }

    public long getEnt_start_time() {
        return ent_start_time;
    }

    public void setEnt_start_time(long ent_start_time) {
        this.ent_start_time = ent_start_time * 1000;
    }

    public String getResidency() {
        return residency;
    }

    public void setResidency(String residency) {
        this.residency = residency;
    }

    public int getIndus_pid() {
        return indus_pid;
    }

    public void setIndus_pid(int indus_pid) {
        this.indus_pid = indus_pid;
    }

    public boolean isLicenceLongPeriod() {
        return getEnt_end_time() == 1000;
    }

    public String getLicen_address() {
        return licen_address;
    }

    public void setLicen_address(String licen_address) {
        this.licen_address = licen_address;
    }
}
