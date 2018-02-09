package com.lht.pan_android.view.popupwins;

import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IPreventPenetrate;

import android.app.Activity;

/**
 * @ClassName: PublicShareOperatePopWinsCreater
 * @Description: TODO
 * @date 2016年3月30日 上午11:32:51
 * 
 * @author leobert.lan
 * @version 1.0
 */
public final class PublicShareOperatePopWinsCreator extends CustomDialogCreator {

	public PublicShareOperatePopWinsCreator(final Activity activity, IPreventPenetrate ippl) {
		super(activity, ippl);
		// setDefault(); insuper
	}

	private static int defPositiveStrRes = R.string.share_tpshare;

	private static int defNegativeStrRes = R.string.string_cancel;

	private static String defContent = null;

	@Override
	void setDefault() {
		builder.setContent(defContent);
		setNegativeButton(defNegativeStrRes);
		setPositiveButton(defPositiveStrRes);
	}

}
