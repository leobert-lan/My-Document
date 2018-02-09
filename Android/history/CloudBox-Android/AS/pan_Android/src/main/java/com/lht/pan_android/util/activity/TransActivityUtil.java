package com.lht.pan_android.util.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IPreventPenetrate;
import com.lht.pan_android.Interface.IUmengEventKey;
import com.lht.pan_android.Interface.MainActivityIPreventPenetrate;
import com.lht.pan_android.Interface.Mime;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.activity.ViewPagerItem.TransportManagerActivity;
import com.lht.pan_android.activity.asyncProtected.MainActivity;
import com.lht.pan_android.util.database.DownLoadDataBaseUtils;
import com.lht.pan_android.util.database.UpLoadDataBaseUtils;
import com.lht.pan_android.util.string.StringUtil;
import com.lht.pan_android.view.TransViewInfo;
import com.lht.pan_android.view.TransViewInfo.Status;
import com.lht.pan_android.view.TransViewItem;
import com.lht.pan_android.view.TransViewItem.OnItemBodyClickListener;
import com.lht.pan_android.view.popupwins.CustomDialog;
import com.lht.pan_android.view.popupwins.CustomPopupWindow.OnPositiveClickListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

/**
 * @ClassName: TransActivityUtil
 * @Description: TODO
 * @date 2015年12月30日 上午9:36:52
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class TransActivityUtil implements Mime {

	private Context mContext;

	private CustomDialog guideToSetting;

	private CustomDialog deleteAlertDialog;

	private HashSet<Integer> multiSelectItems;

	private MainActivity mParent;

	public TransActivityUtil(Context ctx) {
		mContext = ctx;
		mParent = (MainActivity) ((TransportManagerActivity) mContext).getParent();
		guideToSetting = new CustomDialog(mParent, new MainActivityIPreventPenetrate());
		deleteAlertDialog = new CustomDialog(mParent, new IPreventPenetrate() {

			@Override
			public void preventPenetrate(Activity activity, boolean isProtectNeed) {
				((MainActivity) activity).setActiveStateOfDispatchOnTouch(!isProtectNeed);
			}
		});
		multiSelectItems = new HashSet<Integer>();
		init();
	}

	private void init() {
		initDialog();

	}

	/**
	 * @Title: initDialog
	 * @Description: 初始化引导设置WiFi的dialog
	 * @author: leobert.lan
	 */
	private void initDialog() {
		guideToSetting.setContent(R.string.trans_guide_to_changesetting_content);
		guideToSetting.setNegativeButton(R.string.trans_guide_to_changesetting_negative);
		guideToSetting.setPositiveButton(R.string.trans_guide_to_changesetting_positive);
		guideToSetting.setPositiveClickListener(new OnPositiveClickListener() {

			@Override
			public void onPositiveClick() {
				((MainActivity) ((TransportManagerActivity) mContext).getParent()).flingToPageSmooth(3);
				guideToSetting.dismiss();
				getActivity().reportCountEvent(IUmengEventKey.CALC_CB_DEL);
			}
		});
		deleteAlertDialog.setContent(R.string.string_delete_multi);
		deleteAlertDialog.setPositiveButton(R.string.string_sure);
		deleteAlertDialog.setNegativeButton(R.string.string_cancel);
	}

	public CustomDialog getChangeSettingGuideDialog() {
		return guideToSetting;
	}

	public CustomDialog getDeleteAlertDialog() {
		return deleteAlertDialog;
	}

	/**
	 * @Title: addSelectItem
	 * @Description: 添加一条选中记录
	 * @author: leobert.lan
	 * @param dbIndex
	 */
	public void addSelectItem(int dbIndex) {
		multiSelectItems.add(dbIndex);
		((TransportManagerActivity) mContext).enableDelete();
	}

	public void removeSelectItem(int dbIndex) {
		if (!multiSelectItems.contains(dbIndex))
			throw new IllegalArgumentException("check the whole process,why the argument is not contains in the set?");
		multiSelectItems.remove(dbIndex);
		if (multiSelectItems.isEmpty())
			((TransportManagerActivity) mContext).disableDelete();
	}

	/**
	 * @Title: removeAllOnViewChanged
	 * @Description: 去掉所有选中的记录
	 * @author: leobert.lan
	 */
	public void removeAllOnViewChanged() {
		multiSelectItems.clear();
	}

	/**
	 * @Title: getSelectedItems
	 * @Description: 获取选取的items
	 * @author: leobert.lan
	 * @return
	 */
	public ArrayList<Integer> getSelectedItems() {
		ArrayList<Integer> temp = new ArrayList<Integer>();
		Iterator<Integer> i = multiSelectItems.iterator();
		while (i.hasNext()) {
			temp.add(i.next());
		}
		return temp;
	}

	/**
	 * @Title: switchAll2MultiView
	 * @Description: 切换到多选视图
	 * @author: leobert.lan
	 * @param mDownloadedMap
	 */

	public void switchAll2MultiView(HashMap<String, TransViewItem> map) {
		for (String key : map.keySet()) {
			map.get(key).switch2MultiSelectView();
		}
	}

	/**
	 * @Title: switchAll2NormalView
	 * @Description: 全部切换为常规视图
	 * @author: leobert.lan
	 * @param mDownloadedMap
	 */
	public void switchAll2NormalView(HashMap<String, TransViewItem> map) {
		for (String key : map.keySet()) {
			map.get(key).switch2NormalView();
		}
	}

	/**
	 * @Title: selectAll
	 * @Description: 全选
	 * @author: leobert.lan
	 * @param map
	 */
	public void selectAll(HashMap<String, TransViewItem> map) {
		for (String key : map.keySet()) {
			map.get(key).setSelected(true);
		}
	}

	/**
	 * @Title: deSelectAll
	 * @Description: 全不选
	 * @author: leobert.lan
	 * @param map
	 */
	public void deSelectAll(HashMap<String, TransViewItem> map) {
		for (String key : map.keySet()) {
			map.get(key).setSelected(false);
		}
	}

	public OnItemBodyClickListener getOnItemBodyClickListner() {
		return new OnItemBodyClickListener() {

			@Override
			public void onItemBodyClick(TransViewItem item) {
				TransViewInfo info = item.getInfo();
				boolean isUpload = info.isUpload();

				if (info.getStatus() == Status.complete) {
					String path = "";
					if (isUpload) {
						path = UpLoadDataBaseUtils.getInstance(mContext).getCompletedPath(info.getDbIndex());
					} else {
						path = DownLoadDataBaseUtils.getInstance(mContext).getCompletedPath(info.getDbIndex());
					}
					selectProcedure(path);
				} else if (info.getStatus() == Status.failure) {
					Toast.makeText(mContext, "文件不存在", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mContext, "传输中的文件无法预览", Toast.LENGTH_SHORT).show();
				}

			}
		};
	}

	private void selectProcedure(String path) {

		File file = new File(path);
		if (!file.exists() || file == null) {
			Toast.makeText(mContext, "文件已删除，无法预览", Toast.LENGTH_SHORT).show();
		} else {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(Intent.ACTION_VIEW);
			// 获取扩展名
			String type = getMIMEType(file);

			// umeng统计
			reportOpenEvent(type);

			intent.setDataAndType(Uri.fromFile(file), type);
			// 必须要检验
			PackageManager pm = mContext.getPackageManager();
			ComponentName cn = intent.resolveActivity(pm);
			if (cn == null) {
				// 弹出询问dialog
				final Intent marketIntent = new Intent("android.intent.action.MAIN");
				marketIntent.addCategory("android.intent.category.APP_MARKET");
				if (marketIntent.resolveActivity(pm) != null) {
					getAskOpenMarketDialog(marketIntent).show();
				} else {
					getMarketUnavailableDialog().showSingle();
				}
			} else
				mContext.startActivity(intent);
		}
	}

	/**
	 * @Title: reportOpenEvent
	 * @Description: 汇报打开事件
	 * @author: leobert.lan
	 * @param type
	 */
	private void reportOpenEvent(String type) {
		if (type.startsWith("text")) {
			getActivity().reportCountEvent(IUmengEventKey.COUNT_CB_OPEN_DOC);
		} else if (type.startsWith("audio")) {
			getActivity().reportCountEvent(IUmengEventKey.COUNT_CB_OPEN_AUDIO);
		} else if (type.startsWith("video")) {
			getActivity().reportCountEvent(IUmengEventKey.COUNT_CB_OPEN_VIDEO);
		} else if (type.startsWith("image")) {
			getActivity().reportCountEvent(IUmengEventKey.COUNT_CB_OPEN_IMAGE);
		} else {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(IUmengEventKey.COUNT_CB_KEY_MINETYPE, type);
			getActivity().reportCountEvent(IUmengEventKey.COUNT_CB_OPEN_EXTRA, map);
		}
	}

	/**
	 * @Title: getMIMEType
	 * @Description: 获取文件类型
	 * @author: leobert.lan
	 * @param 比对文件类型
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	private String getMIMEType(File file) {
		String type = "*/*";
		String fName = file.getName();
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0)
			return type;
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		if (StringUtil.isEmpty(end))
			return type;
		for (int i = 0; i < MIME_MapTable.length; i++) {
			if (end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
	}

	private CustomDialog getAskOpenMarketDialog(final Intent marketIntent) {
		CustomDialog dialog = new CustomDialog(mParent, new IPreventPenetrate() {

			@Override
			public void preventPenetrate(Activity activity, boolean isProtectNeed) {
				((MainActivity) activity).setActiveStateOfDispatchOnTouch(!isProtectNeed);
			}
		});
		dialog.setContent(R.string.dialog_content_openmarket);
		dialog.setNegativeButton(R.string.string_cancel);
		dialog.setPositiveButton(R.string.dialog_positive_openmarket);
		dialog.setPositiveClickListener(new OnPositiveClickListener() {

			@Override
			public void onPositiveClick() {
				mContext.startActivity(marketIntent);
			}
		});
		return dialog;
	}

	private CustomDialog getMarketUnavailableDialog() {
		CustomDialog dialog = new CustomDialog(mParent, new IPreventPenetrate() {

			@Override
			public void preventPenetrate(Activity activity, boolean isProtectNeed) {
				((MainActivity) activity).setActiveStateOfDispatchOnTouch(!isProtectNeed);
			}
		});
		dialog.setContent(R.string.dialog_content_marketunavailable);
		dialog.setPositiveButton(R.string.string_sure);
		return dialog;
	}

	public class MyHandler extends Handler {
		public static final int WHAT_ADDUP = 1;
		public static final int WAHT_ADDDOWN = 2;

		public MyHandler() {
		}

		public MyHandler(Looper L) {
			super(L);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == WHAT_ADDUP) {
				((TransportManagerActivity) mContext).addUpJobItems();
			} else if (msg.what == WAHT_ADDDOWN) {
				((TransportManagerActivity) mContext).addDownJobItems();
			} else {

			}
		}
	}

	public MyHandler getDeleteHandler() {
		return new MyHandler();
	}

	public UMengActivity getActivity() {
		return (TransportManagerActivity) mContext;
	}
}
