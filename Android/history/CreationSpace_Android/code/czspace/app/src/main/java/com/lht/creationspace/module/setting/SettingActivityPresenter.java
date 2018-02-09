package com.lht.creationspace.module.setting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.MainApplication;
import com.lht.creationspace.base.model.IFileDownloadCallbacks;
import com.lht.creationspace.base.model.apimodel.ApiModelCallback;
import com.lht.creationspace.base.model.apimodel.BaseBeanContainer;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.base.model.pojo.DownloadEntity;
import com.lht.creationspace.base.model.pojo.LoginInfo;
import com.lht.creationspace.cfg.SPConstants;
import com.lht.creationspace.customview.popup.CustomPopupWindow;
import com.lht.creationspace.module.home.ui.ac.HomeActivity;
import com.lht.creationspace.module.pub.model.DownloadModel;
import com.lht.creationspace.module.setting.model.CheckVersionUpdateModel;
import com.lht.creationspace.module.setting.ui.ISettingActivity;
import com.lht.creationspace.module.user.login.model.LogoutModel;
import com.lht.creationspace.util.CleanUtil;
import com.lht.creationspace.util.SPUtil;
import com.lht.creationspace.util.VersionUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import static com.lht.creationspace.util.VersionUtil.updateVersion;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.presenter
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> SettingActivityPresenter
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/5/11.
 */
public class SettingActivityPresenter {

    private final ISettingActivity iSettingActivity;

    private CleanUtil cleanUtil;
    private File downloadDir;

    public SettingActivityPresenter( ISettingActivity activity) {
        this.iSettingActivity = activity;

        cleanUtil = new CleanUtil(new CleanUtil.ICleanView() {
            @Override
            public void doShowWaitView(boolean isProtected) {
                iSettingActivity.showWaitView(isProtected);
            }

            @Override
            public void doCancelWaitView() {
                iSettingActivity.cancelWaitView();
            }

            @Override
            public void doShowSize(long cacheSize) {
                iSettingActivity.showCacheSize(cacheSize);
            }

//            @Override
//            public File getCacheFileDir() {
//
//                return iSettingActivity.getActivity().getCacheDir();
//            }

            @Override
            public void doCreateIndividualFolder() {
                iSettingActivity.createIndividualFolder();
            }
        });
    }

    public void callCountCacheSize() {
        cleanUtil.calcSize();
    }

    public void callCleanCache() {
        iSettingActivity.showCacheCleanAlert(pCleanCacheListener);
    }

    public void callFeedback() {
        iSettingActivity.jump2Feedback();
    }

