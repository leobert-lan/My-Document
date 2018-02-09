package com.lht.cloudjob.mvp.presenter;

import android.content.Intent;

import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.BaseActivity;
import com.lht.cloudjob.clazz.ImagePreviewIntentFactory;
import com.lht.cloudjob.clazz.LoginIntentFactory;
import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.cloudjob.interfaces.ITriggerCompare;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.FilePreviewModel;
import com.lht.cloudjob.mvp.model.FollowModel;
import com.lht.cloudjob.mvp.model.UnfollowModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.DemandInfoResBean;
import com.lht.cloudjob.mvp.model.interfaces.IFilePreviewCallbacks;
import com.lht.cloudjob.mvp.model.pojo.PreviewFileEntity;
import com.lht.cloudjob.mvp.model.pojo.PreviewImage;
import com.lht.cloudjob.mvp.viewinterface.IDemandRequireFragment;
import com.lht.cloudjob.util.file.PreviewUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> DemandRequireFragmentPresenter
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/9/5
 */
public class DemandRequireFragmentPresenter extends ABSVerifyNeedPresenter {
    private IDemandRequireFragment iDemandRequireFragment;

    public DemandRequireFragmentPresenter(IDemandRequireFragment iDemandRequireFragment) {
        this.iDemandRequireFragment = iDemandRequireFragment;
    }


    /**
     * desc: 页面接收到订阅事件后，调用presenter#identifyTrigger，执行逻辑，需要区分触发事件是不是登录事件
     *
     * @param trigger an  interface to identify trigger,use equal(ITriggerCompare compare)
     */
    @Override
    public void identifyTrigger(ITriggerCompare trigger) {
        if (trigger.equals(LoginTrigger.FollowPublisher)) {
            if (!IVerifyHolder.mLoginInfo.getUsername().equals(publisher)) {
                //不是自身的情况下进行关注
                callFollowPublisher(IVerifyHolder.mLoginInfo.getUsername(), publisher);
            }
        }

    }

    @Override
    public void identifyCanceledTrigger(ITriggerCompare trigger) {
        super.identifyCanceledTrigger(trigger);
        if (trigger.equals(LoginTrigger.FollowPublisher)) {
            iDemandRequireFragment.setPublisherFollowed(false);
        }
    }

    private boolean isLogin;

    /**
     * desc: check if login
     *
     * @return true while login,false otherwise
     */
    @Override
    protected boolean isLogin() {
        return isLogin;
    }

    /**
     * desc: update status,implement the method with an appropriate design
     *
     * @param isLogin
     */
    @Override
    public void setLoginStatus(boolean isLogin) {
        this.isLogin = isLogin;
    }

    public void callUnFollowPublisher(String username, String publisher) {
        iDemandRequireFragment.showWaitView(true);
        IApiRequestModel model = new UnfollowModel(username, publisher, new UnFollowModelCallback());
        model.doRequest(iDemandRequireFragment.getActivity());
    }

    /**
     * 图片预览
     * @param images 预览的图片
     * @param index position
     */
    public void callPreviewImage(ArrayList<DemandInfoResBean.AttachmentExt> images, int index) {
        ArrayList<PreviewImage> previewImages = PreviewImage.copyFromAttachmentExtArrayList(images);
        Intent intent = ImagePreviewIntentFactory.newImagePreviewIntent(
                iDemandRequireFragment.getActivity(), previewImages, index);
        iDemandRequireFragment.getActivity().startActivity(intent);
    }
    private FilePreviewModel model;
    /**
     * 文件预览
     * @param demandId
     * @param attachmentExt 预览的文件
     */
    public void callPreviewFile(String demandId, DemandInfoResBean.AttachmentExt attachmentExt) {
        if (!PreviewUtils.isSupportByVsoPreviewRules(attachmentExt.getMime())) {
            iDemandRequireFragment.showMsg(iDemandRequireFragment
                    .getAppResource().getString(R.string.v1020_toast_preview_unsupportbyus));
            return;
        }
        PreviewFileEntity entity = PreviewFileEntity.copyFromAttachmentExt(attachmentExt);
        entity.setTaskBn(demandId);
        model = new FilePreviewModel(iDemandRequireFragment.getActivity(),
                entity, IVerifyHolder.mLoginInfo.getUsername(),
                BaseActivity.getPreviewDirCache(), new MyFilePreviewCallbacks());

        model.doRequest();
    }

    /**
     * 取消下载
     */
    public void doCancelDownload() {
        if (model != null) {
            model.cancelDownload(true);
        }
        model = null;
    }

    class MyFilePreviewCallbacks implements IFilePreviewCallbacks {

        private String getMessage(int resId) {
            return iDemandRequireFragment.getAppResource().getString(resId);
        }

        @Override
        public void onNoInternet() {
            //无网络状态`
            iDemandRequireFragment.showMsg(getMessage(R.string.v1010_toast_net_exception));
            iDemandRequireFragment.hidePreviewProgress();
        }

        @Override
        public void onMobileNet() {
            //使用数据流量的时候
            iDemandRequireFragment.showMobileDownloadAlert(new CustomPopupWindow.OnPositiveClickListener() {
                @Override
                public void onPositiveClick() {
                    model.forceStart();
                }
            });
            iDemandRequireFragment.hidePreviewProgress();
        }

