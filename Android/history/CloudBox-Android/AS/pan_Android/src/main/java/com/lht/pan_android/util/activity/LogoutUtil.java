package com.lht.pan_android.util.activity;

import org.apache.http.Header;

import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.util.internet.HttpRequestFailureUtil;
import com.lht.pan_android.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @ClassName: LoginUtil
 * @Description: 登录验证助手
 * @date 2015年11月25日 上午10:45:16
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class LogoutUtil {

	private SharedPreferences sharedPreferences;

	private final Context mContext;

	private final HttpUtil mHttpUtil;

	private final HttpRequestFailureUtil mHttpRequestFailureUtil;

	public LogoutUtil(Context context) {
		mContext = context;
		mHttpUtil = new HttpUtil();
		mHttpRequestFailureUtil = new HttpRequestFailureUtil(context);
	}

	public void logout() {
		if (sharedPreferences == null) {
			sharedPreferences = mContext.getSharedPreferences(IKeyManager.Token.SP_TOKEN, Context.MODE_PRIVATE);
		}
		MobclickAgent.onProfileSignOff();

		String username = sharedPreferences.getString(IKeyManager.Token.KEY_USERNAME, "");
		String access_token = sharedPreferences.getString(IKeyManager.Token.KEY_ACCESS_TOKEN, "");

		RequestParams requestParams = new RequestParams();
		requestParams.put("username", username);
		requestParams.put("access_token", access_token);
		requestParams.put("appkey", "YPMB");
		mHttpUtil.postWithParams(mContext, IUrlManager.LOGOUT_CHECK, requestParams, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
			}
		});
	}

	public void destroy() {
		mHttpUtil.getClient().cancelRequests(mContext, true);
	}

}
