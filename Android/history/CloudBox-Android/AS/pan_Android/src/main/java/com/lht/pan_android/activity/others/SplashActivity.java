package com.lht.pan_android.activity.others;

import java.io.File;

import org.apache.http.Header;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;

import com.alibaba.fastjson.JSON;
import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.activity.BaseActivity;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.activity.asyncProtected.LoginActivity;
import com.lht.pan_android.activity.asyncProtected.MainActivity;
import com.lht.pan_android.activity.innerWeb.WebActivity;
import com.lht.pan_android.bean.AdsBean;
import com.lht.pan_android.mpv.model.AdsImageHunterModel;
import com.lht.pan_android.util.CloudBoxApplication;
import com.lht.pan_android.util.SPUtil;
import com.lht.pan_android.util.internet.HttpUtil;
import com.lht.pan_android.util.string.StringUtil;
import com.litesuits.orm.LiteOrm;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
//import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class SplashActivity extends BaseActivity {

	private final static int CHECKTAG = 1;

	private final static String mPageName = "splashActivity";

	private ImageView imgAd;

	private View cover;

	boolean isFirstIn = false;

	private SharedPreferences sharedPreferences;
	private HttpUtil mHttpUtil;

	private String username;
	private String access_id;
	private String access_token;
	private String urlString;

	// 设置延迟2秒
	public static final long SPLASH_DELAY_MILLIS = 2000;

	public static final long AD_DELAY_MILLIS = 5000;

	public static final String SHAREDPREFERENCES_NAME = "first_pref";
	public static final String KEY_FIRSTIN = "isFirstIn";
	/**
	 * onCheckStatus:token校验是否完成
	 */
	private boolean onCheckStatus = true;

	/**
	 * isTimeout:代表2秒计时是否结束
	 */
	private boolean isTimeout = false;

	private boolean isAdJumped = false;

	private LiteOrm liteOrm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		CloudBoxApplication.addActivity(this);
		setContentView(R.layout.activity_splash);

		reportCountEvent(COUNT_CB_QD_SHOW);

		initView();
		liteOrm = LiteOrm.newSingleInstance(getApplicationContext(), "Ads.db");

		startSplashOrAds();

		if (!isFirstIn)
			initRequest();

		queryAdsToShowNextTime();

	}

	private Handler mHandler;

	private void startSplashOrAds() {
		String adsId = getPreferences(MODE_PRIVATE).getString(IKeyManager.Ads.KEY_ADID, "");
		AdsBean adsBean = liteOrm.queryById(adsId, AdsBean.class);

		// Log.e("lmsg", JSONObject.toJSONString(adsBean));
		mHandler = new Handler();

		if (adsBean != null && adsBean.getIsDownload() && inPeriod(adsBean) && isAdFileExist(adsBean)) {
			showAds(adsBean);
			mHandler.postDelayed(delayJumpActivity, AD_DELAY_MILLIS);
		} else {
			showCover();
			mHandler.postDelayed(delayJumpActivity, SPLASH_DELAY_MILLIS);
		}

		if (adsBean != null && !isAdFileExist(adsBean)) {
			liteOrm.delete(adsBean);
		}

	}

	private boolean inPeriod(AdsBean adsBean) {
		if (adsBean == null)
			return false;
		long start = adsBean.getStartTime();
		long end = adsBean.getEndTime();
		long now = System.currentTimeMillis();
		boolean b = start < now && now < end;
		return b;
	}

	private boolean isAdFileExist(AdsBean adsBean) {
		if (adsBean == null)
			return false;
		if (adsBean.getImgRes() != null) {
			File dest = new File(adsBean.getImgRes());
			return dest.exists();
		} else {
			return false;
		}
	}

	private void showCover() {
		imgAd.setEnabled(false);
		imgAd.setVisibility(View.GONE);

		cover.setVisibility(View.VISIBLE);
		cover.bringToFront();
	}

	private void showAds(final AdsBean adsBean) {
		imgAd.setEnabled(true);
		imgAd.setVisibility(View.VISIBLE);

		cover.setVisibility(View.GONE);
		imgAd.bringToFront();

		Picasso.with(this).load(new File(adsBean.getImgRes())).fit().into(imgAd);
		imgAd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (adsBean.getJumpLink().equals("null") || StringUtil.isEmpty(adsBean.getJumpLink()))
					return;
				mHandler.removeCallbacks(delayJumpActivity);
				isAdJumped = true;
				Intent intent = new Intent(SplashActivity.this, WebActivity.class);
				intent.putExtra(WebActivity.KEY_DATA, JSON.toJSONString(adsBean));
				startActivity(intent);
			}
		});
	}

	private void queryAdsToShowNextTime() {
		// mytest http://172.16.7.117:3000/Ads
		// String url = "http://192.168.1.16:3000/v3/ad/mobile/appboot";

		String url = IUrlManager.Ads.DOMAIN + IUrlManager.Ads.FUNCTION;

		mHttpUtil.getWithoutParams(this, url, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				String ret = new String(arg2);
				if (StringUtil.isEmpty(ret))
					return;
				AdsBean bean = JSON.parseObject(ret, AdsBean.class);
				if (StringUtil.isEmpty(bean.getAid()) || StringUtil.isEmpty(bean.getImageUrl()))
					return;

				if (liteOrm.queryById(bean.getAid(), AdsBean.class) == null) {
					liteOrm.save(bean);

					getPreferences(MODE_PRIVATE).edit().putString(IKeyManager.Ads.KEY_ADID, bean.getAid()).commit();
					mHttpUtil.get(bean.getImageUrl(),
							new AdsImageHunterModel(getAdStorageDir(), liteOrm, bean.getAid()));
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				// Log.e("lmsg", "queryads failure:" + arg0);
			}
		});

	}

	private boolean isTimeOut = false;

	private int i = 1;

	private void initView() {

		imgAd = (ImageView) findViewById(R.id.splash_img_ad);

		cover = findViewById(R.id.splash_cover);

		/**
		 * 登录校验初始化
		 */
		mHttpUtil = new HttpUtil();
		sharedPreferences = getSharedPreferences(IKeyManager.Token.SP_TOKEN, Context.MODE_PRIVATE);

		username = sharedPreferences.getString(IKeyManager.Token.KEY_USERNAME, "");
		access_id = sharedPreferences.getString(IKeyManager.Token.KEY_ACCESS_ID, "");
		access_token = sharedPreferences.getString(IKeyManager.Token.KEY_ACCESS_TOKEN, "");

		urlString = IUrlManager.CheckToken.DOMAIN + IUrlManager.CheckToken.FUNCTION + IUrlManager.CheckToken.USERNAME
				+ username + IUrlManager.CheckToken.ACCESS_ID + access_id + IUrlManager.CheckToken.ACCESS_TOKEN
				+ access_token;

		/**
		 * 判断是否是第一次登录初始化
		 */
		SharedPreferences preferences = getSharedPreferences(SHAREDPREFERENCES_NAME, MODE_PRIVATE);
		SSLSocketFactory.getSocketFactory().setHostnameVerifier(new AllowAllHostnameVerifier());

		isFirstIn = preferences.getBoolean(KEY_FIRSTIN, true);

	}

	Runnable delayJumpActivity = new Runnable() {

		@Override
		public void run() {
			jump();
		}
	};

	/**
	 * isCheckSuccess:代表token是否失效
	 */
	private boolean isCheckSuccess = false;

	/**
	 * @Title: jump
	 * @Description: splash计时内跳转逻辑
	 * @author: leobert.lan
	 */
	protected void jump() {
		Intent intent = new Intent();
		if (isFirstIn) {
			intent.setClass(this, GuideActivity.class);
			startActivity(intent);
			SplashActivity.this.finishWithoutOverrideAnim();
		} else {
			if (!onCheckStatus) {
				if (isCheckSuccess) {
					intent.setClass(this, MainActivity.class);
				} else {
					intent.setClass(this, LoginActivity.class);
					// intent.setClass(this, NewLoginActivity.class);
				}
				startActivity(intent);
				SplashActivity.this.finishWithoutOverrideAnim();
			} else {
				isTimeout = true;
			}
		}
	}

	private void initRequest() {

		// test http://54.175.222.246/delay/4
		// Log.e("lmsg", "check loginstatus:\r\n" + urlString);

		mHttpUtil.getWithTag(CHECKTAG, urlString, new AsyncHttpResponseHandler() {

			@Override
			public void onCancel() {
				// Log.e("lmsg", "on req cancel");
				onCheckStatus = false;
				isCheckSuccess = false;
				super.onCancel();
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				if (isTimeout) {
					Intent intent = new Intent(SplashActivity.this, MainActivity.class);
					startActivity(intent);
					SplashActivity.this.finishWithoutOverrideAnim();
				}

				SPUtil.modifyString(sharedPreferences, IKeyManager.Token.KEY_ACCESS_ID, access_id);
				SPUtil.modifyString(sharedPreferences, IKeyManager.Token.KEY_ACCESS_TOKEN, access_token);
				SPUtil.modifyString(sharedPreferences, IKeyManager.Token.KEY_USERNAME, username);
				// 友盟统计登录
				MobclickAgent.onProfileSignIn(username);
				onCheckStatus = false;
				isCheckSuccess = true;
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				if (isTimeout) {
					Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
					startActivity(intent);
					SplashActivity.this.finishWithoutOverrideAnim();
				}
				onCheckStatus = false;
				isCheckSuccess = false;
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isAdJumped) {
			isTimeout = true;
			if (onCheckStatus) {
				mHttpUtil.getClient().cancelRequestsByTAG(CHECKTAG, true);
				// Log.e("lmsg", "cancel request");
			}
			jump();
		}
	}

	@Override
	protected String getPageName() {
		return SplashActivity.mPageName;
	}

	@Override
	protected UMengActivity getActivity() {
		return SplashActivity.this;
	}

	@Override
	public void onBackPressed() {
		mHttpUtil.getClient().cancelRequestsByTAG(CHECKTAG, true);
		mHandler.removeCallbacks(delayJumpActivity);
		super.onBackPressed();
	}

}
