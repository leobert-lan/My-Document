//
//  H5JSBridge
//  H5
//
//  Created by Qingyang on 16/2/5.
//  Copyright © 2016年 Qingyang. All rights reserved.
//

//启动监听
//不存在Native 调用 JS GPS相关的API
setupJSBridge(function(bridge) {

	bridge.registerHandler('testJavascriptHandler', function(data,
			responseCallback) {
		//收到数据，在页面打印
		log('原生调H5', data)
		//回调原生，发送新数据
		var responseData = {
			'testJavascriptHandler call back to native' : 'recu data!'
		}
		responseCallback(responseData)
	})

})


//调原生GPS定位信息
function callNativeSendMessage(data, responseCallback) {

	setupJSBridge(function(bridge) {
		bridge.callHandler('NATIVE_FUNCTION_SENDMESSAGE', data, responseCallback)
	})
};
