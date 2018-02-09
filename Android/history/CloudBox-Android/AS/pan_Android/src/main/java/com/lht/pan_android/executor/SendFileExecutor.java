package com.lht.pan_android.executor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;

import com.alibaba.fastjson.JSON;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.ISendFileCallBack.ICheckChunkCallBack;
import com.lht.pan_android.Interface.ISendFileCallBack.ISendChunkCallBack;
import com.lht.pan_android.Interface.ISendFileCallBack.OnFileSendSuccessListener;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.Interface.SendFileCallback;
import com.lht.pan_android.bean.TransProgressBean;
import com.lht.pan_android.bean.UploadJobInfo;
import com.lht.pan_android.clazz.Chunk;
import com.lht.pan_android.test.Debug;
//import com.lht.pan_android.util.ACache;
import com.lht.pan_android.util.CloudBoxApplication;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.DLog.LogLocation;
import com.lht.pan_android.util.TimeUtil;
import com.lht.pan_android.util.database.UpLoadDataBaseUtils;
import com.lht.pan_android.util.file.FileMd5Util;
import com.lht.pan_android.util.file.FileUtil;
import com.lht.pan_android.util.internet.CheckChunkUtil;
import com.lht.pan_android.util.internet.HttpRequestFailureUtil;
import com.lht.pan_android.util.internet.HttpUtil;
import com.lht.pan_android.util.internet.UploadChunkUtil;
import com.lht.pan_android.util.string.StringUtil;
import com.lht.pan_android.view.TransViewInfo.Status;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SendFileExecutor extends Thread implements IKeyManager.FileCombine, IKeyManager.UploadDataBaseKey {

	private final UploadJobInfo job;

	private final HttpUtil mHttpUtil;

	private final String CHECK_EXIST_URL = IUrlManager.UpLoadFile.COMBINE;

	private Context mContext;

	private String tag = "SendFileExecutor";

	private final int NEED_UPLOAD = 404;

	private CheckChunkUtil mCheckChunkUtil;

	private SendFileCallback sendFileCallback = null;

//	private Object mCacheLock = new Object();

	public final static int SECTION_LENGTH =  1024 * 512;

	private Chunk mChunk;

	/**
	 * 循环工作中的进度标示
	 */
	private WorkingStatus status;

	/**
	 * 状态
	 */
	private MachineState state;

	private UploadChunkUtil uploadChunkUtil;

	private long DummyCompleteSize = 0L;

	private final int MAX_WAIT_COUNT = 50;

	private boolean retryJob = false;

	public boolean isRetryJob() {
		return retryJob;
	}

	public void setRetryJob(boolean retryJob) {
		this.retryJob = retryJob;
	}

	/**
	 * fileSendSuccessListener:文件传输成功回调接口
	 */
	private OnFileSendSuccessListener fileSendSuccessListener = new OnFileSendSuccessListener() {

		@Override
		public void onSuccess(int dbIndex) {
			if (dbIndex != job.getDbIndex()) {
				// Log.wtf("tmsg", "fileSend 回调出错");
				// 显然这个不可能的
				return;
			}
			if (null == sendFileCallback)
				throw new NullPointerException("send File callback is null");
			state = MachineState.success;
			dataBaseUtils.updateEndTime(dbIndex, TimeUtil.getCurrentTimeStamp());
			dataBaseUtils.updateStatus(dbIndex, IKeyManager.UploadDataBaseKey.VALUE_STATUS_COMPLETE);
			Log.d(tag, "file Send Success");
			broadCastProgress(job.getDbIndex(), job.getFileSize(), true);
			dataBaseUtils.updateProgress(job.getDbIndex(), job.getFileSize());
			sendFileCallback.onSendSuccess(job.getDbIndex());
		}
	};

	private final UpLoadDataBaseUtils dataBaseUtils;

	public SendFileExecutor(UploadJobInfo job, Context context, UpLoadDataBaseUtils dataBaseUtils) {
		DLog.d(getClass(), new LogLocation(), "check the job id is:" + job.getDbIndex());
		this.job = job;
		mHttpUtil = new HttpUtil();
		this.mContext = context;
		mCheckChunkUtil = new CheckChunkUtil(mContext);
		uploadChunkUtil = new UploadChunkUtil(context, job.getDbIndex(), fileSendSuccessListener);
		this.dataBaseUtils = dataBaseUtils;
		isSuccess = false;
		isAppDie = false;
	}

	/**
	 * @Title: setSendFileCallback
	 * @Description: 设置SendFileCallback回调接口
	 * @author: leobert.lan
	 * @param sendFileCallback
	 */
	public void setSendFileCallback(SendFileCallback sendFileCallback) {
		this.sendFileCallback = sendFileCallback;
	}

	private boolean hasBeenStartedEver = false;

	public boolean isHasBeenStartedEver() {
		return hasBeenStartedEver;
	}

	@Override
	public void run() {
		hasBeenStartedEver = true;
		DLog.d(getClass(), new LogLocation(), "on Run name:" + job.getFinalName() + "id:" + job.getDbIndex());
		this.jobStart();

		if (StringUtil.isEmpty(job.getLocalPath())) {
			sendFileCallback.onNotFound(job.getDbIndex());
			// 2.7.0临时统计一下这个问题
			MobclickAgent.onEvent(CloudBoxApplication.getMainAppContext(), "cb_err_upload_localpath_miss");
			return;
		}
		// only check file is exist
		File f = new File(job.getLocalPath());
		if (!f.exists()) {
			sendFileCallback.onNotFound(job.getDbIndex());
			return;
		}

		String md5 = null;
		md5 = FileMd5Util.getFileMd5String(job.getLocalPath());
		job.setMd5(md5);
		try {
			checkFileExistOnRemote();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.run();
	}

	/**
	 * @Title: jobStart
	 * @Description: 改变state恢复上传,
	 * @author: leobert.lan
	 */
	public void jobStart() {
		DLog.d(getClass(), new LogLocation(), "on job start");
		state = MachineState.uploading;
		status = WorkingStatus.OnSplit;
		isAppDie = false;
		dataBaseUtils.updateStatus(job.getDbIndex(), VALUE_STATUS_UPLOADING);
	}

	/**
	 * @Title: jobPause
	 * @Description: 改变state暂停上传
	 * @author: leobert.lan
	 */
	public void jobPause() {
		state = MachineState.pause;
		DLog.d(getClass(), new LogLocation(), "on job pause");
		dataBaseUtils.updateStatus(job.getDbIndex(), VALUE_STATUS_PAUSE);
	}

	public boolean isPausedByUser() {
		return state == MachineState.pause;
	}

	private boolean isAppDie = false;

	private boolean commitSuicide = false;

	private void commitSuicide() {
		this.commitSuicide = true;
	}

	public boolean isNetChangedEver() {
		return !isAppDie;
	}

	private Object appDieSafeLock = new Object();

	/**
	 * @Title: jobStopWhileAppDie
	 * @Description: 应用退出时安全结束线程
	 * @author: leobert.lan
	 */
	public void jobStopWhileAppDie() {
		synchronized (appDieSafeLock) {
			isAppDie = true;
			dataBaseUtils.updateStatus(job.getDbIndex(), VALUE_STATUS_PAUSE);
		}
	}

	public void jobStopWhileNoInternet() {
		synchronized (appDieSafeLock) {
			isAppDie = true;
		}
	}

	private boolean isSuccess = false;

	private Object jobSuccessSafeLock = new Object();

	public void jobSuccess() {
		synchronized (jobSuccessSafeLock) {
			isSuccess = true;
		}
	}

	public void jobDelete() {
		// 数据库操作在外部，内部处理暂停任务，删除缓冲
		state = MachineState.stop;
		status = WorkingStatus.reset;

//		synchronized (mCacheLock) {
//			if (mCache != null)
//				mCache.clear();
//		}

	}

//	private ACache mCache;

	private byte[] b;

	/**
	 * @Title: splitFromUri
	 * @Description: 使用string获取文件进行切分
	 * @author: leobert.lan
	 * @param uri
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void splitFromPath(String path) throws IOException, InterruptedException {

		status = WorkingStatus.OnSplit;

		File file = new File(path);
		InputStream in = new FileInputStream(file);
		String mCacheName = path;
//		synchronized (mCacheLock) {
//			mCache = ACache.getInstance(mContext, mCacheName);
//		}

		DLog.d(getClass(), new LogLocation(), "i will split");

		byte[] buffer = new byte[SECTION_LENGTH];
		int len = 0;
		int i = 0;
		while (len != -1) {
			int waitCount = 0;
			DLog.d(getClass(), new LogLocation(), "see this to check 阻塞");
			while (status != WorkingStatus.OnSplit || state == MachineState.pause) {
				if (status != WorkingStatus.OnSplit) {
					DLog.d(getClass(), new LogLocation(), "name:" + job.getFinalName() + "\r\nreason is not onsplit");
					waitCount++;
					if (waitCount > MAX_WAIT_COUNT) {
						commitSuicide();
						sendFileCallback.onFailure(job.getDbIndex());
					}
				}
				if (state == MachineState.pause)
					DLog.d(getClass(), new LogLocation(),
							"name:" + job.getFinalName() + "\r\reason is not state is pause");
				if (isAppDie || isSuccess)
					return;
				if (commitSuicide)
					return;
				DLog.d(getClass(), new LogLocation(), "waiting");
				Thread.sleep(200);
			}
			if (isAppDie || isSuccess) {
				DLog.d(getClass(), new LogLocation(),
						"see this mean return ,and you should set the isAppDie and isSuccess\r\n" + "isAppdie?"
								+ (isAppDie == true) + ";issuccess?" + (isSuccess == true));
				return;
			}
			if (commitSuicide)
				return;
			DLog.d(getClass(), new LogLocation(), "read:" + i);
			mChunk = new Chunk();
			mChunk.setChunkIndex(i);
			mChunk.setParentMd5(job.getMd5());
			mChunk.setChunkKey(String.valueOf(i));
			mChunk.setCacheName(mCacheName);
			mChunk.setChunkSize(String.valueOf(SECTION_LENGTH));
			mChunk.setChunkCount(job.getChunks());
			mChunk.setUploadPath(job.getUploadPath());
			mChunk.setFinalName(job.getFinalName());
			mChunk.setOverwrite(job.isOverwrite());
			mChunk.setProjectAccess(job.isProjectAccess());
			len = in.read(buffer);
			mChunk.setActualSize(len);
			DLog.d(getClass(), new LogLocation(), "split round seems ok：" + i);

			// 检验存在和写碎片
			if (len == SECTION_LENGTH) {
				checkAndWrite(mChunk, buffer);
			} else if (len != -1) {
				b = new byte[len];
				for (int j = 0; j < len; j++) {
					b[j] = buffer[j];
				}
				checkAndWrite(mChunk, b);
			} else {
				if (isRetryJob())
					sendFileCallback.onFailure(job.getDbIndex());
				else
					sendFileCallback.onRetry(job.getDbIndex());
			}
			i++;
		}
		DLog.d(getClass(), new LogLocation(), "file split complete");
		in.close();
	}

	/**
	 * @Title: checkAndWrite
	 * @Description: 检验存在和写碎片
	 * @author: leobert.lan
	 * @param c
	 * @param b
	 */
	private void checkAndWrite(Chunk chunk, final byte[] b) {

		DLog.d(getClass(), new LogLocation(), "onChenk chunk");
		DLog.d(getClass(), new LogLocation(), "i will check and write");

		status = WorkingStatus.OnCheckChunk;

		mCheckChunkUtil.setCallBack(new ICheckChunkCallBack() {

			@Override
			public void isChunkExist(Chunk c, boolean isExist) {
				if (!isExist) {
					Log.wtf("upserver", "check chunk not exist, write cache");
					writeToCacheAndSend(String.valueOf(c.getChunkIndex()), b);
				} else {
					spliteAndCheckNext();
					DummyCompleteSize += c.getActualSize();
					broadCastProgress(job.getDbIndex(), DummyCompleteSize, false);
				}
			}

			@Override
			public void sendStatus(int httpStatus) {
				// 如果是500及其以上错误，说明服务器出问题了，提示并关闭任务
				if (httpStatus >= 500) {
					handleRemoteServiceBrokendown();
				}
			}
		});
		mCheckChunkUtil.doJob(chunk);
	}

	/**
	 * @Title: handleRemoteServiceBrokendown
	 * @Description: 服务器异常 的处理
	 * @author: leobert.lan
	 */
	protected void handleRemoteServiceBrokendown() {
		sendFileCallback.onRemoteServiceBrokendown(job.getDbIndex());
	}

	private void writeToCacheAndSend(String key, byte[] b) {
		Log.i(tag, "onWrite chunk \r\nwrite" + key);
//		synchronized (mCacheLock) {
//			Log.i(tag, "see this means lock is corrent");
//			mCache.put(key, b);
//		}
//		TODO
		SendChunk(key,b);
	}

	private void SendChunk(String key,byte[] b) {

		Log.d(tag, "onSend chunk");

		status = WorkingStatus.OnSendChunk;
		uploadChunkUtil.setCallBack(new ISendChunkCallBack() {

			@Override
			public void OnUploadSuccess(Chunk c) {
//				removeChunkCache(c);
				status = WorkingStatus.OnSplit;
				DummyCompleteSize += c.getActualSize();
				Log.d(tag, "complete info:\r\n" + JSON.toJSONString(c));
				Log.i("tmsg", "chunk ok ,属于：" + job.getDbIndex());
				broadCastProgress(job.getDbIndex(), DummyCompleteSize, false);
			}

			@Override
			public void OnUploadFailure(Chunk c) {
				// TODO 已添加失败后全文件重试
				dataBaseUtils.updateStatus(job.getDbIndex(), VALUE_STATUS_FAIL);
				sendFileCallback.onFailure(job.getDbIndex());
				Log.e(tag, "send chunk failure,chunk info:\r\n" + JSON.toJSONString(c));
				commitSuicide();
			}
		});
//		File f;
//		synchronized (mCacheLock) {
//			f = mCache.getAsFile(key);
//		}
		InputStream stream = new ByteArrayInputStream(b);
		uploadChunkUtil.doJob(mChunk, stream);
	}

//	/**
//	 * @Title: removeChunkCache
//	 * @Description: 碎片上传完成后移除
//	 * @author: leobert.lan
//	 * @param c
//	 */
//	protected void removeChunkCache(Chunk c) {
//		synchronized (mCacheLock) {
//			mCache.remove(c.getChunkKey());
//		}
//	}

	protected void spliteAndCheckNext() {
		status = WorkingStatus.OnSplit;
	}

	/**
	 * @Title: checkFileExistOnRemote
	 * @Description: 检验服务端有没有该文件
	 * @author: leobert.lan
	 * @throws IOException
	 */
	private void checkFileExistOnRemote() throws IOException {

		status = WorkingStatus.OnCheckFile;

		File f = new File(job.getLocalPath());
		job.setFileSize(f.length());

		// update filesize and md5
		dataBaseUtils.updateFileSize(job.getDbIndex(), job.getFileSize());
		dataBaseUtils.updateMd5(job.getDbIndex(), job.getMd5());

		job.setChunks(calcChunkCount(f.length(), SECTION_LENGTH));

		RequestParams params = new RequestParams();

		params.add(PARAM_USERNAME, job.getUsername());
		params.add(PARAM_UPLOADPATH, job.getUploadPath());
		params.add(PARAM_CHUNKSIZE, String.valueOf(SECTION_LENGTH));
		params.add(PARAM_CHUNKCOUNT, job.getChunks());
		params.add(PARAM_MD5, job.getMd5());
		params.add(PARAM_FINALNAME, job.getFinalName());
		params.add(PARAM_PROJECTACCESS, VALUE_FALSE);
		params.add(PARAM_OVERWRITE, VALUE_FALSE);

		mHttpUtil.postWithParams(mContext, CHECK_EXIST_URL, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				Log.i(tag, "do not need upload");
				Log.d("tmsg", "donot need upload," + job.getFinalName());
				if (null == sendFileCallback)
					throw new NullPointerException("send File callback is null");
				state = MachineState.success;
				fileSendSuccessListener.onSuccess(job.getDbIndex());
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				if (arg0 == NEED_UPLOAD) {
					Log.i(tag, "need upload;" + job.getFinalName());
					try {
						splitFromPath(job.getLocalPath());
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					if (IKeyManager.Debug.DEBUG_MODE) {
						Debug.Log("combine 1:status:" + arg0);
					}
					if (arg0 != 0) {
						sendFileCallback.onFailure(job.getDbIndex());
					}
				}
				HttpRequestFailureUtil failureUtil = new HttpRequestFailureUtil(mContext);
				failureUtil.handleFailureWithCode(arg0, false);
			}
		});
	}

	private String calcChunkCount(long filesize, int blocksize) {
		long a = filesize / blocksize;
		if (filesize % blocksize != 0)
			a++;
		return String.valueOf(a);
	}

	private enum MachineState {
		wait, pause, uploading, stop, success
	}

	/**
	 * @Title: isOnWait
	 * @Description: 是否排队中
	 * @author: leobert.lan
	 * @return
	 */
	public boolean isOnWait() {
		return state == MachineState.wait;
	}

	/**
	 * use {@link #isPausedByUser()} instead
	 */
	@Deprecated
	public boolean isOnPause() {
		return state == MachineState.pause;
	}

	/**
	 * @Title: isStarted
	 * @Description: 任务是否已经启动了
	 * @author: leobert.lan
	 * @return
	 */
	public boolean isStarted() {
		return state == MachineState.uploading;
	}

	private enum WorkingStatus {
		OnCheckFile, OnCheckChunk, OnSplit, OnSendChunk, reset
	}

	/**
	 * @Title: broadCastProgress
	 * @Description:广播传输进度
	 * @author: leobert.lan
	 * @param dbIndex
	 * @param completeSize
	 * @param isComplete
	 */
	private void broadCastProgress(int dbIndex, long completeSize, boolean isComplete) {
		Log.i(tag, "broadCast progress");
		dataBaseUtils.updateProgress(dbIndex, completeSize);
		Intent intent = new Intent();
		TransProgressBean progressBean = new TransProgressBean();
		progressBean.setId(dbIndex);
		String s = String.format("%s/%s", FileUtil.calcSize(completeSize), FileUtil.calcSize(job.getFileSize()));
		progressBean.setMessage(s);
		if (isComplete)
			progressBean.setStatus(Status.complete);
		else
			progressBean.setStatus(Status.start);

		String message = JSON.toJSONString(progressBean);
		Log.d(tag, "check broadcast msg:\r\n" + message);

		intent.putExtra(IKeyManager.BroadCastProps.UPLOAD_BROADCAST_MESSAGE, message);
		intent.setAction(IKeyManager.BroadCastProps.UPLOAD_BROADCAST_ACTION);
		mContext.sendBroadcast(intent);
	}

	public UploadJobInfo getJob() {
		return job;
	}

	public SendFileCallback getSendFileCallback() {
		return sendFileCallback;
	}

}
