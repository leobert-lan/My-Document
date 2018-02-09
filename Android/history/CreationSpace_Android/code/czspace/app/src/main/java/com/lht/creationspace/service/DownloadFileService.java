package com.lht.creationspace.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.base.MainApplication;
import com.lht.creationspace.R;
import com.lht.creationspace.module.pub.model.DownloadModel;
import com.lht.creationspace.base.model.IFileDownloadCallbacks;
import com.lht.creationspace.base.model.pojo.DownloadEntity;
import com.lht.creationspace.hybrid.native4js.expandreqbean.NF_DownloadReqBean;
import com.lht.creationspace.hybrid.native4js.impl.DownloadImpl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.HashMap;

public class DownloadFileService extends Service {

    public static String DOWNLOAD_INFO = "download_info";

    private HashMap<String, DownloadModel> downloadModelHashMap = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String downloadInfo = intent.getStringExtra(DOWNLOAD_INFO);

//        Log.d("lmsg", "download info:\r" + downloadInfo);

        NF_DownloadReqBean bean = JSON.parseObject(downloadInfo, NF_DownloadReqBean.class);
        DownloadEntity entity = DownloadEntity.copyFromDownloadBean(bean);
        DownloadModel model = new DownloadModel(entity, MainApplication.getOurInstance().getLocalDownloadCacheDir(),
                new DownloadFileCallback(bean, entity));

        addTask(bean.getUrl_download(), model);
        model.doRequest(this);


        return super.onStartCommand(intent, flags, startId);
    }

    private void addTask(String tag, DownloadModel model) {
        synchronized (this) {
            if (downloadModelHashMap.containsKey(tag)) {
                DownloadModel tagModel = downloadModelHashMap.get(tag);
                if (tagModel != null) {
                    tagModel.cancelDownload();
                }
            }
            downloadModelHashMap.put(tag, model);
        }
    }

    @Subscribe
    public void onEventMainThread(DownloadImpl.VsoBridgeDownloadCancelEvent event) {
        synchronized (this) {
            String tag = event.getTag();
            if (downloadModelHashMap.containsKey(tag)) {
                DownloadModel tagModel = downloadModelHashMap.remove(tag);
                if (tagModel != null) {
                    tagModel.cancelDownload();
                } else {
//                    Log.i("tmsg", "model is null,tag:" + tag);
                }
            } else {
//                Log.i("tmsg", "do not have the download tag:" + tag);
            }
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    static class DownloadFileCallback implements IFileDownloadCallbacks {
        private final NF_DownloadReqBean nfDownloadReqBean;
        private final DownloadEntity downloadEntity;

        DownloadFileCallback(NF_DownloadReqBean nfDownloadReqBean, DownloadEntity downloadEntity) {
            this.nfDownloadReqBean = nfDownloadReqBean;
            this.downloadEntity = downloadEntity;
        }

        private Context getApplicationContext() {
            return MainApplication.getOurInstance();
        }

        @Override
        public void onNoInternet() {
            Toast.makeText(getApplicationContext(), R.string.v1000_toast_net_exception, Toast.LENGTH_SHORT).show();
            DownloadImpl.VsoBridgeDownloadEvent event = newDownLoadEventInstance(DownloadImpl.VsoBridgeDownloadEvent.STATUS_ERROR,
                    0, getApplicationContext().getString(R.string.v1000_toast_net_exception));
            postEvent(event);
        }

        @Override
        public void onMobileNet() {
            Toast.makeText(getApplicationContext(), R.string.v1020_dialog_preview_onmobile_toolarge, Toast.LENGTH_SHORT).show();
            DownloadImpl.VsoBridgeDownloadEvent event = newDownLoadEventInstance(DownloadImpl.VsoBridgeDownloadEvent.STATUS_DEFAULT,
                    0, getApplicationContext().getString(R.string.v1020_versionupdate_dialog_onmobile_remind));
            postEvent(event);
        }

        @Override
        public void onFileNotFoundOnServer() {
            Toast.makeText(getApplicationContext(), R.string.v1020_toast_download_onnotfound, Toast.LENGTH_SHORT).show();
            DownloadImpl.VsoBridgeDownloadEvent event = newDownLoadEventInstance(DownloadImpl.VsoBridgeDownloadEvent.STATUS_ERROR,
                    0, getApplicationContext().getString(R.string.v1020_toast_download_onnotfound));
            postEvent(event);
        }

        @Override
        public void onDownloadStart(DownloadEntity entity) {
            DownloadImpl.VsoBridgeDownloadEvent event = newDownLoadEventInstance(DownloadImpl.VsoBridgeDownloadEvent.STATUS_ONSTART, 0, "开始下载");
            postEvent(event);
        }

        @Override
        public void onDownloadCancel() {
            DownloadImpl.VsoBridgeDownloadEvent event = newDownLoadEventInstance(DownloadImpl.VsoBridgeDownloadEvent.STATUS_CANCEL, 0, "取消下载");
            postEvent(event);
        }

        @Override
        public void onDownloadSuccess(DownloadEntity entity, File file) {
            DownloadImpl.VsoBridgeDownloadEvent event = newDownLoadEventInstance(DownloadImpl.VsoBridgeDownloadEvent.STATUS_SUCCESS,
                    entity.getFileSize(), "下载成功");
            event.setFile(file);
            postEvent(event);
        }

        @Override
        public void onDownloading(DownloadEntity entity, long current, long total) {
            DownloadImpl.VsoBridgeDownloadEvent event = newDownLoadEventInstance(DownloadImpl.VsoBridgeDownloadEvent.STATUS_DOWNLOADING,
                    current, "正在下载");

            postEvent(event);
        }

        @Override
        public void onNoEnoughSpace() {
            Toast.makeText(getApplicationContext(), R.string.v1020_toast_download_onnoenoughspace, Toast.LENGTH_SHORT).show();
            DownloadImpl.VsoBridgeDownloadEvent event = newDownLoadEventInstance(DownloadImpl.VsoBridgeDownloadEvent.STATUS_ERROR,
                    0, getApplicationContext().getString(R.string.v1020_toast_download_onnoenoughspace));
            postEvent(event);
        }

        @Override
        public void downloadFailure() {
            Toast.makeText(getApplicationContext(), R.string.v1020_versionupdate_text_download_fuilure, Toast.LENGTH_SHORT).show();
            DownloadImpl.VsoBridgeDownloadEvent event = newDownLoadEventInstance(DownloadImpl.VsoBridgeDownloadEvent.STATUS_ERROR,
                    0, getApplicationContext().getString(R.string.v1020_versionupdate_text_download_fuilure));
            postEvent(event);
        }

        private void postEvent(DownloadImpl.VsoBridgeDownloadEvent event) {
            EventBus.getDefault().post(event);
        }

        private DownloadImpl.VsoBridgeDownloadEvent newDownLoadEventInstance(int status, long currentSize, String msg) {
            DownloadImpl.VsoBridgeDownloadEvent event = new DownloadImpl.VsoBridgeDownloadEvent();
            event.setStatus(status);
            event.setDownloadEntity(downloadEntity);
            event.setCurrentSize(currentSize);
            event.setTotalSize(downloadEntity.getFileSize());
            event.setUniqueTag(nfDownloadReqBean.getUrl_download());
            event.setMsg(msg);
            return event;
        }
    }
}
