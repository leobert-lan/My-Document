package com.lht.cloudjob.customview;


import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.lht.cloudjob.R;
import com.lht.cloudjob.interfaces.umeng.IUmengEventKey;
import com.lht.cloudjob.share.DefaultShareIMPL;
import com.lht.cloudjob.share.IShare;
import com.lht.cloudjob.share.QShareImage7TextBean;
import com.lht.cloudjob.share.SinaShareBean;
import com.lht.cloudjob.share.WeChatShareBean;
import com.lht.cloudjob.util.string.StringUtil;
import com.sina.weibo.sdk.api.WebpageObject;

/**
 * ClassName: ThirdPartyShareItemClickListenerImpl
 * Description: TODO
 * date 2016年3月29日 上午10:20:09
 *
 * @author leobert.lan
 * @version 1.0
 */
public class ThirdPartyShareItemClickListenerImpl
        implements ThirdPartySharePopWins.OnThirdPartyShareItemClickListener, OnShareClick,
        IUmengEventKey {

    private final Activity mActivity;
    IShare iShare;

    private String from;

    private String title;

    private String defSummary = null;

    /**
     * bitmap:sina使用的默认值
     */
    private Bitmap bitmap;

    private String logoUrl = "https://req.vsochina.com/images/phone/icon_launcher.png";

    /**
     * bitmapRes:微信使用的默认值
     */
    private int bitmapRes = R.drawable.v1011_drawable_icon_app_roundrect;

    public ThirdPartyShareItemClickListenerImpl(Activity activity) {
        mActivity = activity;
        iShare = new DefaultShareIMPL(mActivity);
        Resources r = activity.getResources();
        bitmap = BitmapFactory.decodeResource(r, R.drawable.v1011_drawable_icon_app_roundrect);
        from = activity.getString(R.string.app_name);
        title = activity.getString(R.string.share_title);
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    @Override
    public void onClick(ThirdPartySharePopWins popWins, int itemIndex, View item) {
        popWins.dismiss();
        String targetUrl = popWins.getShareContent();
        String summary = popWins.getShareSummary();
        switch (itemIndex) {
            case 0:
//			((UMengActivity) mActivity).reportCountEvent(CALC_CB_SHARE_SINA);
                onClickSinaShare(targetUrl, summary);
                break;
            case 1:
//			((UMengActivity) mActivity).reportCountEvent(CALC_CB_SHARE_QQ);
                onClickQQShare(targetUrl, summary);
                break;
            case 2:
//			((UMengActivity) mActivity).reportCountEvent(CALC_CB_SHARE_WX);
                onClickWechatShare(targetUrl, summary);
                break;
            case 3:
//			((UMengActivity) mActivity).reportCountEvent(CALC_CB_SHARE_QQ_ZONE);
                onClickQZoneShare(targetUrl, summary);
                break;
            case 4:
//			((UMengActivity) mActivity).reportCountEvent(CALC_CB_SHARE_WXFRIEND);
                onClickWechatFCShare(targetUrl, summary);
                break;
            case 5:
//			onClickCopy(targetUrl, popWins.getiPreventPenetrate());
                break;
            default:
                break;
        }
    }

    @Override
    public void onClickQQShare(String targetUrl, String summary) {
        iShare.share2QQ(getQQBean(targetUrl, summary));
    }

    @Override
    public void onClickQZoneShare(String targetUrl, String summary) {
        iShare.share2QZone(getQZBean(targetUrl, summary));

    }

    @Override
    public void onClickWechatShare(String targetUrl, String summary) {
        iShare.share2Wechat(getWechatBean(targetUrl, summary));
    }

    @Override
    public void onClickWechatFCShare(String targetUrl, String summary) {
        iShare.share2WechatFC(getWechatFCBean(targetUrl, summary));
    }

    @Override
    public void onClickSinaShare(String targetUrl, String summary) {
        iShare.share2Sina(getSinaBean(targetUrl, summary));
    }

//	@Override
//	public void onClickCopy(String targetUrl, IPreventPenetrate iPreventPenetrate) {
//
//		AlertDialogWithImageCreator clipboardCopyedAlertCreator = new AlertDialogWithImageCreator
// (mActivity,
//				iPreventPenetrate);
//
//		clipboardCopyedAlertCreator.setContentRes(R.string.share_copy_url);
//		clipboardCopyedAlertCreator.setImgRes(R.drawable.daduigou);
//		clipboardCopyedAlertCreator.setPositiveButton(R.string.string_sure);
//		clipboardCopyedAlertCreator.create().show();
//		ClipboardManager myClipboardManager = (ClipboardManager) mActivity.getSystemService
// (Context.CLIPBOARD_SERVICE);
//		ClipData myClip;
//		myClip = ClipData.newPlainText("text", targetUrl);
//		myClipboardManager.setPrimaryClip(myClip);
//	}

    public QShareImage7TextBean getQQBean(String targetUrl, String summary) {
        QShareImage7TextBean bean = new QShareImage7TextBean();
        bean.setFlag(QShareImage7TextBean.FLAG_QQ);
        bean.setTargetUrl(targetUrl);
        bean.setFrom(from);
        bean.setTitle(title);
        bean.setSummary(StringUtil.isEmpty(defSummary) ? summary + "" : defSummary);
        bean.setImageUrl(logoUrl);
        return bean;
    }

    QShareImage7TextBean getQZBean(String targetUrl, String summary) {
        QShareImage7TextBean bean = new QShareImage7TextBean();
        bean.setFlag(QShareImage7TextBean.FLAG_QZONE);
        bean.setTargetUrl(targetUrl);
        bean.setFrom(from);
        bean.setTitle(title);
        bean.setSummary(StringUtil.isEmpty(defSummary) ? summary + "" : defSummary);
        bean.setImageUrl(logoUrl);
        return bean;
    }

    public WeChatShareBean getWechatBean(String targetUrl, String summary) {
        WeChatShareBean bean = new WeChatShareBean();
        bean.setUrl(targetUrl);
        bean.setPicResource(bitmapRes);
        bean.setTitle(title);
        bean.setContent(StringUtil.isEmpty(defSummary) ? summary + "" : defSummary);
        bean.setShareType(WeChatShareBean.FLAG_WECHAT);
        return bean;
    }

    WeChatShareBean getWechatFCBean(String targetUrl, String summary) {
        WeChatShareBean bean = new WeChatShareBean();
        bean.setUrl(targetUrl);
        bean.setPicResource(bitmapRes);
        bean.setTitle(title);
        bean.setContent(StringUtil.isEmpty(defSummary) ? summary + "" : defSummary);
        bean.setShareType(WeChatShareBean.FLAG_WECHATFC);
        return bean;
    }

    SinaShareBean getSinaBean(String targetUrl, String summary) {
        SinaShareBean bean = new SinaShareBean();

        String str = StringUtil.isEmpty(defSummary) ? title + "" : defSummary + "";
        bean.setText(str + targetUrl);

        // TODO
        bean.setContent(StringUtil.isEmpty(summary) ? defSummary : summary);
        bean.setContentType(SinaShareBean.CTYPE_WEB);
        bean.setImage(bitmap);

        return bean;
    }

    public void setSummary(String string) {
        defSummary = string;
    }

}

interface OnShareClick {
    void onClickQQShare(String targetUrl, String summary);

    void onClickQZoneShare(String targetUrl, String summary);

    void onClickWechatShare(String targetUrl, String summary);

    void onClickWechatFCShare(String targetUrl, String summary);

    void onClickSinaShare(String targetUrl, String summary);

//	void onClickCopy(String targetUrl, IPreventPenetrate iPreventPenetrate);
}
