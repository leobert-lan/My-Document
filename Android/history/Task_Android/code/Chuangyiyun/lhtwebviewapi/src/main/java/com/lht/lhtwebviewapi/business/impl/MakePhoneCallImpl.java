package com.lht.lhtwebviewapi.business.impl;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.google.zxing.common.StringUtils;
import com.lht.lhtwebviewapi.business.API.API;
import com.lht.lhtwebviewapi.business.API.NativeRet;
import com.lht.lhtwebviewapi.business.bean.PhoneNumBean;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;
import com.lht.lhtwebviewlib.base.model.BridgeNativeFunction;
import com.lht.lhtwebviewlib.business.bean.BaseResponseBean;
import com.lht.lhtwebviewlib.business.impl.ABSApiImpl;

import java.lang.ref.WeakReference;

/**
 * @author leobert.lan
 * @version 1.0
 * @ClassName: DemoImpl
 * @Description: TODO
 * @date 2016年2月19日 下午4:11:26
 */
public class MakePhoneCallImpl extends ABSApiImpl implements API.CallTelHandler {

    private final WeakReference<Context> contextRef;

    private CallBackFunction mFunction;

    private static final String MSG_SUCCESS = "success";
    private static final String MSG_FAILURE = "fail";
    private static final String MSG_FAILURE_INTERNAL = "cannot operate this call,context is null";
    private static final int RET_FAILURE = 0;

    public MakePhoneCallImpl(Context mContext) {
        contextRef = new WeakReference<>(mContext);
    }

    @Override
    public void handler(String data, CallBackFunction function) {
        mFunction = function;
        Context context = contextRef.get();
        if (context == null) {
            BaseResponseBean<String> bean = newFailureResBean(RET_FAILURE, MSG_FAILURE_INTERNAL);
            mFunction.onCallBack(JSON.toJSONString(bean));
            return;
        }
        PhoneNumBean phoneNumBean = JSON.parseObject(data, PhoneNumBean.class);

        if (!isBeanError(phoneNumBean)) {
            String number = phoneNumBean.getTelphone();
            makePhoneCall(context,number);

            BaseResponseBean<String> bean = newSuccessResBean();
            mFunction.onCallBack(JSON.toJSONString(bean));
        } else {
            BaseResponseBean<String> bean = newFailureResBean(RET_FAILURE, MSG_FAILURE);
            mFunction.onCallBack(JSON.toJSONString(bean));
        }
    }

    private void makePhoneCall(Context context,String phone) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        context.startActivity(intent);
    }

    @Override
    protected boolean isBeanError(Object o) {
        if (o instanceof PhoneNumBean) {
            PhoneNumBean bean = (PhoneNumBean) o;
            if (TextUtils.isEmpty(bean.getTelphone())) {
                Log.wtf(API_NAME,
                        "30001:data error,check bean:" + JSON.toJSONString(bean));
                return BEAN_IS_ERROR;
            }
//            // TODO: 2016/12/9 rules
//            if (!bean.getTelphone().matches("[0-9]+")) {
//                Log.wtf(API_NAME,
//                        "30002:data error,check bean:" + JSON.toJSONString(bean));
//                return BEAN_IS_ERROR;
//            }
            return BEAN_IS_CORRECT;
        } else {
            Log.wtf(API_NAME,
                    "check you code,bean not match because your error");
            return BEAN_IS_ERROR;
        }
    }

    /*for test*/
//    public
    private BaseResponseBean<String> newSuccessResBean() {
        BaseResponseBean<String> bean = new BaseResponseBean<>();
        bean.setStatus(BaseResponseBean.STATUS_SUCCESS);
        bean.setMsg(MSG_SUCCESS);
        bean.setData(null);
        return bean;
    }

    /*for test*/
//    public
    private BaseResponseBean<String> newFailureResBean(int ret, String msg) {
        BaseResponseBean<String> bean = new BaseResponseBean<>();
        bean.setStatus(BaseResponseBean.STATUS_FAILURE);
        bean.setRet(ret);
        bean.setMsg(msg);
        return bean;
    }

    public static BridgeNativeFunction newInstance(Context context) {
        return new BridgeNativeFunction(API_NAME, new MakePhoneCallImpl(context));
    }

}
