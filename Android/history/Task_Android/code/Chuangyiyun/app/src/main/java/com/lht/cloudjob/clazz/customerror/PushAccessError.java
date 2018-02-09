package com.lht.cloudjob.clazz.customerror;

/**
 * <p><b>Package</b> com.lht.cloudjob.clazz.customerror
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> PushAccessError
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/12/14.
 */

public class PushAccessError extends BaseUmengError {
    private static final String ERROR_TYPE = IllegalAccessError.class.getSimpleName();

    private static final String ERROR_INFO = "Android Jpush should use message,but this is a notification";

    public PushAccessError(String errorData) {
        setErrorType(ERROR_TYPE);
        setErrorInfo(ERROR_INFO);
        this.errorData = errorData;
    }

}
