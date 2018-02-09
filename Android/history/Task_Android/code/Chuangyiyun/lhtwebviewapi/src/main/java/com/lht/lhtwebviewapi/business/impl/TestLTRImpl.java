package com.lht.lhtwebviewapi.business.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lht.lhtwebviewapi.business.API.API;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;
import com.lht.lhtwebviewlib.business.impl.ABSLTRApiImpl;

/**
 * @ClassName: TestLTRImpl
 * @Description: TODO
 * @date 2016年2月22日 上午10:02:59
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class TestLTRImpl extends ABSLTRApiImpl implements API.TestLTRHandler {
	private CallBackFunction mFunction;

	@Override
	public void handler(String data, CallBackFunction function) {
		mFunction = function;
		execute(getLTRExecutor());
	}

	@Override
	protected LTRHandler getLTRHandler() {
		return new LTRHandler() {

			@Override
			public void onJobExecuted(String data) {
				mFunction.onCallBack(data);
			}

		};
	}

	@Override
	protected boolean isBeanError(Object o) {
		return false;
	}

	@Override
	protected LTRExecutor getLTRExecutor() {
		return new DemoExecutor(getLTRHandler());
	}

	class DemoExecutor extends LTRExecutor {

		public DemoExecutor(LTRHandler h) {
			super(h);
		}

		@Override
		public void run() {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			JSONObject jObj = new JSONObject();
			jObj.put("ltrResponseKey1", "ltrResponseValue1");
			onJobExecuted(JSON.toJSONString(jObj));
		}

	}

}
