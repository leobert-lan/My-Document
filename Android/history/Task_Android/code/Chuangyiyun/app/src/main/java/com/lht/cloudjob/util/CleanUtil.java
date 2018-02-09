package com.lht.cloudjob.util;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.lht.cloudjob.activity.BaseActivity;
import com.lht.cloudjob.activity.asyncprotected.SettingActivity;
import com.lht.cloudjob.util.debug.DLog;

import java.io.File;
import java.util.ArrayList;

/**
 * @author leobert.lan
 * @version 1.0
 * @ClassName: CleaUtil
 * @Description: TODO
 * @date 2016年4月26日 下午12:31:03
 */
public class CleanUtil {

    private final ICleanView mCleanView;
    private final MyHandler mCustomHandler;

    public CleanUtil(ICleanView iCleanView) {
        this.mCleanView = iCleanView;
        mCustomHandler = new MyHandler();
    }

    public interface ICleanView {
        void doShowWaitView(boolean isProtected);

        void doCancelWaitView();

        void doShowSize(long cacheSize);

        /**
         * @return 获取应用缓存，系统分配的
         */
        File getCacheFileDir();

        void doCreateIndividualFolder();
    }

    public void calcSize() {
        // Log.e("lmsg", "calc size");
        new CalcThread(mCustomHandler).start();
    }

    @SuppressLint("HandlerLeak")
    private final class MyHandler extends Handler {
        static final int WHAT_START = 1;
        static final int WHAT_ERROR = 2;
        static final int WHAT_DELETECOMPLETE = 3;
        static final int WHAT_CALCCOMPLETE = 4;

        static final String KEY_DOWNLOADSIZE = "down";
        static final String KEY_CACHESIZE = "cache";

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_START:
                    mCleanView.doShowWaitView(true);
                    break;
                case WHAT_ERROR:
                    // TODO 暂时没有实际的error情况
                    break;
                case WHAT_DELETECOMPLETE:
                    mCleanView.doCancelWaitView();
                    calcSize();
                    break;
                case WHAT_CALCCOMPLETE:
                    Bundle b = msg.getData();
                    long downloadSize = b.getLong(KEY_DOWNLOADSIZE, 0L);
                    long cacheSize = b.getLong(KEY_CACHESIZE, 0L);
                    mCleanView.doShowSize(cacheSize);
                    mCleanView.doCancelWaitView();
                    break;
                default:
                    break;
            }
        }
    }

    final class CleanAction {
        ArrayList<File> filesToClean = new ArrayList<>();
        String dbName = "";
        String tableName = "";
        String key = "";
    }

    class CalcThread extends Thread {
        private final Handler mHandler;

        public CalcThread(Handler hanlder) {
            mHandler = hanlder;
        }

        @Override
        public void run() {
            super.run();
            mHandler.sendEmptyMessage(MyHandler.WHAT_START);
            File downFile = BaseActivity.getDownloadDir();
//			File thumbFile = new File(BaseActivity.getThumbnailPath());
            File previewFile = new File(BaseActivity.getPreviewDir());
            File localThumbFile = new File(BaseActivity.getLocalImageCachePath());
            File innerCache = mCleanView.getCacheFileDir();
            Bundle b = new Bundle();
            try {
                long downSize = getFileSize(downFile);
                long cacheSize =
                        //getFileSize(thumbFile) deprecate
                        +getFileSize(previewFile)
                                + getFileSize(localThumbFile)
                                + getFileSize(innerCache);
                DLog.e(SettingActivity.class, new DLog.LogLocation(), "cache size:" + cacheSize);
                b.putLong(MyHandler.KEY_DOWNLOADSIZE, downSize);
                b.putLong(MyHandler.KEY_CACHESIZE, cacheSize);
            } catch (Exception e) {
                mHandler.sendEmptyMessage(MyHandler.WHAT_ERROR);
                e.printStackTrace();
            }

            // Log.e("lmsg", "calc size complete");
            Message msg = new Message();
            msg.what = MyHandler.WHAT_CALCCOMPLETE;
            msg.setData(b);
            mHandler.sendMessage(msg);
        }

        private long getFileSize(File f) throws Exception {
            long size = 0;
            if (!f.exists())
                return size;
            File flist[] = f.listFiles();
            for (int i = 0; i < flist.length; i++) {
                if (flist[i].isDirectory()) {
                    size = size + getFileSize(flist[i]);
                } else {
                    size = size + flist[i].length();
                }
            }
            return size;
        }
    }

    class CleanThread extends Thread {
        private final Handler mHandler;
        private final CleanAction mCleanAction;

        CleanThread(Handler handler, CleanAction action) {
            mHandler = handler;
            mCleanAction = action;
        }

        @Override
        public void run() {
            super.run();
            postStart();
            if (!mCleanAction.filesToClean.isEmpty()) {
                deleteFiles(mCleanAction.filesToClean);
            }
            // 补充被删除的基本文件夹
            mCleanView.doCreateIndividualFolder();
            postComplete();
        }


        private void deleteFiles(ArrayList<File> filesToClean) {
            for (File f : filesToClean) {
                delete(f);
            }
        }

        void postStart() {
            mHandler.sendEmptyMessage(MyHandler.WHAT_START);
        }

        void postError() {
            mHandler.sendEmptyMessage(MyHandler.WHAT_ERROR);
        }

        void postComplete() {
            // Log.e("lmsg", "delete complete");
            mHandler.sendEmptyMessage(MyHandler.WHAT_DELETECOMPLETE);
        }

        private void delete(File file) {
            if (file.exists()) {
                if (file.isFile()) {
                    file.delete();
                } else if (file.isDirectory()) {
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        this.delete(files[i]);
                    }
                }
                file.delete();
            } else {
                // TODO 没有任何文件的时候会执行，但不是error
                postError();
            }
        }
    }

    public void cleanCache() {
        CleanAction action = new CleanAction();
        ArrayList<File> files = new ArrayList<>();
        File previews = new File(BaseActivity.getPreviewDir());
//		File thumbnails = new File(BaseActivity.getThumbnailPath());
        File localThumbs = new File(BaseActivity.getLocalImageCachePath());
        files.add(previews);
//		files.add(thumbnails);
        files.add(localThumbs);
        files.add(mCleanView.getCacheFileDir());
        action.filesToClean = files;
        new CleanThread(mCustomHandler, action).start();
    }

}