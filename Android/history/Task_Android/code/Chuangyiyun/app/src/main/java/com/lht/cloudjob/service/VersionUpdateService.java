package com.lht.cloudjob.service;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.MainApplication;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.BaseActivity;
import com.lht.cloudjob.mvp.model.DownloadModel;
import com.lht.cloudjob.mvp.model.bean.VersionResBean;
import com.lht.cloudjob.mvp.model.interfaces.IFileDownloadCallbacks;
import com.lht.cloudjob.mvp.model.pojo.DownloadEntity;
import com.lht.cloudjob.util.debug.DLog;

import java.io.File;


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
        VersionResBean.VersionInfoData data = JSON.parseObject(versionInfo, VersionResBean.VersionInfoData.class);
        DownloadEntity entity = DownloadEntity.copyFromVersionResBean(data);

        File downloadDir = BaseActivity.getPublicDownloadDir();
        DownloadModel downloadModel = new DownloadModel(entity, downloadDir, new ApkDownloadCallback());
        downloadModel.doRequest(this);
        return super.onStartCommand(intent, flags, startId);
    }

    private class ApkDownloadCallback implements IFileDownloadCallbacks {
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
            Intent intent = new Intent();
            // 执行动作
            intent.setAction(Intent.ACTION_VIEW);
            // 执行的数据类型
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getBaseContext().startActivity(intent);
            MainApplication.getOurInstance().finishAll();
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
