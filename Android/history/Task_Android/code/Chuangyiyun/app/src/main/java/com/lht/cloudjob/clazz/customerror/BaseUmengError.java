package com.lht.cloudjob.clazz.customerror;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lht.cloudjob.MainApplication;
import com.umeng.analytics.MobclickAgent;

/**
 * <p><b>Package</b> com.lht.cloudjob.clazz
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> BaseUmengError
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/12/14.
 */

public abstract class BaseUmengError implements IUmengCustomError {

    protected String errorType;

    protected String errorInfo;

    protected String errorData;

    @Override
    public void setErrorType(String type) {
        this.errorType = type;
    }

    @Override
    public void setErrorInfo(String info) {
        this.errorInfo = info;
    }

    @Override
    public void setErrorData(String data) {
        this.errorData = data;
    }

    protected String getErrorType() {
        return errorType;
    }

    protected String getErrorInfo() {
        return errorInfo;
    }

    protected String getErrorData() {
        return errorData;
    }

    protected final String formatErrorString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("errorType",getErrorType());
        jsonObject.put("errorInfo",getErrorInfo());
        jsonObject.put("errorData",getErrorData());
        return JSON.toJSONString(jsonObject);
    }

    @Override
    public final void report() {
        report(MainApplication.getOurInstance());
    }

    @Override
    public final void report(Context context) {
        MobclickAgent.reportError(context,formatErrorString());
    }
}
