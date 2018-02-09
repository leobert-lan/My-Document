package com.lht.creationspace.checkable.jobs;

import com.lht.creationspace.checkable.AbsCheckJob;
import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;
import com.lht.creationspace.util.CheckPwdUtil;

/**
 * <p><b>Package</b> com.lht.creationspace.checkable.jobs
 * <p><b>Project</b> czspace
 * <p><b>Classname</b> PasswordCheckJob
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2017/2/24.
 */

public class PasswordCheckJob extends AbsCheckJob {
    private String pwd;
    private IActivityAsyncProtected iActivityAsyncProtected;

    public PasswordCheckJob(String pwd, IActivityAsyncProtected iActivityAsyncProtected) {
        this.pwd = pwd;
        this.iActivityAsyncProtected = iActivityAsyncProtected;
    }

    @Override
    public boolean check() {
        boolean ret = CheckPwdUtil.isPasswordLegal(pwd, iActivityAsyncProtected);
        if (ret)
            return RESULT_CHECK_LEGAL;
        else
            return RESULT_CHECK_ILLEGAL;
    }
}
