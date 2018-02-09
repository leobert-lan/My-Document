package com.lht.pan_android.util.thirdPartyLogin;

import org.json.JSONObject;

import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.text.TextUtils;
import android.util.Log;

/**
 * @ClassName: QQOAuthListener
 * @Description: TODO
 * @date 2016年3月16日 上午11:01:08
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class QQOAuthListener implements IUiListener {

	private Tencent mTencent;

	private ITPLoginViewPresenter mLoginViewPresenter;

	public QQOAuthListener(Tencent tencent, ITPLoginViewPresenter presenter) {
		mTencent = tencent;
		this.mLoginViewPresenter = presenter;
	}

	@Override
	public void onError(UiError arg0) {
		String tag = "lmsg";
		cancelWait();
		Log.d(tag, "on error");
	}

	@Override
	public void onComplete(Object response) {

		String tag = "loginactivity";

		cancelWait();

		TPLoginVerifyBean bean = new TPLoginVerifyBean();
		bean.setType(TPLoginVerifyBean.TYPE_QQ);

		if (null == response) {
			Log.e(tag, "qq login info:\r\n null");
			bean.setSuccess(false);
			mLoginViewPresenter.onVarifyDecoded(bean);
			return;
		}
		JSONObject jsonResponse = (JSONObject) response;
		if (null != jsonResponse && jsonResponse.length() == 0) {
			Log.e(tag, "qq login info:\r\n 返回为空");
			bean.setSuccess(false);
			mLoginViewPresenter.onVarifyDecoded(bean);
			return;
		}

		try {
			String token = jsonResponse.getString(Constants.PARAM_ACCESS_TOKEN);
			String expires = jsonResponse.getString(Constants.PARAM_EXPIRES_IN);
			String openId = jsonResponse.getString(Constants.PARAM_OPEN_ID);
			if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
				mTencent.setAccessToken(token, expires);
				mTencent.setOpenId(openId);
				bean.setSuccess(true);
				bean.setUniqueId(openId);
				mLoginViewPresenter.onVarifyDecoded(bean);
			}

			Log.e(tag, "qq login info:\r\n" + "token:" + token + "\r\nexpires:" + expires + "\r\nopenid:" + openId
					+ "\r\ntotal length" + jsonResponse.length() + "\r\n content:" + jsonResponse.toString());

		} catch (Exception e) {
			bean.setSuccess(false);
			Log.e(tag, "qq login exception");
			mLoginViewPresenter.onVarifyDecoded(bean);
		}

	}

	@Override
	public void onCancel() {
		String tag = "lmsg";
		Log.d(tag, "qq login oncancel");
		cancelWait();
	}

	private void cancelWait() {
		mLoginViewPresenter.onTPPullUpFinish();
	}

}