    private CustomPopupWindow.OnPositiveClickListener pLogoutListener = new CustomPopupWindow.OnPositiveClickListener() {
        @Override
        public void onPositiveClick() {

            SharedPreferences sp = iSettingActivity.getActivity()
                    .getSharedPreferences(SPConstants.Token.SP_TOKEN, Context.MODE_PRIVATE);
            SPUtil.modifyString(sp, SPConstants.Token.KEY_ACCESS_ID, "");
            SPUtil.modifyString(sp, SPConstants.Token.KEY_ACCESS_TOKEN, "");
            SPUtil.modifyString(sp, SPConstants.Token.KEY_USERNAME, "");

            IApiRequestModel model = new LogoutModel( new LogoutModelCallback());
            model.doRequest(iSettingActivity.getActivity());

            Intent intent = new Intent(iSettingActivity.getActivity(), HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            IVerifyHolder.mLoginInfo.copy(new LoginInfo());//make sure

            EventBus.getDefault().post(new AppEvent.LogoutEvent());
            MainApplication.getOurInstance().startActivity(intent);
            iSettingActivity.getActivity().finish();
        }
    };

    private CustomPopupWindow.OnPositiveClickListener pCleanCacheListener = new CustomPopupWindow
            .OnPositiveClickListener() {
        @Override
        public void onPositiveClick() {
            cleanUtil.cleanCache();
        }
    };

    public void callLogout() {
        iSettingActivity.showLogoutAlert(pLogoutListener);
    }

    public void callAboutUs() {
        iSettingActivity.jump2AboutUs();
    }

    private class LogoutModelCallback implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            //empty
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            //empty
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            //empty
        }
    }

    /**
     * 检查更新
     */
    public void callCheckUpdate() {
        iSettingActivity.showWaitView(true);
        CheckVersionUpdateModel model =
                new CheckVersionUpdateModel(VersionUtil.getVersion(iSettingActivity.getActivity()),
                        new CheckUpdateCallback());
        model.doRequest(iSettingActivity.getActivity());
    }

    private class CheckUpdateCallback implements ApiModelCallback<CheckVersionUpdateModel.VersionResBean> {
        @Override
        public void onSuccess(BaseBeanContainer<CheckVersionUpdateModel.VersionResBean> beanContainer) {
            iSettingActivity.cancelWaitView();
            final CheckVersionUpdateModel.VersionResBean versionResBean = beanContainer.getData();
            final CheckVersionUpdateModel.VersionResBean.VersionInfoData data = versionResBean.getData();

            if (data == null) {

                return;
            }
            iSettingActivity.showNewVersionDialog(versionResBean.getVer(), new CustomPopupWindow.OnPositiveClickListener() {
                @Override
                public void onPositiveClick() {
                    handleVersionUpdate(data);
                }
            });

        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iSettingActivity.cancelWaitView();
            iSettingActivity.showMsg(iSettingActivity.getActivity().getString(R.string.v1020_versionupdate_text_already_newversion));
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iSettingActivity.cancelWaitView();
        }
    }

    /**
     * 处理版本更新--下载新版本
     *
     * @param data
     */
    private DownloadModel downloadModel;

    private void handleVersionUpdate(CheckVersionUpdateModel.VersionResBean.VersionInfoData data) {
        DownloadEntity entity = DownloadEntity.copyFromVersionResBean(data);
        downloadDir = iSettingActivity.getActivity().getSystemDownloadDir();
        downloadModel = new DownloadModel(entity, downloadDir, new ApkDownloadCallback());
        downloadModel.doRequest(iSettingActivity.getActivity());
    }

    private class ApkDownloadCallback implements IFileDownloadCallbacks {

        @Override
        public void onNoInternet() {
            iSettingActivity.showMsg(iSettingActivity.getActivity().getString(R.string.v1000_toast_net_exception));
        }

        @Override
        public void onMobileNet() {
            iSettingActivity.showOnMobileNet(new CustomPopupWindow.OnPositiveClickListener() {
                @Override
                public void onPositiveClick() {
                    downloadModel.forceStart(iSettingActivity.getActivity());
                }
            });
        }

        @Override
        public void onFileNotFoundOnServer() {
            iSettingActivity.showMsg(iSettingActivity.getActivity().getString(R.string.v1020_toast_download_onnotfound));
            iSettingActivity.hideDownloadProgress();
        }

        @Override
        public void onDownloadStart(DownloadEntity entity) {
            //显示进度条
            iSettingActivity.showDownloadProgress(entity);
        }

        @Override
        public void onDownloadCancel() {
            //进度条消失
            downloadModel.cancelDownload();
            iSettingActivity.hideDownloadProgress();
            iSettingActivity.showMsg(iSettingActivity.getActivity().getString(R.string.v1020_toast_download_cancel));
        }

        @Override
        public void onDownloadSuccess(DownloadEntity entity, final File file) {
            //下载成功、执行安装
            iSettingActivity.hideDownloadProgress();
            iSettingActivity.showInstallDialog(new CustomPopupWindow.OnPositiveClickListener() {
                @Override
                public void onPositiveClick() {
                    updateVersion(iSettingActivity.getActivity(),file);
                }
            });

        }

        @Override
        public void onDownloading(DownloadEntity entity, long current, long total) {
            //更新进度条
            iSettingActivity.updateProgress(entity, current, total);
        }

        @Override
        public void onNoEnoughSpace() {
            iSettingActivity.showMsg(iSettingActivity.getActivity().getString(R.string.v1020_toast_download_onnoenoughspace));
        }

        @Override
        public void downloadFailure() {
            iSettingActivity.showMsg(iSettingActivity.getActivity().getString(R.string.v1020_versionupdate_text_download_fuilure));
            iSettingActivity.hideDownloadProgress();
        }
    }
}
