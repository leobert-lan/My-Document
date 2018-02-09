package com.lht.pan_android.activity.ViewPagerItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.ISendFileCallBack;
import com.lht.pan_android.activity.BaseActivity;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.activity.asyncProtected.MainActivity;
import com.lht.pan_android.bean.DownloadInfoBean;
import com.lht.pan_android.bean.TransProgressBean;
import com.lht.pan_android.bean.UpLoadDbBean;
import com.lht.pan_android.service.DownloadService;
import com.lht.pan_android.service.UpLoadService;
import com.lht.pan_android.util.CloudBoxApplication;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.DLog.LogLocation;
import com.lht.pan_android.util.TimeUtil;
import com.lht.pan_android.util.ToastUtil;
import com.lht.pan_android.util.ToastUtil.Duration;
import com.lht.pan_android.util.activity.TransActivityUtil;
import com.lht.pan_android.util.database.DownLoadDataBaseUtils;
import com.lht.pan_android.util.database.UpLoadDataBaseUtils;
import com.lht.pan_android.util.file.FileUtil;
import com.lht.pan_android.util.string.StringUtil;
import com.lht.pan_android.view.LinearListWithAnim;
import com.lht.pan_android.view.TransViewInfo;
import com.lht.pan_android.view.TransViewInfo.Status;
import com.lht.pan_android.view.TransViewItem;
import com.lht.pan_android.view.TransViewItem.OnItemSelectedChangedListener;
import com.lht.pan_android.view.popupwins.CustomDialog;
import com.lht.pan_android.view.popupwins.CustomPopupWindow.OnPositiveClickListener;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

@SuppressLint("UseSparseArrays")
public class TransportManagerActivity extends ViewPagerItemActivity implements IKeyManager.UploadDataBaseKey {

	private final static String mPageName = "transportManagerActivity";

	/**
	 * mUpTransMap:上传 数据
	 */
	private LinkedHashMap<String, TransViewItem> mUploadingMap = new LinkedHashMap<String, TransViewItem>();

	/**
	 * mUpTransMap:上传 完成数据
	 */
	private LinkedHashMap<String, TransViewItem> mUploadedMap = new LinkedHashMap<String, TransViewItem>();

	/**
	 * mDownTransMap:下载 数据
	 */
	private LinkedHashMap<String, TransViewItem> mDownloadingMap = new LinkedHashMap<String, TransViewItem>();

	/**
	 * mDownTransMap:下载 完成数据
	 */
	private LinkedHashMap<String, TransViewItem> mDownloadedMap = new LinkedHashMap<String, TransViewItem>();
	// 虽然分数据源这个没什么用了，但去不去也无所谓了
	private final String UP_PREFIX = "up:";

	private final String DOWN_PREFIX = "down:";

	private LinearListWithAnim TransportingList;

	private LinearListWithAnim TransportedList;

	/**
	 * tag:"transportM".
	 */
	private String tag = "transportM";

	private UpLoadService upService;

	private DownloadService downService;

	private boolean isUploadView = false;

	private RadioGroup mRadioGroup;

	private ScrollView scrollView;

	private View nullView;

	private Context mContext;

	private String auth = "";

	private TextView txtColumnEd;

	private TextView txtColumnIng;

	private boolean onDownloadQuery = false;

	private boolean onUploadQuery = false;

	private TransActivityUtil transActivityUtil;

	private CheckBox multiSelect, toogleAll;

	private MainActivity mParent;

	private Button multiDelete;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CloudBoxApplication.addActivity(this);
		setContentView(R.layout.activity_transport_manager);

		mParent = (MainActivity) getParent();

		reportCountEvent(COUNT_CB_TRANS);
		mContext = this;
		transActivityUtil = new TransActivityUtil(mContext);

		initView();

		upProgressReceiver = new UploadProgressReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(IKeyManager.BroadCastProps.UPLOAD_BROADCAST_ACTION);
		registerReceiver(upProgressReceiver, filter);

		downProgressReceiver = new DownloadProgressReceiver();
		IntentFilter filter2 = new IntentFilter();
		filter2.addAction(IKeyManager.BroadCastProps.DOWNLOAD_BROADCAST_ACTION);
		registerReceiver(downProgressReceiver, filter2);

