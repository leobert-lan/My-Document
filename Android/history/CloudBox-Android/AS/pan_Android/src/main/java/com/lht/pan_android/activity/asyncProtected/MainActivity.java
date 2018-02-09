package com.lht.pan_android.activity.asyncProtected;

import java.util.ArrayList;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.Subscribe;

import com.alibaba.fastjson.JSON;
import com.lht.pan_android.BackgroundActivity;
import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.Interface.MainActivityIPreventPenetrate;
import com.lht.pan_android.Interface.OnTabItemClickListener;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.activity.ViewPagerItem.CloudBoxActivity;
import com.lht.pan_android.activity.ViewPagerItem.SettingActivity;
import com.lht.pan_android.activity.ViewPagerItem.ShareActivity;
import com.lht.pan_android.activity.ViewPagerItem.TransportManagerActivity;
import com.lht.pan_android.activity.selectItem.SelectImageGroupActivity;
import com.lht.pan_android.activity.selectItem.SelectMediaGroupActivity;
import com.lht.pan_android.adapter.MainViewPagerAdapter;
import com.lht.pan_android.clazz.Events.RefuseOn4G;
import com.lht.pan_android.clazz.Events.ShareReportEvent;
import com.lht.pan_android.clazz.TabItem;
import com.lht.pan_android.service.DownloadService;
import com.lht.pan_android.service.UpLoadService;
import com.lht.pan_android.util.BottomBarItemUtil;
import com.lht.pan_android.util.CloudBoxApplication;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.DLog.LogLocation;
import com.lht.pan_android.util.Exit;
import com.lht.pan_android.util.UpdateUtil;
import com.lht.pan_android.util.database.DownLoadDataBaseUtils;
import com.lht.pan_android.util.database.UpLoadDataBaseUtils;
import com.lht.pan_android.util.internet.HttpRequestFailureUtil;
import com.lht.pan_android.util.internet.HttpUtil;
import com.lht.pan_android.view.TabView;
import com.lht.pan_android.view.popupwins.CustomDialog;
import com.lht.pan_android.view.popupwins.CustomPopupWindow.OnPositiveClickListener;
import com.lht.pan_android.view.popupwins.SuperButtonPopUpWindow;
import com.lht.pan_android.view.popupwins.SuperButtonPopUpWindow.OnFolderClickListener;
import com.lht.pan_android.view.popupwins.SuperButtonPopUpWindow.OnPictureClickListener;
import com.lht.pan_android.view.popupwins.SuperButtonPopUpWindow.OnVedioClickListener;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.app.LocalActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class MainActivity extends AsyncProtectedActivity {
	private final static String mPageName = "mainActivity";

	private Context mContext;

	Exit exit = new Exit();

	private TabView tabCloudBox;

	private TabView tabProject;

	private TabView tabTransport;

	private TabView tabSetting;

	private int width;

	private ViewPager mViewPager;
	public ProgressBar mProgressBar;

	private LocalActivityManager manager = null;

	private BottomBarItemUtil mTabBarUtil;

	private CheckBox mSuperBtn;
	private PopupWindow popupWindow;

	private SharedPreferences sharedPreferences;
	private String username;
	private String access_id;
	private String access_token;

	private final int REQUESTCODE_CREATE_NEWFOLDER = 2;

	/**
	 * transMultiControlViewGroup:传输管理的多选控制bar组
	 */
	private LinearLayout transMultiControlViewGroup;

	private LinearLayout cloudboxMultiControlViewGroup;
	/**
	 * mainControlViewGroup:当前activity的分页标签bar组
	 */
	private LinearLayout mainControlViewGroup;

	// activity 标签

	private final static String ACTIVITY_FLAG_CLOUDBOX = "CloudBox";

	private final static String ACTIVITY_FLAG_SHARE = "share";

	private final static String ACTIVITY_FLAG_TRANSPORT = "Transport";

	private final static String ACTIVITY_FLAG_SETTING = "Setting";

	private UpdateUtil mUpdateUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		CloudBoxApplication.addActivity(this);
		setContentView(R.layout.activity_main);
		mContext = MainActivity.this;
		mUpdateUtil = new UpdateUtil(mContext);
		manager = new LocalActivityManager(this, true);

		EventBus.getDefault().register(this);

		sharedPreferences = getSharedPreferences(IKeyManager.Token.SP_TOKEN, Context.MODE_PRIVATE);

		username = sharedPreferences.getString(IKeyManager.Token.KEY_USERNAME, "");
		access_id = sharedPreferences.getString(IKeyManager.Token.KEY_ACCESS_ID, "");
		access_token = sharedPreferences.getString(IKeyManager.Token.KEY_ACCESS_TOKEN, "");

		DisplayMetrics dm = getResources().getDisplayMetrics();
		width = dm.widthPixels * 13 / 60;

		mViewPager = (ViewPager) findViewById(R.id.main_viewpage);
		bindBottomBar();

		mSuperBtn = (CheckBox) findViewById(R.id.main_superbtn);
		mSuperBtn.setOnCheckedChangeListener(superBtnListener);
		transMultiControlViewGroup = (LinearLayout) findViewById(R.id.main_ll_transControl);
		cloudboxMultiControlViewGroup = (LinearLayout) findViewById(R.id.main_ll_cloudboxControl);
		mainControlViewGroup = (LinearLayout) findViewById(R.id.main_ll_bottombar);
		mProgressBar = (ProgressBar) findViewById(R.id.main_progress);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				mUpdateUtil.checkUpdate(false);
			}
		}, 1500);
	}

	@Override
	protected void onStart() {
		Log.e("lmsg", "main on start");
		DLog.d(getClass(), "main onStart");
		Intent[] intents = new Intent[2];
		intents[0] = new Intent(MainActivity.this, BackgroundActivity.class);
		intents[1] = new Intent(MainActivity.this, MainActivity.class);
		startActivities(intents);

		upLoadDataBaseUtils = UpLoadDataBaseUtils.getInstance(this);
		downLoadDataBaseUtils = DownLoadDataBaseUtils.getInstance(this);
		if (upServiceBinder == null)
			bindUploadService();
		if (downServiceBinder == null)
			bindDownloadService();

		manager.dispatchResume();
		initPagerViewer();

		super.onStart();
	}

	/**
	 * @Title: getDownloadService
	 * @Description: 获取下载service
	 * @author: leobert.lan
	 * @return
	 */
	public DownloadService getDownloadService() {
		if (downServiceBinder == null)
			bindDownloadService();
		DLog.d(getClass(), new LogLocation(), "baseacti.. null:" + (null == downServiceBinder));
		return downServiceBinder;
	}

	private void bindDownloadService() {
		Intent bindDownloadIntent = new Intent(MainActivity.this, DownloadService.class);
		bindService(bindDownloadIntent, mDownLoadConnection, Context.BIND_AUTO_CREATE);
	}

	/**
	 * @Title: getUploadService
	 * @Description: 获取上传service
	 * @author: leobert.lan
	 * @return
	 */
	public UpLoadService getUploadService() {
		return upServiceBinder;
	}

	private void bindUploadService() {
		DLog.d(getClass(), new LogLocation(), "bind");
		Intent bindUploadIntent = new Intent(MainActivity.this, UpLoadService.class);
		bindService(bindUploadIntent, mUploadConnection, Context.BIND_AUTO_CREATE);
	}

	private ServiceConnection mUploadConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			upServiceBinder = ((UpLoadService.MyBinder) service).getService();
			upServiceBinder.setDataBaseUtils(upLoadDataBaseUtils);
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			upServiceBinder = null;
		}
	};

	private ServiceConnection mDownLoadConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			downServiceBinder = ((DownloadService.DownLoadServiceBinder) service).getService();
			downServiceBinder.setDataBaseUtils(downLoadDataBaseUtils);
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			downServiceBinder = null;
		}
	};

	private boolean FLAG_ACTIVITYPAUSE = true;

	private boolean isActivityPause() {
		return FLAG_ACTIVITYPAUSE;
	}

	@Override
	protected void onPause() {
		FLAG_ACTIVITYPAUSE = true;
		Log.e("lmsg", "main on pause");
		manager.dispatchPause(isFinishing());
		super.onPause();
	}

	@Override
	protected void onResume() {
		FLAG_ACTIVITYPAUSE = false;
		Log.e("lmsg", "main on resume");
		if (popupWindow != null)
			popupWindow.dismiss();
		manager.dispatchResume();
		if (mSuperBtn.isChecked())
			mSuperBtn.setChecked(false);
		checkLoginStatus();

		super.onResume();
	}

	private void shutdown() {
		if (upServiceBinder != null) {
			Intent bindIntent = new Intent(MainActivity.this, UpLoadService.class);
			unbindService(mUploadConnection);
			stopService(bindIntent);
			upServiceBinder = null;
		}
		if (downServiceBinder != null) {
			Intent bindIntent = new Intent(MainActivity.this, DownloadService.class);
			unbindService(mDownLoadConnection);
			stopService(bindIntent);
			downServiceBinder = null;
		}
	}

	private CustomDialog guideToSetting;

	private CustomDialog warmOn4gTrans;

	@Subscribe
	public void onEventMainThread(RefuseOn4G event) {
		if (RefuseOn4G.postCount > 1)
			return;
		guideToSetting = new CustomDialog(this, new MainActivityIPreventPenetrate());
		guideToSetting.setContent(R.string.trans_guide_to_changesetting_content);
		guideToSetting.setNegativeButton(R.string.trans_guide_to_changesetting_negative);
		guideToSetting.setPositiveButton(R.string.trans_guide_to_changesetting_positive);
		guideToSetting.setPositiveClickListener(new OnPositiveClickListener() {

			@Override
			public void onPositiveClick() {
				((MainActivity) mContext).flingToPageSmooth(3);
				guideToSetting.dismiss();
			}
		});
		// TODO
		if (!isActivityPause())
			guideToSetting.show();
	}

	@Subscribe
	public void onEventMainThread(ShareReportEvent event) {
		// Log.e("amsg", "report event:\r\n"+JSONObject.toJSONString(event));
		Intent i = new Intent(MainActivity.this, ShareReportActivity.class);
		i.putExtra("data", JSON.toJSONString(event.getBean()));
		startActivity(i);
	}

	// @Subscribe
	// public void onEventMainThread(WarmOn4G event) {
	// }

	/**
	 * 显然，写在onStop中更合适的样子，但是写在onStop中，你想要的东西是null的，防止内存泄露的想法完全用
	 * 
	 * @see com.lht.pan_android.activity.BaseActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		manager.dispatchDestroy(true);
		shutdown();
		upLoadDataBaseUtils = null;
		downLoadDataBaseUtils = null;
		super.onDestroy();
	}

	private OnCheckedChangeListener superBtnListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

			if (isChecked) {
				SuperButtonPopUpWindow superButtonPopUpWindow = new SuperButtonPopUpWindow(MainActivity.this, null);
				superButtonPopUpWindow.show();
				superButtonPopUpWindow.setPictureClickListener(new OnPictureClickListener() {

					@Override
					public void onPictureClick() {
						mSuperBtn.setChecked(false);
						CloudBoxActivity activity = (CloudBoxActivity) manager.getActivity(ACTIVITY_FLAG_CLOUDBOX);
						String currentPath = activity.mUserDataDirectionUtil.getCurrentPath();
						Intent i = new Intent(MainActivity.this, SelectImageGroupActivity.class);
						i.putExtra(IKeyManager.UserFolderList.PARAM_PATH, currentPath);
						startActivity(i);
						reportCountEvent(COUNT_CB_ADD_PHOTOS);
					}
				});
				superButtonPopUpWindow.setVedioClickListener(new OnVedioClickListener() {

					@Override
					public void onVedioClick() {
						mSuperBtn.setChecked(false);
						CloudBoxActivity activity = (CloudBoxActivity) manager.getActivity(ACTIVITY_FLAG_CLOUDBOX);
						String currentPath = activity.mUserDataDirectionUtil.getCurrentPath();
						Intent i = new Intent(MainActivity.this, SelectMediaGroupActivity.class);
						i.putExtra(IKeyManager.UserFolderList.PARAM_PATH, currentPath);
						startActivity(i);
						reportCountEvent(COUNT_CB_ADD_VIDEOS);
					}
				});
				superButtonPopUpWindow.setFloderClickListener(new OnFolderClickListener() {

					@Override
					public void onClickFolder() {
						reportCountEvent(COUNT_CB_ADD_FOLDER);
						mSuperBtn.setChecked(false);
						CloudBoxActivity activity = (CloudBoxActivity) manager.getActivity(ACTIVITY_FLAG_CLOUDBOX);
						String currentPath = activity.mUserDataDirectionUtil.getCurrentPath();
						Intent i = new Intent(MainActivity.this, NewFolderActivity.class);
						i.putExtra(IKeyManager.UserFolderList.PARAM_PATH, currentPath);
						startActivityForResult(i, REQUESTCODE_CREATE_NEWFOLDER);
					}
				});
				superButtonPopUpWindow.setOnDismissListener(new OnDismissListener() {

					@Override
					public void onDismiss() {
						mSuperBtn.setChecked(false);
						mProgressBar.setVisibility(View.GONE);
					}
				});
				reportCountEvent(COUNT_CB_ADD_SHOW);
			}

		}
	};

	private int pageIndex = 0;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUESTCODE_CREATE_NEWFOLDER) {
			if (resultCode == RESULT_OK) {
				mViewPager.setCurrentItem(0, true);
			}
		}
	}

	private void initPagerViewer() {
		final ArrayList<View> list = new ArrayList<View>();
		Intent intent1 = new Intent(mContext, CloudBoxActivity.class);
		list.add(getView(ACTIVITY_FLAG_CLOUDBOX, intent1));
		Intent intent2 = new Intent(mContext, ShareActivity.class);
		list.add(getView(ACTIVITY_FLAG_SHARE, intent2));
		Intent intent3 = new Intent(mContext, TransportManagerActivity.class);
		list.add(getView(ACTIVITY_FLAG_TRANSPORT, intent3));
		Intent intent4 = new Intent(mContext, SettingActivity.class);
		list.add(getView(ACTIVITY_FLAG_SETTING, intent4));

		mViewPager.setAdapter(new MainViewPagerAdapter(list));
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				pageIndex = arg0;
				mTabBarUtil.onSelect(arg0);
				hideMultiView();
				switch (arg0) {
				case 2:
					if (manager.getCurrentId() == ACTIVITY_FLAG_TRANSPORT)
						return;
					((TransportManagerActivity) manager.getActivity(ACTIVITY_FLAG_TRANSPORT)).onResume();
					break;
				case 3:
					if (manager.getCurrentId() == ACTIVITY_FLAG_SETTING)
						return;
					((SettingActivity) manager.getActivity(ACTIVITY_FLAG_SETTING)).onResume();
					break;
				default:
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				return;
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				return;
			}
		});
		mTabItemClickListener.onTabItemClick(pageIndex);
	}

	private View getView(String id, Intent intent) {
		return manager.startActivity(id, intent).getDecorView();
	}

	private void bindBottomBar() {

		tabCloudBox = (TabView) findViewById(R.id.main_bottombar_cloudbox);
		tabProject = (TabView) findViewById(R.id.main_bottombar_project);
		tabTransport = (TabView) findViewById(R.id.main_bottombar_transport);
		tabSetting = (TabView) findViewById(R.id.main_bottombar_setting);
		tabCloudBox.setWidth(width);
		tabProject.setWidth(width);
		tabTransport.setWidth(width);
		tabSetting.setWidth(width);
		tabCloudBox.setIconSize(width * 2 / 5);
		tabProject.setIconSize(width * 2 / 5);
		tabTransport.setIconSize(width * 2 / 5);
		tabSetting.setIconSize(width * 2 / 5);

		ArrayList<TabItem> list = new ArrayList<TabItem>();
		list.add(new TabItem(tabCloudBox, R.drawable.yunpan, R.drawable.yunpan2, R.string.main_label_cloudbox,
				R.color.txt_blue_main, R.color.gray_777));
		list.add(new TabItem(tabProject, R.drawable.fenxzhucd, R.drawable.fenxzhucd2, R.string.main_label_project,
				R.color.txt_blue_main, R.color.gray_777));
		list.add(new TabItem(tabTransport, R.drawable.chaunsgl, R.drawable.chaunsgl2, R.string.main_label_transport,
				R.color.txt_blue_main, R.color.gray_777));
		list.add(new TabItem(tabSetting, R.drawable.wod, R.drawable.wod2, R.string.main_label_setting,
				R.color.txt_blue_main, R.color.gray_777));

		mTabBarUtil = new BottomBarItemUtil(list, mTabItemClickListener);
		mTabBarUtil.create();
	}

	private OnTabItemClickListener mTabItemClickListener = new OnTabItemClickListener() {

		@Override
		public void onTabItemClick(int index) {
			mViewPager.setCurrentItem(index, true);
			mTabBarUtil.onSelect(index);
		}
	};

	/** 注意，重写onKeydown无效，没有焦点 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {

		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			DLog.d(getClass(), "mainactivity onback");
			if (pw != null) {
				DLog.d(getClass(), "需要取消弹出窗");
				// 对话框类的执行一次否定选择
				if (pw instanceof CustomDialog) {
					CustomDialog dialog = (CustomDialog) pw;
					if (dialog.getNegativeClickListener() != null)
						dialog.getNegativeClickListener().onNegativeClick();
				}
				if (pw != null)
					pw.dismiss();
				return true;
			}
			if (this.isCloudBox()) {
				DLog.d(getClass(), new LogLocation(), "当前为0");
				CloudBoxActivity activity = (CloudBoxActivity) manager.getActivity(ACTIVITY_FLAG_CLOUDBOX);
				if (activity.back())
					return true;
			} else if (this.isTransport()) {
				TransportManagerActivity activity = (TransportManagerActivity) manager
						.getActivity(ACTIVITY_FLAG_TRANSPORT);
				if (activity.back())
					return true;
			} else if (this.isShare()) {
				ShareActivity activity = (ShareActivity) manager.getActivity(ACTIVITY_FLAG_SHARE);
				if (activity.back())
					return true;
			}
			pressAgainExit();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	/**
	 * @Title: isCloudBox
	 * @Description: 是否处于cloudbox
	 * @author: leobert.lan
	 * @return
	 */
	private boolean isCloudBox() {
		return manager.getCurrentId().equals(ACTIVITY_FLAG_CLOUDBOX) || mViewPager.getCurrentItem() == 0;
	}

	/**
	 * @Title: isTransport
	 * @Description: 是否处于传输列表
	 * @author: leobert.lan
	 * @return
	 */
	private boolean isTransport() {
		return manager.getCurrentId().equals(ACTIVITY_FLAG_TRANSPORT) || mViewPager.getCurrentItem() == 2;
	}

	/**
	 * @Title: isShare
	 * @Description: 是否处于查看分享
	 * @author: leobert.lan
	 * @return
	 */
	private boolean isShare() {
		return manager.getCurrentId().equals(ACTIVITY_FLAG_SHARE) || mViewPager.getCurrentItem() == 1;
	}

	/**
	 * @Title: pressAgainExit
	 * @Description: 再次点击返回退出
	 * @author: leobert.lan
	 */
	private void pressAgainExit() {
		if (exit.isExit()) {
			CloudBoxApplication.finishAll();
			finish();
			// TODO
			// WarmOn4G.postCount = 0;
			RefuseOn4G.postCount = 0;
		} else {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.press_again_toexit),
					Toast.LENGTH_SHORT).show();
			exit.doExitInTwoSecond();
		}
	}

	/**
	 * @Title: checkLoginStatus
	 * @Description: 检验token是否还有效
	 * @author: leobert.lan
	 */
	private void checkLoginStatus() {

		HttpUtil mHttpUtil = new HttpUtil();

		String urlString = IUrlManager.CheckToken.DOMAIN + IUrlManager.CheckToken.FUNCTION
				+ IUrlManager.CheckToken.USERNAME + username + IUrlManager.CheckToken.ACCESS_ID + access_id
				+ IUrlManager.CheckToken.ACCESS_TOKEN + access_token;
		mHttpUtil.get(urlString, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				HttpRequestFailureUtil failureUtil = new HttpRequestFailureUtil(mContext);
				failureUtil.handleFailureWithCode(arg0, false);
				reportCountEvent(COUNT_CB_TOKEN_FAIL);
			}
		});
	}

	/**
	 * @Title: flingToPageSmooth
	 * @Description: 切换页
	 * @author: leobert.lan
	 * @param page
	 */
	public void flingToPageSmooth(int page) {
		mViewPager.setCurrentItem(page, true);
	}

	/**
	 * @Title: showTransMultiSelectView
	 * @Description: 显示transport的多选视图,并且隐藏自身的control-view
	 * @author: leobert.lan
	 */
	public void showTransMultiSelectView() {
		hideControlBarOfMain();
		transMultiControlViewGroup.setVisibility(View.VISIBLE);
	}

	/**
	 * @Title: hideControlBarOfMain
	 * @Description: 隐藏自身的控制bar
	 * @author: leobert.lan
	 */
	private void hideControlBarOfMain() {
		mSuperBtn.setVisibility(View.INVISIBLE);
		mainControlViewGroup.setVisibility(View.INVISIBLE);
	}

	/**
	 * @Title: showControlBarOfMain
	 * @Description: 显示吱声的控制bar，注意图层叠加问题
	 * @author: leobert.lan
	 */
	private void showControlBarOfMain() {
		mSuperBtn.setVisibility(View.VISIBLE);
		mainControlViewGroup.setVisibility(View.VISIBLE);
		mSuperBtn.bringToFront();
	}

	/**
	 * @Title: hideTransMultiSelectView
	 * @Description: 隐藏
	 * @author: leobert.lan
	 */
	public void hideTransMultiSelectView() {
		transMultiControlViewGroup.setVisibility(View.GONE);
		showControlBarOfMain();
	}

	/**
	 * @Title: hideCloudboxMultiSelectView
	 * @Description: 隐藏云盘的多选视图
	 * @author: leobert.lan
	 */
	public void hideCloudboxMultiSelectView() {
		cloudboxMultiControlViewGroup.setVisibility(View.GONE);
		showControlBarOfMain();
	}

	/**
	 * @Title: showCloudboxMultiSelectView
	 * @Description: 显示云盘的多选视图
	 * @author: leobert.lan.
	 */
	public void showCloudboxMultiSelectView() {
		cloudboxMultiControlViewGroup.setVisibility(View.VISIBLE);
		hideControlBarOfMain();
	}

	/**
	 * @Title: hideMultiView
	 * @Description: 滑动时调用（注意，bottombar是没有机会用到它的，只有滑动切页有这个必要）
	 * @author: leobert.lan
	 */
	private void hideMultiView() {
		hideCloudboxMultiSelectView();
		hideTransMultiSelectView();
		((CloudBoxActivity) manager.getActivity(ACTIVITY_FLAG_CLOUDBOX)).closeMultiView();
		((TransportManagerActivity) manager.getActivity(ACTIVITY_FLAG_TRANSPORT)).changeMulti2Normal();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Log.e("lmsg", "main onStop");
		super.onStop();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		Log.e("lmsg", "main on restart");
		super.onRestart();
	}

	@Override
	protected ProgressBar getProgressBar() {
		return mProgressBar;
	}

	@Override
	protected String getPageName() {
		return MainActivity.mPageName;
	}

	@Override
	protected UMengActivity getActivity() {
		return MainActivity.this;
	}
}
