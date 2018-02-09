package com.lht.lhtwebviewlib;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.lht.lhtwebviewlib.base.model.BridgeUtil;
import com.lht.lhtwebviewlib.base.model.Message;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @ClassName: BridgeWebViewClient
 * @Description: TODO
 * @date 2016年2月17日 下午12:45:27
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class BridgeWebViewClient extends WebViewClient {

	private static final String TAG = "BridgeWebViewClient";

	private BridgeWebView webView;

	public BridgeWebViewClient(BridgeWebView webView) {
		this.webView = webView;
	}

	/**
	 * 重写URL加载 如果URL 符合API约定，执行对应API方法 并消费事件，否则调用父类方法
	 * 
	 * @see WebViewClient#shouldOverrideUrlLoading(WebView,
	 *      String)
	 */
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		try {
			url = URLDecoder.decode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		if (url.startsWith(BridgeUtil.YY_RETURN_DATA)) {
			// 如果是返回数据
			// Log.d(TAG, "check the url :"+url);

			webView.handlerReturnData(url);
			return true;
		} else if (url.startsWith(BridgeUtil.YY_OVERRIDE_SCHEMA)) {
			// handler called
			// Log.d(TAG, "check the url :"+url);

			webView.flushMessageQueue();
			return true;
		} else {
			return super.shouldOverrideUrlLoading(view, url);
		}
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		super.onPageStarted(view, url, favicon);
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);

		if (BridgeWebView.toLoadJs != null) {
			BridgeUtil.webViewLoadLocalJs(view, BridgeWebView.toLoadJs);
		}
		
//		view.loadUrl("javascript:"+IMediaTrans.AUTOLAYOUT_JS);

		//
		if (webView.getStartupMessage() != null) {
			for (Message m : webView.getStartupMessage()) {
				webView.dispatchMessage(m);
			}
			webView.setStartupMessage(null);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
		super.onReceivedError(view, errorCode, description, failingUrl);
	}

}