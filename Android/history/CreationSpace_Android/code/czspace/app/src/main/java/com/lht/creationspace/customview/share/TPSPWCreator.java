package com.lht.creationspace.customview.share;

import android.view.Gravity;

import com.lht.creationspace.R;
import com.lht.creationspace.customview.popup.IPopupHolder;

import java.util.ArrayList;


/**
 * <p><b>Package</b> com.lht.creationspace.customview
 * <p><b>Project</b> czspace
 * <p><b>Classname</b> TPSPWCreator
 * <p><b>Description</b>: 三方分享弹窗创建器
 * <p>
 * Created by leobert on 2017/3/8.
 */
public class TPSPWCreator {

    public static ThirdPartySharePopWins create(IPopupHolder iPopupHolder,
                                                ThirdPartySharePopWins.ShareData shareData) {
        ThirdPartySharePopWins wins = new ThirdPartySharePopWins(iPopupHolder,shareData);

        ArrayList<ThirdPartyShareViewItem> items = new ArrayList<>();
        String[] mTextIds = {"新浪微博", "QQ", "微信", "QQ空间", "朋友圈", "复制链接"};
        Integer[] mThumbIds = {R.drawable.share_plateform_sina, R.drawable.share_plateform_qq,
                R.drawable.share_plateform_wechat, R.drawable.share_plateform_qqzone,
                R.drawable.share_plateform_wechat_fc, R.drawable.share_plateform_copy};
        for (int i = 0; i < mTextIds.length; i++) {
            ThirdPartyShareViewItem item = new ThirdPartyShareViewItem();
            item.setName(mTextIds[i]);
            item.setDrawableRes(mThumbIds[i]);
            items.add(item);
        }

        wins.setItems(items);

        wins.setGravity(Gravity.BOTTOM);

        wins.setOnThirdPartyShareItemClickListener(new ThirdPartyShareHandler(
                iPopupHolder.getHolderActivity()));
        wins.setOutsideClickDismiss();

        return wins;
    }


}
