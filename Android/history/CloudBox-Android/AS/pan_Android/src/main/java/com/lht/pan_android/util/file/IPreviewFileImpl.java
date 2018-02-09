package com.lht.pan_android.util.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.protocol.HTTP;

import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.IPreViewFile;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.Interface.MainActivityIPreventPenetrate;
import com.lht.pan_android.Interface.Mime;
import com.lht.pan_android.activity.BaseActivity;
import com.lht.pan_android.bean.PreviewBean;
import com.lht.pan_android.util.ToastUtil;
import com.lht.pan_android.util.ToastUtil.Duration;
import com.lht.pan_android.util.string.StringUtil;
import com.lht.pan_android.view.popupwins.CustomDialog;
import com.lht.pan_android.view.popupwins.CustomPopupWindow.OnNegativeClickListener;
import com.lht.pan_android.view.popupwins.CustomPopupWindow.OnPositiveClickListener;
import com.lht.pan_android.view.popupwins.ProgressPopupWindow;
import com.lht.pan_android.view.popupwins.ProgressPopupWindow.ICancelListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @ClassName: IPreviewFileImpl
 * @Description: TODO
 * @date 2016年3月11日 下午1:27:34
 * 
 * @author leobert.lan
 * @version 1.0
 */
@SuppressLint("DefaultLocale")
public class IPreviewFileImpl implements IPreViewFile, Mime, Runnable, IUrlManager.DownloadFile {

	private final static String tag = "preview";

	private final Activity mActivity;

	private final PreviewBean bean;

	private String username = null;

	private FileDownHandler fileDownHandler;

	private ProgressPopupWindow popupWindow;

	private final IPreviewCaller mPreviewCaller;

	private String access_id;
	private String access_token;

	/**
	 * Creates a new instance of IPreviewFileImpl.
	 * 
	 * @param activity
	 * @param bean
	 */
	public IPreviewFileImpl(IPreviewCaller previewCaller, PreviewBean bean) {
		this.mActivity = previewCaller.getCallerActivity();
		this.bean = bean;

		this.mPreviewCaller = previewCaller;
		SharedPreferences sp = mActivity.getSharedPreferences(IKeyManager.Token.SP_TOKEN, Context.MODE_PRIVATE);

		username = sp.getString(IKeyManager.Token.KEY_USERNAME, null);
		access_id = sp.getString(IKeyManager.Token.KEY_ACCESS_ID, "");
		access_token = sp.getString(IKeyManager.Token.KEY_ACCESS_TOKEN, "");

		popupWindow = new ProgressPopupWindow(mActivity);

		popupWindow.setICancelListener(new ICancelListener() {

			@Override
			public void onCancel() {
				cancelPreview();
			}
		});

		popupWindow.setCancelBtn(R.string.string_cancel);

		fileDownHandler = new FileDownHandler(popupWindow);
	}

	/**
	 * @ClassName: IPreviewCaller
	 * @Description: 请求预览时界面控制接口
	 * @date 2016年3月16日 上午8:59:35
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	public interface IPreviewCaller {
		Activity getCallerActivity();

		void onOpenStart();

		void onOpenFinish();
	}

	private DownloadThread downloadThread = null;

	@Override
	public void loadPreviewFile(String fileurl, String localPath) {
		Log.d(tag, "开始下载:" + fileurl);

		downloadThread = new DownloadThread(fileurl, localPath);
		downloadThread.start();
	}

	private String getRemoteFileUniqueInfo(PreviewBean bean) {
		String res = bean.getPath() + bean.getModifyTime() + bean.getSize();
		String enc = FileMd5Util.getStringMd5(res).toLowerCase();
		return enc;
	}

	private String getOrCreateFolder(String md5) {
		String s = BaseActivity.getPreviewPath() + "/" + md5;
		File f = new File(s);
		if (f.exists()) {
			return f.getAbsolutePath();
		} else {
			f.mkdirs();
			return f.getAbsolutePath();
		}
	}

	private void updateProgress(int total) {
		Message msg = new Message();
		msg.what = FileDownHandler.WHAT_PROGRESS;
		Bundle b = new Bundle();
		b.putInt("Progress", total);
		msg.setData(b);
		fileDownHandler.sendMessage(msg);
	}

	private void updateMaxSize(int contentLength) {
		Message msg = new Message();
		msg.what = FileDownHandler.WHAT_MAX;
		Bundle b = new Bundle();
		b.putInt("MAX", contentLength);
		msg.setData(b);
		fileDownHandler.sendMessage(msg);
	}

	@Override
	public boolean checkSupportedBy3rdParty(String contentType) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);

		intent.setDataAndType(null, contentType);
		PackageManager pm = mActivity.getPackageManager();
		ComponentName cn = intent.resolveActivity(pm);
		return !(cn == null);
	}

	@Override
	public void endJob() {
		if (popupWindow.isShowing())
			popupWindow.dismiss();
		mPreviewCaller.onOpenFinish();
	}

	@Override
	public void openPreView(String localPath, String contentType) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(localPath)), contentType);
		mActivity.startActivity(intent);
	}

	@SuppressLint("DefaultLocale")
	@Override
	public String getMIMETypeBySuffix(String suffix) {
		String type = "*/*";
		String end = suffix.toLowerCase();
		for (int i = 0; i < MIME_MapTable.length; i++) {
			if (end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
	}

	@SuppressLint("DefaultLocale")
	@Override
	public String getMIMEType(File file) {
		String fName = file.getName();
		return getMIMETypeByName(fName);
	}

	private String getMIMETypeByName(String fName) {
		Log.d(tag, fName);
		String type = "*/*";
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0)
			return type;
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		if (StringUtil.isEmpty(end))
			return type;
		return getMIMETypeBySuffix(end);
	}

