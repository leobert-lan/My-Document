package com.lht.cloudjob.activity.asyncprotected;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.activity.others.AboutUsActivity;
import com.lht.cloudjob.customview.CustomDialog;
import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.cloudjob.customview.CustomProgressView;
import com.lht.cloudjob.customview.TitleBar;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.pojo.DownloadEntity;
import com.lht.cloudjob.mvp.model.pojo.LoginInfo;
import com.lht.cloudjob.mvp.presenter.SettingActivityPresenter;
import com.lht.cloudjob.mvp.viewinterface.ISettingActivity;
import com.lht.cloudjob.util.file.FileUtils;
import com.lht.cloudjob.util.string.StringUtil;

public class SettingActivity extends AsyncProtectedActivity implements ISettingActivity, View.OnClickListener {

    private static final String PAGENAME = "SettingActivity";

    public static final String KEY_DATA = "data_userinfo";

    private TitleBar simpleBackTitleBar;

    private LinearLayout mCleanCache, mFeedback, mCheckUpdate, mAboutUs;

    private Button btnLogout;

    private SettingActivityPresenter mPresenter;

    private ProgressBar pb;

    private TextView txtCacheSize;

    private LoginInfo mVerifyInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected IApiRequestPresenter getApiRequestPresenter() {
        //不牵涉网络请求
        return null;
    }

    @Override
    protected String getPageName() {
        return SettingActivity.PAGENAME;
    }

    @Override
    public UMengActivity getActivity() {
        return SettingActivity.this;
    }

    @Override
    protected void initView() {
        mCleanCache = (LinearLayout) findViewById(R.id.setting_ll_cleancache);
        mFeedback = (LinearLayout) findViewById(R.id.setting_ll_feedback);
        mCheckUpdate = (LinearLayout) findViewById(R.id.setting_ll_checkupdate);
        mAboutUs = (LinearLayout) findViewById(R.id.setting_ll_aboutus);

        btnLogout = (Button) findViewById(R.id.setting_btn_logout);
        simpleBackTitleBar = (TitleBar) findViewById(R.id.titlebar);
        //TODO
        simpleBackTitleBar.setTitle(R.string.title_activity_settings);
//        simpleBackTitleBar.setTitleTextColor(R.color.text_white);

        pb = (ProgressBar) findViewById(R.id.progressbar);

        txtCacheSize = (TextView) findViewById(R.id.setting_txt_cachesize);
    }

    @Override
    protected void initVariable() {
        mPresenter = new SettingActivityPresenter(this);
        String data = getIntent().getStringExtra(KEY_DATA);
        mVerifyInfo = JSON.parseObject(data, LoginInfo.class);
    }

    @Override
    protected void initEvent() {
        simpleBackTitleBar.setDefaultOnBackListener(this);

        mCleanCache.setOnClickListener(this);
        mCheckUpdate.setOnClickListener(this);
        mFeedback.setOnClickListener(this);
        mAboutUs.setOnClickListener(this);

        btnLogout.setOnClickListener(this);

        if (StringUtil.isEmpty(mVerifyInfo.getUsername())) {
            // hide feedback and logout,because never login
            btnLogout.setVisibility(View.GONE);
            mFeedback.setVisibility(View.GONE);
            findViewById(R.id.line2).setVisibility(View.GONE);
            findViewById(R.id.line4).setVisibility(View.GONE);
        }
    }

    private boolean onUpgrading = false;

    @Override
    public void onBackPressed() {
        if (onUpgrading) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public ProgressBar getProgressBar() {
        return pb;
    }

    @Override
    public void jump2Feedback() {
        start(FeedbackActivity.class);
    }

    @Override
    public void showCacheSize(long bytes) {
        txtCacheSize.setText(FileUtils.calcSize(bytes));
    }

    @Override
    public void showCacheCleanAlert(CustomPopupWindow.OnPositiveClickListener positiveClickListener) {
        CustomDialog dialog = new CustomDialog(this);

        dialog.setContent(R.string.v1010_dialog_setting_content_clean);
        dialog.setNegativeButton(R.string.cancel);
        dialog.setPositiveButton(R.string.permit);

        dialog.setPositiveClickListener(positiveClickListener);
        dialog.show();
    }

    @Override
    public void showLogoutAlert(CustomPopupWindow.OnPositiveClickListener positiveClickListener) {
        CustomDialog dialog = new CustomDialog(this);

        dialog.setContent(R.string.v1010_dialog_setting_content_logout);
        dialog.setNegativeButton(R.string.cancel);
        dialog.setPositiveButton(R.string.permit);

        dialog.setPositiveClickListener(positiveClickListener);

        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_btn_logout:
                mPresenter.callLogout();
                break;
            case R.id.setting_ll_checkupdate:
                mPresenter.callCheckUpdate();
                break;
            case R.id.setting_ll_cleancache:
                mPresenter.callCleanCache();
                break;
            case R.id.setting_ll_feedback:
                mPresenter.callFeedback();
                break;
            case R.id.setting_ll_aboutus:
                start(AboutUsActivity.class);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.callCountCacheSize();
    }


    @Override
    public void showNewVersionDialog(String versionCode, CustomPopupWindow.OnPositiveClickListener onPositiveClickListener) {
        CustomDialog dialog = new CustomDialog(getActivity());
        dialog.setContent(getString(R.string.v1020_versionupdate_dialog_text_newversion) + versionCode + "\r\n" + getString(R.string.v1020_versionupdate_dialog_text_update));
        dialog.setNegativeButton(R.string.v1020_versionupdate_dialog_btn_nodownload);
        dialog.setPositiveButton(R.string.v1020_versionupdate_dialog_btn_download);
        dialog.setPositiveClickListener(onPositiveClickListener);
        dialog.show();
    }

    @Override
    public void showInstallDialog(CustomPopupWindow.OnPositiveClickListener onPositiveClickListener) {
        CustomDialog dialog = new CustomDialog(getActivity());
        dialog.setContent(R.string.v1020_versionupdate_dialog_text_install);
        dialog.setNegativeButton(R.string.v1020_versionupdate_dialog_btn_cancel);
        dialog.setPositiveButton(R.string.v1020_versionupdate_dialog_btn_install);
        dialog.setPositiveClickListener(onPositiveClickListener);
        dialog.show();
    }

    @Override
    public void showOnMobileNet(CustomPopupWindow.OnPositiveClickListener onPositiveClickListener) {
        CustomDialog dialog = new CustomDialog(getActivity());
        dialog.setContent(R.string.v1020_dialog_download_onmobile);
        dialog.setNegativeButton(R.string.v1020_dialog_nbtn_onmobile);
        dialog.setPositiveButton(R.string.v1020_dialog_pbtn_onmobile);
        dialog.setPositiveClickListener(onPositiveClickListener);
        dialog.show();
    }

    private CustomProgressView progress;

    @Override
    public void showDownloadProgress(DownloadEntity entity) {
        progress = new CustomProgressView(getActivity());
        onUpgrading = true;
        progress.show();
        progress.setProgress(0, 100);
    }

    @Override
    public void hideDownloadProgress() {
        onUpgrading = false;
        if (progress == null) {
            return;
        }
        progress.dismiss();
        progress = null;
    }

    @Override
    public void updateProgress(DownloadEntity entity, long current, long total) {
        if (progress == null) {
            return;
        }
        onUpgrading = true;
        if (!progress.isShowing()) {
            progress.show();
        }
        progress.setProgress(current, total);
    }
}
