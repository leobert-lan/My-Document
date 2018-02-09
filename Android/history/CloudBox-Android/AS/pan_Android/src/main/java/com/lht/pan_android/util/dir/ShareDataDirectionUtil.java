package com.lht.pan_android.util.dir;

import java.util.ArrayList;

import org.apache.http.Header;

import com.alibaba.fastjson.JSON;
import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.activity.ViewPagerItem.ShareActivity;
import com.lht.pan_android.activity.asyncProtected.MainActivity;
import com.lht.pan_android.bean.DirPaginationBean;
import com.lht.pan_android.bean.ShareBean;
import com.lht.pan_android.bean.ShareItemBean;
import com.lht.pan_android.bean.ShareToBean;
import com.lht.pan_android.util.ToastUtil;
import com.lht.pan_android.util.ToastUtil.Duration;
import com.lht.pan_android.util.internet.HttpRequestFailureUtil;
import com.lht.pan_android.util.internet.HttpUtil;
import com.lht.pan_android.util.string.StringUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * @ClassName: UserDataDirectionUtil
 * @Description: 用户数据列表获取数据方法类
 * @date 2015年11月30日 上午10:30:12
 * @author leobert.lan
 * @version 1.0
 */
public class ShareDataDirectionUtil {

	private final static String tag = "ShareDataDirectionUtil";
	/**
	 * ownerName:获取分享人的姓名
	 */
	private String ownerName = null;
	/**
	 * REQUEST_URL:获取分享的类型
	 */
	private String REQUEST_URL = null;
	/**
	 * shareId:获取分享的信息的ID
	 */
	private String shareId = null;

	private String mCurrentPath = "/";
	private final String PATH_SEPARATOR = "/";

	private final Context mContext;

	private final HttpUtil mHttpUtil;

	public ShareDataDirectionUtil(Context ctx) {
		mContext = ctx;
		mHttpUtil = new HttpUtil();

		((MainActivity) ((ShareActivity) mContext).getParent()).showWaitView(true);
	}

	public String getCurrentPath() {
		return this.mCurrentPath;
	}

	public String getPrePath() {
		String path = StringUtil.splitLastSubPath(mCurrentPath);
		return StringUtil.isEmpty(path) ? PATH_SEPARATOR : path;
	}

	public void appendNext(String subPath) {
		String path[] = subPath.split("/");
		shareId = path[path.length - 2];
		ownerName = path[path.length - 1];
		String pathString = subPath.substring(0, subPath.lastIndexOf("/"));
		mCurrentPath = pathString.substring(0, pathString.lastIndexOf("/"));
	}

	public void goBack() {
		if (!isRoot()) {
			mCurrentPath = getPrePath();
			getListData();
		}
	}

	public boolean isRoot() {
		if (mCurrentPath.equals("/"))
			return true;
		else
			return false;
	}

	private SharedPreferences sharedPreferences;

	public void getListData() {
		this.getListData(IKeyManager.UserFolderList.TYPE_ALL, 1, 50);
	}

