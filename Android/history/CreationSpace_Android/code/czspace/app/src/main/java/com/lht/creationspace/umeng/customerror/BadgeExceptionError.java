package com.lht.creationspace.umeng.customerror;

/**
 * <p><b>Package</b> com.lht.vsocyy.clazz.customerror
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> BadgeExcetionError
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/12/14.
 */

public class BadgeExceptionError extends BaseUmengError {

    private static final String ERROR_TYPE = Exception.class.getSimpleName();

    public BadgeExceptionError(Exception e) {
        setErrorType(ERROR_TYPE);
        setErrorInfo("can not control desktop badge");
        setErrorData(e.toString());
    }
}
