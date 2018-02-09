package com.lht.pan_android.activity.asyncProtected;

import java.util.ArrayList;
import java.util.HashMap;

import com.lht.pan_android.R;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.adapter.SearchUserCardAdapter;
import com.lht.pan_android.adapter.SearchUserListAdapter;
import com.lht.pan_android.adapter.SearchUserListAdapter.OnItemSelectChangedListener;
import com.lht.pan_android.adapter.SearchUserSSVListAdapter;
import com.lht.pan_android.bean.SearchUserItemBean;
import com.lht.pan_android.util.CloudBoxApplication;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.DLog.LogLocation;
import com.lht.pan_android.util.activity.ShareToUserActivityUtil;
import com.lht.pan_android.util.activity.ShareToUserActivityUtil.SearchUserCallback;
import com.lht.pan_android.view.AutoArrangeCardLayout;
import com.lht.pan_android.view.SSVAdapter.OnItemDeleteListener;
import com.lht.pan_android.view.SSVListView;
import com.lht.pan_android.view.ShareSelectView;
import com.lht.pan_android.view.ShareSelectView.OnModeChangedLintener;
import com.lht.pan_android.view.ShareSelectView.ViewMode;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

/**
 * @ClassName: ShareToUserActivity
 * @Description:
 * @note:优化计划：数据关系强正向映射，弱反向映射，增强ssvlist数据源的维护，此处可以当做暂存
 * @date 2016年1月28日 上午11:03:04
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class ShareToUserActivity extends AsyncProtectedActivity {
	private final static String mPageName = "shareToUserActivity";

	public final static String INTENT_KEY_PATH = "path";

	/**
	 * INTENT_KEY_SHAREID:实际使用中和INTENT_KEY_PATH只能存在一个
	 */
	public final static String INTENT_KEY_SHAREID = "shareid";

	public final static String INTENT_TYPE = "operate";

	public final static int INTENT_TYPE_MODIFY = 1;

	public final static int INTENT_TYPE_SHARE = 0;

	private ProgressBar mProgressBar;

	private EditText txtSearch;

	private ImageView imgSearch;

	private ShareToUserActivityUtil mShareToUserActivityUtil;

	private ListView searchList;

	private SearchUserListAdapter searchUserListAdapter;

	private ShareSelectView shareSelectView;

	private AutoArrangeCardLayout arrangeCardLayout;

	private SSVListView ssvListView;

	private Context mContext;

	private SearchUserCardAdapter searchUserCardAdapter;

	private CheckBox mToggle;

	private SearchUserSSVListAdapter ssvListAdapter;

	private LinearLayout cardSection;

	/**
	 * ***Share:分享按钮
	 */
	private Button cardShare, listShare;

	private String sharePath, shareId;

	private ImageView back;

	private RelativeLayout emptyView;

	/**
	 * showSection:搜索结果显示区
	 */
	private RelativeLayout showSection;

	private TextView txtTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_sharetouser);
		CloudBoxApplication.addActivity(this);

		initView();
		initEvent();
		mShareToUserActivityUtil = new ShareToUserActivityUtil(this);
		mShareToUserActivityUtil.setSearchUserCallback(searchUserCallback);

		change2NormalView();

		detectRemodify();
	}

	/**
	 * @Title: detectRemodify
	 * @Description: 检测是否重新编辑分享，存在数据显示编辑，否则启动联系人获取
	 * @author: leobert.lan
	 */
	private void detectRemodify() {
		int operate = getIntent().getIntExtra(INTENT_TYPE, 0);

		if (operate == INTENT_TYPE_SHARE) {
			sharePath = getIntent().getStringExtra(INTENT_KEY_PATH);

			cardShare.setText(R.string.string_share1);
			listShare.setText(R.string.string_share);
			txtTitle.setText(R.string.title_activity_share_to_user);

			cardShare.setOnClickListener(shareClickListner);
			listShare.setOnClickListener(shareClickListner);
			mShareToUserActivityUtil.startContactGet();
			return;
		} else if (operate == INTENT_TYPE_MODIFY) {
			shareId = getIntent().getStringExtra(INTENT_KEY_SHAREID);

			cardShare.setText(R.string.string_share_modify1);
			listShare.setText(R.string.string_share_modify2);
			txtTitle.setText(R.string.title_activity_share_to_user_modify);

			cardShare.setOnClickListener(modifyClickListener);
			listShare.setOnClickListener(modifyClickListener);
		}

		ArrayList<SearchUserItemBean> temp = getIntent().getParcelableArrayListExtra("_data");
		if (temp == null || temp.size() == 0) {
			mShareToUserActivityUtil.startContactGet();
			Log.e("lmsg", "get no data");
			return;
		}
		change2NormalView();
		Log.e("lmsg", "get data");
		searchUserListAdapter.ReplaceAll(temp, true);

	}

	public void change2NormalView() {
		searchList.setVisibility(View.VISIBLE);
		emptyView.setVisibility(View.GONE);
		searchList.bringToFront();
	}

	public void change2EmptyView() {
		emptyView.setVisibility(View.VISIBLE);
		searchList.setVisibility(View.GONE);
		emptyView.bringToFront();
	}

	private void initEvent() {
		imgSearch.setOnClickListener(callSearchListener);
		txtSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				change2NormalView();
			}
		});

		searchUserListAdapter = new SearchUserListAdapter(new ArrayList<SearchUserItemBean>(), this);

		searchList.setAdapter(searchUserListAdapter);

		searchUserListAdapter.setOnItemSelectChangedListener(itemSelectChangedListener);

		searchUserCardAdapter = new SearchUserCardAdapter(mContext, new ArrayList<SearchUserItemBean>());

		ssvListAdapter = new SearchUserSSVListAdapter(mContext, new ArrayList<SearchUserItemBean>());

		shareSelectView.setSSVCardAdapter(searchUserCardAdapter);

		shareSelectView.setSSVListAdapter(ssvListAdapter);

		searchUserCardAdapter.setOnItemDeleteListener(deleteListener);
		ssvListAdapter.setOnItemDeleteListener(deleteListener);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ShareToUserActivity.this.finish();
			}
		});

		mToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					shareSelectView.changeMode(ViewMode.list);
				} else {
					shareSelectView.changeMode(ViewMode.simple);
				}
			}
		});

		shareSelectView.setOnModeChangedLintener(new OnModeChangedLintener() {

			@Override
			public void onModeChanged(ViewMode modeToChange) {
				if (modeToChange == ViewMode.simple) {
					changeViewStyle2Card();
				} else {
					changeViewStyle2List();
				}
			}
		});

		// 已经迁移到detectModify中
		// cardShare.setOnClickListener(shareClickListner);
		// listShare.setOnClickListener(shareClickListner);

		txtSearch.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					imgSearch.performClick();
				}
				return false;
			}
		});
	}

	protected void changeViewStyle2List() {
		Animation dismiss_RB = AnimationUtils.loadAnimation(mContext, R.anim.scale_dismiss_rb);
		Animation show_RB = AnimationUtils.loadAnimation(mContext, R.anim.scale_show_rb);

		listShare.startAnimation(show_RB);
		searchList.startAnimation(dismiss_RB);

		Animation show_RB2 = AnimationUtils.loadAnimation(mContext, R.anim.scale_show_rb400);
		ssvListView.startAnimation(show_RB2);
		listShare.setVisibility(View.VISIBLE);

		dismiss_RB.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				cardSection.setVisibility(View.GONE);
				showSection.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
			}
		});
		ssvListAdapter.notifyDataSetChanged();

	}

	protected void changeViewStyle2Card() {
		Animation show_RT = AnimationUtils.loadAnimation(mContext, R.anim.scale_show_rt);
		Animation dismiss_LT = AnimationUtils.loadAnimation(mContext, R.anim.scale_dismiss);

		show_RT.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				showSection.setVisibility(View.VISIBLE);
				// searchList.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				cardSection.setVisibility(View.VISIBLE);
			}
		});

		dismiss_LT.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				listShare.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
			}
		});

		searchList.startAnimation(show_RT);
		cardSection.startAnimation(show_RT);
		listShare.startAnimation(dismiss_LT);
	}

	private void initView() {
		mProgressBar = (ProgressBar) findViewById(R.id.share_user_progress);
		txtSearch = (EditText) findViewById(R.id.share_user_et_search);
		txtTitle = (TextView) findViewById(R.id.title_txt_name);
		imgSearch = (ImageView) findViewById(R.id.share_user_img_search);
		searchList = (ListView) findViewById(R.id.share_user_listview);

		arrangeCardLayout = (AutoArrangeCardLayout) findViewById(R.id.search_user_aacl);
		ssvListView = (SSVListView) findViewById(R.id.search_user_ssv_listview);
		shareSelectView = new ShareSelectView(mContext, arrangeCardLayout, ssvListView);

		mToggle = (CheckBox) findViewById(R.id.search_user_toggle);

		cardSection = (LinearLayout) findViewById(R.id.search_user_cardsection);

		cardShare = (Button) findViewById(R.id.search_user_share);
		listShare = (Button) findViewById(R.id.search_user_share2);
		back = (ImageView) findViewById(R.id.share_back);

		emptyView = (RelativeLayout) findViewById(R.id.shareuser_ll_nothing);
		showSection = (RelativeLayout) findViewById(R.id.searchuser_showsection);
	}

	@Override
	protected String getPageName() {
		return ShareToUserActivity.mPageName;
	}

	@Override
	protected UMengActivity getActivity() {
		return ShareToUserActivity.this;
	}

	@Override
	protected ProgressBar getProgressBar() {
		return mProgressBar;
	}

	private OnClickListener callSearchListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			DLog.d(getClass(), new LogLocation(), "call search");
			String key = txtSearch.getText().toString();
			mShareToUserActivityUtil.searchUser(key);
			change2NormalView();
			hideSoftInput();
			reportCountEvent(CALC_CB_SEARCHUSER);
		}
	};

	private ArrayList<SearchUserItemBean> selected = new ArrayList<SearchUserItemBean>();

	private HashMap<String, Integer> selectedMap = new HashMap<String, Integer>();

	private SearchUserCallback searchUserCallback = new SearchUserCallback() {

		@Override
		public void onUserSearched(ArrayList<SearchUserItemBean> list) {
			HashMap<String, SearchUserItemBean> datas = new HashMap<String, SearchUserItemBean>();
			ArrayList<SearchUserItemBean> temp = new ArrayList<SearchUserItemBean>();

			for (SearchUserItemBean bean : selected) {
				datas.put(bean.getUsername(), bean);
			}

			for (SearchUserItemBean bean : list) {
				// 防止覆盖、重复
				if (datas.containsKey(bean.getUsername()))
					continue;
				temp.add(bean);
			}
			searchUserListAdapter.ReplaceAll(temp, true);
		}
	};

	private OnItemSelectChangedListener itemSelectChangedListener = new OnItemSelectChangedListener() {

		@Override
		public void OnItemSelectChanged(SearchUserItemBean bean) {
			DLog.e(ShareToUserActivity.class, "select change");
			if (bean == null)
				return;
			if (bean.isSelect()) {
				addToHolder(bean);
			} else {
				removeFromHolder(bean);
			}
			searchUserCardAdapter.sync(selected, bean);
			ssvListAdapter.sync(selected, bean);
		}
	};

	private OnItemDeleteListener deleteListener = new OnItemDeleteListener() {

		@Override
		public void onItemDelete(String username) {
			cancelSelect(username);
		}
	};

	private OnClickListener shareClickListner = new OnClickListener() {

		@Override
		public void onClick(View v) {
			callShare();
		}
	};

	private OnClickListener modifyClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// 修改分享的逻辑
			if (selected.isEmpty()) {
				Toast.makeText(mContext, "用户不能为空!", Toast.LENGTH_SHORT).show();
				return;
			}
			mShareToUserActivityUtil.modify(shareId, selected);
			mShareToUserActivityUtil.saveContract(selected);
		}
	};

	protected void callShare() {
		mShareToUserActivityUtil.share(sharePath, selected);
		mShareToUserActivityUtil.saveContract(selected);
	}

	protected void removeFromHolder(SearchUserItemBean bean) {
		if (selectedMap.containsKey(bean.getUsername())) {
			int index = selectedMap.get(bean.getUsername());
			selected.remove(index);
			selectedMap.clear();
			for (int i = 0; i < selected.size(); i++) {
				SearchUserItemBean bean2 = selected.get(i);
				selectedMap.put(bean2.getUsername(), i);
			}
		} else {
			DLog.e(getClass(), "出现了移除但是map中不包含的情况");
		}
	}

	protected void addToHolder(SearchUserItemBean bean) {
		if (selectedMap.containsKey(bean.getUsername())) {
			// 重复在第一列表删除添加
			DLog.e(getClass(),
					"出现了添加但是已经包含的情况,检验逻辑是否不包含：" + (selected.get(selectedMap.get(bean.getUsername())) == null));
		} else {
			selected.add(bean);
			selectedMap.put(bean.getUsername(), selected.size() - 1);
		}
	}

	protected void cancelSelect(String username) {
		if (selectedMap.containsKey(username)) {
			boolean b = searchUserListAdapter.cancelSelect(username);
			// 说明数据已经没有了，是之前保存的
			if (!b) {
				int index = selectedMap.get(username);
				selected.get(index).setSelect(false);
				itemSelectChangedListener.OnItemSelectChanged(selected.get(index));
			}
		}
	}

	public SearchUserListAdapter getSearchUserListAdapter() {
		return searchUserListAdapter;
	}

	@Override
	protected void onDestroy() {
		mShareToUserActivityUtil.destory();
		super.onDestroy();
	}

}
