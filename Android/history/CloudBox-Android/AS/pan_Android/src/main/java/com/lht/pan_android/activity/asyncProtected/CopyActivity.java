package com.lht.pan_android.activity.asyncProtected;

import java.util.ArrayList;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.adapter.CopyListAdapter;
import com.lht.pan_android.bean.ShareItemBean;
import com.lht.pan_android.util.CloudBoxApplication;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.DLog.LogLocation;
import com.lht.pan_android.util.activity.CloudBoxUtil.Operate;
import com.lht.pan_android.util.activity.CopyUtil;
import com.lht.pan_android.util.dir.CopyDataDirectionUtil;
import com.lht.pan_android.view.BreadLineView;
import com.lht.pan_android.view.BreadLineView.OnBreadItemClickListener;
import com.lht.pan_android.view.BreadLineView.OnUpdatedListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class CopyActivity extends AsyncProtectedActivity implements OnClickListener, IKeyManager.UserFolderList {

	private final static String mPageName = "copyActivity";

	/**
	 * MESSAGE_KEY:传递数据的key
	 */
	public final static String MESSAGE_KEY = "message";

	private String tag = "copyActivity";

	private TextView txtCancle;
	private TextView txtSure;
	private Button btnMoveCreateFolder;
	private EditText editFolderName;
	private PullToRefreshListView mPullToRefreshListView;
	private Context mContext;

	private ArrayList<ShareItemBean> mData;
	public CopyListAdapter mCopyListAdapter;
	public CopyDataDirectionUtil mCopyDataDirectionUtil;
	public CopyUtil mCopyUtil;

	private HorizontalScrollView breadGuideSection;

	private BreadLineView breadGuide;

	/**
	 * filesNeedToMove:需要移动的文件
	 */
	private ArrayList<ShareItemBean> filesNeedToMove;
	/**
	 * pathToMove:移动到的文件夹，初始化为根目录
	 */
	private String pathToMove = "/";

	private ProgressBar mProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_move);
		CloudBoxApplication.addActivity(this);

		mContext = this;
		mCopyUtil = new CopyUtil(this);
		mCopyDataDirectionUtil = new CopyDataDirectionUtil(CopyActivity.this, this);
		filesNeedToMove = new ArrayList<ShareItemBean>();
		// 获取传递的数据，并反序列化
		getAndFormatData();

		initView();
		initEvent();

		mCopyDataDirectionUtil.setGetListImpl(mCopyUtil.getOperateCallBack());
		mData = new ArrayList<ShareItemBean>();
		mCopyListAdapter = new CopyListAdapter(mData, mContext);

		mCopyUtil.performOperate(Operate.pull_to_refresh, null);

		mPullToRefreshListView.setAdapter(mCopyListAdapter);
		mPullToRefreshListView.setOnRefreshListener(pullToFreshListener);

		this.updataBreadLine("/");
	}

	/**
	 * @Title: getAndFormatData
	 * @Description: 获取数据并反序列化
	 * @author: leobert.lan
	 */
	private void getAndFormatData() {
		Intent i = getIntent();
		ArrayList<String> temp = i.getStringArrayListExtra(CopyActivity.MESSAGE_KEY);
		for (int m = 0; m < temp.size(); m++) {
			String str = temp.get(m);
			ShareItemBean bean = JSON.parseObject(str, ShareItemBean.class);
			filesNeedToMove.add(bean);
			DLog.d(getClass(), new LogLocation(), "check item" + JSON.toJSONString(bean));
		}
	}

	/**
	 * @Title: updataBreadLine
	 * @Description: 通知更新面包屑导航
	 * @author: zhangbin
	 * @param paths
	 */
	public void updataBreadLine(String paths) {
		DLog.d(getClass(), new LogLocation(), "call update");
		breadGuide.updateView(paths);
	}

	/**
	 * @Title: checkPathForFile
	 * @Description: 该方法用于优化文件移动，如果没有发生实际移动，就提示一下，优化体验
	 * @author: leobert.lan
	 * @return true:不需要提示 false:需要提示
	 */
	public boolean checkPathForFile(String path) {
		if (filesNeedToMove.size() == 0)
			return true;
		for (int i = 0; i < filesNeedToMove.size(); i++) {
			ShareItemBean bean = filesNeedToMove.get(i);
			String parentPath = bean.getPath().substring(0, bean.getPath().lastIndexOf("/"));
			Log.i("zhang", "parentPath:" + parentPath);
			if (bean.getPath().equals(path))
				return false;
		}
		return true;
	}

	private void initView() {
		txtCancle = (TextView) findViewById(R.id.move_cancle);
		txtSure = (TextView) findViewById(R.id.move_sure);
		btnMoveCreateFolder = (Button) findViewById(R.id.move_create_folder);
		editFolderName = (EditText) findViewById(R.id.move_folder_name);
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.move_listview);
		breadGuideSection = (HorizontalScrollView) findViewById(R.id.move_bread_scrollview);
		breadGuide = new BreadLineView(mContext);

		breadGuideSection.addView(breadGuide);

		mProgressBar = (ProgressBar) findViewById(R.id.move_progress);
	}

	private void initEvent() {
		txtCancle.setOnClickListener(this);
		txtSure.setOnClickListener(this);
		btnMoveCreateFolder.setOnClickListener(this);
		breadGuide.setBreadItemClickListener(new OnBreadItemClickListener() {

			@Override
			public void postPath(String path) {
				mCopyDataDirectionUtil.updateCurrentPath(path);
				updateDestination(path);
				mCopyUtil.performOperate(Operate.pull_to_refresh, null);
			}
		});

		breadGuide.setOnUpdatedListener(new OnUpdatedListener() {

			@Override
			public void onUpdated() {
				breadGuideSection.fullScroll(View.FOCUS_RIGHT);
			}
		});

		editFolderName.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					btnMoveCreateFolder.performClick();
				}
				return false;
			}
		});
	}

	public void updateDestination(String path) {
		pathToMove = path;
		// updataBreadLine(pathToMove);
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
		case R.id.move_cancle:
			CopyActivity.this.finish();
			break;
		case R.id.move_sure:
			performMove();
			break;
		case R.id.move_create_folder:
			createNewFolder(pathToMove, editFolderName.getText().toString());
			break;
		default:
			break;
		}
	}

	/**
	 * @Title: performMove
	 * @Description: 执行移动
	 * @author: leobert.lan
	 */
	private void performMove() {
		mCopyUtil.move(filesNeedToMove, pathToMove);
	}

	/**
	 * @Title: createNewFolder
	 * @Description: 创建新文件夹
	 * @author: leobert.lan
	 * @param parentPath
	 * @param folderName
	 */
	private void createNewFolder(String parentPath, String folderName) {
		mCopyUtil.createNewFolder(parentPath, folderName);
	}

	public PullToRefreshListView getPullRefreshListView() {
		return mPullToRefreshListView;
	}

	/**
	 * pullToFreshListener:刷新加载事件回调接口
	 */
	private OnRefreshListener2<ListView> pullToFreshListener = new OnRefreshListener2<ListView>() {
		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			updateLastFreshTime(refreshView);
			mCopyUtil.performOperate(Operate.pull_to_refresh, null);
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			updateLastFreshTime(refreshView);
			mCopyUtil.performOperate(Operate.pull_to_add, null);
		}
	};

	/**
	 * @Title: hideModifyView
	 * @Description: 关闭编辑视图
	 * @author: leobert.lan
	 */
	public void hideModifyView() {
		editFolderName.setText(null);
		((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(editFolderName.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected String getPageName() {
		return CopyActivity.mPageName;
	}

	@Override
	protected UMengActivity getActivity() {
		return CopyActivity.this;
	}

	@Override
	protected ProgressBar getProgressBar() {
		return mProgressBar;
	}

}
