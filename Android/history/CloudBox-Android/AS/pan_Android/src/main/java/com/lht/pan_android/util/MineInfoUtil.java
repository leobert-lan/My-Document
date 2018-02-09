package com.lht.pan_android.util;

import org.apache.http.Header;

import com.alibaba.fastjson.JSON;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.Interface.UserInfoCallBack;
import com.lht.pan_android.bean.UserInfoBean;
import com.lht.pan_android.bean.UserInfoStorageBean;
import com.lht.pan_android.util.internet.HttpRequestFailureUtil;
import com.lht.pan_android.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * @ClassName: MineInfoUtil
 * @Description: 个人信息的网络请求
 * @date 2015年11月26日 上午8:28:06
 * 
 * @author zhang
 * @version 1.0
 * @since JDK 1.6
 */
public class MineInfoUtil {

	private SharedPreferences sharedPreferences;

	private final Context mContext;

	private final HttpUtil mHttpUtil;

	private final HttpRequestFailureUtil mHttpRequestFailureUtil;

	private UserInfoCallBack mCallback;

	private int GB = 1024 * 1024 * 1024;
	private int MB = 1024 * 1024;

	public MineInfoUtil(Context context) {
		mContext = context;
		mHttpUtil = new HttpUtil();
		mHttpRequestFailureUtil = new HttpRequestFailureUtil(context);
	}

	public void getUserInfo() {

		if (sharedPreferences == null) {
			sharedPreferences = mContext.getSharedPreferences(IKeyManager.Token.SP_TOKEN, Context.MODE_PRIVATE);
		}

		String access_id = sharedPreferences.getString(IKeyManager.Token.KEY_ACCESS_ID, "");
		String access_token = sharedPreferences.getString(IKeyManager.Token.KEY_ACCESS_TOKEN, "");

		String urlFormat = IUrlManager.MineInfoUrl.HOST + IUrlManager.MineInfoUrl.PATH;

		String username = sharedPreferences.getString(IKeyManager.Token.KEY_USERNAME, "");

		String urlString = String.format(urlFormat, username);

//		DLog.d(getClass(), "user info jiekou :" + urlString);
		Log.e("VSOApi", "info api:" + urlString);

		RequestParams requestParams = new RequestParams();
		requestParams.put("access_id", access_id);
		requestParams.put("access_token", access_token);

		queryStorageInfo(username, requestParams);

		mHttpUtil.getWithParams(mContext, urlString, requestParams, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				String UserInfoData = new String(arg2);

				UserInfoBean userInfoBean = JSON.parseObject(UserInfoData, UserInfoBean.class);
				if (userInfoBean.getUsername() != null) {
					String nickname = userInfoBean.getNickname();
					String imgUrl = userInfoBean.getIcon();
					mCallback.setBasicInfo(nickname, imgUrl);
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				mHttpRequestFailureUtil.handleFailureWithCode(arg0, true);
				arg3.printStackTrace();
			}
		});
	}

	private void queryStorageInfo(String username, RequestParams requestParams) {
		String urlFormat = IUrlManager.MineStorageInfoApi.HOST + IUrlManager.MineStorageInfoApi.PATH;

		String urlString = String.format(urlFormat, username);

		Log.e("VSOApi", "storage api:" + urlString);

		mHttpUtil.getWithParams(mContext, urlString, requestParams, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				String data = new String(arg2);

				UserInfoStorageBean userInfoStorageBean = JSON.parseObject(data, UserInfoStorageBean.class);
				if (userInfoStorageBean != null) {
					String used = userInfoStorageBean.getStorage().getUsed();
					String all = userInfoStorageBean.getStorage().getQuota();
					double bigUsed = Double.valueOf(used);
					double bigall = Double.valueOf(all);
					int i5 = (int) (bigUsed / bigall * 100);
					if (bigUsed / MB >= 1) {
						used = String.valueOf((Math.round(bigUsed / MB))).toString() + "MB";
					}
					if (bigUsed / GB >= 1) {
						used = String.valueOf((Math.round(bigUsed / GB))).toString() + "GB";
					}
					if (bigall / MB >= 1) {
						all = String.valueOf((Math.round(bigall / MB))).toString() + "MB";
					}
					if (bigall / GB >= 1) {
						all = String.valueOf((Math.round(bigall / GB))).toString() + "GB";
					}
					mCallback.setCapacityInfo(used, all, i5);
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				mHttpRequestFailureUtil.handleFailureWithCode(arg0, true);
				arg3.printStackTrace();
			}
		});

	}

	public void setCallBack(UserInfoCallBack callback) {
		this.mCallback = callback;
	}
}
