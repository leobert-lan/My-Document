package com.lht.cloudjob.clazz.customerror;

/**
 * <p><b>Package</b> com.lht.cloudjob.clazz.customerror
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IllegalArgsError
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2017/1/11.
 */

public class IllegalArgsError extends BaseUmengError {
    public IllegalArgsError(String errorInfo) {
        this.errorType = IllegalArgumentException.class.getSimpleName();
        this.errorInfo = errorInfo;
    }
}