        @Override
        public void onFileNotFoundOnServer() {
            //服务器上没有资源的时候
            iDemandRequireFragment.showMsg(getMessage(R.string.v1020_toast_download_onnotfound));
            iDemandRequireFragment.hidePreviewProgress();
        }

        @Override
        public void onDownloadStart(PreviewFileEntity entity) {
            iDemandRequireFragment.showPreviewProgress(entity);
        }

        @Override
        public void onDownloadCancel() {
            //取消下载的时候
            iDemandRequireFragment.hidePreviewProgress();
            iDemandRequireFragment.showMsg(getMessage(R.string.v1020_toast_preview_cancel));
        }

        /**
         * 文件就绪,可能是下载的，可能是从缓存区拿出来的
         *
         * @param previewFile hint：按照设计，该文件的预览事件是在模块内处理的，此处先给出，但未必使用
         */
        @Override
        public void onFileReady(File previewFile) {
            iDemandRequireFragment.hidePreviewProgress();
        }

        /**
         * 超过下载警告阈值
         *
         * @param netType 网络类型
         * @param max     最大上限 byte
         * @param actual  实际值 byte
         */
        @Override
        public void onLargeSize(int netType, long max, long actual) {
            iDemandRequireFragment.showLargeSizeDownloadAlert(actual, new CustomPopupWindow.OnPositiveClickListener() {
                @Override
                public void onPositiveClick() {
                    model.forceStart();
                }
            });
            iDemandRequireFragment.hidePreviewProgress();
        }


        @Override
        public void onDownloading(PreviewFileEntity entity, long current, long total) {
            //下载过程中、进度更新
            iDemandRequireFragment.UpdateProgress(entity, current, total);
        }

        @Override
        public void onNoEnoughSpace() {
            //本地没有足够空间下载
            iDemandRequireFragment.showMsg(getMessage(R.string.v1020_toast_download_onnoenoughspace));
            iDemandRequireFragment.hidePreviewProgress();
        }

        @Override
        public void onLocalCacheChanged() {
            //本地缓存的文件已修改
        }

        @Override
        public void onNoAppCanPreview() {
            //本地没有应用可以打开此类文件
            iDemandRequireFragment.showMsg(getMessage(R.string.v1020_toast_no_app_can_open));
            iDemandRequireFragment.hidePreviewProgress();
        }

        @Override
        public void downloadFailure() {
            //下载失败
            iDemandRequireFragment.showMsg(getMessage(R.string.v1020_toast_preview_failure));
            iDemandRequireFragment.hidePreviewProgress();
        }
    }

    private String publisher;

    public void callFollowPublisher(String username, String publisher) {
        this.publisher = publisher;
        if (isLogin()) {
            iDemandRequireFragment.showWaitView(true);
            IApiRequestModel model = new FollowModel(username, publisher, new FollowModelCallback
                    ());
            model.doRequest(iDemandRequireFragment.getActivity());
        } else {
            Intent intent = LoginIntentFactory.create(iDemandRequireFragment.getActivity(),
                    LoginTrigger.FollowPublisher);
            iDemandRequireFragment.getActivity().startActivity(intent);
        }
    }

    private class FollowModelCallback implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iDemandRequireFragment.cancelWaitView();
            iDemandRequireFragment.showMsg(beanContainer.getData().getMessage());
            iDemandRequireFragment.setPublisherFollowed(true);
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iDemandRequireFragment.cancelWaitView();
            iDemandRequireFragment.showErrorMsg(beanContainer.getData().getMessage());
            iDemandRequireFragment.setPublisherFollowed(false);
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iDemandRequireFragment.cancelWaitView();
            iDemandRequireFragment.setPublisherFollowed(false);

            // TODO: 2016/9/5
        }
    }

    private class UnFollowModelCallback implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iDemandRequireFragment.cancelWaitView();
            iDemandRequireFragment.showMsg(beanContainer.getData().getMessage());
            iDemandRequireFragment.setPublisherFollowed(false);
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iDemandRequireFragment.cancelWaitView();
            iDemandRequireFragment.showErrorMsg(beanContainer.getData().getMessage());
            iDemandRequireFragment.setPublisherFollowed(true);
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iDemandRequireFragment.cancelWaitView();
            iDemandRequireFragment.setPublisherFollowed(true);

            // TODO: 2016/9/5
        }
    }


    /**
     *
     */
    public enum LoginTrigger implements ITriggerCompare {
        /**
         *
         */
        FollowPublisher(1);

        //unfollow is set as the default state,so if not login,you can only call follow
        //UnFollowPublisher(2);

        private final int tag;

        LoginTrigger(int i) {
            tag = i;
        }

        @Override
        public boolean equals(ITriggerCompare compare) {
            boolean b1 = compare.getClass().getName().equals(getClass().getName());
            boolean b2 = compare.getTag().equals(getTag());
            return b1 & b2;
        }

        @Override
        public Object getTag() {
            return tag;
        }

        @Override
        public Serializable getSerializable() {
            return this;
        }

    }
}
