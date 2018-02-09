package com.lht.pan_android.view.popupwins;

import java.util.ArrayList;

import com.lht.pan_android.R;
import com.lht.pan_android.Interface.MainActivityIPreventPenetrate;

import android.app.Activity;
import android.view.Gravity;

/**
 * @ClassName: TPSPWCreater
 * @Description: TODO
 * @date 2016年3月29日 下午1:09:27
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class TPSPWCreater {

	public static ThirdPartySharePopWins create(final Activity activity) {
		ThirdPartySharePopWins wins = new ThirdPartySharePopWins(activity, new MainActivityIPreventPenetrate());

		ArrayList<ThirdPartyShareViewItem> items = new ArrayList<ThirdPartyShareViewItem>();
		String[] mTextIds = { "新浪微博", "QQ", "微信", "QQ空间", "朋友圈", "复制链接" };
		Integer[] mThumbIds = { R.drawable.share_plateform_sina, R.drawable.share_plateform_qq,
				R.drawable.share_plateform_wechat, R.drawable.share_plateform_qqzone,
				R.drawable.share_plateform_wechat_fc, R.drawable.share_plateform_copy };
		for (int i = 0; i < mTextIds.length; i++) {
			ThirdPartyShareViewItem item = new ThirdPartyShareViewItem();
			item.setName(mTextIds[i]);
			item.setDrawableRes(mThumbIds[i]);
			items.add(item);
		}

		wins.setItems(items);

		wins.setGravity(Gravity.BOTTOM);

		wins.setOnThirdPartyShareItemClickListener(new ThirdPartyShareItemClickListenerImpl(activity));
		wins.setAnimationStyle(R.style.iOSActionSheet);
		wins.setOutsideClickDismiss();
		return wins;
	}

}
