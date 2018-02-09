package com.lht.pan_android.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.Interface.SubMenuClickListener;
import com.lht.pan_android.activity.BaseActivity;
import com.lht.pan_android.activity.asyncProtected.ImagePreviewActivity;
import com.lht.pan_android.activity.asyncProtected.SearchActivity;
import com.lht.pan_android.bean.DirItemBean;
import com.lht.pan_android.bean.PreviewImgInfosBean;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.DLog.LogLocation;
import com.lht.pan_android.util.TimeUtil;
import com.lht.pan_android.util.ToastUtil;
import com.lht.pan_android.util.ToastUtil.Duration;
import com.lht.pan_android.util.activity.CloudBoxUtil.Operate;
import com.lht.pan_android.util.file.FileUtil;
import com.lht.pan_android.util.string.StringUtil;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @ClassName: TestAdapter
 * @Description: TODO
 * @date 2015年11月25日 下午5:41:42
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class SearchAdapter extends AbsUserListAdapter
		implements IKeyManager.UserFolderList, IKeyManager.ContentType, IKeyManager.Token {

	private Context mContext;

	private ArrayList<DirItemBean> mLiData;

	private final String auth;

	private HashMap<String, String> va = new HashMap<String, String>();

	public SearchAdapter(ArrayList<DirItemBean> mData, Context mContext) {
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
		TextView mTitle;
		TextView mCount;
		TextView mComment;
		RelativeLayout mClickSection;
		CheckBox mToggle;
		// CheckBox mSelect;
		LinearLayout subSection;
		LinearLayout bt_share;
		LinearLayout bt_download;
		LinearLayout bt_rename;
		LinearLayout bt_move;
		LinearLayout bt_delete;
	}

	@Override
	public int getCount() {
		return mLiData.size();
	}

	@Override
	public DirItemBean getItem(int position) {
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
				click((Integer) v.getTag());
				break;
			// case R.id.searchbar_img_search:
			default:
				break;
			}

		}
	};

	/**
	 * @Title: click
	 * @Description: 点击逻辑,分文件夹和文件
	 * @author: leobert.lan
	 * @param position
	 */
	protected void click(int position) {
		if (!BaseActivity.isConnected()) {
			ToastUtil.show(mContext, R.string.no_internet, Duration.s);
			return;
		}
		DirItemBean dirItemBean = mLiData.get(position);
		if (dirItemBean.getType().equals(IKeyManager.SearchList.TYPE_FOLDER)) {
			DLog.d(getClass(), new LogLocation(), "ed2:" + "folder path:" + dirItemBean.getPath());

			// mSearchDataDirectionUtil.setListCurrentPath(dirItemBean.getPath());
			((SearchActivity) mContext).mSearchUtil.listPerformOperate(Operate.click_to_access, dirItemBean.getName(),
					dirItemBean.getPath());
			((SearchActivity) mContext).changeCallbackToRefresh();
		} else {
			if (dirItemBean.getContentType().startsWith(IMAGE)) {
				Intent i = new Intent();
				i.setClass(mContext, ImagePreviewActivity.class);
				i.putExtra("Extra", getPreviewImagesInfo(position));
				mContext.startActivity(i);
			} else {
				ToastUtil.show(mContext, R.string.preview_unsupported_file, Duration.s);
			}
		}
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
			holder.mTitle = (TextView) convertView.findViewById(R.id.cloudbox_dirlist_item_title);
			holder.mCount = (TextView) convertView.findViewById(R.id.cloudbox_dirlist_item_num);
			holder.mComment = (TextView) convertView.findViewById(R.id.cloudbox_dirlist_item_comment);
			holder.mToggle = (CheckBox) convertView.findViewById(R.id.cloudbox_dirlist_item_toggle);
			// holder.mSelect = (CheckBox) convertView
			// .findViewById(R.id.cloudbox_dirlist_item_select);
			holder.subSection = (LinearLayout) convertView.findViewById(R.id.cloudbox_list_ll_sub);

			bindSubView(convertView, holder);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// ============code section :set========================
		setSubView(holder, position);
		if (getItem(position).getType().equals(TYPE_FOLDER))
			holder.bt_download.setVisibility(View.GONE);
		else
			holder.bt_download.setVisibility(View.VISIBLE);

		final int position2 = position;

		holder.mToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					holder.subSection.setVisibility(View.VISIBLE);
					if (null != openHolder && openHolder != holder.mToggle && openHolder.isChecked())
						openHolder.setChecked(false);
				} else
					holder.subSection.setVisibility(View.GONE);
				getItem(position2).setOpen(isChecked);
				openHolder = holder.mToggle;
			}
		});
		holder.mToggle.setChecked(getItem(position).isOpen());

		// ***********填充数据*********
		fillWidght(holder, mLiData.get(position));

		holder.mClickSection.setTag(position);
		holder.mClickSection.setOnClickListener(mClickListener);

		// if (mDownloader == null)
		// mDownloader = new ImageDownloaderTest();
		// 这句代码的作用是为了解决convertView被重用的时候，图片预设的问题 TODO 换图
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

		return convertView;
	}

	private void setSubView(ViewHolder holder, int position) {
		holder.bt_share.setTag(position);
		holder.bt_download.setTag(position);
		holder.bt_rename.setTag(position);
		holder.bt_move.setTag(position);
		holder.bt_delete.setTag(position);

		holder.bt_share.setOnClickListener(subMenuClickListener);
		holder.bt_download.setOnClickListener(subMenuClickListener);
		holder.bt_rename.setOnClickListener(subMenuClickListener);
		holder.bt_move.setOnClickListener(subMenuClickListener);
		holder.bt_delete.setOnClickListener(subMenuClickListener);
	}

	private CheckBox openHolder = null;

	private void bindSubView(View convertView, ViewHolder holder) {
		holder.bt_share = (LinearLayout) convertView.findViewById(R.id.cloudbox_list_child_share);
		holder.bt_download = (LinearLayout) convertView.findViewById(R.id.cloudbox_list_child_download);
		holder.bt_rename = (LinearLayout) convertView.findViewById(R.id.cloudbox_list_child_rename);
		holder.bt_move = (LinearLayout) convertView.findViewById(R.id.cloudbox_list_child_move);
		holder.bt_delete = (LinearLayout) convertView.findViewById(R.id.cloudbox_list_child_delete);
	}

	private SubMenuClickListener mSubMenuClickListener = null;

	public void setSubMenuClickListener(SubMenuClickListener l) {
		this.mSubMenuClickListener = l;
	}

	private OnClickListener subMenuClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			DirItemBean item = getItem((Integer) v.getTag());
			switch (v.getId()) {
			case R.id.cloudbox_list_child_share:
				mSubMenuClickListener.onShareClick(item);
				break;
			case R.id.cloudbox_list_child_download:
				mSubMenuClickListener.onDownLoadClick(item);
				break;
			case R.id.cloudbox_list_child_rename:
				mSubMenuClickListener.onRenameClick(item);
				break;
			case R.id.cloudbox_list_child_move:
				mSubMenuClickListener.onMoveClick(item);
				break;
			case R.id.cloudbox_list_child_delete:
				mSubMenuClickListener.onDeleteClick(item);
				break;
			default:
				break;
			}
		}
	};

	private void fillWidght(ViewHolder holder, DirItemBean dirItemBean) {

		if (dirItemBean.getType().equals(TYPE_FILE)) {
			holder.mIcon.setImageResource(R.drawable.word);
			holder.mCount.setText("");
			holder.mComment.setText(TimeUtil.getFormatedTime(dirItemBean.getDatetime()) + "  "
					+ FileUtil.calcSize(dirItemBean.getSize()));
		} else {
			holder.mIcon.setImageResource(R.drawable.wenjj);
			holder.mCount.setText(formateDescendantFiles(dirItemBean.getDescendantFiles()));
			holder.mComment.setText(TimeUtil.getFormatedTime(dirItemBean.getDatetime()));
		}
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
			mLiData = new ArrayList<DirItemBean>();
		mLiData.clear();
		notifyDataSetChanged();
	}

	public void AddAll(ArrayList<DirItemBean> datas) {
		if (mLiData == null)
			mLiData = new ArrayList<DirItemBean>();
		for (int i = 0; i < datas.size(); i++) {
			mLiData.add(datas.get(i));
		}
		notifyDataSetChanged();
	}

	public void ReplaceAll(ArrayList<DirItemBean> datas) {
		mLiData = new ArrayList<DirItemBean>();
		for (int i = 0; i < datas.size(); i++) {
			mLiData.add(datas.get(i));
		}
		notifyDataSetChanged();
	}

	public boolean AddItem(DirItemBean data) {
		if (mLiData == null)
			mLiData = new ArrayList<DirItemBean>();
		mLiData.add(data);
		notifyDataSetChanged();
		return true;
	}

	public boolean AddItem(int position, DirItemBean data) {
		if (mLiData == null)
			mLiData = new ArrayList<DirItemBean>();
		if (position > getCount())
			return false;
		else
			mLiData.add(position, data);
		notifyDataSetChanged();
		return true;
	}

	/**
	 * @Title: getPreviewImagesInfo
	 * @Description: 获取当前目录中所有支持预览的图片的必要信息
	 * @author: leobert.lan
	 * @param position
	 * @return
	 */
	public String getPreviewImagesInfo(int position) {

		ArrayList<String> dirItem = new ArrayList<String>();

		int index = 0;

		for (int i = 0; i < mLiData.size(); i++) {
			if (mLiData.get(i).getContentType() != null) {
				if (mLiData.get(i).getContentType().startsWith(IMAGE)) {
					dirItem.add(JSON.toJSONString(mLiData.get(i)));
					if (i < position)
						index++;
				}
			}
		}
		String[] dirBeans = new String[dirItem.size()];
		for (int j = 0; j < dirItem.size(); j++) {
			dirBeans[j] = dirItem.get(j);
		}
		PreviewImgInfosBean bean = new PreviewImgInfosBean();
		bean.setDirBeans(dirBeans);
		bean.setCposition(index);
		return JSON.toJSONString(bean);
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

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		if (getCount() == 0) {
			if (((SearchActivity) mContext).isSearchView())
				((SearchActivity) mContext).change2EmptyView();
			else
				((SearchActivity) mContext).change2EmptyView2();
		}
	}

}