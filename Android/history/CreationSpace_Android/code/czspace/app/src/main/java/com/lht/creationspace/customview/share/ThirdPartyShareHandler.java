package com.lht.creationspace.customview.share;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.lht.creationspace.base.MainApplication;
import com.lht.creationspace.R;
import com.lht.creationspace.umeng.IUmengEventKey;
import com.lht.creationspace.social.share.DefaultShareIMPL;
import com.lht.creationspace.social.share.IShare;
import com.lht.creationspace.social.share.QShareImage7TextBean;
import com.lht.creationspace.social.share.QShareImageBean;
import com.lht.creationspace.social.share.SinaShareBean;
import com.lht.creationspace.social.share.SinaShareImageBean;
import com.lht.creationspace.social.share.WeChatShareBean;
import com.lht.creationspace.social.share.WeChatShareImageBean;
import com.lht.creationspace.util.string.StringUtil;
import com.lht.creationspace.util.toast.ToastUtils;

/**
 * ClassName: ThirdPartyShareItemClickListenerImpl
 * Description: TODO
 * date 2016年3月29日 上午10:20:09
 *
 * @author leobert.lan
 * @version 1.0
 */
public class ThirdPartyShareHandler
        implements ThirdPartySharePopWins.OnThirdPartyShareItemClickListener, OnShareClick,
        IUmengEventKey {

    private final Activity mActivity;
    private IShare iShare;

    private String from;

    private final String DEF_TITLE = "FLAG社-你的脑洞超乎你想象";

    private final String DEF_SUMMARY =
            "这里汇聚了敢想，敢拼，乐于分享的创作者与爱好者，被吐槽图样图森破？来这里证明自己！";

    //应用标题：flag社-你的脑洞超乎你想象
//    这里汇聚了敢想，敢拼，乐于分享的创作者与爱好者，被吐槽图样图森破？来这里证明自己！

    /**
     * bitmap:sina使用的默认值
     */
    private Bitmap bitmap;

    private String logoUrl =
            "https://maker.vsochina.com/images/icon.png";


    /**
     * bitmapRes:微信使用的默认值
     */
    private int bitmapRes = R.drawable.v1000_drawable_icon_launcher_roundrect;

    public ThirdPartyShareHandler(Activity activity) {
        mActivity = activity;
        iShare = new DefaultShareIMPL(mActivity);
        Resources r = activity.getResources();
        bitmap = BitmapFactory.decodeResource(r,
                R.drawable.v1000_drawable_icon_launcher_roundrect);
        from = activity.getString(R.string.app_name);
    }


    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }


    @Override
    public void onClick(ThirdPartySharePopWins popWins, int itemIndex, View item) {
        popWins.dismiss();
        ThirdPartySharePopWins.ShareData shareData = popWins.getShareData();
        switch (itemIndex) {
            case 0:
//			((UMengActivity) mActivity).reportCountEvent(CALC_CB_SHARE_SINA);
                onClickSinaShare(shareData);
                break;
            case 1:
//			((UMengActivity) mActivity).reportCountEvent(CALC_CB_SHARE_QQ);
                onClickQQShare(shareData);
                break;
            case 2:
//			((UMengActivity) mActivity).reportCountEvent(CALC_CB_SHARE_WX);
                onClickWechatShare(shareData);
                break;
            case 3:
//			((UMengActivity) mActivity).reportCountEvent(CALC_CB_SHARE_QQ_ZONE);
                onClickQZoneShare(shareData);
                break;
            case 4:
//			((UMengActivity) mActivity).reportCountEvent(CALC_CB_SHARE_WXFRIEND);
                onClickWechatFCShare(shareData);
                break;
            case 5:
                onClickCopy((ThirdPartySharePopWins.UrlShareData) shareData);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClickQQShare(ThirdPartySharePopWins.ShareData data) {
        if (data instanceof ThirdPartySharePopWins.ImageShareData) {
            iShare.share2QQ(getOnlyImageQQBean((ThirdPartySharePopWins.ImageShareData) data));
        } else if (data instanceof ThirdPartySharePopWins.UrlShareData) {
            iShare.share2QQ(getQQBean((ThirdPartySharePopWins.UrlShareData) data));
        } else {
            //error
        }
    }


    @Override
    public void onClickQZoneShare(ThirdPartySharePopWins.ShareData data) {
        if (data instanceof ThirdPartySharePopWins.ImageShareData) {
            iShare.share2QZone(getOnlyImageQZBean((ThirdPartySharePopWins.ImageShareData) data));
        } else if (data instanceof ThirdPartySharePopWins.UrlShareData) {
            iShare.share2QZone(getQZBean((ThirdPartySharePopWins.UrlShareData) data));
        } else {
            //error
        }

    }

    @Override
    public void onClickWechatShare(ThirdPartySharePopWins.ShareData data) {
        if (data instanceof ThirdPartySharePopWins.ImageShareData) {
            iShare.share2Wechat(getOnlyImageWechatBean((ThirdPartySharePopWins.ImageShareData) data));
        } else if (data instanceof ThirdPartySharePopWins.UrlShareData) {
            iShare.share2Wechat(getWechatBean((ThirdPartySharePopWins.UrlShareData) data));
        } else {
            //error
        }
    }

    @Override
    public void onClickWechatFCShare(ThirdPartySharePopWins.ShareData data) {
        if (data instanceof ThirdPartySharePopWins.ImageShareData) {
            iShare.share2WechatFC(getOnlyImageWechatFCBean((ThirdPartySharePopWins.ImageShareData) data));
        } else if (data instanceof ThirdPartySharePopWins.UrlShareData) {
            iShare.share2WechatFC(getWechatFCBean((ThirdPartySharePopWins.UrlShareData) data));
        } else {
            //error
        }
    }

    @Override
    public void onClickSinaShare(ThirdPartySharePopWins.ShareData data) {
        if (data instanceof ThirdPartySharePopWins.ImageShareData) {
            iShare.share2Sina(getOnlyImageSinaBean((ThirdPartySharePopWins.ImageShareData) data));
        } else if (data instanceof ThirdPartySharePopWins.UrlShareData) {
            iShare.share2Sina(getSinaBean((ThirdPartySharePopWins.UrlShareData) data));
        }
    }

    @Override
    public void onClickCopy(ThirdPartySharePopWins.UrlShareData data) {
        ClipboardManager myClipboardManager = (ClipboardManager) mActivity.getSystemService
                (Context.CLIPBOARD_SERVICE);
        ClipData myClip;
        myClip = ClipData.newPlainText("text", data.getOpenUrl());
        myClipboardManager.setPrimaryClip(myClip);
        ToastUtils.show(MainApplication.getOurInstance(), "已复制到剪切板", ToastUtils.Duration.s);
    }

    public QShareImage7TextBean getQQBean(ThirdPartySharePopWins.UrlShareData data) {
        QShareImage7TextBean bean = new QShareImage7TextBean();
        bean.setFlag(QShareImage7TextBean.FLAG_QQ);
        bean.setTargetUrl(data.getOpenUrl());
        bean.setFrom(from);
        bean.setTitle(StringUtil.isEmpty(data.getShareTitle()) ? DEF_TITLE : data.getShareTitle());
        bean.setSummary(StringUtil.isEmpty(data.getShareSummary()) ? DEF_SUMMARY : data.getShareSummary());
        bean.setImageUrl(logoUrl);
        return bean;
    }

    /**
     * 仅分享图片到QQ
     *
     * @param data
     * @return
     */
    private QShareImageBean getOnlyImageQQBean(ThirdPartySharePopWins.ImageShareData data) {
        QShareImageBean bean = new QShareImageBean();
        bean.setFlag(QShareImageBean.FLAG_QQ);
        bean.setImage(data.getLocalImagePath());
        return bean;
    }

    /**
     * 仅分享图片到QQzone
     *
     * @param data
     * @return
     */
    private QShareImageBean getOnlyImageQZBean(ThirdPartySharePopWins.ImageShareData data) {
        QShareImageBean bean = new QShareImageBean();
        bean.setFlag(QShareImageBean.FLAG_QZONE);
        bean.setImage(data.getLocalImagePath());
        return bean;
    }


    QShareImage7TextBean getQZBean(ThirdPartySharePopWins.UrlShareData data) {
        QShareImage7TextBean bean = new QShareImage7TextBean();
        bean.setFlag(QShareImage7TextBean.FLAG_QZONE);
        bean.setTargetUrl(data.getOpenUrl());
        bean.setFrom(from);
        bean.setTitle(StringUtil.isEmpty(data.getShareTitle()) ? DEF_TITLE : data.getShareTitle());
        bean.setSummary(StringUtil.isEmpty(data.getShareSummary()) ? DEF_SUMMARY : data.getShareSummary());
        bean.setImageUrl(logoUrl);
        return bean;
    }

    public WeChatShareBean getWechatBean(ThirdPartySharePopWins.UrlShareData data) {
        WeChatShareBean bean = new WeChatShareBean();
        bean.setUrl(data.getOpenUrl());
        bean.setPicResource(bitmapRes);
        bean.setTitle(StringUtil.isEmpty(data.getShareTitle()) ? DEF_TITLE : data.getShareTitle());
        bean.setContent(StringUtil.isEmpty(data.getShareSummary()) ? DEF_SUMMARY : data.getShareSummary());
        bean.setShareType(WeChatShareBean.FLAG_WECHAT);
        return bean;
    }

    /**
     * 仅分享图片到微信
     *
     * @param data
     * @return
     */
    private WeChatShareImageBean getOnlyImageWechatBean(ThirdPartySharePopWins.ImageShareData data) {
        WeChatShareImageBean bean = new WeChatShareImageBean();
        bean.setLocalImagePath(data.getLocalImagePath());
        bean.setFlag(WeChatShareImageBean.FLAG_WECHAT);
        return bean;
    }

    WeChatShareBean getWechatFCBean(ThirdPartySharePopWins.UrlShareData data) {
        WeChatShareBean bean = new WeChatShareBean();
        bean.setUrl(data.getOpenUrl());
        bean.setPicResource(bitmapRes);
        bean.setTitle(StringUtil.isEmpty(data.getShareTitle()) ? DEF_TITLE : data.getShareTitle());
        bean.setContent(StringUtil.isEmpty(data.getShareSummary()) ? DEF_SUMMARY : data.getShareSummary());
        bean.setShareType(WeChatShareBean.FLAG_WECHATFC);
        return bean;
    }

    /**
     * 仅分享图片到微信朋友圈
     *
     * @param data
     * @return
     */
    private WeChatShareImageBean getOnlyImageWechatFCBean(ThirdPartySharePopWins.ImageShareData data) {
        WeChatShareImageBean bean = new WeChatShareImageBean();
        bean.setLocalImagePath(data.getLocalImagePath());
        bean.setFlag(WeChatShareImageBean.FLAG_WECHATFC);
        return bean;
    }


    SinaShareBean getSinaBean(ThirdPartySharePopWins.UrlShareData data) {
        SinaShareBean bean = new SinaShareBean();

        String title = StringUtil.isEmpty(data.getShareTitle()) ? DEF_TITLE : data.getShareTitle();
        String summary = StringUtil.isEmpty(data.getShareSummary()) ? DEF_SUMMARY : data.getShareSummary();

        bean.setText(title + data.getOpenUrl());

        bean.setContent(title + "  " + summary);
        bean.setContentType(SinaShareBean.CTYPE_WEB);
        bean.setImage(bitmap);

        return bean;
    }


    private SinaShareImageBean getOnlyImageSinaBean(ThirdPartySharePopWins.ImageShareData data) {
        SinaShareImageBean bean = new SinaShareImageBean();
        bean.setLocalImagePath(data.getLocalImagePath());
        return bean;
    }

}

interface OnShareClick {
    void onClickQQShare(ThirdPartySharePopWins.ShareData data);

    void onClickQZoneShare(ThirdPartySharePopWins.ShareData data);

    void onClickWechatShare(ThirdPartySharePopWins.ShareData data);

    void onClickWechatFCShare(ThirdPartySharePopWins.ShareData data);

    void onClickSinaShare(ThirdPartySharePopWins.ShareData data);

    void onClickCopy(ThirdPartySharePopWins.UrlShareData data);
}
