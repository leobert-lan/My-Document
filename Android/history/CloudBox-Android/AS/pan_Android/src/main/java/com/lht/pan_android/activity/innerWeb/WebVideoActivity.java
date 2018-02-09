package com.lht.pan_android.activity.innerWeb;

import com.alibaba.fastjson.JSON;
import com.lht.pan_android.R;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.bean.VideoItem;
import com.lht.pan_android.util.SystemUiHider;
import com.lht.pan_android.util.ToastUtil;
import com.lht.pan_android.util.ToastUtil.Duration;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class WebVideoActivity extends UMengActivity implements OnTouchListener {

	private String url;

	private WebView wView;

	public final static String KEY_DATA = "_data";

	private static final String PAGENAME = "WebVideoActivity";

	private VideoItem item;

	private boolean isToolShown = false;

	private View bar;

	// private View refreshView;
	//
	// private ImageButton btnRefresh;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_web_video);

		String data = getIntent().getStringExtra(KEY_DATA);

		item = JSON.parseObject(data, VideoItem.class);

		registerReceiver();

		bar = findViewById(R.id.fullscreen_content_controls);
		wView = (WebView) findViewById(R.id.fullscreen_content);

		findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		TextView title = (TextView) findViewById(R.id.video_tv_title);
		title.setText(item.getName());

		WebSettings wSet = wView.getSettings();
		wSet.setJavaScriptEnabled(true);
		wSet.setJavaScriptCanOpenWindowsAutomatically(true);
		wSet.setAllowFileAccess(true);
		wSet.setDefaultTextEncodingName("UTF-8");
		wSet.setLoadWithOverviewMode(true);
		wSet.setUseWideViewPort(true);

		// 不使用cache
		wSet.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

		url = getUrl();
		refresh();
		wView.setOnTouchListener(this);
		showToolbar();
	}

	private void refresh() {
		wView.loadUrl(url);
		networkStateCheck();
	}

	private String getUrl() {
		return item.getUrl();
	}

	@Override
	protected String getPageName() {
		return WebVideoActivity.PAGENAME;
	}

	@Override
	protected UMengActivity getActivity() {
		return WebVideoActivity.this;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (!isToolShown) {
			showToolbar();
		}
		return false;
	}

	private void showToolbar() {
		isToolShown = true;
		bar.setVisibility(View.VISIBLE);
		bar.bringToFront();
		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

			@Override
			public void run() {
				hideToolbar();
			}
		}, 3000);
	}

	protected void hideToolbar() {
		bar.setVisibility(View.GONE);
		isToolShown = false;
	}

	private BroadcastReceiver netChangedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				// Log.d("mark", "网络状态已经改变");
				networkStateCheck();
			}
		}
	};

	/**
	 * 检测网络状态，并激活任务
	 */
	protected void networkStateCheck() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobileNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (wifiInfo != null && wifiInfo.isConnected()) {
			onWifiConnect();
		} else if (mobileNetInfo != null && mobileNetInfo.isConnected()) {
			onMobileNetConnect();
		} else {
			onNoInternet();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (wView != null) {
			wView.onPause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (wView != null) {
			wView.onResume();
		}
	}

	/**
	 * @Title: onWifiConnect
	 * @Description: TODO
	 * @author: leobert.lan
	 */
	private void onWifiConnect() {
		// ToastUtil.show(getActivity(), R.string.video_onwifi, Duration.s);
	}

	private void onMobileNetConnect() {
		ToastUtil.show(getActivity(), R.string.video_warmon4g, Duration.s);
	}

	private void onNoInternet() {
		ToastUtil.show(getActivity(), R.string.no_internet, Duration.s);
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		this.registerReceiver(netChangedReceiver, filter);
	}

	@Override
	protected void onDestroy() {
		this.unregisterReceiver(netChangedReceiver);
		if (wView != null) {
			wView.onPause();
		}
		super.onDestroy();
	}

	
}
