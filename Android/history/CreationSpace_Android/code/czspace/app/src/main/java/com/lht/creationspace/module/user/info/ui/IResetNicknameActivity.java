package com.lht.creationspace.module.user.info.ui;

import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.viewinterface
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> IResetNicknameActivity
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/27.
 */
public interface IResetNicknameActivity extends IActivityAsyncProtected {
    void clearInput();

    void showErrorMsg(String msg);
}
