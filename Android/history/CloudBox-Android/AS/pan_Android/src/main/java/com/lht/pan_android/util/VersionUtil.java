package com.lht.pan_android.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * @ClassName: VersionUtil
 * @Description: TODO
 * @date 2016年2月29日 上午10:07:41
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class VersionUtil {

	/**
	 * @Title: getVersion
	 * @Description: 获取当前版本名
	 * @author: leobert.lan
	 * @return
	 */
	public static String getVersion(Context mContext) {
		try {
			PackageManager manager = mContext.getPackageManager();
			PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
