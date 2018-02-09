package com.lht.creationspace.social.share;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;

/**
 * Created by chhyu on 2017/4/27.
 */

public class WeChatShareImageBean extends ShareBean {

    private String localImagePath;

    private int flag;

    public static final int FLAG_WECHATFC = SendMessageToWX.Req.WXSceneTimeline;

    public static final int FLAG_WECHAT = SendMessageToWX.Req.WXSceneSession;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getLocalImagePath() {
        return localImagePath;
    }

    public void setLocalImagePath(String localImagePath) {
        this.localImagePath = localImagePath;
    }
}
