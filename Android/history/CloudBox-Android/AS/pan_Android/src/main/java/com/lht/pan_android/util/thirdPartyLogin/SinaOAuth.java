package com.lht.pan_android.util.thirdPartyLogin;

import com.sina.weibo.sdk.auth.sso.SsoHandler;

import android.app.Activity;
import android.content.Context;

/**
 * @ClassName: SinaOAuth
 * @Description: TODO
 * @date 2016年3月4日 下午3:05:17
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class SinaOAuth {

	public static void login(final Activity activity, SsoHandler sinaSsoHandler, ITPLoginViewPresenter presenter) {
		if (presenter != null)
			presenter.onTPPullUp();
		sinaSsoHandler.authorize(new DummySinaAuthListener(activity, presenter));
	}

	public static void logout(Context ctx) {
		AccessTokenKeeper.clear(ctx);
	}
}
