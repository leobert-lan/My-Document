package com.lht.creationspace.social.share;

import com.lht.creationspace.util.debug.DLog;
import com.tencent.connect.share.QQShare;


/**
 * @ClassName: QQShareBean
 * @Description: TODO
 * @date 2016年3月2日 下午5:10:31
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class QShareImage7TextBean extends ShareBean {

	public static final int FLAG_QQ = QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE;

	public static final int FLAG_QZONE = QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN;
	/**
	 * targetUrl:分享目标
	 */
	private String targetUrl;

	/**
	 * title:分享标题 小于30字符
	 */
	private String title;

	/**
	 * summary:分享概述 小于40
	 */
	private String summary;

	/**
	 * image:没什么卵用的image，分享者能看到，接收者看不到
	 */
	private String imageUrl;

	/**
	 * from:最好填写应用名
	 */
	private String from;

	/**
	 * flag:区分qq还是qzone
	 */
	private int flag;

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		if (title.length() > QQShare.QQ_SHARE_TITLE_MAX_LENGTH) {
			DLog.wtf(getClass(), "title 字符超长");
			this.title = title.substring(0, QQShare.QQ_SHARE_TITLE_MAX_LENGTH);
			return;
		}
		this.title = title;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		if (summary.length() > QQShare.QQ_SHARE_SUMMARY_MAX_LENGTH) {
			DLog.wtf(getClass(), "summary 字符超长");
			this.summary = summary.substring(0, QQShare.QQ_SHARE_SUMMARY_MAX_LENGTH);
			return;
		}
		this.summary = summary;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

}
