package com.lht.cloudjob.mvp.viewinterface;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.viewinterface
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IResetNicknameActivity
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/27.
 */
public interface IResetNicknameActivity extends IActivityAsyncProtected {
    void clearInput();

    void showErrorMsg(String msg);
}
