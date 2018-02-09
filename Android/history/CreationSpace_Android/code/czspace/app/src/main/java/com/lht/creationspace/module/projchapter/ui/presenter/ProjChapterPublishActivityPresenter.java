package com.lht.creationspace.module.projchapter.ui.presenter;

import android.content.Context;
import android.widget.EditText;

import com.lht.creationspace.R;
import com.lht.creationspace.base.model.TextWatcherModel;
import com.lht.creationspace.base.model.apimodel.RestfulApiModelCallback;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.checkable.AbsOnAllCheckLegalListener;
import com.lht.creationspace.checkable.CheckableJobs;
import com.lht.creationspace.checkable.jobs.UnCompetedInputCheckJob;
import com.lht.creationspace.customview.popup.CustomPopupWindow;
import com.lht.creationspace.module.projchapter.ui.model.ProjChapterPublishModel;
import com.lht.creationspace.module.projchapter.ui.model.bean.ProjUpdateResBean;
import com.lht.creationspace.module.proj.ui.HybridProjectDetailActivity;
import com.lht.creationspace.module.projchapter.ui.IProjChapterPublishActivity;
import com.lht.creationspace.module.pub.model.MediaCenterUploadModel;
import com.lht.creationspace.module.pub.model.pojo.MediaCenterUploadResBean;
import com.lht.creationspace.structure.objpool.LocalMediaComparator;
import com.lht.creationspace.structure.objpool.LocalMediasPool;
import com.lht.creationspace.util.internet.HttpUtil;
import com.yalantis.ucrop.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

import thirdparty.leobert.pvselectorlib.PVSelector;
import thirdparty.leobert.pvselectorlib.model.PictureConfig;

/**
 * Created by chhyu on 2017/7/26.
 */

public class ProjChapterPublishActivityPresenter implements IApiRequestPresenter {

    private IProjChapterPublishActivity iProjChapterPublishActivity;
    private final TextWatcherModel textWatcherModel;
    private final ProjChapterPublishModel.RequestData chapterData;

    public ProjChapterPublishActivityPresenter(IProjChapterPublishActivity iProjChapterPublishActivity) {
        this.iProjChapterPublishActivity = iProjChapterPublishActivity;
        textWatcherModel = new TextWatcherModel(new TextWatcherModelCallbackImpl());
        chapterData = new ProjChapterPublishModel.RequestData();
    }

    private class TextWatcherModelCallbackImpl implements TextWatcherModel.TextWatcherModelCallback {

        @Override
        public void onOverLength(int edittextId, int maxLength) {
            iProjChapterPublishActivity.showMsg(iProjChapterPublishActivity.getActivity().getString(R.string.v1013_default_project_update_toast_title_too_long));
        }

        @Override
        public void onChanged(int edittextId, int currentCount, int remains) {
            iProjChapterPublishActivity.updateCurrentTitleLength(currentCount);
        }
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    public void watchText(EditText e, int maxLength) {
        textWatcherModel.doWatcher(e, maxLength);
    }

    private List<LocalMedia> selectedMedias = new ArrayList<>();

    public void callAddPhoto() {
        PVSelector.getPhotoSelector(iProjChapterPublishActivity.getActivity())
                .enableCamera().multiSelect(9).useSystemCompressOnCrop(true, false)
                .setCompleteText("选好了")
                .setSelectedMedia(selectedMedias)
                .launch(new PictureConfig.OnSelectResultCallback() {
                    @Override
                    public void onSelectSuccess(List<LocalMedia> resultList) {
                        selectedMedias = resultList;
                        iProjChapterPublishActivity.updateSelectedImages(selectedMedias);
                    }
                });
    }

    public void removeSelected(int position) {
        selectedMedias.remove(position);
    }

    /**
     * 发布
     */
    public void callPublish(String title, String content, String projectId) {
        chapterData.setProjectId(projectId);
        chapterData.setTitle(title);
        chapterData.setContent(content);

        CheckableJobs.getInstance()
                .next(new UnCompetedInputCheckJob(title,
                        R.string.v1013_default_project_update_title_isempty,
                        iProjChapterPublishActivity))
                .next(new UnCompetedInputCheckJob(content,
                        R.string.v1013_default_project_update_content_isempty,
                        iProjChapterPublishActivity))
                .onAllCheckLegal(new AbsOnAllCheckLegalListener<ProjChapterPublishModel.RequestData>(chapterData) {
                    @Override
                    public void onAllCheckLegal() {

                        if (selectedMedias == null || selectedMedias.isEmpty()) {
                            //无图片，直接发布
                            doPublish(getSavedParam());
                        } else {
                            solveUploadMediaPool();
                            doUploadImages(uploadMediaPool);
                        }
                    }
                })
                .start();
    }

