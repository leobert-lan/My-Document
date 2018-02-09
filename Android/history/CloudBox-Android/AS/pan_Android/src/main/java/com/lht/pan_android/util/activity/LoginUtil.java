package com.lht.pan_android.util.activity;

import org.apache.http.Header;

import com.alibaba.fastjson.JSON;
import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.activity.asyncProtected.LoginActivity;
import com.lht.pan_android.bean.LoginDataBean;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.SPUtil;
import com.lht.pan_android.util.ToastUtil;
import com.lht.pan_android.util.ToastUtil.Duration;
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
public class LoginUtil implements IUrlManager.LoginUrl {

	private final Context mContext;

	private final HttpUtil mHttpUtil;

	public LoginUtil(Context context) {
		mContext = context;
		mHttpUtil = new HttpUtil();
	}

	private ILoginCallBack mCallBack = null;

	public void setCallBack(ILoginCallBack callBack) {
		this.mCallBack = callBack;
	}

	public void login(String u, String p) {
		if (u.equals("") || p.equals("")) {
			ToastUtil.show(mContext, R.string.login_input, Duration.s);
		} else {
			if (!((LoginActivity) mContext).isFinishing())
				(((LoginActivity) mContext)).showWaitView(true);
			if (mCallBack == null)
				throw new NullPointerException("mCallback is null,set it firstly");
			RequestParams params = new RequestParams();
			params.put(KEY_USERNAME, u);
			params.put(KEY_PASSWORD, p);
			params.put(KEY_APPKEY, APPKEY);
			mHttpUtil.postWithParams(mContext, DOMAIN + LOGIN_CHECK, params, new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
					DLog.d(LoginUtil.class, "login failure,status:" + arg0);
					if (arg0 == 0) {
						ToastUtil.show(mContext, R.string.no_internet, Duration.l);
					} else if (arg0 == 401) {
						ToastUtil.show(mContext, R.string.login_failure, Duration.l);
					}
					(((LoginActivity) mContext)).cancelWaitView();
				}

				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					(((LoginActivity) mContext)).cancelWaitView();
					String returnData = new String(arg2);

					LoginDataBean loginDataBean = JSON.parseObject(returnData, LoginDataBean.class);
					SharedPreferences sp = mContext.getSharedPreferences(IKeyManager.Token.SP_TOKEN,
							Context.MODE_PRIVATE);
					SPUtil.modifyString(sp, IKeyManager.Token.KEY_ACCESS_ID, loginDataBean.getAccess_id());
					SPUtil.modifyString(sp, IKeyManager.Token.KEY_ACCESS_TOKEN, loginDataBean.getAccess_token());
					SPUtil.modifyString(sp, IKeyManager.Token.KEY_USERNAME, loginDataBean.getUsername());
					// 友盟统计登录
					MobclickAgent.onProfileSignIn(loginDataBean.getUsername());
					mCallBack.onSuccess();
				}
			});
		}
	}

	public void destroy() {
		mHttpUtil.getClient().cancelRequests(mContext, true);
	}

	public interface ILoginCallBack {
		/**
		 * @Title: onSuccess
		 * @Description: 回调一般实现跳转
		 * @author: leobert.lan
		 */
		void onSuccess();

	}

}
