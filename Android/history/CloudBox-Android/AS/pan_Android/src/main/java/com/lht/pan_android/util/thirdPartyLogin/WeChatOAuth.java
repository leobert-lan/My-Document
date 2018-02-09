package com.lht.pan_android.util.thirdPartyLogin;

import java.util.HashMap;

import org.apache.http.Header;

import com.alibaba.fastjson.JSON;
import com.lht.pan_android.util.CloudBoxApplication;
import com.lht.pan_android.util.internet.HttpUtil;
import com.lht.pan_android.util.share.WeChatCodeBean;
import com.lht.pan_android.util.share.WeChatUserInfoBean;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

/**
 * @ClassName: WeChatOAuth
 * @Description: TODO
 * @date 2016年3月16日 上午11:20:23
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class WeChatOAuth implements IWXAPIEventHandler {

	// private static ITPLoginViewPresenter mItpLoginViewPresenter;
	//
	// public static ITPLoginViewPresenter getmItpLoginViewPresenter() {
	// return mItpLoginViewPresenter;
	// }
	//
	// public static void setmItpLoginViewPresenter(ITPLoginViewPresenter
	// mItpLoginViewPresenter) {
	// WeChatOAuth.mItpLoginViewPresenter = mItpLoginViewPresenter;
	// }

	private static HashMap<String, IWechatOAuthCallback> callbacks = new HashMap<String, IWechatOAuthCallback>();

	public static void login(final Activity activity, ITPLoginViewPresenter presenter) {
		if (presenter != null)
			presenter.onTPPullUp();
		loginWithWeixin(activity, new WechatOAuthListener(presenter));
	}

	private static void loginWithWeixin(final Activity activity, IWechatOAuthCallback callback) {

		IWXAPI api = CloudBoxApplication.getmWeChat();
		if (!api.isWXAppInstalled()) {
			Toast.makeText(activity, "没有安装微信", Toast.LENGTH_SHORT).show();
			return;
		}
		api.registerApp(WeChatConstants.APP_ID);

		final SendAuth.Req req = new SendAuth.Req();
		req.scope = "snsapi_userinfo";
		req.state = "com.lht.pan_android.session";

		// TODO 业务
		String transaction = buildTransaction("login");
		req.transaction = transaction;
		callbacks.put(transaction, callback);
		api.sendReq(req);
	}

	private static String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		String weChatCode;

		Log.e("lmsg", "check response:\r\n" + resp.errCode + ".." + resp.errStr + ".." + resp.openId + ".."
				+ resp.transaction);

		String result = null;

		// if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
		// IWechatOAuthCallback callback = callbacks.get(resp.transaction);
		// if (callback!=null) {
		// Log.d("wechat", "call mItpLoginViewPresenter");
		// callback.onTPPullUpFinish();
		// } else {
		// Log.e("wechat", "null getmItpLoginViewPresenter");
		// }
		// }
		// TODO 区分业务
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:

			if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
				result = "授权成功";
				weChatCode = ((SendAuth.Resp) resp).code;
				IWechatOAuthCallback callback = callbacks.get(resp.transaction);
				getTokenAndId(weChatCode, callback);
				callbacks.remove(resp.transaction);
				Log.e("lmsg", "util 登录时成功的回调");
			} else {
				result = "发送成功";
			}

			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = "发送取消";
			Toast.makeText(CloudBoxApplication.getMainAppContext(), result, Toast.LENGTH_LONG).show();
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = "发送被拒绝";
			Toast.makeText(CloudBoxApplication.getMainAppContext(), result, Toast.LENGTH_LONG).show();
			break;
		default:
			result = "发送返回";
			Toast.makeText(CloudBoxApplication.getMainAppContext(), result, Toast.LENGTH_LONG).show();
			break;
		}

		Toast.makeText(CloudBoxApplication.getMainAppContext(), result, Toast.LENGTH_LONG).show();
	}

	/**
	 * @Title: getTokenAndId
	 * @Description: 刷新token和id
	 * @author: leobert.lan
	 * @param callback
	 * @param codeUrl
	 */
	private void getTokenAndId(String weChatCode, final IWechatOAuthCallback callback) {
		HttpUtil httpUtil = new HttpUtil();

		String codeUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WeChatConstants.APP_ID
				+ "&secret=" + WeChatConstants.APP_SECRET + "&code=" + weChatCode + "&grant_type=authorization_code";

		httpUtil.get(codeUrl, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				TPLoginVerifyBean bean = new TPLoginVerifyBean();
				bean.setType(TPLoginVerifyBean.TYPE_WECHAT);
				bean.setSuccess(false);
				callback.onFinish(bean);
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				String codeData = new String(arg2);
				WeChatCodeBean weChatCodeBean = JSON.parseObject(codeData, WeChatCodeBean.class);
				String access_token = weChatCodeBean.getAccess_token();
				String openId = weChatCodeBean.getOpenid();

				// 回调登录后续
				TPLoginVerifyBean bean = new TPLoginVerifyBean();
				bean.setType(TPLoginVerifyBean.TYPE_WECHAT);
				bean.setSuccess(true);
				bean.setUniqueId(openId);
				callback.onFinish(bean);
				// WeChatOAuth.getmItpLoginViewPresenter().onVarifyDecoded(bean);
				// WeChatOAuth.setmItpLoginViewPresenter(null);

				// get user's information
				// getUserInfo(access_token_Url);
			}
		});
	}

	public static void getUserInfo(String accessToken, String openId) {
		HttpUtil httpUtil = new HttpUtil();
		String access_token_Url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid="
				+ openId;
		httpUtil.get(access_token_Url, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

				String userInfo = new String(arg2);
				// Log.e(TAG, "个人信息" + JSON.toJSON(userInfo));
				WeChatUserInfoBean weChatUserInfoBean = JSON.parseObject(userInfo, WeChatUserInfoBean.class);
				// 先不使用广播了
				// broadCastWeChatUserInfo(JSON.toJSONString(weChatUserInfoBean));
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				// Log.e(TAG, "http failure");
			}
		});
	}

}
