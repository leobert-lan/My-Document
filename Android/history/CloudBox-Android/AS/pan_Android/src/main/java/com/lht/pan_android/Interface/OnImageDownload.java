package com.lht.pan_android.Interface;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * @ClassName: OnImageDownload
 * @Description: TODO
 * @date 2015年12月4日 下午1:33:22
 * 
 * @author leobert.lan
 * @version 1.0
 */
public interface OnImageDownload {
	void onDownloadSucc(Bitmap bitmap, String c_url, ImageView imageView);
}
