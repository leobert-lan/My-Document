package com.lht.pan_android.activity.asyncProtected;

import java.util.ArrayList;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lht.pan_android.R;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.activity.selectItem.SelectUploadItemsActivity;
import com.lht.pan_android.adapter.PathChooseListAdapter;
import com.lht.pan_android.bean.DirItemBean;
import com.lht.pan_android.util.CloudBoxApplication;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.DLog.LogLocation;
import com.lht.pan_android.util.activity.CloudBoxUtil.Operate;
import com.lht.pan_android.util.activity.PathChooseUtil;
import com.lht.pan_android.util.activity.PathSelectSave;
import com.lht.pan_android.util.dir.MoveDataDirectionUtil;
import com.lht.pan_android.view.BreadLineView;
import com.lht.pan_android.view.BreadLineView.OnBreadItemClickListener;
import com.lht.pan_android.view.BreadLineView.OnUpdatedListener;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PathChooseActivity extends AsyncProtectedActivity implements OnClickListener {

	private final static String mPageName = "choosePathActivity";

	private TextView txtCancle;
	private TextView txtSure;
	private Button btnMoveCreateFolder;
	private EditText editFolderName;
	private PullToRefreshListView mPullToRefreshListView;
	private Context mContext;

	private ArrayList<DirItemBean> mData;
	public PathChooseListAdapter mAdapter;
	public MoveDataDirectionUtil mMoveDataDirectionUtil;
	public PathChooseUtil mPathChooseUtil;

	private HorizontalScrollView breadGuideSection;

	private BreadLineView breadGuide;

	/**
	 * pathToMove:移动到的文件夹，初始化为根目录
	 */
	private String pathToUpload = "/";

	private ProgressBar mProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_move);
		CloudBoxApplication.addActivity(this);

		mContext = this;
		mPathChooseUtil = new PathChooseUtil(this);
		mMoveDataDirectionUtil = new MoveDataDirectionUtil(this, this);
		initView();
		initEvent();

		mMoveDataDirectionUtil.setGetListImpl(mPathChooseUtil.getOperateCallBack());
		mData = new ArrayList<DirItemBean>();
		mAdapter = new PathChooseListAdapter(mData, mContext);

		mPathChooseUtil.performOperate(Operate.pull_to_refresh, null);

		mPullToRefreshListView.setAdapter(mAdapter);
		mPullToRefreshListView.setOnRefreshListener(pullToFreshListener);

		this.updataBreadLine("/");
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
				mMoveDataDirectionUtil.updateCurrentPath(path);
				updateDestination(path);
				mPathChooseUtil.performOperate(Operate.pull_to_refresh, null);
			}
		});

		breadGuide.setOnUpdatedListener(new OnUpdatedListener() {

			@Override
			public void onUpdated() {
				breadGuideSection.fullScroll(View.FOCUS_RIGHT);
			}
		});
	}

	public void updateDestination(String path) {
		pathToUpload = path;
		// updataBreadLine(pathToUpload);
	}

	protected void updateLastFreshTime(PullToRefreshBase<ListView> refreshView) {
		String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
				DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.move_cancle:
			PathChooseActivity.this.finish();
			break;
		case R.id.move_sure:
			submit();
			break;
		case R.id.move_create_folder:
			createNewFolder(pathToUpload, editFolderName.getText().toString());
			break;
		default:
			break;
		}
	}

	private void submit() {
		PathSelectSave.setREQUEST_CODE(REQUEST_CODE);
		PathSelectSave.setPath(pathToUpload);

		PathChooseActivity.this.finish();
	}

	/**
	 * @Title: createNewFolder
	 * @Description: 创建新文件夹
	 * @author: leobert.lan
	 * @param parentPath
	 * @param folderName
	 */
	private void createNewFolder(String parentPath, String folderName) {
		mPathChooseUtil.createNewFolder(parentPath, folderName);
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
			mPathChooseUtil.performOperate(Operate.pull_to_refresh, null);
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			updateLastFreshTime(refreshView);
			mPathChooseUtil.performOperate(Operate.pull_to_add, null);
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

	private int REQUEST_CODE = -1;

	@Override
	protected void onResume() {
		super.onResume();
		// MobclickAgent.onPageStart(mPageName);
		// MobclickAgent.onResume(this);
		REQUEST_CODE = getIntent().getIntExtra(SelectUploadItemsActivity.MSG_CODE, -1);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// MobclickAgent.onPageEnd(mPageName);
		// MobclickAgent.onPause(this);
	}

	@Override
	protected String getPageName() {
		return PathChooseActivity.mPageName;
	}

	@Override
	protected UMengActivity getActivity() {
		return PathChooseActivity.this;
	}

	@Override
	protected ProgressBar getProgressBar() {
		return mProgressBar;
	}
}