	@Override
	public String getMIMEType(String filePath) {
		File f = new File(filePath);
		return getMIMEType(f);
	}

	/**
	 * MAXSIZE:TODO 询问阈值
	 */
	private long MAXSIZE = 5 * 1024 * 1024;

	@Override
	public void run() {
		if (!(bean.getSize() > 0)) {
			URL url;
			try {
				url = new URL(getUrl(""));
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				bean.setSize(conn.getContentLength());
				conn.disconnect();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Log.d(tag, "update size:" + bean.getSize());

		tryToOpen();
	}

	private String contentType;

	private void tryToOpen() {
		if (checkSupportedBy3rdParty(bean.getContentType())) {
			// 可以打开
			contentType = bean.getContentType();
			try {
				open();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				toastUnsupport();
				endJob();
			}
		} else if (checkSupportedBy3rdParty(getMIMETypeByName(bean.getName()))) {
			// 可以打开
			contentType = getMIMETypeByName(bean.getName());
			try {
				open();
			} catch (UnsupportedEncodingException e) {
				toastUnsupport();
				endJob();
				e.printStackTrace();
			}
		} else {
			// 不能打开
			Log.d(tag, "不能打开");
			toastUnsupport();
			endJob();
		}

	}

	private void open() throws UnsupportedEncodingException {
		// 获取服务端Md5，如果成功，进入本地检验，如果失败，提示无法
		// 预览 视图状态还原
		final String sfm = getRemoteFileUniqueInfo(bean);
		String lfPath = getOrCreateFolder(sfm) + "/" + bean.getName();
		boolean isCacheExist = compareCheckSum(sfm, lfPath);
		if (isCacheExist) {
			Log.d(tag, "文件存在");
			Message msg = new Message();
			msg.what = FileDownHandler.WHAT_COMPLETE;
			Bundle b = new Bundle();
			b.putString("FilePath", lfPath);
			msg.setData(b);
			fileDownHandler.dispatchMessage(msg);
		} else {
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						tempFolder = getOrCreateFolder(sfm);
						// 文件过大需要询问
						if (bean.getSize() > MAXSIZE) {
							Message msg = new Message();
							msg.what = FileDownHandler.WHAT_OVERLARGE;

							Bundle b = new Bundle();
							b.putString("URL", getUrl(bean.getPath()));
							b.putString("FOLDER", tempFolder);
							msg.setData(b);

							fileDownHandler.sendMessage(msg);
						} else {
							loadPreviewFile(getUrl(bean.getPath()), tempFolder);
						}
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}).start();

		}
	}

	private String tempFolder = null;

	private void toastUnsupport() {
		Message msg = new Message();
		msg.what = FileDownHandler.WHAT_UNSUPPORT;
		fileDownHandler.sendMessage(msg);
	}

