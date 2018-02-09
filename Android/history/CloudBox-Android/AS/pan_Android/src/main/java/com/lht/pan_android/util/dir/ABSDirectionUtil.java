package com.lht.pan_android.util.dir;

import java.util.ArrayList;

import com.lht.pan_android.Interface.IAsyncWithProgressbar;
import com.lht.pan_android.bean.DirItemBean;
import com.lht.pan_android.bean.DirPaginationBean;
import com.lht.pan_android.util.internet.HttpUtil;
import com.lht.pan_android.util.string.StringUtil;

import android.content.Context;

/**
 * @ClassName: ABSDirectionUtil
 * @Description: TODO
 * @date 2016年4月11日 上午11:06:48
 * 
 * @author leobert.lan
 * @version 1.0
 */
public abstract class ABSDirectionUtil {

	protected String mCurrentPath = "/";

	protected final Context mContext;

	protected final String PATH_SEPARATOR = "/";

	protected final HttpUtil mHttpUtil;

	protected final IAsyncWithProgressbar iView;

	private TypeFilter filter = TypeFilter.all;

	public ABSDirectionUtil(Context ctx, IAsyncWithProgressbar iView) {
		mContext = ctx;
		this.iView = iView;
		mHttpUtil = new HttpUtil();
	}

	public String getCurrentPath() {
		return this.mCurrentPath;
	}

	public String getPrePath() {
		String path = StringUtil.splitLastSubPath(mCurrentPath);
		return StringUtil.isEmpty(path) ? PATH_SEPARATOR : path;
	}

	public void setCurrentPath(String mCurrentPath) {
		this.mCurrentPath = mCurrentPath;
	}

	public boolean isRoot() {
		if (mCurrentPath.equals("/")) {
			return true;
		} else {
			return false;
		}
	}

	protected boolean checkPath(String subpath) {

		if (StringUtil.isEmpty(subpath))
			throw new NullPointerException("sub-path is null!are you kidding me?");

		if (!StringUtil.isCorrectFolderName(subpath))
			throw new IllegalArgumentException("your sub path contains illegal charset");
		return true;
	}

	protected abstract void goBack();

	protected abstract String getUrl(TypeFilter filter);

	protected abstract void appendNext(String subPath);

	/**
	 * @Title: getListData
	 * @Description: TODO
	 * @author: leobert.lan
	 * @param Type
	 *            0文件，1目录，2文件和目录 default
	 * @param page
	 *            分页页号 start from 1
	 * @param pagesize
	 *            分页小大
	 * @param filter
	 *            接口功能对应的过滤类型
	 */
	protected abstract void getListData(String Type, int page, int pagesize, TypeFilter filter);

	public TypeFilter getFilter() {
		return filter;
	}

	public void setFilter(TypeFilter filter) {
		this.filter = filter;
	}

	public void cancelAllCalls() {
		mHttpUtil.getClient().cancelRequests(mContext, true);
	}

	public enum TypeFilter {
		all, image, doc, audio, video
	}

	protected IGetList mGetListImpl = null;

	public void setGetListImpl(IGetList getListImpl) {
		this.mGetListImpl = getListImpl;
	}

	/**
	 * @ClassName: IGetList
	 * @Description: 获取、解析完成的output接口，用于传输数据给activity
	 * @date 2015年11月30日 下午1:31:55
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	public interface IGetList {
		/**
		 * @Title: onFinish
		 * @Description: 回调接口，传输解析完成的数据
		 * @author: leobert.lan
		 * @param list
		 * @param dirPagination
		 */
		void onFinish(ArrayList<DirItemBean> list, DirPaginationBean dirPagination);

		void onFailed(int httpStatus);
	}

	protected void destroy() {
		mHttpUtil.getClient().cancelRequests(mContext, true);
	}

}
