package com.lht.cloudjob.mvp.presenter;

import android.content.Context;

import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.BaseActivity;
import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.DownloadModel;
import com.lht.cloudjob.mvp.model.interfaces.IFileDownloadCallbacks;
import com.lht.cloudjob.mvp.model.pojo.DownloadEntity;
import com.lht.cloudjob.mvp.model.pojo.PreviewImage;
import com.lht.cloudjob.mvp.viewinterface.IImagePreviewActivity;
import com.lht.cloudjob.util.file.FileUtils;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.lht.customwidgetlib.actionsheet.OnActionSheetItemClickListener;

import java.io.File;
import java.util.Locale;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> ImagePreviewActivityPresenter
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/11/7.
 */

public class ImagePreviewActivityPresenter implements IApiRequestPresenter {
    private IImagePreviewActivity iImagePreviewActivity;
    private DownloadModel model;

    public ImagePreviewActivityPresenter(IImagePreviewActivity iImagePreviewActivity) {
        this.iImagePreviewActivity = iImagePreviewActivity;
    }

    public void callDowmloadImage(final PreviewImage image) {
        final String format = "下载原图(%s)";
        final String _formated = String.format(Locale.ENGLISH, format, FileUtils.calcSize(image.getFileSize()));


        iImagePreviewActivity.showDownloadAlertActionsheet(new String[]{_formated}, new OnActionSheetItemClickListener() {
            @Override
            public void onActionSheetItemClick(int position) {
                if (position == 0) {
                    downloadImage(image);
                }
            }
        });
    }

    private void downloadImage(PreviewImage previewImage) {
        DownloadEntity.copyFromPreviewImage(previewImage);
        DownloadEntity entity = DownloadEntity.copyFromPreviewImage(previewImage);
        File downloadDir = BaseActivity.getSystemImageDir();
        model = new DownloadModel(entity, downloadDir, new FileDownloadCallbacks());
        model.doRequest(iImagePreviewActivity.getActivity());
    }

    public boolean cancelDownload() {
        if (model != null) {
            iImagePreviewActivity.cancelWaitView();
            return model.cancelDownload();
        }
        return false;
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }


    private class FileDownloadCallbacks implements IFileDownloadCallbacks {

        private String getMessage(int resId) {
            return iImagePreviewActivity.getAppResource().getString(resId);
        }

        @Override
        public void onNoInternet() {
            iImagePreviewActivity.showMsg(getMessage(R.string.v1010_toast_net_exception));
            iImagePreviewActivity.cancelWaitView();
        }

        @Override
        public void onMobileNet() {
            iImagePreviewActivity.showMobileDownloadAlert(new CustomPopupWindow.OnPositiveClickListener() {
                @Override
                public void onPositiveClick() {
                    model.forceStart(iImagePreviewActivity.getActivity());
                }
            });
        }

        @Override
        public void onFileNotFoundOnServer() {
            iImagePreviewActivity.showMsg(getMessage(R.string.v1020_toast_download_onnotfound));
            iImagePreviewActivity.cancelWaitView();
        }

        @Override
        public void onDownloadStart(DownloadEntity entity) {
            //ignore
            iImagePreviewActivity.showWaitView(true);
        }

        @Override
        public void onDownloadCancel() {
            //ignore
            iImagePreviewActivity.cancelWaitView();
            iImagePreviewActivity.showMsg(getMessage(R.string.v1020_toast_download_cancel));
        }

        @Override
        public void onDownloadSuccess(DownloadEntity entity, final File file) {
            iImagePreviewActivity.showMsg(getMessage(R.string.v1020_toast_download_success));
            iImagePreviewActivity.cancelWaitView();
        }

        @Override
        public void onDownloading(DownloadEntity entity, long current, long total) {
            //ignore
        }

        @Override
        public void onNoEnoughSpace() {
            iImagePreviewActivity.showMsg(getMessage(R.string.v1020_toast_download_onnoenoughspace));
        }

        @Override
        public void downloadFailure() {
            iImagePreviewActivity.showMsg(getMessage(R.string.v1020_toast_download_failure));
            iImagePreviewActivity.cancelWaitView();
        }
    }
}
