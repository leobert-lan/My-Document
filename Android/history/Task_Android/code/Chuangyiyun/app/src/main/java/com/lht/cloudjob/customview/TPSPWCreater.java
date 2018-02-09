package com.lht.cloudjob.customview;

import android.view.Gravity;

import com.lht.cloudjob.R;
import com.lht.cloudjob.interfaces.custompopupwins.IPopupHolder;

import java.util.ArrayList;

/**
 * @ClassName: TPSPWCreater
 * @Description: TODO
 * @date 2016年3月29日 下午1:09:27
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class TPSPWCreater {

	public static ThirdPartySharePopWins create(IPopupHolder iPopupHolder) {
		ThirdPartySharePopWins wins = new ThirdPartySharePopWins(iPopupHolder);

		ArrayList<ThirdPartyShareViewItem> items = new ArrayList<>();
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

		wins.setOnThirdPartyShareItemClickListener(new ThirdPartyShareItemClickListenerImpl(
				iPopupHolder.getHolderActivity()));
		wins.setAnimationStyle(R.style.iOSActionSheet);
		wins.setOutsideClickDismiss();
		return wins;
	}

}
