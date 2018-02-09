package com.lht.creationspace.social.share;

import android.graphics.Bitmap;

import com.lht.creationspace.util.debug.DLog;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.VideoObject;
import com.sina.weibo.sdk.api.WebpageObject;

/**
 * @ClassName: SinaShareBean
 * @Description: TODO
 * @date 2016年3月8日 上午10:49:49
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class SinaShareBean extends ShareBean {

	private static final String tag = "SinaShareBean";

	private int contentType = -1;

	/**
	 * text:文本 可选
	 */
	private String text;

	/**
	 * image:可选 图片 32k以内
	 */
	private Bitmap image;

	public static int CTYPE_WEB = 1;

	public static int CTYPE_AUDIO = 2;

	public static int CTYPE_VIDEO = 3;

	// 语音暂不做

	private Object content;

	/**
	 * @Title: isWeb
	 * @Description: 分享内容是web连接
	 * @author: leobert.lan
	 * @return
	 */
	public boolean isWeb() {
		return contentType == CTYPE_WEB;
	}

	/**
	 * @Title: isAudio
	 * @Description: 分享内容是audio
	 * @author: leobert.lan
	 * @return
	 */
	public boolean isAudio() {
		return contentType == CTYPE_AUDIO;
	}

	/**
	 * @Title: isVideo
	 * @Description: 分享内容是video
	 * @author: leobert.lan
	 * @return
	 */
	public boolean isVideo() {
		return contentType == CTYPE_VIDEO;
	}

	public int getContentType() {
		return contentType;
	}

	public void setContentType(int contentType) {
		this.contentType = contentType;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		if (content instanceof WebpageObject)
			this.setContentType(CTYPE_WEB);
		else if (content instanceof MusicObject)
			this.setContentType(CTYPE_AUDIO);
		else if (content instanceof VideoObject)
			this.setContentType(CTYPE_VIDEO);
		else {
			DLog.e(getClass(), "content error");
			return;
		}

		this.content = content;
	}

}
