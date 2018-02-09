package com.lht.pan_android.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

//import org.greenrobot.eventbus.EventBus;

import com.alibaba.fastjson.JSON;
import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.ISendFileCallBack;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.Interface.SendFileCallback;
import com.lht.pan_android.activity.BaseActivity;
import com.lht.pan_android.bean.DirItemBean;
import com.lht.pan_android.bean.DownloadInfoBean;
import com.lht.pan_android.bean.ShareDownloadInfoBean;
import com.lht.pan_android.bean.TransProgressBean;
import com.lht.pan_android.clazz.Events.RefuseOn4G;
import com.lht.pan_android.clazz.Events.TransSettingChanged;
import com.lht.pan_android.executor.DownloadExecutor;
import com.lht.pan_android.service.UpLoadService.NetState;
import com.lht.pan_android.util.TimeUtil;
import com.lht.pan_android.util.ToastUtil;
import com.lht.pan_android.util.ToastUtil.Duration;
import com.lht.pan_android.util.activity.TransActivityUtil.MyHandler;
import com.lht.pan_android.util.database.DownLoadDataBaseUtils;
import com.lht.pan_android.util.file.FileUtil;
import com.lht.pan_android.util.string.StringUtil;
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
//import android.util.Log;
import android.widget.Toast;

/**
 * Created by zhangbin on 2015/12/2.
 */
@SuppressLint("UseSparseArrays")
public class DownloadService extends Service implements IUrlManager.DownloadFile, ISendFileCallBack.ManageTransport {

	private String TAG = "DownloadService";

	private String path = null;
	private String username = null;

	/**
	 * downLoaders:存储当前的任务
	 */
	private static HashMap<Integer, DownloadExecutor> mThreadHashMap = new HashMap<Integer, DownloadExecutor>();
	/**
	 * mCacheList:缓存下载列表
	 */
	private ArrayList<DownloadInfoBean> mCacheList = new ArrayList<DownloadInfoBean>();
	/**
	 * ids:存储列表id
	 */
	private ArrayList<Integer> ids = new ArrayList<Integer>();

	private Binder downLoadServiceBinder = new DownLoadServiceBinder();

	private DownLoadDataBaseUtils dataBaseUtils;

	private static boolean isOnlyOnWifi = true;

	private NetState netState = NetState.disconnected;

	private Object mapLock = new Object();

	private Object cacheLock = new Object();

	@Override
	public void onCreate() {
		super.onCreate();
		EventBus.getDefault().register(this);
		username = this.getSharedPreferences(IKeyManager.Token.SP_TOKEN, Context.MODE_PRIVATE)
				.getString(IKeyManager.Token.KEY_USERNAME, null);
		this.registerReceiver();
		networkStateCheckAndResumeWork();
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		this.registerReceiver(netChangedReceiver, filter);
	}

	public void setOnlyOnWifi(boolean isOnlyOnWifi) {
		DownloadService.isOnlyOnWifi = isOnlyOnWifi;
		networkStateCheckAndResumeWork();
	}

	/**
	 * @Title: getUrl
	 * @Description: 获取不带token的url，cloudbox相关使用
	 * @author: leobert.lan
	 * @return
	 */
	private String getUrl(String path) {
		return DOMAIN + ADDRESS_CB + username + FUNCTION + PARAM + path;
	}

	private static final String tag = "download service:";

