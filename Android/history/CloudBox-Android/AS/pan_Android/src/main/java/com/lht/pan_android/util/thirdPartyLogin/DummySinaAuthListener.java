package com.lht.pan_android.util.thirdPartyLogin;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * @ClassName: DummySinaAuthListener
 * @Description: TODO
 * @date 2016年3月4日 下午3:16:16
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class DummySinaAuthListener implements WeiboAuthListener {

	private final static String tag = "sinaauth";

	private final Activity mActivity;

	private Oauth2AccessToken mAccessToken;

	private ITPLoginViewPresenter mItpLoginViewPresenter;

	public DummySinaAuthListener(final Activity activity, ITPLoginViewPresenter presenter) {
		mActivity = activity;
		mItpLoginViewPresenter = presenter;
		mAccessToken = AccessTokenKeeper.readAccessToken(mActivity);
	}

	@Override
	public void onComplete(Bundle values) {
		Log.d(tag, "complete");
		cancelWait();

		TPLoginVerifyBean bean = new TPLoginVerifyBean();
		bean.setType(TPLoginVerifyBean.TYPE_SINA);

		// 从 Bundle 中解析 Token

		mAccessToken = Oauth2AccessToken.parseAccessToken(values);
		// 从这里获取用户输入的 电话号码信息
		// String phoneNum = mAccessToken.getPhoneNum();
		if (mAccessToken.isSessionValid()) {
			// 显示 Token TODO
			Log.d(tag, "check result:\r\n" + mAccessToken.toString());
			// 保存 Token 到 SharedPreferences
			AccessTokenKeeper.writeAccessToken(mActivity, mAccessToken);

			bean.setSuccess(true);
			bean.setUniqueId(mAccessToken.getUid());

			Toast.makeText(mActivity, "授权成功", Toast.LENGTH_SHORT).show();
			Log.d(tag, "sina 授权成功");
		} else {
			// 以下几种情况，您会收到 Code：
			// 1. 当您未在平台上注册的应用程序的包名与签名时；
			// 2. 当您注册的应用程序包名与签名不正确时；
			// 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
			String code = values.getString("code");
			String message = "授权失败";
			if (!TextUtils.isEmpty(code)) {
				message = message + "\nObtained the code: " + code;
			}
			Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();

			bean.setSuccess(false);

			Log.d(tag, message);
		}

		// 回调后续操作
		mItpLoginViewPresenter.onVarifyDecoded(bean);

	}

	@Override
	public void onCancel() {
		Log.d(tag, "cancel");
		// Toast.makeText(mActivity, "授权取消", Toast.LENGTH_LONG).show();
		cancelWait();
	}

	@Override
	public void onWeiboException(WeiboException e) {
		Log.wtf(tag, "Auth exception : " + e.getMessage());
		// Toast.makeText(mActivity, "Auth exception : " + e.getMessage(),
		// Toast.LENGTH_LONG).show();
		cancelWait();
	}

	private void cancelWait() {
		mItpLoginViewPresenter.onTPPullUpFinish();
	}

}