    private LocalMediasPool uploadMediaPool = new LocalMediasPool();

    private void solveUploadMediaPool() {
        if (uploadMediaPool.isEmpty()) {
            uploadMediaPool.add(selectedMedias);
        } else {
            uploadMediaPool.prepare();
            uploadMediaPool.solveRemovedItems(selectedMedias,
                    LocalMediaComparator.getInstance());
            uploadMediaPool.solveAddedItems(selectedMedias,
                    LocalMediaComparator.getInstance());
        }
    }


    private void doUploadImages(LocalMediasPool uploadMediaPool) {
        if (uploadMediaPool.hasNext()) {
            LocalMedia localMedia = uploadMediaPool.next();
            doUpload(localMedia);
        } else {
            iProjChapterPublishActivity.cancelWaitView();
            ArrayList<MediaCenterUploadResBean> images
                    = uploadMediaPool.getSavedItems();
            ArrayList<String> imageIds = new ArrayList<>();
            for (MediaCenterUploadResBean bean : images) {
                imageIds.add(bean.getId());
            }

            chapterData.setImages(imageIds);
            doPublish(chapterData);
        }
    }

    private void doUpload(LocalMedia localMedia) {
        iProjChapterPublishActivity.showWaitView(true);
        MediaCenterUploadModel.MediaCenterUploadData data = new MediaCenterUploadModel.MediaCenterUploadData();
        data.setFilePath(localMedia.getCompressPath());
        data.setType(MediaCenterUploadModel.TYPE_ARTICLE_ATTACHMENT);
        MediaCenterUploadModel model =
                new MediaCenterUploadModel(data, new UploadModelCallback(localMedia));
        model.doRequest(iProjChapterPublishActivity.getActivity());
    }

    private class UploadModelCallback implements
            RestfulApiModelCallback<MediaCenterUploadResBean> {
        private final LocalMedia localMedia;

        UploadModelCallback(LocalMedia localMedia) {
            this.localMedia = localMedia;
        }

        @Override
        public void onSuccess(MediaCenterUploadResBean bean) {
            uploadMediaPool.save(localMedia, bean);
            doUploadImages(uploadMediaPool);
        }

        @Override
        public void onFailure(int restCode, String msg) {

            iProjChapterPublishActivity.cancelWaitView();
            iProjChapterPublishActivity.showMsg("文件上传失败");
            iProjChapterPublishActivity.showDialog(R.string.v1010_dialog_feedback_content_error_upload,
                    R.string.v1010_dialog_feedback_positive_reupload,
                    new CustomPopupWindow.OnPositiveClickListener() {
                        @Override
                        public void onPositiveClick() {
                            doUpload(localMedia);
                        }
                    });
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iProjChapterPublishActivity.cancelWaitView();
        }
    }

    private void doPublish(ProjChapterPublishModel.RequestData paramsDate) {
        iProjChapterPublishActivity.showWaitView(true);
        ProjChapterPublishModel model = new ProjChapterPublishModel(paramsDate,
                new ProjChapterPublishModelImpl());
        model.doRequest(iProjChapterPublishActivity.getActivity());
    }

    private class ProjChapterPublishModelImpl implements RestfulApiModelCallback<ProjUpdateResBean> {

        @Override
        public void onSuccess(ProjUpdateResBean bean) {
            iProjChapterPublishActivity.cancelWaitView();
            iProjChapterPublishActivity.showMsg(iProjChapterPublishActivity.getActivity().getString(R.string.v1013_default_project_update_success));
            iProjChapterPublishActivity.getActivity().finishWithoutOverrideAnim();
            jump2ProjectDetail(bean);
        }

        private void jump2ProjectDetail(ProjUpdateResBean resBean) {
            HybridProjectDetailActivity.LaunchData data
                    = new HybridProjectDetailActivity.LaunchData();
            data.setUrl(resBean.getTo_url());
            data.setOid(resBean.getProjectId());

            HybridProjectDetailActivity.getLauncher(iProjChapterPublishActivity.getActivity())
                    .injectData(data)
                    .launch();
        }

        @Override
        public void onFailure(int restCode, String msg) {
            iProjChapterPublishActivity.cancelWaitView();
            iProjChapterPublishActivity.showMsg(iProjChapterPublishActivity.getActivity().getString(R.string.v1013_default_project_update_failure));
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iProjChapterPublishActivity.cancelWaitView();
        }
    }
}
