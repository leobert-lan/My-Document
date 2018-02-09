package com.lht.lhtwebviewlib.base.Interface;

/**
 * @ClassName: WebViewJavascriptBridge
 * @Description: Native向js发送数据的接口
 * @date 2016年2月17日 上午10:43:24
 * 
 * @author leobert.lan
 * @version 1.0
 */
public interface WebViewJavascriptBridge {

	/**
	 * @Title: send
	 * @Description: 单纯发送数据
	 * @author: leobert.lan
	 * @param data
	 */
	void send(String data);

	/**
	 * @Title: send
	 * @Description: 发送回调方法产生的数据
	 * @author: leobert.lan
	 * @param data
	 * @param responseCallback
	 */
	void send(String data, CallBackFunction responseCallback);

}
