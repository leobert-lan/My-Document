package com.lht.pan_android.util.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.IUmengEventKey;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.Interface.MainActivityIPreventPenetrate;
import com.lht.pan_android.Interface.SubMenuClickListener;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.activity.ViewPagerItem.CloudBoxActivity;
import com.lht.pan_android.activity.asyncProtected.MainActivity;
import com.lht.pan_android.activity.asyncProtected.MoveActivity;
import com.lht.pan_android.activity.others.RenameActivity;
import com.lht.pan_android.bean.DirItemBean;
import com.lht.pan_android.bean.DirPaginationBean;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.DLog.LogLocation;
import com.lht.pan_android.util.ToastUtil;
import com.lht.pan_android.util.ToastUtil.Duration;
import com.lht.pan_android.util.dir.ABSDirectionUtil.IGetList;
import com.lht.pan_android.util.internet.HttpRequestFailureUtil;
import com.lht.pan_android.util.internet.HttpUtil;
import com.lht.pan_android.view.popupwins.CustomDialog;
import com.lht.pan_android.view.popupwins.CustomPopupWindow.OnPositiveClickListener;
import com.lht.pan_android.view.popupwins.SharePopupWindow;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

/**
 * @ClassName: CloudBoxUtil
 * @Description: TODO
 * @date 2015年11月30日 下午3:07:31
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class CloudBoxUtil implements IKeyManager.UserFolderList, IKeyManager.Token {

	private final CloudBoxActivity mActivity;

	private MainActivity mParent;

	private HttpUtil httpUtil;

	private String username = "";

	private String accessId = "";

	private String accessToken = "";

	private CustomDialog mAlertdialog;

	public CloudBoxUtil(CloudBoxActivity activity) {
		this.mActivity = activity;
		mParent = (MainActivity) mActivity.getParent();
		httpUtil = new HttpUtil();
		SharedPreferences sp = activity.getSharedPreferences(SP_TOKEN, Context.MODE_PRIVATE);
		username = sp.getString(KEY_USERNAME, "");
		accessId = sp.getString(KEY_ACCESS_ID, "");
		accessToken = sp.getString(KEY_ACCESS_TOKEN, "");
		multiSelectedItems = new HashSet<DirItemBean>();
		mAlertdialog = new CustomDialog(mParent, new MainActivityIPreventPenetrate());
	}

	private String tag = "cloudboxUtil";

	SharePopupWindow sharePopupWindow;

	/**
	 * @Title: getSubMenuClickListener
	 * @Description: 获取子菜单点击回调接口
	 * @author: leobert.lan
	 * @return
	 */
	public SubMenuClickListener getSubMenuClickListener() {
		return new SubMenuClickListener() {

			@Override
			public void onShareClick(DirItemBean item) {
				View view = new View(mParent);
				sharePopupWindow = new SharePopupWindow(mParent, item.getPath(), new MainActivityIPreventPenetrate());
				sharePopupWindow.setAnimationStyle(R.style.iOSActionSheet);
				sharePopupWindow.setOutsideClickDismiss();
				sharePopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
			}

			@Override
			public void onDownLoadClick(DirItemBean item) {
				getActivity().reportCountEvent(IUmengEventKey.CALC_CB_DOWNLOAD);
				ArrayList<DirItemBean> items = new ArrayList<DirItemBean>();
				items.add(item);
				mActivity.getDownloadService().startDownLoadJob(items, mActivity.getIndividualFolder().toString());
			}

			@Override
			public void onRenameClick(DirItemBean item) {
				getActivity().reportCountEvent(IUmengEventKey.CALC_CB_RENAME);
				String msg = JSON.toJSONString(item);
				Intent i = new Intent(mActivity, RenameActivity.class);
				i.putExtra("msg", msg);
				mActivity.startActivity(i);
			}

			@Override
			public void onMoveClick(DirItemBean item) {
				getActivity().reportCountEvent(IUmengEventKey.CALC_CB_MOVE);
				Intent i = new Intent(mActivity, MoveActivity.class);
				String str = JSON.toJSONString(item);
				ArrayList<String> temp = new ArrayList<String>();
				temp.add(str);
				i.putStringArrayListExtra(MoveActivity.MESSAGE_KEY, temp);
				mActivity.startActivity(i);
			}

			@Override
			public void onDeleteClick(final DirItemBean item) {
				getActivity().reportCountEvent(IUmengEventKey.CALC_CB_DEL);
				CustomDialog dialog = getDeleteAlertDialog(R.string.string_delete_title, R.string.string_cancel,
						R.string.string_sure);
				dialog.setPositiveClickListener(new OnPositiveClickListener() {

					@Override
					public void onPositiveClick() {
						delete(item);
					}
				});
				dialog.show();
			}
		};
	}

	/**
	 * @Title: getFilterToggleListener
	 * @Description: ToggleFilter的回调接口
	 * @author: zhangbin
	 * @return
	 */
	public OnCheckedChangeListener getFilterToggleListener() {

		return new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					mActivity.getmTypeFilterGroup().setVisibility(View.VISIBLE);
					mActivity.getmTypeFilterGroup().bringToFront();
				} else {
					mActivity.getmTypeFilterGroup().setVisibility(View.GONE);
				}
			}
		};
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (sharePopupWindow != null && sharePopupWindow.isShowing()) {
			sharePopupWindow.dismiss();
			sharePopupWindow = null;
		}
		return false;
	}

	/**
	 * @Title: getDeleteItemEntity
	 * @Description: 删除消息体
	 * @author: leobert.lan
	 * @param item
	 * @return
	 */
	protected StringEntity getDeleteItemEntity(DirItemBean item) {
		JSONObject jObj = new JSONObject();
		JSONArray jArray = new JSONArray();
		StringEntity ret = null;
		try {
			jArray.add(item.getPath());
			jObj.put("path", jArray);
			ret = new StringEntity(jObj.toString(), HTTP.UTF_8);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Log.d(tag, "entity:" + jObj.toString());
		return ret;
	}

	/**
	 * @Title: getDeleteItemEntity
	 * @Description: 获取批量删除的entity
	 * @author: leobert.lan
	 * @param items
	 * @return
	 */
	protected StringEntity getDeleteItemEntity(ArrayList<String> items) {
		JSONObject jObj = new JSONObject();
		JSONArray jArray = new JSONArray();
		StringEntity ret = null;
		for (int i = 0; i < items.size(); i++) {
			try {
				jArray.add(items.get(i));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		jObj.put("path", jArray);
		try {
			ret = new StringEntity(jObj.toString(), HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Log.d(tag, "entity:" + jObj.toString());
		return ret;
	}

	private Operate mOperate = Operate.reset;

	/**
	 * @Title: performOperate
	 * @Description: 执行操作
	 * @author: leobert.lan
	 * @param operate
	 */
	public void performOperate(Operate operate, String nextSubPath) {
		switch (operate) {
		case pull_to_refresh:
			this.mOperate = Operate.pull_to_refresh;
			// 获取本路径第一页数据
			mActivity.mUserDataDirectionUtil.getListData();
			mActivity.getPullRefreshListView().setMode(Mode.BOTH);
			break;
		case pull_to_add:
			this.mOperate = Operate.pull_to_add;
			// 获取下一页数据
			addNextPage();
			break;
		case click_to_access:
			this.mOperate = Operate.click_to_access;
			// 设定下一级目录，获取下一页
			mActivity.mUserDataDirectionUtil.appendNext(nextSubPath);
			mActivity.mUserDataDirectionUtil.getListData();
			mActivity.getPullRefreshListView().setMode(Mode.BOTH);
			break;
		case click_to_goback:
			this.mOperate = Operate.click_to_goback;
			mActivity.getPullRefreshListView().setMode(Mode.BOTH);
			// 此处不再验证根目录（外层验证），返回上一层获取第一页数据
			mActivity.mUserDataDirectionUtil.goBack();
			break;
		default:
			break;
		}
	}

	private void addNextPage() {
		mActivity.getPullRefreshListView().onRefreshComplete();
		if (dirPagination == null) {
			// 本情况专治上来没网还上拉的手贱者
			ToastUtil.show(mActivity, R.string.no_internet, Duration.s);
			return;
		}
		if (mActivity.mAdapter.getCount() < dirPagination.getTotalItems()) {
			mActivity.mUserDataDirectionUtil.getListData(IKeyManager.UserFolderList.TYPE_ALL,
					Integer.parseInt(dirPagination.getNext()), dirPagination.getPagesize(), null);
			mActivity.getPullRefreshListView().setMode(Mode.BOTH);
		} else {
			ToastUtil.show(mActivity, R.string.no_more_toAdd, Duration.s);
			mActivity.getPullRefreshListView().setMode(Mode.PULL_FROM_START);
		}
	}

	private DirPaginationBean dirPagination = null;

	public IGetList getOperateCallBack() {
		return new IGetList() {

			@Override
			public void onFinish(ArrayList<DirItemBean> list, DirPaginationBean dirPagination) {

				if (mOperate == Operate.pull_to_add) {
					mActivity.mAdapter.AddAll(list);
					mOperate = Operate.reset;
				} else {
					mActivity.mAdapter.ReplaceAll(list);
					mOperate = Operate.reset;

					if (mActivity.isMultiSelectMode()) {
						// 多选状态刷新取消所有已选数据，恢复原始状态
						removeAllSelceted();
						mActivity.initMultiSelectModeWhileFresh();
					}
				}
				mActivity.prepareEmptyView();
				CloudBoxUtil.this.dirPagination = dirPagination;
				mActivity.getPullRefreshListView().onRefreshComplete();
			}

			@Override
			public void onFailed(int httpStatus) {
				mOperate = Operate.reset;
				if (httpStatus == 0) {
					mActivity.prepareFailureView();
				} else {
					mActivity.prepareEmptyView();
				}
				mActivity.mAdapter.Clear();
				mActivity.getPullRefreshListView().onRefreshComplete();
				dirPagination = null;
			}
		};
	}

	/**
	 * @Title: delete
	 * @Description: 删除单个
	 * @author: leobert.lan
	 * @param item
	 */
	public void delete(DirItemBean item) {
		String url = getDeleteUrl(item.getType());
		StringEntity entity = getDeleteItemEntity(item);
		delete(url, entity);
	}

	private String getDeleteUrl(String type) {
		String url = IUrlManager.Delete.DOMAIN + IUrlManager.Delete.ADDRESS + username;
		if (type.equals(TYPE_FILE))
			url += IUrlManager.Delete.FUNCTION_FILE;
		else
			url += IUrlManager.Delete.FUNCTION_FOLDER;
		url += "?access_token=" + accessToken + "&access_id=" + accessId;
		Log.d(tag, "url:" + url);
		return url;
	}

	/**
	 * @ClassName: Operate
	 * @Description: 操作类型
	 * @date 2015年11月30日 下午3:33:16
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	public enum Operate {
		/**
		 * pull_to_refresh:下拉刷新
		 */
		pull_to_refresh,

		/**
		 * pull_to_add:上拉加载
		 */
		pull_to_add,

		/**
		 * click_to_access:点击进入下一级目录
		 */
		click_to_access,

		/**
		 * click_to_goback:回到上一级目录
		 */
		click_to_goback,

		/**
		 * reset:习惯给个默认值
		 */
		reset;

	}

	public void setOperate(Operate op) {
		this.mOperate = op;
	}

	private HashSet<DirItemBean> multiSelectedItems;

	/**
	 * @Title: addSelectedItem
	 * @Description: 添加一条选中的
	 * @author: leobert.lan
	 * @param item
	 */
	public void addSelectedItem(DirItemBean item) {
		multiSelectedItems.add(item);
	}

	/**
	 * @Title: removeSelectedItem
	 * @Description: 移除一条选中的
	 * @author: leobert.lan
	 * @param item
	 */
	public void removeSelectedItem(DirItemBean item) {
		// if (!multiSelectedItems.contains(item))
		// throw new IllegalArgumentException(
		// "the argument you give is illegel,check the whole process");
		if (multiSelectedItems.contains(item))
			multiSelectedItems.remove(item);
	}

	/**
	 * @Title: removeAllSelceted
	 * @Description: 移除全部
	 * @author: leobert.lan
	 */
	public void removeAllSelceted() {
		multiSelectedItems.clear();
	}

	/**
	 * @ClassName: OnItemSelectedChangedListener
	 * @Description: 选中状态变化回调接口
	 * @date 2016年1月5日 下午4:16:30
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	public interface OnItemSelectedChangedListener {
		void OnItemSelectedChanged(DirItemBean item, boolean isSelected);
	}

	/**
	 * @Title: multiDelete
	 * @Description: 批量删除
	 * @author: leobert.lan
	 */
	public void multiDelete() {
		if (multiSelectedItems.size() == 0) {
			ToastUtil.show(mActivity, R.string.cloudbox_multi_null1, Duration.s);
			return;
		}
		// 取消的时候不退出多选视图
		CustomDialog dialog = getDeleteAlertDialog(R.string.string_delete_multi, R.string.string_cancel,
				R.string.string_sure);
		dialog.setPositiveClickListener(new OnPositiveClickListener() {

			@Override
			public void onPositiveClick() {
				Log.d("amsg", "count:" + multiSelectedItems.size());
				Iterator<DirItemBean> i = multiSelectedItems.iterator();
				ArrayList<String> folders = new ArrayList<String>();
				ArrayList<String> files = new ArrayList<String>();
				while (i.hasNext()) {
					DirItemBean bean = i.next();
					if (bean.getType().equals(TYPE_FILE)) {
						files.add(bean.getPath());
					} else if (bean.getType().equals(TYPE_FOLDER)) {
						folders.add(bean.getPath());
					}
				}
				if (folders.size() > 0) {
					String url = getDeleteUrl(TYPE_FOLDER);
					StringEntity entity = getDeleteItemEntity(folders);
					delete(url, entity);
				}
				if (files.size() > 0) {
					String url = getDeleteUrl(TYPE_FILE);
					StringEntity entity = getDeleteItemEntity(files);
					delete(url, entity);
				}
				finishMultiOperate();
			}
		});
		dialog.show();
	}

	/**
	 * @Title: multiDownload
	 * @Description: 批量下载，TODO通知
	 * @author: leobert.lan
	 * @return false means containing folder in selected items,you should give
	 *         an AlertDialog back to user true means ready to download
	 */
	public boolean multiDownload() {
		String localPath = mActivity.getIndividualFolder().toString();
		ArrayList<DirItemBean> items = new ArrayList<DirItemBean>();
		Iterator<DirItemBean> i = multiSelectedItems.iterator();
		while (i.hasNext()) {
			DirItemBean bean = i.next();
			if (bean.getType().equals(TYPE_FOLDER)) {
				mAlertdialog.setTitle(R.string.string_dialog_prompt);
				mAlertdialog.setContent(R.string.download_refuse_folder);
				mAlertdialog.setPositiveButton(R.string.string_sure);
				mAlertdialog.setPositiveClickListener(new OnPositiveClickListener() {

					@Override
					public void onPositiveClick() {
						mActivity.getMultiSelect().setChecked(true);
					}
				});
				mAlertdialog.showSingle();
				finishMultiOperate();
				return false;
			}
			items.add(bean);
		}
		if (items.size() > 0) {
			finishMultiOperate();
			MyAsyncJobs asyncJobs = new MyAsyncJobs(items, localPath, MyAsyncJobs.operate_download);
			new Thread(asyncJobs).start();
		} else {
			ToastUtil.show(mParent, R.string.cloudbox_multi_null2, Duration.s);
		}

		return true;
	}

	/**
	 * @Title: multiMove
	 * @Description: 批量移动
	 * @author: leobert.lan
	 */
	public void multiMove() {
		Intent intent = new Intent(mActivity, MoveActivity.class);
		Iterator<DirItemBean> i = multiSelectedItems.iterator();
		ArrayList<String> temp = new ArrayList<String>();
		while (i.hasNext()) {
			String str = JSON.toJSONString(i.next());
			temp.add(str);
		}
		if (temp.size() > 0) {
			intent.putStringArrayListExtra(MoveActivity.MESSAGE_KEY, temp);
			finishMultiOperate();
			mActivity.startActivity(intent);
		} else
			ToastUtil.show(mParent, R.string.cloudbox_multi_null1, Duration.s);

	}

	public void multiShare() {
		ToastUtil.show(mActivity, R.string.function_notcomplete, Duration.s);
		// TODO 如果不退出视图
		return;
		// finishMultiOperate();
	}

	/**
	 * @Title: finishMultiOperate
	 * @Description: 关闭多选视图
	 * @author: leobert.lan
	 */
	private void finishMultiOperate() {
		mActivity.getMultiSelect().setChecked(false);
	}

	/**
	 * @Title: delete
	 * @Description: 发出request
	 * @author: leobert.lan
	 * @param url
	 * @param entityString
	 */
	private void delete(String url, StringEntity entityString) {
		mParent.showWaitView(true);
		httpUtil.delete(mActivity, url, entityString, "application/json", new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// 不需要cancelWaitView
				performOperate(Operate.pull_to_refresh, null);
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				mParent.cancelWaitView();
				HttpRequestFailureUtil failureUtil = new HttpRequestFailureUtil(mActivity);
				failureUtil.handleFailureWithCode(arg0, false);
				Toast.makeText(mActivity, "删除失败、请稍后再试", Toast.LENGTH_SHORT).show();
				DLog.e(CloudBoxUtil.class, new LogLocation(), "delete file failure");
			}
		});
	}

	/**
	 * @Title: getDeleteAlertDialog
	 * @Description: 获取删除的警告dialog
	 * @author: leobert.lan
	 * @param contentRid
	 * @param negRid
	 * @param posiRid
	 * @return
	 */
	private CustomDialog getDeleteAlertDialog(int contentRid, int negRid, int posiRid) {
		CustomDialog dialog = new CustomDialog(mParent, new MainActivityIPreventPenetrate());
		dialog.setContent(contentRid);
		dialog.setNegativeButton(negRid);
		dialog.setPositiveButton(posiRid);
		return dialog;
	}

	private class MyAsyncJobs implements Runnable {
		// public final static int operate_delete = 0;

		public final static int operate_download = 1;

		private final ArrayList<DirItemBean> items;

		private final int operate;

		private final String localpath;

		public MyAsyncJobs(ArrayList<DirItemBean> items, String localpath, int operate) {
			this.items = items;
			this.operate = operate;
			this.localpath = localpath;
		}

		@Override
		public void run() {
			Looper.prepare();

			if (operate == operate_download)
				mActivity.getDownloadService().startDownLoadJob(items, localpath);
			Looper.loop();
		}

	}

	public void destroy() {
		httpUtil.getClient().cancelRequests(mActivity, true);
	}

	public UMengActivity getActivity() {
		return mActivity;
	}
}
