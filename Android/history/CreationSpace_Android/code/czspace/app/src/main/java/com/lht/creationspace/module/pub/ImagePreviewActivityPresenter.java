package com.lht.creationspace.module.pub;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.lht.creationspace.customview.share.ThirdPartySharePopWins;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.base.model.pojo.PreviewImage;
import com.lht.creationspace.module.pub.ui.IImagePreviewActivity;
import com.lht.creationspace.util.file.FileUtils;
import com.lht.creationspace.util.internet.HttpUtil;
import com.lht.creationspace.util.string.StringUtil;

import java.io.File;
import java.lang.ref.WeakReference;

import individual.leobert.uilib.actionsheet.OnActionSheetItemClickListener;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.presenter
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> ImagePreviewActivityPresenter
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/11/7.
 */

public class ImagePreviewActivityPresenter implements IApiRequestPresenter {
    private IImagePreviewActivity iImagePreviewActivity;

    public ImagePreviewActivityPresenter(IImagePreviewActivity iImagePreviewActivity) {
        this.iImagePreviewActivity = iImagePreviewActivity;
    }


    public void onMenuOpenCalled(final PreviewImage image) {

        final String[] ops = new String[]{"保存图片", "分享"};

        iImagePreviewActivity.showMenuActionSheet(ops, new OnActionSheetItemClickListener() {
            @Override
            public void onActionSheetItemClick(int position) {
                switch (position) {
                    case 0:
                        saveImage(image);
                        break;
                    case 1:
                        onShareCalled(image);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void saveImage(PreviewImage previewImage) {
        new SaveImageTask(iImagePreviewActivity).execute(previewImage);
    }

    private void onShareCalled(PreviewImage image) {
        new HuntImageForShareTask(iImagePreviewActivity).execute(image);
    }


    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    private class HuntImageForShareTask extends GlideCacheHuntTask {

        public HuntImageForShareTask(IImagePreviewActivity imagePreviewActivity) {
            super(imagePreviewActivity);
        }

        @Override
        protected File doInBackground(PreviewImage... params) {
            File file = super.doInBackground(params);
            if (file == null)
                return null;
            else {
                return saveTemp(file.getAbsolutePath(), file.getName());
            }
        }

        private File saveTemp(String path, String name) {
            IImagePreviewActivity viewInterface = viewRef.get();
            if (viewInterface == null) {
                Log.d("lmsg", "ac has bean destroy before save to gallery");
                return null;
            }

            Context context = viewInterface.getActivity();
            String savedPath = null;

            // 保存到公用目录
            try {
                File dest = new File(Environment.getExternalStorageDirectory().getPath(), "Pictures");
                if (!dest.exists())
                    dest.mkdirs();

                String url = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                        path, name, null);
                savedPath = FileUtils.queryImageByUri(Uri.parse(url), context.getContentResolver());
                Log.d("lmsg", "temp save success");

            } catch (Exception e) {
                Log.e("lmsg", "temp save failure");
                e.printStackTrace();
            }
            if (!StringUtil.isEmpty(savedPath)) { //通知扫描
                return new File(savedPath);
            } else {
                Log.e("lmsg", "temp saved path null");
                return null;
            }
        }

        @Override
        protected void onPostExecute(File result) {
            if (result == null) {
                Log.d("lmsg", "share no temp");
                IImagePreviewActivity viewInterface = viewRef.get();
                if (viewInterface == null) {
                    Log.d("lmsg", "ac has bean destroy before save to gallery");
                    return;
                }
                viewInterface.showMsg("无法分享");
                return;
            }
            String path = result.getAbsolutePath();
            showSharePopupWins(path);
        }

        private void showSharePopupWins(String localPath) {
            IImagePreviewActivity viewInterface = viewRef.get();
            if (viewInterface == null) {
                Log.d("lmsg", "ac has bean destroy before save to gallery");
                return;
            }

            ThirdPartySharePopWins.ImageShareData shareData =
                    new ThirdPartySharePopWins.ImageShareData();
            shareData.setLocalImagePath(localPath);
            viewInterface.showSharePopwins(shareData);
        }
    }


    private class SaveImageTask extends GlideCacheHuntTask {

        public SaveImageTask(IImagePreviewActivity imagePreviewActivity) {
            super(imagePreviewActivity);
        }

        @Override
        protected void onPostExecute(File result) {
            if (result == null) {
                IImagePreviewActivity viewInterface = viewRef.get();
                if (viewInterface == null) {
                    Log.d("lmsg", "ac has bean destroy before save to gallery");
                    return;
                }
                viewInterface.notifySaveFailure();
                return;
            }
            //此path就是对应文件的缓存路径
            String path = result.getAbsolutePath();
            saveImageToGallery(path, result.getName());
        }

        private void saveImageToGallery(String path, String name) {
            IImagePreviewActivity viewInterface = viewRef.get();
            if (viewInterface == null) {
                Log.d("lmsg", "ac has bean destroy before save to gallery");
                return;
            }

            Context context = viewInterface.getActivity();
            String savedPath = null;

            // 其次把文件插入到系统图库
            try {
                File dest = new File(Environment.getExternalStorageDirectory().getPath(), "Pictures");
                if (!dest.exists())
                    dest.mkdirs();

                String url = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                        path, name, null);
                savedPath = FileUtils.queryImageByUri(Uri.parse(url), context.getContentResolver());
                viewInterface.notifySaveSuccess();
            } catch (Exception e) {
                viewInterface.notifySaveFailure();
                e.printStackTrace();
            }
            if (!StringUtil.isEmpty(savedPath)) { //通知扫描
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://" + savedPath)));
            }
        }
    }


    private class GlideCacheHuntTask extends AsyncTask<PreviewImage, Void, File> {

        protected final WeakReference<IImagePreviewActivity> viewRef;

        public GlideCacheHuntTask(IImagePreviewActivity imagePreviewActivity) {
            viewRef = new WeakReference<>(imagePreviewActivity);
        }

        protected Context getContext() {
            if (viewRef.get() == null)
                return null;
            else
                return viewRef.get().getActivity();
        }

        @Override
        protected File doInBackground(PreviewImage... params) {
            PreviewImage img = params[0];
            Context context = getContext();
            if (context == null)
                return null;
            try {
                return Glide.with(context)
                        .load(img.getPreviewUrl())
                        .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get();
            } catch (Exception ex) {
                return null;
            }
        }
    }
}
