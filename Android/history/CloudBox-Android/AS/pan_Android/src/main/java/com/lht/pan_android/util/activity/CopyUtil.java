package com.lht.pan_android.util.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import com.alibaba.fastjson.JSONObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.activity.asyncProtected.CopyActivity;
import com.lht.pan_android.bean.DirItemBean;
import com.lht.pan_android.bean.DirPaginationBean;
import com.lht.pan_android.bean.ShareItemBean;
import com.lht.pan_android.util.ToastUtil;
import com.lht.pan_android.util.ToastUtil.Duration;
import com.lht.pan_android.util.activity.CloudBoxUtil.Operate;
import com.lht.pan_android.util.dir.ABSDirectionUtil.IGetList;
import com.lht.pan_android.util.internet.HttpRequestFailureUtil;
import com.lht.pan_android.util.internet.HttpUtil;
import com.lht.pan_android.util.string.StringUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

/**
 * @ClassName: CloudBoxUtil
 * @Description: TODO
 * @date 2015年11月30日 下午3:07:31
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class CopyUtil implements IKeyManager.UserFolderList, IKeyManager.Token, IUrlManager.CreateNewFolder {

	private final CopyActivity mActivity;

	private HttpUtil httpUtil = new HttpUtil();

	private String username = "";

	private String accessId = "";

	private String accessToken = "";

	public CopyUtil(CopyActivity copyActivity) {
		this.mActivity = copyActivity;
		httpUtil = new HttpUtil();
		SharedPreferences sp = copyActivity.getSharedPreferences(SP_TOKEN, Context.MODE_PRIVATE);
		username = sp.getString(KEY_USERNAME, "");
		accessId = sp.getString(KEY_ACCESS_ID, "");
		accessToken = sp.getString(KEY_ACCESS_TOKEN, "");
	}

	private String tag = "CopyUtil";

	private Operate mOperate = Operate.reset;

	/**
	 * @Title: performOperate
	 * @Description: 执行操作 TODO 补全操作逻辑
	 * @author: leobert.lan
	 * @param operate
	 */
	public void performOperate(Operate operate, String nextSubPath) {
		this.performOperate(operate, nextSubPath, null);
	}

	public void performOperate(Operate operate, String nextSubPath, String absolutepath) {
		switch (operate) {
		case pull_to_refresh:
			this.mOperate = Operate.pull_to_refresh;
			mActivity.mCopyDataDirectionUtil.getListData();
			mActivity.getPullRefreshListView().setMode(Mode.BOTH);
			break;
		case pull_to_add:
			this.mOperate = Operate.pull_to_add;
			addNextPage();
			break;
		case click_to_access:
			this.mOperate = Operate.click_to_access;
			mActivity.mCopyDataDirectionUtil.appendNext(nextSubPath);
			mActivity.mCopyDataDirectionUtil.getListData(true, absolutepath);
			mActivity.getPullRefreshListView().setMode(Mode.BOTH);
			break;
		case click_to_goback:
			this.mOperate = Operate.click_to_goback;
			mActivity.getPullRefreshListView().setMode(Mode.BOTH);
			mActivity.mCopyDataDirectionUtil.goBack();
			break;
		default:
			break;
		}
	}

	private void addNextPage() {
		mActivity.getPullRefreshListView().onRefreshComplete();
		if (dirPagination == null) {
			ToastUtil.show(mActivity, R.string.no_internet, Duration.s);
			return;
		}
		if (mActivity.mCopyListAdapter.getCount() < dirPagination.getTotalItems()) {
			mActivity.mCopyDataDirectionUtil.getListData(IKeyManager.UserFolderList.TYPE_ALL,
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
					mActivity.mCopyListAdapter.AddAll(list);
					mOperate = Operate.reset;
				} else {
					mActivity.mCopyListAdapter.ReplaceAll(list);
					mOperate = Operate.reset;
				}
				CopyUtil.this.dirPagination = dirPagination;
				mActivity.getPullRefreshListView().onRefreshComplete();
			}

			@Override
			public void onFailed(int httpStatus) {
				mOperate = Operate.reset;
				mActivity.mCopyListAdapter.Clear();
				mActivity.getPullRefreshListView().onRefreshComplete();
				dirPagination = null;
			}
		};
	}

	/**
	 * @Title: createNewFolder
	 * @Description: 创建文件夹的网络请求
	 * @author: zhangbin
	 * @param parentPath
	 * @param name
	 */
	public void createNewFolder(String parentPath, String name) {

		if (!StringUtil.JudgeFileName(mActivity, name)) {
			return;
		}
		httpUtil.postWithEntity(mActivity, getUrl(), getEntity(parentPath, name), "application/json",
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						mActivity.hideModifyView();
						performOperate(Operate.pull_to_refresh, null);
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
						ToastUtil.show(mActivity, R.string.string_folder_create_failure, Duration.l);
						HttpRequestFailureUtil failureUtil = new HttpRequestFailureUtil(mActivity);
						failureUtil.handleFailureWithCode(arg0, false);
					}
				});

	}

	/**
	 * @Title: getUrl
	 * @Description: 创建文件夹的URL
	 * @author: zhangbin
	 * @return
	 */
	private String getUrl() {
		String url = DOMAIN + ADDRESS + username + FUNCTION;
		url += "?access_token=" + accessToken + "&access_id=" + accessId;
		return url;
	}

	/**
	 * @Title: getEntity
	 * @Description: 创建文件夹的请求体
	 * @author: zhangbin
	 * @param parentPath
	 * @param name
	 * @return
	 */
	private StringEntity getEntity(String parentPath, String name) {
		JSONObject jObj = new JSONObject();
		String path = null;
		if (parentPath.endsWith("/"))
			path = parentPath + name;
		else
			path = parentPath + "/" + name;
		jObj.put("path", path);
		StringEntity ret = null;
		try {
			ret = new StringEntity(jObj.toJSONString(), HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * @Title: move
	 * @Description:复制的网络请求
	 * @author: zhangbin
	 * @param filesNeedToMove
	 * @param pathToMove
	 */
	public void move(ArrayList<ShareItemBean> filesNeedToMove, String pathToMove) {

		String url = IUrlManager.ShareSaveAs.DOMAIN + IUrlManager.ShareSaveAs.MINEADDRESS
				+ filesNeedToMove.get(0).getOwner() + IUrlManager.ShareSaveAs.SHARE
				+ filesNeedToMove.get(0).getShareId() + IUrlManager.ShareSaveAs.FUNCTION;
		url += "?access_token=" + accessToken + "&access_id=" + accessId + "&queryUsername=" + username;

		StringEntity entity = getEntity(filesNeedToMove, pathToMove);
		httpUtil.postWithParams(mActivity, url, entity, "application/json", new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				mActivity.finish();
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				// TODO hard-code
				if (arg0 == 400) {
					Toast.makeText(mActivity, "目录中包含同名文件，请重新选择！", Toast.LENGTH_SHORT).show();
				} else if (arg0 == 404) {
					Toast.makeText(mActivity, "该分享已失效，无法转存", Toast.LENGTH_SHORT).show();
					mActivity.finish();
				}
				HttpRequestFailureUtil failureUtil = new HttpRequestFailureUtil(mActivity);
				failureUtil.handleFailureWithCode(arg0, false);
			}
		});
	}

	/**
	 * @Title: getEntity
	 * @Description: 复制的请求体
	 * @author: zhangbin
	 * @param filesNeedToMove
	 * @param pathToMove
	 * @return
	 */
	private StringEntity getEntity(ArrayList<ShareItemBean> filesNeedToMove, String pathToMove) {
		JSONObject jObj = new JSONObject();
		jObj.put(IUrlManager.ShareSaveAs.KEY_MOVETO, pathToMove);
		jObj.put(IUrlManager.ShareSaveAs.KEY_ABSOLUTEPATH, filesNeedToMove.get(0).getPath());
		StringEntity ret = null;
		try {
			ret = new StringEntity(jObj.toJSONString(), HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}
}
