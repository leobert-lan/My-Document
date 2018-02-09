package com.lht.lhtwebviewapi.business.impl;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lht.lhtwebviewapi.business.API.API;
import com.lht.lhtwebviewapi.business.API.NativeRet;
import com.lht.lhtwebviewapi.business.bean.GetClipboardBean;
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
public class GetClipboardContentImpl extends ABSApiImpl implements
		API.GetClipBoardContentHandler {

	private final Context mContext;

	private CallBackFunction mFunction;

	public GetClipboardContentImpl(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	public void handler(String data, CallBackFunction function) {
		mFunction = function;

		GetClipboardBean copyClipboardBean = JSON.parseObject(data, GetClipboardBean.class);
		boolean bool = isBeanError(copyClipboardBean);

		if (!bool) {
			String clipBoard = copyClipboardBean.getContent();
			ClipboardManager myClipboardManager = (ClipboardManager) mContext
					.getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData myClip;
			myClip = ClipData.newPlainText("text", clipBoard);
			myClipboardManager.setPrimaryClip(myClip);

			myClipboardManager.setPrimaryClip(myClip);	
			
			/*//获取剪切板的数据
			ClipboardManager clipboard = (ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(copyClipboardBean.getContent());
			String text = clipboard.getText().toString();
			*/
			BaseResponseBean bean = new BaseResponseBean();
			bean.setRet(NativeRet.RET_SUCCESS);
			bean.setMsg("OK");
			bean.setData(copyClipboardBean.getContent());
			mFunction.onCallBack(JSON.toJSONString(bean));
		} else {

		}
	}

	@Override
	protected boolean isBeanError(Object o) {
		if (o instanceof GetClipboardBean) {
			GetClipboardBean bean = (GetClipboardBean) o;
			if (TextUtils.isEmpty(bean.getContent())) {
				Log.wtf(API_NAME,
						"20000:data error,check bean:" + JSON.toJSONString(bean));
				return BEAN_IS_ERROR;
			}
			return BEAN_IS_CORRECT;
		} else {
			Log.wtf(API_NAME,
					"42000:check you code,bean not match because your error");
			return BEAN_IS_ERROR;
		}
	}

}
