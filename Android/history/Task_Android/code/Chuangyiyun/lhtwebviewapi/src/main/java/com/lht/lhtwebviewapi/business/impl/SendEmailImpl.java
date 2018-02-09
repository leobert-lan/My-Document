package com.lht.lhtwebviewapi.business.impl;

import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lht.lhtwebviewapi.business.API.API;
import com.lht.lhtwebviewapi.business.API.NativeRet;
import com.lht.lhtwebviewapi.business.bean.SendEmailBean;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;
import com.lht.lhtwebviewlib.business.bean.BaseResponseBean;
import com.lht.lhtwebviewlib.business.impl.ABSApiImpl;

/**
 * @ClassName: DemoImpl
 * @Description: TODO
 * @date 2016年2月19日 下午4:11:26
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class SendEmailImpl extends ABSApiImpl implements API.SendEmailHandler {

	private final Context mContext;

	private CallBackFunction mFunction;

	public SendEmailImpl(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	public void handler(String data, CallBackFunction function) {
		mFunction = function;

		SendEmailBean sendEmailBean = JSON.parseObject(data,
				SendEmailBean.class);
		boolean bool = isBeanError(sendEmailBean);
		if (!bool) {
			Intent myIntent = new Intent(Intent.ACTION_SEND);
			myIntent.setType("plain/text");
			myIntent.putExtra(Intent.EXTRA_EMAIL,
					sendEmailBean.getAddressee());
			myIntent.putExtra(Intent.EXTRA_TEXT,
					sendEmailBean.getMessage());
			mContext.startActivity(Intent.createChooser(myIntent, "请选择邮件"));

			BaseResponseBean bean = new BaseResponseBean();
			bean.setRet(NativeRet.RET_SUCCESS);
			bean.setMsg("OK");
			bean.setData("");
			mFunction.onCallBack(JSON.toJSONString(bean));
		} else {

		}
	}

	@Override
	protected boolean isBeanError(Object o) {
		if (o instanceof SendEmailBean) {
			SendEmailBean bean = (SendEmailBean) o;
			if (TextUtils.isEmpty(bean.getAddressee().toString())) {
				Log.wtf(API_NAME,
						"51001,data error,check bean:" + JSON.toJSONString(bean));
				return BEAN_IS_ERROR;
			}
			if (TextUtils.isEmpty(bean.getMessage())) {
				Log.wtf(API_NAME,
						"51002,data error,check bean:" + JSON.toJSONString(bean));
				return BEAN_IS_ERROR;
			}
			if (!Pattern
					.compile(
							"^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$")
					.matcher(bean.getAddressee()[0]).matches()) {
				Log.wtf(API_NAME,
						"51004,data error,check bean:"
								+ JSON.toJSONString(bean));
				return BEAN_IS_ERROR;
			}
			return BEAN_IS_CORRECT;

		} else {
			Log.wtf(API_NAME,
					"check you code,bean not match because your error");
			return BEAN_IS_ERROR;
		}
	}

}
