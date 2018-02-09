package com.lht.pan_android.util.activity;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;

import com.alibaba.fastjson.JSON;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.bean.SharePermissionBean;
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
public class SharePublicAndPrivateUtil implements IUrlManager.CheckToken, IUrlManager, IKeyManager.Token {

	private final Context mContext;

	private final HttpUtil mHttpUtil;

	private String username = "";

	private String accessId = "";

	private String accessToken = "";

	private SharedPreferences sharedPreferences;

	public SharePublicAndPrivateUtil(Context context) {
		mContext = context;
		mHttpUtil = new HttpUtil();

		if (sharedPreferences == null) {
			sharedPreferences = mContext.getSharedPreferences(IKeyManager.Token.SP_TOKEN, Context.MODE_PRIVATE);
		}
		username = sharedPreferences.getString(IKeyManager.Token.KEY_USERNAME, "");
		accessId = sharedPreferences.getString(IKeyManager.Token.KEY_ACCESS_ID, "");
		accessToken = sharedPreferences.getString(IKeyManager.Token.KEY_ACCESS_TOKEN, "");
	}

	public void sharePublic(String url, StringEntity stringEntity, final String filaName) {

		url += "?access_token=" + accessToken + "&access_id=" + accessId;
		mHttpUtil.postWithParams(mContext, getUrl() + url, stringEntity, "application/json",
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						String sharePulicData = new String(arg2);
						SharePermissionBean sharePermissionBean = JSON.parseObject(sharePulicData,
								SharePermissionBean.class);
						String link = sharePermissionBean.getLink();

						sharePublicCallBack.onSuccess(link, filaName);
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
						sharePublicCallBack.onFailure();
					}
				});
	}

	private String getUrl() {

		String url = IUrlManager.SharePublicAndPrivateUrl.DOMAIN + IUrlManager.SharePublicAndPrivateUrl.ADDRESS
				+ username;
		return url;

	}

	public interface SharePublicCallBack {
		/**
		 * @Title: onSuccess
		 * @Description: 回调成功
		 * @author: zhangbin
		 */
		void onSuccess(String link, String fileName);

		void onFailure();

	}

	private SharePublicCallBack sharePublicCallBack = null;

	public void setCallBack(SharePublicCallBack sharePublicCallBack) {
		this.sharePublicCallBack = sharePublicCallBack;
	}

}
