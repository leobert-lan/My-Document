package com.lht.pan_android.mpv.model;

import java.lang.ref.WeakReference;

import org.apache.http.Header;

import com.lht.pan_android.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.content.Context;

/**
 * @ClassName: RegisteredCheckModel
 * @Description: TODO
 * @date 2016年5月26日 上午11:03:52
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class RegisteredCheckModel {

	private HttpUtil mHttpUtil;
	private WeakReference<IRegisteredCheckModelCallback> callback;

	public RegisteredCheckModel(IRegisteredCheckModelCallback callback) {
		this.callback = new WeakReference<RegisteredCheckModel.IRegisteredCheckModelCallback>(callback);
		mHttpUtil = new HttpUtil();
	}

	private IRegisteredCheckModelCallback getCallback() {
		return callback.get();
	}

	public void checkRegistered(Context context, String phoneNum) {
		mHttpUtil.getWithParams(context, "", null, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub

				if (getCallback() != null) {
					// ...
				}

			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				if (getCallback() != null) {
					getCallback().onRequestFailure(arg0);
				}
			}
		});
	}

	public interface IRegisteredCheckModelCallback {
		void hanlde(String phoneNum, boolean isRegistered);

		void onRequestFailure(int httpStatus);
	}

}
