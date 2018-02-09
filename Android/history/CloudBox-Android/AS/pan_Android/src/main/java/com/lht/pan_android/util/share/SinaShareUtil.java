package com.lht.pan_android.util.share;

import com.lht.pan_android.util.thirdPartyLogin.AccessTokenKeeper;
import com.lht.pan_android.util.thirdPartyLogin.SinaConstants;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

/**
 * @ClassName: SinaShareUtil
 * @Description: TODO
 * @date 2016年3月8日 上午9:59:24
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class SinaShareUtil {

	private static String tag = "sinaShare";

	public static void shareByClient(final Context context) {
		registerApi(context);

		if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
			int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
			if (supportApi >= 10351 /* ApiUtils.BUILD_INT_VER_2_2 */) {
				Log.d(tag, "可以multi");
			} else {
				Log.d(tag, "只能single");
			}
		} else {
			Log.e(tag, "没有合理初始化");
		}

	}

	public static void shareByAPI(final Context context, final SinaShareBean bean) {
		registerApi(context);

		// 1. 初始化微博的分享消息
		WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
		// 可选
		if (!TextUtils.isEmpty(bean.getText())) {
			TextObject textObject = new TextObject();
			textObject.text = bean.getText();
			weiboMessage.textObject = textObject;
		}

		if (bean.getImage() != null) {
			ImageObject imageObject = new ImageObject();
			imageObject.setImageObject(bean.getImage());
			weiboMessage.imageObject = imageObject;
		}

		if (bean.isWeb()) {
			// WebpageObject mediaObject = new WebpageObject();
			// mediaObject.identify = Utility.generateGUID();
			// mediaObject.title = mShareWebPageView.getTitle();
			// mediaObject.description = mShareWebPageView.getShareDesc();
			//
			// Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
			// R.drawable.ic_logo);
			// // 设置 Bitmap 类型的图片到视频对象里 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
			// mediaObject.setThumbImage(bitmap);
			// mediaObject.actionUrl = mShareWebPageView.getShareUrl();
			// mediaObject.defaultText = "Webpage 默认文案";
			weiboMessage.mediaObject = (WebpageObject) bean.getContent();
		} else if (bean.isVideo()) {
			// TODO
		} else if (bean.isAudio()) {
			// TODO
		}

		share2Sina(context, weiboMessage);

	}

	private static void share2Sina(final Context context, WeiboMultiMessage weiboMessage) {
		if (!(context instanceof Activity)) {
			Log.e(tag, "context should be a instance of activity");
			return;
		}

		// 2. 初始化从第三方到微博的消息请求
		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.multiMessage = weiboMessage;

		AuthInfo authInfo = new AuthInfo(context, SinaConstants.APP_KEY, SinaConstants.REDIRECT_URL,
				SinaConstants.SCOPE);
		Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(context.getApplicationContext());
		String token = "";
		if (accessToken != null) {
			token = accessToken.getToken();
		}
		mWeiboShareAPI.sendRequest((Activity) context, request, authInfo, token, new WeiboAuthListener() {

			@Override
			public void onWeiboException(WeiboException arg0) {
			}

			@Override
			public void onComplete(Bundle bundle) {
				// TODO Auto-generated method stub
				Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
				AccessTokenKeeper.writeAccessToken(context.getApplicationContext(), newToken);
			}

			@Override
			public void onCancel() {
			}
		});
	}

	private static IWeiboShareAPI mWeiboShareAPI;

	/**
	 * @Title: registerApi
	 * @Description: 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
	 *               但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
	 *               NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
	 * @author: leobert.lan
	 * @param context
	 */
	private static void registerApi(final Context context) {
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(context, SinaConstants.APP_KEY);
		mWeiboShareAPI.registerApp();
	}

}
