package com.lht.creationspace.module.user.register.model.pojo;

import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.module.user.register.model.RegisterModel;
import com.lht.creationspace.util.string.StringUtil;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.model.bean
 * <p><b>Project</b> VsoCyy
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
 * <p>
 * to see Model at{@link RegisterModel}
 */
public class RegisterResBean {

    public static final int RET_MOBILE_ILLEGAL = 13182;// => "手机号不合法",

    public static final int RET_MOBILE_CONFLICT = 13183;

    public static final int RET_ERROR_VALIDCOE = 13145;

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


    public static String parseMsgByRet(int ret, BaseVsoApiResBean bean) {
        String msg = null;
        switch (ret) {
            case RET_MOBILE_CONFLICT:
                msg = "账号已存在";
                break;
            case RET_MOBILE_ILLEGAL:
                msg = "请输入11位正确的手机号";
                break;
            case RET_ERROR_VALIDCOE:
                msg = "验证码错误，请重新输入";
                break;
            default:
                if (bean != null)
                    msg = bean.getMessage();
                if (StringUtil.isEmpty(msg))
                    msg = "注册失败";
                break;
        }
        return msg;
    }
}
