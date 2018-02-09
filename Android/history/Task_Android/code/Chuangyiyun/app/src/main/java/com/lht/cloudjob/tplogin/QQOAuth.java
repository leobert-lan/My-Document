package com.lht.cloudjob.tplogin;

import android.app.Activity;
import android.content.Context;

import com.lht.cloudjob.MainApplication;
import com.lht.cloudjob.util.debug.DLog;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;

/**
 * ClassName: QQOAuth
 * Description: TODO
 * date 2016年3月3日 上午10:16:54
 * 
 * author leobert.lan
 * version 1.0
 */
public class QQOAuth {
	private static final String TAG = "QQOAuth";

	/**
	 * Description: 登录 最好写一个 IUiListener的实现类，因为使用场景可能有几个
	 * @param activity
	 * @param callback
	 * @param presenter
	 */
	public static void login(final Activity activity, final IUiListener callback, ITPLoginViewPresenter presenter) {
		if (!MainApplication.getTencent().isSessionValid())
			MainApplication.getTencent().logout(activity);
		// setmItpLoginViewPresenter(presenter);
		if (presenter != null)
			presenter.onTPPullUp();
		MainApplication.getTencent().login(activity, "all", callback);
	}

	public static void login(final android.support.v4.app.Fragment fragment, final IUiListener callback,
			ITPLoginViewPresenter presenter) {
		// setmItpLoginViewPresenter(presenter);
		if (presenter != null)
			presenter.onTPPullUp();
		MainApplication.getTencent().login(fragment, "all", callback);
	}

	public static void logout(final Context ctx) {
		MainApplication.getTencent().logout(ctx);
	}

	/**
	 * Description: 用于已经登录情况下再次获取缓存的信息
	 */
	public static void getQAuthInfo(final Activity activity, IUiListener callback) {
		if (MainApplication.getTencent() != null && MainApplication.getTencent().isSessionValid()) {
			UserInfo info = new UserInfo(activity, MainApplication.getTencent().getQQToken());
			info.getUserInfo(callback);
		} else {
			DLog.wtf(QQOAuth.class, "mTencent == null 或者 验证非法，好好检验");
		}

	}
}
