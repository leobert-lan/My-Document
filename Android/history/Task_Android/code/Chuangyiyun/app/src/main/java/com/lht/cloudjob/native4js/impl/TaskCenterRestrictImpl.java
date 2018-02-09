package com.lht.cloudjob.native4js.impl;

import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.activity.asyncprotected.TaskCenterActivity;
import com.lht.cloudjob.native4js.Native4JsExpandAPI;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;
import com.lht.lhtwebviewlib.base.model.BridgeNativeFunction;
import com.lht.lhtwebviewlib.business.bean.BaseResponseBean;
import com.lht.lhtwebviewlib.business.impl.ABSApiImpl;

import java.lang.ref.WeakReference;


/**
 * 需求大厅重定向
 */
public class TaskCenterRestrictImpl extends ABSApiImpl implements
        Native4JsExpandAPI.TaskCenterRestrictHandler {

    private final WeakReference<Context> contextRef;

    public TaskCenterRestrictImpl(Context ctx) {
        contextRef = new WeakReference<>(ctx);
    }

    public static BridgeNativeFunction newInstance(Context context) {
        return new BridgeNativeFunction(API_NAME, new TaskCenterRestrictImpl(context));
    }

    @Override
    public void handler(String s, CallBackFunction callBackFunction) {

        //跳转到taskcenter，给出反馈
        Context ctx = contextRef.get();
        if (ctx == null) {
            DLog.i(Native4JsExpandAPI.class, "onHandler:" + API_NAME + "----context ref has bean gc");
            BaseResponseBean<String> resBean = newFailureResBean(0, "context ref has bean gc");
            callBackFunction.onCallBack(JSON.toJSONString(resBean));
            return;
        }

        Intent intent = new Intent(ctx, TaskCenterActivity.class);
        intent.putExtra(UMengActivity.KEY_ORIGINCLASS_SHOULDRESTRICTONFINISH,
                ctx.getClass().getName());
        ctx.startActivity(intent);

        BaseResponseBean<String> bean = newSuccessResBean();
        callBackFunction.onCallBack(JSON.toJSONString(bean));
    }

    private BaseResponseBean<String> newFailureResBean(int ret, String msgError) {
        BaseResponseBean<String> bean = new BaseResponseBean<>();
        bean.setStatus(BaseResponseBean.STATUS_FAILURE);
        bean.setRet(ret);
        bean.setMsg(msgError);
        return bean;
    }

    private BaseResponseBean<String> newSuccessResBean() {
        BaseResponseBean<String> bean = new BaseResponseBean<>();
        bean.setStatus(BaseResponseBean.STATUS_SUCCESS);
        bean.setMsg("success");
        return bean;
    }

    @Override
    protected boolean isBeanError(Object o) {
        return BEAN_IS_CORRECT;
    }
}
