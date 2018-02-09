package com.lht.cloudjob.mvp.model;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.interfaces.ITriggerCompare;
import com.lht.cloudjob.util.file.FileUtils;
import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> ImageCopyModel
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/5/12.
 */
public class ImageCopyModel implements IImageCopy {

    @Override
    public void doCopy(File origin, File dest, ITriggerCompare triggerCompare) {
        NHandler handler = new NHandler(Looper.getMainLooper());
        new CopyThread(handler, origin, dest, triggerCompare).start();
    }


    public static class NHandler extends Handler {

        NHandler() {
            super();
        }

        NHandler(Looper l) {
            super(l);
        }

        public static final int WHAT_FAILURE = 1;
        public static final int WHAT_SUCCESS = 2;

        public static final String data_key = "path";
        public static final String data_trigger = "trigger";

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle b = msg.getData();
            switch (msg.what) {
                case WHAT_FAILURE:
                    if (b != null) {
                        ITriggerCompare trigger = (ITriggerCompare) b.getSerializable(data_trigger);
                        AppEvent.ImageGetEvent event = new AppEvent.ImageGetEvent(b.getString(data_key), false);
                        event.setTrigger(trigger);
                        EventBus.getDefault().post(event);
                    }
                    break;
                case WHAT_SUCCESS:
                    if (b != null) {
                        ITriggerCompare trigger = (ITriggerCompare) b.getSerializable(data_trigger);
                        AppEvent.ImageGetEvent event = new AppEvent.ImageGetEvent(b.getString(data_key), true);
                        event.setTrigger(trigger);
                        EventBus.getDefault().post(event);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private final class CopyThread extends Thread {
        private final Handler handler;
        private final File originFile;
        private final File destFile;
        private final ITriggerCompare iTriggerCompare;

        CopyThread(Handler hanlder, File origin, File dest, ITriggerCompare triggerCompare) {
            this.handler = hanlder;
            this.originFile = origin;
            this.destFile = dest;
            this.iTriggerCompare = triggerCompare;
        }

        @Override
        public void run() {
            super.run();
            try {
                FileUtils.copyFile(originFile, destFile);
                Message msg = new Message();
                msg.what = NHandler.WHAT_SUCCESS;
                Bundle b = new Bundle();
                b.putString(NHandler.data_key, destFile.getAbsolutePath());
                b.putSerializable(NHandler.data_trigger, iTriggerCompare.getSerializable());
                msg.setData(b);
                handler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
                Message msg = new Message();
                msg.what = NHandler.WHAT_FAILURE;
                Bundle b = new Bundle();
                b.putString(NHandler.data_key, destFile.getAbsolutePath());
                b.putSerializable(NHandler.data_trigger, iTriggerCompare.getSerializable());
                msg.setData(b);
                handler.sendMessage(msg);
            }
        }
    }
}

interface IImageCopy {
    void doCopy(File origin, File dest, ITriggerCompare triggerCompare);
}
