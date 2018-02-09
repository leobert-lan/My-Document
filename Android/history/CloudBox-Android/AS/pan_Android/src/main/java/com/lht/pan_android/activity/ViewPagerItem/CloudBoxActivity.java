package com.lht.pan_android.activity.ViewPagerItem;

import java.io.File;
import java.util.ArrayList;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.activity.BaseActivity;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.activity.asyncProtected.MainActivity;
import com.lht.pan_android.activity.asyncProtected.SearchActivity;
import com.lht.pan_android.adapter.UserListAdapter;
import com.lht.pan_android.bean.DirItemBean;
import com.lht.pan_android.service.DownloadService;
import com.lht.pan_android.util.CloudBoxApplication;
import com.lht.pan_android.util.activity.CloudBoxUtil;
import com.lht.pan_android.util.activity.CloudBoxUtil.OnItemSelectedChangedListener;
import com.lht.pan_android.util.activity.CloudBoxUtil.Operate;
import com.lht.pan_android.util.dir.ABSDirectionUtil.TypeFilter;
import com.lht.pan_android.util.dir.UserDataDirectionUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

@SuppressLint("InflateParams")
public class CloudBoxActivity extends ViewPagerItemActivity implements OnClickListener {
	private final static String mPageName = "cloudboxActivity";

	private Context mContext;

	private ArrayList<DirItemBean> mData;

	public UserListAdapter mAdapter;

	public UserDataDirectionUtil mUserDataDirectionUtil;

	public CloudBoxUtil mCloudBoxUtil;

	/**
	 * searchBar:搜索框
	 */
	private RelativeLayout searchBar;
	/**
	 * mTypeFilterGroup:过滤器组
	 */
	private LinearLayout mTypeFilterGroup;
	/**
	 * mParentBtBack:返回上一级按钮
	 */
	private LinearLayout mParentBtBack;
	/**
	 * mParentFolder:上一级目录显示
	 */
	private TextView mParentFolder;

	/**
	 * mCurrentLayout:title主区
	 */
	private LinearLayout mCurrentLayout;
	/**
	 * mCurrentToggleFilter:过滤器呼出键
	 */
	private ToggleButton mCurrentToggleFilter;
	/**
	 * txtCurrentTitleFolder:当前目录名
	 */
	private TextView txtCurrentTitleFolder;

	/**
	 * filterPullAll:过滤器：全部
	 */
	private LinearLayout filterPullAll;
	/**
	 * filterPullPicture:过滤器：图片
	 */
	private LinearLayout filterPullPicture;
	/**
	 * filterPullDocu:过滤器：文本
	 */
	private LinearLayout filterPullDocu;
	/**
	 * filterPullVideo:过滤器：视频
	 */
	private LinearLayout filterPullVideo;
	/**
	 * filterPullMusic:过滤器：音乐
	 */
	private LinearLayout filterPullMusic;

	private PullToRefreshListView mPullRefreshListView;

	private View viewNoFile;

	private CheckBox multiSelect, toogleAll;
	// 多选控制
	private LinearLayout btnMultiDelete, btnMultiMove, btnMultiShare, btnMultiDownload;

	private MainActivity mParent;

	// private String currentPath = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		CloudBoxApplication.addActivity(this);
		setContentView(R.layout.activity_cloudbox);

		reportCountEvent(COUNT_CB_BOX);
		mContext = this;
		mParent = (MainActivity) getParent();

		mCloudBoxUtil = new CloudBoxUtil(this);

		mUserDataDirectionUtil = new UserDataDirectionUtil(mContext, mParent);

		mUserDataDirectionUtil.setGetListImpl(mCloudBoxUtil.getOperateCallBack());

		initWidget();
		initEvent();

		mData = new ArrayList<DirItemBean>();

		mAdapter = new UserListAdapter(mData, mContext);

		mAdapter.setOnItemSelectedChangedListener(new OnItemSelectedChangedListener() {

			@Override
			public void OnItemSelectedChanged(DirItemBean item, boolean isSelected) {
				if (isSelected)
					mCloudBoxUtil.addSelectedItem(item);
				else
					mCloudBoxUtil.removeSelectedItem(item);
			}
		});

