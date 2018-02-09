package com.lht.cloudjob.share;

import com.tencent.connect.share.QQShare;

/**
 * @ClassName: QZoneShareBean
 * @Description: TODO
 * @date 2016年3月2日 下午5:10:50
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class QShareImageBean extends ShareBean {

	public static final int FLAG_QQ = QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE;

	public static final int FLAG_QZONE = QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN;
	/**
	 * image:分享的图片，必须，建议本地的，url的好像有问题，需要再测试
	 */
	private String image;

	/**
	 * from:最好填写应用名
	 */
	private String from;

	private int flag;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

}
