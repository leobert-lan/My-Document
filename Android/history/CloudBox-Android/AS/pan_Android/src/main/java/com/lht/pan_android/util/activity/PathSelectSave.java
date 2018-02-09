package com.lht.pan_android.util.activity;

/**
 * @ClassName: PathSelectSave
 * @Description: 我一口老血吐在屏幕上。
 *               解决部分第三方android系统无法正确使用startActivityForResult和onActivityResult的问题
 * @date 2016年1月21日 下午3:15:49
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class PathSelectSave {

	private static Integer REQUEST_CODE = -1;

	private static String path = "";

	public static int getREQUEST_CODE() {
		return REQUEST_CODE;
	}

	public static void setREQUEST_CODE(int rEQUEST_CODE) {
		REQUEST_CODE = rEQUEST_CODE;
	}

	public static boolean checkCode(int code) {
		return code == REQUEST_CODE;
	}

	public static String getPath() {
		return path;
	}

	public static void setPath(String path) {
		PathSelectSave.path = path;
	}

	public static void destroy() {
		REQUEST_CODE = -1;
		path = null;
	}

}
