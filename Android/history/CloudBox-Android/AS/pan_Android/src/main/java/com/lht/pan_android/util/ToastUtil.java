package com.lht.pan_android.util;

import android.content.Context;
import android.widget.Toast;

/**
 * @ClassName: ToastUtil
 * @Description: TODO
 * @date 2015年11月6日 上午9:49:03
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class ToastUtil {

	public static void show(Context c, int sRid, Duration d) {
		int duration = 0;
		switch (d) {
		case l:
			duration = Toast.LENGTH_LONG;
			break;
		case s:
			duration = Toast.LENGTH_SHORT;
			break;
		default:
			break;
		}
		String msg = c.getResources().getString(sRid);
		Toast.makeText(c, msg, duration).show();
	}

	// public static void showOnCenter(Context c, int sRid, Duration d) {
	// int duration = 0;
	// switch (d) {
	// case l:
	// duration = Toast.LENGTH_LONG;
	// break;
	// case s:
	// duration = Toast.LENGTH_SHORT;
	// break;
	// default:
	// break;
	// }
	// Toast t = Toast.makeText(c, c.getResources().getString(sRid), duration);
	// t.setGravity(Gravity.CENTER, 0, 0);
	// t.show();
	// }

	public enum Duration {
		l, s;
	}

}
