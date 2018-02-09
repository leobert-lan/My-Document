package com.lht.pan_android.activity.ViewPagerItem;

import java.io.File;
import java.util.ArrayList;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lht.pan_android.R;
import com.lht.pan_android.activity.BaseActivity;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.activity.asyncProtected.MainActivity;
import com.lht.pan_android.adapter.ShareListAdapter;
import com.lht.pan_android.bean.ShareItemBean;
import com.lht.pan_android.clazz.Events.CallFreshShareEvent;
import com.lht.pan_android.clazz.Events.MultiUserQuery;
import com.lht.pan_android.service.DownloadService;
import com.lht.pan_android.util.CloudBoxApplication;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.DLog.LogLocation;
import com.lht.pan_android.util.activity.ShareUtil;
import com.lht.pan_android.util.activity.ShareUtil.Operate;
import com.lht.pan_android.util.dir.ShareDataDirectionUtil;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ShareActivity extends ViewPagerItemActivity implements OnClickListener {

	private final static String mPageName = "shareActivity";
	public int URLFLAG = 0;

	public final static int FLAG_SHARETOME = 101;

	public final static int FLAG_MYSHARE = 100;

	public ShareUtil mShareUtil;
	public ShareListAdapter mShareListAdapter;
	public ShareDataDirectionUtil mShareDataDirectionUtil;

	private TextView shareCurrentTitle;
	private TextView shareParentTitle;
	private LinearLayout share;
	private LinearLayout shareBack;
	private LinearLayout shareToMe;
	private LinearLayout shareMyShare;
	private LinearLayout shareRefreshList;
	private PullToRefreshListView sharePullToRefresh;

	private View viewNoShare;
	private Context mContext;
	private MainActivity mParent;
	private ArrayList<ShareItemBean> mData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CloudBoxApplication.addActivity(this);
		setContentView(R.layout.activity_share);
		EventBus.getDefault().register(this);

		reportCountEvent(CALC_CB_SHARE);
		mContext = this;

		mParent = (MainActivity) getParent();
		mShareUtil = new ShareUtil(this);
		mShareDataDirectionUtil = new ShareDataDirectionUtil(mContext);

		mShareDataDirectionUtil.setShareGetListImpl(mShareUtil.getOperateCallBack());

		initView();
		initEvent();

		mData = new ArrayList<ShareItemBean>();
		mShareListAdapter = new ShareListAdapter(mData, mContext);
		mShareListAdapter.setShareSubMenuClickListener(mShareUtil.getShareSubMenuClickListener());
		sharePullToRefresh.setAdapter(mShareListAdapter);
	}

	private void initEvent() {
		shareToMe.setOnClickListener(this);
		shareBack.setOnClickListener(this);
		shareMyShare.setOnClickListener(this);
	}

	private void initView() {
		viewNoShare = findViewById(R.id.share_list_nodata);
		share = (LinearLayout) findViewById(R.id.share_linear_share);
		shareBack = (LinearLayout) findViewById(R.id.share_parent_back);
		shareToMe = (LinearLayout) findViewById(R.id.share_linear_to_me);
		shareParentTitle = (TextView) findViewById(R.id.share_parent_txt);
		shareCurrentTitle = (TextView) findViewById(R.id.activity_share_title);
		shareMyShare = (LinearLayout) findViewById(R.id.share_linear_my_share);
		shareRefreshList = (LinearLayout) findViewById(R.id.share_linear_list_refresh);
		sharePullToRefresh = (PullToRefreshListView) findViewById(R.id.activity_share_list);
	}

	protected void updateLastFreshTime(PullToRefreshBase<ListView> refreshView) {
		String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
				DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.share_linear_my_share:
			reportCountEvent(CALC_CB_SHARE_SEND);
			share.setVisibility(View.GONE);
			shareBack.setVisibility(View.VISIBLE);
			shareRefreshList.setVisibility(View.VISIBLE);
			shareParentTitle.setVisibility(View.VISIBLE);
			shareCurrentTitle.setVisibility(View.VISIBLE);
			shareCurrentTitle.setText(R.string.string_my_share);
			shareParentTitle.setText(R.string.string_share);
			URLFLAG = FLAG_MYSHARE;
			mShareUtil.performOperate(Operate.pull_to_refresh, null);
			share();
			break;
		case R.id.share_linear_to_me:
			reportCountEvent(CALC_CB_SHARE_GOT);
			share.setVisibility(View.GONE);
			shareBack.setVisibility(View.VISIBLE);
			shareRefreshList.setVisibility(View.VISIBLE);
			shareParentTitle.setVisibility(View.VISIBLE);
			shareCurrentTitle.setVisibility(View.VISIBLE);
			shareCurrentTitle.setText(R.string.string_sharetome);
			shareParentTitle.setText(R.string.string_share);
			URLFLAG = FLAG_SHARETOME;
			mShareUtil.performOperate(Operate.pull_to_refresh, null);
			share();
			break;
		case R.id.share_parent_back:
			back();
			break;
		default:
			break;
		}
	}

	/**
	 * @Title: share
	 * @Description:
	 * @author: leobert.lan
	 */
	private void share() {
		// 本句代码是用于区分root状态下的两个状态（一级页面、二级页面）
		changeViewStateFlag(true);
		sharePullToRefresh.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				updateLastFreshTime(refreshView);
				mShareUtil.performOperate(Operate.pull_to_refresh, null);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				updateLastFreshTime(refreshView);
				mShareUtil.performOperate(Operate.pull_to_add, null);
			}
		});

		sharePullToRefresh.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
	}

	/**
	 * TODO 简单描述该方法的实现功能（可选）.
	 * 
	 * @see com.lht.pan_android.activity.ViewPagerItem.ViewPagerItemActivity#back()
	 */
	@Override
	public boolean back() {
		String[] folderNames = mShareDataDirectionUtil.getCurrentPath().split("/");
		// 取消之前的所有请求
		mShareDataDirectionUtil.cancelAllCalls();
		if (mShareDataDirectionUtil.isRoot()) {
			share.setVisibility(View.VISIBLE);
			shareBack.setVisibility(View.GONE);
			shareRefreshList.setVisibility(View.GONE);
			shareParentTitle.setVisibility(View.GONE);
			shareCurrentTitle.setText(R.string.string_share);
			viewNoShare.setVisibility(View.GONE);
			mShareListAdapter.Clear();
			DLog.d(getClass(), new LogLocation(), "is root");
			if (isViewState) {
				changeViewStateFlag(false);
				return true;
			}
			return false;
		} else {
			DLog.d(getClass(), new LogLocation(), "is not root");
			// 浏览文件夹子层级目录状态
			mShareUtil.performOperate(Operate.click_to_goback, null);
			if (folderNames.length > 3) {
				shareParentTitle.setText(folderNames[folderNames.length - 3]);
				shareCurrentTitle.setText(folderNames[folderNames.length - 2]);
			} else if (folderNames.length > 2) {
				shareParentTitle.setText(R.string.string_my_share);
				shareCurrentTitle.setText(folderNames[folderNames.length - 2]);
			} else if (folderNames.length == 2) {
				shareParentTitle.setText(R.string.string_share);
				if (URLFLAG == FLAG_MYSHARE)
					shareCurrentTitle.setText(R.string.string_my_share);
				else
					shareCurrentTitle.setText(R.string.string_sharetome);
			}
			return true;
		}
	}

	@Subscribe
	public void onEventMainThread(MultiUserQuery event) {
		mShareUtil.multiQueryUsersInfo(mParent, event.apiRequest, event.handler);
	}

	@Subscribe
	public void onEventMainThread(CallFreshShareEvent event) {
		mShareUtil.performOperate(Operate.pull_to_refresh, null);
	}

	public PullToRefreshListView getPullRefreshListView() {
		return sharePullToRefresh;
	}

	public DownloadService getDownloadService() {
		return ((MainActivity) getParent()).getDownloadService();
	}

	public void change2EmptyView() {
		viewNoShare.setVisibility(View.VISIBLE);
		sharePullToRefresh.setVisibility(View.GONE);
		viewNoShare.bringToFront();
	}

	public void change2NormalView() {
		viewNoShare.setVisibility(View.GONE);
		sharePullToRefresh.setVisibility(View.VISIBLE);
		sharePullToRefresh.bringToFront();
	}

	public LinearLayout getParentBack() {
		return shareBack;
	}

	public TextView getCurrentTitle() {
		return shareCurrentTitle;
	}

	public TextView getParentTitle() {
		return shareParentTitle;
	}

	@Override
	protected String getPageName() {
		return ShareActivity.mPageName;
	}

	@Override
	protected UMengActivity getActivity() {
		return ShareActivity.this;
	}

	public File getIndividualFolder() {
		return BaseActivity.getIndividualFolder();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		Destroy();
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	private void Destroy() {

	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	private boolean isViewState = false;

	public void changeViewStateFlag(boolean b) {
		isViewState = b;
	}
}
