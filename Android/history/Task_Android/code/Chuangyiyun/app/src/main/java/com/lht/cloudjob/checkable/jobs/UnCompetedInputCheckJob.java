package com.lht.cloudjob.checkable.jobs;

import com.lht.cloudjob.checkable.AbsCheckJob;
import com.lht.cloudjob.mvp.viewinterface.IActivityAsyncProtected;
import com.lht.cloudjob.util.string.StringUtil;

import java.lang.ref.WeakReference;

/**
 * <p><b>Package</b> com.lht.cloudjob.checkable.jobs
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> UnCompetedInputCheckJob
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2017/2/23.
 */

public class UnCompetedInputCheckJob extends AbsCheckJob {
    private final String input;

    private final int toastResId;

    private final WeakReference<IActivityAsyncProtected> uiRef;

    public UnCompetedInputCheckJob(String input, int toastResId, IActivityAsyncProtected ui) {
        this.input = input;
        this.toastResId = toastResId;
        this.uiRef = new WeakReference<>(ui);
    }

    @Override
    public boolean check() {
        boolean unComplete = StringUtil.isEmpty(input);
        if (unComplete)
            return RESULT_CHECK_ILLEGAL;
        else
            return RESULT_CHECK_LEGAL;
    }

    @Override
    public void onCheckIllegal() {
        super.onCheckIllegal();
        IActivityAsyncProtected iActivityAsyncProtected = uiRef.get();
        if (iActivityAsyncProtected != null) {
            iActivityAsyncProtected.showMsg(iActivityAsyncProtected.getAppResource().getString(toastResId));
        }
    }
}