	// TODO
	private String getUrl(String path) throws UnsupportedEncodingException {
		if (bean.getType() == 1) {
			return DOMAIN + ADDRESS_CB + username + FUNCTION + PARAM + URLEncoder.encode(path, HTTP.UTF_8);

		} else {
			return DOMAIN + ADDRESS_SHARE + bean.getOwner() + "/share/" + bean.getShareId() + FUNCTION + PARAM
					+ URLEncoder.encode(bean.getPath(), HTTP.UTF_8) + "&queryUsername=" + username + "&access_id="
					+ access_id + "&access_token=" + access_token;
		}

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

		public final static int WHAT_OVERLARGE = 5;

		public final static int WHAT_UNSUPPORT = 6;

		public final static int WHAT_NOTMOUNTED = 7;

		public final static int WHAT_CANCEL = 8;

		private boolean isOver2M = true;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case WHAT_COMPLETE:
				String fPath = msg.getData().getString("FilePath");
				Log.d(tag, "filePath:" + fPath);
				File f = new File(fPath);
				// 重命名
				File nFile = new File(f.getParent() + "/" + bean.getName());
				f.renameTo(nFile);

				Log.d(tag, "filePath:" + nFile.getAbsolutePath());
				endJob();
				openPreView(nFile.getAbsolutePath(), contentType);
				break;

			case WHAT_FAILURE:
				ToastUtil.show(mActivity, R.string.toast_preview_loadfailure, Duration.l);
				Log.d(tag, "download failure");
				endJob();
				break;

			case WHAT_PROGRESS:
				int progress = msg.getData().getInt("Progress");
				pd.setProgress(progress);
				break;
			case WHAT_MAX:
				int max = msg.getData().getInt("MAX");
				// TODO hard-code
				if (max >= 2 * 1024 * 1024) {
					// 大于2兆进度条
					isOver2M = true;
					pd.setTitle("正在打开");
					pd.setMax(max);
					pd.show();
				} else {
					isOver2M = false;
					mPreviewCaller.onOpenStart();
				}

				break;

			case WHAT_OVERLARGE:
				// TODO hard-code

				Bundle b = msg.getData();

				final String url = b.getString("URL");
				final String folder = b.getString("FOLDER");

				CustomDialog dialog = new CustomDialog(mActivity, new MainActivityIPreventPenetrate());
				dialog.setContent("文件大小大于5兆，确定进行预览吗？");
				dialog.setPositiveButton("确定");
				dialog.setNegativeButton("取消");
				dialog.setNegativeClickListener(new OnNegativeClickListener() {

					@Override
					public void onNegativeClick() {
						Message msg = new Message();
						msg.what = FileDownHandler.WHAT_CANCEL;
						fileDownHandler.sendMessage(msg);
					}
				});
				dialog.setPositiveClickListener(new OnPositiveClickListener() {

					@Override
					public void onPositiveClick() {
						loadPreviewFile(url, folder);
					}
				});
				dialog.show();
				break;
			case WHAT_UNSUPPORT:
				ToastUtil.show(mActivity, R.string.preview_unsupported_file, Duration.s);
				break;
			case WHAT_NOTMOUNTED:
				ToastUtil.show(mActivity, R.string.preview_sdcard_notmounted, Duration.l);
				break;
			case WHAT_CANCEL:
				ToastUtil.show(mActivity, R.string.preview_cancel, Duration.s);
				break;

			default:
				break;
			}
		}
	}

	/**
	 * md5是否一致
	 * 
	 * 也许这段注释会让你迷茫...最终我们没有比对文件的md5，而是检验了服务端文件生成的一段唯一码和本地的“key”一致 一致返回true
	 * 
	 * @see com.lht.pan_android.Interface.IPreViewFile#compareCheckSum(java.lang.String,
	 *      java.lang.String)
	 */
	@SuppressLint("DefaultLocale")
	@Override
	public boolean compareCheckSum(String serverFileCheckSum, String localFilePath) {
		String sfm = serverFileCheckSum.toLowerCase();
		File f = new File(localFilePath);
		if (!f.exists())
			return false;

		File p = new File(f.getParent());

		Log.d(tag, "check md5:\r\n" + sfm + "\r\n" + p.getName());

		return sfm.equals(p.getName());
	}

	@Override
	public void cancelPreview() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (downloadThread != null)
					downloadThread.cancel();
				// 删除缓存文件
				if (tempFolder != null)
					FileUtil.delete(new File(tempFolder));
			}
		}).start();
		endJob();
		Message msg = new Message();
		msg.what = FileDownHandler.WHAT_CANCEL;
		fileDownHandler.sendMessage(msg);
	}

	private final class DownloadThread extends Thread {
		private final String fileurl, localPath;

		public DownloadThread(String fileurl, String localpath) {
			this.fileurl = fileurl;
			this.localPath = localpath;
		}

		private HttpURLConnection conn = null;
		private InputStream is = null;
		private FileOutputStream fos = null;
		private BufferedInputStream bis = null;

		@Override
		public void run() {
			super.run();
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				URL url;
				try {
					url = new URL(fileurl);
					conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(5000);
					// 更新文件大小
					updateMaxSize(conn.getContentLength());
					// InputStream
					is = conn.getInputStream();
					// 防止杀进程，取名为temp
					File file = new File(localPath, "temp");

					// read only

					file.setReadOnly();

					// FileOutputStream
					fos = new FileOutputStream(file);
					// BufferedInputStream
					bis = new BufferedInputStream(is);
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
					// 尝试打开，
					Log.d(tag, "download success");
					Message msg = new Message();
					msg.what = FileDownHandler.WHAT_COMPLETE;
					Bundle b = new Bundle();
					b.putString("FilePath", file.getAbsolutePath());
					msg.setData(b);
					fileDownHandler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
					if (!userCancel) {
						Message msg = new Message();
						msg.what = FileDownHandler.WHAT_FAILURE;
						fileDownHandler.sendMessage(msg);
					}
				}

			} else {
				Log.d(tag, "not mounted");
				Message msg = new Message();
				msg.what = FileDownHandler.WHAT_NOTMOUNTED;
				fileDownHandler.sendMessage(msg);
			}
		}

		private boolean userCancel = false;

		public void cancel() {
			if (conn != null) {
				try {
					userCancel = true;
					bis.close();
					fos.close();
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				conn.disconnect();
			}

		}
	}

}
