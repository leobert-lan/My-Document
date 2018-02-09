package com.lht.pan_android.util.share;

import com.lht.pan_android.R;
import com.lht.pan_android.util.CloudBoxApplication;
import com.lht.pan_android.util.ToastUtil;
import com.lht.pan_android.util.ToastUtil.Duration;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @ClassName: QQShareUtil
 * @Description: qq分享业务类
 * @date 2016年3月2日 下午5:09:56
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class WeChatShareUtil {

	private IWXAPI api;
	private Context mContext;
	private boolean isWxAppInstalled = false;

	public WeChatShareUtil(Context mContext) {
		super();
		this.mContext = mContext;
		api = CloudBoxApplication.getmWeChat();
		isWxAppInstalled = api.isWXAppInstalled();
	}

	/**
	 * @Title: weChatShareText
	 * @Description: 分享文字
	 * @author: zhangbin
	 */
	public void weChatShareText(String content) {
		String text = content;
		if (text == null || text.length() == 0) {
			return;
		}

		WXTextObject textObj = new WXTextObject();
		textObj.text = text;

		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		msg.description = text;

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("text");
		req.message = msg;
		req.scene =
				// SendMessageToWX.Req.WXSceneTimeline;
				SendMessageToWX.Req.WXSceneSession;

		api.sendReq(req);
	}

	/**
	 * @Title: weChatSharePicture
	 * @Description: 分享图片 only test
	 * @author: zhangbin
	 * @param shareType
	 * @param shareContent
	 */
	public void weChatSharePicture() {
		Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.logofx);
		WXImageObject imgObj = new WXImageObject(bmp);

		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = imgObj;

		Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
		bmp.recycle();
		msg.thumbData = WeChatJudgeImgUtil.bmpToByteArray(thumbBmp, true); // 设置缩略图

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("imgshareappdata");
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneSession;
		api.sendReq(req);
	}

	/**
	 * @Title: weChatShareWebPage
	 * @Description: 分享URL 2.4.0使用
	 * @author: zhangbin
	 * @param shareType
	 * @param shareContent
	 */
	public void weChatShareWebPage(WeChatShareBean bean) {
		if (!isWxAppInstalled) {
			ToastUtil.show(mContext, R.string.tp_failure_wxnotinstalled, Duration.s);
			return;
		}
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = bean.getUrl();
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = bean.getTitle();
		msg.description = bean.getContent();
		// 写死 公司logo或者创意云盘logo
		Bitmap thumb = BitmapFactory.decodeResource(mContext.getResources(), bean.getPicResource());
		if (thumb == null) {
			// Toast.makeText(mContext, "图片不能为空", Toast.LENGTH_SHORT).show();
		} else {
			Bitmap thumbBmp = Bitmap.createScaledBitmap(thumb, 150, 150, true);
			thumb.recycle();
			msg.thumbData = WeChatJudgeImgUtil.bmpToByteArray(thumbBmp, true);
		}

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("share");
		req.message = msg;
		req.scene = bean.getShareType();
		api.sendReq(req);

	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

}
