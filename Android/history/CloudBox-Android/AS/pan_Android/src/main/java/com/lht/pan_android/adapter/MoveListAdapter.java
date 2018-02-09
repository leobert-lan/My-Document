package com.lht.pan_android.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.activity.BaseActivity;
import com.lht.pan_android.activity.ViewPagerItem.CloudBoxActivity;
import com.lht.pan_android.activity.asyncProtected.MoveActivity;
import com.lht.pan_android.bean.DirItemBean;
import com.lht.pan_android.util.TimeUtil;
import com.lht.pan_android.util.ToastUtil;
import com.lht.pan_android.util.ToastUtil.Duration;
import com.lht.pan_android.util.activity.CloudBoxUtil.Operate;
import com.lht.pan_android.util.file.FileUtil;
import com.lht.pan_android.util.string.StringUtil;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
public class MoveListAdapter extends AbsUserListAdapter
		implements IKeyManager.UserFolderList, IKeyManager.ContentType, IKeyManager.Token {

	private Context mContext;

	private ArrayList<DirItemBean> mLiData;

	private final String auth;

	private View viewNoFile;

	private HashMap<String, String> va = new HashMap<String, String>();

	public MoveListAdapter(ArrayList<DirItemBean> mData, Context mContext) {
		this.mLiData = mData;
		this.mContext = mContext;
		SharedPreferences sp = mContext.getSharedPreferences(SP_TOKEN, Context.MODE_PRIVATE);
		String accessId;
		String accessToken;
		// username = sp.getString(KEY_USERNAME, "");
		accessId = sp.getString(KEY_ACCESS_ID, "");
		accessToken = sp.getString(KEY_ACCESS_TOKEN, "");
		auth = "&access_id=" + accessId + "&access_token=" + accessToken;

		va.put(IKeyManager.Token.KEY_ACCESS_TOKEN, accessToken);
		va.put(IKeyManager.Token.KEY_ACCESS_ID, accessId);
		if (mContext instanceof CloudBoxActivity)
			viewNoFile = ((CloudBoxActivity) mContext).getViewNoFile();
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
	public DirItemBean getItem(int position) {
		return mLiData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public void notifyDataSetChanged() {
		if (getCount() == 0) {
			if (mContext instanceof CloudBoxActivity) {
				viewNoFile.setVisibility(View.VISIBLE);
				viewNoFile.bringToFront();
			}
		} else {
			if (mContext instanceof CloudBoxActivity) {
				viewNoFile.setVisibility(View.GONE);
			}
		}
		super.notifyDataSetChanged();
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
		DirItemBean dirItemBean = mLiData.get(position);
		if (dirItemBean.getType().equals(IKeyManager.UserFolderList.TYPE_FOLDER)) {
			boolean isLegal = ((MoveActivity) mContext).checkPathLegalForFolder(dirItemBean.getPath());
			if (isLegal) {
				// 文件夹进入 一定要获取成功后再更新ui，否则path就会有乱掉的可能
				((MoveActivity) mContext).mMoveUtil.performOperate(Operate.click_to_access, dirItemBean.getName(),
						dirItemBean.getPath());
				// 但是应该即刻更新目标路径，这时候也允许submit
				((MoveActivity) mContext).updateDestination(dirItemBean.getPath());
			} else {
				// TODO 路径对于文件移动不合法，可以更详细一些
				ToastUtil.show(mContext, R.string.move_illegal, Duration.l);
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
			holder.mToggle = (CheckBox) convertView.findViewById(R.id.cloudbox_dirlist_item_toggle);
			holder.mTitle = (TextView) convertView.findViewById(R.id.cloudbox_dirlist_item_title);
			holder.mCount = (TextView) convertView.findViewById(R.id.cloudbox_dirlist_item_num);
			holder.mComment = (TextView) convertView.findViewById(R.id.cloudbox_dirlist_item_comment);
			holder.toggle = (CheckBox) convertView.findViewById(R.id.cloudbox_dirlist_item_toggle);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 修改不应该选取的文件夹的视觉效果
		if (!((MoveActivity) mContext).checkPathLegalForFolder(mLiData.get(position).getPath()))
			changeToGrayMode(holder.mClickSection);
		else
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

		return convertView;
	}

	private void changeToNornalMode(View view) {
		if (view != null) {
			view.setAlpha(1f);
		}
	}

	/**
	 * @Title: changeToGrayMode
	 * @Description: 将“非法”的文件夹变为灰色
	 * @author: leobert.lan
	 * @param holder
	 */
	private void changeToGrayMode(View view) {
		if (view != null) {
			// TODO 写灰色样式
			view.setAlpha(0.6f);
			// view.setBackgroundColor(mContext.getResources().getColor(
			// R.color.gray_b3));
		}
	}

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