	/**
	 * @Title: getListData
	 * @Description: 获取数据
	 * @author: zhangbin
	 * @param Type
	 *            ：文件类型， "0":文件"1":文件夹"2":文件和文件夹
	 * @param page
	 *            :页数
	 * @param pagesize
	 *            ：页面大小
	 */
	public void getListData(String Type, int page, int pagesize) {
		// 先把之前的请求全部cancel
		cancelAllCalls();

		if (!((ShareActivity) mContext).isFinishing()) {
			((MainActivity) ((ShareActivity) mContext).getParent()).showWaitView(true);
		}

		if (sharedPreferences == null) {
			sharedPreferences = mContext.getSharedPreferences(IKeyManager.Token.SP_TOKEN, Context.MODE_PRIVATE);
		}

		getUrl();

		String access_id = sharedPreferences.getString(IKeyManager.Token.KEY_ACCESS_ID, "");
		String access_token = sharedPreferences.getString(IKeyManager.Token.KEY_ACCESS_TOKEN, "");
		String username = sharedPreferences.getString(IKeyManager.Token.KEY_USERNAME, "");

		RequestParams params = new RequestParams();

		if (mCurrentPath != "/") {
			params.add("path", mCurrentPath);
			// TODO bug username
			params.add("queryUsername", username);
		}
		params.add(IKeyManager.ShareInfo.PARAM_PAGE, String.valueOf(page));
		params.add(IKeyManager.ShareInfo.PARAM_PAGESIZE, String.valueOf(pagesize));
		params.add(IKeyManager.Token.KEY_ACCESS_ID, access_id);
		params.add(IKeyManager.Token.KEY_ACCESS_TOKEN, access_token);

		mHttpUtil.getWithParams(mContext, REQUEST_URL, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				ShareBean mshareBean;
				ShareToBean mShareToBean;
				DirPaginationBean dirPaginationBean = null;
				ArrayList<ShareItemBean> list = null;
				String[] share;
				String[] items;
				String shareListData = new String(arg2);
				Log.d(tag, "check share data:\r\n" + shareListData);
				if (mCurrentPath != "/") {
					mShareToBean = JSON.parseObject(shareListData, ShareToBean.class);
					items = mShareToBean.getItems();
					dirPaginationBean = JSON.parseObject(mShareToBean.getPagination(), DirPaginationBean.class);
					list = new ArrayList<ShareItemBean>();
					for (int i = 0; i < items.length; i++) {
						ShareItemBean mShareItemBean = JSON.parseObject(items[i], ShareItemBean.class);
						list.add(mShareItemBean);
					}
				} else {
					mshareBean = JSON.parseObject(shareListData, ShareBean.class);
					share = mshareBean.getShare();
					dirPaginationBean = JSON.parseObject(mshareBean.getPagination(), DirPaginationBean.class);
					list = new ArrayList<ShareItemBean>();

					if (share != null) {
						for (int i = 0; i < share.length; i++) {
							ShareItemBean mShareItemBean = JSON.parseObject(share[i], ShareItemBean.class);
							list.add(mShareItemBean);
						}
					}
				}
				if (mShareGetListImpl == null)
					throw new NullPointerException("callback is null");
				mShareGetListImpl.onFinish(list, dirPaginationBean);
				((MainActivity) ((ShareActivity) mContext).getParent()).cancelWaitView();
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				arg3.printStackTrace();
				if (mShareGetListImpl == null)
					throw new NullPointerException("callback is null");
				mShareGetListImpl.onFailed();

				((MainActivity) ((ShareActivity) mContext).getParent()).cancelWaitView();
				HttpRequestFailureUtil failureUtil = new HttpRequestFailureUtil(mContext);
				failureUtil.handleFailureWithCode(arg0, false);
				if (arg0 == 404) {
					ToastUtil.show(mContext, R.string.string_share_invalid, Duration.s);
				}
			}
		});
	}

	public void cancelAllCalls() {
		((MainActivity) ((ShareActivity) mContext).getParent()).cancelWaitView();
		mHttpUtil.getClient().cancelRequests(mContext, true);
	}

	private void getUrl() {

		String url = IUrlManager.ShareUrl.DOMAIN + IUrlManager.ShareUrl.MINEADDRESS
				+ sharedPreferences.getString(IKeyManager.Token.KEY_USERNAME, "");
		if (((ShareActivity) mContext).URLFLAG == 100) {
			url += IUrlManager.ShareUrl.MYSHAREFUNCTION;
		} else if (((ShareActivity) mContext).URLFLAG == 101) {
			if (mCurrentPath != "/") {
				url += "/share/" + shareId + "/list?";
			} else {
				url += IUrlManager.ShareUrl.SHARETOMEFUNCTION;
			}
		}
		REQUEST_URL = url;
	}

	private IShareGetList mShareGetListImpl = null;

	public void setShareGetListImpl(IShareGetList iShareGetList) {
		this.mShareGetListImpl = iShareGetList;
	}

	/**
	 * @ClassName: IShareGetList
	 * @Description: 获取解析的数据并传给activity
	 * @date 2016年1月21日 下午1:37:12
	 * @author zhangbin
	 * @version 1.0
	 * @since JDK 1.6
	 */
	public interface IShareGetList {
		void onFinish(ArrayList<ShareItemBean> list, DirPaginationBean dirPagination);

		void onFailed();
	}

}
