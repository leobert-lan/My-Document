package com.lht.pan_android.clazz;

import android.app.Activity;
import android.os.Environment;

/**
 * @ClassName: Util
 * @date 2015年12月4日 下午1:28:51
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class ImageLoaderUtil {

	private static ImageLoaderUtil util;
	public static int flag = 0;

	private ImageLoaderUtil() {

	}

	public static ImageLoaderUtil getInstance() {
		if (util == null) {
			util = new ImageLoaderUtil();
		}
		return util;
	}

	/**
	 * 判断是否有sdcard
	 * 
	 * @return
	 */
	public boolean hasSDCard() {
		boolean b = false;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			b = true;
		}
		return b;
	}

	/**
	 * 得到sdcard路径
	 * 
	 * @return
	 */
	public String getExtPath() {
		String path = "";
		if (hasSDCard()) {
			path = Environment.getExternalStorageDirectory().getPath();
		}
		return path;
	}

	/**
	 * 得到/data/data/yanbin.imagedownload目录
	 * 
	 * @param mActivity
	 * @return
	 */
	public String getPackagePath(Activity mActivity) {
		return mActivity.getFilesDir().toString();
	}

	/**
	 * 根据url得到图片名
	 * 
	 * @param url
	 * @return
	 */
	public String getImageName(String url) {
		String imageName = "";
		if (url != null) {
			imageName = url.substring(url.lastIndexOf("/") + 1);
		}
		return imageName;
	}

}
