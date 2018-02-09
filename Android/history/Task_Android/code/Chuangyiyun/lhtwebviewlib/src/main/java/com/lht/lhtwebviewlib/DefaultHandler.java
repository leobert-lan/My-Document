package com.lht.lhtwebviewlib;


import com.lht.lhtwebviewlib.base.Interface.BridgeHandler;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;

/**
 * @ClassName: DefaultHandler
 * @Description: BridgeHandler接口实现类
 * @date 2016年2月17日 上午10:33:40
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class DefaultHandler implements BridgeHandler {

	String TAG = "DefaultHandler";

	@Override
	public void handler(String data, CallBackFunction function) {
		if (function != null) {
			function.onCallBack("DefaultHandler response data");
		}
	}

}
