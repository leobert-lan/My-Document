package com.lht.pan_android.activity.selectItem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.adapter.GroupAdapter;
import com.lht.pan_android.bean.ImageBean;
import com.lht.pan_android.util.CloudBoxApplication;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class SelectMediaGroupActivity extends SelectActivity {

	private final static String mPageName = "videoGroupActivity";

	private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>();
	private final static int SCAN_OK = 1;
	private ProgressDialog mProgressDialog;
	private GroupAdapter adapter;
	private ListView mGroupGridView;
	private ImageView mMediaGroup;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SCAN_OK:
				// 关闭进度条
				mProgressDialog.dismiss();
				if (mGruopMap.size() == 0) {
					Toast.makeText(getApplicationContext(), R.string.no_content, Toast.LENGTH_SHORT).show();
					return;
				}

				adapter = new GroupAdapter(SelectMediaGroupActivity.this, subGroupOfImage(mGruopMap), mGroupGridView);
				adapter.setContentType(IKeyManager.ContentType.VIDEO);
				mGroupGridView.setAdapter(adapter);
				break;
			}
		}

	};

	private String currentPath = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		CloudBoxApplication.addActivity(this);
		CloudBoxApplication.addSelectActivity(this);
		setContentView(R.layout.activity_select_image_group);

		mGroupGridView = (ListView) findViewById(R.id.select_image_group_list);
		mMediaGroup = (ImageView) findViewById(R.id.group_select_back);
		getImages();

		mMediaGroup.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SelectMediaGroupActivity.this.finish();
			}
		});
		mGroupGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				List<String> childList = mGruopMap.get(((ImageBean) adapter.getItem(position)).getFolderName());

				Intent mIntent = new Intent(SelectMediaGroupActivity.this, SelectUploadItemsActivity.class);
				mIntent.setAction(IKeyManager.ContentType.VIDEO);
				mIntent.putStringArrayListExtra("data", (ArrayList<String>) childList);
				// 暂且使用这个key
				mIntent.putExtra(IKeyManager.UserFolderList.PARAM_PATH, currentPath);
				startActivity(mIntent);
			}
		});

	}

	@Override
	protected void onResume() {
		currentPath = getIntent().getStringExtra(IKeyManager.UserFolderList.PARAM_PATH);
		super.onResume();
	}

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中
	 */
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, R.string.no_other_memory, Toast.LENGTH_SHORT).show();
			return;
		}

		// 显示进度条
		mProgressDialog = ProgressDialog.show(this, null, getResources().getString(R.string.cloudbox_on_add));

		new Thread(new Runnable() {

			@Override
			public void run() {
				// MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				Uri mVedioUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = SelectMediaGroupActivity.this.getContentResolver();

				// 只查询jpeg和png的图片

				Cursor mCursor = mContentResolver.query(mVedioUri, null, null, null, MediaColumns.DATE_MODIFIED);

				while (mCursor.moveToNext()) {
					// 获取图片的路径
					String path = mCursor.getString(mCursor.getColumnIndex(MediaColumns.DATA));

					// 获取该图片的父路径名
					String parentName = new File(path).getParentFile().getName();

					// 根据父路径名将图片放入到mGruopMap中
					if (!mGruopMap.containsKey(parentName)) {
						List<String> chileList = new ArrayList<String>();
						chileList.add(path);
						mGruopMap.put(parentName, chileList);
					} else {
						mGruopMap.get(parentName).add(path);
					}
				}

				mCursor.close();

				// 通知Handler扫描图片完成
				mHandler.sendEmptyMessage(SCAN_OK);

			}
		}).start();

	}

	/**
	 * @Title: subGroupOfImage
	 * @Description: 组装分组界面GridView的数据源， <br>
	 *               因为我们扫描手机的时候将图片信息放在HashMap中 所以需要遍历HashMap将数据组装成List
	 * @author: leobert.lan
	 * @param mGruopMap
	 * @return
	 */
	private List<ImageBean> subGroupOfImage(HashMap<String, List<String>> mGruopMap) {
		List<ImageBean> list = new ArrayList<ImageBean>();
		if (mGruopMap.size() == 0) {
			return list;
		}

		Iterator<Map.Entry<String, List<String>>> it = mGruopMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, List<String>> entry = it.next();
			ImageBean mImageBean = new ImageBean();
			String key = entry.getKey();
			List<String> value = entry.getValue();

			mImageBean.setFolderName(key);
			mImageBean.setImageCounts(value.size());
			// 获取该组的第一张图片作为封面
			mImageBean.setTopImagePath(value.get(0));

			list.add(mImageBean);
		}

		return list;

	}

	@Override
	protected String getPageName() {
		return SelectMediaGroupActivity.mPageName;
	}

	@Override
	protected UMengActivity getActivity() {
		return SelectMediaGroupActivity.this;
	}

}
