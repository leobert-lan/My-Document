package com.lht.cloudjob.share;

import java.util.ArrayList;
import java.util.List;


import com.lht.cloudjob.MainApplication;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.string.StringUtil;
import com.tencent.connect.share.QQShare;
import com.tencent.open.utils.ThreadManager;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

/**
 * @ClassName: QQShareUtil
 * @Description: qq分享业务类
 * @date 2016年3月2日 下午5:09:56
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class QQShareUtil {

	public static void qShareImage7Text(final QShareImage7TextBean bean, final Activity activity,
			final IQShareListener callback) {
		final Bundle params = new Bundle();

		// 类型 必须
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);

		// Title 必须
		String title = StringUtil.isEmpty(bean.getTitle()) ? "云差事分享" : bean.getTitle();
		params.putString(QQShare.SHARE_TO_QQ_TITLE, title);

		// summary 建议
		if (!StringUtil.isEmpty(bean.getSummary()))
			params.putString(QQShare.SHARE_TO_QQ_SUMMARY, bean.getSummary());

		// target 必须
		if (!StringUtil.isEmpty(bean.getTargetUrl()))
			params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, bean.getTargetUrl());
		else {
			callback.onError(new UiError(-1, "必须参数缺失", "target 缺失"));
			return;
		}

		// 图标 建议
		if (!StringUtil.isEmpty(bean.getImageUrl()))
			params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, bean.getImageUrl());

		// 返回append 建议
		String from = StringUtil.isEmpty(bean.getFrom()) ? "云差事" : bean.getFrom();
		params.putString(QQShare.SHARE_TO_QQ_APP_NAME, from);

		// 分享到qq 还是qzone
		boolean b = bean.getFlag() == QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN
				|| bean.getFlag() == QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE;
		if (!b) {
			callback.onError(new UiError(-2, "必须参数错误", "flag 错误"));
			return;
		}

		params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, bean.getFlag());

		if (bean.getFlag() == QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN) {
			if (!isQZoneAvailable(activity)) {
				callback.onQZoneNotAvailable();
				return;
			}
		} else {
			if (!isQQAvailable(activity)) {
				callback.onQQNotAvailable();
				return;
			}
		}

		doShareToQQ(params, activity, callback);
	}

	/**
	 * @Title: qShareImageOnly
	 * @Description: 单纯分享图片，仅支持本地图片
	 * @author: leobert.lan
	 * @param bean
	 * @param activity
	 * @param callback
	 */
	public static void qShareImageOnly(final QShareImageBean bean, final Activity activity,
			final IQShareListener callback) {
		final Bundle params = new Bundle();

		// 类型 必须
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);

		// 图标 必须
		if (!StringUtil.isEmpty(bean.getImage()))
			params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, bean.getImage());
		else {
			callback.onError(new UiError(-1, "必须参数缺失", "qzone分享 target 缺失"));
			return;
		}

		// 返回append 建议 已经注册的应用，该设置无效，也是醉了，浪费感情。
		String from = StringUtil.isEmpty(bean.getFrom()) ? "云差事" : bean.getFrom();

		params.putString(QQShare.SHARE_TO_QQ_APP_NAME, from);

		// 分享到qq 还是qzone
		boolean b = bean.getFlag() == QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN
				|| bean.getFlag() == QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE;
		if (!b) {
			callback.onError(new UiError(-2, "必须参数错误", "flag 错误"));
			return;
		}

		params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, bean.getFlag());

		if (bean.getFlag() == QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN) {
			if (!isQZoneAvailable(activity)) {
				callback.onQZoneNotAvailable();
				return;
			}
		} else {
			if (!isQQAvailable(activity)) {
				callback.onQQNotAvailable();
				return;
			}
		}

		doShareToQQ(params, activity, callback);

	}

	private static void doShareToQQ(final Bundle params, final Activity activity, final IUiListener callback) {
		// QQ分享要在主线程做
		ThreadManager.getMainHandler().post(new Runnable() {

			@Override
			public void run() {
				if (null != MainApplication.getTencent()) {
					MainApplication.getTencent().shareToQQ(activity, params, callback);
				} else {
					DLog.e(getClass(), new DLog.LogLocation(), "mTencent is null!");
				}

			}
		});
	}

	/**
	 * @Title: isQQAvilible
	 * @Description: 检测QQ是否安装
	 * @author: leobert.lan
	 * @param context
	 * @return
	 */
	private static boolean isQQAvailable(Context context) {

		final String qqPackageName = "com.tencent.mobileqq";

		return isAvailable(context, qqPackageName);
	}

	private static boolean isQZoneAvailable(Context context) {
		final String qzonePackageName = "com.qzone";
		return isAvailable(context, qzonePackageName);
	}

	private static boolean isAvailable(Context context, String pkn) {

		final PackageManager packageManager = context.getPackageManager();

		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		List<String> pName = new ArrayList<String>();

		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				pName.add(pn);
			}
		}
		return pName.contains(pkn);

	}

}
