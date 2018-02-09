package com.lht.cloudjob.mvp.model.bean;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.bean
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> FavorListItemResBean
 * <p><b>Description</b>: 关注列表元素数据模型，注意列表
 * <br> {
 * <br> "ret": 13340,
 * <br> "status": 1,
 * <br> "message": "我的关注列表查询成功",
 * <br> "data":[
 * <br> {
 * <br> "obj_name": "86332295",
 * <br> "on_date": "1433221423",
 * <br> "nickname": "4214124215215",
 * <br> "avatar": "http://www.vsochina.com/data/avatar/default/man_small.jpg",
 * <br> "indus_name": "平面设计",
 * <br> "already_favor": true,
 * <br> "user_type": 1,
 * <br> "user_type_str": "个人"
 * <br> },
 * <br> {},
 * <br> ...
 * <br> ]
 * <br> }
 * Created by leobert on 2016/8/11.
 */
public class FavorListItemResBean {

    /**
     * 请求区间内无数据
     */
    public static final int NULL_IN_INTERVAL = 13862;

    /**
     * 被关注人用户名
     */
    private String obj_name;

    /**
     * 关注时间
     */
    private long on_date;

    /**
     * 被关注人昵称
     */
    private String nickname;

    /**
     * 被关注人头像
     */
    private String avatar;

    /**
     * 被关注人所属行业
     */
    private String indus_name;

    /**
     * obj_username是否已经关注username,对方是否关注查询人
     */
    private boolean already_favor;

    /**
     * 被关注人用户类型（1=>个人，2=>企业，3=>工作室，6=>校园）
     */
    private int user_type;

    /**
     * 被关注人用户类型别名（个人，企业，工作室，校园）
     */
    private String user_type_str;


    public String getObj_name() {
        return obj_name;
    }

    public void setObj_name(String obj_name) {
        this.obj_name = obj_name;
    }

    public long getOn_date() {
        return on_date;
    }

    public void setOn_date(long on_date) {
        //trans to millis
        this.on_date = on_date * 1000;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getIndus_name() {
        return indus_name;
    }

    public void setIndus_name(String indus_name) {
        this.indus_name = indus_name;
    }

    public boolean isAlready_favor() {
        return already_favor;
    }

    public void setAlready_favor(boolean already_favor) {
        this.already_favor = already_favor;
    }

    public int getUser_type() {
        return user_type;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }

    public String getUser_type_str() {
        return user_type_str;
    }

    public void setUser_type_str(String user_type_str) {
        this.user_type_str = user_type_str;
    }
}
