package com.lht.pan_android.util.activity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.http.Header;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IAsyncWithProgressbar;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.IUmengEventKey;
import com.lht.pan_android.Interface.MainActivityIPreventPenetrate;
import com.lht.pan_android.Interface.ShareSubMenuClickListener;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.activity.ViewPagerItem.ShareActivity;
import com.lht.pan_android.activity.asyncProtected.CopyActivity;
import com.lht.pan_android.activity.asyncProtected.MainActivity;
import com.lht.pan_android.activity.asyncProtected.MoveActivity;
import com.lht.pan_android.bean.DirPaginationBean;
import com.lht.pan_android.bean.ShareDownloadInfoBean;
import com.lht.pan_android.bean.ShareItemBean;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.ToastUtil;
import com.lht.pan_android.util.ToastUtil.Duration;
import com.lht.pan_android.util.activity.IgnoreUtil.IgnoreCallBack;
import com.lht.pan_android.util.dir.ShareDataDirectionUtil.IShareGetList;
import com.lht.pan_android.util.internet.HttpUtil;
import com.lht.pan_android.view.popupwins.CustomDialog;
import com.lht.pan_android.view.popupwins.CustomPopupWindow.OnPositiveClickListener;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

/**
 * @ClassName: CloudBoxUtil
 * @Description: TODO
 * @date 2015年11月30日 下午3:07:31
 * @author leobert.lan
 * @version 1.0
 */
public class ShareUtil implements IKeyManager.UserFolderList, IKeyManager.Token {

	private final ShareActivity mActivity;

	private MainActivity mParent;

	private String username = "";

	private HttpUtil mHttpUtil;

	public ShareUtil(ShareActivity shareActivity) {
		this.mActivity = shareActivity;
		mParent = (MainActivity) mActivity.getParent();
		SharedPreferences sp = shareActivity.getSharedPreferences(SP_TOKEN, Context.MODE_PRIVATE);
		username = sp.getString(KEY_USERNAME, "");
		multiSelectedItems = new HashSet<ShareItemBean>();
		mHttpUtil = new HttpUtil();
	}

	public int share = 0;
	public String shareId;
	public String queryName;

	/**
	 * @Title: getSubMenuClickListener
	 * @Description: 获取子菜单点击回调接口
	 * @author: leobert.lan
	 * @return
	 */
	public ShareSubMenuClickListener getShareSubMenuClickListener() {
		return new ShareSubMenuClickListener() {

			@Override
			public void onDownLoadClick(ShareItemBean item) {
				getActivity().reportCountEvent(IUmengEventKey.CALC_CB_SHARE_DOWNLOAD);
				ArrayList<ShareDownloadInfoBean> items = new ArrayList<ShareDownloadInfoBean>();
				ShareDownloadInfoBean sInfoBean = new ShareDownloadInfoBean();
				sInfoBean.setRemotepath(item.getPath());
				sInfoBean.setOwner(item.getOwner());
				sInfoBean.setIcon(item.getIcon());
				sInfoBean.setSize(item.getSize());
				sInfoBean.setShareId(item.getShareId());
				sInfoBean.setType(item.getShareType());
				sInfoBean.setUsername(username);
				sInfoBean.setName(item.getName());
				items.add(sInfoBean);
				mActivity.getDownloadService().startDownloadJob(items, mActivity.getIndividualFolder().toString());
			}

			@Override
			public void onMoveClick(ShareItemBean item) {
				getActivity().reportCountEvent(IUmengEventKey.CALC_CB_SHARE_CLOUDBOX);
				Intent i = new Intent(mActivity, CopyActivity.class);
				String str = JSON.toJSONString(item);
				ArrayList<String> temp = new ArrayList<String>();
				temp.add(str);
				i.putStringArrayListExtra(CopyActivity.MESSAGE_KEY, temp);
				mActivity.startActivity(i);
			}

			@Override
			public void onIgnoreClick(final ShareItemBean item, final int position) {
				getActivity().reportCountEvent(IUmengEventKey.CALC_CB_SHARE_IGNOREGOT);

				// TODO bug
				CustomDialog ignoreShareAskDialog = new CustomDialog(mActivity.getParent(),
						new MainActivityIPreventPenetrate());
				// SharePopUpCopy shareUtilPopUpCopy = new SharePopUpCopy(
				// mActivity.getParent(),
				// new MainActivityIPreventPenetrate());

				ignoreShareAskDialog.setContent("确定忽略该条分享吗？");

				// shareUtilPopUpCopy
				// .setContent(R.string.share_dialog_cancle_share);
				ignoreShareAskDialog.setNegativeButton(R.string.string_cancel);
				ignoreShareAskDialog.setPositiveButton(R.string.string_sure);
				ignoreShareAskDialog.setPositiveClickListener(new OnPositiveClickListener() {

					@Override
					public void onPositiveClick() {
						IgnoreUtil ignoreUtil = new IgnoreUtil(mActivity);
						ignoreUtil.ignore(item.getShareId(), item.getOwner());
						ignoreUtil.setCallBack(ignoreCallBack, position);
					}
				});
				ignoreShareAskDialog.show();
			}
		};
	}

