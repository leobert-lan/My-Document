package com.lht.lhtwebviewapi.business.impl;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lht.lhtwebviewapi.business.API.API;
import com.lht.lhtwebviewapi.business.API.NativeRet;
import com.lht.lhtwebviewapi.business.bean.CopyToClipboardBean;
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
public class CopyToClipboardImpl extends ABSApiImpl implements API.SendToClipBoardHandler {

	private final Context mContext;

	private CallBackFunction mFunction;

	public CopyToClipboardImpl(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	public void handler(String data, CallBackFunction function) {
		mFunction = function;

		CopyToClipboardBean copyClipboardBean = JSON.parseObject(data,
				CopyToClipboardBean.class);
		boolean bool = isBeanError(copyClipboardBean);

		if (!bool) {
			String clipBoard = copyClipboardBean.getContent();
			ClipboardManager myClipboardManager = (ClipboardManager) mContext
					.getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData myClip;
			myClip = ClipData.newPlainText("text", clipBoard);
			myClipboardManager.setPrimaryClip(myClip);

			BaseResponseBean bean = new BaseResponseBean();
			bean.setRet(NativeRet.RET_SUCCESS);
			bean.setMsg("content has been send to clipboard");
			bean.setData("");
			mFunction.onCallBack(JSON.toJSONString(bean));
		} else {
			BaseResponseBean bean = new BaseResponseBean();
			bean.setRet(NativeRet.NativeCopyToClipBorad.RET_ERROR_CONTENTNULL);
			bean.setMsg("content is needed,this function is not for flush!");
			bean.setData("");
			mFunction.onCallBack(JSON.toJSONString(bean));
		}
	}

	@Override
	protected boolean isBeanError(Object o) {
		if (o instanceof CopyToClipboardBean) {
			CopyToClipboardBean bean = (CopyToClipboardBean) o;
			if (TextUtils.isEmpty(bean.getContent())) {
				Log.wtf(API_NAME,
						"20000,data error,check bean:" + JSON.toJSONString(bean));
				return BEAN_IS_ERROR;
			}
			return BEAN_IS_CORRECT;

		} else {
			Log.wtf(API_NAME,
					"41000:check you code,bean not match because your error");
			return BEAN_IS_ERROR;
		}
	}
}
