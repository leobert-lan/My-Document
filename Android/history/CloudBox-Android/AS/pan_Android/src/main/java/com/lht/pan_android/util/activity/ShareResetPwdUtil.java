package com.lht.pan_android.util.activity;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;

import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.IUrlManager;
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
public class ShareResetPwdUtil implements IUrlManager.CheckToken, IUrlManager, IKeyManager.Token {

	private final Context mContext;

	private final HttpUtil mHttpUtil;

	private String username = "";

	private String accessId = "";

	private String accessToken = "";

	private SharedPreferences sharedPreferences;

	public ShareResetPwdUtil(Context context) {
		mContext = context;
		mHttpUtil = new HttpUtil();

		if (sharedPreferences == null) {
			sharedPreferences = mContext.getSharedPreferences(IKeyManager.Token.SP_TOKEN, Context.MODE_PRIVATE);
		}
		username = sharedPreferences.getString(IKeyManager.Token.KEY_USERNAME, "");
		accessId = sharedPreferences.getString(IKeyManager.Token.KEY_ACCESS_ID, "");
		accessToken = sharedPreferences.getString(IKeyManager.Token.KEY_ACCESS_TOKEN, "");
	}

	public void shareResetPwd(String shareId, StringEntity stringEntity) {
		String url = getUrl() + shareId + "/password" + "?access_token=" + accessToken + "&access_id=" + accessId;
		mHttpUtil.postWithParams(mContext, url, stringEntity, "application/json", new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				shareResetPwdCallBack.onSuccess();
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
			}
		});
	}

	private String getUrl() {

		String url = IUrlManager.SharePublicAndPrivateUrl.DOMAIN + IUrlManager.SharePublicAndPrivateUrl.ADDRESS
				+ username + "/share/";
		return url;

	}

	public interface ShareResetPwdCallBack {
		/**
		 * @Title: onSuccess
		 * @Description: 回调成功
		 * @author: zhangbin
		 */
		void onSuccess();

	}

	private ShareResetPwdCallBack shareResetPwdCallBack = null;

	public void setCallBack(ShareResetPwdCallBack shareResetPwdCallBack) {
		this.shareResetPwdCallBack = shareResetPwdCallBack;
	}

}