	private IgnoreCallBack ignoreCallBack = new IgnoreCallBack() {

		@Override
		public void onSuccess(int position) {
			mActivity.mShareListAdapter.DeleteItem(position);
		}
	};
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
			mActivity.mShareDataDirectionUtil.getListData();
			mActivity.getPullRefreshListView().setMode(Mode.BOTH);
			break;
		case pull_to_add:
			this.mOperate = Operate.pull_to_add;
			addNextPage();
			break;
		case click_to_access:
			this.mOperate = Operate.click_to_access;
			mActivity.mShareDataDirectionUtil.appendNext(nextSubPath);
			mActivity.mShareDataDirectionUtil.getListData();
			mActivity.getPullRefreshListView().setMode(Mode.BOTH);
			break;
		case click_to_goback:
			this.mOperate = Operate.click_to_goback;
			mActivity.getPullRefreshListView().setMode(Mode.BOTH);
			mActivity.mShareDataDirectionUtil.goBack();
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
		if (dirPagination.getNext() != null) {
			mActivity.mShareDataDirectionUtil.getListData(IKeyManager.ShareInfo.TYPE_ALL,
					Integer.parseInt(dirPagination.getNext()), dirPagination.getPagesize());
			mActivity.getPullRefreshListView().setMode(Mode.BOTH);
		} else {
			ToastUtil.show(mActivity, R.string.no_more_toAdd, Duration.s);
			mActivity.getPullRefreshListView().setMode(Mode.PULL_FROM_START);
		}
	}

	public void multiQueryUsersInfo(final IAsyncWithProgressbar iView, String apiRequest,
			final AsyncHttpResponseHandler handler) {
		iView.showProgressBarOnAsync(true);
		Log.e("VSOApi", "query shares:"+apiRequest);
		mHttpUtil.getWithParams(mActivity, apiRequest, null, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				iView.cancelProgressBarOnAsyncFinish();
				handler.onSuccess(arg0, arg1, arg2);
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				iView.cancelProgressBarOnAsyncFinish();
				handler.onFailure(arg0, arg1, arg2, arg3);
				Toast.makeText(mActivity, "数据异常！", Toast.LENGTH_SHORT).show();
			}
		});

	}

	private DirPaginationBean dirPagination = null;

	public IShareGetList getOperateCallBack() {
		return new IShareGetList() {

			@Override
			public void onFinish(ArrayList<ShareItemBean> list, DirPaginationBean dirPagination) {
				if (mOperate == Operate.pull_to_add) {
					mActivity.mShareListAdapter.AddAll(list);
					mOperate = Operate.reset;
				} else {
					mActivity.mShareListAdapter.ReplaceAll(list);
					mOperate = Operate.reset;
				}
				ShareUtil.this.dirPagination = dirPagination;
				mActivity.getPullRefreshListView().onRefreshComplete();
			}

			@Override
			public void onFailed() {
				mOperate = Operate.reset;
				mActivity.getPullRefreshListView().onRefreshComplete();
				dirPagination = null;
			}
		};
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

	private HashSet<ShareItemBean> multiSelectedItems;

	/**
	 * @Title: addSelectedItem
	 * @Description: 添加一条选中的
	 * @author: leobert.lan
	 * @param item
	 */
	public void addSelectedItem(ShareItemBean item) {
		multiSelectedItems.add(item);
	}

	/**
	 * @Title: removeSelectedItem
	 * @Description: 移除一条选中的
	 * @author: leobert.lan
	 * @param item
	 */
	public void removeSelectedItem(ShareItemBean item) {
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
		void OnItemSelectedChanged(ShareItemBean item, boolean isSelected);
	}

	/**
	 * @Title: multiMove
	 * @Description: 批量移动
	 * @author: leobert.lan
	 */
	public void multiMove() {
		Intent intent = new Intent(mActivity, MoveActivity.class);
		Iterator<ShareItemBean> i = multiSelectedItems.iterator();
		ArrayList<String> temp = new ArrayList<String>();
		while (i.hasNext()) {
			String str = JSON.toJSONString(i.next());
			temp.add(str);
		}
		if (temp.size() > 0) {
			intent.putStringArrayListExtra(MoveActivity.MESSAGE_KEY, temp);
			// finishMultiOperate();
			mActivity.startActivity(intent);
		} else
			ToastUtil.show(mParent, R.string.cloudbox_multi_null1, Duration.s);

	}

	public UMengActivity getActivity() {
		return mActivity;
	}
}
