package com.lht.cloudjob.native4js.impl;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.activity.asyncprotected.DemandInfoActivity;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.mvp.model.pojo.DemandInfoActivityData;
import com.lht.cloudjob.native4js.Native4JsExpandAPI;
import com.lht.cloudjob.native4js.expandresbean.NF_VsoDemandInfoResbean;
import com.lht.cloudjob.util.string.StringUtil;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;
import com.lht.lhtwebviewlib.base.model.BridgeNativeFunction;
import com.lht.lhtwebviewlib.business.bean.BaseResponseBean;
import com.lht.lhtwebviewlib.business.impl.ABSApiImpl;

/**
 * Created by chhyu on 2016/12/9.
 */

public class VsoDemandInfoRestrictImpl extends ABSApiImpl implements Native4JsExpandAPI.VsoDemandInfoRestrictHandler {

    private Context mContext;
    private CallBackFunction callBackFunction;
    private static String MSG_ERROR;

    public VsoDemandInfoRestrictImpl(Context ctx) {
        this.mContext = ctx;
    }

    public static BridgeNativeFunction newInstance(Context context) {
        return new BridgeNativeFunction(API_NAME, new VsoDemandInfoRestrictImpl(context));
    }

    @Override
    public void handler(String s, CallBackFunction callBackFunction) {
        this.callBackFunction = callBackFunction;
        NF_VsoDemandInfoResbean nf_vsoDemandInfoResbean = JSON.parseObject(s, NF_VsoDemandInfoResbean.class);
        boolean bool = isBeanError(nf_vsoDemandInfoResbean);
        if (!bool) {
            //跳转到DemandInfo ，给出反馈
            DemandInfoActivityData data = new DemandInfoActivityData();
            data.setDemandId(nf_vsoDemandInfoResbean.getTask_bn());
            data.setLoginInfo(IVerifyHolder.mLoginInfo);
            Intent intent = new Intent(mContext, DemandInfoActivity.class);
            intent.putExtra(DemandInfoActivity.KEY_DATA, JSON.toJSONString(data));
            intent.putExtra(UMengActivity.KEY_ORIGINCLASS_SHOULDRESTRICTONFINISH,
                    mContext.getClass().getName());
            mContext.startActivity(intent);

            BaseResponseBean<NF_VsoDemandInfoResbean> bean = newSuccessResBean(nf_vsoDemandInfoResbean);
            callBackFunction.onCallBack(JSON.toJSONString(bean));
        } else {

            BaseResponseBean<NF_VsoDemandInfoResbean> bean = newFailureResBean(0, MSG_ERROR);
            callBackFunction.onCallBack(JSON.toJSONString(bean));
        }
    }

    private BaseResponseBean<NF_VsoDemandInfoResbean> newFailureResBean(int ret, String msgError) {
        BaseResponseBean<NF_VsoDemandInfoResbean> bean = new BaseResponseBean<>();
        bean.setStatus(BaseResponseBean.STATUS_FAILURE);
        bean.setRet(ret);
        bean.setMsg(msgError);
        return bean;
    }

    private BaseResponseBean<NF_VsoDemandInfoResbean> newSuccessResBean(NF_VsoDemandInfoResbean data) {
        BaseResponseBean<NF_VsoDemandInfoResbean> bean = new BaseResponseBean<>();
        bean.setData(data);
        bean.setStatus(BaseResponseBean.STATUS_SUCCESS);
        bean.setMsg("success");
        return bean;
    }

    @Override
    protected boolean isBeanError(Object o) {
        if (o instanceof NF_VsoDemandInfoResbean) {
            NF_VsoDemandInfoResbean bean = (NF_VsoDemandInfoResbean) o;
            if (StringUtil.isEmpty(bean.getTask_bn())) {
                MSG_ERROR = "task_bn is null";
                return BEAN_IS_ERROR;
            }
            return BEAN_IS_CORRECT;
        } else {
            Log.wtf(API_NAME, "check you code,bean not match because your error");
            return BEAN_IS_ERROR;
        }
    }
}
