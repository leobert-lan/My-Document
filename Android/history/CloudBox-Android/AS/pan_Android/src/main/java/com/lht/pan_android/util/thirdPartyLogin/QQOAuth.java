package com.lht.pan_android.util.thirdPartyLogin;

import com.lht.pan_android.util.CloudBoxApplication;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

/**
 * @ClassName: QQOAuth
 * @Description: TODO
 * @date 2016年3月3日 上午10:16:54
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class QQOAuth {
	private static final String TAG = "QQOAuth";

	// private static ITPLoginViewPresenter mItpLoginViewPresenter;
	//
	// public static ITPLoginViewPresenter getmItpLoginViewPresenter() {
	// return mItpLoginViewPresenter;
	// }
	//
	// public static void setmItpLoginViewPresenter(
	// ITPLoginViewPresenter mItpLoginViewPresenter) {
	// QQOAuth.mItpLoginViewPresenter = mItpLoginViewPresenter;
	// }

	/**
	 * @Title: login
	 * @Description: 登录 最好写一个 IUiListener的实现类，因为使用场景可能有几个
	 * @author: leobert.lan
	 * @param activity
	 * @param callback
	 */
	public static void login(final Activity activity, final IUiListener callback, ITPLoginViewPresenter presenter) {
		if (!CloudBoxApplication.mTencent.isSessionValid())
			CloudBoxApplication.mTencent.logout(activity);
		// setmItpLoginViewPresenter(presenter);
		if (presenter != null)
			presenter.onTPPullUp();
		CloudBoxApplication.mTencent.login(activity, "all", callback);
	}

	public static void login(final android.support.v4.app.Fragment fragment, final IUiListener callback,
			ITPLoginViewPresenter presenter) {
		// setmItpLoginViewPresenter(presenter);
		if (presenter != null)
			presenter.onTPPullUp();
		CloudBoxApplication.mTencent.login(fragment, "all", callback);
	}

	public static void logout(final Context ctx) {
		CloudBoxApplication.mTencent.logout(ctx);
	}

	/**
	 * @Title: getQAuthInfo
	 * @Description: 用于已经登录情况下再次获取缓存的信息
	 * @author: leobert.lan
	 */
	public static void getQAuthInfo(final Activity activity, IUiListener callback) {
		if (CloudBoxApplication.mTencent != null && CloudBoxApplication.mTencent.isSessionValid()) {
			UserInfo info = new UserInfo(activity, CloudBoxApplication.mTencent.getQQToken());
			info.getUserInfo(callback);
		} else {
			Log.wtf(TAG, "mTencent == null 或者 验证非法，好好检验");
		}

	}
}
