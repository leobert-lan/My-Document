package com.lht.lhtwebviewapi.business.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.lht.lhtwebviewapi.business.API.API;
import com.lht.lhtwebviewapi.business.API.NativeRet;
import com.lht.lhtwebviewapi.business.API.NativeRet.NativeScanCodeRet;
import com.lht.lhtwebviewapi.business.bean.ScanCodeResponseBean;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;
import com.lht.lhtwebviewlib.business.bean.BaseResponseBean;
import com.lht.lhtwebviewlib.business.impl.ABSLTRApiImpl;
import com.lht.qrcode.scan.ScanActivity;

/**
 * @ClassName: ScanCodeImpl
 * @Description: TODO
 * @date 2016年2月24日 上午10:17:01
 *
 * @author leobert.lan
 * @version 1.0
 */
public class ScanCodeImpl extends ABSLTRApiImpl implements API.ScanCodeHandler {

	private final Context mContext;

	private CallBackFunction mCallBackFunction;

	public ScanCodeImpl(Context ctx) {
		mContext = ctx;
	}

	@Override
	public void handler(String data, CallBackFunction function) {
		mCallBackFunction = function;
		execute(getLTRExecutor());
		Intent i = new Intent(mContext, ScanActivity.class);
		mContext.startActivity(i);
	}

	@Override
	protected LTRHandler getLTRHandler() {
		return new LTRHandler() {

			@Override
			public void onJobExecuted(String data) {
				Log("解除广播");
				mContext.unregisterReceiver(mReceiver);
				mCallBackFunction.onCallBack(data);
			}
		};
	}

	@Override
	protected LTRExecutor getLTRExecutor() {
		executor = new ScanCodeExecutor(getLTRHandler());
		return executor;
	}

	private LTRExecutor executor;

	@Override
	protected boolean isBeanError(Object o) {
		return false;
	}

	class ScanCodeExecutor extends LTRExecutor {

		public ScanCodeExecutor(ABSLTRApiImpl.LTRHandler h) {
			super(h);
		}

		@Override
		public void run() {
			Looper.prepare();
			IntentFilter intentFilter = new IntentFilter(
					ScanActivity.BROADCAST_ACTION);
			mContext.registerReceiver(mReceiver, intentFilter);
			Looper.loop();
		}

	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log("receive broadcast");
			if (intent.getAction()
					.equals(ScanActivity.BROADCAST_ACTION)) {
				// TODO
				// 处理情况
				int scanResultCode = intent.getIntExtra(
						ScanActivity.RESULT_CODE,
						ScanActivity.SCAN_FAILURE);
				String data = intent
						.getStringExtra(ScanActivity.RESULT);
				ScanCodeResponseBean scanCodeResponseBean = new ScanCodeResponseBean();
				scanCodeResponseBean.setContent(data);
				switch (scanResultCode) {
				case ScanActivity.SCAN_OK:
					response(NativeRet.RET_SUCCESS, "success", data);
					break;
				case ScanActivity.SCAN_FAILURE:
					response(NativeScanCodeRet.RET_UNSUPPORT, "unsupported encoding type", data);
					break;
				case ScanActivity.SCAN_TIMEOUT:
					response(NativeScanCodeRet.RET_TIMEOUT, "scan time out", data);
					break;
				case ScanActivity.SCAN_CANCEL:
					response(ScanCodeResponseBean.RET_CANCEL, "scan canceled by user", data);
				default:
					break;
				}

			} else {
				Log("check broadcast,seem error!");
			}
		}

		private void response(int ret,String msg,String data) {
			BaseResponseBean bean = new BaseResponseBean();
			bean.setData(data);
			bean.setMsg(msg);
			bean.setRet(ret);
			executor.mHandler.onJobExecuted(JSON.toJSONString(bean));
		}
	};
}
