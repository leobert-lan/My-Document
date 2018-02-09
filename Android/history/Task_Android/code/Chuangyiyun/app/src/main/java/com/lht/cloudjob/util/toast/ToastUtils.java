package com.lht.cloudjob.util.toast;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * @package com.lht.cloudjob.util.toast
 * @project AndroidBase
 * @classname ToastUtils
 * @description: TODO
 * Created by leobert on 2016/4/5.
 */
public class ToastUtils {
    private static String preMsg = "";
    private static final int shortTime = 2000;
    private static final int longTime = 3500;
    private static boolean onShowing = false;
    private static Object syncLock = new Object();
    private static ArrayList<ToastJob> ToastList = new ArrayList<ToastJob>();

    public static void show(Context c, int sRid, Duration d) {
        int duration = 0;
        long time = 0;
        switch (d) {
            case l:
                duration = Toast.LENGTH_LONG;
                time = longTime;
                break;
            case s:
                duration = Toast.LENGTH_SHORT;
                time = shortTime;
                break;
            default:
                break;
        }
        String msg = c.getResources().getString(sRid);
        if (msg.equals(preMsg) && onShowing)
            return;
        preMsg = msg;
        ToastJob job = new ToastJob();
        job.setToast(Toast.makeText(c, msg, duration));
        job.setTime(time);
        synchronized (syncLock) {
            ToastList.add(job);
            callShow();
        }
    }

    public static void show(Context c, String msg, Duration d) {
        int duration = 0;
        long time = 0;
        switch (d) {
            case l:
                duration = Toast.LENGTH_LONG;
                time = longTime;
                break;
            case s:
                duration = Toast.LENGTH_SHORT;
                time = shortTime;
                break;
            default:
                break;
        }

        if (msg == null)
            return;

        if (msg.equals(preMsg) && onShowing)
            return;
        preMsg = msg;
        ToastJob job = new ToastJob();
        job.setToast(Toast.makeText(c, msg, duration));
        job.setTime(time);
        synchronized (syncLock) {
            ToastList.add(job);
            callShow();
        }
    }

    private static synchronized void callShow() {
        synchronized (syncLock) {
            if (!onShowing && ToastList.size() > 0) {
                ToastList.get(0).getToast().show();
                onShowing = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onShowing = false;
                        callShow();
                    }

                }, ToastList.get(0).getTime());
                ToastList.remove(0);
            }
        }
    }


    public enum Duration {
        l, s;
    }


    static class ToastJob {
        private Toast Toast;
        private long time;

        public Toast getToast() {
            return Toast;
        }

        public void setToast(Toast Toast) {
            this.Toast = Toast;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }
    }
}