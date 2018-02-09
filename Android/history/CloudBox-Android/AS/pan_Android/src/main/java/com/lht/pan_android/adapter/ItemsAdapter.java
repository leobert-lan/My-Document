package com.lht.pan_android.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.greenrobot.eventbus.EventBus;

import com.lht.pan_android.R;
import com.lht.pan_android.activity.BaseActivity;
import com.lht.pan_android.clazz.Events.UploadSelectChange;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.DLog.LogLocation;
import com.lht.pan_android.view.MyImageView;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ProgressBar;

/**
 * @ClassName: ItemsAdapter
 * @Description: 分组内item以GridView显示的适配器
 * @date 2015年12月1日 下午6:18:59
 * 
 * @author leobert.lan
 * @version 1.0
 */

public class ItemsAdapter extends BaseAdapter {
	/**
	 * 用来存储图片的选中情况
	 */
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
	private List<String> list;
	protected LayoutInflater mInflater;

	private DisplayImageOptions options;

	public ItemsAdapter(Activity activity, List<String> list, GridView mGridView) {
		this.list = list;
		mInflater = LayoutInflater.from(activity);

		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.tupmor)// tpjz
				.showImageForEmptyUri(R.drawable.tupmor).showImageOnFail(R.drawable.tupmor).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(activity)
				.defaultDisplayImageOptions(options).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCache(new UnlimitedDiskCache(new File(BaseActivity.getLocalImageCachePath())))
				.diskCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO)
				.build();
		ImageLoader.getInstance().init(config);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		DLog.d(getClass(), new LogLocation(), "ed2:" + "getView:" + position);

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.grid_child_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mImageView = (MyImageView) convertView.findViewById(R.id.child_image);
			viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.child_checkbox);
			viewHolder.pb = (ProgressBar) convertView.findViewById(R.id.child_pb);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.mImageView.setImageResource(R.drawable.tpjz);
		}

		viewHolder.mImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewHolder.mCheckBox.setChecked(!viewHolder.mCheckBox.isChecked());
			}
		});

		final int position2 = position;

		viewHolder.mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// 如果是未选中的CheckBox,则添加动画
				if (!mSelectMap.containsKey(position2) || !mSelectMap.get(position2)) {
					addAnimation(viewHolder.mCheckBox);
				}
				mSelectMap.put(position2, isChecked);
				EventBus.getDefault().post(new UploadSelectChange());
			}
		});

		viewHolder.mCheckBox.setChecked(mSelectMap.containsKey(position) ? mSelectMap.get(position) : false);

		// 图片异步加载
		ImageLoader.getInstance().displayImage("file://" + list.get(position), viewHolder.mImageView, options,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						viewHolder.pb.setProgress(0);
						viewHolder.pb.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						viewHolder.pb.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						viewHolder.pb.setVisibility(View.GONE);
					}
				}, new ImageLoadingProgressListener() {
					@Override
					public void onProgressUpdate(String imageUri, View view, int current, int total) {
						viewHolder.pb.setProgress(Math.round(100.0f * current / total));
					}
				});

		return convertView;
	}

	/**
	 * 给CheckBox加点击动画，利用开源库nineoldandroids设置动画
	 * 
	 * @param view
	 */
	private void addAnimation(View view) {
		float[] vaules = new float[] { 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f,
				1.0f };
		AnimatorSet set = new AnimatorSet();
		set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),
				ObjectAnimator.ofFloat(view, "scaleY", vaules));
		set.setDuration(150);
		set.start();
	}

	/**
	 * 获取选中的Item的position
	 * 
	 * @return
	 */
	public ArrayList<Integer> getSelectItems() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (Iterator<Map.Entry<Integer, Boolean>> it = mSelectMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Integer, Boolean> entry = it.next();
			if (entry.getValue()) {
				list.add(entry.getKey());
			}
		}

		return list;
	}

	public int getSelectCount() {
		return getSelectItems().size();
	}

	public static class ViewHolder {
		public MyImageView mImageView;
		public CheckBox mCheckBox;
		public ProgressBar pb;
	}

	private String contentType = "";

	public void setType(String contentType) {
		this.contentType = contentType;
	}

}
