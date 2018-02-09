package com.lht.cloudjob.mvp.model.bean;

import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.RegisterModel;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.bean
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> RegisterResBean
 * <p><b>Description</b>: TODO
 * <p/>
 * <p/>
 * 返回示例和返回说明:
 * <p/>
 * HTTP/1.1 200 OK
 * {
 * "ret": 13760,
 * "message": "注册成功",
 * "data": {
 * "uid": "1",
 * "username": "admin",
 * "nickname": "343445",
 * "email": "",
 * "mobile": "15050431508",
 * "user_type": "1",
 * "avatar": "http://static.vsochina.com/data/avatar/000/00/00/01_avatar_middle.jpg",
 * "isnewpwd": 2,  // 是否需要重置密码（1=>不需要，2=>需要），用于三方
 * }
 * }
 * <p/>
 * 返回码表 13760 注册成功
 * <p/>
 * 错误码 => 错误信息
 * "13001" => "缺少密码",
 * "13181" => "缺少手机号",
 * "13182" => "手机号不合法",
 * "13183" => "手机号被占用",
 * "13760" => "注册成功",
 * "13761" => "注册失败"
 * <p/>
 * Created by leobert on 2016/7/21.
 *
 * to see Model at{@link RegisterModel}
 * to see API at{@link IRestfulApi.RegisterApi}
 */
public class RegisterResBean {

    private String uid;
    //        * "uid": "1",
    private String username;
    private String nickname;
    private String email;
    private String mobile;
    /**
     * 1 个人，2 企业
     */
    private int user_type;
    private String avatar;
    private int isnewpwd;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

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

    public int getUser_type() {
        return user_type;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getIsnewpwd() {
        return isnewpwd;
    }

    public void setIsnewpwd(int isnewpwd) {
        this.isnewpwd = isnewpwd;
    }
}
