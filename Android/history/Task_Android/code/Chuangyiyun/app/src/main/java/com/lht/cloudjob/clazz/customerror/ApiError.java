package com.lht.cloudjob.clazz.customerror;

/**
 * <p><b>Package</b> com.lht.cloudjob.clazz.customerror
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> ApiError
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2017/2/28.
 */

public class ApiError extends BaseUmengError {

    public ApiError(String apiResp) {
        this.errorType = IllegalArgumentException.class.getSimpleName();
        this.errorInfo = "api返回数据无法解析";
        this.errorData = apiResp;
    }


    public ApiError(Class apiModel, String apiResp) {
        this.errorType = IllegalArgumentException.class.getSimpleName();
        this.errorInfo = "api返回数据无法解析:" + apiModel.getSimpleName();
        this.errorData = apiResp;
    }
}
