package com.lht.cloudjob.service;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;

import com.lht.cloudjob.MainApplication;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.notification.NotificationUtil;

/**
 * <p><b>Package</b> com.lht.cloudjob.service
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> NotificationCancelService
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/11/24.
 */

public class NotificationCancelService extends IntentService {

    public static final String KEY_DATA = "_data_notificationId";


    public NotificationCancelService() {
        super(NotificationCancelService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            DLog.e(getClass(), "intent is null");
            return;
        }
        int id = intent.getIntExtra(KEY_DATA, -1);
        if (id >= 0) {
            DLog.d(getClass(), "try to cancel notification,id is:" + id);
            NotificationManager nm = (NotificationManager) getSystemService(Activity.NOTIFICATION_SERVICE);
            nm.cancel(id);
        } else {
            DLog.e(getClass(), "the id of notification to cancel is less than 0 ");
        }
    }

    public static Intent newStartIntent(NotificationUtil notificationUtil) {
        Intent intent = new Intent(MainApplication.getOurInstance(), NotificationCancelService.class);
        intent.putExtra(KEY_DATA, notificationUtil.getNotificationId());
        return intent;
    }
}
