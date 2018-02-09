package com.lht.pan_android.Interface;

/**
 * @ClassName: ISendFileCallBack
 * @Description: 发送文件事件的回调接口集
 * @date 2015年11月20日 下午4:22:30
 * 
 * @author leobert.lan
 * @version 1.0
 */
public interface UserInfoCallBack {

	// /**
	// * @Title: callback
	// * @Description: 用户信息回调接口
	// * @author: zhangbin
	// * @param s1
	// * @param s2
	// * @param s3
	// * @param s4
	// * @param i5
	// */
	// void callback(String user, String ivanta, String used, String total, int
	// percent);

	/**
	 * @Title: setBasicInfo
	 * @Description: set the basic info of user,such as nickname、avatar
	 * @author: leobert.lan
	 * @param user
	 * @param avatar 
	 */
	void setBasicInfo(String user, String avata);

	/** 
	 * @Title: setCapacityInfo 
	 * @Description: set the storage info
	 * @author: leobert.lan
	 * @param used
	 * @param total
	 * @param percent    
	 */
	void setCapacityInfo(String used, String total, int percent);

}
