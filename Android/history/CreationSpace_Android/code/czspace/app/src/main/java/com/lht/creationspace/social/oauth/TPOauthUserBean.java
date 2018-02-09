package com.lht.creationspace.social.oauth;

import com.lht.creationspace.module.user.security.model.pojo.WeChatCodeBean;

/**
 * author leobert.lan
 * version 1.0
 * ClassName: TPOauthUserBean
 * Description: 三方授权信息、三方用户基本信息
 * date 2016年3月16日 上午11:07:10
 */
public class TPOauthUserBean {

    public static final int TYPE_QQ = 1;

    public static final int TYPE_SINA = 2;

    public static final int TYPE_WECHAT = 3;

    /**
     * type:第三方平台
     */
    private int type;

    /**
     * 授权是否成功
     */
    private boolean isSuccess = false;

    /**
     * uniqueId:第三方用户唯一标识
     */
    private String uniqueId;

    /**
     * 三方平台已有的昵称
     */
    private String nickname;

    /**
     * 三方平台已有的头像
     */
    private String avatar;

    /**
     * only works in the wechat-platform
     */
    private WeChatCodeBean weChatCodeBean;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
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

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public static int getTypeQq() {
        return TYPE_QQ;
    }

    public static int getTypeSina() {
        return TYPE_SINA;
    }

    public static int getTypeWechat() {
        return TYPE_WECHAT;
    }

    public WeChatCodeBean getWeChatCodeBean() {
        return weChatCodeBean;
    }

    public void setWeChatCodeBean(WeChatCodeBean weChatCodeBean) {
        this.weChatCodeBean = weChatCodeBean;
    }
}
