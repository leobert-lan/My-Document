package com.lht.pan_android.adapter;

/** 
 * @ClassName: GroupAdapter 
 * @Description: TODO
 * @date 2015年12月1日 下午5:39:10
 *  
 * @author leobert.lan
 * @version 1.0
 */

import java.util.HashMap;
import java.util.List;

import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.bean.ImageBean;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.DLog.LogLocation;
import com.lht.pan_android.util.thumbnail.ThumbnailUtil;
import com.lht.pan_android.view.MyImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class GroupAdapter extends BaseAdapter {
	private List<ImageBean> list;
	private ListView mGridView;
	protected LayoutInflater mInflater;
	private boolean[] isLoad;
	private HashMap<Integer, Bitmap> imageThumbs = new HashMap<Integer, Bitmap>();
	private boolean isFinish;
	private String contentType = "";

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

	public GroupAdapter(Context context, List<ImageBean> list, ListView mGroupGridView) {
		this.list = list;
		this.mGridView = mGroupGridView;
		mInflater = LayoutInflater.from(context);
		isLoad = new boolean[list.size()];
		isFinish = false;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		ImageBean mImageBean = list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.listitem_selectimage_list_group, null);
			viewHolder.mImageView = (MyImageView) convertView.findViewById(R.id.list_imagegroup_img);
			viewHolder.mTextViewTitle = (TextView) convertView.findViewById(R.id.list_imagegroup_name);
			viewHolder.mTextViewCounts = (TextView) convertView.findViewById(R.id.list_imagegroup_count);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.mImageView.setImageResource(R.drawable.unknow108);
		}

		viewHolder.mTextViewTitle.setText(mImageBean.getFolderName());
		viewHolder.mTextViewCounts.setText(Integer.toString(mImageBean.getImageCounts()));

		viewHolder.mImageView.setTag(position);

		if (!isLoad[position]) {
			new GetThumbNailTask().execute(viewHolder.mImageView);

			viewHolder.mImageView.setImageResource(R.drawable.unknow108);
		} else {
			DLog.d(getClass(), new LogLocation(), "yes");
			viewHolder.mImageView.setImageBitmap(imageThumbs.get(position));
		}

		return convertView;
	}

	public static class ViewHolder {
		public MyImageView mImageView;
		public TextView mTextViewTitle;
		public TextView mTextViewCounts;
	}

	private class GetThumbNailTask extends AsyncTask<ImageView, Void, Void> {

		String path = null;

		ImageView img = null;

		Bitmap bitmap = null;
		int position;

		@Override
		protected void onPreExecute() {
			if (isFinish)
				onCancelled();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(ImageView... params) {
			if (isFinish)
				onCancelled();
			img = params[0];
			position = (Integer) img.getTag();
			path = list.get(position).getTopImagePath();
			ThumbnailUtil thumbnailUtil = new ThumbnailUtil();
			if (contentType.equals(IKeyManager.ContentType.IMAGE))
				bitmap = thumbnailUtil.getImageThumbnail(path, 100, 100);
			else if (contentType.equals(IKeyManager.ContentType.VIDEO))
				bitmap = thumbnailUtil.getVideoThumbnail(path, 100, 100, MediaStore.Images.Thumbnails.MICRO_KIND);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (isFinish)
				onCancelled();
			img.setImageBitmap(bitmap);
			imageThumbs.put(position, bitmap);
			bitmap = null;
			isLoad[position] = true;
			super.onPostExecute(result);
		}

	}

	public void loadCancel() {
		this.isFinish = true;
	}

	public void loadStart() {
		this.isFinish = false;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}
