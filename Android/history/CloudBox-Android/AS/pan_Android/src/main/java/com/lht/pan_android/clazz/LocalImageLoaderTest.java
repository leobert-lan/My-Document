package com.lht.pan_android.clazz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import com.lht.pan_android.Interface.OnLocalImageLoad;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.DLog.LogLocation;
import com.lht.pan_android.util.thumbnail.ThumbnailUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;

/**
 * @ClassName: ImageDownloader
 * @Description: TODO
 * @date 2015年12月4日 下午1:28:24
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class LocalImageLoaderTest {
	private static final String TAG = "LocalImageLoader";
	private HashMap<String, GetImageTask> map = new HashMap<String, GetImageTask>();
	private Map<String, SoftReference<Bitmap>> imageCaches = new HashMap<String, SoftReference<Bitmap>>();

	public boolean loadLocalImage(String fpath, int type, ImageView mImageView, String tempPath, Activity mActivity,
			OnLocalImageLoad download) {
		SoftReference<Bitmap> currBitmap = imageCaches.get(tempPath);
		Bitmap softRefBitmap = null;
		if (currBitmap != null) {
			softRefBitmap = currBitmap.get();
		}
		String imageName = "";
		if (fpath != null) {
			imageName = ImageLoaderUtil.getInstance().getImageName(fpath);
		}
		Bitmap bitmap = getBitmapFromFile(mActivity, imageName, tempPath);
		// 先从软引用中拿数据
		if (currBitmap != null && mImageView != null && softRefBitmap != null && fpath.equals(mImageView.getTag())) {
			mImageView.setImageBitmap(softRefBitmap);
			return true;
		}
		// 软引用中没有，从文件中拿数据
		else if (bitmap != null && mImageView != null && fpath.equals(mImageView.getTag())) {
			mImageView.setImageBitmap(bitmap);
			return true;
		}
		// 文件中也没有，此时根据mImageView的tag，即url去判断该url对应的task是否已经在执行，如果在执行，本次操作不创建新的线程，否则创建新的线程。
		else if (fpath != null && needCreateNewTask(mImageView)) {
			GetImageTask task = new GetImageTask(fpath, type, mImageView, tempPath, mActivity, download);
			if (mImageView != null) {
				DLog.d(getClass(), new LogLocation(), "执行本地加载 --> " + ImageLoaderUtil.flag);
				ImageLoaderUtil.flag++;
				task.execute();
				// 将对应的url对应的任务存起来
				map.put(fpath, task);
			}
			return false;
		}
		return false;
	}

	/**
	 * 判断是否需要重新创建线程下载图片，如果需要，返回值为true。
	 * 
	 * @param url
	 * @param mImageView
	 * @return
	 */
	private boolean needCreateNewTask(ImageView mImageView) {
		boolean b = true;
		if (mImageView != null) {
			String curr_task_url = (String) mImageView.getTag();
			if (isTasksContains(curr_task_url)) {
				b = false;
			}
		}
		return b;
	}

	/**
	 * 检查该url（最终反映的是当前的ImageView的tag，tag会根据position的不同而不同）对应的task是否存在
	 * 
	 * @param url
	 * @return
	 */
	private boolean isTasksContains(String url) {
		boolean b = false;
		if (map != null && map.get(url) != null) {
			b = true;
		}
		return b;
	}

	/**
	 * 删除map中该url的信息，这一步很重要，不然MyAsyncTask的引用会“一直”存在于map中
	 * 
	 * 这里我们不需要担心token的东西，下就是下了，没下就是没下，token过期会直接回到登录界面，之后会全部初始化，应该是安全的
	 * 
	 * @param url
	 */
	private void removeTaskFormMap(String url) {
		if (url != null && map != null && map.get(url) != null) {
			map.remove(url);
		}
	}

	/**
	 * 从文件中拿图片
	 * 
	 * @param mActivity
	 * @param imageName
	 *            图片名字
	 * @param path
	 *            图片路径
	 * @return
	 */
	private Bitmap getBitmapFromFile(Activity mActivity, String imageName, String path) {
		Bitmap bitmap = null;
		if (imageName != null) {
			File file = null;
			String real_path = "";
			try {
				if (ImageLoaderUtil.getInstance().hasSDCard()) {
					real_path = ImageLoaderUtil.getInstance().getExtPath()
							+ (path != null && path.startsWith("/") ? path : "/" + path);
				} else {
					real_path = ImageLoaderUtil.getInstance().getPackagePath(mActivity)
							+ (path != null && path.startsWith("/") ? path : "/" + path);
				}
				file = new File(real_path, imageName);
				if (file.exists())
					bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
			} catch (Exception e) {
				e.printStackTrace();
				bitmap = null;
			}
		}
		return bitmap;
	}

	/**
	 * 将下载好的图片存放到文件中
	 * 
	 * @param path
	 *            图片路径
	 * @param mActivity
	 * @param imageName
	 *            图片名字
	 * @param bitmap
	 *            图片
	 * @return
	 */
	private boolean setBitmapToFile(String path, Activity mActivity, String imageName, Bitmap bitmap) {
		File file = null;
		String real_path = "";
		try {
			if (ImageLoaderUtil.getInstance().hasSDCard()) {
				String ext = ImageLoaderUtil.getInstance().getExtPath();
				if (path.startsWith(ext)) {
					real_path = (path != null && path.startsWith("/") ? path : "/" + path);
				} else {
					real_path = ext + (path != null && path.startsWith("/") ? path : "/" + path);
				}
			} else {
				real_path = ImageLoaderUtil.getInstance().getPackagePath(mActivity)
						+ (path != null && path.startsWith("/") ? path : "/" + path);
			}
			file = new File(real_path, imageName);
			if (!file.exists()) {
				File file2 = new File(real_path + "/");
				file2.mkdirs();
			}
			file.createNewFile();
			FileOutputStream fos = null;
			if (ImageLoaderUtil.getInstance().hasSDCard()) {
				fos = new FileOutputStream(file);
			} else {
				fos = mActivity.openFileOutput(imageName, Context.MODE_PRIVATE);
			}

			if (imageName != null && (imageName.contains(".png") || imageName.contains(".PNG"))) {
				bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
			} else {
				bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
			}
			fos.flush();
			if (fos != null) {
				fos.close();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 辅助方法，一般不调用
	 * 
	 * @param path
	 * @param mActivity
	 * @param imageName
	 */
	private void removeBitmapFromFile(String path, Activity mActivity, String imageName) {
		File file = null;
		String real_path = "";
		try {
			if (ImageLoaderUtil.getInstance().hasSDCard()) {
				String ext = ImageLoaderUtil.getInstance().getExtPath();
				if (path.startsWith(ext)) {
					real_path = (path != null && path.startsWith("/") ? path : "/" + path);
				} else {
					real_path = ext + (path != null && path.startsWith("/") ? path : "/" + path);
				}
			} else {
				real_path = ImageLoaderUtil.getInstance().getPackagePath(mActivity)
						+ (path != null && path.startsWith("/") ? path : "/" + path);
			}
			file = new File(real_path, imageName);
			if (file != null)
				file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class GetImageTask extends AsyncTask<String, Void, Bitmap> {

		public static final int TYPE_IMAGE = 1;
		public static final int TYPE_VIDEO = 2;

		private ImageView mImageView;
		private String fpath;
		private OnLocalImageLoad download;
		private String path;
		private Activity mActivity;
		private int type;

		public GetImageTask(String fpath, int type, ImageView mImageView, String path, Activity mActivity,
				OnLocalImageLoad download) {
			this.mImageView = mImageView;
			this.fpath = fpath;
			this.path = path;
			this.mActivity = mActivity;
			this.download = download;
			this.type = type;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap mBitmap = null;

			if (type == GetImageTask.TYPE_VIDEO) {
				ThumbnailUtil thumbnailUtil = new ThumbnailUtil();
				mBitmap = thumbnailUtil.getVideoThumbnail(fpath, 108, 108, MediaStore.Images.Thumbnails.MICRO_KIND);
			} else if (type == GetImageTask.TYPE_IMAGE) {
				ThumbnailUtil thumbnailUtil = new ThumbnailUtil();
				mBitmap = thumbnailUtil.getImageThumbnail(fpath, 108, 108);
			} else
				throw new IllegalArgumentException("illegal arguement");

			String imageName = ImageLoaderUtil.getInstance().getImageName(fpath);
			if (!setBitmapToFile(path, mActivity, imageName, mBitmap)) {
				removeBitmapFromFile(path, mActivity, imageName);
			}
			if (mBitmap != null)
				imageCaches.put(fpath, new SoftReference<Bitmap>(Bitmap.createBitmap(mBitmap)));
			return mBitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// 回调设置图片
			if (download != null) {
				download.onLocalLoadSucc(result, fpath, mImageView);
				// 该url对应的task已经下载完成，从map中将其删除
				removeTaskFormMap(fpath);
			}
			super.onPostExecute(result);
		}

	}
}
