package com.lht.pan_android.executor;

/**
 * Created by zhangbin on 2015/11/30.
 */
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import com.alibaba.fastjson.JSON;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.SendFileCallback;
import com.lht.pan_android.bean.DownloadInfoBean;
import com.lht.pan_android.bean.TransProgressBean;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.DLog.LogLocation;
import com.lht.pan_android.util.TimeUtil;
import com.lht.pan_android.util.database.DownLoadDataBaseUtils;
import com.lht.pan_android.util.file.FileUtil;
import com.lht.pan_android.util.string.StringUtil;
import com.lht.pan_android.view.TransViewInfo.Status;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class DownloadExecutor {

	private int dbIndex;
	private DownloadInfoBean info;

	private Context context;

	private static final int INIT = 1;// 定义三种下载的状态：初始化状态，正在下载状态，暂停状态
	private static final int DOWNLOADING = 2;
	private static final int PAUSE = 3;
	private static final int STOP_NOINTERNET = 4;
	private int state = INIT;

	private DownLoadDataBaseUtils dataBaseUtils;

	private SendFileCallback callback;

	/**
	 * BUFFER_SIZE:1024k一个分片
	 */
	private final static int BUFFER_SIZE = 1024 * 1024;

	public DownloadExecutor(int dbIndex, DownloadInfoBean bean, Context context, DownLoadDataBaseUtils dataBaseUtils2,
			SendFileCallback callback) {
		this.dbIndex = dbIndex;
		this.info = bean;
		this.context = context;
		dataBaseUtils = dataBaseUtils2;
		this.callback = callback;
	}

	public boolean isPausedByUser() {
		return state == PAUSE;
	}

	private boolean hasBeenStartedEver = false;

	public boolean isHasBeenStartedEver() {
		return hasBeenStartedEver;
	}

	/**
	 * 判断是否正在下载
	 */
	public boolean isStarted() {
		return state == DOWNLOADING;
	}

	public boolean isOnWait() {
		return state == INIT;
	}

	/**
	 * @Title: getJobInfos
	 * @Description: 获取数据库中任务的信息，这对于由任务管理启动的任务尤其重要
	 * @author: leobert.lan
	 * @param dbIndex
	 */
	private void getJobInfos(int dbIndex) {
		info = new DownloadInfoBean();
		info = dataBaseUtils.getRowById(dbIndex);
		if (StringUtil.isEmpty(info.getRemotepath())) {
			callback.onNotFound(info.getId());
		}
	}

	private String appendToken(String url) {
		SharedPreferences sp = context.getSharedPreferences(IKeyManager.Token.SP_TOKEN, Context.MODE_PRIVATE);
		String access_id = sp.getString(IKeyManager.Token.KEY_ACCESS_ID, "");
		String access_token = sp.getString(IKeyManager.Token.KEY_ACCESS_TOKEN, "");
		return (url + "&access_id=" + access_id + "&access_token=" + access_token);
	}

	/**
	 * @Title: download
	 * @Description: 利用线程开始下载数据
	 * @author: zhangbin
	 */
	public void download() {
		hasBeenStartedEver = true;
		getJobInfos(dbIndex);

		if (dbIndex != -1) {
			if (state == DOWNLOADING) {
				DLog.e(getClass(),
						"job has been started ever,refresh your ui when wifi connected,and donot start a job twice");
				return;
			}

			state = DOWNLOADING;
			new DownloadThread(info.getId(), info.getUsername(), info.getLocalpath(), info.getRemotepath(),
					info.getCompeleteSize(), info.getSize()).start();
			TransProgressBean progressBean = new TransProgressBean();
			progressBean.setId(dbIndex);
			progressBean.setStatus(Status.start);
			progressBean.setMessage("开始下载");
			broadCastProgress(JSON.toJSONString(progressBean));
		} else {
			throw new NullPointerException("infos is null, getDownLoadInfo at first");
		}
	}

	private class DownloadThread extends Thread implements IKeyManager.DownLoadDataBaseKey {

		private String username;
		private String localpath;
		private String remotepath;
		private long completeSize;
		private long size;

		public DownloadThread(int id, String username, String localpath, String remotepath, long completeSize,
				long size) {
			this.username = username;
			this.localpath = localpath;
			this.remotepath = remotepath;
			this.completeSize = completeSize;
			this.size = size;
		}

		@SuppressWarnings("resource")
		@Override
		public void run() {
			dataBaseUtils.updateStatus(dbIndex, VALUE_STATUS_DOWNLOADING);
			HttpURLConnection connection = null;
			RandomAccessFile randomAccessFile = null;
			InputStream is = null;

			DLog.d(getClass(), new LogLocation(), "downloader start connect");
			/*
			 * 这里还没有考虑本地文件被删除的问题
			 */
			try {
				URL url = new URL(appendToken(remotepath));
				DLog.d(getClass(), new LogLocation(), "check url:" + url);
				connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(5000);
				connection.setRequestMethod("GET");
				// 设置范围，格式为Range：bytes x-y;
				connection.setRequestProperty("Accept", "*/*");
				connection.setRequestProperty("Accept-Language", "zh-CN");
				connection.setRequestProperty("Charset", "UTF-8");
				connection.setRequestProperty("Range", "bytes=" + info.getCompeleteSize() + "-" + (size - 1));

				randomAccessFile = new RandomAccessFile(localpath, "rwd");
				randomAccessFile.seek(completeSize);

				if (connection.getResponseCode() == 404) {
					callback.onNotFound(info.getId());
					return;
				} else if (connection.getResponseCode() != 200) {
					DLog.d(getClass(), new LogLocation(),
							"check download connect status:" + connection.getResponseCode());
				}

				is = connection.getInputStream();
				byte[] buffer = new byte[BUFFER_SIZE];
				int length = -1;
				// DLog.d(getClass(), new LogLocation(), "buffer" +
				// buffer.toString());
				while ((length = is.read(buffer)) != -1) {
					// TODO 本身这个读写就不快，频繁更新数据库也浪费了时间
					randomAccessFile.write(buffer, 0, length);

					completeSize += length;
					// 更新数据库中的下载信息
					dataBaseUtils.updateCompleteSize(dbIndex, completeSize);

					TransProgressBean progressBean = new TransProgressBean();
					String s = String.format("%s/%s", FileUtil.calcSize(completeSize), FileUtil.calcSize(size));
					progressBean.setMessage(s);
					progressBean.setId(dbIndex);
					progressBean.setStatus(Status.start);
					String progressMessage = JSON.toJSONString(progressBean);
					broadCastProgress(progressMessage);

					if (state == PAUSE) {
						dataBaseUtils.updateSizeAndStatus(dbIndex, completeSize, VALUE_STATUS_PAUSE);
						is.close();
						return;
					}
					if (state == STOP_NOINTERNET) {
						is.close();
						return;
					}
					if (state == INIT) {
						is.close();
						return;
					}
				}
				// Log.d("dmsg", "c/t" + completeSize + "..." + size);
				if (completeSize < size) {
					DLog.d(getClass(), new LogLocation(), "断网或者服务器异常导致无法下载");
					callback.onFailure(dbIndex);
					return;
				}

				TransProgressBean progressBean = new TransProgressBean();
				progressBean.setId(dbIndex);
				progressBean.setStatus(Status.complete);
				String progressMessage = JSON.toJSONString(progressBean);
				broadCastProgress(progressMessage);
				dataBaseUtils.updateEndTime(dbIndex, TimeUtil.getCurrentTimeStamp());
				dataBaseUtils.updateStatus(dbIndex, VALUE_STATUS_COMPLETE);
				callback.onSendSuccess(dbIndex);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void broadCastProgress(String s) {
		Intent intent = new Intent();
		intent.setAction(IKeyManager.BroadCastProps.DOWNLOAD_BROADCAST_ACTION);
		intent.putExtra(IKeyManager.BroadCastProps.DOWNLOAD_BROADCAST_MESSAGE, s);
		context.sendBroadcast(intent);
	}

	// 设置暂停
	public void pause() {
		state = PAUSE;
	}

	// 重置下载状态
	public void reset() {
		state = INIT;
	}

	public void pauseWhileNoConnect() {
		// 避免改动手动暂停的状态
		if (state != PAUSE)
			state = STOP_NOINTERNET;
	}
}