	/**
	 * @Title: startDownLoadJob
	 * @Description: 开启批量下载，cloudbox相关调用
	 * @author: zhangbin
	 * @param items
	 * @param localPath
	 */
	public void startDownLoadJob(ArrayList<DirItemBean> items, String localPath) {
		ArrayList<DownloadInfoBean> dBeans = new ArrayList<DownloadInfoBean>();
		for (int i = 0; i < items.size(); i++) {

			try {
				path = URLEncoder.encode(items.get(i).getPath(), "utf-8");
				if (path == null) {
					Toast.makeText(this, R.string.file_not_found, Toast.LENGTH_SHORT).show();
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			DownloadInfoBean dBean = new DownloadInfoBean();
			dBean.setBegin_time(TimeUtil.getCurrentTimeStamp());
			dBean.setCompeleteSize(0);
			dBean.setLocalpath(getUnCrashPath(localPath + "/" + items.get(i).getName()));
			dBean.setRemotepath(getUrl(path));
			dBean.setSize(items.get(i).getSize());
			dBean.setStatus(IKeyManager.DownLoadDataBaseKey.VALUE_STATUS_WAIT);
			dBean.setUsername(username);
			dBean.setIcon(items.get(i).getIcon());

			// 创建本地文件
			FileUtil.createLocalFile(dBean.getLocalpath(), dBean.getSize());

			dBeans.add(dBean);
		}
		ids = dataBaseUtils.insertDownloadJob(dBeans);

		if (ids.size() > 0) {
			MHandler handler = new MHandler(getMainLooper());
			handler.sendEmptyMessage(MHandler.WHAT_JOBADDED);
		}

		for (int i = 0; i < ids.size(); i++) {
			DownloadInfoBean downloadInfoBean = dataBaseUtils.getRowById(ids.get(i));
			synchronized (cacheLock) {
				mCacheList.add(downloadInfoBean);
			}
		}

		boolean b = false;
		synchronized (cacheLock) {
			b = this.callTryToWork().isEmpty();
		}
		if (b)
			doNotify();

	}

	/**
	 * @Title: startDownloadJob
	 * @Description: share 相关调用
	 * @author: leobert.lan
	 * @param items
	 * @param localPath
	 */
	public void startDownloadJob(ArrayList<ShareDownloadInfoBean> items, String localPath) {
		ArrayList<ShareDownloadInfoBean> dBeans = new ArrayList<ShareDownloadInfoBean>();

		String local = getUnCrashPath(localPath + "/" + items.get(0).getName());
		ShareDownloadInfoBean sInfoBean = new ShareDownloadInfoBean();
		for (int i = 0; i < items.size(); i++) {

			try {
				path = URLEncoder.encode(items.get(i).getRemotepath(), "utf-8");
				if (path == null) {
					Toast.makeText(this, R.string.file_not_found, Toast.LENGTH_SHORT).show();
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sInfoBean.setBegin_time(TimeUtil.getCurrentTimeStamp());
			sInfoBean.setCompeleteSize(0);
			sInfoBean.setName(items.get(i).getName());
			sInfoBean.setLocalpath(local);
			sInfoBean.setShareId(items.get(i).getShareId());
			sInfoBean.setOwner(items.get(i).getOwner());
			sInfoBean.setUsername(username);
			sInfoBean.setRemotepath(DOMAIN + ADDRESS_SHARE + sInfoBean.getOwner() + "/share/" + sInfoBean.getShareId()
					+ FUNCTION + PARAM + path + "&queryUsername=" + sInfoBean.getUsername());
			sInfoBean.setSize(items.get(i).getSize());
			sInfoBean.setStatus(IKeyManager.DownLoadDataBaseKey.VALUE_STATUS_WAIT);
			sInfoBean.setIcon(items.get(i).getIcon());
			// // 创建本地文件
			FileUtil.createLocalFile(sInfoBean.getLocalpath(), sInfoBean.getSize());
			dBeans.add(sInfoBean);
		}
		ids = dataBaseUtils.insertShareDownloadJob(dBeans);

		if (ids.size() > 0) {
			MHandler handler = new MHandler(getMainLooper());
			handler.sendEmptyMessage(MHandler.WHAT_JOBADDED);
		}
		for (int i = 0; i < ids.size(); i++) {
			ShareDownloadInfoBean shareDownloadInfoBean = dataBaseUtils.getShareRowById(ids.get(i));
			synchronized (cacheLock) {
				mCacheList.add(shareDownloadInfoBean);
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

	/**
	 * @Title: doNotify
	 * @Description: 进行提示
	 * @author: leobert.lan
	 */
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
			// TODO Auto-generated method stub
			if (msg.what == WHAT_NOTIFY) {
				// Log.e("lmsg", "only on wifi:" + isOnlyOnWifi);
				if (netState == NetState.wifi) {
					// ToastUtil.show(DownloadService.this,
					// R.string.download_start, Duration.s);
				} else if (netState == NetState.mobile && isOnlyOnWifi) {
					ToastUtil.show(DownloadService.this, R.string.trans_jobadded, Duration.s);
					RefuseOn4G.postCount++;
					EventBus.getDefault().post(new RefuseOn4G());
				} else if (netState == NetState.mobile && !isOnlyOnWifi) {
				} else {
					ToastUtil.show(DownloadService.this, R.string.no_internet, Duration.s);
				}
			} else if (msg.what == WHAT_TOAST_WARM4G) {
				if (netState == NetState.mobile && !isOnlyOnWifi)
					ToastUtil.show(DownloadService.this, R.string.trans_warmOn4G_toast_download, Duration.s);
			} else if (msg.what == WHAT_JOBADDED) {
				ToastUtil.show(DownloadService.this, R.string.trans_jobadded, Duration.s);
			} else if (msg.what == WHAT_JOBSTARTED) {
				ToastUtil.show(DownloadService.this, R.string.download_start, Duration.s);
				if (netState == NetState.mobile && !isOnlyOnWifi)
					ToastUtil.show(DownloadService.this, R.string.trans_warmOn4G_toast_download, Duration.s);
			} else if (msg.what == WHAT_SUCCESS) {
				ToastUtil.show(DownloadService.this, R.string.trans_toast_complete, Duration.s);
			}
			super.handleMessage(msg);
		}
	}

	private ArrayList<Integer> callTryToWork() {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		synchronized (mapLock) {
			while (mThreadHashMap.size() < 3 && mCacheList.size() > 0) {
				DownloadInfoBean downJob = new DownloadInfoBean();
				downJob = mCacheList.get(0);
				if (downJob instanceof ShareDownloadInfoBean) {
					ret.addAll(callTryWork());
				} else {
					mCacheList.remove(0);
					ret.add(startJob(downJob));
				}
			}
		}
		return ret;
	}

	private ArrayList<Integer> callTryWork() {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		synchronized (mapLock) {
			while (mThreadHashMap.size() < 3 && mCacheList.size() > 0) {
				ShareDownloadInfoBean downJob = new ShareDownloadInfoBean();
				if (!(mCacheList.get(0) instanceof ShareDownloadInfoBean))
					return ret;
				downJob = (ShareDownloadInfoBean) mCacheList.get(0);
				mCacheList.remove(0);
				ret.add(startJob(downJob));
				// int temp = startJob(downJob);
				// if (ret != START_SUCCESS && temp == START_SUCCESS)
				// ret = START_SUCCESS;
			}
		}
		return ret;
	}

	/**
	 * @Title: startJob
	 * @Description: 分享给我的处
	 * @author: leobert.lan
	 * @param downItem
	 */
	private int startJob(ShareDownloadInfoBean downItem) {

		DownloadExecutor downloader = new DownloadExecutor(downItem.getId(), downItem, this, dataBaseUtils, callback);
		synchronized (mapLock) {
			mThreadHashMap.put(downItem.getId(), downloader);
		}
		doNotify();

		if (netState == NetState.wifi) {
			downloader.download();
			MHandler handler = new MHandler(getMainLooper());
			handler.sendEmptyMessage(MHandler.WHAT_JOBSTARTED);
			return START_SUCCESS;
		} else if (netState == NetState.mobile && isOnlyOnWifi) {
			return START_REFUSE_MOBILE;
		} else if (netState == NetState.mobile && !isOnlyOnWifi) {
			downloader.download();
			MHandler handler = new MHandler(getMainLooper());
			handler.sendEmptyMessage(MHandler.WHAT_JOBSTARTED);
			return START_SUCCESS;
		} else {
			return START_NONETAVAILABLE;
		}

	}

	/**
	 * @Title: starAddJob
	 * @Description: 添加任务，开始下载任务 云盘处
	 * @author: zhangbin
	 * @param item
	 * @param localPath
	 */
	private int startJob(DownloadInfoBean downItem) {

		DownloadExecutor downloader = new DownloadExecutor(downItem.getId(), downItem, this, dataBaseUtils, callback);
		synchronized (mapLock) {
			mThreadHashMap.put(downItem.getId(), downloader);
		}
		doNotify();
		if (netState == NetState.wifi) {
			downloader.download();
			MHandler handler = new MHandler(getMainLooper());
			handler.sendEmptyMessage(MHandler.WHAT_JOBSTARTED);
			return START_SUCCESS;
		} else if (netState == NetState.mobile && isOnlyOnWifi) {
			return START_REFUSE_MOBILE;
		} else if (netState == NetState.mobile && !isOnlyOnWifi) {
			downloader.download();
			MHandler handler = new MHandler(getMainLooper());
			handler.sendEmptyMessage(MHandler.WHAT_JOBSTARTED);
			return START_SUCCESS;
		} else {
			return START_NONETAVAILABLE;
		}
	}

	/**
	 * @Title: getUnCrashPath
	 * @Description: 处理下载同一个文件时文件命名冲突
	 * @author: zhangbin
	 * @param path
	 * @return
	 */
	private String getUnCrashPath(String path) {
		int i = 1;
		String name = null;
		String extension = null;

		if (path.contains(".")) {
			name = path.substring(0, path.lastIndexOf("."));
			extension = path.substring(path.lastIndexOf("."));
		} else {
			name = path;
			extension = "";
		}
		while (new File(path).exists()) {
			path = String.format(name + "(%d)" + extension, i++);
		}
		return path;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return downLoadServiceBinder;
	}

	// @Subscribe
	// public void onEventMainThread(PermitTransOn4G event) {
	// Log.e("lmsg", "try to download");
	// callTryToWork();
	// }

	@Subscribe
	public void onEventMainThread(TransSettingChanged event) {
		// Log.e("lmsg", "check setting:" + event.isOnlyOnWifi);
		setOnlyOnWifi(event.isOnlyOnWifi);
	}

	@Override
	public void onDestroy() {
		// 退出时暂停所有下载
		synchronized (mapLock) {
			for (Integer key : mThreadHashMap.keySet()) {
				mThreadHashMap.get(key).pause();
			}
		}
		this.unregisterReceiver(netChangedReceiver);
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	public class DownLoadServiceBinder extends Binder {
		public DownloadService getService() {
			return DownloadService.this;
		}
	}

	@Override
	public int onPause(int dbIndex) {
		// Log.i("transportM", "down:p" + dbIndex);
		// 暂停线程池中的
		synchronized (mapLock) {
			if (mThreadHashMap.containsKey(dbIndex)) {
				mThreadHashMap.get(dbIndex).pause();
				return STOP_SUCCESS;
			} else {
				synchronized (cacheLock) {
					for (DownloadInfoBean job : mCacheList) {
						if (job.getId() == dbIndex) {
							return START_ONWAIT;
						}
					}
				}
				return STOP_ASYNC;
			}
		}
	}

	@Override
	public int onStart(int dbIndex) {
		// Log.i("transportM", "down:s" + dbIndex);
		if (netState == NetState.disconnected)
			return START_NONETAVAILABLE;
		if (isOnlyOnWifi && netState == NetState.mobile)
			return START_REFUSE_MOBILE;
		// 尝试打开线程池中已经暂停的
		synchronized (mapLock) {
			if (mThreadHashMap.containsKey(dbIndex)) {
				mThreadHashMap.get(dbIndex).download();
				return START_SUCCESS;
			}
		}
		// 尝试添加任务
		synchronized (cacheLock) {
			// 存在于缓冲队列中的提示等待
			for (DownloadInfoBean job : mCacheList) {
				if (job.getId() == dbIndex)
					return START_ONWAIT;
			}
			// 重启后的添加任务
			DownloadInfoBean job = dataBaseUtils.getRowById(dbIndex);
			synchronized (cacheLock) {
				mCacheList.add(0, job);
				if (callTryToWork().contains(START_SUCCESS))
					return START_SUCCESS;
				else
					return START_ONWAIT;
			}
		}
	}

	/*
	 * 已加入管理队列的要暂停再删除 没有的直接删除
	 */
	@Override
	public boolean onDelete(int dbIndex) {
		// Log.i("transportM", "down:d" + dbIndex);
		final DownloadInfoBean bean = dataBaseUtils.getRowById(dbIndex);
		synchronized (mapLock) {
			if (mThreadHashMap.containsKey(dbIndex)) {
				mThreadHashMap.get(dbIndex).pause();
				mThreadHashMap.remove(dbIndex);
			}
		}
		// Log.e("lmsg", "delete file:" + bean.getLocalpath());
		if (!StringUtil.isEmpty(bean.getLocalpath())) {
			FileUtil.delete(new File(bean.getLocalpath()));
		}
		dataBaseUtils.delete(dbIndex);
		callTryToWork();
		return true;
	}

	public void setDataBaseUtils(DownLoadDataBaseUtils dataBaseUtils) {
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
			for (Integer key : mThreadHashMap.keySet()) {
				DownloadExecutor th = mThreadHashMap.get(key);
				if (!th.isPausedByUser()) {
					// Log.d("dmsg", "start down:" + key);
					ToastUtil.show(this, R.string.trans_resumeOnWifi, Duration.s);
					th.download();
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
				for (Integer key : mThreadHashMap.keySet()) {
					DownloadExecutor th = mThreadHashMap.get(key);
					// Log.d("dmsg", "is pause by user:" + th.isPausedByUser());
					if (!th.isPausedByUser()) {
						// Log.d("dmsg", "start down:" + key);
						needToast = true;
						th.download();
					}
				}
				// TODO 是否需要修改提示内容
				if (needToast) {
					MHandler handler = new MHandler(getMainLooper());
					handler.sendEmptyMessage(MHandler.WHAT_TOAST_WARM4G);
				}
			}
		} else {
			// 非手动暂停的，提示等待wifi
			synchronized (mapLock) {
				for (Integer key : mThreadHashMap.keySet()) {
					DownloadExecutor th = mThreadHashMap.get(key);
					if (!th.isPausedByUser()) {
						TransProgressBean bean = new TransProgressBean();
						bean.setId(key);
						if (th.isOnWait())
							bean.setStatus(Status.wait);
						if (th.isStarted())
							bean.setStatus(Status.start);

						bean.setMessage(getResources().getString(R.string.trans_status_waitWifi));
						broadcast(bean);
					}
				}
			}
		}
	}

	/**
	 * @Title: onNoInternet
	 * @Description: 结束全部任务，不清理，TODO发出通知
	 * @author: leobert.lan
	 */
	private void onNoInternet() {
		BaseActivity.setConnected(false);
		BaseActivity.setOnWifi(false);
		netState = NetState.disconnected;
		synchronized (mapLock) {
			for (Integer key : mThreadHashMap.keySet()) {
				mThreadHashMap.get(key).pauseWhileNoConnect();
				// 非手动暂停的进行提示
				DownloadExecutor th = mThreadHashMap.get(key);
				if (!th.isPausedByUser()) {
					TransProgressBean bean = new TransProgressBean();
					bean.setId(key);
					if (th.isOnWait())
						bean.setStatus(Status.wait);
					if (th.isStarted())
						bean.setStatus(Status.start);
					if (isOnlyOnWifi)
						bean.setMessage(getResources().getString(R.string.trans_status_waitWifi));
					else
						bean.setMessage(getResources().getString(R.string.trans_status_waitInternet));
					broadcast(bean);
				}
			}
		}
	}

	private SendFileCallback callback = new SendFileCallback() {

		@Override
		public void onSendSuccess(int dbIndex) {
			synchronized (mapLock) {
				mThreadHashMap.remove(dbIndex);
			}
			MHandler handler = new MHandler(getMainLooper());
			handler.sendEmptyMessage(MHandler.WHAT_SUCCESS);
			synchronized (mapLock) {
				callTryToWork();
			}

		}

		@Override
		public void onFailure(int dbIndex) {
			if (BaseActivity.isConnected) {
				dataBaseUtils.updateStatus(dbIndex, IKeyManager.DownLoadDataBaseKey.VALUE_STATUS_FAIL_SERVERERR);
				TransProgressBean bean = new TransProgressBean();
				bean.setId(dbIndex);
				bean.setMessage("服务端异常");
				bean.setStatus(Status.failure);
				broadcast(bean);
				synchronized (mapLock) {
					mThreadHashMap.remove(dbIndex);
					callTryToWork();
				}
			}
		}

		@Override
		public void onNotFound(int dbIndex) {
			dataBaseUtils.updateStatus(dbIndex, IKeyManager.DownLoadDataBaseKey.VALUE_STATUS_FAIL_FILEDELETED);
			synchronized (mapLock) {
				mThreadHashMap.remove(dbIndex);
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
			// TODO Auto-generated method stub
		}

		@Override
		public void onTokenExpired(int dbIndex) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRetry(int dbIndex) {
			// TODO Auto-generated method stub

		}
	};

	private BroadcastReceiver netChangedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
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
		intent.putExtra(IKeyManager.BroadCastProps.DOWNLOAD_BROADCAST_MESSAGE, message);
		intent.setAction(IKeyManager.BroadCastProps.DOWNLOAD_BROADCAST_ACTION);
		sendBroadcast(intent);
	}

	/**
	 * @Title: deleteAll
	 * @Description: 批量删除任务
	 * @author: leobert.lan
	 * @param temp
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
					if (mThreadHashMap.containsKey(temp.get(i))) {
						mThreadHashMap.get(temp.get(i)).pause();
						mThreadHashMap.remove(temp.get(i));
					}
					final DownloadInfoBean bean = dataBaseUtils.getRowById(temp.get(i));
					FileUtil.delete(new File(bean.getLocalpath()));
				}
			}
			callTryToWork();
			dataBaseUtils.delete(temp);
			Message msg = new Message();
			msg.what = MyHandler.WAHT_ADDDOWN;
			handler.sendMessage(msg);
			ToastUtil.show(getApplicationContext(), R.string.string_delete_success, Duration.s);
			Looper.loop();
		}
	}

	public void doPauseAllInBackground() {
		synchronized (mapLock) {
			for (Integer key : mThreadHashMap.keySet()) {
				mThreadHashMap.get(key).reset();
			}
			mThreadHashMap.clear();
		}
	}
}