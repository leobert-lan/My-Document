package com.lht.creationspace.base.domain.thread.impl;

import android.os.Handler;
import android.os.Looper;

import com.lht.creationspace.base.domain.thread.MainThread;

/**
 * <p><b>Package:</b> com.lht.creationspace.base.domain.thread.impl </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> MainThreadImpl </p>
 * <p><b>Description:</b> the Implementation of MainThread,works for post jobs running at
 * main thread</p>
 * Created by leobert on 2017/8/4.
 */

public class MainThreadImpl implements MainThread {

    private static MainThread sMainThread;

    private Handler mHandler;

    private MainThreadImpl() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void post(Runnable runnable) {
        mHandler.post(runnable);
    }

    public static MainThread getInstance() {
        if (sMainThread == null) {
            sMainThread = new MainThreadImpl();
        }

        return sMainThread;
    }
}
