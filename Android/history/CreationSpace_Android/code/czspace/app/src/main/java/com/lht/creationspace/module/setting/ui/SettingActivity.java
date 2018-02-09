package com.lht.creationspace.module.setting.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lht.creationspace.R;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.module.pub.ui.AboutUsActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.customview.popup.dialog.CustomDialog;
import com.lht.creationspace.customview.popup.CustomPopupWindow;
import com.lht.creationspace.customview.popup.CustomProgressView;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme1;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.base.model.pojo.DownloadEntity;
import com.lht.creationspace.module.setting.SettingActivityPresenter;
import com.lht.creationspace.util.file.FileUtils;
import com.lht.creationspace.util.string.StringUtil;

public class SettingActivity extends AsyncProtectedActivity implements ISettingActivity, View.OnClickListener {

    private static final String PAGENAME = "SettingActivity";

    private ToolbarTheme1 simpleBackTitleBar;

    private LinearLayout mCleanCache, mFeedback, mCheckUpdate, mAboutUs;

    private Button btnLogout;

    private SettingActivityPresenter mPresenter;

    private ProgressBar pb;

    private TextView txtCacheSize;

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
    public BaseActivity getActivity() {
        return SettingActivity.this;
    }

    @Override
    protected void initView() {
        mCleanCache = (LinearLayout) findViewById(R.id.setting_ll_cleancache);
        mFeedback = (LinearLayout) findViewById(R.id.setting_ll_feedback);
        mCheckUpdate = (LinearLayout) findViewById(R.id.setting_ll_checkupdate);
        mAboutUs = (LinearLayout) findViewById(R.id.setting_ll_aboutus);

        btnLogout = (Button) findViewById(R.id.setting_btn_logout);
        simpleBackTitleBar = (ToolbarTheme1) findViewById(R.id.titlebar);
        //TODO
        simpleBackTitleBar.setTitle(R.string.title_activity_settings);
//        simpleBackTitleBar.setTitleTextColor(R.color.text_white);

        pb = (ProgressBar) findViewById(R.id.progressbar);

        txtCacheSize = (TextView) findViewById(R.id.setting_txt_cachesize);
    }

    @Override
    protected void initVariable() {
        mPresenter = new SettingActivityPresenter(this);
//        String data = getIntent().getStringExtra(KEY_DATA);
//        mVerifyInfo = JSON.parseObject(data, LoginInfo.class);
    }

    @Override
    protected void initEvent() {
        simpleBackTitleBar.setTitle(getString(R.string.v1000_title_activity_settings));
        simpleBackTitleBar.setDefaultOnBackListener(this);

        mCleanCache.setOnClickListener(this);
        mCheckUpdate.setOnClickListener(this);
        mFeedback.setOnClickListener(this);
        mAboutUs.setOnClickListener(this);

        btnLogout.setOnClickListener(this);

        if (StringUtil.isEmpty(IVerifyHolder.mLoginInfo.getUsername())) {
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
        FeedbackActivity.getLauncher(this).launch();
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
    public void createIndividualFolder() {
        getMainApplication().reloadCache();
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
                mPresenter.callAboutUs();
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
//        Log.e("lmsg", "版本号：" + versionCode);
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
    public void jump2AboutUs() {
        AboutUsActivity.getLauncher(this).launch();
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


    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
    }

    public static final class Launcher extends AbsActivityLauncher<Void> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, SettingActivity.class);
        }

        @Override
        public AbsActivityLauncher<Void> injectData(Void data) {
            return Launcher.this;
        }
    }

}