		init();
	}

	/**
	 * @Title: initView
	 * @Description: 实例化控件
	 * @author: leobert.lan
	 */
	private void initView() {
		TransportingList = (LinearListWithAnim) findViewById(R.id.transport_manager_ll_viewlist);
		TransportedList = (LinearListWithAnim) findViewById(R.id.transport_manager_ll_viewlist2);
		mRadioGroup = (RadioGroup) findViewById(R.id.trans_rg_label);
		scrollView = (ScrollView) findViewById(R.id.transport_manager_scrollview);
		nullView = findViewById(R.id.trans_view_norecord);
		txtColumnEd = (TextView) findViewById(R.id.trans_column2);
		txtColumnIng = (TextView) findViewById(R.id.trans_column1);
		multiSelect = (CheckBox) findViewById(R.id.trans_cb_multi);
		toogleAll = (CheckBox) findViewById(R.id.trans_toogel_all);
		// 注意，在main的视图中
		multiDelete = (Button) mParent.findViewById(R.id.main_trans_multicontrol_delete);

		multiDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ArrayList<Integer> temp = transActivityUtil.getSelectedItems();

				if (temp.size() == 0) {
					ToastUtil.show(mContext, R.string.trans_delete_null, Duration.s);
					return;
				}

				CustomDialog dialog = transActivityUtil.getDeleteAlertDialog();
				dialog.setPositiveClickListener(new OnPositiveClickListener() {

					@Override
					public void onPositiveClick() {
						ArrayList<Integer> temp = transActivityUtil.getSelectedItems();
						if (isUploadView) {
							if (null == upService)
								upService = mParent.getUploadService();
							mParent.showWaitView(true);
							upService.deleteAll(temp, transActivityUtil.getDeleteHandler());
						} else {
							if (null == downService)
								downService = mParent.getDownloadService();
							mParent.showWaitView(true);
							downService.deleteAll(temp, transActivityUtil.getDeleteHandler());
						}
						// 关闭多选视图
						multiSelect.setChecked(false);
					}
				});
				dialog.show(multiDelete);
			}
		});
	}

	/**
	 * @Title: hideMultiSelectView
	 * @Description: 隐藏多选视图
	 * @author: leobert.lan
	 */
	protected void hideMultiSelectView() {
		if (!isUploadView) {
			transActivityUtil.switchAll2NormalView(mDownloadedMap);
			transActivityUtil.switchAll2NormalView(mDownloadingMap);
		} else {
			transActivityUtil.switchAll2NormalView(mUploadedMap);
			transActivityUtil.switchAll2NormalView(mUploadingMap);
		}
		mParent.hideTransMultiSelectView();
		toogleAll.setVisibility(View.GONE);
	}

	/**
	 * @Title: showMultiSelectView
	 * @Description: 切换到多选视图
	 * @author: leobert.lan
	 */
	protected void showMultiSelectView() {
		mParent.showTransMultiSelectView();
		if (!isUploadView) {
			transActivityUtil.switchAll2MultiView(mDownloadedMap);
			transActivityUtil.switchAll2MultiView(mDownloadingMap);
		} else {
			transActivityUtil.switchAll2MultiView(mUploadedMap);
			transActivityUtil.switchAll2MultiView(mUploadingMap);
		}
		toogleAll.setVisibility(View.VISIBLE);
		toogleAll.setChecked(false);
		disableDelete();
	}

	/**
	 * @Title: enableDelete
	 * @Description: 激活删除按钮
	 * @author: leobert.lan
	 */
	public void enableDelete() {
		multiDelete.setEnabled(true);
	}

	/**
	 * @Title: disableDelete
	 * @Description: 失活删除按钮
	 * @author: leobert.lan
	 */
	public void disableDelete() {
		multiDelete.setEnabled(false);
	}

	@Override
	public void onResume() {

		upService = ((MainActivity) getParent()).getUploadService();
		upLoadDataBaseUtils = ((MainActivity) getParent()).getUpLoadDataBaseUtils();
		downLoadDataBaseUtils = ((MainActivity) getParent()).getDownLoadDataBaseUtils();
		DLog.d(getClass(), new LogLocation(), "transport onResume");
		if (!isUploadView)
			addDownJobItems();
		else
			addUpJobItems();
		super.onResume();
	}

	/**
	 * @Title: Pause
	 * @Description: 手动调用生命周期-pause
	 * @author: leobert.lan
	 */
	@Override
	protected void onPause() {
		// MobclickAgent.onPageEnd(mPageName);
		// MobclickAgent.onPause(this);
		super.onPause();
	}

	private String username;

	private void init() {
		SharedPreferences sp = getSharedPreferences(IKeyManager.Token.SP_TOKEN, MODE_PRIVATE);
		username = sp.getString(IKeyManager.Token.KEY_USERNAME, "");
		auth = "&" + IKeyManager.Token.KEY_ACCESS_ID + "=" + sp.getString(IKeyManager.Token.KEY_ACCESS_ID, "") + "&"
				+ IKeyManager.Token.KEY_ACCESS_TOKEN + "=" + sp.getString(IKeyManager.Token.KEY_ACCESS_TOKEN, "");
		mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// 先确保多选视图的关闭
				multiSelect.setChecked(false);
				switch (checkedId) {
				case R.id.trans_rb_down:
					isUploadView = false;
					addDownJobItems();
					break;
				case R.id.trans_rb_up:
					isUploadView = true;
					addUpJobItems();
					break;
				default:
					break;

				}
			}
		});
		multiSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				/*
				 * 如果 false 取消多选视图，util取消多选数据， 如果true 显示多选视图
				 */
				if (isChecked) {
					showMultiSelectView();
					buttonView.setText(getResources().getString(R.string.string_cancel));
				} else {
					if (null != transActivityUtil)
						transActivityUtil.removeAllOnViewChanged();
					buttonView.setText(getResources().getString(R.string.string_multiselect));
					hideMultiSelectView();
				}
			}
		});
		toogleAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					// 执行全选
					buttonView.setText(getResources().getString(R.string.string_not_choice));
					if (isUploadView) {
						transActivityUtil.selectAll(mUploadedMap);
						transActivityUtil.selectAll(mUploadingMap);
					} else {
						transActivityUtil.selectAll(mDownloadedMap);
						transActivityUtil.selectAll(mDownloadingMap);
					}
				} else {
					// 执行全不选
					buttonView.setText(getResources().getString(R.string.string_choice_all));
					if (isUploadView) {
						transActivityUtil.deSelectAll(mUploadedMap);
						transActivityUtil.deSelectAll(mUploadingMap);
					} else {
						transActivityUtil.deSelectAll(mDownloadedMap);
						transActivityUtil.deSelectAll(mDownloadingMap);
					}
				}
			}
		});
	}

	/**
	 * @Title: changeMulti2Normal
	 * @Description: 将多选视图模式切换到正常视图模式
	 * @author: leobert.lan
	 */
	public void changeMulti2Normal() {
		multiSelect.setChecked(false);
	}

	/**
	 * @Title: changeNormal2Multi
	 * @Description: 将正常视图模式切换到多选视图模式
	 * @author: leobert.lan
	 */
	public void changeNormal2Multi() {
		multiSelect.setChecked(true);
	}

	private OnTransCallback callback = new OnTransCallback() {

		@Override
		public boolean OnUploadPause(int dbIndex) {
			if (upService == null)
				upService = ((MainActivity) getParent()).getUploadService();
			int ret = upService.onPause(dbIndex);
			if (ret == ISendFileCallBack.ManageTransport.STOP_SUCCESS) {
				ToastUtil.show(mContext, R.string.trans_toast_pause, Duration.s);
				return true;
			} else {
				if (ret == ISendFileCallBack.ManageTransport.STOP_ASYNC) {
					addUpJobItems();
				}
				return false;
			}
		}

		@Override
		public boolean OnUploadRestart(int dbIndex) {
			if (upService == null)
				upService = ((MainActivity) getParent()).getUploadService();
			DLog.d(getClass(), new LogLocation(), "call restart");
			int ret = upService.onStart(dbIndex);
			return feedbackByOprateRet(ret);
		}

		private boolean feedbackByOprateRet(int ret) {
			if (ret == ISendFileCallBack.ManageTransport.START_SUCCESS) {
				ToastUtil.show(mContext, R.string.trans_toast_start, Duration.s);
				return true;
			} else if (ret == ISendFileCallBack.ManageTransport.START_ONWAIT) {
				ToastUtil.show(mContext, R.string.trans_toast_wait, Duration.s);
				return false;
			} else if (ret == ISendFileCallBack.ManageTransport.START_REFUSE_MOBILE) {
				showChangeSettingGuideDialog();
				return false;
			} else {
				ToastUtil.show(mContext, R.string.no_internet, Duration.s);
				return false;
			}
		}

		@Override
		public boolean OnDownloadPause(int dbIndex) {
			if (downService == null)
				downService = ((MainActivity) getParent()).getDownloadService();
			int ret = downService.onPause(dbIndex);
			if (ret == ISendFileCallBack.ManageTransport.STOP_SUCCESS) {
				ToastUtil.show(mContext, R.string.trans_toast_pause, Duration.s);
				return true;
			} else {
				if (ret == ISendFileCallBack.ManageTransport.STOP_ASYNC) {
					addDownJobItems();
				}
				// 剩余STOP_ONWAIT 逻辑上不可能出现
				return false;
			}
		}

		@Override
		public boolean OnDownloadRestart(int dbIndex) {
			if (downService == null)
				downService = ((MainActivity) getParent()).getDownloadService();
			int ret = downService.onStart(dbIndex);
			DLog.e(TransportManagerActivity.class, new LogLocation(), "end restart" + ret);
			return feedbackByOprateRet(ret);
		}

		@Override
		public boolean OnUploadDelete(int dbIndex) {
			if (upService == null)
				upService = ((MainActivity) getParent()).getUploadService();
			boolean ret = upService.onDelete(dbIndex);
			if (ret)
				ToastUtil.show(mContext, R.string.string_delete_success, Duration.s);
			String key = UP_PREFIX + dbIndex;
			if (mUploadingMap.containsKey(key)) {
				if (isUploadView)
					TransportingList.removeOneCell(mUploadingMap.get(key).getItem());
				mUploadingMap.remove(key);
				checkNoRecord(mUploadedMap.size(), mUploadingMap.size());
			} else if (mUploadedMap.containsKey(key)) {
				if (isUploadView)
					TransportedList.removeOneCell(mUploadedMap.get(key).getItem());
				mUploadedMap.remove(key);
				checkNoRecord(mUploadedMap.size(), mUploadingMap.size());
			} else {
				addUpJobItems();
			}
			return true;
		}

		@Override
		public boolean OnDownloadDelete(int dbIndex) {
			if (downService == null)
				downService = ((MainActivity) getParent()).getDownloadService();
			boolean ret = downService.onDelete(dbIndex);
			if (ret)
				ToastUtil.show(mContext, R.string.string_delete_success, Duration.s);
			String key = DOWN_PREFIX + dbIndex;
			if (mDownloadingMap.containsKey(key)) {
				if (!isUploadView)
					TransportingList.removeOneCell(mDownloadingMap.get(key).getItem());
				mDownloadingMap.remove(key);
				checkNoRecord(mDownloadedMap.size(), mDownloadingMap.size());
			} else if (mDownloadedMap.containsKey(key)) {
				if (!isUploadView)
					TransportedList.removeOneCell(mDownloadedMap.get(key).getItem());
				mDownloadedMap.remove(key);
				checkNoRecord(mDownloadedMap.size(), mDownloadingMap.size());
			} else {
				addDownJobItems();
			}
			return true;
		}

	};

	private UpLoadDataBaseUtils upLoadDataBaseUtils;
	private DownLoadDataBaseUtils downLoadDataBaseUtils;

	private ArrayList<UpLoadDbBean> uploadingJobList;

	private ArrayList<UpLoadDbBean> uploadedJobList;

	private ArrayList<DownloadInfoBean> downloadingJobList;

	private ArrayList<DownloadInfoBean> downloadedJobList;

	/**
	 * @Title: addUpJobItems
	 * @Description: 异步任务获取上传任务记录并显示
	 * @author: leobert.lan
	 */
	public synchronized void addUpJobItems() {
		if (!isUploadView)
			return;
		if (onUploadQuery)
			return;
		AddUploadJob addUploadJob = new AddUploadJob(new LoadUploadRecordHandler());
		addUploadJob.start();
	}

	/**
	 * @Title: showChangeSettingGuideDialog
	 * @Description: 显示
	 * @author: leobert.lan
	 */
	protected void showChangeSettingGuideDialog() {
		transActivityUtil.getChangeSettingGuideDialog().show();
	}

	/*
	 * 防止多线程同时发出了广播导致crash
	 */
	public synchronized void addDownJobItems() {
		if (isUploadView)
			return;
		if (onDownloadQuery)
			return;
		AddDownloadJob addDownloadJob = new AddDownloadJob(new LoadDownloadRecordHandler());
		addDownloadJob.start();
	}

	private OnItemSelectedChangedListener itemSelectedChangedListener = new OnItemSelectedChangedListener() {

		@Override
		public void OnItemSelectedChanged(boolean isSelected, int dbIndex) {
			if (isSelected)
				transActivityUtil.addSelectItem(dbIndex);
			else
				transActivityUtil.removeSelectItem(dbIndex);
		}
	};

	private void initUploadItemView(ArrayList<UpLoadDbBean> list, LinkedHashMap<String, TransViewItem> map) {
		map.clear();
		for (int i = 0; i < list.size(); i++) {
			UpLoadDbBean bean = list.get(i);
			TransViewInfo info = new TransViewInfo();

			info.setIconUrl(bean.getLocal_path());
			info.setDbIndex(bean.getId());
			info.setName(bean.getFinalName());
			if (bean.getStatus() == VALUE_STATUS_PAUSE) {
				info.setStatus(Status.pause);
				info.setComment("已暂停");
			} else if (bean.getStatus() == VALUE_STATUS_UPLOADING) {
				info.setStatus(Status.start);
				if (BaseActivity.isConnected) {
					if (BaseActivity.isOnlyOnWifi && !BaseActivity.isOnWifi()) {
						info.setComment(getResources().getString(R.string.trans_status_waitWifi));
					} else {
						info.setComment(getResources().getString(R.string.trans_status_started));
					}
				} else {
					info.setComment(getResources().getString(R.string.trans_status_waitInternet));
				}
			} else if (bean.getStatus() == VALUE_STATUS_WAIT) {
				info.setStatus(Status.wait);
				info.setComment("等待中");
			} else if (bean.getStatus() == VALUE_STATUS_COMPLETE) {
				info.setStatus(Status.complete);
				String s1 = TimeUtil.getFormatedTime(bean.getEnd_time());
				String s2 = FileUtil.calcSize(bean.getFile_size());
				info.setComment(String.format("%s  %s", s1, s2));
			} else {
				info.setStatus(Status.failure);
				info.setComment("文件不存在");
			}
			info.setUpload(true);
			info.setContentType(bean.getContentType());
			TransViewItem item = new TransViewItem(this, auth, info, callback);
			item.setOnItemSelectedChangedListener(itemSelectedChangedListener);
			item.setOnItemBodyClickListener(transActivityUtil.getOnItemBodyClickListner());
			map.put(UP_PREFIX + item.getInfo().getDbIndex(), item);
		}
	}

	private void initDownloadItemView(ArrayList<DownloadInfoBean> list, LinkedHashMap<String, TransViewItem> map) {
		map.clear();
		for (int i = 0; i < list.size(); i++) {
			DownloadInfoBean bean = list.get(i);

			TransViewInfo info = new TransViewInfo();
			info.setIconUrl(StringUtil.removeAuth(bean.getIcon()));

			info.setDbIndex(bean.getId());
			String p = bean.getLocalpath();
			info.setName(p.substring(p.lastIndexOf("/") + 1));
			if (bean.getStatus() == IKeyManager.DownLoadDataBaseKey.VALUE_STATUS_PAUSE) {
				info.setStatus(Status.pause);
				info.setComment("已暂停");
			} else if (bean.getStatus() == IKeyManager.DownLoadDataBaseKey.VALUE_STATUS_DOWNLOADING) {
				info.setStatus(Status.start);
				if (BaseActivity.isOnlyOnWifi && !BaseActivity.isOnWifi()) {
					info.setComment(getResources().getString(R.string.trans_status_waitWifi));
				} else {
					info.setComment(getResources().getString(R.string.trans_status_started));
				}
			} else if (bean.getStatus() == IKeyManager.DownLoadDataBaseKey.VALUE_STATUS_WAIT) {
				info.setComment("等待中");
				info.setStatus(Status.wait);
			} else if (bean.getStatus() == IKeyManager.DownLoadDataBaseKey.VALUE_STATUS_COMPLETE) {
				info.setStatus(Status.complete);
				String s1 = TimeUtil.getFormatedTime(bean.getEnd_time());
				String s2 = FileUtil.calcSize(bean.getSize());
				info.setComment(String.format("%s  %s", s1, s2));
			} else if (bean.getStatus() == IKeyManager.DownLoadDataBaseKey.VALUE_STATUS_FAIL_SERVERERR) {
				info.setStatus(Status.failure);
				info.setComment("服务端异常");
			} else {
				info.setStatus(Status.failure);
				info.setComment("文件不存在");
			}
			info.setUpload(false);
			TransViewItem item = new TransViewItem(this, auth, info, callback);
			item.setOnItemSelectedChangedListener(itemSelectedChangedListener);
			item.setOnItemBodyClickListener(transActivityUtil.getOnItemBodyClickListner());
			map.put(DOWN_PREFIX + item.getInfo().getDbIndex(), item);
		}
	}

	private void unRegisteReceiver() {
		if (null != upProgressReceiver)
			unregisterReceiver(upProgressReceiver);
		if (null != downProgressReceiver)
			unregisterReceiver(downProgressReceiver);
	}

	private UploadProgressReceiver upProgressReceiver;

	private DownloadProgressReceiver downProgressReceiver;

	private boolean activityFinish = false;

	public class UploadProgressReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String message = intent.getStringExtra(IKeyManager.BroadCastProps.UPLOAD_BROADCAST_MESSAGE);
			TransProgressBean bean = JSON.parseObject(message, TransProgressBean.class);
			String key = UP_PREFIX + bean.getId();
			if (!isUploadView || isFinishing())
				return;
			if (bean.getStatus() == Status.complete) {
				ToastUtil.show(mContext, R.string.trans_toast_complete, Duration.s);
				if (mUploadingMap.containsKey(key)) {
					// 秒传的数据库会很快更新，此处没有记录，不注意就会空指针
					TransportingList.removeView(mUploadingMap.get(key).getItem());
					TransViewItem item = mUploadingMap.get(key);
					item.updateProgress(TimeUtil.getFormatedTime(TimeUtil.getCurrentTimeStamp()));
					item.getInfo().setStatus(Status.complete);
					item.UpdateViewWhileComplete();
					TransportedList.addView(item.getItem());
					mUploadingMap.remove(key);
					// 为确保多选视图正确显示
					mUploadedMap.put(key, item);
				}
				// 此处不能根据数据源判断，因为没有刷新数据源
				checkNoRecord(TransportedList.getChildCount(), TransportingList.getChildCount());
			} else if (bean.getStatus() == Status.failure) {
				if (mUploadingMap.containsKey(key)) {
					// 秒传的数据库会很快更新，此处没有记录，不注意就会空指针
					TransportingList.removeView(mUploadingMap.get(key).getItem());
					TransViewItem item = mUploadingMap.get(key);
					item.getInfo().setStatus(Status.failure);
					item.getInfo().setComment(bean.getMessage());
					item.UpdateViewWhileComplete();
					TransportedList.addView(item.getItem());
					mUploadingMap.remove(key);
					// 为确保多选视图正确显示
					mUploadedMap.put(key, item);
				}
				// 此处不能根据数据源判断，因为没有刷新数据源
				checkNoRecord(TransportedList.getChildCount(), TransportingList.getChildCount());

			} else {
				/*
				 * Attention 注意，这里将非complete状态汇总了，如果要进行扩展，请留意网络状态变化时的广播
				 */
				if (mUploadingMap.get(key) != null)
					mUploadingMap.get(key).updateProgress(bean.getMessage());
			}

		}

	}

	public class DownloadProgressReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String progress = intent.getStringExtra(IKeyManager.BroadCastProps.DOWNLOAD_BROADCAST_MESSAGE);
			TransProgressBean bean = JSON.parseObject(progress, TransProgressBean.class);
			String key = DOWN_PREFIX + bean.getId();

			DLog.d(getClass(), new LogLocation(), "key:" + key + "message:" + progress);
			if (isUploadView || isFinishing())
				return;
			if (bean.getStatus() == Status.complete) {

				if (mDownloadingMap.containsKey(key)) {
					TransportingList.removeView(mDownloadingMap.get(key).getItem());
					TransViewItem item = mDownloadingMap.get(key);
					item.updateProgress(TimeUtil.getFormatedTime(TimeUtil.getCurrentTimeStamp()));
					item.getInfo().setStatus(Status.complete);
					item.UpdateViewWhileComplete();
					TransportedList.addView(item.getItem());
					mDownloadingMap.remove(key);
					mDownloadedMap.put(key, item);
				}
				// 此处不能按照数据源判断，因为没有刷新数据源
				checkNoRecord(TransportedList.getChildCount(), TransportingList.getChildCount());
			} else if (bean.getStatus() == Status.failure) {
				if (mDownloadingMap.containsKey(key)) {
					TransportingList.removeView(mDownloadingMap.get(key).getItem());
					TransViewItem item = mDownloadingMap.get(key);
					item.getInfo().setStatus(Status.failure);
					item.getInfo().setComment(bean.getMessage());
					item.UpdateViewWhileComplete();
					TransportedList.addView(item.getItem());
					mDownloadingMap.remove(key);
					// 为确保多选视图正确显示
					mDownloadedMap.put(key, item);
				} else {
					addDownJobItems();
				}
				// 此处不能根据数据源判断，因为没有刷新数据源
				checkNoRecord(TransportedList.getChildCount(), TransportingList.getChildCount());

			} else {
				if (mDownloadingMap.get(key) != null)
					mDownloadingMap.get(key).updateProgress(bean.getMessage());
			}
		}
	}

	/**
	 * @ClassName: OnTransCallback
	 * @Description: 控制接口
	 * @date 2015年12月9日 下午1:48:56
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	public interface OnTransCallback {
		boolean OnUploadPause(int dbIndex);

		boolean OnUploadRestart(int dbIndex);

		boolean OnDownloadPause(int dbIndex);

		boolean OnDownloadRestart(int dbIndex);

		boolean OnUploadDelete(int dbIndex);

		boolean OnDownloadDelete(int dbIndex);
	}

	public void checkNoRecord(int completedCount, int transportingCount) {
		if (completedCount + transportingCount == 0) {
			scrollView.setVisibility(View.GONE);
			nullView.setVisibility(View.VISIBLE);
		} else {
			nullView.setVisibility(View.GONE);
			scrollView.setVisibility(View.VISIBLE);
		}
		String s1 = String.format(getResources().getString(R.string.trans_column_transported), completedCount);
		String s2 = String.format(getResources().getString(R.string.trans_column_transporting), transportingCount);
		txtColumnEd.setText(s1);
		txtColumnIng.setText(s2);

		if (completedCount == 0)
			txtColumnEd.setVisibility(View.GONE);
		else
			txtColumnEd.setVisibility(View.VISIBLE);
		if (transportingCount == 0)
			txtColumnIng.setVisibility(View.GONE);
		else
			txtColumnIng.setVisibility(View.VISIBLE);

	}

	public String getUsername() {
		return username;
	}

	/**
	 * @Title: isMultiMode
	 * @Description: 是否是多选视图模式
	 * @author: leobert.lan
	 * @return
	 */
	private boolean isMultiMode() {
		return multiSelect.isChecked();
	}

	/**
	 * @Title: back
	 * @Description: 重写其返回事件，由MainActivity负责分发
	 * @author: leobert.lan
	 * @return
	 */
	@Override
	public boolean back() {
		if (this.isMultiMode()) {
			changeMulti2Normal();
			return true;
		}
		return false;
	}

	public class LoadUploadRecordHandler extends Handler {

		public static final int WHAT_PREPARE = 1;

		public static final int WHAT_POST = 2;

		public LoadUploadRecordHandler() {
		}

		public LoadUploadRecordHandler(Looper L) {
			super(L);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == WHAT_PREPARE) {
				mParent.showWaitView(true);
			} else if (msg.what == WHAT_POST) {
				if (activityFinish)
					return;

				checkNoRecord(mUploadedMap.size(), mUploadingMap.size());
				DLog.d(getClass(), new LogLocation(), "" + mUploadedMap.size());
				TransportingList.removeAllViews();
				TransportedList.removeAllViews();
				
				for (String key : mUploadingMap.keySet()) {
					TransportingList.addView(mUploadingMap.get(key).getItem());
				}
				for (String key : mUploadedMap.keySet()) {
					TransportedList.addView(mUploadedMap.get(key).getItem());
				}

//				ListIterator<Map.Entry<String, TransViewItem>> i = new ArrayList<Map.Entry<String, TransViewItem>>(
//						mUploadingMap.entrySet()).listIterator(mUploadingMap.size());
//				while (i.hasPrevious()) {
//					Map.Entry<String, TransViewItem> entry = i.previous();
//					TransportingList.addView(entry.getValue().getItem());
//				}
//
//				ListIterator<Map.Entry<String, TransViewItem>> i2 = new ArrayList<Map.Entry<String, TransViewItem>>(
//						mUploadedMap.entrySet()).listIterator(mUploadedMap.size());
//				while (i2.hasPrevious()) {
//					Map.Entry<String, TransViewItem> entry = i2.previous();
//					TransportedList.addView(entry.getValue().getItem());
//				}
				mParent.cancelWaitView();
			} else {
				throw new IllegalArgumentException("error message what");
			}
		}
	}

	public class AddUploadJob extends Thread {
		private final LoadUploadRecordHandler handler;

		public AddUploadJob(LoadUploadRecordHandler handler) {
			this.handler = handler;
		}

		@Override
		public void run() {
			super.run();
			prepare();
			getDbInfo();
			updateUi();
		}

		private void prepare() {
			onUploadQuery = true;
			handler.sendEmptyMessage(LoadUploadRecordHandler.WHAT_PREPARE);
		}

		private void getDbInfo() {
			uploadingJobList = upLoadDataBaseUtils.getUnCompletedJobs(getUsername());
			uploadedJobList = upLoadDataBaseUtils.getCompletedJobs(getUsername());
			initUploadItemView(uploadingJobList, mUploadingMap);
			initUploadItemView(uploadedJobList, mUploadedMap);
		}

		private void updateUi() {
			DLog.d(getClass(), new LogLocation(), "ed2:" + uploadedJobList.size());
			if (isCancel)
				return;
			handler.sendEmptyMessage(LoadUploadRecordHandler.WHAT_POST);
			onUploadQuery = false;
		}

		private boolean isCancel = false;

		public void cancel() {
			isCancel = true;
			onUploadQuery = false;
		}
	}

	public class LoadDownloadRecordHandler extends Handler {

		public static final int WHAT_PREPARE = 1;

		public static final int WHAT_POST = 2;

		public LoadDownloadRecordHandler() {
		}

		public LoadDownloadRecordHandler(Looper L) {
			super(L);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == WHAT_PREPARE) {
				mParent.showWaitView(true);
			} else if (msg.what == WHAT_POST) {
				if (activityFinish)
					return;

				TransportingList.removeAllViews();
				TransportedList.removeAllViews();

				for (String key : mDownloadingMap.keySet()) {
					TransportingList.addView(mDownloadingMap.get(key).getItem());
				}
				for (String key : mDownloadedMap.keySet()) {
					TransportedList.addView(mDownloadedMap.get(key).getItem());
				}

				DLog.d(getClass(), new LogLocation(), "ed:" + mDownloadedMap.size());

				checkNoRecord(mDownloadedMap.size(), mDownloadingMap.size());
				mParent.cancelWaitView();

				onDownloadQuery = false;
			} else {
				throw new IllegalArgumentException("error message what");
			}
		}
	}

	public class AddDownloadJob extends Thread {
		private final LoadDownloadRecordHandler handler;

		public AddDownloadJob(LoadDownloadRecordHandler handler) {
			this.handler = handler;
		}

		@Override
		public void run() {
			super.run();
			prepare();
			getDbInfo();
			updateUi();
		}

		private void prepare() {
			onDownloadQuery = true;
			handler.sendEmptyMessage(LoadDownloadRecordHandler.WHAT_PREPARE);
		}

		private void getDbInfo() {
			downloadingJobList = downLoadDataBaseUtils.getUnCompletedJobs(getUsername());
			downloadedJobList = downLoadDataBaseUtils.getCompletedJobs(getUsername());
			initDownloadItemView(downloadingJobList, mDownloadingMap);
			initDownloadItemView(downloadedJobList, mDownloadedMap);
		}

		private void updateUi() {
			DLog.d(getClass(), new LogLocation(), "ed2:" + downloadedJobList.size());
			if (isCancel)
				return;
			handler.sendEmptyMessage(LoadDownloadRecordHandler.WHAT_POST);
			onDownloadQuery = false;
		}

		private boolean isCancel = false;

		public void cancel() {
			isCancel = true;
			onDownloadQuery = false;
		}
	}

	private void Destroy() {
		activityFinish = true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Destroy();
		unRegisteReceiver();
		Log.e("lmsg", "trans on destroy");
		super.onDestroy();
	}

	@Override
	protected String getPageName() {
		return TransportManagerActivity.mPageName;
	}

	@Override
	public UMengActivity getActivity() {
		return TransportManagerActivity.this;
	}
}
