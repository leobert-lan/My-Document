package com.lht.pan_android.util.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.IPreventPenetrate;
import com.lht.pan_android.Interface.IUmengEventKey;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.Interface.MainActivityIPreventPenetrate;
import com.lht.pan_android.Interface.SubMenuClickListener;
import com.lht.pan_android.activity.BaseActivity;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.activity.asyncProtected.MoveActivity;
import com.lht.pan_android.activity.asyncProtected.SearchActivity;
import com.lht.pan_android.activity.others.RenameActivity;
import com.lht.pan_android.adapter.SearchHistroyAdapter;
import com.lht.pan_android.bean.DirItemBean;
import com.lht.pan_android.bean.DirPaginationBean;
import com.lht.pan_android.clazz.LRUCache;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.DLog.LogLocation;
import com.lht.pan_android.util.SPUtil;
import com.lht.pan_android.util.ToastUtil;
import com.lht.pan_android.util.ToastUtil.Duration;
import com.lht.pan_android.util.activity.CloudBoxUtil.Operate;
import com.lht.pan_android.util.dir.ABSDirectionUtil.IGetList;
import com.lht.pan_android.util.internet.HttpRequestFailureUtil;
import com.lht.pan_android.util.internet.HttpUtil;
import com.lht.pan_android.util.string.StringUtil;
import com.lht.pan_android.view.popupwins.CustomDialog;
import com.lht.pan_android.view.popupwins.CustomPopupWindow.OnPositiveClickListener;
import com.lht.pan_android.view.popupwins.SharePopupWindow;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

/**
 * @ClassName: SearchUtil
 * @Description: 搜索
 * @date 2016年1月8日 下午4:43:04
 * @author zhangbin
 * @version 1.0
 * @since JDK 1.6
 */
public class SearchUtil implements IKeyManager.UserFolderList, IKeyManager.Token {

	private final SearchActivity mActivity;

	private HttpUtil httpUtil = new HttpUtil();

	private String username = "";

	private String accessId = "";

	private String accessToken = "";

	private SharedPreferences searchHistroySp = null;

	private LRUCache<String, String> searchHis;

	public SearchUtil(SearchActivity activity) {
		this.mActivity = activity;
		httpUtil = new HttpUtil();
		SharedPreferences sp = activity.getSharedPreferences(SP_TOKEN, Context.MODE_PRIVATE);
		username = sp.getString(KEY_USERNAME, "");
		accessId = sp.getString(KEY_ACCESS_ID, "");
		accessToken = sp.getString(KEY_ACCESS_TOKEN, "");

		searchHistroySp = activity.getSharedPreferences(IKeyManager.SearchHistroySp.SP_SUFFIX + username,
				Context.MODE_PRIVATE);
		searchHis = new LRUCache<String, String>(IKeyManager.SearchHistroySp.COUNT);

		rebuildHisCache();
	}

	public void initHistroy(SearchHistroyAdapter adapter) {
		// TODO
		Log.e("lmsg", "call init search histroy");
		GetSearchHisHandler h = new GetSearchHisHandler(adapter);
		GetSearchHisThread t = new GetSearchHisThread(h);
		t.start();
	}

	private void rebuildHisCache() {
		String temp = searchHistroySp.getString(IKeyManager.SearchHistroySp.KEY_CONTENT, null);
		if (temp != null) {
			JSONArray jArray = JSON.parseArray(temp);
			int count = jArray.size();
			// for (int i = count - 1; i > -1; i--) {
			// String searchKey = jArray.getString(i);
			// // 键值一致
			// searchHis.put(searchKey, searchKey);
			// }
			for (int i = 0; i < count; i++) {
				String searchKey = jArray.getString(i);
				// 键值一致
				searchHis.put(searchKey, searchKey);
			}
		}
	}

	private String tag = "SearchUtil";

	private SharePopupWindow sharePopupWindow;

