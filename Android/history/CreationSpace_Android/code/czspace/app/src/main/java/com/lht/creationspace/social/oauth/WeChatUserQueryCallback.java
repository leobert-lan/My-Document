package com.lht.creationspace.social.oauth;

import com.lht.creationspace.module.user.security.model.pojo.WeChatUserInfoBean;

/**
 * <p><b>Package</b> com.lht.creationspace.tplogin
 * <p><b>Project</b> czspace
 * <p><b>Classname</b> WeChatUserQueryCallback
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2017/2/27.
 */

public class WeChatUserQueryCallback {

    private final IOauthPresenter presenter;
    private final TPOauthUserBean savedBean;

    public WeChatUserQueryCallback(TPOauthUserBean bean, IOauthPresenter presenter) {
        this.presenter = presenter;
        this.savedBean = bean;
    }

    public void onFinish(WeChatUserInfoBean bean) {
        // 回调登录后续
        savedBean.setNickname(bean.getNickname());
        savedBean.setAvatar(bean.getHeadimgurl());
        presenter.onUserInfoGet(savedBean);
    }
}
