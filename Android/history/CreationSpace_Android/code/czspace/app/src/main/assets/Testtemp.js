//为了减少js调用NativeAPI出错的机率，定义这样一个可以免去频繁输入方法名
//但是会增加加载内容，该部分如何设计由H5人员把控

//Log: 2016-2-22 13:38:55 为了避免H5 wakeng ，按照业务分JS .... 你懂得
//function callNativeOpenGPSAndResponseLocation(onNativeResponseCallback) {
//
//	connectWebViewJavascriptBridge(function(bridge) {
//		bridge.callHandler('NF_OPENGPS', {
//			'key1' : 'this is test data,this native api do not need data'
//		}, onNativeResponseCallback)
//	})
//
//};

function callErrorFunc() {

	setupJSBridge(function(bridge) {
		bridge.callHandler('wtf', {
			'key1' : 'test data'
		}, null)
	})

};

function callLongTimeDemo(data,callback) {
	setupJSBridge(function(bridge) {
		bridge.callHandler('NATIVE_FUNCTION_LTDEMO',data,callback);
	})
}