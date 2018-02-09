package com.lht.lhtwebviewlib;

import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.lht.lhtwebviewlib.base.Interface.IMediaTrans;


/**
 * @ClassName: BridgeWebChoreClient
 * @Description: TODO
 * @date 2016年2月23日 下午4:21:49
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class BridgeWebChromeClient extends WebChromeClient {

	private IMediaTrans iMediaTrans;
	
	private static final String tag  = "BridgeWebChromeClient";

	public void setIMediaTrans(IMediaTrans iMediaTrans) {
		this.iMediaTrans = iMediaTrans;
	}

	/**
	 * 覆盖默认的window.alert展示界面
	 */
	@Override
	public boolean onJsAlert(WebView view, String url, String message,
			final JsResult result) {
		JSAlertDialog dialog = new JSAlertDialog(view.getContext(), result);
		dialog.fixContent(message);
		dialog.show();
		return true;
	}

	@Override
	public boolean onJsBeforeUnload(WebView view, String url, String message,
			JsResult result) {
		return super.onJsBeforeUnload(view, url, message, result);
	}

	/**
	 * 覆盖默认的window.confirm展示界面
	 */
	@Override
	public boolean onJsConfirm(WebView view, String url, String message,
			final JsResult result) {
		JSConfirmDialog dialog = new JSConfirmDialog(view.getContext(), result);
		dialog.fixContent(message);
		dialog.show();

		return true;
	}


	@Override
	public void onShowCustomView(View view, CustomViewCallback callback) {
		// TODO Auto-generated method stub
		super.onShowCustomView(view, callback);
		if (iMediaTrans != null) {
			iMediaTrans.onShowCustomView(view, callback);
		} else {
			Log.e(tag,"iMediaTrans is null,set at first");
		}
	}
	
	@Override
	public void onHideCustomView() {
		// TODO Auto-generated method stub
		super.onHideCustomView();
		if (iMediaTrans != null) {
			iMediaTrans.onHideCustomView();
		} else {
			Log.e(tag,"iMediaTrans is null,set at first");
		}
	}




}
