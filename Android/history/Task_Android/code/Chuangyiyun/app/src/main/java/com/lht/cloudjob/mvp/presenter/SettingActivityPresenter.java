package com.lht.cloudjob.mvp.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.MainApplication;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.BaseActivity;
import com.lht.cloudjob.activity.asyncprotected.HomeActivity;
import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.interfaces.keys.SPConstants;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.CheckVersionUpdateModel;
import com.lht.cloudjob.mvp.model.DownloadModel;
import com.lht.cloudjob.mvp.model.LogoutModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.VersionResBean;
import com.lht.cloudjob.mvp.model.interfaces.IFileDownloadCallbacks;
import com.lht.cloudjob.mvp.model.pojo.DownloadEntity;
import com.lht.cloudjob.mvp.model.pojo.LoginInfo;
import com.lht.cloudjob.mvp.viewinterface.ISettingActivity;
import com.lht.cloudjob.util.CleanUtil;
import com.lht.cloudjob.util.SPUtil;
import com.lht.cloudjob.util.VersionUtil;
import com.lht.cloudjob.util.VsoUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> SettingActivityPresenter
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/5/11.
 */
public class SettingActivityPresenter {

    private final ISettingActivity iSettingActivity;

    private CleanUtil cleanUtil;
    private File downloadDir;

    public SettingActivityPresenter(final ISettingActivity iSettingActivity) {
        this.iSettingActivity = iSettingActivity;

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

            @Override
            public File getCacheFileDir() {

                return iSettingActivity.getActivity().getCacheDir();
            }

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

    private CustomPopupWindow.OnPositiveClickListener pLogoutListener = new CustomPopupWindow
            .OnPositiveClickListener() {
        @Override
        public void onPositiveClick() {

            SharedPreferences sp = iSettingActivity.getActivity()
                    .getSharedPreferences(SPConstants.Token.SP_TOKEN, Context.MODE_PRIVATE);
            SPUtil.modifyString(sp, SPConstants.Token.KEY_ACCESS_ID, "");
            SPUtil.modifyString(sp, SPConstants.Token.KEY_ACCESS_TOKEN, "");
            SPUtil.modifyString(sp, SPConstants.Token.KEY_USERNAME, "");

            LoginInfo info = IVerifyHolder.mLoginInfo;
            String sess = VsoUtil.createVsoSessionCode(info.getUsername(), info.getAccessId());
            IApiRequestModel model = new LogoutModel(info.getAccessId(), sess,
                    info.getAccessToken(), new LogoutModelCallback());
            model.doRequest(iSettingActivity.getActivity());

            Intent intent = new Intent(iSettingActivity.getActivity(), HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
        CheckVersionUpdateModel model = new CheckVersionUpdateModel(VersionUtil.getVersion(iSettingActivity.getActivity()), new CheckUpdataCallback());
        model.doRequest(iSettingActivity.getActivity());
    }

    class CheckUpdataCallback implements ApiModelCallback<VersionResBean> {
        @Override
        public void onSuccess(BaseBeanContainer<VersionResBean> beanContainer) {
            iSettingActivity.cancelWaitView();
            final VersionResBean versionResBean = beanContainer.getData();
            final VersionResBean.VersionInfoData data = versionResBean.getData();

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

    private void handleVersionUpdate(VersionResBean.VersionInfoData data) {
        DownloadEntity entity = DownloadEntity.copyFromVersionResBean(data);
        downloadDir = BaseActivity.getPublicDownloadDir();
        downloadModel = new DownloadModel(entity, downloadDir, new ApkDownloadCallback());
        downloadModel.doRequest(iSettingActivity.getActivity());
    }

    class ApkDownloadCallback implements IFileDownloadCallbacks {

        @Override
        public void onNoInternet() {
            iSettingActivity.showMsg(iSettingActivity.getActivity().getString(R.string.v1010_toast_net_exception));
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
                    Intent intent = new Intent();
                    // 执行动作
                    intent.setAction(Intent.ACTION_VIEW);
                    // 执行的数据类型
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    iSettingActivity.getActivity().startActivity(intent);
                    MainApplication.getOurInstance().finishAll();
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
