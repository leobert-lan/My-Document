package com.lht.cloudjob.mvp.model;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.lht.cloudjob.util.debug.DLog;

import java.lang.ref.WeakReference;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> DbCURDModel
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/8/1.
 */
public class DbCURDModel<Model> {

    private WeakReference<IDbCURD<Model>> reference;
    
    private MHandler<Model> modelMHandler;

    public DbCURDModel(IDbCURD<Model> iDbCURD) {
        reference = new WeakReference<>(iDbCURD);
        modelMHandler = new MHandler<>(iDbCURD);
    }

    public void doRequest() {
        new CURDThread(modelMHandler).start();
    }

    class CURDThread extends Thread {

        MHandler<Model> mHandler;

        CURDThread(MHandler<Model> mHandler) {
            this.mHandler = mHandler;
        }

        @Override
        public void run() {
            super.run();
            IDbCURD iDbCURD = reference.get();
            Model data = null;
            if (iDbCURD != null) {
                data = (Model) iDbCURD.doCURDRequest();
            } else {
                DLog.e(getClass(), "iDbCurd is null on thread");
            }

            Message msg =  new Message();
            msg.what = MHandler.WHAT_COMPLETE;
            msg.obj = data;
            mHandler.sendMessage(msg);
        }
    }

    private static class MHandler<M> extends Handler {

        public static final int WHAT_COMPLETE = 0;

        private  WeakReference<IDbCURD<M>> curdWeakReference;

        MHandler(IDbCURD<M> dbCURD) {
            super(Looper.getMainLooper());
            this.curdWeakReference = new WeakReference<>(dbCURD);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_COMPLETE:
                    IDbCURD<M> iDbCURD = curdWeakReference.get();
                    if (iDbCURD != null) {
                        M model = (M) msg.obj;
                        iDbCURD.onCURDFinish(model);
                    } else {
                        DLog.e(getClass(),"iDbCurd is null on handler");
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public interface IDbCURD<Model> {
        Model doCURDRequest();

        void onCURDFinish(Model model);
    }
}
