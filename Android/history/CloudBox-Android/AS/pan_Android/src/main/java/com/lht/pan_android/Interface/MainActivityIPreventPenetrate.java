package com.lht.pan_android.Interface;

import com.lht.pan_android.activity.asyncProtected.MainActivity;

import android.app.Activity;

/**
 * @ClassName: MainActivityIPreventPenetrate
 * @Description: TODO
 * @date 2016年3月7日 下午3:41:16
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class MainActivityIPreventPenetrate implements IPreventPenetrate {

	@Override
	public void preventPenetrate(Activity activity, boolean isProtectNeed) {
		if (activity instanceof MainActivity)
			((MainActivity) activity).setActiveStateOfDispatchOnTouch(!isProtectNeed);
	}

}
