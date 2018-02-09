package com.lht.pan_android.activity;

import java.io.File;

import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.service.DownloadService;
import com.lht.pan_android.service.UpLoadService;
import com.lht.pan_android.util.CloudBoxApplication;
import com.lht.pan_android.util.ToastUtil;
import com.lht.pan_android.util.ToastUtil.Duration;
import com.lht.pan_android.util.database.DownLoadDataBaseUtils;
import com.lht.pan_android.util.database.UpLoadDataBaseUtils;
import com.squareup.picasso.Picasso;

import android.os.Bundle;
import android.os.Environment;

public abstract class BaseActivity extends UMengActivity {

	public static UpLoadService upServiceBinder = null;

	public static UpLoadDataBaseUtils upLoadDataBaseUtils;

	public static DownLoadDataBaseUtils downLoadDataBaseUtils;

	public static DownloadService downServiceBinder = null;

	public static boolean onWifi = false;

	public static boolean isConnected = false;

	public static boolean isOnlyOnWifi = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		CloudBoxApplication.addActivity(this);
		createIndividualFolder();

		Picasso.setExtendDiskCacheFile(new File(getThumbnailPath()));

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onResume() {
		createIndividualFolder();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private String SDCARD_ROOT = null;

	/**
	 * thumbnailPath:缩略图路径
	 */
	public static String thumbnailPath;

	/**
	 * previewPath:预览存储路径
	 */
	public static String previewPath;

	/**
	 * localImageCache:本地上传图片、视频，传输记录的缩略图
	 */
	static String localImageCache;

	/**
	 * destDir:文件加载
	 */
	private static File destDir = null;

	/**
	 * @Title: createIndividualFolder
	 * @Description: 创建个人文件夹
	 * @author: leobert.lan
	 */
	public void createIndividualFolder() {
		if (!isSdCardMounted()) {
			ToastUtil.show(this, R.string.sdcard_not_mounted, Duration.l);
			return;
		}

		SDCARD_ROOT = Environment.getExternalStorageDirectory().getPath();

		String username = getSharedPreferences(IKeyManager.Token.SP_TOKEN, MODE_PRIVATE)
				.getString(IKeyManager.Token.KEY_USERNAME, "default");

		destDir = new File(SDCARD_ROOT + "/Vso/Pan/" + username + "/download");
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		thumbnailPath = SDCARD_ROOT + "/Vso/Pan/" + username + "/thumbnail";
		previewPath = SDCARD_ROOT + "/Vso/Pan/" + username + "/preview";
		localImageCache = SDCARD_ROOT + "/Vso/Pan/" + username + "/localImageCache";
		File f = new File(thumbnailPath);
		if (!f.exists()) {
			f.mkdirs();
		}
		File f2 = new File(previewPath);
		if (!f2.exists()) {
			f2.mkdirs();
		}
		File f3 = new File(localImageCache);
		if (!f3.exists()) {
			f3.mkdirs();
		}
	}

	/**
	 * @Title: getIndividualFolder
	 * @Description: 获取用户目录
	 * @author: leobert.lan
	 * @return
	 */
	public static File getIndividualFolder() {
		return destDir;
	}

	/**
	 * @Title: isSdCardMounted
	 * @Description: 检测外部存储是否加载
	 * @author: leobert.lan
	 * @return
	 */
	private boolean isSdCardMounted() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	public UpLoadService getUpServiceBinder() {
		return upServiceBinder;
	}

	public DownloadService getDownServiceBinder() {
		return downServiceBinder;
	}

	public UpLoadDataBaseUtils getUpLoadDataBaseUtils() {
		return upLoadDataBaseUtils;
	}

	public DownLoadDataBaseUtils getDownLoadDataBaseUtils() {
		return downLoadDataBaseUtils;
	}

	public static boolean isConnected() {
		return isConnected;
	}

	public static void setConnected(boolean isConnected) {
		BaseActivity.isConnected = isConnected;
	}

	public static boolean isOnWifi() {
		return onWifi;
	}

	public static void setOnWifi(boolean onWifi) {
		BaseActivity.onWifi = onWifi;
	}

	public static String getThumbnailPath() {
		return thumbnailPath;
	}

	public static String getLocalImageCachePath() {
		return localImageCache;
	}

	public static String getPreviewPath() {
		return previewPath;
	}

	public static boolean isOnlyOnWifi() {
		return isOnlyOnWifi;
	}

	public static void setOnlyOnWifi(boolean isOnlyOnWifi) {
		BaseActivity.isOnlyOnWifi = isOnlyOnWifi;
	}

	public static File getAdStorageDir() {
		File f = new File(Environment.getExternalStorageDirectory() + "/Vso/Pan/default/ADS");
		if (!f.exists())
			f.mkdirs();
		return f;
	}

}
