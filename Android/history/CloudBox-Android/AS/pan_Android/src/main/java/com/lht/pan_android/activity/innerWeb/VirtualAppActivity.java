package com.lht.pan_android.activity.innerWeb;

import com.lht.pan_android.R;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.util.CloudBoxApplication;
import com.lht.pan_android.view.DragLayout;
import com.lht.pan_android.view.DragLayout.OnDragChildClickListener;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class VirtualAppActivity extends UMengActivity {

	private static String PageName = "VirtualAppActivity";

	public static String KEY_URL = "key_url";

	private ProgressBar progressbar;

	private WebView webView;

	private ImageView btnBack;
	DragLayout frameLayout;

	private String url;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_virtual_app);
		CloudBoxApplication.addActivity(this);

		webView = (WebView) findViewById(R.id.webView_test);
		progressbar = (ProgressBar) findViewById(R.id.virtualapp_pregress);

		Intent intent = getIntent();
		url = intent.getStringExtra(KEY_URL);

		btnBack = (ImageView) findViewById(R.id.virtual_app_back);
		frameLayout = (DragLayout) findViewById(R.id.becausefloat);
		frameLayout.addDragImageListener(btnBack, new OnDragChildClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		// 不使用cache
		WebSettings webSettings = webView.getSettings();
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		webSettings.setJavaScriptEnabled(true);

		webView.setWebViewClient(new webViewClient());
		webView.setWebChromeClient(new ChromeView());

//		webView.loadUrl(url);

	}

	@Override
	protected void onPause() {
		webView.loadData("", "text/HTML", "UTF-8");
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		webView.loadData("", "text/HTML", "UTF-8");
		frameLayout.bringToFront();
		webView.loadUrl(url);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		webView.loadData("", "text/HTML", "UTF-8");
		super.onDestroy();
	}

	private class ChromeView extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
			if (newProgress == 100) {
				progressbar.setVisibility(View.GONE);
			} else {
				if (progressbar.getVisibility() == View.GONE)
					progressbar.setVisibility(View.VISIBLE);
				progressbar.bringToFront();
				progressbar.setProgress(newProgress);
			}
		}

	}

	private class webViewClient extends WebViewClient {

		// 重写shouldOverrideUrlLoading方法，使点击链接后不使用其他的浏览器打开。
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed(); // 接受所有证书
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			view.setVisibility(View.INVISIBLE);
		}

		@Override
		public void onPageFinished(final WebView view, String url) {
			super.onPageFinished(view, url);
			// 继续显示一下
			progressbar.setVisibility(View.VISIBLE);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					progressbar.setVisibility(View.GONE);
					view.setVisibility(View.VISIBLE);
				}
			}, 2000);

		}
	}

	@Override
	protected String getPageName() {
		return VirtualAppActivity.PageName;
	}

	@Override
	protected UMengActivity getActivity() {
		return VirtualAppActivity.this;
	}

	// /**
	// * 或许你会觉得奇怪，这是啥玩意。这个webrdp要是不返回就会保持session。。
	// *
	// * @see android.app.Activity#onBackPressed()
	// */
	// @Override
	// public void onBackPressed() {
	//// if (webView.canGoBack())
	//// webView.goBack();
	// webView.loadData("", "text/HTML", "UTF-8");
	// webView = null;
	// super.onBackPressed();
	// }

}
