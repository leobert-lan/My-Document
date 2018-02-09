package com.lht.lhtwebviewapi.business.impl;

import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.lht.lhtwebviewapi.business.API.API;
import com.lht.lhtwebviewapi.business.bean.DemoBean;
import com.lht.lhtwebviewapi.business.bean.DemoResponseBean;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;
import com.lht.lhtwebviewlib.business.impl.ABSApiImpl;

/**
 * @ClassName: DemoImpl
 * @Description: TODO
 * @date 2016年2月19日 下午4:11:26
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class DemoImpl extends ABSApiImpl implements API.Demo {
	
	public DemoImpl(Context ctx) {
		super(ctx);
	}

	@Override
	public void handler(String data, CallBackFunction function) {
		// TODO Auto-generated method stub
		DemoBean demoBean = JSON.parseObject(data, DemoBean.class);
		boolean b = isBeanError(demoBean);
		if (b)
			return;
		// 业务逻辑
		// ....
		WindowManager manager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		Point screenResolution = new Point();

		display.getRealSize(screenResolution);
		
//		display.getSize(screenResolution);

		// 模拟一个返回数据
		DemoResponseBean responseBean = new DemoResponseBean();
		responseBean.setDemoKeyOne("x:"+screenResolution.x);
		responseBean.setDemoKeyTwo("y:"+screenResolution.y);
		responseBean.setDemoKeyThree("value for key3");

		// 返回序列化的数据
		function.onCallBack(JSON.toJSONString(responseBean));

	}

	@Override
	protected boolean isBeanError(Object o) {
		if (o instanceof DemoBean) {
			DemoBean bean = (DemoBean) o;
			// 数据完整性、合法性 校验 example：
			if (TextUtils.isEmpty(bean.getJsKeyOne())) {
				Log.wtf(API_NAME,
						"501,data error,check bean:" + JSON.toJSONString(bean));
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
