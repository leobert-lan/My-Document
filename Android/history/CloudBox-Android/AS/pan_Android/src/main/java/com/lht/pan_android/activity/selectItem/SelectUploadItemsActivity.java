package com.lht.pan_android.activity.selectItem;

import java.util.ArrayList;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.activity.BaseActivity;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.activity.asyncProtected.PathChooseActivity;
import com.lht.pan_android.adapter.ItemsAdapter;
import com.lht.pan_android.clazz.Events.UploadSelectChange;
import com.lht.pan_android.util.CloudBoxApplication;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.DLog.LogLocation;
import com.lht.pan_android.util.activity.PathSelectSave;
import com.lht.pan_android.util.string.StringUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectUploadItemsActivity extends BaseActivity implements OnClickListener {

	private final static String mPageName = "selectUploadItemActivity";

	private GridView mGridView;
	/**
	 * list:本目录中的数据
	 */
	private ArrayList<String> list;
	private ItemsAdapter adapter;

	private Button btnUpload;

	private String currentPath = null;

	private String contentType = "";

	private TextView txtPath;
	private ImageView mImageBack;

	private Button btnChoosePath;

	public final static String MSG_CODE = "code";

	private static int REQUEST_CODE = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		CloudBoxApplication.addActivity(this);
		setContentView(R.layout.activity_select_images);

		EventBus.getDefault().register(this);

		mGridView = (GridView) findViewById(R.id.select_images_gridview);
		list = getIntent().getStringArrayListExtra("data");
		contentType = getIntent().getAction();
		adapter = new ItemsAdapter(this, list, mGridView);
		adapter.setType(contentType);
		mGridView.setAdapter(adapter);

		txtPath = (TextView) findViewById(R.id.linear_select_image_path);
		btnUpload = (Button) findViewById(R.id.select_images_bt_upload);
		mImageBack = (ImageView) findViewById(R.id.image_select_back);

		btnChoosePath = (Button) findViewById(R.id.select_images_bt_choose);

		btnUpload.setOnClickListener(this);
		mImageBack.setOnClickListener(this);
		btnChoosePath.setOnClickListener(this);

	}

	@Subscribe
	public void onEventMainThread(UploadSelectChange event) {
		boolean enable = adapter.getSelectCount() > 0;
		btnUpload.setEnabled(enable);

	}

	@Override
	protected void onResume() {
		if (PathSelectSave.checkCode(REQUEST_CODE)) {
			currentPath = PathSelectSave.getPath();
			UploadPath = currentPath;
		} else {
			currentPath = getIntent().getStringExtra(IKeyManager.UserFolderList.PARAM_PATH);
		}

		String show = currentPath;
		if (currentPath.equals("/"))
			show = getResources().getString(R.string.title_activity_cloud_box);
		txtPath.setText(show);

		REQUEST_CODE++;
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
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		PathSelectSave.destroy();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		adapter = null;
		SelectUploadItemsActivity.this.finish();
	}

	/**
	 * @Title: getSelectedPath
	 * @Description: 选取已经选定的图片的path
	 * @author: leobert.lan
	 * @return
	 */
	private ArrayList<String> getSelectedPath() {
		ArrayList<Integer> selects = adapter.getSelectItems();
		DLog.d(getClass(), new LogLocation(), "count" + selects.size());
		ArrayList<String> selectedPath = new ArrayList<String>();
		for (int i = 0; i < selects.size(); i++) {
			selectedPath.add(list.get(selects.get(i)));
		}
		return selectedPath;

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.select_images_bt_upload:
			doUpload();
			break;
		case R.id.image_select_back:
			finish();
			break;
		case R.id.select_images_bt_choose:
			choosePath();
			break;
		default:
			break;
		}
	}

	private void doUpload() {
		reportCountEvent(CALC_CB_UPLOAD);
		ArrayList<String> s = getSelectedPath();
		for (int i = 0; i < s.size(); i++) {
			DLog.d(getClass(), new LogLocation(), "do upload path:" + s.get(i));
		}

		if (StringUtil.isEmpty(UploadPath))
			UploadPath = currentPath;

		getUpServiceBinder().doJobs(s, UploadPath, contentType);
		UploadPath = null;
		CloudBoxApplication.finishSelectActivities();
		SelectUploadItemsActivity.this.finish();
	}

	private String UploadPath = null;

	/**
	 * @Title: choosePath
	 * @Description: 选择路径,修改上传路径
	 * @author: leobert.lan
	 */
	private void choosePath() {
		Intent i = new Intent(SelectUploadItemsActivity.this, PathChooseActivity.class);
		i.putExtra(MSG_CODE, REQUEST_CODE);
		startActivity(i);
	}

	@Override
	protected String getPageName() {
		return SelectUploadItemsActivity.mPageName;
	}

	@Override
	protected UMengActivity getActivity() {
		return SelectUploadItemsActivity.this;
	}

}
