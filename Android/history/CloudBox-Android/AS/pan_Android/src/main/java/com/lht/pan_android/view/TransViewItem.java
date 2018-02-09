package com.lht.pan_android.view;

import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.Interface.OnImageDownload;
import com.lht.pan_android.Interface.OnLocalImageLoad;
import com.lht.pan_android.activity.BaseActivity;
import com.lht.pan_android.activity.ViewPagerItem.TransportManagerActivity;
import com.lht.pan_android.activity.ViewPagerItem.TransportManagerActivity.OnTransCallback;
import com.lht.pan_android.clazz.ImageDownloaderTest;
import com.lht.pan_android.clazz.LocalImageLoaderTest;
import com.lht.pan_android.clazz.LocalImageLoaderTest.GetImageTask;
import com.lht.pan_android.view.TransViewInfo.Status;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @ClassName: TransViewItem
 * @Description: 传输管理cell的View控制类
 * @date 2015年12月9日 上午10:37:49
 * 
 * @author leobert.lan
 * @version 1.0
 */
@SuppressLint("DefaultLocale")
public class TransViewItem {

	private View view;

	private TransViewInfo info;

	private ViewHolder holder;

	private Context mContext;

	private final OnTransCallback mCallback;

	private Status mStatus;

	private String auth;

	@SuppressLint("InflateParams")
	public TransViewItem(Context ctx, String auth, final TransViewInfo info, OnTransCallback callback) {
		view = LayoutInflater.from(ctx).inflate(R.layout.transport_manager_item, null);
		this.info = info;
		this.mContext = ctx;
		this.auth = auth;
		this.mCallback = callback;
		bindView();
		init();

		holder.transInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (shouldMultiWork) {
					// 多选视图时，定向到选择框
					holder.multiSelect.setChecked(!holder.multiSelect.isChecked());
				} else {
					// 正常视图时，已完成的提供预览
					if (itemBodyClickListener != null)
						itemBodyClickListener.onItemBodyClick(TransViewItem.this);
				}
			}
		});
	}

	@SuppressLint("DefaultLocale")
	private void bindView() {
		holder = new ViewHolder();
		holder.icon = (ImageView) view.findViewById(R.id.trans_on_item_icon);
		holder.name = (TextView) view.findViewById(R.id.trans_on_item_title);
		holder.comment = (TextView) view.findViewById(R.id.trans_on_item_comment);
		holder.toogle = (CheckBox) view.findViewById(R.id.trans_on_item_toggle);
		holder.sub = (LinearLayout) view.findViewById(R.id.trans_on_item_ll_sub);
		holder.pOrs = (LinearLayout) view.findViewById(R.id.trans_on_item_control);
		holder.img_pOrs = (ImageView) view.findViewById(R.id.trans_on_item_img_control);
		holder.txt_pOrs = (TextView) view.findViewById(R.id.trans_on_item_text_control);
		holder.delete = (LinearLayout) view.findViewById(R.id.trans_on_item_delete);
		holder.multiSelect = (CheckBox) view.findViewById(R.id.trans_on_item_multiselect);
		holder.transInfo = (RelativeLayout) view.findViewById(R.id.trans_on_item_clicksection);

	}

	private void init() {
		setName(info.getName());
		holder.toogle.setChecked(false);
		addCallBack();
		mStatus = info.getStatus();
		// 此处应该扩展情况
		if (mStatus != Status.failure)
			setIcon(info.getIconUrl());
		initStatus(mStatus);
		setComment(info.getComment());
		setMultiSelect();
	}

	/**
	 * @Title: setMultiSelect
	 * @Description: 设置multiselect回调
	 * @author: leobert.lan
	 */
	private void setMultiSelect() {
		holder.multiSelect.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (!shouldMultiWork)
					return;
				if (null == selectedChangedListener)
					throw new NullPointerException("set OnItemSelectedChangedListener at first");
				selectedChangedListener.OnItemSelectedChanged(isChecked, getInfo().getDbIndex());
			}
		});
	}

	private void initStatus(Status status) {

		switch (status) {
		case pause:
			holder.img_pOrs.setImageResource(R.drawable.zant);
			holder.txt_pOrs.setText(mContext.getResources().getString(R.string.trans_control_restart));
			break;
		case start:
			holder.img_pOrs.setImageResource(R.drawable.kais);
			holder.txt_pOrs.setText(mContext.getResources().getString(R.string.trans_control_pause));
			break;
		case wait:
			holder.img_pOrs.setImageResource(R.drawable.zant);
			holder.txt_pOrs.setText(mContext.getResources().getString(R.string.trans_control_restart));
			holder.comment.setText(mContext.getResources().getString(R.string.trans_comment_wait));
			break;
		case failure:
			holder.icon.setBackgroundResource(R.drawable.weifxx3);
			holder.icon.setImageResource(R.drawable.wenjbcz);
			holder.pOrs.setVisibility(View.GONE);
			holder.comment.setText(mContext.getResources().getString(R.string.trans_comment_failure));
			break;
		case complete:
			holder.pOrs.setVisibility(View.GONE);
			break;
		default:
			holder.pOrs.setVisibility(View.GONE);
			holder.comment.setText(mContext.getResources().getString(R.string.trans_comment_wait));
			break;
		}
	}

	/*
	 * 暂时的
	 */
	public void UpdateViewWhileComplete() {
		initStatus(mStatus);
		holder.pOrs.setVisibility(View.GONE);
		// 关闭副菜单视图
		holder.toogle.setChecked(false);
	}

	private void addCallBack() {
		// 展开键逻辑
		holder.toogle.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked)
					holder.sub.setVisibility(View.VISIBLE);
				else
					holder.sub.setVisibility(View.GONE);
			}

		});
		holder.delete.setOnClickListener(clickListener);
		holder.pOrs.setOnClickListener(clickListener);
	}

	/**
	 * @Title: setComment
	 * @Description: 设置name下面的
	 * @author: leobert.lan
	 */
	private void setComment(String string) {
		holder.comment.setText(string);
	}

	/**
	 * @Title: setName
	 * @Description: 设置任务名
	 * @author: leobert.lan
	 * @param string
	 */
	private void setName(String string) {
		holder.name.setText(string);
	}

	public View getItem() {
		return view;
	}

	public TransViewInfo getInfo() {
		return info;
	}

	private ImageDownloaderTest mDownloader;

	public void switch2MultiSelectView() {
		holder.multiSelect.setVisibility(View.VISIBLE);
		// 关闭副菜单
		holder.toogle.setChecked(false);
		shouldMultiWork = true;
		// 隐藏副菜单开关
		holder.toogle.setVisibility(View.GONE);
	}

	private boolean shouldMultiWork = false;

	public void switch2NormalView() {
		// 隐藏多选时的选择器
		holder.multiSelect.setVisibility(View.GONE);
		shouldMultiWork = false;
		holder.multiSelect.setChecked(false);
		// 关闭副菜单
		holder.toogle.setChecked(false);
		// 显示副菜单开关
		holder.toogle.setVisibility(View.VISIBLE);
	}

	/**
	 * @Title: setSelected
	 * @Description: 外部调用设置是否选取
	 * @author: leobert.lan
	 * @param isSelected
	 */
	public void setSelected(boolean isSelected) {
		holder.multiSelect.setChecked(isSelected);
	}

	private LocalImageLoaderTest localImageLoader;

	/**
	 * @Title: setIcon
	 * @Description: 设置图标
	 * @author: leobert.lan
	 * @param url
	 */
	private void setIcon(String url) {
		if (info.isUpload()) {
			if (localImageLoader == null)
				localImageLoader = new LocalImageLoaderTest();
			String icon_url = url;
			holder.icon.setTag(icon_url);

			int type = -1;

			if (info.getContentType().equals(IKeyManager.ContentType.VIDEO))
				type = GetImageTask.TYPE_VIDEO;
			else if (info.getContentType().equals(IKeyManager.ContentType.IMAGE))
				type = GetImageTask.TYPE_IMAGE;

			localImageLoader.loadLocalImage(url, type, holder.icon, BaseActivity.getLocalImageCachePath(),
					(TransportManagerActivity) mContext, new OnLocalImageLoad() {

						@Override
						public void onLocalLoadSucc(Bitmap bitmap, String c_url, ImageView imageView) {
							if (imageView != null) {
								if (bitmap == null) {
									imageView.setImageResource(R.drawable.weifxx3);
								} else
									imageView.setImageBitmap(bitmap);
								imageView.setTag("");
							}
						}
					});

		} else {
			if (mDownloader == null)
				mDownloader = new ImageDownloaderTest();
			holder.icon.setImageResource(R.drawable.weifxx3);
			String icon_url = url;
			holder.icon.setTag(icon_url);
			if (url.startsWith(IUrlManager.FileIcon.DOMAIN + IUrlManager.FileIcon.FUNCTION_ICON_V3)
					|| url.startsWith(IUrlManager.FileIcon.DOMAIN + IUrlManager.FileIcon.FUNCTION_ICON_V3SUB))
				auth = "";
			if (mDownloader != null) {
				// 异步下载图片
				mDownloader.imageDownload(icon_url, auth, holder.icon, BaseActivity.getThumbnailPath(),
						(TransportManagerActivity) mContext, new OnImageDownload() {
							@Override
							public void onDownloadSucc(Bitmap bitmap, String c_url, ImageView mimageView) {
								ImageView imageView = (ImageView) view.findViewWithTag(c_url);
								if (imageView != null) {
									if (bitmap == null)
										imageView.setImageResource(R.drawable.weifxx3);
									else
										imageView.setImageBitmap(bitmap);
									imageView.setTag("");
								}
							}
						});
			}
		}

	}

	public void updateProgress(String s) {
		// 如果是任务还处于初始状态，收到广播时，更新状态，好像下载还没牵涉到
		if (mStatus == Status.wait) {
			setStatus(Status.start);
			initStatus(mStatus);
		}
		if (mStatus != Status.pause)
			// 前台控制暂停了就不更新了，处理最后一片的回调比较复杂
			holder.comment.setText(s);
	}

	private void setStatus(Status stauts) {
		mStatus = stauts;
		info.setStatus(stauts);
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.trans_on_item_delete:
				if (info.isUpload())
					mCallback.OnUploadDelete(info.getDbIndex());
				else
					mCallback.OnDownloadDelete(info.getDbIndex());
				break;
			case R.id.trans_on_item_control:
				if (info.isUpload()) {
					if (info.getStatus() == Status.pause || info.getStatus() == Status.wait) {
						boolean ret = mCallback.OnUploadRestart(info.getDbIndex());
						if (ret)
							setStatus(Status.start);
					} else if (info.getStatus() == Status.start) {
						boolean ret = mCallback.OnUploadPause(info.getDbIndex());
						if (ret)
							setStatus(Status.pause);
					}

					initStatus(mStatus);
				} else {
					// 下载
					Log.d("transportM", "click");
					if (info.getStatus() == Status.pause || info.getStatus() == Status.wait) {
						boolean ret = mCallback.OnDownloadRestart(info.getDbIndex());
						if (ret)
							setStatus(Status.start);
						Log.d("transportM", "click p");
					} else if (info.getStatus() == Status.start) {
						boolean ret = mCallback.OnDownloadPause(info.getDbIndex());
						if (ret)
							setStatus(Status.pause);
						Log.d("transportM", "click s");
					}
					initStatus(mStatus);
				}

				break;

			default:
				break;
			}
		}
	};

	/**
	 * @ClassName: ViewHolder
	 * @Description: 自从有了viewholder，感觉自己萌萌哒。 用于获取每个item的子view
	 * @date 2015年12月9日 下午1:00:28
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	private class ViewHolder {
		/**
		 * transInfo:传输信息
		 */
		RelativeLayout transInfo;
		/**
		 * icon:图标
		 */
		ImageView icon;
		/**
		 * name:任务名
		 */
		TextView name;

		/**
		 * comment:详情
		 */
		TextView comment;
		/**
		 * toogle:展开键
		 */
		CheckBox toogle;
		/**
		 * sub:扩展区
		 */
		LinearLayout sub;
		/**
		 * pOrs:暂停继续
		 */
		LinearLayout pOrs;

		/**
		 * img_pOrs:暂停继续图
		 */
		ImageView img_pOrs;

		/**
		 * txt_pOrs:暂停继续文字
		 */
		TextView txt_pOrs;
		/**
		 * delete:删除
		 */
		LinearLayout delete;

		/**
		 * multiSelect:多选时的选择器
		 */
		CheckBox multiSelect;
	}

	private OnItemSelectedChangedListener selectedChangedListener;

	public void setOnItemSelectedChangedListener(OnItemSelectedChangedListener l) {
		this.selectedChangedListener = l;
	}

	public interface OnItemSelectedChangedListener {
		void OnItemSelectedChanged(boolean isSelected, int dbIndex);
	}

	private OnItemBodyClickListener itemBodyClickListener;

	public void setOnItemBodyClickListener(OnItemBodyClickListener l) {
		this.itemBodyClickListener = l;
	}

	public interface OnItemBodyClickListener {
		void onItemBodyClick(TransViewItem item);
	}

}
