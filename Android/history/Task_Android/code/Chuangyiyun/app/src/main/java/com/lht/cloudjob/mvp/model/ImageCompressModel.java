package com.lht.cloudjob.mvp.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.lht.cloudjob.util.bitmap.BitmapUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> ImageCompressModel
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/7.
 */
public class ImageCompressModel {

//    private WeakReference<IImageCompressCallback> callbackHolder;

    private final IImageCompressCallback iImageCompressCallback;

    private static final int MAXWIDTH = 360;

    public ImageCompressModel(IImageCompressCallback callback) {
        iImageCompressCallback = callback;
//        callbackHolder = new WeakReference<>(callback);
    }

    public interface IImageCompressCallback {
        void onCompressed(String imageFilePath);
    }

//    private static final long MAXBYTE = 2 * 1000 * 1000;

    public void doCompress(File origin) {
        NHandler handler = new NHandler(Looper.getMainLooper());
        new CompressThread(handler, origin).start();
    }


    public class NHandler extends Handler {

        NHandler() {
            super();
        }

        NHandler(Looper l) {
            super(l);
        }

        public static final int WHAT_FAILURE = 1;
        public static final int WHAT_SUCCESS = 2;

        public static final String data_key = "path";
//        public static final String data_trigger = "trigger";

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            IImageCompressCallback callback = callbackHolder.get();
            IImageCompressCallback callback = iImageCompressCallback;
            Bundle b = msg.getData();
            switch (msg.what) {
                case WHAT_FAILURE:
                    if (callback != null) {
                        if (b != null) {
                            callback.onCompressed(b.getString(data_key));
                        }
                    }
                    break;
                case WHAT_SUCCESS:
                    if (callback != null) {
                        if (b != null) {
                            callback.onCompressed(b.getString(data_key));
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private final class CompressThread extends Thread {
        private final Handler handler;
        private final File originFile;

        CompressThread(Handler handler, File origin) {
            this.handler = handler;
            this.originFile = origin;
        }

        @Override
        public void run() {
            super.run();
            try {
                String path = originFile.getAbsolutePath();
//                Bitmap bitmap = BitmapFactory.decodeFile(path);
//                bitmap = BitmapUtils.compressBitmap(bitmap, MAXBYTE);
                BitmapFactory.Options options = BitmapUtils.getBitmapOptions(path);
                int h = options.outHeight;
                int w = options.outWidth;
                if (w > MAXWIDTH) {
                    h = h * MAXWIDTH / w;
                    w = MAXWIDTH;
                }
                Bitmap bitmap = BitmapUtils.compressBitmap(path, w, h);

                originFile.delete();

                File f = new File(path);
                f.createNewFile();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                byte[] bitmapdata = bos.toByteArray();

                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapdata);

                fos.flush();
                fos.close();

                Message msg = new Message();
                msg.what = NHandler.WHAT_SUCCESS;
                Bundle b = new Bundle();
                b.putString(NHandler.data_key, path);
                msg.setData(b);
                handler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
                Message msg = new Message();
                msg.what = NHandler.WHAT_FAILURE;
                Bundle b = new Bundle();
                b.putString(NHandler.data_key, originFile.getAbsolutePath());
                msg.setData(b);
                handler.sendMessage(msg);
            }
        }
    }
}
