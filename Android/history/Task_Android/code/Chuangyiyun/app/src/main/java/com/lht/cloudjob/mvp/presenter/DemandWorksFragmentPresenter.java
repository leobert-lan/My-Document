package com.lht.cloudjob.mvp.presenter;

import android.content.Context;
import android.content.Intent;

import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.BaseActivity;
import com.lht.cloudjob.clazz.ImagePreviewIntentFactory;
import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.interfaces.net.IPagedApiRequestModel;
import com.lht.cloudjob.mvp.DemandWorksListModel;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.FilePreviewModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.DemandInfoResBean;
import com.lht.cloudjob.mvp.model.bean.DemandWorksResBean;
import com.lht.cloudjob.mvp.model.interfaces.IFilePreviewCallbacks;
import com.lht.cloudjob.mvp.model.pojo.PreviewFileEntity;
import com.lht.cloudjob.mvp.model.pojo.PreviewImage;
import com.lht.cloudjob.mvp.viewinterface.IDemandWorksFragment;
import com.lht.cloudjob.util.file.PreviewUtils;
import com.lht.cloudjob.util.string.StringUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> DemandWorksPresenter
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/8/29
 */
public class DemandWorksFragmentPresenter implements IApiRequestPresenter {
    private IDemandWorksFragment iDemandWorksFragment;

    private IPagedApiRequestModel worksListModel;

    public DemandWorksFragmentPresenter(IDemandWorksFragment iDemandWorksFragment) {
        this.iDemandWorksFragment = iDemandWorksFragment;
        worksListModel = new DemandWorksListModel(iDemandWorksFragment.getTaskBn(), new
                DemandWorksListModelCallback());
    }

    private boolean isRefreshOperate;

    public void callGetWorksList(String usr) {
        isRefreshOperate = true;
        iDemandWorksFragment.showWaitView(true);
        worksListModel.setParams(usr, 0);
        worksListModel.doRequest(iDemandWorksFragment.getActivity());
    }

    public void callAddWorksList(String usr, int offset) {
        isRefreshOperate = false;
        iDemandWorksFragment.showWaitView(true);
        worksListModel.setParams(usr, offset);
        worksListModel.doRequest(iDemandWorksFragment.getActivity());
    }

    /**
     * 页面结束时取消所有相关的回调
     *
     * @param context
     */
    @Override
    public void cancelRequestOnFinish(Context context) {
//        HttpUtil.getInstance().onActivityDestroy(context);
        worksListModel.cancelRequestByContext(context);
    }

    public void doCancelDownload() {
        if (model != null) {
            model.cancelDownload(true);
        }
        model = null;
    }

