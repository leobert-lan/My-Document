package com.lht.pan_android.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.IUmengEventKey;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.Interface.SubMenuClickListener;
import com.lht.pan_android.activity.BaseActivity;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.activity.ViewPagerItem.CloudBoxActivity;
import com.lht.pan_android.activity.asyncProtected.ImagePreviewActivity;
import com.lht.pan_android.activity.asyncProtected.MainActivity;
import com.lht.pan_android.activity.innerWeb.VirtualAppActivity;
import com.lht.pan_android.activity.innerWeb.WebVideoActivity;
import com.lht.pan_android.bean.DirItemBean;
import com.lht.pan_android.bean.PreviewBean;
import com.lht.pan_android.bean.PreviewImgInfosBean;
import com.lht.pan_android.bean.VideoItem;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.DLog.LogLocation;
import com.lht.pan_android.util.TimeUtil;
import com.lht.pan_android.util.ToastUtil;
import com.lht.pan_android.util.ToastUtil.Duration;
import com.lht.pan_android.util.activity.CloudBoxUtil.OnItemSelectedChangedListener;
import com.lht.pan_android.util.activity.CloudBoxUtil.Operate;
import com.lht.pan_android.util.activity.VirtualApplicationUtil;
import com.lht.pan_android.util.activity.VirtualApplicationUtil.VirtualAppCallBack;
import com.lht.pan_android.util.file.FileUtil;
import com.lht.pan_android.util.file.IPreviewFileImpl;
import com.lht.pan_android.util.file.IPreviewFileImpl.IPreviewCaller;
import com.lht.pan_android.util.string.StringUtil;
import com.squareup.picasso.Picasso;