	/**
	 * @Title: getSubMenuClickListener
	 * @Description: 子菜单的点击回调接口
	 * @author: zhangbin
	 * @return
	 */
	public SubMenuClickListener getSubMenuClickListener() {
		return new SubMenuClickListener() {

			@Override
			public void onShareClick(DirItemBean item) {
				getActivity().reportCountEvent(IUmengEventKey.CALC_CB_SHARE);
				View view = new View(mActivity);
				String sharePath = null;
				try {
					sharePath = URLDecoder.decode(item.getPath(), "utf-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				sharePopupWindow = new SharePopupWindow(mActivity, sharePath, new MainActivityIPreventPenetrate());
				sharePopupWindow.setAnimationStyle(R.style.iOSActionSheet);
				sharePopupWindow.setOutsideClickDismiss();
				sharePopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
			}

			@Override
			public void onDownLoadClick(DirItemBean item) {
				getActivity().reportCountEvent(IUmengEventKey.CALC_CB_DOWNLOAD);
				ArrayList<DirItemBean> items = new ArrayList<DirItemBean>();
				items.add(item);
				mActivity.getDownServiceBinder().startDownLoadJob(items, BaseActivity.getIndividualFolder().toString());
			}

			@Override
			public void onRenameClick(DirItemBean item) {
				getActivity().reportCountEvent(IUmengEventKey.CALC_CB_RENAME);
				String msg = JSON.toJSONString(item);
				Intent i = new Intent(mActivity, RenameActivity.class);
				i.putExtra(RenameActivity.MSG, msg);
				mActivity.startActivity(i);
			}

			@Override
			public void onMoveClick(DirItemBean item) {
				getActivity().reportCountEvent(IUmengEventKey.CALC_CB_MOVE);
				Log.i(tag, "move");
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

	private Operate mOperate = Operate.reset;

	/**
	 * @Title: updateOperate
	 * @Description: 手动更新下operate
	 * @author: leobert.lan
	 * @param operate
	 */
	public void updateOperate(Operate operate) {
		this.mOperate = operate;
	}

	public void listPerformOperate(Operate operate, String nextSubPath, String absolutepath) {
		switch (operate) {
		case pull_to_refresh:
			this.mOperate = Operate.pull_to_refresh;
			mActivity.mSearchDataDirectionUtil.getListData(1, 50);
			mActivity.getPullRefreshListView().setMode(Mode.BOTH);

			break;
		case pull_to_add:
			this.mOperate = Operate.pull_to_add;
			addNextPage();
			break;
		case click_to_access:
			this.mOperate = Operate.click_to_access;
			mActivity.mSearchDataDirectionUtil.addOneFlag();
			mActivity.mSearchDataDirectionUtil.getListData(1, 50, absolutepath);
			mActivity.getPullRefreshListView().setMode(Mode.BOTH);
			break;
		case click_to_goback:
			this.mOperate = Operate.click_to_goback;
			mActivity.getPullRefreshListView().setMode(Mode.BOTH);
			mActivity.mSearchDataDirectionUtil.goBack();
			break;
		default:
			break;
		}
	}

	/**
	 * @Title: addNextPage
	 * @Description: folder内容添加下一页
	 * @author: zhangbin
	 */
	private void addNextPage() {
		mActivity.getPullRefreshListView().onRefreshComplete();
		if (dirPagination == null) {
			ToastUtil.show(mActivity, R.string.no_internet, Duration.s);
			return;
		}
		if (mActivity.mSearchAdapter.getCount() < dirPagination.getTotalItems()) {
			mActivity.mSearchDataDirectionUtil.getListData(Integer.parseInt(dirPagination.getNext()),
					dirPagination.getPagesize());
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
					mActivity.mSearchAdapter.AddAll(list);
					mOperate = Operate.reset;
				} else {
					mActivity.mSearchAdapter.ReplaceAll(list);
					mOperate = Operate.reset;
				}
				SearchUtil.this.dirPagination = dirPagination;

				// 这里简单粗暴的为搜索执行分页数据判断，切换模式
				if (mActivity.mSearchAdapter.getCount() < dirPagination.getTotalItems()) {
					mActivity.getPullRefreshListView().setMode(Mode.BOTH);
				} else {
					mActivity.getPullRefreshListView().setMode(Mode.PULL_FROM_START);
				}

				mActivity.getPullRefreshListView().onRefreshComplete();
			}

			@Override
			public void onFailed(int httpStatus) {
				mActivity.mSearchAdapter.Clear();
				mOperate = Operate.reset;
				mActivity.getPullRefreshListView().onRefreshComplete();
				dirPagination = null;
			}
		};
	}

	/**
	 * @Title: delete
	 * @Description: 删除操作
	 * @author: zhangbin
	 * @param item
	 */
	public void delete(DirItemBean item) {
		String url = getDeleteUrl(item.getType());
		StringEntity entity = getDeleteItemEntity(item);
		delete(url, entity);
	}

	/**
	 * @Title: getDeleteItemEntity
	 * @Description: 删除的消息体
	 * @author: zhangbin
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
		// Log.d(tag, "entity:" + jObj.toString());
		return ret;
	}

	/**
	 * @Title: getDeleteUrl
	 * @Description: 删除的URL
	 * @author: zhangbin
	 * @param type
	 * @return
	 */
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
	 * @Title: delete
	 * @Description: 删除的网络请求
	 * @author: zhangbin
	 * @param url
	 * @param entityString
	 */
	private void delete(String url, StringEntity entityString) {
		httpUtil.delete(mActivity, url, entityString, "application/json", new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				PullToRefreshListView lv = mActivity.getPullToRefreshListView();
				if (lv.getOnRefreshListener2() != null)
					lv.getOnRefreshListener2().onPullDownToRefresh(lv);
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				HttpRequestFailureUtil failureUtil = new HttpRequestFailureUtil(mActivity);
				failureUtil.handleFailureWithCode(arg0, false);
				DLog.d(SearchUtil.class, new LogLocation(), "删除文件失败，status：" + arg0);
			}
		});
	}

	/**
	 * @Title: getDeleteAlertDialog
	 * @Description: 删除的提示对话框
	 * @author: zhangbin
	 * @param contentRid
	 * @param negRid
	 * @param posiRid
	 * @return
	 */
	private CustomDialog getDeleteAlertDialog(int contentRid, int negRid, int posiRid) {
		CustomDialog dialog = new CustomDialog(mActivity, new IPreventPenetrate() {

			@Override
			public void preventPenetrate(Activity activity, boolean isProtectNeed) {
				((SearchActivity) activity).setActiveStateOfDispatchOnTouch(!isProtectNeed);
			}
		});
		dialog.setContent(contentRid);
		dialog.setNegativeButton(negRid);
		dialog.setPositiveButton(posiRid);
		return dialog;
	}

	public UMengActivity getActivity() {
		return mActivity;
	}

	private ArrayList<String> getHistroyData() {
		ArrayList<String> ret = new ArrayList<String>();
		for (Map.Entry<String, String> e : searchHis.getAll()) {
			ret.add(e.getValue());
		}
		return ret;
	}

	public void updateHistroy(String searchKey) {
		if (StringUtil.isEmpty(searchKey))
			return;
		searchHis.put(searchKey, searchKey);
	}

	public void writeOnfinish() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONArray jArray = new JSONArray();

				for (Map.Entry<String, String> e : searchHis.getAll()) {
					jArray.add(e.getValue());
				}
				SPUtil.modifyString(searchHistroySp, IKeyManager.SearchHistroySp.KEY_CONTENT, jArray.toJSONString());
			}
		}).start();
	}

	public class GetSearchHisThread extends Thread {

		private GetSearchHisHandler handler;

		public GetSearchHisThread(GetSearchHisHandler handler) {
			this.handler = handler;
		}

		@Override
		public void run() {
			// TODO
			Log.e("lmsg", "thread run");
			ArrayList<String> ret = getHistroyData();
			Log.e("lmsg", "ret size:" + ret.size());
			Message msg = new Message();
			msg.what = GetSearchHisHandler.MSG_OK;
			Bundle b = new Bundle();
			b.putStringArrayList(GetSearchHisHandler.KEY_DATA, ret);
			msg.setData(b);
			handler.sendMessage(msg);
			super.run();
		}
	}

	@SuppressLint("HandlerLeak")
	public class GetSearchHisHandler extends Handler {

		public static final String KEY_DATA = "data";
		private SearchHistroyAdapter adapter;

		public GetSearchHisHandler(SearchHistroyAdapter adapter) {
			this.adapter = adapter;
		}

		public final static int MSG_OK = 1;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == MSG_OK) {
				ArrayList<String> ret = msg.getData().getStringArrayList(KEY_DATA);
				// TODO
				Log.e("lmsg", "handler ret size:" + ret.size());
				adapter.ReplaceAll(ret, false);
				mActivity.change2NormalView();
			}
		}
	}
}
