package com.lht.pan_android.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.activity.BaseActivity;
import com.lht.pan_android.activity.asyncProtected.ImagePreviewActivity;
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
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class ImagePreviewViewPagerAdapter<K> extends PagerAdapter implements IUrlManager.ImagePreview {
	List<View> list = new ArrayList<View>();
	private Context mContext;

	private SharedPreferences sp;

	private final String username;

	private final String token;

	private final String access_id;

	private DisplayMetrics dm;

	private DisplayImageOptions options;

	public ImagePreviewViewPagerAdapter(Context ctx, DisplayMetrics dm) {
		this.mContext = ctx;
		this.dm = dm;
		sp = mContext.getSharedPreferences(IKeyManager.Token.SP_TOKEN, Context.MODE_PRIVATE);
		username = sp.getString(IKeyManager.Token.KEY_USERNAME, "");
		access_id = sp.getString(IKeyManager.Token.KEY_ACCESS_ID, "");
		token = sp.getString(IKeyManager.Token.KEY_ACCESS_TOKEN, "");

		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.weifxx3)
				.showImageForEmptyUri(R.drawable.weifx2).showImageOnFail(R.drawable.weifx2).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(ctx).defaultDisplayImageOptions(options)
				.threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
				.diskCache(new UnlimitedDiskCache(new File(BaseActivity.getPreviewPath())))
				.diskCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO)
				.build();
		ImageLoader.getInstance().init(config);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		ViewPager pViewPager = ((ViewPager) container);
		pViewPager.removeView(list.get(position));
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		ViewPager pViewPager = ((ViewPager) arg0);
		pViewPager.addView(list.get(arg1));
		return list.get(arg1);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {

	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}

	/**
	 * @Title: add
	 * @Description: 警告！临时补的，千万不要在这基础上再设计，重设计！
	 * @author: leobert.lan
	 * @param paths
	 */
	public void add(ArrayList<String> paths, ArrayList<String> shareIds, ArrayList<String> owners) {
		for (int i = 0; i < paths.size(); i++) {

			LayoutInflater inflater = LayoutInflater.from(mContext);
			RelativeLayout rl = (RelativeLayout) inflater.inflate(R.layout.preview_cell, null);
			ViewHolder holder = new ViewHolder();
			holder.img = (ImageView) rl.findViewById(R.id.preview_img);
			holder.pb = (ProgressBar) rl.findViewById(R.id.preview_pb);

			// TODO
			String image_url = "";
			if (type != null && type.equals(ImagePreviewActivity.FLAG_SHARE)) {
				image_url = DOMAIN + ADDRESS_SHARE + owners.get(i) + "/share/" + shareIds.get(i) + FUNCTION + "?path="
						+ paths.get(i) + "&queryUsername=" + username + "&width=" + dm.widthPixels + "&height="
						+ dm.heightPixels;
			} else {
				image_url = DOMAIN + ADDRESS_CB + username + FUNCTION + "?path=" + paths.get(i) + "&width="
						+ dm.widthPixels + "&height=" + dm.heightPixels;
			}

			holder.img.setTag(image_url);
			holder.img.setImageResource(R.drawable.weifx2);
			rl.setTag(holder);
			list.add(rl);
		}
		notifyDataSetChanged();
	}

	private boolean isBarShown = true;

	public void getCurrentPicture(int position) {

		@SuppressWarnings("unchecked")
		final ViewHolder holder = (ViewHolder) list.get(position).getTag();
		final String image_url = (String) holder.img.getTag();

		holder.img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isBarShown == false) {
					((ImagePreviewActivity) mContext).imgLinearBar.setVisibility(View.GONE);
					((ImagePreviewActivity) mContext).imgRelatTitle.setVisibility(View.GONE);
					isBarShown = true;
				} else {
					if (((ImagePreviewActivity) mContext).flag == null) {
						((ImagePreviewActivity) mContext).imgLinearBar.setVisibility(View.VISIBLE);
						((ImagePreviewActivity) mContext).imgRelatTitle.setVisibility(View.VISIBLE);
						((ImagePreviewActivity) mContext).imgRelatTitle.bringToFront();
					} else {
						((ImagePreviewActivity) mContext).imgLinearBar.setVisibility(View.GONE);
						((ImagePreviewActivity) mContext).imgRelatTitle.setVisibility(View.VISIBLE);
						((ImagePreviewActivity) mContext).imgRelatTitle.bringToFront();
					}
					isBarShown = false;
				}
			}
		});

		final String auth = "&access_id=" + access_id + "&access_token=" + token;

		ImageLoader.getInstance().displayImage(image_url + auth, holder.img, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				holder.pb.setProgress(0);
				holder.pb.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				holder.pb.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				holder.pb.setVisibility(View.GONE);
			}
		}, new ImageLoadingProgressListener() {
			@Override
			public void onProgressUpdate(String imageUri, View view, int current, int total) {
				holder.pb.setProgress(Math.round(100.0f * current / total));
			}
		});

	}

	private class ViewHolder {
		ImageView img;
		ProgressBar pb;
	}

	public void delete(int position) {
		list.remove(position);
		notifyDataSetChanged();
	}

	private String type;

	public void setType(String type) {
		this.type = type;
	}
}