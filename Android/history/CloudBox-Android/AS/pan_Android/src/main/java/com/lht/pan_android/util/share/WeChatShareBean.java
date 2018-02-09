package com.lht.pan_android.util.share;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;

/**
 * @ClassName: SinaShareBean
 * @Description: TODO
 * @date 2016年3月8日 上午10:49:49
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class WeChatShareBean extends ShareBean {

	private int shareType;
	private String title;
	private String content;
	private String url;
	private int picResource;

	public static final int FLAG_WECHATFC = SendMessageToWX.Req.WXSceneTimeline;

	public static final int FLAG_WECHAT = SendMessageToWX.Req.WXSceneSession;

	public int getShareType() {
		return shareType;
	}

	public void setShareType(int shareType) {
		this.shareType = shareType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getPicResource() {
		return picResource;
	}

	public void setPicResource(int picResource) {
		this.picResource = picResource;
	}

}
