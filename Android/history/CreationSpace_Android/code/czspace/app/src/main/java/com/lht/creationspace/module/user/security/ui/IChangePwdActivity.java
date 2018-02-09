package com.lht.creationspace.module.user.security.ui;

import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.viewinterface
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> IChangePwdActivity
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/8/5.
 */
public interface IChangePwdActivity extends IActivityAsyncProtected {
    void showErrorMsg(String msg);

    void onResetSuccess();
}
