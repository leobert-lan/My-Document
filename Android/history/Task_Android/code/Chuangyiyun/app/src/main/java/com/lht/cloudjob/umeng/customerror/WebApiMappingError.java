package com.lht.cloudjob.umeng.customerror;

import org.json.JSONException;

/**
 * <p><b>Package:</b> com.lht.cloudjob.umeng.customerror </p>
 * <p><b>Project:</b> Chuangyiyun </p>
 * <p><b>Classname:</b> WebApiMappingError </p>
 * <p><b>Description:</b> used to push error info about the exception when parsing response json of web api </p>
 * Created by leobert on 2017/10/16.
 */

public class WebApiMappingError extends BaseUmengError {
    private static final String ERROR_TYPE = JSONException.class.getSimpleName();

    public WebApiMappingError(Exception e,String res) {
        setErrorType(ERROR_TYPE);
        setErrorInfo(e.toString());
        setErrorData(res);
    }

    public WebApiMappingError(Exception e,byte[] res) {
        setErrorType(ERROR_TYPE);
        setErrorInfo(e.toString());
        setErrorData(new String(res));
    }
}
