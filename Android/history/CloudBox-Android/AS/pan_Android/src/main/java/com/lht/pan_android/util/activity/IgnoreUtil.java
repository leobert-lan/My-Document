package com.lht.pan_android.util.activity;

import org.apache.http.Header;

import com.alibaba.fastjson.JSON;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.util.internet.HttpRequestFailureUtil;
import com.lht.pan_android.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;

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
public class IgnoreUtil implements IUrlManager.LoginUrl {

	private final HttpUtil mHttpUtil;
	private String username;
	private String accessId;
	private String accessToken;
	private Context mContext;
	private SharedPreferences sharedPreferences;

	public IgnoreUtil(Context mContext2) {
		this.mContext = mContext2;
		mHttpUtil = new HttpUtil();
		if (sharedPreferences == null) {
			sharedPreferences = mContext2.getSharedPreferences(IKeyManager.Token.SP_TOKEN, Context.MODE_PRIVATE);
		}
		username = sharedPreferences.getString(KEY_USERNAME, "");
		accessId = sharedPreferences.getString(IKeyManager.Token.KEY_ACCESS_ID, "");
		accessToken = sharedPreferences.getString(IKeyManager.Token.KEY_ACCESS_TOKEN, "");
	}

	private IgnoreCallBack mCallBack = null;
	private int position;

	public void setCallBack(IgnoreCallBack callBack, int position) {
		this.mCallBack = callBack;
		this.position = position;
	}

	public void ignore(String shareId, String owner) {

		String url = IUrlManager.ShareIgnore.DOMAIN + IUrlManager.ShareIgnore.MINEADDRESS + owner
				+ IUrlManager.ShareIgnore.SHARE + shareId + IUrlManager.ShareIgnore.FUNCTION;

		url += "?queryUsername=" + username + "&" + IKeyManager.Token.KEY_ACCESS_ID + "=" + accessId + "&"
				+ IKeyManager.Token.KEY_ACCESS_TOKEN + "=" + accessToken;
		mHttpUtil.postWithoutParams(url, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

				String string = JSON.toJSONString(arg2);
				mCallBack.onSuccess(position);
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				HttpRequestFailureUtil failureUtil = new HttpRequestFailureUtil(mContext);
				failureUtil.handleFailureWithCode(arg0, true);
			}
		});
	}

	public interface IgnoreCallBack {
		/**
		 * @Title: onSuccess
		 * @Description: 回调一般实现跳转
		 * @author: leobert.lan
		 */
		void onSuccess(int position);

	}

}