import android.app.Activity;
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
public class UserListAdapter extends AbsUserListAdapter implements IKeyManager.UserFolderList, IKeyManager.ContentType,
		IKeyManager.Token, IPreviewCaller, VirtualAppCallBack, IUmengEventKey {

	private Context mContext;

	private ArrayList<DirItemBean> mLiData;

	private String tag = "mylistadapter";

	private String username;

	private final String auth;

	private View viewNoFile;

	private VirtualApplicationUtil virtualApplicationUtil;

	private HashMap<String, String> va = new HashMap<String, String>();

	public UserListAdapter(ArrayList<DirItemBean> mData, Context mContext) {
		this.mLiData = mData;
		this.mContext = mContext;
		SharedPreferences sp = mContext.getSharedPreferences(SP_TOKEN, Context.MODE_PRIVATE);
		String accessId;
		String accessToken;
		username = sp.getString(KEY_USERNAME, "");
		accessId = sp.getString(KEY_ACCESS_ID, "");
		accessToken = sp.getString(KEY_ACCESS_TOKEN, "");
		auth = "&access_id=" + accessId + "&access_token=" + accessToken;
		viewNoFile = ((CloudBoxActivity) mContext).getViewNoFile();
		virtualApplicationUtil = new VirtualApplicationUtil(mContext);
		virtualApplicationUtil.setCallBack(this);

		va.put(IKeyManager.Token.KEY_ACCESS_TOKEN, accessToken);
		va.put(IKeyManager.Token.KEY_ACCESS_ID, accessId);
	}

	private class ViewHolder {
		ImageView mIcon;
		TextView mTitle;
		TextView mCount;
		TextView mComment;
		RelativeLayout mClickSection;
		/**
		 * mToggle:子菜单开关
		 */
		CheckBox mToggle;
		/**
		 * mSelect:cell是否已经选择
		 */
		CheckBox mSelect;
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
		if (position < getCount())
			return mLiData.get(position);
		// 就算bug也比crash好
		else
			return new DirItemBean();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public void notifyDataSetChanged() {
		if (getCount() == 0) {
			viewNoFile.setVisibility(View.VISIBLE);
			viewNoFile.bringToFront();
		} else {
			viewNoFile.setVisibility(View.GONE);
		}
		super.notifyDataSetChanged();
	}

	/**
	 * @Title: click
	 * @Description: 点击逻辑
	 * @author: leobert.lan
	 * @param position
	 */
	protected void click(int position) {
		// 分发时已经过滤
		// if (isMultView())
		// return;
		if (!BaseActivity.isConnected()) {
			ToastUtil.show(mContext, R.string.no_internet, Duration.s);
			return;
		}

		if (position>= mLiData.size()) {
			return;
		}
		
		DirItemBean dirItemBean = mLiData.get(position);
		if (dirItemBean.getType().equals(IKeyManager.UserFolderList.TYPE_FOLDER)) {
			// 文件夹进入
			((CloudBoxActivity) mContext).mCloudBoxUtil.performOperate(Operate.click_to_access, dirItemBean.getName());
			LinearLayout ll = ((CloudBoxActivity) mContext).getBtBack();
			ll.setVisibility(View.VISIBLE);

			String s = dirItemBean.getPath();
			if (s.lastIndexOf("/") > 0) {
				String temp = s.substring(0, s.lastIndexOf("/"));
				((CloudBoxActivity) mContext).getTxtParentFolder().setText(temp.substring(temp.lastIndexOf("/") + 1));
			} else {
				((CloudBoxActivity) mContext).getTxtParentFolder().setText(R.string.cloudbox_activity_title);
			}
			((CloudBoxActivity) mContext).getmToggleFilter().setVisibility(View.GONE);
			TextView textView = ((CloudBoxActivity) mContext).getTxtTitleCurrentFolder();
			textView.setText(dirItemBean.getName());
		} else {
			// 图片预览
			if (dirItemBean.getContentType().startsWith(IMAGE) && FileUtil.isSupportedImage(dirItemBean.getName())) {
				Intent i = new Intent();
				i.setClass(mContext, ImagePreviewActivity.class);
				// TODO
				i.putExtra("Extra", getPreviewImagesInfo(position));
				mContext.startActivity(i);
			} else {
				// 其他文件的预览 此处做一个预判断，能用第三方的就不用虚拟应用
				String type = FileUtil.getMIMETypeByName(dirItemBean.getName());
				if (type.equals(FileUtil.UNSUPPORTTYPE)) {
					DLog.d(getClass(), new LogLocation(), "use vir open:" + dirItemBean.getPath());
					onOpenStart();
					virtualApplicationUtil.openVirtualApp(dirItemBean.getPath());
					// TODO 2.4.0 因2.3.0虚拟应用问题，暂关闭该功能 temp

					// ToastUtil.show(mContext,
					// R.string.preview_unsupported_file, Duration.s);

					((UMengActivity) mContext).reportCountEvent(CALC_CB_OPEN_VIRTUAL);
				} else if (type.startsWith(FileUtil.VIDOETYPE)) {
					startVideoPreview(dirItemBean);

				} else {
					DLog.d(getClass(), new LogLocation(), "use third open");
					PreviewBean bean = new PreviewBean();
					bean.setContentType(dirItemBean.getContentType());
					bean.setModifyTime(dirItemBean.getDatetime());
					bean.setName(dirItemBean.getName());
					bean.setPath(dirItemBean.getPath());
					bean.setSize(dirItemBean.getSize());
					bean.setType(1);

					new Thread(new IPreviewFileImpl(this, bean)).start();
					((UMengActivity) mContext).reportCountEvent(CALC_CB_OPEN_DOCUMENT);
				}

			}
		}
	}

	// ImageDownloaderTest mDownloader;

	/**
	 * @Title: startVideoPreview
	 * @Description: 预览视频
	 * @author: leobert.lan
	 * @param dirItemBean
	 */
	private void startVideoPreview(DirItemBean dirItemBean) {

		Intent intent = new Intent(mContext, WebVideoActivity.class);

		StringBuilder builder = new StringBuilder();
		builder.append("https://").append(IUrlManager.VideoPreview.HOST).append("/")
				.append(IUrlManager.VideoPreview.PATH).append("?").append("path=").append(dirItemBean.getPath())
				.append(auth).append("&username=").append(username);

		VideoItem item = new VideoItem();
		item.setUrl(builder.toString());
		item.setName(dirItemBean.getName());
		
		intent.putExtra(WebVideoActivity.KEY_DATA, JSON.toJSONString(item));
		
		mContext.startActivity(intent);

	}

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
			holder.mSelect = (CheckBox) convertView.findViewById(R.id.cloudbox_dirlist_item_select);
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

		holder.mSelect.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// 关闭多选视图时就没有必要回调了
				if (isMultView())
					itemSelectChangedListener.OnItemSelectedChanged(getItem(position2), isChecked);

				getItem(position2).setSelected(isChecked);
			}
		});
		holder.mSelect.setChecked(getItem(position).isSelected());

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
		holder.mClickSection.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((CloudBoxActivity) mContext).getmToggleFilter().setChecked(false);
				if (isMultView())
					holder.mSelect.setChecked(!holder.mSelect.isChecked());
				else
					click((Integer) v.getTag());
			}
		});

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

		// 多选视图切换
		if (isMultView()) {
			holder.mSelect.setVisibility(View.VISIBLE);
			holder.mToggle.setVisibility(View.GONE);
		} else {
			holder.mSelect.setVisibility(View.GONE);
			holder.mToggle.setVisibility(View.VISIBLE);
		}

		return convertView;
	}

	private boolean isMultView;

	private boolean isMultView() {
		return isMultView;
	}

	public void setMultView(boolean isMultView) {
		this.isMultView = isMultView;
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
				openHolder.setChecked(false);
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
		if (isMultView() && isAllSelected) {
			// 确保新添加的数据多选的全选能够被执行
			for (int j = 0; j < mLiData.size(); j++) {
				itemSelectChangedListener.OnItemSelectedChanged(getItem(j), true);
				getItem(j).setSelected(true);
			}
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

	public void toogleMultView() {
		// 关闭副菜单
		if (null != openHolder)
			openHolder.setChecked(false);
		this.setMultView(!isMultView());
		notifyDataSetChanged();
	}

	private OnItemSelectedChangedListener itemSelectChangedListener;

	private boolean isAllSelected;

	/**
	 * @Title: setOnItemSelectedChangedListener
	 * @Description: 设置item选中（取消）的回调接口
	 * @author: leobert.lan
	 * @param l
	 */
	public void setOnItemSelectedChangedListener(OnItemSelectedChangedListener l) {
		itemSelectChangedListener = l;
	}

	/**
	 * @Title: selectAll
	 * @Description: 全选
	 * @author: leobert.lan
	 */
	public void selectAll() {
		isAllSelected = true;
		// 需要手动调用，应为ListView优化效率，没有展现的视图不会被执行回调
		for (int i = 0; i < getCount(); i++) {
			itemSelectChangedListener.OnItemSelectedChanged(getItem(i), isAllSelected);

			getItem(i).setSelected(true);
		}
		notifyDataSetChanged();
	}

	/**
	 * @Title: deSelectAll
	 * @Description: 全不选
	 * @author: leobert.lan
	 */
	public void deSelectAll() {
		isAllSelected = false;
		for (int i = 0; i < getCount(); i++) {
			itemSelectChangedListener.OnItemSelectedChanged(getItem(i), isAllSelected);

			getItem(i).setSelected(false);
		}
		notifyDataSetChanged();
	}

	/**
	 * @Title: initMultiSelectState
	 * @Description: 初始化多选视图的数据
	 * @author: leobert.lan
	 */
	public void initMultiSelectState() {
		isAllSelected = false;
		for (int i = 0; i < getCount(); i++) {
			getItem(i).setSelected(false);
		}
	}

	@Override
	public Activity getCallerActivity() {
		return ((Activity) mContext).getParent();
	}

	@Override
	public void onOpenStart() {
		// 跟合适的做法是使用接口隔离减少耦合
		((MainActivity) ((Activity) mContext).getParent()).showWaitView(true);
	}

	@Override
	public void onOpenFinish() {
		((MainActivity) ((Activity) mContext).getParent()).cancelWaitView();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see 虚拟应用成功时的回调
	 */
	@Override
	public void onSuccess(String url) {
		onOpenFinish();
		Intent intent = new Intent();
		intent.setClass(mContext, VirtualAppActivity.class);
		intent.putExtra(VirtualAppActivity.KEY_URL, url);
		mContext.startActivity(intent);
	}

	/**
	 * 虚拟应用打开失败 -1 文件路径问题 其他 网络访问问题
	 * 
	 * @see com.lht.pan_android.util.activity.VirtualApplicationUtil.VirtualAppCallBack#onFailure(int)
	 */
	@Override
	public void onFailure(int httpStatus) {
		onOpenFinish();
		ToastUtil.show(mContext, R.string.preview_unsupported_file, Duration.s);
	}

	@Override
	public void onNotSupport() {
		onOpenFinish();
		ToastUtil.show(mContext, R.string.preview_unsupported_file, Duration.s);
	}

}