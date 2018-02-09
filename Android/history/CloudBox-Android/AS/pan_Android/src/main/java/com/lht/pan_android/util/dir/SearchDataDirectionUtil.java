package com.lht.pan_android.util.dir;

import java.util.ArrayList;

import org.apache.http.Header;

import com.alibaba.fastjson.JSON;
import com.lht.pan_android.Interface.IAsyncWithProgressbar;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.activity.asyncProtected.SearchActivity;
import com.lht.pan_android.bean.DirBean;
import com.lht.pan_android.bean.DirItemBean;
import com.lht.pan_android.bean.DirPaginationBean;
import com.lht.pan_android.util.activity.CloudBoxUtil.Operate;
import com.lht.pan_android.util.internet.HttpRequestFailureUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @ClassName: SearchDataDirectionUtil
 * @Description: 获取搜索列表
 * @date 2016年1月7日 下午3:02:26
 * 
 * @author zhangbin
 * @version 1.0
 * @since JDK 1.6
 */
public class SearchDataDirectionUtil extends ABSDirectionUtil {

	private String name = null;

	private SearchActivity mActivity;

	private String access_id, access_token;

	public SearchDataDirectionUtil(Context ctx, IAsyncWithProgressbar iView) {
		super(ctx, iView);
		mActivity = (SearchActivity) ctx;
	}

	public void setSearchContent(String name) {
		this.name = name;
	}

	public void addOneFlag() {
		backFlag.add(false);
	}

	private ArrayList<Boolean> backFlag = new ArrayList<Boolean>();

	public void setSearchView() {
		mCurrentPath = "/";
		backFlag.clear();
		backFlag.add(true);
	}

	public boolean shouldFinish() {
		if (backFlag.size() == 0)
			return true;
		int lastIndex = backFlag.size() - 1;
		return backFlag.get(lastIndex);
	}

	private boolean checkPreIsSearchView() {
		int _index = backFlag.size() - 2;
		return backFlag.get(_index);
	}

	private SharedPreferences sharedPreferences;

	private String REQUEST_URL = null;

	private int page = 0;

	public void getSearchData() {
		mActivity.mSearchUtil.updateOperate(Operate.pull_to_refresh);
		this.page = 1;
		this.getSearchData(IKeyManager.UserFolderList.TYPE_ALL, 1, 50);
	}

	public void getSearchDataNextpage() {
		mActivity.mSearchUtil.updateOperate(Operate.pull_to_add);
		page++;
		this.getSearchData(IKeyManager.UserFolderList.TYPE_ALL, page, 50);
	}

	private void init() {
		cancelAllCalls();
		// TODO MVP 模式
		mActivity.change2NormalView();
		if (sharedPreferences == null) {
			sharedPreferences = mContext.getSharedPreferences(IKeyManager.Token.SP_TOKEN, Context.MODE_PRIVATE);
		}
		access_id = sharedPreferences.getString(IKeyManager.Token.KEY_ACCESS_ID, "");
		access_token = sharedPreferences.getString(IKeyManager.Token.KEY_ACCESS_TOKEN, "");

	}

