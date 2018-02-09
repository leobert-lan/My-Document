package com.lht.pan_android.activity.ViewPagerItem;

import java.io.File;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.MainActivityIPreventPenetrate;
import com.lht.pan_android.Interface.UserInfoCallBack;
import com.lht.pan_android.activity.BaseActivity;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.activity.asyncProtected.LoginActivity;
import com.lht.pan_android.activity.asyncProtected.MainActivity;
import com.lht.pan_android.activity.others.FeedbackActivity;
import com.lht.pan_android.activity.others.MineAboutUsActivity;
import com.lht.pan_android.clazz.Events.SetTransOnlyOnWifi;
import com.lht.pan_android.clazz.Events.TransSettingChanged;
import com.lht.pan_android.util.CloudBoxApplication;
import com.lht.pan_android.util.MineInfoUtil;
import com.lht.pan_android.util.SPUtil;
import com.lht.pan_android.util.UpdateUtil;
import com.lht.pan_android.util.VersionUtil;
import com.lht.pan_android.util.activity.CleanUtil;
import com.lht.pan_android.util.activity.CleanUtil.ICleanView;
import com.lht.pan_android.util.activity.LogoutUtil;
import com.lht.pan_android.util.file.FileUtil;
import com.lht.pan_android.view.popupwins.CustomDialog;
import com.lht.pan_android.view.popupwins.CustomPopupWindow.OnPositiveClickListener;
import com.lht.pan_android.view.popupwins.TPSPWCreater;
import com.lht.pan_android.view.popupwins.ThirdPartyShareItemClickListenerImpl;
import com.lht.pan_android.view.popupwins.ThirdPartySharePopWins;
import com.loopj.android.http.AsyncHttpClient;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SettingActivity extends ViewPagerItemActivity
		implements OnClickListener, IKeyManager.SettingKey, ICleanView {

	private final static String mPageName = "mineActivity";

	private LinearLayout linearActivityabout;
	private LinearLayout linearActivityadvice;
	private LinearLayout linearCheckUpdate;
	private LinearLayout btnShareApp;

	private ToggleButton togBtnOnlyWIFI;
	private ToggleButton togBtnBackup;
	private ToggleButton togBtnSync;

	private ProgressBar progressBar;
	private Button btnActivityExit;
	private ImageView imgBtnHead;
	private TextView txtUserName;
	private TextView txtUsed;
	private TextView txtAll;

	private MineInfoUtil mineInfoUtil;
	private LogoutUtil mLogoutUtil;

	private TextView txtCversion;

	private SharedPreferences userSetSp;

	private SharedPreferences tokenSp;
	private Context mContext;

	private UpdateUtil mUpdateUtil;

	private LinearLayout btnCleanCache;

	private CleanUtil cleanUtil;

	private TextView txtCacheSize;

	private CustomDialog cacheCleanAlert = null;

	private MainActivity mParent;

	private void askCleanCache() {
		cacheCleanAlert.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CloudBoxApplication.addActivity(this);
		setContentView(R.layout.activity_setting);
		mContext = this;

		EventBus.getDefault().register(this);

		mUpdateUtil = new UpdateUtil(getParent());
		mParent = (MainActivity) getParent();
		cleanUtil = new CleanUtil(this);

		reportCountEvent(COUNT_CB_MINE);

		if (tokenSp == null) {
			tokenSp = getSharedPreferences(IKeyManager.Token.SP_TOKEN, Context.MODE_PRIVATE);
		}

		userSetSp = getSharedPreferences("Setting_" + tokenSp.getString(IKeyManager.Token.KEY_USERNAME, ""),
				MODE_PRIVATE);

		initView();
		initEvent();

		mLogoutUtil = new LogoutUtil(this);
		mineInfoUtil = new MineInfoUtil(this);
		mineInfoUtil.setCallBack(userInfoCallBack);
		mineInfoUtil.getUserInfo();

	}

	@Override
	public void onResume() {
		super.onResume();
		mineInfoUtil.setCallBack(userInfoCallBack);
		mineInfoUtil.getUserInfo();
		cleanUtil.calcSize();
		if (!hasAutoSetted) {
			makeSettingEffect(togBtnOnlyWIFI.isChecked());
			hasAutoSetted = true;
		}
	}

	/**
	 * @Title: Pause
	 * @Description: 手动调用生命周期-pause
	 * @author: leobert.lan
	 */
	@Override
	public void onPause() {
		super.onPause();
	}

	private void initView() {
		progressBar = (ProgressBar) findViewById(R.id.linear_progressbar);
		imgBtnHead = (ImageView) findViewById(R.id.linear_image_heading);
		txtUserName = (TextView) findViewById(R.id.linear_txt_username);
		txtUsed = (TextView) findViewById(R.id.linear_txt_used);
		txtAll = (TextView) findViewById(R.id.linear_txt_all);
		togBtnSync = (ToggleButton) findViewById(R.id.linear_setting_sync);
		togBtnBackup = (ToggleButton) findViewById(R.id.linear_setting_backup);
		togBtnOnlyWIFI = (ToggleButton) findViewById(R.id.setting_toggle_onlyWIFI);
		btnActivityExit = (Button) findViewById(R.id.linear_activity_exit);

		btnCleanCache = (LinearLayout) findViewById(R.id.clean_ll_ccache);
		txtCacheSize = (TextView) findViewById(R.id.clean_tv_cachesize);

		linearActivityabout = (LinearLayout) findViewById(R.id.setting_ll_about);
		linearActivityadvice = (LinearLayout) findViewById(R.id.setting_ll_feedback);
		linearCheckUpdate = (LinearLayout) findViewById(R.id.setting_ll_version);
		txtCversion = (TextView) findViewById(R.id.setting_tv_cversion);

		txtCversion.setText("v" + VersionUtil.getVersion(mContext));

		btnShareApp = (LinearLayout) findViewById(R.id.setting_ll_share);

		cacheCleanAlert = new CustomDialog(mParent, mParent);
		cacheCleanAlert.setContent("是否清空缓存？");
		cacheCleanAlert.setPositiveButton("确定");
		cacheCleanAlert.setNegativeButton("取消");
		cacheCleanAlert.setPositiveClickListener(new OnPositiveClickListener() {

			@Override
			public void onPositiveClick() {
				cleanUtil.cleanCache();
				reportCountEvent(CALC_CB_CLEAN_CACHE);
			}
		});

	}

	private void initEvent() {
		btnActivityExit.setOnClickListener(this);
		togBtnSync.setOnClickListener(this);
		togBtnBackup.setOnClickListener(this);
		btnActivityExit.setOnClickListener(this);
		btnCleanCache.setOnClickListener(this);
		btnShareApp.setOnClickListener(this);
		linearCheckUpdate.setOnClickListener(this);
		linearActivityabout.setOnClickListener(this);
		linearActivityadvice.setOnClickListener(this);

		togBtnOnlyWIFI.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SPUtil.modifyBoolean(userSetSp, SETTING_KEY_ONLYTRANSONWIFI, isChecked);
				makeSettingEffect(isChecked);
			}
		});

		togBtnOnlyWIFI.setChecked(userSetSp.getBoolean(SETTING_KEY_ONLYTRANSONWIFI, true));
		makeSettingEffect(togBtnOnlyWIFI.isChecked());
	}

	private boolean hasAutoSetted = false;

	/**
	 * 使WiFi设置生效
	 */
	protected void makeSettingEffect(boolean isChecked) {
		TransSettingChanged event = new TransSettingChanged();
		event.isOnlyOnWifi = isChecked;
		EventBus.getDefault().post(event);
		BaseActivity.setOnlyOnWifi(isChecked);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.linear_setting_sync:
			if (togBtnSync.isChecked()) {
				Toast.makeText(this, "功能尚未开放", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.linear_setting_backup:
			if (togBtnBackup.isChecked()) {
				Toast.makeText(this, "功能尚未开放", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.linear_activity_exit:
			reportCountEvent(COUNT_CB_QUIT);

			CustomDialog customDialog = getLoginoutDialog(R.string.string_exit, R.string.string_cancel,
					R.string.string_sure);

			customDialog.setPositiveClickListener(
					new com.lht.pan_android.view.popupwins.CustomPopupWindow.OnPositiveClickListener() {

						@Override
						public void onPositiveClick() {
							mLogoutUtil.logout();
							// TODO
							SPUtil.modifyString(tokenSp, IKeyManager.Token.KEY_ACCESS_TOKEN, "");

							CloudBoxApplication.finishAll();
							startActivity(new Intent(SettingActivity.this, LoginActivity.class));
						}
					});
			customDialog.show(btnActivityExit);
			break;
		case R.id.setting_ll_about:
			startActivity(new Intent(SettingActivity.this, MineAboutUsActivity.class));
			break;
		case R.id.clean_ll_ccache:
			askCleanCache();
			break;
		case R.id.setting_ll_feedback:
			startActivity(new Intent(SettingActivity.this, FeedbackActivity.class));
			break;
		case R.id.setting_ll_version:
			mUpdateUtil.checkUpdate(true);
			break;
		case R.id.setting_ll_share:
			shareApp();
			break;
		default:
			break;
		}

	}

	private void shareApp() {
		ThirdPartySharePopWins wins = TPSPWCreater.create(getParent());
		wins.setShareContent("https://pan.vsochina.com/app");
		wins.removeItem(wins.getItemCount() - 1);
		ThirdPartyShareItemClickListenerImpl l = new ThirdPartyShareItemClickListenerImpl(getActivity());

		l.setTitle(getString(R.string.tp_appshare_title));
		l.setSummary(getString(R.string.tp_appshare_summary));
		wins.setOnThirdPartyShareItemClickListener(l);
		wins.show();
	}

	/**
	 * @Title: getLoginoutDialog
	 * @Description: 自定义dialog
	 * @author: zhangbin
	 * @param contentRid
	 * @param negRid
	 * @param posiRid
	 * @return
	 */
	private CustomDialog getLoginoutDialog(int contentRid, int negRid, int posiRid) {
		CustomDialog dialog = new CustomDialog(getParent(), new MainActivityIPreventPenetrate());
		dialog.setContent(contentRid);
		dialog.setNegativeButton(negRid);
		dialog.setPositiveButton(posiRid);
		return dialog;
	}

	private AsyncHttpClient client = new AsyncHttpClient();

	private void getHeadImg(String headImgUrl) {

		Picasso.with(mContext).load(headImgUrl).placeholder(R.drawable.mortx).error(R.drawable.mortx).fit()
				.into(imgBtnHead);
	}

	private UserInfoCallBack userInfoCallBack = new UserInfoCallBack() {


		@Override
		public void setBasicInfo(String user, String avatar) {
			txtUserName.setText(user);
			getHeadImg(avatar);
		}

		@Override
		public void setCapacityInfo(String used, String total, int percent) {
			txtUsed.setText(used);
			txtAll.setText(total);
			progressBar.setProgress(percent);
		}
	};

	private void Destroy() {
		client.cancelAllRequests(true);
	}

	@Override
	protected void onDestroy() {
		Destroy();
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	protected String getPageName() {
		return SettingActivity.mPageName;
	}

	@Override
	protected UMengActivity getActivity() {
		return SettingActivity.this;
	}

	@Override
	public boolean back() {
		return false;
	}

	@Override
	public void doShowWaitView(boolean isProtected) {
		mParent.showWaitView(isProtected);
	}

	@Override
	public void doCancelWaitView() {
		mParent.cancelWaitView();
	}

	@Override
	public void doShowSize(long cacheSize) {
		txtCacheSize.setText(FileUtil.calcSize(cacheSize));
	}

	@Override
	public File getCacheFileDir() {
		return getCacheDir();
	}

	@Override
	public void doCreateIndividualFolder() {
		mParent.createIndividualFolder();
	}

	@Subscribe
	public void onEventMainThread(SetTransOnlyOnWifi event) {
		togBtnOnlyWIFI.setChecked(true);
	}

}
