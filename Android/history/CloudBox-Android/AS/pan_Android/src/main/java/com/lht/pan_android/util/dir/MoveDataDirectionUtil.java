package com.lht.pan_android.util.dir;

import java.util.ArrayList;

import org.apache.http.Header;

import com.alibaba.fastjson.JSON;
import com.lht.pan_android.Interface.IAsyncWithProgressbar;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.activity.asyncProtected.CopyActivity;
import com.lht.pan_android.activity.asyncProtected.MoveActivity;
import com.lht.pan_android.activity.asyncProtected.PathChooseActivity;
import com.lht.pan_android.bean.DirBean;
import com.lht.pan_android.bean.DirItemBean;
import com.lht.pan_android.bean.DirPaginationBean;
import com.lht.pan_android.util.internet.HttpRequestFailureUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @ClassName: MoveDataDirectionUtil
 * @Description: 获取移动列表
 * @date 2016年1月5日 上午9:46:12
 * 
 * @author zhangbin
 * @version 1.0
 * @since JDK 1.6
 */
public class MoveDataDirectionUtil extends ABSDirectionUtil {

	// private Context mContext;

	private SharedPreferences sharedPreferences;

	private boolean needCallBack = false;

	private String callBackArgument;

	public MoveDataDirectionUtil(Context ctx, IAsyncWithProgressbar iView) {
		super(ctx, iView);
	}

	public void updateCurrentPath(String path) {
		mCurrentPath = path;
	}

	public void getListData() {
		this.getListData(IKeyManager.UserFolderList.TYPE_FOLDER, 1, 50, getFilter());
	}

	public void getListData(boolean needCallBack, String callBackArgument) {
		this.needCallBack = needCallBack;
		this.callBackArgument = callBackArgument;
		getListData();
	}

	@Override
	public void goBack() {
		if (!isRoot()) {
			mCurrentPath = getPrePath();
			getListData();
		}
	}

	@Override
	protected String getUrl(TypeFilter filter) {
		if (filter == null)
			filter = getFilter();
		StringBuilder builder = new StringBuilder();
		builder.append(IUrlManager.UserListUrl.DOMAIN);
		builder.append(IUrlManager.UserListUrl.ADDRESS);
		builder.append(sharedPreferences.getString(IKeyManager.Token.KEY_USERNAME, ""));
		switch (filter) {
		case all:
			setFilter(TypeFilter.all);
			builder.append(IUrlManager.UserListUrl.FUNCTION_ALL);
			break;
		case image:
			setFilter(TypeFilter.image);
			builder.append(IUrlManager.UserListUrl.FUNCTION_IMAGE);
			break;
		case doc:
			setFilter(TypeFilter.doc);
			builder.append(IUrlManager.UserListUrl.FUNCTION_DOC);
			break;
		case audio:
			setFilter(TypeFilter.audio);
			builder.append(IUrlManager.UserListUrl.FUNCTION_AUDIO);
			break;
		case video:
			setFilter(TypeFilter.video);
			builder.append(IUrlManager.UserListUrl.FUNCTION_VIDEO);
			break;
		default:
			break;
		}

		return builder.toString();
	}

	@Override
	public void appendNext(String subPath) {
		if (checkPath(subPath)) {
			StringBuilder pathBuilder = new StringBuilder(mCurrentPath);
			if (mCurrentPath.endsWith(PATH_SEPARATOR))
				pathBuilder.append(subPath);
			else
				pathBuilder.append(PATH_SEPARATOR).append(subPath);
			mCurrentPath = pathBuilder.toString();
		}
	}

	@Override
	public void getListData(String Type, int page, int pagesize, TypeFilter filter) {
		cancelAllCalls();

		if (sharedPreferences == null) {
			sharedPreferences = mContext.getSharedPreferences(IKeyManager.Token.SP_TOKEN, Context.MODE_PRIVATE);
		}

		String access_id = sharedPreferences.getString(IKeyManager.Token.KEY_ACCESS_ID, "");
		String access_token = sharedPreferences.getString(IKeyManager.Token.KEY_ACCESS_TOKEN, "");

		RequestParams params = new RequestParams();

		params.add(IKeyManager.UserFolderList.PARAM_PATH, mCurrentPath);
		params.add(IKeyManager.UserFolderList.PARAM_TYPE, Type);
		params.add(IKeyManager.UserFolderList.PARAM_PAGE, String.valueOf(page));
		params.add(IKeyManager.UserFolderList.PARAM_PAGESIZE, String.valueOf(pagesize));
		params.add(IKeyManager.UserFolderList.PARAM_DESCENDANTFILES,
				IKeyManager.UserFolderList.DEFAULT_VALUE_DESCENDANTFILES);
		params.add(IKeyManager.Token.KEY_ACCESS_ID, access_id);
		params.add(IKeyManager.Token.KEY_ACCESS_TOKEN, access_token);

		mHttpUtil.getWithParams(mContext, getUrl(filter), params, new AsyncHttpResponseHandler() {

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
				// TODO 使用接口
				if (needCallBack) {
					if (mContext instanceof MoveActivity)
						((MoveActivity) mContext).updataBreadLine(callBackArgument);
					if (mContext instanceof PathChooseActivity)
						((PathChooseActivity) mContext).updataBreadLine(callBackArgument);
					if (mContext instanceof CopyActivity)
						((CopyActivity) mContext).updataBreadLine(callBackArgument);
					needCallBack = false;
					callBackArgument = null;
				}

			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				arg3.printStackTrace();
				if (mGetListImpl == null)
					throw new NullPointerException("callback is null");
				mGetListImpl.onFailed(arg0);
				HttpRequestFailureUtil failureUtil = new HttpRequestFailureUtil(mContext);
				failureUtil.handleFailureWithCode(arg0, true);
			}
		});
	}
}
