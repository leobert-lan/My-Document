package com.lht.creationspace.social.oauth;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.util.debug.DLog;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.models.User;

/**
 * <p><b>Package</b> com.lht.creationspace.tplogin
 * <p><b>Project</b> czspace
 * <p><b>Classname</b> SinaUserQueryCallback
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2017/2/27.
 */

public class SinaUserQueryCallback implements RequestListener {

    private final IOauthPresenter iOauthPresenter;
    private final TPOauthUserBean savedBean;


    public SinaUserQueryCallback(TPOauthUserBean bean, IOauthPresenter presenter) {
        this.savedBean = bean;
        this.iOauthPresenter = presenter;
    }

    @Override
    public void onComplete(String response) {
        if (!TextUtils.isEmpty(response)) {
            // 调用 User#parse 将JSON串解析成User对象
            User user = User.parse(response);
            savedBean.setNickname(user.screen_name);
            savedBean.setAvatar(user.profile_image_url);
        }
        DLog.d(getClass(), "userinfo" + JSON.toJSONString(savedBean));
        iOauthPresenter.onUserInfoGet(savedBean);
    }

    @Override
    public void onWeiboException(WeiboException e) {
        DLog.d(getClass(), "query weibo info got exception:\r\n" + e.getMessage());
        iOauthPresenter.onUserInfoGet(savedBean);
        e.printStackTrace();
    }
}
