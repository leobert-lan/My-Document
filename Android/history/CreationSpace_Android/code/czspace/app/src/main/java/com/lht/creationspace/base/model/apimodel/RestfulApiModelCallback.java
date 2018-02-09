package com.lht.creationspace.base.model.apimodel;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.model
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> ApiModelCallback
 * <p><b>Description</b>: 创意空间-restful风格新接口-请求解析回调
 * Created by leobert on 2016/7/15.
 */
public interface RestfulApiModelCallback<T> {
    void onSuccess(T bean);

    /**
     * 业务失败
     * @param restCode httpCode
     * @param msg may be null
     */
    void onFailure(int restCode,String msg);

    /**
     * 访问错误
     * @param httpStatus
     */
    void onHttpFailure(int httpStatus);
}
