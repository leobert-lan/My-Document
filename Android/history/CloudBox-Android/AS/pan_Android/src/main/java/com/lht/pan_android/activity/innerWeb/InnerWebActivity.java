package com.lht.pan_android.activity.innerWeb;

import com.lht.pan_android.R;
import com.lht.pan_android.activity.asyncProtected.LoginActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public abstract class InnerWebActivity extends Activity {

	private String url;

	private WebView wView;

	private TextView title;

	private ProgressBar progressbar;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inner_web);

		wView = (WebView) findViewById(R.id.protocol_webview);
		title = (TextView) findViewById(R.id.innerweb_title);
		progressbar = (ProgressBar) findViewById(R.id.innerweb_pb);

		WebSettings wSet = wView.getSettings();
		wSet.setJavaScriptEnabled(true);

		// 不使用cache
		wSet.setCacheMode(WebSettings.LOAD_NO_CACHE);

		wView.setWebViewClient(new webViewClient());
		wView.setWebChromeClient(new ChromeView());

		url = getUrl();

		wView.loadUrl(url);

		ImageView back = (ImageView) findViewById(R.id.activity_protocol_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// finish();
				onBackPressed();
			}
		});

		if (getTitleStr() == null)
			title.setText(getResources().getString(getMyTitle()));
		else {
			title.setText(getTitleStr());
		}
	}

	@Override
	public void onBackPressed() {
		if (wView.canGoBack()) {
			wView.goBack();
		} else
			super.onBackPressed();
	}

	private class ChromeView extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			// TODO Auto-generated method stub
			super.onProgressChanged(view, newProgress);
			if (newProgress == 100) {
				progressbar.setVisibility(View.GONE);
			} else {
				if (progressbar.getVisibility() == View.GONE)
					progressbar.setVisibility(View.VISIBLE);
				progressbar.setProgress(newProgress);
			}
		}
	}

	private class webViewClient extends WebViewClient {

		// 重写shouldOverrideUrlLoading方法，使点击链接后不使用其他的浏览器打开。
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.equals("https://account.vsochina.com/user/login")) {
				Intent i = new Intent(InnerWebActivity.this, LoginActivity.class);
				startActivity(i);
				InnerWebActivity.this.finish();
				return false;
			}
			view.loadUrl(url);
			// 如果不需要其他对点击链接事件的处理返回true，否则返回false
			return true;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && wView.canGoBack()) {
			wView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected abstract String getUrl();

	protected abstract int getMyTitle();

	protected String getTitleStr() {
		return null;

	}

}
