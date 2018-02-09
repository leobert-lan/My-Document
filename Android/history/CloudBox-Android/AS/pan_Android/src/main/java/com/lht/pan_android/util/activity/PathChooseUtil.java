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
import com.lht.pan_android.activity.asyncProtected.PathChooseActivity;
import com.lht.pan_android.bean.DirItemBean;
import com.lht.pan_android.bean.DirPaginationBean;
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

/**
 * @ClassName: PathChooseUtil
 * @Description: TODO
 * @date 2016年1月8日 下午3:48:42
 * 
 * @author leobert.lan
 * @version 1.0
 */

public class PathChooseUtil implements IKeyManager.UserFolderList, IKeyManager.Token, IUrlManager.CreateNewFolder {

	private final PathChooseActivity mActivity;

	private HttpUtil httpUtil = new HttpUtil();

	private String username = "";

	private String accessId = "";

	private String accessToken = "";

	public PathChooseUtil(PathChooseActivity activity) {
		this.mActivity = activity;
		httpUtil = new HttpUtil();
		SharedPreferences sp = activity.getSharedPreferences(SP_TOKEN, Context.MODE_PRIVATE);
		username = sp.getString(KEY_USERNAME, "");
		accessId = sp.getString(KEY_ACCESS_ID, "");
		accessToken = sp.getString(KEY_ACCESS_TOKEN, "");
	}

	private String tag = "MoveUtil";

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
			mActivity.mMoveDataDirectionUtil.getListData();
			mActivity.getPullRefreshListView().setMode(Mode.BOTH);
			break;
		case pull_to_add:
			this.mOperate = Operate.pull_to_add;
			addNextPage();
			break;
		case click_to_access:
			this.mOperate = Operate.click_to_access;
			mActivity.mMoveDataDirectionUtil.appendNext(nextSubPath);
			mActivity.mMoveDataDirectionUtil.getListData(true, absolutepath);
			mActivity.getPullRefreshListView().setMode(Mode.BOTH);
			break;
		case click_to_goback:
			this.mOperate = Operate.click_to_goback;
			mActivity.getPullRefreshListView().setMode(Mode.BOTH);
			mActivity.mMoveDataDirectionUtil.goBack();
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
		if (mActivity.mAdapter.getCount() < dirPagination.getTotalItems()) {
			mActivity.mMoveDataDirectionUtil.getListData(IKeyManager.UserFolderList.TYPE_ALL,
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
				}
				PathChooseUtil.this.dirPagination = dirPagination;
				mActivity.getPullRefreshListView().onRefreshComplete();
			}

			@Override
			public void onFailed(int httpStatus) {
				mOperate = Operate.reset;
				mActivity.mAdapter.Clear();
				mActivity.getPullRefreshListView().onRefreshComplete();
				dirPagination = null;
			}
		};
	}

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

	private String getUrl() {
		String url = DOMAIN + ADDRESS + username + FUNCTION;
		url += "?access_token=" + accessToken + "&access_id=" + accessId;
		return url;
	}

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

}
