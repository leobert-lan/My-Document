package com.lht.pan_android.util;

import android.annotation.SuppressLint;

/**
 * @ClassName: TimeUtil
 * @Description: TODO
 * @date 2015年11月30日 下午7:52:34
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class TimeUtil {

	@SuppressLint("SimpleDateFormat")
	public static String getFormatedTime(String timeStamp) {
		if (timeStamp != null) {
			try {
				Long stamp = Long.parseLong(timeStamp);
				return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date(stamp));
			} catch (Exception e) {
				return null;
			}
		}
		return null;

	}

	@SuppressLint("SimpleDateFormat")
	public static String getFormatedTime(long timeStamp) {
		return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date(timeStamp * 1000));

	}

	/**
	 * @Title: getFormatedTime2
	 * @Description: 非服务器时间戳，带s格式化
	 * @author: leobert.lan
	 * @param timeStamp
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getFormatedTime2(long timeStamp) {
		return new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date(timeStamp * 1000));

	}

	public static Long getCurrentTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}
}
