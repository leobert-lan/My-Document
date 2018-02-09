package com.lht.pan_android.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.Header;

import com.alibaba.fastjson.JSON;
import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.IPreventPenetrate;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.activity.asyncProtected.MainActivity;
import com.lht.pan_android.bean.UpdateBean;
import com.lht.pan_android.bean.UpdateDataBean;
import com.lht.pan_android.util.ToastUtil.Duration;
import com.lht.pan_android.util.file.FileMd5Util;
import com.lht.pan_android.util.internet.HttpUtil;
import com.lht.pan_android.view.popupwins.CustomDialog;
import com.lht.pan_android.view.popupwins.CustomPopupWindow.OnNegativeClickListener;
import com.lht.pan_android.view.popupwins.CustomPopupWindow.OnPositiveClickListener;
import com.lht.pan_android.view.popupwins.ProgressPopupWindow;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @ClassName: UpdateUtil
 * @Description: 更新
 * @date 2016年2月29日 上午9:37:51
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class UpdateUtil implements IKeyManager.VersionSp, IUrlManager.CheckUpdate {

	private final static String TAG = "updataCheck";

	private final Context mContext;

	private HttpUtil mHttpUtil;

	private SharedPreferences spVersion;

	private FileDownHandler fileDownHandler;

	private CustomDialog dialog;

	private String sVersion;

	private String apkFilePath;

	private String md5;

	public UpdateUtil(Context context) {
		this.mContext = context;
		mHttpUtil = new HttpUtil();
		spVersion = mContext.getSharedPreferences(SP_VERSION, Context.MODE_PRIVATE);
		Activity a = (Activity) mContext;
		fileDownHandler = new FileDownHandler(new ProgressPopupWindow(a));
		dialog = new CustomDialog(a, new IPreventPenetrate() {

			@Override
			public void preventPenetrate(Activity activity, boolean isProtectNeed) {
				((MainActivity) activity).setActiveStateOfDispatchOnTouch(!isProtectNeed);
			}
		});

		dialog.setTitle(R.string.dialog_title_update);
		dialog.setNegativeButton(R.string.dialog_negative_update);

		dialog.setPositiveButton(R.string.dialog_positive_update);
		dialog.setPositiveClickListener(new OnPositiveClickListener() {

			@Override
			public void onPositiveClick() {
				// 下载更新
				SPUtil.modifyString(spVersion, KEY_CHECKEDSAVE, sVersion);
				getApkFile(apkFilePath);
			}
		});
	}

	/**
	 * @Title: checkUpdate
	 * @Description: 检查服务器版本
	 * @author: leobert.lan
	 */
	public void checkUpdate(boolean force) {
		String cVersionName = VersionUtil.getVersion(mContext);
		String spVersionName = spVersion.getString(KEY_CHECKEDSAVE, cVersionName);
		// =======
		// // Log.d(TAG, "cv:" + cVersionName);
		// String spVersionName = spVersion.getString(KEY_CHECKEDSAVE,
		// cVersionName);
		// // Log.d(TAG, "spv:" + spVersionName);
		// >>>>>>> 2.5.0_debug_release
		boolean isIgnored = spVersion.getBoolean(KEY_IGNORED, false);
		if (force) {
			SPUtil.modifyBoolean(spVersion, KEY_IGNORED, false);
			isIgnored = false;
		}
		getLatestVersionInfo(cVersionName, spVersionName, isIgnored, force);

	}

	private void getLatestVersionInfo(final String cVersion, final String spVersionName, final boolean isIgnored,
			final boolean byhand) {

		if (byhand) {
			dialog.setNegativeButton(R.string.dialog_negative_update2);
			if (mContext instanceof MainActivity) {
				((MainActivity) mContext).showWaitView(true);
			}
		} else {
			dialog.setNegativeClickListener(new OnNegativeClickListener() {

				@Override
				public void onNegativeClick() {
					// 修改ignore
					SPUtil.modifyBoolean(spVersion, KEY_IGNORED, true);
					SPUtil.modifyString(spVersion, KEY_CHECKEDSAVE, sVersion);

				}
			});
		}

		RequestParams params = new RequestParams();
		params.put(KEY_SYS, VALUE_SYS);
		params.put(KEY_SID, VALUE_SID);
		params.put(KEY_CURRENTVERSION, cVersion);
		mHttpUtil.postWithParams(mContext, URL, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				UpdateBean updateBean = JSON.parseObject(new String(arg2), UpdateBean.class);
				if (byhand) {
					if (mContext instanceof MainActivity) {
						((MainActivity) mContext).cancelWaitView();
					}
				}
				if (updateBean.getRet() == 200) {
					//
					UpdateDataBean bean = JSON.parseObject(updateBean.getData(), UpdateDataBean.class);
					apkFilePath = bean.getUrl();
					md5 = bean.getMd5();
					String content = String.format(mContext.getResources().getString(R.string.dialog_content_update),
							updateBean.getVer());
					dialog.setContent(content);

					boolean isOld = needUpdate(cVersion, updateBean.getVer());

					if (isOld) {
						if (!isIgnored) {
							// 显示询问对话框
							dialog.show();
						} else {
							// nothing todo
							Log.d(TAG, "ignored");
						}
					} else {
						// 已经是最新版本
						if (byhand)
							ToastUtil.show(mContext, R.string.toast_update_latestversion, Duration.s);
					}
				} else {
					// 不需要更新，可能服务器有误，或者已经判断了
					if (byhand)
						ToastUtil.show(mContext, R.string.toast_update_latestversion, Duration.s);
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				Log.d(TAG, "update php failure,status:" + arg0);
				if (byhand) {
					if (mContext instanceof MainActivity) {
						((MainActivity) mContext).cancelWaitView();
					}
				}
				ToastUtil.show(mContext, R.string.toast_update_timeout, Duration.s);
			}
		});
	}

	private boolean needUpdate(String cv, String nv) {
		String[] cvs = cv.split("\\.");
		String[] nvs = nv.split("\\.");
		for (int i = 0; i < cvs.length; i++) {
			Log.e(TAG, "check:new" + Integer.parseInt(nvs[i]) + " , old" + Integer.parseInt(cvs[i]));
			if (Integer.parseInt(nvs[i]) > Integer.parseInt(cvs[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @Title: getApkFile
	 * @Description: 启动线程下载，注意looper
	 * @author: leobert.lan
	 * @param url
	 */
	private void getApkFile(String url) {
		new Thread(new FileDown(url, fileDownHandler)).start();
	}

	@SuppressLint("HandlerLeak")
	class FileDownHandler extends Handler {
		private final ProgressPopupWindow pd;

		public FileDownHandler(ProgressPopupWindow pd) {
			this.pd = pd;
		}

		public final static int WHAT_COMPLETE = 1;

		public final static int WHAT_FAILURE = 2;

		public final static int WHAT_PROGRESS = 3;

		public final static int WHAT_MAX = 4;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case WHAT_COMPLETE:
				String fPath = msg.getData().getString("FilePath");
				Log.d(TAG, "filePath:" + fPath);
				File f = new File(fPath);
				install(f);
				break;

			case WHAT_FAILURE:
				ToastUtil.show(mContext, R.string.toast_update_servererror, Duration.l);
				// TODO umeng feedback
				Log.d(TAG, "download failure");
				break;

			case WHAT_PROGRESS:
				int progress = msg.getData().getInt("Progress");
				pd.setProgress(progress);
				break;
			case WHAT_MAX:
				int max = msg.getData().getInt("MAX");
				pd.setMax(max);
				pd.show();
				break;
			default:
				break;
			}
		}

		private void install(File f) {
			Intent intent = new Intent();
			// 执行动作
			intent.setAction(Intent.ACTION_VIEW);
			// 执行的数据类型
			intent.setDataAndType(Uri.fromFile(f), "application/vnd.android.package-archive");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(intent);
			CloudBoxApplication.finishAll();
		}
	}

	class FileDown implements Runnable {

		private final String apkFilePath;

		private Handler mHandler;

		public FileDown(String path, Handler h) {
			apkFilePath = path;
			mHandler = h;
		}

		@Override
		public void run() {
			File f = null;
			try {
				f = getFileFromServer(apkFilePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (f == null) {
				Message msg = new Message();
				msg.what = FileDownHandler.WHAT_FAILURE;
				mHandler.sendMessage(msg);
			} else {
				Message msg = new Message();
				msg.what = FileDownHandler.WHAT_COMPLETE;
				Bundle b = new Bundle();
				b.putString("FilePath", f.getAbsolutePath());
				msg.setData(b);
				mHandler.sendMessage(msg);
			}
		}

		private File getFileFromServer(String path) throws Exception {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				URL url = new URL(path);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5000);
				// 更新文件大小
				updateMaxSize(conn.getContentLength());
				InputStream is = conn.getInputStream();
				File file = new File(Environment.getExternalStorageDirectory() + "/Vso/Pan/", "vso_pan_updata.apk");
				FileOutputStream fos = new FileOutputStream(file);
				BufferedInputStream bis = new BufferedInputStream(is);
				byte[] buffer = new byte[1024];
				int len;
				int total = 0;
				while ((len = bis.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
					total += len;
					// 获取当前下载量
					updateProgress(total);
				}
				fos.close();
				bis.close();
				is.close();
				// check md5
				String _md5 = FileMd5Util.getFileMd5String(file.getAbsolutePath()).toLowerCase();
				if (_md5.equals(md5.toLowerCase()))
					return file;
				else
					return null;
			} else {
				return null;
			}
		}

		private void updateProgress(int total) {
			Message msg = new Message();
			msg.what = FileDownHandler.WHAT_PROGRESS;
			Bundle b = new Bundle();
			b.putInt("Progress", total);
			msg.setData(b);
			mHandler.sendMessage(msg);
		}

		private void updateMaxSize(int contentLength) {
			Message msg = new Message();
			msg.what = FileDownHandler.WHAT_MAX;
			Bundle b = new Bundle();
			b.putInt("MAX", contentLength);
			msg.setData(b);
			mHandler.sendMessage(msg);
		}

	}

}
