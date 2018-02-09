package com.lht.cloudjob.mvp.viewinterface;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.viewinterface
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IChangePwdActivity
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/8/5.
 */
public interface IChangePwdActivity extends IActivityAsyncProtected {
    void showErrorMsg(String msg);

    void onResetSuccess();
}
