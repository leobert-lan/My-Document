package com.lht.pan_android.activity.asyncProtected;

import java.util.ArrayList;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.adapter.MoveListAdapter;
import com.lht.pan_android.bean.DirItemBean;
import com.lht.pan_android.util.CloudBoxApplication;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.DLog.LogLocation;
import com.lht.pan_android.util.activity.CloudBoxUtil.Operate;
import com.lht.pan_android.util.activity.MoveUtil;
import com.lht.pan_android.util.dir.MoveDataDirectionUtil;
import com.lht.pan_android.view.BreadLineView;
import com.lht.pan_android.view.BreadLineView.OnBreadItemClickListener;
import com.lht.pan_android.view.BreadLineView.OnUpdatedListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
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

public class MoveActivity extends AsyncProtectedActivity implements OnClickListener, IKeyManager.UserFolderList {

	private final static String mPageName = "moveActivity";

	/**
	 * MESSAGE_KEY:传递数据的key
	 */
	public final static String MESSAGE_KEY = "message";

	private String tag = "MoveActivity";

	private TextView txtCancle;
	private TextView txtSure;
	private Button btnMoveCreateFolder;
	private EditText editFolderName;
	private PullToRefreshListView mPullToRefreshListView;
	private Context mContext;

	private ArrayList<DirItemBean> mData;
	public MoveListAdapter mAdapter;
	public MoveDataDirectionUtil mMoveDataDirectionUtil;
	public MoveUtil mMoveUtil;

	private HorizontalScrollView breadGuideSection;

	private BreadLineView breadGuide;

	/**
	 * filesNeedToMove:需要移动的文件
	 */
	private ArrayList<DirItemBean> filesNeedToMove;
	/**
	 * foldersNeedToMove: 需要移动的文件夹
	 */
	private ArrayList<DirItemBean> foldersNeedToMove;

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
		mMoveUtil = new MoveUtil(this);
		mMoveDataDirectionUtil = new MoveDataDirectionUtil(MoveActivity.this, this);
		// new IPreventPenetrate() {
		//
		// @Override
		// public void preventPenetrate(Activity activity,
		// boolean isProtectNeed) {
		// if (isProtectNeed)
		// showWaitView(isProtectNeed);
		// else
		// cancelWaitView();
		//
		// }
		// });
		filesNeedToMove = new ArrayList<DirItemBean>();
		foldersNeedToMove = new ArrayList<DirItemBean>();

		// 获取传递的数据，并反序列化
		getAndFormatData();

		initView();
		initEvent();

		mMoveDataDirectionUtil.setGetListImpl(mMoveUtil.getOperateCallBack());
		mData = new ArrayList<DirItemBean>();
		mAdapter = new MoveListAdapter(mData, mContext);

		mMoveUtil.performOperate(Operate.pull_to_refresh, null);

		mPullToRefreshListView.setAdapter(mAdapter);
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
		ArrayList<String> temp = i.getStringArrayListExtra(MoveActivity.MESSAGE_KEY);
		for (int m = 0; m < temp.size(); m++) {
			String str = temp.get(m);
			DirItemBean bean = JSON.parseObject(str, DirItemBean.class);
			if (bean.getType().equals(TYPE_FILE))
				filesNeedToMove.add(bean);
			else if (bean.getType().equals(TYPE_FOLDER))
				foldersNeedToMove.add(bean);
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
	 * @Title: checkPathLegalForFolder
	 * @Description: 检验路径对于移动文件夹是合法的
	 * @author: leobert.lan
	 * @param path
	 * @return true：合法，如果没有folder需要移动，同样返回 true false：不合法
	 */
	public boolean checkPathLegalForFolder(String path) {
		if (path.equals("/"))
			return true;
		if (foldersNeedToMove.size() == 0)
			return true;
		for (int i = 0; i < foldersNeedToMove.size(); i++) {
			DirItemBean bean = foldersNeedToMove.get(i);
			// Log.d(tag, "check res:"+bean.getPath()+"\r\n check des:"+path);
			if (bean.getPath().equals(path) || (bean.getPath() + "/").equals(path))
				return false;
		}
		return true;
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
			DirItemBean bean = filesNeedToMove.get(i);
			String parentPath = bean.getPath().substring(0, bean.getPath().lastIndexOf("/"));
			DLog.d(getClass(), new LogLocation(), "parentPath" + parentPath);
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
				mMoveDataDirectionUtil.updateCurrentPath(path);
				updateDestination(path);
				mMoveUtil.performOperate(Operate.pull_to_refresh, null);
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
			MoveActivity.this.finish();
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
		mMoveUtil.move(filesNeedToMove, foldersNeedToMove, pathToMove);
	}

	/**
	 * @Title: createNewFolder
	 * @Description: 创建新文件夹
	 * @author: leobert.lan
	 * @param parentPath
	 * @param folderName
	 */
	private void createNewFolder(String parentPath, String folderName) {
		mMoveUtil.createNewFolder(parentPath, folderName);
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
			mMoveUtil.performOperate(Operate.pull_to_refresh, null);
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			updateLastFreshTime(refreshView);
			mMoveUtil.performOperate(Operate.pull_to_add, null);
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
		// MobclickAgent.onPageStart(mPageName);
		// MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// MobclickAgent.onPageEnd(mPageName);
		// MobclickAgent.onPause(this);
	}

	@Override
	protected String getPageName() {
		return MoveActivity.mPageName;
	}

	@Override
	protected UMengActivity getActivity() {
		return MoveActivity.this;
	}

	@Override
	protected ProgressBar getProgressBar() {
		return mProgressBar;
	}

}
