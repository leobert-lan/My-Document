package com.lht.pan_android.util;

import java.util.ArrayList;
import java.util.List;

import com.lht.pan_android.util.thirdPartyLogin.TencentConstants;
import com.lht.pan_android.util.thirdPartyLogin.WeChatConstants;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

/**
 * Created by Gates on 2015/9/8.
 */
public class CloudBoxApplication extends Application {

	public static List<Activity> activities = new ArrayList<Activity>();

	public static List<Activity> selectActivities = new ArrayList<Activity>();

	public static Activity backgroundActivity = null;

	public static Tencent mTencent;

	public static IWXAPI mWeChat;

	public static String ANDROIDVERSION = "N/A";

	public static String APPVERSION = "N/A";

	private static Context appContext;

	@Override
	public void onCreate() {
		super.onCreate();
		if (appContext == null)
			appContext = this;
		if (mTencent == null) {
			mTencent = Tencent.createInstance(TencentConstants.APP_KEY, this);
		}

		if (mWeChat == null) {
			mWeChat = WXAPIFactory.createWXAPI(this, WeChatConstants.APP_ID, false);
		}
		/*
		 * android.os.Build.VERSION.SDK; SDK号android.os.Build.MODEL; 手机型号
		 * android.os.Build.VERSION.RELEASE; android系统版本号
		 */
		CloudBoxApplication.ANDROIDVERSION = android.os.Build.VERSION.RELEASE;
		String version = VersionUtil.getVersion(this);
		CloudBoxApplication.APPVERSION = version == null ? "N/A" : version;
	}

	public static void addActivity(Activity activity) {
		activities.add(activity);
	}

	public static void removeActivity(Activity activity) {
		activities.remove(activity);
	}

	public static void finishAll() {

		for (Activity activity : activities) {
			if (!activity.isFinishing()) {
				activity.finish();
			}
		}

		backgroundActivity = null;
	}

	public static void addSelectActivity(Activity activity) {
		selectActivities.add(activity);
	}

	public static void finishSelectActivities() {
		for (Activity activity : selectActivities) {
			if (!activity.isFinishing()) {
				activity.finish();
			}
		}
	}

	public static void addBackgroundActivity(Activity activity) {
		backgroundActivity = activity;
		addActivity(activity);
	}

	public static boolean isBackgroundReady() {
		return !(backgroundActivity == null);
	}

	public static Tencent getmTencent() {
		return mTencent;
	}

	public static IWXAPI getmWeChat() {
		if (mWeChat == null) {
			mWeChat = WXAPIFactory.createWXAPI(appContext, WeChatConstants.APP_ID, false);
			mWeChat.registerApp(WeChatConstants.APP_ID);

		}
		return mWeChat;
	}

	public static Context getMainAppContext() {
		return appContext;
	}

}
