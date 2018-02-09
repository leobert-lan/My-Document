package com.lht.lhtwebviewapi.business.impl;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lht.lhtwebviewapi.business.API.API;
import com.lht.lhtwebviewapi.business.bean.SendMessageBean;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;
import com.lht.lhtwebviewlib.base.model.BridgeNativeFunction;
import com.lht.lhtwebviewlib.business.bean.BaseResponseBean;
import com.lht.lhtwebviewlib.business.impl.ABSApiImpl;

import java.lang.ref.WeakReference;

/**
 * 发送消息桥接实现
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class SendMessageImpl extends ABSApiImpl implements API.SendMessageHandler {

	private final WeakReference<Context> contextRef;

	private CallBackFunction mFunction;


    private static final String MSG_SUCCESS = "success";
    private static final String MSG_FAILURE = "fail";
    private static final String MSG_FAILURE_INTERNAL = "cannot operate this call,context is null";

	public SendMessageImpl(Context mContext) {
        contextRef = new WeakReference<>(mContext);
	}

	@Override
	public void handler(String data, CallBackFunction function) {
		mFunction = function;

		SendMessageBean sendMessageBean = JSON.parseObject(data,
				SendMessageBean.class);

        Context context = contextRef.get();
        if (context == null) {
            BaseResponseBean<String> bean = newFailureResBean(0,MSG_FAILURE_INTERNAL);
            mFunction.onCallBack(JSON.toJSONString(bean));
        }

		if (!isBeanError(sendMessageBean)) {
			String message = sendMessageBean.getMessageContent();
			Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"
					+ sendMessageBean.getContacts()));
			intent.putExtra("sms_body", message);
			context.startActivity(intent);

			BaseResponseBean<String> bean = newSuccessResBean();
			mFunction.onCallBack(JSON.toJSONString(bean));
		} else {
            BaseResponseBean<String> bean = newFailureResBean(0,MSG_FAILURE);
            mFunction.onCallBack(JSON.toJSONString(bean));
		}
	}

	@Override
	protected boolean isBeanError(Object o) {
		if (o instanceof SendMessageBean) {
			SendMessageBean bean = (SendMessageBean) o;
			if (TextUtils.isEmpty(bean.getContacts())) {
				Log.wtf(API_NAME,
						"41001,data error,check bean:" + JSON.toJSONString(bean));
//				mFunction.onCallBack("您的发送对象为空");
				return BEAN_IS_ERROR;
			}
			if (TextUtils.isEmpty(bean.getMessageContent())) {
				Log.wtf(API_NAME,
						"41002,data error,check bean:" + JSON.toJSONString(bean));
				return BEAN_IS_ERROR;
			}
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
        return new BridgeNativeFunction(API_NAME, new SendMessageImpl(context));
    }

}
