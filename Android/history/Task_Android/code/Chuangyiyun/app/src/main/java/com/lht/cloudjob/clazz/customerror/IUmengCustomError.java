package com.lht.cloudjob.clazz.customerror;

import android.content.Context;

/**
 * <p><b>Package</b> com.lht.cloudjob.clazz
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IUmengCustomError
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/12/14.
 */

public interface IUmengCustomError {

    void setErrorType(String type);

    void setErrorInfo(String info);

    void setErrorData(String data);

    /**
     * if you are sure the application is alive,you can use this;
     */
    void report();

    /**
     * better use this to report
     * @param context
     */
    void report(Context context);
}
