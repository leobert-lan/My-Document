package com.lht.creationspace.checkable.jobs;

import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;
import com.lht.creationspace.checkable.AbsCheckJob;

import java.lang.ref.WeakReference;

public class EqualCheckJob extends AbsCheckJob {

    private Object foo;
    private Object bar;

    private final int toastResId;

    private final WeakReference<IActivityAsyncProtected> uiRef;

    public EqualCheckJob(Object foo, Object bar, int toastResId, IActivityAsyncProtected iActivityAsyncProtected) {
        this.foo = foo;
        this.bar = bar;
        this.toastResId = toastResId;
        this.uiRef = new WeakReference<>(iActivityAsyncProtected);
    }

    @Override
    public void onCheckIllegal() {
        super.onCheckIllegal();
        IActivityAsyncProtected iActivityAsyncProtected = uiRef.get();
        if (iActivityAsyncProtected != null) {
            iActivityAsyncProtected.showMsg(iActivityAsyncProtected.getAppResource().getString(toastResId));
        }
    }

    @Override
    public boolean check() {
        if (foo == null)
            return bar == null; //legal

        return foo.equals(bar);
    }
}
