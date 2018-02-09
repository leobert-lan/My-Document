package com.lht.creationspace.service.notice;

import android.support.annotation.NonNull;

import com.lht.creationspace.util.debug.DLog;

import java.util.Observable;

/**
 * <p><b>Package:</b> com.lht.creationspace.service </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> UnreadCheckManager </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/8/8.
 */

/*public*/ class NotifyCheckManager<T>{

    private UnreadNotifyChecker notifyChecker;

    private MObservable mObservable;

    public NotifyCheckManager(@NonNull UnreadNotifyChecker notifyChecker) {
        this.notifyChecker = notifyChecker;
        mObservable = new MObservable();
    }

    public void addOnNoticeCheckedListener(OnNoticeCheckedListener<T> listener) {
        mObservable.addObserver(listener);
    }

    public void removeNoticeCheckedListener(OnNoticeCheckedListener listener) {
        mObservable.deleteObserver(listener);
    }

    private boolean onChecking = false;

    public final void check() {
        if (isOnChecking()) {
            DLog.d(getClass(),"onCheck,drop this request");
            return;
        }
        runCheck();
    }

    private void runCheck() {
        onChecking = true;
        notifyChecker.check();
    }

    protected void onCheckFinish() {
        onChecking = false;
    }

    public boolean isOnChecking() {
        return onChecking;
    }

    protected void notifyListener(T t) {
        DLog.d(DLog.Lmsg.class,"observer count:"+mObservable.countObservers());
        mObservable.setChanged();
        mObservable.notifyObservers(t);
    }

    private static class MObservable extends Observable {
        @Override
        public synchronized void setChanged() {
            super.setChanged();
        }
    }
}
