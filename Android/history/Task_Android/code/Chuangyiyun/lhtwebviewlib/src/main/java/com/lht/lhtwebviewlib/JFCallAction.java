package com.lht.lhtwebviewlib;


import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;

/**
 * @ClassName: JFCallAction
 * @Description: TODO
 * @date 2016年4月25日 下午12:53:08
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class JFCallAction {

	private final String mJsFunctionName;

	private JFParams mParams;

	private CallBackFunction mCallBack;

	public JFCallAction(String functionName) {
		this(functionName, null);
	}

	public JFCallAction(String functionName, JFParams params) {
		this(functionName, params, null);
	}

	public JFCallAction(String functionName, JFParams params,
			CallBackFunction callback) {
		this.mJsFunctionName = functionName;
		this.mParams = params;
		this.mCallBack = callback;
	}

	public JFParams getParams() {
		return mParams;
	}

	public void setParams(JFParams mParams) {
		this.mParams = mParams;
	}

	public CallBackFunction getCallBack() {
		return mCallBack;
	}

	public void setCallBack(CallBackFunction mCallBack) {
		this.mCallBack = mCallBack;
	}

	public String getJsFunctionName() {
		return mJsFunctionName;
	}

	public String getFormatedParams() {
		return mParams == null ? null : mParams.toJsonString();
	}

}
