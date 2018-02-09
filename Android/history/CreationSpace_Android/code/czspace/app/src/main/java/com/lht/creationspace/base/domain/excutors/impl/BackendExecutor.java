package com.lht.creationspace.base.domain.excutors.impl;

import com.lht.creationspace.base.domain.excutors.Executor;
import com.lht.creationspace.base.domain.interactors.AbsInteractor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p><b>Package:</b> com.lht.creationspace.base.domain.excutors.impl </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> BackendExecutor </p>
 * <p><b>Description:</b> A Implementation of executor use ThreadPool to schedule jobs </p>
 * Created by leobert on 2017/8/4.
 */

public class BackendExecutor implements Executor {
    // This is a singleton
    private static volatile BackendExecutor sThreadExecutor;

    private static final int CORE_POOL_SIZE = 3;
    private static final int MAX_POOL_SIZE = 5;
    private static final int KEEP_ALIVE_TIME = 120;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private static final BlockingQueue<Runnable> WORK_QUEUE =
            new LinkedBlockingQueue<>();

    private ThreadPoolExecutor mThreadPoolExecutor;

    private BackendExecutor() {
        long keepAlive = KEEP_ALIVE_TIME;
        mThreadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                keepAlive,
                TIME_UNIT,
                WORK_QUEUE);
    }

    @Override
    public void execute(final AbsInteractor interactor) {
        mThreadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                // run the main logic
                interactor.run();

                // mark it as finished
                interactor.onFinished();
            }
        });
    }

    /**
     * Returns a singleton instance of this executor. If the executor is not initialized then it initializes it and returns
     * the instance.
     */
    public static Executor getInstance() {
        if (sThreadExecutor == null) {
            sThreadExecutor = new BackendExecutor();
        }

        return sThreadExecutor;
    }
}
