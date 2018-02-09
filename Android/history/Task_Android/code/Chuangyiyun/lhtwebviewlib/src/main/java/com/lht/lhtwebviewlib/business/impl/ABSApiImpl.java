package com.lht.lhtwebviewlib.business.impl;

import android.content.Context;

import com.lht.lhtwebviewlib.BridgeWebView;

/**
 * @ClassName: ABSApiImpl
 * @Description: API接口定义的扩展接口的实现类的抽象类，即业务类 model层 <br>
 *               Attention：属于Model层，为了减少耦合，针对每个业务，继承该抽象类并具体实现<br>
 *               如果存在更新V的需求(这里特指更新原生的部分，h5部分使用CallBackFunction进行通信),应当再定义单独的接口，
 *               通知P或者C
 * @date 2016年2月19日 下午4:37:20
 * @author leobert.lan
 * @version 1.0
 */
public abstract class ABSApiImpl {
//		implements API {

	public ABSApiImpl() {

	}

	protected Context mContext;

	public ABSApiImpl(final Context ctx) {
		mContext = ctx;
	}

	protected final String TAG = "ABSApiImpl";

	protected final boolean BEAN_IS_ERROR = true;

	protected final boolean BEAN_IS_CORRECT = false;

	/**
	 * @Title: isBeanError
	 * @Description: check the data from js is correct and enough for a API-job
	 * @author: leobert.lan
	 * @param o
	 * @return BEAN_IS_ERROR(true) if error, BEAN_IS_CORRECT(false) otherwise;
	 */
	protected abstract boolean isBeanError(Object o);

	// if it is necessary to update View in native,
	// you should modify a interface and a interface-set function,
	// the instance of interface should be initializes by presenter or
	// controller,function should be implemented to update view

	protected void Log(String msg) {
		if (BridgeWebView.isDebugMode()) {
			android.util.Log.d(TAG, msg);
		}

	}

}
