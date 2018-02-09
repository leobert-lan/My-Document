package com.lht.creationspace.social.oauth;

import android.app.Activity;
import android.content.Context;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.openapi.UsersAPI;

/**
 * @author leobert.lan
 * @version 1.0
 * @ClassName: SinaOAuth
 * @Description: TODO
 * @date 2016年3月4日 下午3:05:17
 */
public class SinaOAuth {

    public static void login(final Activity activity, SsoHandler sinaSsoHandler, IOauthPresenter presenter) {
        if (presenter != null)
            presenter.onTPPullUp();
        sinaSsoHandler.authorize(new SinaAuthListener(activity, presenter));

    }

    public static void logout(Context ctx) {
        AccessTokenKeeper.clear(ctx);
    }

    /**
     * 获取用户信息
     */
    public static void getUserInfo(final Activity activity, TPOauthUserBean bean, IOauthPresenter presenter) {
        Oauth2AccessToken mAccessToken = AccessTokenKeeper.readAccessToken(activity);
        if (mAccessToken.isSessionValid()) {
            UsersAPI usersAPI = new UsersAPI(activity, SinaConstants.APP_KEY, mAccessToken);
            long uid = Long.parseLong(mAccessToken.getUid());
            usersAPI.show(uid, new SinaUserQueryCallback(bean, presenter));
        }

    }
}
