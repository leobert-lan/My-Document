package com.lht.pan_android.Interface;

import com.lht.pan_android.bean.DirItemBean;

/**
 * @ClassName: SubMenuClickListener
 * @Description: 子菜单点击回调接口，返回操作对象
 * @date 2015年12月1日 下午12:45:43
 * 
 * @author leobert.lan
 * @version 1.0
 */
public interface SubMenuClickListener {

	/**
	 * @Title: onShareClick
	 * @Description: 分享
	 * @author: leobert.lan
	 * @param item
	 */
	void onShareClick(DirItemBean item);

	/**
	 * @Title: onDownLoadClick
	 * @Description: 下载
	 * @author: leobert.lan
	 * @param item
	 */
	void onDownLoadClick(DirItemBean item);

	/**
	 * @Title: onRenameClick
	 * @Description: 重命名
	 * @author: leobert.lan
	 * @param item
	 */
	void onRenameClick(DirItemBean item);

	/**
	 * @Title: onMoveClick
	 * @Description: 移动
	 * @author: leobert.lan
	 * @param item
	 */
	void onMoveClick(DirItemBean item);

	/**
	 * @Title: onDeleteClick
	 * @Description: 删除
	 * @author: leobert.lan
	 * @param item
	 */
	void onDeleteClick(DirItemBean item);

}
