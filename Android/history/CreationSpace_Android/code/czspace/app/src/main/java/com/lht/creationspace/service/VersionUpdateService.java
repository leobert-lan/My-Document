package com.lht.creationspace.service;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.R;
import com.lht.creationspace.base.MainApplication;
import com.lht.creationspace.base.model.IFileDownloadCallbacks;
import com.lht.creationspace.base.model.pojo.DownloadEntity;
import com.lht.creationspace.module.pub.model.DownloadModel;
import com.lht.creationspace.module.setting.model.CheckVersionUpdateModel;
import com.lht.creationspace.util.debug.DLog;

import java.io.File;

import static com.lht.creationspace.util.VersionUtil.updateVersion;


/**
 * 更新服务
 * Created by chhyu on 2016/11/23.
 */
public class VersionUpdateService extends Service {
    public static final String VERSION_INFO = "version_info";
    public static final String KEY_CANCEL_ID = "_data_notificationId";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String versionInfo = intent.getStringExtra(VERSION_INFO);
        int id = intent.getIntExtra(KEY_CANCEL_ID, -1);
        if (id >= 0) {
            DLog.d(getClass(), "try to cancel notification,id is:" + id);
            NotificationManager nm = (NotificationManager) getSystemService(Activity.NOTIFICATION_SERVICE);
            nm.cancel(id);
        } else {
            DLog.e(getClass(), "the id of notification to cancel is less than 0 ");
        }
        DLog.d(DLog.Lmsg.class, "版本信息：" + versionInfo);
        CheckVersionUpdateModel.VersionResBean.VersionInfoData data = JSON.parseObject(versionInfo, CheckVersionUpdateModel.VersionResBean.VersionInfoData.class);
        DownloadEntity entity = DownloadEntity.copyFromVersionResBean(data);

        File downloadDir = MainApplication.getOurInstance().getSystemDownloadDir();
        DownloadModel downloadModel = new DownloadModel(entity, downloadDir, new ApkDownloadCallback());
        downloadModel.doRequest(this);
        return super.onStartCommand(intent, flags, startId);
    }

    class ApkDownloadCallback implements IFileDownloadCallbacks {
        @Override
        public void onNoInternet() {

        }

        @Override
        public void onMobileNet() {
            Toast.makeText(getApplicationContext(), R.string.v1020_versionupdate_dialog_onmobile_remind, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFileNotFoundOnServer() {
            Toast.makeText(getApplicationContext(), R.string.v1020_toast_download_onnotfound, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDownloadStart(DownloadEntity entity) {

        }

        @Override
        public void onDownloadCancel() {

        }

        @Override
        public void onDownloadSuccess(DownloadEntity entity, File file) {
            updateVersion(getBaseContext(),file);
        }


        @Override
        public void onDownloading(DownloadEntity entity, long current, long total) {

        }

        @Override
        public void onNoEnoughSpace() {
            Toast.makeText(getApplicationContext(), R.string.v1020_toast_download_onnoenoughspace, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void downloadFailure() {
            Toast.makeText(getApplicationContext(), R.string.v1020_versionupdate_text_download_fuilure, Toast.LENGTH_SHORT).show();
        }
    }
}
