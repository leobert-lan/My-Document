package com.lht.pan_android.view.popupwins;

import java.util.ArrayList;

import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IPreventPenetrate;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.DLog.LogLocation;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @ClassName: ThirdPartySharePopWins
 * @Description: 分享到第三方平台操作表
 * @date 2016年3月28日 上午10:45:24
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class ThirdPartySharePopWins extends CustomPopupWindow {

	private Adapter mAdapter;

	private View contentView;

	private GridView gridView;

	private OnThirdPartyShareItemClickListener itemClickListener;

	public ThirdPartySharePopWins(Activity activity, IPreventPenetrate ippl) {
		super(activity, ippl);
		init();
	}

	private String shareContent;

	public void setShareContent(String shareContent) {
		this.shareContent = shareContent;
	}

	public String getShareContent() {
		return shareContent;
	}

	@Override
	void init() {
		super.doDefaultSetting();
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		contentView = inflater.inflate(R.layout.popwindow_share_to_plateform, null);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setContentView(contentView);

		gridView = (GridView) contentView.findViewById(R.id.share_to_plateform_gridView);

	}

	public void setItems(ArrayList<ThirdPartyShareViewItem> items) {
		mAdapter = new Adapter(items);
		gridView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}

	public void setOnThirdPartyShareItemClickListener(OnThirdPartyShareItemClickListener l) {
		this.itemClickListener = l;
	}

	public OnThirdPartyShareItemClickListener getOnThirdPartyShareItemClickListener() {
		return this.itemClickListener;
	}

	private final class Adapter extends BaseAdapter {
		private final ArrayList<ThirdPartyShareViewItem> mItems;

		Adapter(ArrayList<ThirdPartyShareViewItem> items) {
			this.mItems = items;
		}

		@Override
		public int getCount() {
			return mItems.size();
		}

		@Override
		public Object getItem(int position) {
			return mItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = new ViewHolder();
			if (convertView == null) {
				convertView = LayoutInflater.from(mActivity).inflate(R.layout.share_to_plateform_item, null);
				holder.title = (TextView) convertView.findViewById(R.id.gridview_text);
				holder.image = (ImageView) convertView.findViewById(R.id.gridview_img);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			convertView.setOnClickListener(new ViewClickListenerImpl(position));
			holder.title.setText(mItems.get(position).name);
			holder.image.setImageResource(mItems.get(position).drawableRes);

			convertView.setOnClickListener(new ViewClickListenerImpl(position));

			return convertView;
		}

		public void remove(int index) {
			if (index >= getCount()) {
				DLog.e(getClass(), "移除item使用的index越界");
				return;
			}
			mItems.remove(index);
			notifyDataSetChanged();
		}

	}

	private final class ViewClickListenerImpl implements OnClickListener {
		final int mIndex;

		ViewClickListenerImpl(int index) {
			mIndex = index;
		}

		@Override
		public void onClick(View v) {
			if (null != itemClickListener) {
				itemClickListener.onClick(ThirdPartySharePopWins.this, mIndex, v);
			} else {
				DLog.e(getClass(), new LogLocation(), "先给一个item被点击的回调");
			}
		}
	}

	public interface OnThirdPartyShareItemClickListener {
		void onClick(ThirdPartySharePopWins popWins, int itemIndex, View item);
	}

	public void removeItem(int index) {
		if (mAdapter == null) {
			DLog.e(getClass(), "setItems at first");
			return;
		}
		mAdapter.remove(index);
	}

	public int getItemCount() {
		if (mAdapter == null)
			return 0;
		return mAdapter.getCount();
	}

	/**
	 * @ClassName: ViewHolder
	 * @Description: TODO
	 * @date 2016年3月28日 上午11:03:16
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 * @since JDK 1.7
	 */
	class ViewHolder {
		ImageView image;

		TextView title;
	}

	private String shareSummary;

	public void setShareSummary(String shareSummary) {
		this.shareSummary = shareSummary;
	}

	public String getShareSummary() {
		return shareSummary;
	}

}
