package com.lht.lhtwebviewlib.base.Interface;

/**
 * @ClassName: BridgeHandler
 * @Description: 用接口定义的句柄 负责双向调用?双向:JS -&gt Native
 * @date 2016年2月17日 上午10:29:02
 * 
 * @author leobert.lan
 * @version 1.0
 */
public interface BridgeHandler {

	/**
	 * @Title: handler
	 * @Description: 抽象方法，统一调用形式 实现具体的逻辑
	 * @author: leobert.lan
	 * @param data
	 *            原始数据
	 * @param function
	 *            回调方法
	 */
	void handler(String data, CallBackFunction function);

}
