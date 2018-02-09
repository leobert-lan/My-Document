package com.lht.pan_android.Interface;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * @ClassName: OnLocalImageLoad
 * @Description: 加载成功
 * @date 2016年1月8日 上午9:41:37
 * 
 * @author leobert.lan
 * @version 1.0
 */
public interface OnLocalImageLoad {
	void onLocalLoadSucc(Bitmap bitmap, String c_url, ImageView imageView);
}
