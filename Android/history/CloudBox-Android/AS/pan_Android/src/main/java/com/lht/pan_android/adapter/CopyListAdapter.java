package com.lht.pan_android.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.activity.BaseActivity;
import com.lht.pan_android.activity.ViewPagerItem.CloudBoxActivity;
import com.lht.pan_android.activity.asyncProtected.CopyActivity;
import com.lht.pan_android.bean.DirItemBean;
import com.lht.pan_android.bean.ShareItemBean;
import com.lht.pan_android.util.TimeUtil;
import com.lht.pan_android.util.ToastUtil;
import com.lht.pan_android.util.ToastUtil.Duration;
import com.lht.pan_android.util.activity.CloudBoxUtil.Operate;
import com.lht.pan_android.util.string.StringUtil;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.SharedPreferences;
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
 * @ClassName: MoveListAdapter
 * @Description: 目录列表适配器
 * @date 2016年1月5日 上午10:13:00
 * 
 * @author zhangbin
 * @version 1.0
 */
public class CopyListAdapter extends BaseAdapter
		implements IKeyManager.UserFolderList, IKeyManager.ContentType, IKeyManager.Token {

	private Context mContext;

	private ArrayList<ShareItemBean> mLiData;

	private final String auth;

	private View viewNoFile;

	private HashMap<String, String> va = new HashMap<String, String>();

	public CopyListAdapter(ArrayList<ShareItemBean> mData, Context mContext) {
		this.mLiData = mData;
		this.mContext = mContext;
		SharedPreferences sp = mContext.getSharedPreferences(SP_TOKEN, Context.MODE_PRIVATE);
		String accessId;
		String accessToken;
		accessId = sp.getString(KEY_ACCESS_ID, "");
		accessToken = sp.getString(KEY_ACCESS_TOKEN, "");
		auth = "&access_id=" + accessId + "&access_token=" + accessToken;

		va.put(IKeyManager.Token.KEY_ACCESS_TOKEN, accessToken);
		va.put(IKeyManager.Token.KEY_ACCESS_ID, accessId);
	}

	private class ViewHolder {
		ImageView mIcon;
		CheckBox mToggle;
		TextView mTitle;
		TextView mCount;
		TextView mComment;
		RelativeLayout mClickSection;
		CheckBox toggle;
	}

	@Override
	public int getCount() {
		return mLiData.size();
	}

	@Override
	public ShareItemBean getItem(int position) {
		return mLiData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cloudbox_dirlist_item_clicksection:
				if (mContext instanceof CloudBoxActivity) {
					((CloudBoxActivity) mContext).getmToggleFilter().setChecked(false);
				}
				click((Integer) v.getTag());
				break;
			default:
				break;
			}

		}
	};

	/**
	 * @Title: click
	 * @Description: 点击逻辑
	 * @author: leobert.lan
	 * @param position
	 */
	protected void click(int position) {
		if (!BaseActivity.isConnected()) {
			ToastUtil.show(mContext, R.string.no_internet, Duration.s);
			return;
		}
		ShareItemBean dirItemBean = mLiData.get(position);
		((CopyActivity) mContext).mCopyUtil.performOperate(Operate.click_to_access, dirItemBean.getName(),
				dirItemBean.getPath());
		((CopyActivity) mContext).updateDestination(dirItemBean.getPath());
	}

	// ImageDownloaderTest mDownloader;

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {

		final ViewHolder holder;

		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_cloudbox_list_dir_new, parent, false);
			holder = new ViewHolder();
			holder.mClickSection = (RelativeLayout) convertView.findViewById(R.id.cloudbox_dirlist_item_clicksection);
			holder.mIcon = (ImageView) convertView.findViewById(R.id.cloudbox_dirlist_item_icon);
			holder.mToggle = (CheckBox) convertView.findViewById(R.id.cloudbox_dirlist_item_toggle);
			holder.mTitle = (TextView) convertView.findViewById(R.id.cloudbox_dirlist_item_title);
			holder.mCount = (TextView) convertView.findViewById(R.id.cloudbox_dirlist_item_num);
			holder.mComment = (TextView) convertView.findViewById(R.id.cloudbox_dirlist_item_comment);
			holder.toggle = (CheckBox) convertView.findViewById(R.id.cloudbox_dirlist_item_toggle);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		changeToNornalMode(holder.mClickSection);

		fillWidght(holder, mLiData.get(position));
		// 隐藏副菜单展开开关
		holder.toggle.setVisibility(View.GONE);

		holder.mToggle.setVisibility(View.GONE);
		holder.mClickSection.setTag(position);
		holder.mClickSection.setOnClickListener(mClickListener);

		// if (mDownloader == null)
		// mDownloader = new ImageDownloaderTest();

		holder.mIcon.setImageResource(R.drawable.unknow108);
		String icon_url = getItem(position).getIcon();

		icon_url = StringUtil.removeAuth(icon_url);
		String auth = getAuth(icon_url);
		holder.mIcon.setTag(icon_url);

		if (StringUtil.isEmpty(auth)) {
			Picasso.with(mContext).load(icon_url).placeholder(R.drawable.unknow108).error(R.drawable.unknow108).fit()
					.diskCache().tag(mContext).into(holder.mIcon);
		} else {
			Picasso.with(mContext).load(icon_url, va).placeholder(R.drawable.unknow108).error(R.drawable.unknow108)
					.diskCache().fit().tag(mContext).into(holder.mIcon);
		}

		// if (mDownloader != null) {
		// // 异步下载图片
		// mDownloader.imageDownload(icon_url, auth, holder.mIcon,
		// BaseActivity.getThumbnailPath(), (Activity) mContext,
		// new OnImageDownload() {
		// @Override
		// public void onDownloadSucc(Bitmap bitmap, String c_url,
		// ImageView mimageView) {
		// ImageView imageView = (ImageView) parent
		// .findViewWithTag(c_url);
		// if (imageView != null) {
		// imageView.setImageBitmap(bitmap);
		// }
		// }
		// });
		// }
		return convertView;
	}

	private void changeToNornalMode(View view) {
		if (view != null) {
			view.setAlpha(1f);
		}
	}

	private void fillWidght(ViewHolder holder, ShareItemBean dirItemBean) {

		holder.mIcon.setImageResource(R.drawable.wenjj);
		holder.mComment.setText(TimeUtil.getFormatedTime(dirItemBean.getShareTime()));
		holder.mTitle.setText(dirItemBean.getName());

	}

	public boolean DeleteItem(int position) {
		if (mLiData == null)
			return false;
		if (position >= getCount())
			return false;
		mLiData.remove(position);
		notifyDataSetChanged();
		return true;
	}

	public void Clear() {
		if (mLiData == null)
			mLiData = new ArrayList<ShareItemBean>();
		mLiData.clear();
		notifyDataSetChanged();
	}

	public void AddAll(ArrayList<DirItemBean> datas) {
		if (mLiData == null)
			mLiData = new ArrayList<ShareItemBean>();
		for (int i = 0; i < datas.size(); i++) {
			mLiData.add(datas.get(i));
		}
		notifyDataSetChanged();
	}

	public void ReplaceAll(ArrayList<DirItemBean> datas) {
		mLiData = new ArrayList<ShareItemBean>();
		for (int i = 0; i < datas.size(); i++) {
			mLiData.add(datas.get(i));
		}
		notifyDataSetChanged();
	}

	public boolean AddItem(ShareItemBean data) {
		if (mLiData == null)
			mLiData = new ArrayList<ShareItemBean>();
		mLiData.add(data);
		notifyDataSetChanged();
		return true;
	}

	public boolean AddItem(int position, ShareItemBean data) {
		if (mLiData == null)
			mLiData = new ArrayList<ShareItemBean>();
		if (position > getCount())
			return false;
		else
			mLiData.add(position, data);
		notifyDataSetChanged();
		return true;
	}

	/**
	 * @Title: getAuth
	 * @Description: 如果是icon，就没有auth...
	 * @author: leobert.lan
	 * @param url
	 * @return
	 */
	private String getAuth(String url) {
		return (url.startsWith(IUrlManager.FileIcon.DOMAIN + IUrlManager.FileIcon.FUNCTION_ICON_V3)
				|| url.startsWith(IUrlManager.FileIcon.DOMAIN + IUrlManager.FileIcon.FUNCTION_ICON_V3SUB)) ? "" : auth;
	}

}