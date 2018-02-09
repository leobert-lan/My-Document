package com.lht.pan_android.util.activity;

import org.apache.http.Header;

import com.alibaba.fastjson.JSON;
import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.activity.asyncProtected.RegisterActivity;
import com.lht.pan_android.bean.LoginDataBean;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.DLog.LogLocation;
import com.lht.pan_android.util.ToastUtil;
import com.lht.pan_android.util.ToastUtil.Duration;
import com.lht.pan_android.util.internet.HttpRequestFailureUtil;
import com.lht.pan_android.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.util.Log;

/**
 * @ClassName: RegisterUtil
 * @Description: TODO
 * @date 2016年1月12日 上午9:48:16
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class RegisterUtil {

	private final RegisterActivity mActivity;

	private HttpUtil mHttpUtil;

	private final static String tag = "registerUtil";

	public RegisterUtil(final RegisterActivity activity) {
		this.mActivity = activity;
		mHttpUtil = new HttpUtil();
	}

	/**
	 * @Title: getVerifyCode
	 * @Description: 获取验证码
	 * @author: leobert.lan
	 * @param mobile
	 */
	public void getVerifyCode(String mobile) {
		String url = IUrlManager.Register.DOMAIN + IUrlManager.Register.ADDRESS;
		RequestParams param = new RequestParams();
		param.add(IUrlManager.Register.KEY_MOBILE, mobile);
		mActivity.showWaitView(true);
		mHttpUtil.postWithParams(mActivity, url, param, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				if (arg2 != null && arg2.length > 0)
					DLog.d(RegisterUtil.class, new LogLocation(), "getverify return data:\r\n" + new String(arg2));
				smsSendRequestSuccessListener.OnSmsSendRequestSuccess();
				mActivity.cancelWaitView();
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				mActivity.cancelWaitView();
				if (arg0 == 400)
					ToastUtil.show(mActivity, R.string.register_illegalmoble, Duration.s);
				else {
					HttpRequestFailureUtil failureUtil = new HttpRequestFailureUtil(mActivity);
					failureUtil.handleFailureWithCode(arg0, true);
				}
			}
		});
	}

	/**
	 * @Title: checkVerifyCode
	 * @Description: 检验验证码
	 * @author: leobert.lan
	 * @param username
	 * @param verifyCode
	 */
	public void checkVerifyCode(String username, String verifyCode) {
		// TODO
		mActivity.showWaitView(true);
		String url = IUrlManager.LoginUrl.DOMAIN + IUrlManager.LoginUrl.LOGIN_CHECK;
		Log.d(tag, "check registerVerrify url:\r\n" + url);
		RequestParams params = new RequestParams();
		params.put(IUrlManager.LoginUrl.KEY_USERNAME, username);
		params.put(IUrlManager.LoginUrl.KEY_PASSWORD, verifyCode);
		params.put(IUrlManager.LoginUrl.KEY_APPKEY, IUrlManager.LoginUrl.APPKEY);

		mHttpUtil.postWithParams(mActivity, url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				if (arg2 != null && arg2.length > 0)
					DLog.d(RegisterUtil.class, "message:\r\n" + new String(arg2));
				LoginDataBean bean = JSON.parseObject(new String(arg2), LoginDataBean.class);
				verifyCheckedListerner.onVerifyChecked(true, bean.getUsername());
				mActivity.cancelWaitView();
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				mActivity.cancelWaitView();
				Log.d(tag, "status:" + arg0);
				if (arg0 == 0) {
					ToastUtil.show(mActivity, R.string.no_internet, Duration.s);
				} else
					verifyCheckedListerner.onVerifyChecked(false, null);
				// Log.d(tag, "f msg:" + new String(arg2));
			}
		});

	}

	/**
	 * @Title: resetPassword
	 * @Description: 重置密码
	 * @author: leobert.lan
	 * @param username
	 * @param oldPwd
	 *            即为验证码
	 * @param newPwd
	 */
	public void resetPassword(String username, String oldPwd, String newPwd) {
		mActivity.showWaitView(true);
		String url = IUrlManager.ResetPassword.DOMAIN + IUrlManager.ResetPassword.ADDRESS + username
				+ IUrlManager.ResetPassword.FUNCTION;
		Log.d(tag, "resetPwd url:" + url);
		RequestParams param = new RequestParams();
		param.add(IUrlManager.ResetPassword.KEY_OLDPWD, oldPwd);
		param.add(IUrlManager.ResetPassword.KEY_NEWPWD, newPwd);
		mHttpUtil.postWithParams(mActivity, url, param, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				mActivity.cancelWaitView();
				passwordResetListener.onPasswordReset(true);
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				mActivity.cancelWaitView();
				HttpRequestFailureUtil failureUtil = new HttpRequestFailureUtil(mActivity);
				failureUtil.handleFailureWithCode(arg0, true);
				passwordResetListener.onPasswordReset(false);
			}
		});
	}

	private OnVerifyCheckedListerner verifyCheckedListerner;

	public void setOnVerifyCheckedListerner(OnVerifyCheckedListerner l) {
		this.verifyCheckedListerner = l;
	}

	private OnPasswordResetListener passwordResetListener;

	public void setOnPasswordResetListener(OnPasswordResetListener l) {
		this.passwordResetListener = l;
	}

	private OnSmsSendRequestSuccessListener smsSendRequestSuccessListener;

	public void setOnSmsSendRequestSuccessListener(OnSmsSendRequestSuccessListener l) {
		this.smsSendRequestSuccessListener = l;
	}

	/**
	 * @ClassName: OnVerifyCheckedListerner
	 * @Description: 验证验证码是否正确接口
	 * @date 2016年1月12日 上午9:55:11
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	public interface OnVerifyCheckedListerner {
		void onVerifyChecked(boolean isVerifyed, String username);
	}

	/**
	 * @ClassName: OnPasswordResetListener
	 * @Description: 重设密码回调接口
	 * @date 2016年1月12日 上午9:58:03
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	public interface OnPasswordResetListener {
		void onPasswordReset(boolean isSuccess);
	}

	public interface OnSmsSendRequestSuccessListener {
		void OnSmsSendRequestSuccess();
	}

}
