package com.lht.cloudjob.umeng.customerror;

import android.content.Context;

/**
 * <p><b>Package</b> com.lht.vsocyy.clazz
 * <p><b>Project</b> VsoCyy
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
