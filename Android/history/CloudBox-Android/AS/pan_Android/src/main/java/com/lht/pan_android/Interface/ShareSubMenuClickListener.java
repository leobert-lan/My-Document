package com.lht.pan_android.Interface;

import com.lht.pan_android.bean.ShareItemBean;

/**
 * @ClassName: ShareSubMenuClickListener
 * @Description: 分享子接口回调
 * @date 2016年1月21日 上午11:51:39
 * 
 * @author zhangbin
 * @version 1.0
 * @since JDK 1.6
 */
public interface ShareSubMenuClickListener {

	/**
	 * @Title: onDownLoadClick
	 * @Description: 下载
	 * @author: zhangbin
	 * @param item
	 */
	void onDownLoadClick(ShareItemBean item);

	/**
	 * @Title: onMoveClick
	 * @Description: 移动
	 * @author: zhangbin
	 * @param item
	 */
	void onMoveClick(ShareItemBean item);

	/**
	 * @Title: onIgnoreClick
	 * @Description: 忽略
	 * @author: zhangbin
	 * @param item
	 */
	void onIgnoreClick(ShareItemBean item, int position);

}
