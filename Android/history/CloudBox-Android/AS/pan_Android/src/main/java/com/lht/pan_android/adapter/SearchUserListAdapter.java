package com.lht.pan_android.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.lht.pan_android.R;
import com.lht.pan_android.activity.BaseActivity;
import com.lht.pan_android.activity.asyncProtected.ShareToUserActivity;
import com.lht.pan_android.bean.SearchUserItemBean;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.DLog.LogLocation;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @ClassName: SearchUserListAdapter
 * @Description: TODO
 * @date 2016年1月26日 下午4:07:54
 * 
 * @author leobert.lan
 * @version 1.0
 * @since JDK 1.6
 */
public class SearchUserListAdapter extends BaseAdapter {

	private Context mContext;

	private ArrayList<SearchUserItemBean> mLiData;

	private String username;

	protected String tag = "sul";

	private DisplayImageOptions options;

	public SearchUserListAdapter(ArrayList<SearchUserItemBean> mData, Context mContext) {
		this.mLiData = mData;
		this.mContext = mContext;

		// TODO 替换默认底图
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.mortx)
				.showImageForEmptyUri(R.drawable.mortx).showImageOnFail(R.drawable.mortx).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
				.defaultDisplayImageOptions(options).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCache(new UnlimitedDiskCache(new File(BaseActivity.getPreviewPath())))
				.diskCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO)
				.build();
		ImageLoader.getInstance().init(config);
	}

	private class ViewHolder {
		TextView nickname;
		CheckBox choose;
		ImageView imgHead;
		TextView user;
		RelativeLayout clickSection;
	}

	@Override
	public int getCount() {
		return mLiData.size();
	}

	@Override
	public SearchUserItemBean getItem(int position) {
		return mLiData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {

		final ViewHolder holder;

		if (convertView != null && convertView.getTag() instanceof ViewHolder) {
			holder = (ViewHolder) convertView.getTag();
		} else {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.search_user_list_item, parent, false);
			holder = new ViewHolder();
			holder.nickname = (TextView) convertView.findViewById(R.id.searchuserlist_nickname);
			holder.choose = (CheckBox) convertView.findViewById(R.id.searchuserlist_choose);
			holder.imgHead = (ImageView) convertView.findViewById(R.id.searchuserlist_head);
			holder.user = (TextView) convertView.findViewById(R.id.searchuserlist_username);
			holder.clickSection = (RelativeLayout) convertView.findViewById(R.id.searchuserlist_clicksection);
			convertView.setTag(holder);
		}

		// ============code section :set========================

		final int p = position;

		holder.nickname.setText(mLiData.get(position).getNickname());
		holder.user.setText(mLiData.get(position).getUsername());
		// changelog 2.6.1 扩大点击区域
		holder.clickSection.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean b = mLiData.get(p).isSelect();
				holder.choose.setChecked(!b);
				mLiData.get(p).setSelect(!b);
				if (!b)
					_selected.put(mLiData.get(p).getUsername(), p);
				if (itemSelectChangedListener != null)
					itemSelectChangedListener.OnItemSelectChanged(mLiData.get(p));
			}
		});
		DLog.d(SearchUserListAdapter.class, "位置" + p + "  check" + mLiData.get(p).isSelect());
		holder.choose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean b = mLiData.get(p).isSelect();
				DLog.d(SearchUserListAdapter.class, "修改：" + p + "  to:" + !b);
				mLiData.get(p).setSelect(!b);
				if (!b)
					// 这意味被选中了
					_selected.put(mLiData.get(p).getUsername(), p);
				if (itemSelectChangedListener != null)
					itemSelectChangedListener.OnItemSelectChanged(mLiData.get(p));
			}
		});

		holder.choose.setChecked(mLiData.get(p).isSelect());

		// TODO
		ImageLoader.getInstance().displayImage(mLiData.get(p).getIcon(), holder.imgHead, options,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					}
				}, new ImageLoadingProgressListener() {
					@Override
					public void onProgressUpdate(String imageUri, View view, int current, int total) {
					}
				});

		return convertView;
	}

	public void Clear() {
		if (mLiData == null)
			mLiData = new ArrayList<SearchUserItemBean>();
		mLiData.clear();
		notifyDataSetChanged();
	}

	private OnItemSelectChangedListener itemSelectChangedListener;

	public void setOnItemSelectChangedListener(OnItemSelectChangedListener l) {
		itemSelectChangedListener = l;
	}

	public interface OnItemSelectChangedListener {
		void OnItemSelectChanged(SearchUserItemBean bean);
	}

	private HashMap<String, Integer> _selected = new HashMap<String, Integer>();

	public boolean cancelSelect(String username) {
		DLog.d(getClass(), new LogLocation(), "call remove");
		// 理论上必然包含
		if (!_selected.containsKey(username))
			return false;
		int index = _selected.get(username);
		mLiData.get(index).setSelect(false);
		_selected.remove(username);
		notifyDataSetChanged();

		if (itemSelectChangedListener != null)
			itemSelectChangedListener.OnItemSelectChanged(mLiData.get(index));
		return true;
	}

	/**
	 * @Title: ReplaceAll
	 * @Description: 专供起始添加contract使用，不需要同步selected，因为不可能有选中的
	 * @author: leobert.lan
	 * @param ret
	 */
	public void ReplaceAll(ArrayList<SearchUserItemBean> ret, boolean needCheck) {
		mLiData.clear();
		_selected.clear();
		if (needCheck) {
			if (ret.size() == 0)
				((ShareToUserActivity) mContext).change2EmptyView();
			else
				((ShareToUserActivity) mContext).change2NormalView();
		}
		for (SearchUserItemBean bean : ret) {
			mLiData.add(bean);

			if (bean.isSelect())
				// 这意味被选中了
				_selected.put(bean.getUsername(), mLiData.lastIndexOf(bean));
			if (itemSelectChangedListener != null)
				itemSelectChangedListener.OnItemSelectChanged(bean);

		}
		notifyDataSetChanged();
	}

}