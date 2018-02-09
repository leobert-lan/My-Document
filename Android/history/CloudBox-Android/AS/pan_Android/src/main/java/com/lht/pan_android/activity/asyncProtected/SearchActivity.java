package com.lht.pan_android.activity.asyncProtected;

import java.util.ArrayList;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lht.pan_android.R;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.adapter.SearchAdapter;
import com.lht.pan_android.adapter.SearchHistroyAdapter;
import com.lht.pan_android.bean.DirItemBean;
import com.lht.pan_android.util.CloudBoxApplication;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.DLog.LogLocation;
import com.lht.pan_android.util.ToastUtil;
import com.lht.pan_android.util.ToastUtil.Duration;
import com.lht.pan_android.util.activity.CloudBoxUtil.Operate;
import com.lht.pan_android.util.activity.SearchUtil;
import com.lht.pan_android.util.dir.SearchDataDirectionUtil;
import com.lht.pan_android.util.string.StringUtil;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SearchActivity extends AsyncProtectedActivity implements OnClickListener {

	private final static String mPageName = "searchActivity";

	/**
	 * searchInput:搜索输入框
	 */
	private EditText searchInput;
	private ImageView searchImg;
	private ImageView mParentBtBack;
	private TextView searchClose;
	private PullToRefreshListView mPullToRefreshListView;
	private Context mContext;
	public ProgressBar mProgressBar;

	private ArrayList<DirItemBean> mData;
	public SearchAdapter mSearchAdapter;
	public SearchDataDirectionUtil mSearchDataDirectionUtil;
	public SearchUtil mSearchUtil;

	private ListView searchHistroyList;
	private SearchHistroyAdapter histroyAdapter;

	/**
	 * emptyView:没有搜索结果的空视图
	 */
	private RelativeLayout emptyView, emptyView2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search);
		CloudBoxApplication.addActivity(this);

		mContext = this;
		mSearchUtil = new SearchUtil(this);
		mSearchDataDirectionUtil = new SearchDataDirectionUtil(mContext, this);

		reportCountEvent(COUNT_CB_SEARCH);

		initView();
		histroyAdapter = new SearchHistroyAdapter(this, new ArrayList<String>(), new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v instanceof TextView) {
					TextView tv = (TextView) v;
					searchInput.setText(tv.getText());
					searchImg.performClick();
				} else {
					DLog.d(SearchActivity.class, new LogLocation(), "error type,textView wanted");
				}
			}
		});
		searchHistroyList.setAdapter(histroyAdapter);
		initEvent();

		mSearchUtil.initHistroy(histroyAdapter);

	}

	private void initView() {
		searchInput = (EditText) findViewById(R.id.searchbar_edit_input);
		searchImg = (ImageView) findViewById(R.id.searchbar_img_search);
		searchClose = (TextView) findViewById(R.id.search_txt_close);
		mParentBtBack = (ImageView) findViewById(R.id.search_back);
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.search_listview);
		mProgressBar = (ProgressBar) findViewById(R.id.search_progress);
		mPullToRefreshListView.setMode(Mode.DISABLED);

		emptyView = (RelativeLayout) findViewById(R.id.search_ll_nothing);
		emptyView2 = (RelativeLayout) findViewById(R.id.search_ll_nothing2);
		searchHistroyList = (ListView) findViewById(R.id.search_histroy);
	}

	private void initEvent() {
		searchImg.setOnClickListener(this);
		searchClose.setOnClickListener(this);
		mParentBtBack.setOnClickListener(this);

		searchInput.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				change2NormalView();
			}
		});

		searchInput.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					searchImg.performClick();
				}
				return false;
			}
		});
	}

	/**
	 * @Title: refreshData
	 * @Description: 刷新请求数据
	 * @author: zhangbin
	 */
	public void searchData() {
		mSearchDataDirectionUtil.setGetListImpl(mSearchUtil.getOperateCallBack());
		mData = new ArrayList<DirItemBean>();
		mSearchAdapter = new SearchAdapter(mData, mContext);

		mSearchDataDirectionUtil.getSearchData();
		mSearchAdapter.setSubMenuClickListener(mSearchUtil.getSubMenuClickListener());
		mPullToRefreshListView.setAdapter(mSearchAdapter);
		this.changeCallbackToSearch();
	}

	public void refreshData() {
		mSearchDataDirectionUtil.setGetListImpl(mSearchUtil.getOperateCallBack());
		mData = new ArrayList<DirItemBean>();
		mSearchAdapter = new SearchAdapter(mData, mContext);

		mSearchDataDirectionUtil.getListData(1, 50);
		mSearchAdapter.setSubMenuClickListener(mSearchUtil.getSubMenuClickListener());
		mPullToRefreshListView.setAdapter(mSearchAdapter);
		this.changeCallbackToRefresh();
	}

	/**
	 * searchCallback:对应重新搜索的回调监听器
	 */
	private OnRefreshListener2<ListView> searchCallback = new OnRefreshListener2<ListView>() {
		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			DLog.d(getClass(), new LogLocation(), "onresearch");
			updateLastFreshTime(refreshView);
			searchImg.performClick();
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			updateLastFreshTime(refreshView);
			mSearchDataDirectionUtil.getSearchDataNextpage();
		}
	};
	/**
	 * refreshCallback:对应列表刷新的回调监听器
	 */
	private OnRefreshListener2<ListView> refreshCallback = new OnRefreshListener2<ListView>() {
		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			updateLastFreshTime(refreshView);
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus().getWindowToken(), 0);
			mSearchUtil.listPerformOperate(Operate.pull_to_refresh, null, null);
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			updateLastFreshTime(refreshView);
			// 加载列表数据的后一页
			mSearchUtil.listPerformOperate(Operate.pull_to_add, null, null);
		}
	};

	public void changeCallbackToSearch() {
		mPullToRefreshListView.setOnRefreshListener(searchCallback);
	}

	public void changeCallbackToRefresh() {
		mPullToRefreshListView.setOnRefreshListener(refreshCallback);
	}

	/**
	 * @Title: updateLastFreshTime
	 * @Description: 下拉刷新的时间调用
	 * @author: zhangbin
	 * @param refreshView
	 */
	protected void updateLastFreshTime(PullToRefreshBase<ListView> refreshView) {
		String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
				DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.search_back:
			back();
			break;
		case R.id.search_txt_close:
			finish();
			break;
		case R.id.searchbar_img_search:
			onSearchCall();
			reportCountEvent(CALC_CB_SEARCH_CLOUD);
			break;
		default:
			break;
		}
	}

	public void onSearchCall() {
		this.changeCallbackToSearch();
		// 隐藏历史记录
		searchHistroyList.setVisibility(View.GONE);

		String searchKey = searchInput.getText().toString();
		mSearchDataDirectionUtil.setSearchContent(searchKey);
		mSearchDataDirectionUtil.setSearchView();
		mSearchUtil.updateHistroy(searchKey);
		hideSoftInput();
		// InputMethodManager inputMethodManager = (InputMethodManager)
		// getSystemService(INPUT_METHOD_SERVICE);
		// inputMethodManager.toggleSoftInput(0,
		// InputMethodManager.HIDE_NOT_ALWAYS);
		if (StringUtil.isEmpty(searchKey)) {
			ToastUtil.show(this, R.string.search_input_null, Duration.s);
			return;
		}
		searchData();
		showWaitView(true);
	}

	public boolean back() {
		if (mSearchDataDirectionUtil.shouldFinish()) {
			finish();
			return true;
		} else {
			mSearchUtil.listPerformOperate(Operate.click_to_goback, null, null);
			return true;
		}
	}

	public PullToRefreshListView getPullRefreshListView() {
		return mPullToRefreshListView;
	}

	public PullToRefreshListView getPullToRefreshListView() {
		return mPullToRefreshListView;
	}

	@Override
	protected ProgressBar getProgressBar() {
		return mProgressBar;
	}

	@Override
	protected void onResume() {
		PullToRefreshListView lv = getPullToRefreshListView();
		if (lv.getOnRefreshListener2() != null)
			lv.getOnRefreshListener2().onPullDownToRefresh(lv);
		// MobclickAgent.onPageStart(mPageName);
		// MobclickAgent.onResume(this);
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// MobclickAgent.onPageEnd(mPageName);
		// MobclickAgent.onPause(this);
	}

	/**
	 * @Title: change2NormalView
	 * @Description: 切换到正常视图
	 * @author: leobert.lan
	 */
	public void change2NormalView() {
		emptyView.setVisibility(View.GONE);
		emptyView2.setVisibility(View.GONE);
		mPullToRefreshListView.setVisibility(View.VISIBLE);
	}

	/**
	 * @Title: change2EmptyView
	 * @Description: 搜索无结果时切换到空视图
	 * @author: leobert.lan
	 */
	public void change2EmptyView() {
		emptyView.setVisibility(View.VISIBLE);
		emptyView2.setVisibility(View.GONE);
		mPullToRefreshListView.setVisibility(View.GONE);
	}

	public void change2EmptyView2() {
		emptyView2.setVisibility(View.VISIBLE);
		emptyView.setVisibility(View.GONE);
		mPullToRefreshListView.setVisibility(View.GONE);
	}

	public boolean isSearchView() {
		return mPullToRefreshListView.getOnRefreshListener2() == searchCallback;
	}

	@Override
	protected String getPageName() {
		return SearchActivity.mPageName;
	}

	@Override
	protected UMengActivity getActivity() {
		return SearchActivity.this;
	}

	@Override
	protected void onStop() {
		super.onStop();
		mSearchUtil.writeOnfinish();
	}
}
