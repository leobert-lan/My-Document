package com.lht.creationspace.base.domain.thread;

/**
 * <p><b>Package:</b> com.lht.creationspace.base.domain.excutors </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> MainThread </p>
 * <p><b>Description:</b> interface to post jobs run at main thread </p>
 * Created by leobert on 2017/8/4.
 */

public interface MainThread {
    /**
     * post a runnable job to run at main thread
     * @param runnable the jobs to do
     */
    void post(final Runnable runnable);
}