    private class DemandWorksListModelCallback implements
            ApiModelCallback<DemandWorksResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<DemandWorksResBean> beanContainer) {
            iDemandWorksFragment.cancelWaitView();
            if (isRefreshOperate) {
                ArrayList<DemandInfoResBean.Work> works = beanContainer.getData().getWorks();
                if (works == null) {
                    works = new ArrayList<>();
                }
                iDemandWorksFragment.updateCount(beanContainer.getData().getTotal_bids());
                iDemandWorksFragment.setListData(works);
            } else {
                ArrayList<DemandInfoResBean.Work> works = beanContainer.getData().getWorks();
                if (works == null) {
                    works = new ArrayList<>();
                }
                iDemandWorksFragment.updateCount(beanContainer.getData().getTotal_bids());
                iDemandWorksFragment.addListData(works);
            }
            iDemandWorksFragment.finishRefresh();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iDemandWorksFragment.cancelWaitView();
            if (isRefreshOperate) {
                //ignore
            } else {
                //已经到底了
                iDemandWorksFragment.showErrorMsg(iDemandWorksFragment.getActivity().getString(R.string.v1010_toast_list_all_data_added));
            }
            iDemandWorksFragment.finishRefresh();
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iDemandWorksFragment.cancelWaitView();

            // TODO: 2016/9/23 list done
            iDemandWorksFragment.finishRefresh();

        }
    }

    private FilePreviewModel model;

    /**
     * 图片预览
     */
    public void callPreviewImage(ArrayList<DemandInfoResBean.AttachmentExt> attachmentExts,
                                 String demandOwner, String workOwner) {
        if (!checkMyPreviewAuthority(demandOwner, workOwner)) {
            iDemandWorksFragment.showMsg(iDemandWorksFragment.getAppResource()
                    .getString(R.string.v1020_toast_preview_noauthrity));
            return;
        }
        ArrayList<PreviewImage> previewImages = PreviewImage.copyFromAttachmentExtArrayList(attachmentExts);
        Intent intent = ImagePreviewIntentFactory.newImagePreviewIntent(iDemandWorksFragment.getActivity(), previewImages, 0);
        iDemandWorksFragment.getActivity().startActivity(intent);
    }

    /**
     * 文件预览
     *
     * @param attachmentExt
     */
    public void callPreviewFile(String demandId,DemandInfoResBean.AttachmentExt attachmentExt, String demandOwner, String workOwner) {
        if (!checkMyPreviewAuthority(demandOwner, workOwner)) {
            iDemandWorksFragment.showMsg(iDemandWorksFragment.getAppResource()
                    .getString(R.string.v1020_toast_preview_noauthrity));
            return;
        }
        if (!PreviewUtils.isSupportByVsoPreviewRules(attachmentExt.getMime())) {
            iDemandWorksFragment.showMsg(iDemandWorksFragment
                    .getAppResource().getString(R.string.v1020_toast_preview_unsupportbyus));
            return;
        }
        PreviewFileEntity entity = PreviewFileEntity.copyFromAttachmentExt(attachmentExt);
        entity.setTaskBn(demandId);
        model = new FilePreviewModel(iDemandWorksFragment.getActivity(),
                entity, IVerifyHolder.mLoginInfo.getUsername(), BaseActivity.getPreviewDirCache(),
                new MyFilePreviewCallbacks());

        model.doRequest();
    }

    private boolean checkMyPreviewAuthority(String demandOwner, String workOwner) {
        if (StringUtil.isEmpty(demandOwner)) {
            return false;
        }
        if (StringUtil.isEmpty(workOwner)) {
            return false;
        }
        String me = IVerifyHolder.mLoginInfo.getUsername();
        if (StringUtil.isEmpty(me)) {
            return false;
        }
        if (me.equals(demandOwner) || me.equals(workOwner)) {
            return true;
        }
        return false;
    }

    private class MyFilePreviewCallbacks implements IFilePreviewCallbacks {
        private String getMessage(int resId) {
            return iDemandWorksFragment.getAppResource().getString(resId);
        }

        @Override
        public void onNoInternet() {
            //无网络状态`
            iDemandWorksFragment.showMsg(getMessage(R.string.v1010_toast_net_exception));
            iDemandWorksFragment.hidePreviewProgress();
        }

        @Override
        public void onMobileNet() {
            //使用数据流量的时候
            iDemandWorksFragment.showMobileDownloadAlert(new CustomPopupWindow.OnPositiveClickListener() {
                @Override
                public void onPositiveClick() {
                    model.forceStart();
                }
            });
        }

        @Override
        public void onFileNotFoundOnServer() {
            //服务器上没有资源的时候
            iDemandWorksFragment.showMsg(getMessage(R.string.v1020_toast_download_onnotfound));
            iDemandWorksFragment.hidePreviewProgress();
        }

        @Override
        public void onDownloadStart(PreviewFileEntity entity) {
            iDemandWorksFragment.showPreviewProgress(entity);
        }

        @Override
        public void onDownloadCancel() {
            //取消下载的时候
            iDemandWorksFragment.hidePreviewProgress();
            iDemandWorksFragment.showMsg(getMessage(R.string.v1020_toast_preview_cancel));
        }

        @Override
        public void onFileReady(File previewFile) {
            iDemandWorksFragment.hidePreviewProgress();
        }

        @Override
        public void onLargeSize(int netType, long max, long actual) {

            iDemandWorksFragment.showLargeSizeDownloadAlert(actual,new CustomPopupWindow.OnPositiveClickListener() {
                @Override
                public void onPositiveClick() {
                    model.forceStart();
                }
            });
            iDemandWorksFragment.hidePreviewProgress();
        }

        @Override
        public void onDownloading(PreviewFileEntity entity, long current, long total) {
            //下载过程中、进度更新
            iDemandWorksFragment.UpdateProgress(entity, current, total);
        }

        @Override
        public void onNoEnoughSpace() {
            //本地没有足够空间下载
            iDemandWorksFragment.showMsg(getMessage(R.string.v1020_toast_download_onnoenoughspace));
            iDemandWorksFragment.hidePreviewProgress();
        }

        @Override
        public void onLocalCacheChanged() {
            //本地缓存的文件已修改
            //ignore
        }

        @Override
        public void onNoAppCanPreview() {
            //本地没有应用可以打开此类文件
            iDemandWorksFragment.showMsg(getMessage(R.string.v1020_toast_no_app_can_open));
            iDemandWorksFragment.hidePreviewProgress();
        }

        @Override
        public void downloadFailure() {
//下载失败
            iDemandWorksFragment.showMsg(getMessage(R.string.v1020_toast_preview_failure));
            iDemandWorksFragment.hidePreviewProgress();
        }
    }
}
