package com.lht.creationspace.social.share;

import android.app.Activity;
import android.util.Log;

import com.lht.creationspace.R;
import com.lht.creationspace.util.debug.DLog;
import com.lht.creationspace.util.toast.ToastUtils;
import com.tencent.tauth.UiError;

/**
 * <p><b>Package</b> com.lht.creationspace.share
 * <p><b>Project</b> czspace
 * <p><b>Classname</b> DefaultShareIMPL
 * <p><b>Description</b>: 图文类型的url分享实现
 * <p>
 * Created by leobert on 2017/4/26.
 */
public class DefaultShareIMPL implements IShare {

    private final Activity mActivity;

    public DefaultShareIMPL(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public void share2QQ(ShareBean bean) {
        if (bean instanceof QShareImageBean) {
            qqShare((QShareImageBean) bean);
        } else if (bean instanceof QShareImage7TextBean) {
            qqShare((QShareImage7TextBean) bean);
        } else {
            DLog.e(getClass(), new DLog.LogLocation(), "call qqshare without a qqshare bean");
        }
    }

    private void qqShare(QShareImageBean bean) {
        QQShareUtil.qShareImageOnly(bean, mActivity, new DefaultQShareListener());
    }

    private void qqShare(QShareImage7TextBean bean) {
        QQShareUtil.qShareImage7Text(bean, mActivity, new DefaultQShareListener());
    }

    @Override
    public void share2QZone(ShareBean bean) {
        this.share2QQ(bean);
        // 业务区分是通过bean内的flag的
    }

    @Override
    public void share2Wechat(ShareBean bean) {
        if (bean instanceof WeChatShareBean) {
            new WeChatShareUtil(mActivity).weChatShareWebPage((WeChatShareBean) bean);
        } else if (bean instanceof WeChatShareImageBean) {
            new WeChatShareUtil(mActivity).weChatSharePicture((WeChatShareImageBean) bean);
        } else {
            DLog.e(getClass(), new DLog.LogLocation(), "call wechat share with a error bean");
        }
    }

    @Override
    public void share2WechatFC(ShareBean bean) {
        this.share2Wechat(bean);
    }

    @Override
    public void share2Sina(ShareBean bean) {
        if (bean instanceof SinaShareBean) {
            SinaShareUtil.shareByAPI(mActivity, (SinaShareBean) bean);
        } else if (bean instanceof SinaShareImageBean) {
            SinaShareUtil.shareOnlyImages(mActivity, (SinaShareImageBean) bean);
        } else {
            DLog.e(getClass(), new DLog.LogLocation(), "call sina share without a sina bean");
        }
    }

    /**
     * @author leobert.lan
     * @version 1.0
     * @ClassName: DefaultQShareListener
     * @Description: QQ相关分享回调接口实现类
     * @date 2016年3月25日 下午3:07:10
     * @since JDK 1.7
     */
    class DefaultQShareListener implements IQShareListener {

        @Override
        public void onCancel() {
            ToastUtils.show(mActivity, mActivity.getString(R.string.v1010_default_tp_share_exit), ToastUtils.Duration.s);
//            Toast.makeText(mActivity, mActivity.getString(R.string.v1010_default_tp_share_exit), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onComplete(Object arg0) {
            ToastUtils.show(mActivity, mActivity.getString(R.string.v1010_default_tp_share_success), ToastUtils.Duration.s);
//            Toast.makeText(mActivity, mActivity.getString(R.string.v1010_default_tp_share_success), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(UiError arg0) {
            Log.d("lmsg", "qq share failure:" + arg0.errorCode + "  " + arg0.errorMessage + "  " + arg0.errorDetail);
            ToastUtils.show(mActivity, mActivity.getString(R.string.v1010_default_tp_share_failure), ToastUtils.Duration.s);
//            Toast.makeText(mActivity, mActivity.getString(R.string.v1010_default_tp_share_failure), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onQQNotAvailable() {
            ToastUtils.show(mActivity, mActivity.getString(R.string.v1010_default_tp_failure_QQnotinstalled), ToastUtils.Duration.s);
//            Toast.makeText(mActivity, mActivity.getString(R.string.v1010_default_tp_failure_QQnotinstalled), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onQZoneNotAvailable() {
            ToastUtils.show(mActivity, mActivity.getString(R.string.v1010_default_tp_failure_qqzonenotinstalled), ToastUtils.Duration.s);
//            Toast.makeText(mActivity, mActivity.getString(R.string.v1010_default_tp_failure_qqzonenotinstalled), Toast.LENGTH_SHORT).show();
        }

    }

}
