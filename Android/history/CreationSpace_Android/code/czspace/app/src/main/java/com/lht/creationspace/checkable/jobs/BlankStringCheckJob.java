package com.lht.creationspace.checkable.jobs;

import com.lht.creationspace.checkable.AbsCheckJob;
import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;
import com.lht.creationspace.util.string.StringUtil;

import java.lang.ref.WeakReference;

/**
 * <p><b>Package:</b> com.lht.creationspace.checkable.jobs </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> BlankStringCheckJob </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/4/6.
 */

public class BlankStringCheckJob extends AbsCheckJob {
    private final String input;

    private final int toastResId;

    private final WeakReference<IActivityAsyncProtected> uiRef;

    public BlankStringCheckJob(String input, int toastResId,
                               IActivityAsyncProtected ui) {
        this.input = input;
        this.toastResId = toastResId;
        this.uiRef = new WeakReference<>(ui);
    }

    @Override
    public boolean check() {
        if (StringUtil.isEmpty(input))
            return RESULT_CHECK_ILLEGAL;
        String _s = StringUtil.replaceEnter(input);
        if (StringUtil.isBlank(_s))
            return RESULT_CHECK_ILLEGAL;
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