		mAdapter.setSubMenuClickListener(mCloudBoxUtil.getSubMenuClickListener());

		mPullRefreshListView.setAdapter(mAdapter);

		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				updateLastFreshTime(refreshView);
				mCloudBoxUtil.performOperate(Operate.pull_to_refresh, null);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				updateLastFreshTime(refreshView);
				mCloudBoxUtil.performOperate(Operate.pull_to_add, null);
			}
		});

		mPullRefreshListView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);

		mCurrentToggleFilter.setOnCheckedChangeListener(mCloudBoxUtil.getFilterToggleListener());

		mParentBtBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				back();
			}
		});
	}

	protected void updateLastFreshTime(PullToRefreshBase<ListView> refreshView) {
		String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
				DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
	}

	private void initEvent() {
		mTypeFilterGroup.setOnClickListener(this);
		mParentBtBack.setOnClickListener(this);
		mCurrentLayout.setOnClickListener(this);

		filterPullAll.setOnClickListener(this);
		filterPullPicture.setOnClickListener(this);
		filterPullDocu.setOnClickListener(this);
		filterPullMusic.setOnClickListener(this);
		filterPullVideo.setOnClickListener(this);

		multiSelect.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mAdapter.toogleMultView();
				if (isChecked) {
					// 修改所有初始状态未选中 v2.2.0
					mAdapter.initMultiSelectState();

					// 多选状态
					buttonView.setText(getResources().getString(R.string.string_cancel));
					showMultiSelectView();
					btnMultiShare.setBackgroundResource(R.color.gray);
					// 取消所有选中的数据,确保！
					mCloudBoxUtil.removeAllSelceted();
				} else {
					// 正常状态
					buttonView.setText(getResources().getString(R.string.string_multiselect));
					hideMultiSelectView();
					// 取消所有选中的数据
					mCloudBoxUtil.removeAllSelceted();
				}
			}
		});
		toogleAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO String change name
				if (isChecked) {
					// 处理全选
					buttonView.setText(getResources().getString(R.string.string_not_choice));
					mAdapter.selectAll();
				} else {
					// 处理全不选
					buttonView.setText(getResources().getString(R.string.string_choice_all));
					mAdapter.deSelectAll();
				}
			}
		});

		btnMultiDelete.setOnClickListener(controlBarItemOnClickListener);
		btnMultiShare.setOnClickListener(controlBarItemOnClickListener);
		btnMultiMove.setOnClickListener(controlBarItemOnClickListener);
		btnMultiDownload.setOnClickListener(controlBarItemOnClickListener);

	}

	/**
	 * @Title: initMultiSelectModeWhileFresh
	 * @Description: 多选状态刷新，恢复多选的初始状态
	 * @author: leobert.lan
	 */
	public void initMultiSelectModeWhileFresh() {
		toogleAll.setChecked(false);
	}

	/**
	 * @Title: hideMultiSelectView
	 * @Description: 隐藏多选状态
	 * @author: leobert.lan
	 */
	protected void hideMultiSelectView() {
		toogleAll.setVisibility(View.GONE);
		// 隐藏control bar
		((MainActivity) getParent()).hideCloudboxMultiSelectView();
		// 显示导航
		showNormalGuide();
	}

	/**
	 * @Title: showNormalGuide
	 * @Description: 显示导航，注意根目录视图和非根目录视图的区别
	 * @author: leobert.lan
	 */
	private void showNormalGuide() {
		if (this.mUserDataDirectionUtil.isRoot()) {
			// 显示filter
			getmToggleFilter().setVisibility(View.VISIBLE);
			// 设置返回键不可见
			this.mParentBtBack.setVisibility(View.INVISIBLE);
			// 激活filter展开功能
			activateFilterOpener();
		} else {
			// 非根目录，显示返回键，不显示filter,同时filter不能展开
			getmToggleFilter().setVisibility(View.INVISIBLE);
			this.mParentBtBack.setVisibility(View.VISIBLE);
			deactivateFilterOpener();
		}

	}

	/**
	 * @Title: showMultiSelectView
	 * @Description: 显示多选状态
	 * @author: leobert.lan
	 */
	protected void showMultiSelectView() {
		toogleAll.setVisibility(View.VISIBLE);
		toogleAll.setChecked(false);
		// 显示control bar
		((MainActivity) getParent()).showCloudboxMultiSelectView();
		// 隐藏导航
		hideNormalGuide();
	}

	/**
	 * @Title: hideNormalGuide
	 * @Description: 隐藏导航栏，
	 * @author: leobert.lan
	 */
	private void hideNormalGuide() {
		// 隐藏filter
		getmToggleFilter().setChecked(false);
		getmToggleFilter().setVisibility(View.INVISIBLE);
		mParentBtBack.setVisibility(View.INVISIBLE);
		deactivateFilterOpener();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cloudbox_parent_back:
			back();
			break;
		case R.id.cloudbox_current_title:
			filterPull();
			reportCountEvent(CALC_CB_TYPE);
			break;
		case R.id.linear_pull_all:
			clearImageBackGround();
			updateTitle(R.string.cloudbox_activity_title);
			filterPullAll.setBackgroundResource(R.color.pitch_bg);
			mTypeFilterGroup.setVisibility(View.GONE);
			mCurrentToggleFilter.setChecked(false);
			mCloudBoxUtil.setOperate(Operate.pull_to_refresh);
			mUserDataDirectionUtil.getListData(IKeyManager.UserFolderList.TYPE_ALL, 1, 50, TypeFilter.all);
			break;
		case R.id.linear_pull_docu:
			clearImageBackGround();
			updateTitle(R.string.pull_docu);
			filterPullDocu.setBackgroundResource(R.color.pitch_bg);
			mTypeFilterGroup.setVisibility(View.GONE);
			mCurrentToggleFilter.setChecked(false);
			mCloudBoxUtil.setOperate(Operate.pull_to_refresh);
			mUserDataDirectionUtil.getListData(IKeyManager.UserFolderList.TYPE_ALL, 1, 50, TypeFilter.doc);
			break;
		case R.id.linear_pull_music:
			clearImageBackGround();
			updateTitle(R.string.pull_music);
			filterPullMusic.setBackgroundResource(R.color.pitch_bg);
			mTypeFilterGroup.setVisibility(View.GONE);
			mCurrentToggleFilter.setChecked(false);
			mCloudBoxUtil.setOperate(Operate.pull_to_refresh);
			mUserDataDirectionUtil.getListData(IKeyManager.UserFolderList.TYPE_ALL, 1, 50, TypeFilter.audio);
			break;
		case R.id.linear_pull_picture:
			clearImageBackGround();
			updateTitle(R.string.pull_picture);
			filterPullPicture.setBackgroundResource(R.color.pitch_bg);
			mTypeFilterGroup.setVisibility(View.GONE);
			mCurrentToggleFilter.setChecked(false);
			mCloudBoxUtil.setOperate(Operate.pull_to_refresh);
			mUserDataDirectionUtil.getListData(IKeyManager.UserFolderList.TYPE_ALL, 1, 50, TypeFilter.image);
			break;
		case R.id.linear_pull_video:
			clearImageBackGround();
			updateTitle(R.string.pull_viedo);
			filterPullVideo.setBackgroundResource(R.color.pitch_bg);
			mTypeFilterGroup.setVisibility(View.GONE);
			mCurrentToggleFilter.setChecked(false);
			mCloudBoxUtil.setOperate(Operate.pull_to_refresh);
			mUserDataDirectionUtil.getListData(IKeyManager.UserFolderList.TYPE_ALL, 1, 50, TypeFilter.video);
			break;
		default:
			break;
		}
	}

	/**
	 * @Title: filterPull
	 * @Description: 展开filter
	 * @author: leobert.lan
	 * @return
	 */
	private boolean filterPull() {
		if (mUserDataDirectionUtil.isRoot()) {
			if (!mCurrentToggleFilter.isChecked()) {
				mTypeFilterGroup.setVisibility(View.VISIBLE);
				mTypeFilterGroup.bringToFront();
				mCurrentToggleFilter.setChecked(true);
			} else {
				mTypeFilterGroup.setVisibility(View.GONE);
				mCurrentToggleFilter.setChecked(false);
			}
			return false;
		} else {
			deactivateFilterOpener();
			return true;
		}
	}

	/**
	 * @Title: activateFilterOpener
	 * @Description: 激活filter的展开功能，注意，不会调用展开filter视图
	 * @author: leobert.lan
	 */
	public void activateFilterOpener() {
		txtCurrentTitleFolder.setClickable(true);
	}

	/**
	 * @Title: deactivateFilterOpener
	 * @Description: 失活filter的展开功能，注意不会调用关闭filter视图
	 * @author: leobert.lan
	 */
	public void deactivateFilterOpener() {
		txtCurrentTitleFolder.setClickable(false);
	}

	private void clearImageBackGround() {
		filterPullAll.setBackground(null);
		filterPullDocu.setBackground(null);
		filterPullMusic.setBackground(null);
		filterPullPicture.setBackground(null);
		filterPullVideo.setBackground(null);
		// filterPullUpload.setBackground(null);
		// filterPullOthers.setBackground(null);
	}

	/**
	 * @Title: initWidget
	 * @Description: 初始化
	 * @author: zhangbin
	 */
	private void initWidget() {
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.cloudbox_lv_userlist);
		mPullRefreshListView.setMode(Mode.BOTH);
		viewNoFile = findViewById(R.id.cloudbox_lv_nodata);
		View v = LayoutInflater.from(this).inflate(R.layout.header_search_bar, null);
		searchBar = (RelativeLayout) v.findViewById(R.id.searchbar);

		searchBar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(CloudBoxActivity.this, SearchActivity.class);
				startActivity(intent);
			}
		});

		multiSelect = (CheckBox) findViewById(R.id.cloudbox_cb_multy);
		toogleAll = (CheckBox) findViewById(R.id.cloudbox_toogle_all);

		ListView actureView = mPullRefreshListView.getRefreshableView();
		actureView.addHeaderView(v);

		mCloudBoxUtil.performOperate(Operate.pull_to_refresh, null);

		mCurrentLayout = (LinearLayout) findViewById(R.id.cloudbox_current_title);
		mCurrentToggleFilter = (ToggleButton) findViewById(R.id.cloudbox_current_toggel_filter);
		txtCurrentTitleFolder = (TextView) findViewById(R.id.cloudbox_current_txt);

		mParentBtBack = (LinearLayout) findViewById(R.id.cloudbox_parent_back);
		mParentFolder = (TextView) findViewById(R.id.linear_parent_txt);

		mTypeFilterGroup = (LinearLayout) findViewById(R.id.cloudbox_ll_typefilter);
		filterPullAll = (LinearLayout) findViewById(R.id.linear_pull_all);
		filterPullPicture = (LinearLayout) findViewById(R.id.linear_pull_picture);
		filterPullDocu = (LinearLayout) findViewById(R.id.linear_pull_docu);
		filterPullMusic = (LinearLayout) findViewById(R.id.linear_pull_music);
		filterPullVideo = (LinearLayout) findViewById(R.id.linear_pull_video);

		mCurrentToggleFilter.setEnabled(false);

		// 多选控制，注意实例化
		btnMultiDelete = (LinearLayout) mParent.findViewById(R.id.cloudbox_control_delete);
		btnMultiShare = (LinearLayout) mParent.findViewById(R.id.cloudbox_control_share);
		btnMultiMove = (LinearLayout) mParent.findViewById(R.id.cloudbox_control_move);
		btnMultiDownload = (LinearLayout) mParent.findViewById(R.id.cloudbox_control_download);
	}

	@Override
	public boolean back() {
		// 多选视图拒绝返回上一级，执行视图切换
		if (multiSelect.isChecked()) {
			multiSelect.setChecked(false);
			return true;
		}
		if (mUserDataDirectionUtil.isRoot()) {
			return false;
		} else {
			mCloudBoxUtil.performOperate(Operate.click_to_goback, null);
			String s = mUserDataDirectionUtil.getPrePath();
			String[] folderNames = mUserDataDirectionUtil.getCurrentPath().split("/");

			if (folderNames.length > 2) {
				mParentBtBack.setVisibility(View.VISIBLE);
				mCurrentToggleFilter.setVisibility(View.GONE);
				mParentFolder.setText(s.substring(s.lastIndexOf("/") + 1));
				txtCurrentTitleFolder.setText(folderNames[folderNames.length - 1]);
			} else if (folderNames.length > 1) {
				mParentFolder.setText(R.string.cloudbox_activity_title);
				txtCurrentTitleFolder.setText(folderNames[folderNames.length - 1]);
			} else {
				mParentBtBack.setVisibility(View.GONE);
				mCurrentToggleFilter.setVisibility(View.VISIBLE);
				txtCurrentTitleFolder.setText(R.string.cloudbox_activity_title);
			}
			return true;
		}
	}

	public DownloadService getDownloadService() {
		return ((MainActivity) getParent()).getDownloadService();
	}

	public File getIndividualFolder() {
		return BaseActivity.getIndividualFolder();
	}

	/**
	 * @Title: Resume
	 * @Description: 手动管理生命周期-resume
	 * @author: leobert.lan
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Log.e("lmsg", "cloudbox on resume");
		mCloudBoxUtil.performOperate(Operate.pull_to_refresh, null);
	}

	/**
	 * @Title: Pause
	 * @Description: 手动管理生命周期-pause
	 * @author: leobert.lan
	 */
	@Override
	protected void onPause() {
		Log.e("lmsg", "cloudbox on pause");
		super.onPause();
		// MobclickAgent.onPageEnd(mPageName);
		// MobclickAgent.onPause(mContext);
	}

	public LinearLayout getmTypeFilterGroup() {
		return mTypeFilterGroup;
	}

	public PullToRefreshListView getPullRefreshListView() {
		return mPullRefreshListView;
	}

	public LinearLayout getBtBack() {
		return mParentBtBack;
	}

	public TextView getTxtParentFolder() {
		return mParentFolder;
	}

	public TextView getTxtTitleCurrentFolder() {
		return txtCurrentTitleFolder;
	}

	public ToggleButton getmToggleFilter() {
		return mCurrentToggleFilter;
	}

	private void updateTitle(int rid) {
		txtCurrentTitleFolder.setText(getResources().getString(rid));
	}

	public View getViewNoFile() {
		return viewNoFile;
	}

	/**
	 * @Title: getMultiSelect
	 * @Description: 获取多选视图开启开关
	 * @author: leobert.lan
	 * @return
	 */
	public CheckBox getMultiSelect() {
		return multiSelect;
	}

	public boolean isMultiSelectMode() {
		return multiSelect.isChecked();
	}

	/**
	 * @Title: closeMultiView
	 * @Description: 关闭多选视图
	 * @author: leobert.lan
	 */
	public void closeMultiView() {
		multiSelect.setChecked(false);
	}

	/**
	 * controlBarItemOnClickListener:多选control bar回调
	 */
	private OnClickListener controlBarItemOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cloudbox_control_delete:
				mCloudBoxUtil.multiDelete();
				break;
			case R.id.cloudbox_control_download:
				mCloudBoxUtil.multiDownload();
				break;
			case R.id.cloudbox_control_move:
				mCloudBoxUtil.multiMove();
				break;
			case R.id.cloudbox_control_share:
				mCloudBoxUtil.multiShare();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Destroy();
		super.onDestroy();
	}

	private void Destroy() {
		mUserDataDirectionUtil.destroy();
		mCloudBoxUtil.destroy();
	}

	@Override
	protected String getPageName() {
		return CloudBoxActivity.mPageName;
	}

	@Override
	public UMengActivity getActivity() {
		return CloudBoxActivity.this;
	}

	public void prepareFailureView() {
		// TODO Auto-generated method stub
		ImageView img = (ImageView) viewNoFile.findViewById(R.id.listnofile_img);
		TextView txt = (TextView) viewNoFile.findViewById(R.id.listnofile_txt);
		img.setImageResource(R.drawable.baoqian);
		txt.setText(R.string.notify_nointernet);

	}

	public void prepareEmptyView() {
		ImageView img = (ImageView) viewNoFile.findViewById(R.id.listnofile_img);
		TextView txt = (TextView) viewNoFile.findViewById(R.id.listnofile_txt);
		img.setImageResource(R.drawable.meiywj);
		txt.setText(R.string.cloudbox_list_norecord);
	}

}
