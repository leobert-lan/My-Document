//

//null

//调原生相机扫描
function callNativeOpenCameraForScan(onNativeResponseCallback) {
	setupJSBridge(function(bridge) {
		bridge.callHandler('NATIVE_FUNCTION_OPENCAMERA_SCAN', null, onNativeResponseCallback)
	})
};
