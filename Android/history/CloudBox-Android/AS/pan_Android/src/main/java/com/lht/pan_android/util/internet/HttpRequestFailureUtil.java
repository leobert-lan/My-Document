package com.lht.pan_android.util.internet;

import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.activity.asyncProtected.LoginActivity;
import com.lht.pan_android.util.CloudBoxApplication;
import com.lht.pan_android.util.SPUtil;
import com.lht.pan_android.util.ToastUtil;
import com.lht.pan_android.util.ToastUtil.Duration;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * @ClassName: HttpRequestFailureUtil
 * @Description: TODO
 * @date 2015年11月25日 上午9:52:13
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class HttpRequestFailureUtil {

	private Context mContext;

	/**
	 * TODO attention
	 * err401Handlers:这个是我预留的，可以将一些难以在生命周期内控制的事情，用接口回调，但是必须改为单例模式，且为static
	 * 先弃用，如果真的有必要，需要按照上面的修改
	 */
//	@Deprecated
//	private ArrayList<Handler401> err401Handlers;

	public HttpRequestFailureUtil(Context context) {
		this.mContext = context;
//		err401Handlers = new ArrayList<Handler401>();
	}

	public void handleFailureWithCode(int statusCode, boolean needToast) {
		if (needToast || statusCode == 401)
			toastInternetFailureByCode(statusCode);
		switch (statusCode) {
		case 401:
			handle401();
			break;

		default:
			break;
		}
	}

	private void handle401() {
		CloudBoxApplication.finishAll();
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(IKeyManager.Token.SP_TOKEN,
				Context.MODE_PRIVATE);
		SPUtil.modifyString(sharedPreferences, IKeyManager.Token.KEY_ACCESS_TOKEN, "");

		mContext.startActivity(new Intent(mContext, LoginActivity.class));
	}

	/**
	 * @Title: toastInternetFailureByCode
	 * @Description: 提示性通知
	 * @author: leobert.lan
	 * @param requestCode
	 */
	private void toastInternetFailureByCode(int requestCode) {
		ToastUtil.show(mContext, getFailure(requestCode), Duration.l);
	}

	private int getFailure(int requestCode) {
		// better modify a set of failure code named by its means instead of int
		int rid = -1;
		switch (requestCode) {
		case 0:
			rid = R.string.no_internet;
			break;
		case 401:
			rid = R.string.token_overtime;
			break;
		case 404:
			rid = R.string.not_found;
			break;
		default:
			rid = R.string.other_error;
			break;
		}
		return rid;

	}

	/**
	 * @Title: toastVsoFailureByCode
	 * @Description: 根据vso返回码判断错误类型，可能需要接口返回码集
	 *               鉴于平台接口中心还没有全部统一，如果不便，使用返回消息中的字段信息反馈
	 * @author: leobert.lan
	 * @param ret
	 */
	public static void toastVsoFailureByCode(int ret) {

	}

}
