package com.lht.cloudjob.umeng.customerror;

/**
 * <p><b>Package</b> com.lht.vsocyy.clazz.customerror
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> PushContentError
 * <p><b>Description</b>: 错误内容的推送
 * <p>Created by leobert on 2016/12/14.
 */

public class PushContentError extends BaseUmengError {
    private static final String ERROR_TYPE = IllegalArgumentException.class.getSimpleName();

    private static final String ERROR_INFO = "Android Jpush error content";

    public PushContentError(String errorData) {
        setErrorType(ERROR_TYPE);
        setErrorInfo(ERROR_INFO);
        this.errorData = errorData;
    }

}
