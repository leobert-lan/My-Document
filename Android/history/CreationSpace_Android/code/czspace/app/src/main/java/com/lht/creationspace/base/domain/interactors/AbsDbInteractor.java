package com.lht.creationspace.base.domain.interactors;

import com.lht.creationspace.base.domain.excutors.impl.BackendExecutor;
import com.lht.creationspace.base.domain.thread.impl.MainThreadImpl;
import com.lht.creationspace.util.debug.DLog;

import java.lang.ref.WeakReference;

/**
 * <p><b>Package:</b> com.lht.creationspace.base.domain.interactors </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> AbsDbInteractor </p>
 * <p><b>Description:</b> DB 任务 </p>
 * Created by leobert on 2017/8/4.
 */

public abstract class AbsDbInteractor<R> extends AbsInteractor {

    public interface OnTaskFinishListener<R> {
        void onSuccess(R result);

        void onCanceledBeforeRun();
    }

    private WeakReference<OnTaskFinishListener<R>> listenerRef;

    public AbsDbInteractor() {
        this(null);
    }

    public AbsDbInteractor(OnTaskFinishListener<R> listener) {
        super(BackendExecutor.getInstance(), MainThreadImpl.getInstance());
        if (listener != null) {
            listenerRef = new WeakReference<>(listener);
        }
    }

    @Override
    public void run() {
        if (mIsCanceled) {
            callbackOnCanceledBeforeRun();
            return;
        }

        R result = runTask();
        callbackOnSuccess(result);

    }

    private void callbackOnCanceledBeforeRun() {
        if (listenerRef == null) {
            DLog.d(getClass(), "Job OnCanceledBeforeRun,no callback");
            return;
        }

        final OnTaskFinishListener<R> listener = listenerRef.get();

        if (listener == null) {
            DLog.e(getClass(), "Job OnCanceledBeforeRun,callback null ,err!");
            return;
        }

        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                listener.onCanceledBeforeRun();
            }
        });
    }

    private void callbackOnSuccess(final R result) {
        if (listenerRef == null) {
            DLog.d(getClass(), "Job onSuccess,no callback");
            return;
        }

        final OnTaskFinishListener<R> listener = listenerRef.get();

        if (listener == null) {
            DLog.d(getClass(), "Job onSuccess,callback has bean gc. won't rollback");
            return;
        }

        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                listener.onSuccess(result);

            }
        });
    }

    @Override
    public final void cancel() {
        if (isRunning()) {
            // release dependency
            if (listenerRef != null)
                listenerRef.clear();
        }
        super.cancel();
    }

    protected abstract R runTask();
}
