package com.lht.pan_android.util.activity;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

/**
 * @ClassName: LoginUtil
 * @Description: 登录验证助手
 * @date 2015年11月25日 上午10:45:16
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class ShareDeleteUtil implements IUrlManager.LoginUrl {

	// private final ShareActivity mContext;

	private final HttpUtil mHttpUtil;
	private String username;
	private String accessId;
	private String accessToken;
	private Context mContext;
	private SharedPreferences sharedPreferences;

	public ShareDeleteUtil(Context mContext) {
		this.mContext = mContext;
		mHttpUtil = new HttpUtil();
		if (sharedPreferences == null) {
			sharedPreferences = mContext.getSharedPreferences(IKeyManager.Token.SP_TOKEN, Context.MODE_PRIVATE);
		}
		username = sharedPreferences.getString(KEY_USERNAME, "");
		accessId = sharedPreferences.getString(IKeyManager.Token.KEY_ACCESS_ID, "");
		accessToken = sharedPreferences.getString(IKeyManager.Token.KEY_ACCESS_TOKEN, "");
	}

	private DeleteCallBack mCallBack = null;
	private int position;

	public void setCallBack(DeleteCallBack callBack, int position) {
		this.mCallBack = callBack;
		this.position = position;
	}

	public void delete(String shareId) {
		String url = IUrlManager.ShareDelete.DOMAIN + IUrlManager.ShareDelete.MINEADDRESS + username
				+ IUrlManager.ShareDelete.SHARE + IKeyManager.Token.KEY_ACCESS_ID + "=" + accessId + "&"
				+ IKeyManager.Token.KEY_ACCESS_TOKEN + "=" + accessToken;

		mHttpUtil.delete(mContext, url, getDeleteItemEntity(shareId), "application/json",
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						mCallBack.onSuccess(position);
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
						// TODO hard-code
						Toast.makeText(mContext, "取消分享分享失败，请稍后再试", Toast.LENGTH_SHORT).show();
					}
				});
	}

	protected StringEntity getDeleteItemEntity(String shareId) {
		JSONObject jObj = new JSONObject();
		StringEntity ret = null;
		try {
			jObj.put("shareId", shareId);
			ret = new StringEntity(jObj.toString(), HTTP.UTF_8);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public interface DeleteCallBack {
		/**
		 * @Title: onSuccess
		 * @Description: 成功的回调
		 * @author: zhangbin
		 * @param position
		 */
		void onSuccess(int position);
	}
}