	public void getSearchData(String Type, int page, int pagesize) {

		init();

		REQUEST_URL = IUrlManager.SearchListUrl.DOMAIN + IUrlManager.SearchListUrl.ADDRESS
				+ sharedPreferences.getString(IKeyManager.Token.KEY_USERNAME, "") + IUrlManager.SearchListUrl.FUNCTION;

		RequestParams params = new RequestParams();
		params.add(IKeyManager.SearchList.NAME, name);
		params.add(IKeyManager.SearchList.PARAM_PATH, "/");
		params.add(IKeyManager.SearchList.PARAM_TYPE, "2");
		params.add(IKeyManager.SearchList.PARAM_PAGE, String.valueOf(page));
		params.add(IKeyManager.SearchList.PARAM_PAGESIZE, String.valueOf(pagesize));
		params.add(IKeyManager.SearchList.PARAM_DESCENDANTFILES,
				IKeyManager.UserFolderList.DEFAULT_VALUE_DESCENDANTFILES);
		params.add(IKeyManager.Token.KEY_ACCESS_ID, access_id);
		params.add(IKeyManager.Token.KEY_ACCESS_TOKEN, access_token);

		mHttpUtil.getWithParams(mContext, REQUEST_URL, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				String FileListData = new String(arg2);

				DirBean mDirBean = JSON.parseObject(FileListData, DirBean.class);
				String[] items = mDirBean.getItems();
				DirPaginationBean dirPaginationBean = JSON.parseObject(mDirBean.getPagination(),
						DirPaginationBean.class);
				ArrayList<DirItemBean> list = new ArrayList<DirItemBean>();
				for (int i = 0; i < items.length; i++) {
					DirItemBean mDirItemBean = JSON.parseObject(items[i], DirItemBean.class);
					mDirItemBean.setOpen(false);
					list.add(mDirItemBean);
				}
				if (mGetListImpl == null)
					throw new NullPointerException("callback is null");
				mGetListImpl.onFinish(list, dirPaginationBean);

				((SearchActivity) mContext).cancelWaitView();
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				arg3.printStackTrace();
				if (mGetListImpl == null)
					throw new NullPointerException("callback is null");
				mGetListImpl.onFailed(arg0);
				((SearchActivity) mContext).cancelWaitView();
			}
		});
	}

	public void getListData(int page, int pagesize, String absolutePath) {

		init();

		mCurrentPath = absolutePath;

		REQUEST_URL = IUrlManager.UserListUrl.DOMAIN + IUrlManager.UserListUrl.ADDRESS
				+ sharedPreferences.getString(IKeyManager.Token.KEY_USERNAME, "") + IUrlManager.UserListUrl.FUNCTION_ALL
				+ "?";

		RequestParams params = new RequestParams();
		params.add(IKeyManager.SearchList.PARAM_PATH, mCurrentPath);
		params.add(IKeyManager.SearchList.PARAM_TYPE, "2");
		params.add(IKeyManager.SearchList.PARAM_PAGE, String.valueOf(page));
		params.add(IKeyManager.SearchList.PARAM_PAGESIZE, String.valueOf(pagesize));
		params.add(IKeyManager.SearchList.PARAM_DESCENDANTFILES,
				IKeyManager.UserFolderList.DEFAULT_VALUE_DESCENDANTFILES);
		params.add(IKeyManager.Token.KEY_ACCESS_ID, access_id);
		params.add(IKeyManager.Token.KEY_ACCESS_TOKEN, access_token);

		mHttpUtil.getWithParams(mContext, REQUEST_URL, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				String FileListData = new String(arg2);
				DirBean mDirBean = JSON.parseObject(FileListData, DirBean.class);
				String[] items = mDirBean.getItems();
				DirPaginationBean dirPaginationBean = JSON.parseObject(mDirBean.getPagination(),
						DirPaginationBean.class);
				ArrayList<DirItemBean> list = new ArrayList<DirItemBean>();
				for (int i = 0; i < items.length; i++) {
					DirItemBean mDirItemBean = JSON.parseObject(items[i], DirItemBean.class);
					mDirItemBean.setOpen(false);
					list.add(mDirItemBean);
				}
				if (mGetListImpl == null)
					throw new NullPointerException("callback is null");
				mGetListImpl.onFinish(list, dirPaginationBean);
				((SearchActivity) mContext).cancelWaitView();

			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				if (mGetListImpl == null)
					throw new NullPointerException("callback is null");
				mGetListImpl.onFailed(arg0);
				HttpRequestFailureUtil failureUtil = new HttpRequestFailureUtil(mContext);
				failureUtil.handleFailureWithCode(arg0, true);
				((SearchActivity) mContext).cancelWaitView();
			}
		});
	}

	public void getListData(int page, int pageSize) {
		this.getListData(page, pageSize, mCurrentPath);
	}

	@Override
	public void goBack() {
		// TODO
		if (checkPreIsSearchView()) {
			// 上一页是搜索视图，执行搜索（第一页）
			((SearchActivity) mContext).onSearchCall();
		} else {
			// 上一页是目录视图
			mCurrentPath = getPrePath();
			getListData(1, 50);
			int lastIndex = backFlag.size() - 1;
			backFlag.remove(lastIndex);
		}
	}

	@Override
	protected String getUrl(TypeFilter filter) {
		return null;
	}

	@Override
	protected void appendNext(String subPath) {

	}

	@Override
	@Deprecated
	/**
	 * use getListData(int page,int pageSize,String path) instead
	 */
	protected void getListData(String Type, int page, int pagesize, TypeFilter filter) {

	}

}
