package com.lht.pan_android.util.share;

import com.tencent.tauth.IUiListener;

/**
 * @ClassName: IQShareListener
 * @Description: TODO
 * @date 2016年3月3日 下午1:32:36
 * 
 * @author leobert.lan
 * @version 1.0
 */
public interface IQShareListener extends IUiListener {
	/**
	 * @Title: onQQNotAvailable
	 * @Description: qq没有安装
	 * @author: leobert.lan
	 */
	void onQQNotAvailable();

	/**
	 * @Title: onQZoneNotAvailable
	 * @Description: qq空间没有安装
	 * @author: leobert.lan
	 */
	void onQZoneNotAvailable();

}
