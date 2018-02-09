package com.lht.cloudjob.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.SystemClock;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.R;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.VersionResBean;
import com.lht.cloudjob.service.NotificationCancelService;
import com.lht.cloudjob.service.VersionUpdateService;
import com.lht.cloudjob.util.notification.NotificationUtil;

import java.lang.ref.WeakReference;

/**
 * @author leobert.lan
 * @version 1.0
 */
public class VersionUtil {

    /**
     * @param mContext not null
     * @return version
     */
    public static String getVersion(Context mContext) {
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class VersionCheckCallback implements ApiModelCallback<VersionResBean> {

        private WeakReference<Context> contextRef;

        public VersionCheckCallback(Context context) {
            contextRef = new WeakReference<>(context);
        }

        @Override
        public void onSuccess(BaseBeanContainer<VersionResBean> beanContainer) {
            VersionResBean versionResBean = beanContainer.getData();
            VersionResBean.VersionInfoData data = versionResBean.getData();
            if (data == null) {
                return;
            }
            //发出通知，提示用户更新
            sendUpdateNotification(data);
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            //ignore
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            //ignore
        }

        private void sendUpdateNotification(VersionResBean.VersionInfoData data) {
            int requestCode = (int) SystemClock.uptimeMillis();
            if (contextRef == null) {
                return;
            }
            Context context = contextRef.get();
            if (context == null) {
                return;
            }
            NotificationUtil notificationUtil = new NotificationUtil(context);
//            PendingIntent leftIntent = PendingIntent.getActivity(context, requestCode,
//                    new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
            Intent cancelIntent = NotificationCancelService.newStartIntent(notificationUtil);
            PendingIntent leftIntent = PendingIntent.getService(context, requestCode,
                    cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent startUpdateServiceIntent = new Intent(context, VersionUpdateService.class);
            startUpdateServiceIntent.putExtra(VersionUpdateService.VERSION_INFO, JSON.toJSONString(data));
            startUpdateServiceIntent.putExtra(VersionUpdateService.KEY_CANCEL_ID, notificationUtil.getNotificationId());
            PendingIntent rightIntent = PendingIntent.getService(context, requestCode,
                    startUpdateServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            notificationUtil.notifyWithButton(R.drawable.icon_small,
                    R.drawable.ic_action_ignore,
                    context.getString(R.string.v1020_versionupdate_dialog_btn_noupdate),
                    leftIntent, R.drawable.ic_action_update,
                    context.getString(R.string.v1020_versionupdate_dialog_btn_update),
                    rightIntent,
                    context.getString(R.string.v1020_versionupdate_notification_ticker),
                    context.getString(R.string.v1020_versionupdate_dialog_notification_title),
                    context.getString(R.string.v1020_versionupdate_dialog_notification_description),
                    true, true, true);
        }
    }

}
