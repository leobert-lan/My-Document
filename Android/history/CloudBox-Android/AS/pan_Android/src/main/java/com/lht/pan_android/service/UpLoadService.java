package com.lht.pan_android.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.alibaba.fastjson.JSON;
import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.ISendFileCallBack;
import com.lht.pan_android.Interface.IThreadPoolManager;
import com.lht.pan_android.Interface.SendFileCallback;
import com.lht.pan_android.activity.BaseActivity;
import com.lht.pan_android.bean.TransProgressBean;
import com.lht.pan_android.bean.UpLoadDbBean;
import com.lht.pan_android.bean.UploadJobInfo;
import com.lht.pan_android.clazz.Events.RefuseOn4G;
import com.lht.pan_android.clazz.Events.TransSettingChanged;
import com.lht.pan_android.executor.SendFileExecutor;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.TimeUtil;
import com.lht.pan_android.util.ToastUtil;
import com.lht.pan_android.util.ToastUtil.Duration;
import com.lht.pan_android.util.activity.TransActivityUtil.MyHandler;
import com.lht.pan_android.util.database.UpLoadDataBaseUtils;
import com.lht.pan_android.view.TransViewInfo.Status;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
//import android.util.//Log;

/**
 * @ClassName: UpLoadService
 * @Description: 上传任务后台服务
 * @date 2015年12月2日 上午9:27:18
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class UpLoadService extends Service implements ISendFileCallBack.ManageTransport {

	private Binder binder = new MyBinder();

	private String tag = "upservice";

	private Object cacheLock;
	private Object mapLock = new Object();

	/**
	 * mThreadMap:存储当前接受的任务，key：数据库记录id，value子线程
	 */
	private static HashMap<Integer, SendFileExecutor> mThreadMap;

	private UpLoadDataBaseUtils dataBaseUtils;

	private ArrayList<UploadJobInfo> mCacheList = new ArrayList<UploadJobInfo>();

	private int MaxThreadCount = IThreadPoolManager.UpLoadManager.MAXIMUM_POOL_SIZE;

	private NetState netState = NetState.disconnected;

	public enum NetState {
		wifi, mobile, disconnected
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public class MyBinder extends Binder {
		public UpLoadService getService() {
			return UpLoadService.this;
		}
	}

	private String username;

	private static boolean isOnlyOnWifi = true;

	@SuppressLint("UseSparseArrays")
	@Override
	public void onCreate() {
		super.onCreate();
		EventBus.getDefault().register(this);
		cacheLock = new Object();
		mThreadMap = new HashMap<Integer, SendFileExecutor>();

		// //Log.i(tag, "oncreate");
		username = getSharedPreferences(IKeyManager.Token.SP_TOKEN, MODE_PRIVATE)
				.getString(IKeyManager.Token.KEY_USERNAME, "");
		this.registerReceiver();
		networkStateCheckAndResumeWork();
	}

	@Subscribe
	public void onEventMainThread(TransSettingChanged event) {
		setOnlyOnWifi(event.isOnlyOnWifi);
	}

	public void setOnlyOnWifi(boolean isOnlyOnWifi) {
		UpLoadService.isOnlyOnWifi = isOnlyOnWifi;
		networkStateCheckAndResumeWork();
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		this.registerReceiver(netChangedReceiver, filter);
	}

	public void doJobs(ArrayList<String> jobs, String uploadPath, String contentType) {
		this.doJobs(jobs, uploadPath, false, null, null, contentType);
	}

	/**
	 * @Title: doJobs
	 * @Description: 添加任务
	 * @author: leobert.lan
	 * @param jobs
	 * @param uploadPath
	 * @param isOverwrite
	 * @param projectId
	 * @param taskId
	 */
	public void doJobs(ArrayList<String> jobs, String uploadPath, boolean isOverwrite, String projectId, String taskId,
			String contentType) {
		// //Log.e("tmsg", "call add upload jobs");
		ArrayList<UpLoadDbBean> dbBeans = new ArrayList<UpLoadDbBean>();
		for (int i = 0; i < jobs.size(); i++) {
			UpLoadDbBean bean = new UpLoadDbBean();
			String name = new File(jobs.get(i)).getName();

			bean.setUsername(username);
			bean.setLocal_path(jobs.get(i));
			bean.setRemote_path(uploadPath);
			bean.setBegin_time(TimeUtil.getCurrentTimeStamp());
			bean.setComplete_size(0L);
			bean.setOverwrite(isOverwrite);
			bean.setProject_id(projectId);
			bean.setTask_id(taskId);
			bean.setFinalName(name);
			bean.setChunk_size(SendFileExecutor.SECTION_LENGTH);
			bean.setStatus(IKeyManager.UploadDataBaseKey.VALUE_STATUS_WAIT);
			bean.setContentType(contentType);

			dbBeans.add(bean);
		}
		ArrayList<Integer> ids = new ArrayList<Integer>();
		ids = dataBaseUtils.insertJobs(dbBeans);
		if (ids.size() > 0) {
			MHandler handler = new MHandler(getMainLooper());
			handler.sendEmptyMessage(MHandler.WHAT_JOBADDED);
		}

		for (int j = 0; j < ids.size(); j++) {
			UpLoadDbBean bean = dataBaseUtils.getRowById(ids.get(j));
			UploadJobInfo jobInfo = changeBean2JobInfo(bean);
			// Log.e("tmsg", "call to add to cache ,id:" +
			// jobInfo.getDbIndex());

			synchronized (cacheLock) {
				mCacheList.add(jobInfo);
			}
		}

		boolean b = false;
		synchronized (cacheLock) {
			b = this.callTryToWork().isEmpty();
		}
		// Log.e("tmsg", "call work result:" + b);
		if (b)
			doNotify();

	}

	private void doNotify() {
		new MHandler(getMainLooper()).sendEmptyMessage(MHandler.WHAT_NOTIFY);
	}

	class MHandler extends Handler {
		static final int WHAT_NOTIFY = 1;

		static final int WHAT_TOAST_WARM4G = 2;

		static final int WHAT_JOBADDED = 3;

		static final int WHAT_JOBSTARTED = 4;

		static final int WHAT_SUCCESS = 5;

		public MHandler(Looper L) {
			super(L);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO check here
			if (msg.what == WHAT_NOTIFY) {
				if (netState == NetState.wifi) {
					// ToastUtil.show(UpLoadService.this,
					// R.string.upload_started, Duration.s);
				} else if (netState == NetState.mobile && isOnlyOnWifi) {
					ToastUtil.show(UpLoadService.this, R.string.trans_jobadded, Duration.s);
					RefuseOn4G.postCount++;
					EventBus.getDefault().post(new RefuseOn4G());
				} else if (netState == NetState.mobile && !isOnlyOnWifi) {
					// ToastUtil.show(UpLoadService.this,
					// R.string.trans_jobadded, Duration.s);
				} else {
					ToastUtil.show(UpLoadService.this, R.string.no_internet, Duration.s);
				}
			} else if (msg.what == WHAT_TOAST_WARM4G) {
				if (netState == NetState.mobile && !isOnlyOnWifi)
					ToastUtil.show(UpLoadService.this, R.string.trans_warmOn4G_toast_upload, Duration.s);
			} else if (msg.what == WHAT_JOBADDED) {
				ToastUtil.show(UpLoadService.this, R.string.trans_jobadded, Duration.s);
			} else if (msg.what == WHAT_JOBSTARTED) {
				ToastUtil.show(UpLoadService.this, R.string.upload_started, Duration.s);
				if (netState == NetState.mobile && !isOnlyOnWifi)
					ToastUtil.show(UpLoadService.this, R.string.trans_warmOn4G_toast_upload, Duration.s);
			} else if (msg.what == WHAT_SUCCESS) {
				ToastUtil.show(UpLoadService.this, R.string.trans_toast_complete, Duration.s);
			}
			super.handleMessage(msg);
		}
	}

	private ArrayList<Integer> callTryToWork() {
		// int ret = START_ONWAIT;
		ArrayList<Integer> ret = new ArrayList<Integer>();
		synchronized (mapLock) {
			// Log.d(tag, "try to add work;map size:" + mThreadMap.size() +
			// ".listsize:" + mCacheList.size());
			while (mThreadMap.size() < MaxThreadCount && mCacheList.size() > 0) {
				// Log.d(tag, "adding");
				UploadJobInfo job = new UploadJobInfo();
				job = mCacheList.get(0);
				mCacheList.remove(0);
				// Log.d(tag, "on call try to work: the id is:" +
				// job.getDbIndex());
				ret.add(addjob(job));
			}
		}
		return ret;
	}

	private UploadJobInfo changeBean2JobInfo(UpLoadDbBean bean) {
		UploadJobInfo info = new UploadJobInfo();
		// skip chunks

		info.setDbIndex(bean.getId());
		// skip filesize

		info.setFinalName(bean.getFinalName());
		info.setLocalPath(bean.getLocal_path());
		// skip md5

		info.setOverwrite(bean.isOverwrite());
		// need to do in stage 2.
		info.setProjectAccess(false);
		info.setUploadPath(bean.getRemote_path());
		info.setUsername(bean.getUsername());
		return info;
	}

	/**
	 * @Title: addjob
	 * @Description: 启动一个任务，注意添加记录
	 * @author: leobert.lan
	 * @param id
	 * @param string
	 */
	private synchronized int addjob(UploadJobInfo job) {
		try {
			SendFileExecutor sendFileExecutor = new SendFileExecutor(job, this, dataBaseUtils);
			// 外部已锁
			mThreadMap.put(job.getDbIndex(), sendFileExecutor);
			sendFileExecutor.setSendFileCallback(sendFileCallback);
			// Log.e("tmsg", "adding success ,try to judge for start");
			doNotify();
			if (netState == NetState.wifi) {
				sendFileExecutor.start();
				MHandler handler = new MHandler(getMainLooper());
				handler.sendEmptyMessage(MHandler.WHAT_JOBSTARTED);
				return START_SUCCESS;
			} else if (!isOnlyOnWifi && netState == NetState.mobile) {
				sendFileExecutor.start();
				MHandler handler = new MHandler(getMainLooper());
				handler.sendEmptyMessage(MHandler.WHAT_JOBSTARTED);
				return START_SUCCESS;
			} else if (isOnlyOnWifi && netState == NetState.mobile) {
				return START_REFUSE_MOBILE;
			} else {
				return START_NONETAVAILABLE;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return START_ONWAIT;
		}

	}

	private SendFileCallback sendFileCallback = new SendFileCallback() {

		@Override
		public void onSendSuccess(int dbIndex) {
			synchronized (mapLock) {
				SendFileExecutor th = mThreadMap.get(dbIndex);
				if (th != null)
					th.jobSuccess();
				th = null;
				mThreadMap.remove(dbIndex);
			}
			MHandler handler = new MHandler(getMainLooper());
			handler.sendEmptyMessage(MHandler.WHAT_SUCCESS);
			synchronized (cacheLock) {
				// Log.d("tmsg", "call try to work");
				callTryToWork();
			}
		}

		@Override
		public void onFailure(int dbIndex) {
			// 该接口用于非重要错误重试，例如连接超时
			// Log.d(tag, "onFailure has been called in service,rowid:" +
			// dbIndex);
			// Log.d(tag, "upload failure:" + dbIndex);
			synchronized (mapLock) {
				// Log.d(tag, "thread remove has been called");
				UpLoadDbBean bean = dataBaseUtils.getRowById(dbIndex);
				UploadJobInfo info = changeBean2JobInfo(bean);
				synchronized (cacheLock) {
					mCacheList.add(0, info);
				}
				mThreadMap.remove(dbIndex);
			}
			synchronized (cacheLock) {
				callTryToWork();
			}
		}

		@Override
		public void onNotFound(int dbIndex) {
			// Log.wtf(tag, "fileNotFound has been called in service,rowid:" +
			// dbIndex);
			// Log.wtf(tag, "upload failure:" + dbIndex);
			dataBaseUtils.updateStatus(dbIndex, IKeyManager.UploadDataBaseKey.VALUE_STATUS_FAIL_FILEDELETED);
			synchronized (mapLock) {
				// Log.d(tag, "thread remove has been called,reason");
				mThreadMap.remove(dbIndex);
			}
			synchronized (cacheLock) {
				callTryToWork();
			}
			TransProgressBean bean = new TransProgressBean();
			bean.setId(dbIndex);
			bean.setMessage("文件不存在");
			bean.setStatus(Status.failure);
			broadcast(bean);
		}

		@Override
		public void onRemoteServiceBrokendown(int dbIndex) {
			// TODO 服务器故障，暂停所有传输并提示

		}

		@Override
		public void onTokenExpired(int dbIndex) {
			// TODO token失效，服务端暂未加token校验

		}

		@Override
		public void onRetry(int dbIndex) {
			synchronized (mapLock) {
				// Log.d(tag, "thread remove has been called");
				SendFileExecutor th = mThreadMap.get(dbIndex);
				if (th != null) {
					SendFileExecutor retry = new SendFileExecutor(th.getJob(), UpLoadService.this, dataBaseUtils);
					mThreadMap.put(dbIndex, retry);
					retry.setRetryJob(true);
					retry.start();
					th = null;
				} else {
					DLog.e(UpLoadService.class, "onRetry upload thread null");
					onFailure(dbIndex);
				}
			}
			synchronized (cacheLock) {
				// Log.d("tmsg", "call try to work");
				callTryToWork();
			}
		}

	};

	@Override
	public void onDestroy() {
		stopAll();
		this.unregisterReceiver(netChangedReceiver);
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	/**
	 * 手动暂停任务
	 * 
	 * @see com.lht.pan_android.Interface.ISendFileCallBack.ManageUpLoad#onPause(int)
	 */
	@Override
	public int onPause(int dbIndex) {
		synchronized (mapLock) {
			SendFileExecutor th = mThreadMap.get(dbIndex);
			if (th != null) {
				th.jobPause();
				return STOP_SUCCESS;
			} else {
				// 缓存中的return false
				synchronized (cacheLock) {
					for (UploadJobInfo job : mCacheList) {
						if (job.getDbIndex() == dbIndex)
							return START_ONWAIT;
					}
				}
				return STOP_ASYNC;
			}
		}
	}

	@Override
	public int onStart(int dbIndex) {
		// Log.d("tmsg", "receive start call:" + dbIndex);

		// 如果设置了wifi且非wifi情况，返回提示
		if (isOnlyOnWifi && netState == NetState.mobile)
			return START_REFUSE_MOBILE;
		// 无网直接提示，没必要放到锁内
		if (netState == NetState.disconnected)
			return START_NONETAVAILABLE;

		synchronized (mapLock) {
			SendFileExecutor th = mThreadMap.get(dbIndex);
			if (th != null) {
				if (th.isNetChangedEver()) {
					// Log.d("tmsg", "isNetChangedEver");
					UpLoadDbBean bean = dataBaseUtils.getRowById(dbIndex);
					SendFileExecutor executor = new SendFileExecutor(changeBean2JobInfo(bean), this, dataBaseUtils);
					executor.setSendFileCallback(sendFileCallback);
					mThreadMap.put(dbIndex, executor);
					executor.start();
				} else {
					// Log.d("tmsg", "jobStart");
					th.jobStart();
				}
				return START_SUCCESS;
			} else {
				// 存在于缓冲队列中的提示等待
				synchronized (cacheLock) {
					for (UploadJobInfo job : mCacheList) {
						if (job.getDbIndex() == dbIndex)
							return START_ONWAIT;
					}
				}
				// 重启后添加任务再此
				UpLoadDbBean bean = dataBaseUtils.getRowById(dbIndex);
				synchronized (cacheLock) {
					mCacheList.add(0, changeBean2JobInfo(bean));
					if (callTryToWork().contains(START_SUCCESS))
						return START_SUCCESS;
					else
						return START_ONWAIT;
				}
			}
		}

	}

	/*
	 * 1.管理中的，暂停删除 2.缓冲中的，移除删除 3.嘿嘿，统统带走
	 */
	@Override
	public boolean onDelete(int dbIndex) {
		boolean ret = false;
		synchronized (mapLock) {
			SendFileExecutor th = mThreadMap.get(dbIndex);
			if (th != null) {
				th.jobDelete();
				mThreadMap.remove(dbIndex);
				this.callTryToWork();
			} else {
				synchronized (cacheLock) {
					for (int i = 0; i < mCacheList.size(); i++) {
						UploadJobInfo job = mCacheList.get(i);
						if (job.getDbIndex() == dbIndex) {
							mCacheList.remove(i);
							break;
						}
					}
				}
			}
			dataBaseUtils.delete(dbIndex);
			ret = true;
		}
		return ret;

	}

	/**
	 * @Title: stopAll
	 * @Description: 关闭所有任务
	 * @author: leobert.lan
	 */
	private void stopAll() {
		synchronized (mapLock) {
			for (Integer key : mThreadMap.keySet()) {
				SendFileExecutor th = mThreadMap.get(key);
				th.jobStopWhileAppDie();
				th = null;
			}
		}
		stopSelf();
	}

	public UpLoadDataBaseUtils getDataBaseUtils() {
		return dataBaseUtils;
	}

	public void setDataBaseUtils(UpLoadDataBaseUtils dataBaseUtils) {
		this.dataBaseUtils = dataBaseUtils;
		dataBaseUtils.initStatus();
	}

	/**
	 * @Title: onWifiConnect
	 * @Description: wifi连接将队列中的非手动暂停的任务重启(直接重新走任务)
	 * @author: leobert.lan
	 */
	private void onWifiConnect() {
		BaseActivity.setConnected(true);
		BaseActivity.setOnWifi(true);
		netState = NetState.wifi;
		synchronized (mapLock) {
			if (mThreadMap.size() > 0) {
				boolean need = false;
				for (Integer key : mThreadMap.keySet()) {
					SendFileExecutor th = mThreadMap.get(key);
					if (!th.isPausedByUser()) {
						need = true;
						break;
					}
				}
				if (need)
					ToastUtil.show(this, R.string.trans_resumeOnWifi, Duration.s);
			}

			for (Integer key : mThreadMap.keySet()) {
				SendFileExecutor th = mThreadMap.get(key);
				// 其实就是key
				int dbIndex = th.getJob().getDbIndex();
				th.jobStopWhileNoInternet();
				if (!th.isPausedByUser()) {
					UpLoadDbBean bean = dataBaseUtils.getRowById(dbIndex);
					SendFileExecutor executor = new SendFileExecutor(changeBean2JobInfo(bean), this, dataBaseUtils);
					executor.setSendFileCallback(sendFileCallback);
					mThreadMap.put(key, executor);
					executor.start();
				}
			}
		}
	}

	/**
	 * @Title: onMobileNetConnect
	 * @Description: mobile连接，根据设置将非手动暂停的任务重启
	 * @author: leobert.lan
	 */
	private void onMobileNetConnect() {
		BaseActivity.setConnected(true);
		BaseActivity.setOnWifi(false);
		netState = NetState.mobile;
		if (!isOnlyOnWifi) {
			synchronized (mapLock) {
				boolean needToast = false;
				for (Integer key : mThreadMap.keySet()) {
					SendFileExecutor th = mThreadMap.get(key);
					int dbIndex = th.getJob().getDbIndex();
					th.jobStopWhileNoInternet();
					if (!th.isPausedByUser()) {
						UpLoadDbBean bean = dataBaseUtils.getRowById(dbIndex);
						SendFileExecutor executor = new SendFileExecutor(changeBean2JobInfo(bean), this, dataBaseUtils);
						executor.setSendFileCallback(sendFileCallback);
						mThreadMap.put(key, executor);
						needToast = true;
						executor.start();
					}
				}
				// TODO 是否需要修改提示内容
				if (needToast) {
					MHandler handler = new MHandler(getMainLooper());
					handler.sendEmptyMessage(MHandler.WHAT_TOAST_WARM4G);
				}
			}
		} else {
			// 仅限wifi情况下，将非暂停的任务状态修改为等待wifi
			synchronized (mapLock) {
				for (Integer key : mThreadMap.keySet()) {
					SendFileExecutor th = mThreadMap.get(key);
					int dbIndex = th.getJob().getDbIndex();
					if (!th.isPausedByUser()) {
						TransProgressBean bean = new TransProgressBean();
						bean.setId(dbIndex);
						if (th.isStarted())
							bean.setStatus(Status.start);
						if (th.isOnWait())
							bean.setStatus(Status.wait);
						// 等待wifi
						bean.setMessage(getResources().getString(R.string.trans_status_waitWifi));
						// 广播出去
						broadcast(bean);
					}
				}
			}
		}
	}

	/**
	 * @Title: onNoInternet
	 * @Description: 结束全部任务，不清理
	 * @author: leobert.lan
	 */
	private void onNoInternet() {
		BaseActivity.setConnected(false);
		BaseActivity.setOnWifi(false);
		netState = NetState.disconnected;
		synchronized (mapLock) {
			for (Integer key : mThreadMap.keySet()) {
				// 认为是正儿八经断网，即使是网络的波动所致，让重连触发任务重启
				mThreadMap.get(key).jobStopWhileNoInternet();

				SendFileExecutor th = mThreadMap.get(key);
				// 发出通知，仅限wifi和非仅限wifi的进行区分，手动暂停的不需要修改status
				TransProgressBean bean = new TransProgressBean();
				bean.setId(key);
				if (!mThreadMap.get(key).isPausedByUser()) {
					// 状态
					if (th.isStarted())
						bean.setStatus(Status.start);
					if (th.isOnWait())
						bean.setStatus(Status.wait);

					if (isOnlyOnWifi)
						bean.setMessage(getResources().getString(R.string.trans_status_waitWifi));
					else
						bean.setMessage(getResources().getString(R.string.trans_status_waitInternet));

					// 广播出去
					broadcast(bean);
				}
			}
		}
	}

	private BroadcastReceiver netChangedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				// Log.d("mark", "网络状态已经改变");
				networkStateCheckAndResumeWork();
			}
		}
	};

	/**
	 * 检测网络状态，并激活任务
	 */
	protected void networkStateCheckAndResumeWork() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobileNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (wifiInfo != null && wifiInfo.isConnected()) {
			onWifiConnect();
		} else if (mobileNetInfo != null && mobileNetInfo.isConnected()) {
			onMobileNetConnect();
		} else {
			onNoInternet();
		}
	}

	private void broadcast(TransProgressBean bean) {
		String message = JSON.toJSONString(bean);
		Intent intent = new Intent();
		intent.putExtra(IKeyManager.BroadCastProps.UPLOAD_BROADCAST_MESSAGE, message);
		intent.setAction(IKeyManager.BroadCastProps.UPLOAD_BROADCAST_ACTION);
		sendBroadcast(intent);
	}

	/**
	 * @Title: deleteAll
	 * @Description: 批量删除，可能效率会低一点点，一点点
	 * @author: leobert.lan
	 * @param temp
	 * @return 失败的总数
	 */
	public synchronized void deleteAll(ArrayList<Integer> temp, MyHandler handler) {
		new Thread(new MyAsyncJobs(temp, handler)).start();
	}

	private class MyAsyncJobs implements Runnable {

		private ArrayList<Integer> temp;
		private final MyHandler handler;

		public MyAsyncJobs(final ArrayList<Integer> temp, MyHandler handler) {
			this.temp = temp;
			this.handler = handler;
		}

		@Override
		public void run() {
			Looper.prepare();

			synchronized (mapLock) {

				for (int i = 0; i < temp.size(); i++) {
					int dbIndex = temp.get(i);

					SendFileExecutor th = mThreadMap.get(dbIndex);
					if (th != null) {
						th.jobDelete();
						mThreadMap.remove(dbIndex);
					} else {
						synchronized (cacheLock) {
							for (int j = 0; j < mCacheList.size(); j++) {
								UploadJobInfo job = mCacheList.get(j);
								if (job.getDbIndex() == dbIndex) {
									mCacheList.remove(j);
									break;
								}
							}
						}
					}
				}
			}
			callTryToWork();
			dataBaseUtils.delete(temp);
			Message msg = new Message();
			msg.what = MyHandler.WHAT_ADDUP;
			handler.sendMessage(msg);
			ToastUtil.show(getApplicationContext(), R.string.string_delete_success, Duration.s);
			Looper.loop();

		}

	}

}
