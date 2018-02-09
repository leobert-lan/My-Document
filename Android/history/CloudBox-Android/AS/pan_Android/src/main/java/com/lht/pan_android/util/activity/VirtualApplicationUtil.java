package com.lht.pan_android.util.activity;

import org.apache.http.Header;

import com.alibaba.fastjson.JSON;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.bean.VirtualAppBean;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.internet.HttpUtil;
import com.lht.pan_android.util.string.StringUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.widget.Toast;

/**
 * @ClassName: LoginUtil
 * @Description: 登录验证助手
 * @date 2015年11月25日 上午10:45:16
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class VirtualApplicationUtil implements IUrlManager.VirtualApplication {

	private final Context mContext;

	private final HttpUtil mHttpUtil;

	private SharedPreferences sharedPreferences;
	private String username;
	private String accessId;
	private String accessToken;

	float width;
	float height;

	public VirtualApplicationUtil(Context context) {
		mContext = context;
		mHttpUtil = new HttpUtil();
		if (sharedPreferences == null) {
			sharedPreferences = context.getSharedPreferences(IKeyManager.Token.SP_TOKEN, Context.MODE_PRIVATE);
		}
		username = sharedPreferences.getString(IKeyManager.Token.KEY_USERNAME, "");
		accessId = sharedPreferences.getString(IKeyManager.Token.KEY_ACCESS_ID, "");
		accessToken = sharedPreferences.getString(IKeyManager.Token.KEY_ACCESS_TOKEN, "");

		DisplayMetrics dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;// / 2;
		height = dm.heightPixels ;/// 2;
	}

	private VirtualAppCallBack mCallBack = null;

	public void setCallBack(VirtualAppCallBack callBack) {
		this.mCallBack = callBack;
	}

	public void openVirtualApp(String path) {
		if (mCallBack == null)
			throw new NullPointerException("mCallback is null,set it firstly");
		if (path.equals("")) {
			mCallBack.onFailure(-1);
			return;
		} else {

			String url = DOMAIN + USERS + username + FUNCTION + "path=" + path + "&width=" + height + "&height=" + width
					+ "&access_token=" + accessToken + "&access_id=" + accessId;

			mHttpUtil.get(url, new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					String returnData = new String(arg2);
					VirtualAppBean virtualAppBean = JSON.parseObject(returnData, VirtualAppBean.class);
					String virtualUrl = virtualAppBean.getUrl();
					DLog.i(getClass(), "虚拟应用获取url：" + virtualUrl);
					int flagCode = virtualAppBean.getFlagCode();
					if (flagCode == 0) {
						if (StringUtil.isEmpty(virtualUrl)) {
							mCallBack.onNotSupport();
							return;
						}
						mCallBack.onSuccess(virtualUrl);
					} else {
						mCallBack.onNotSupport();
					}
				}

				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
					if (arg0 == 0) {
						Toast.makeText(mContext, "服务器连接异常，请稍后重试！", Toast.LENGTH_SHORT).show();
					}
					mCallBack.onFailure(arg0);
				}
			});
		}
	}

	public interface VirtualAppCallBack {
		/**
		 * @Title: onSuccess
		 * @Description: 回调一般实现跳转
		 * @author: leobert.lan
		 */
		void onSuccess(String url);

		/**
		 * @Title: onFailure
		 * @Description: 访问有问题
		 * @author: leobert.lan
		 */
		void onFailure(int httpStatus);

		/**
		 * @Title: onNotSupport
		 * @Description: 暂不支持
		 * @author: leobert.lan
		 */
		void onNotSupport();
	}
}
