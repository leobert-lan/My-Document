package com.lht.cloudjob.mvp.model.bean;

import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.PAuthenticQueryModel;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.bean
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> PAutenticQueryResBean
 * <p><b>Description</b>: TODO
 * {
 * "username": "admin",
 * "id_card": "330702197108020812",
 * "id_pic": "http://account.vsochina.com/upload//2015/0115/300/1421299869.jpg",
 * "id_pic_2": "http://account.vsochina.com/upload//2015/0115/3f9/1421299869.jpg",
 * "id_pic_3": "http://account.vsochina.com/upload//2015/0115/3f9/1421299869.jpg",
 * "realname": "张三",
 * "auth_status": "1",
 * "start_time": "1421300108",
 * "auth_area": 0,
 * "validity_s_time": "1465920000",
 * "validity_e_time": "1467216000",
 * "nopass_des": "信息不全",
 * }
 * Created by leobert on 2016/8/5.
 * <p/>
 * to see Model at{@link PAuthenticQueryModel}
 * to see API at{@link IRestfulApi.QueryPAuthenticApi}
 */
public class PAuthenticQueryResBean {
    //* 返回码 => 错误信息：
//        * "13820" => "实名认证信息查询成功"
//        * "13862" => "数据不存在" 说明没有进行过认证
    public static final int RET_UNSUBMIT = 13862;

    public static final int STATE_WAIT = 0;

    public static final int STATE_PASS = 1;

    public static final int STATE_REFUSE = 2;

    /**
     *
     */
    private String username;
    /**
     * 身份证号
     */
    private String id_card;

    /**
     * 身份证正面图片
     */
    private String id_pic;

    /**
     * 身份证反面图片
     */
    private String id_pic_2;

    /**
     * 手持证照片
     */
    private String id_pic_3;

    /**
     * 真实姓名
     */
    private String realname;

    /**
     * 认证状态（0=>待审核，1=>通过，2=>驳回）
     */
    private int auth_status;

    /**
     * 认证时间
     */
    private String start_time;

    /**
     * 认证地区（0=>大陆，1=>台湾，2=>香港，3=>澳门）
     */
    private int auth_area;

    /**
     * 证件有效期开始时间
     */
    private long validity_s_time;

    /**
     * 证件有效期结束时间
     */
    private long validity_e_time;

    /**
     * 不通过审核说明
     */
    private String nopass_des;

    public static int getRetUnsubmit() {
        return RET_UNSUBMIT;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId_card() {
        return id_card;
    }

    public void setId_card(String id_card) {
        this.id_card = id_card;
    }

    public String getId_pic() {
        return id_pic;
    }

    public void setId_pic(String id_pic) {
        this.id_pic = id_pic;
    }

    public String getId_pic_2() {
        return id_pic_2;
    }

    public void setId_pic_2(String id_pic_2) {
        this.id_pic_2 = id_pic_2;
    }

    public String getId_pic_3() {
        return id_pic_3;
    }

    public void setId_pic_3(String id_pic_3) {
        this.id_pic_3 = id_pic_3;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public int getAuth_status() {
        return auth_status;
    }

    public void setAuth_status(int auth_status) {
        this.auth_status = auth_status;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public int getAuth_area() {
        return auth_area;
    }

    public void setAuth_area(int auth_area) {
        this.auth_area = auth_area;
    }

    public long getValidity_s_time() {
        return validity_s_time;
    }

    public void setValidity_s_time(long validity_s_time) {
        this.validity_s_time = validity_s_time * 1000;
    }

    public long getValidity_e_time() {
        return validity_e_time;
    }

    public void setValidity_e_time(long validity_e_time) {
        this.validity_e_time = validity_e_time * 1000;
    }

    public String getNopass_des() {
        return nopass_des;
    }

    public void setNopass_des(String nopass_des) {
        this.nopass_des = nopass_des;
    }
}
