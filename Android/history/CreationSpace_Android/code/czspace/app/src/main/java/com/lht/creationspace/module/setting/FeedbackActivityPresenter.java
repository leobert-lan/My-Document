package com.lht.creationspace.module.setting;

import android.content.Context;
import android.widget.EditText;

import com.lht.creationspace.R;
import com.lht.creationspace.structure.objpool.LocalMediaComparator;
import com.lht.creationspace.structure.objpool.LocalMediasPool;
import com.lht.creationspace.customview.popup.CustomPopupWindow;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.base.model.apimodel.ApiModelCallback;
import com.lht.creationspace.module.setting.model.FeedbackModel;
import com.lht.creationspace.module.pub.model.MediaCenterUploadModel;
import com.lht.creationspace.base.model.apimodel.RestfulApiModelCallback;
import com.lht.creationspace.base.model.TextWatcherModel;
import com.lht.creationspace.base.model.apimodel.BaseBeanContainer;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.module.pub.model.pojo.MediaCenterUploadResBean;
import com.lht.creationspace.module.setting.ui.IFeedbackActivity;
import com.lht.creationspace.util.internet.HttpUtil;
import com.lht.creationspace.util.string.StringUtil;
import com.yalantis.ucrop.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

import thirdparty.leobert.pvselectorlib.PVSelector;
import thirdparty.leobert.pvselectorlib.model.PictureConfig;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.presenter
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> FeedbackActivityPresenter
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/5/1.
 */
public class FeedbackActivityPresenter implements IApiRequestPresenter {
    private final TextWatcherModel textWatcherModel;

    private final IFeedbackActivity iFeedbackActivity;

    public FeedbackActivityPresenter(IFeedbackActivity iFeedbackActivity) {
        this.iFeedbackActivity = iFeedbackActivity;
//        imagesSelectedToUpload = new HashSet<>();
        textWatcherModel = new TextWatcherModel(new TextWatcherModelCallbackImpl());
    }

    public void watchInputLength(EditText editText, int maxLength) {
        textWatcherModel.doWatcher(editText, maxLength);
    }

    public static final int MAX_IMAGE_COUNT = 5;

    private List<LocalMedia> selectedMedias = new ArrayList<>();

    public void callAddPicture(int currentCount) {
        if (currentCount > MAX_IMAGE_COUNT) { // we have a add-image-cell,so currentCount is 6
            // when imagesCount is 5
            iFeedbackActivity.showMsg(iFeedbackActivity.getAppResource()
                    .getString(R.string.v1010_toast_feedback_error_images_over_count));
            return;
        }

        PVSelector.getPhotoSelector(iFeedbackActivity.getActivity())
                .enableCamera().multiSelect(MAX_IMAGE_COUNT)
                .useSystemCompressOnCrop(true, false)
                .setCompleteText("选好了")
                .setSelectedMedia(selectedMedias)
                .launch(new PictureConfig.OnSelectResultCallback() {
                    @Override
                    public void onSelectSuccess(List<LocalMedia> resultList) {
                        selectedMedias = resultList;
                        iFeedbackActivity.updateSelectedMedias(selectedMedias);
                    }
                });

    }


    private void doUpload(LocalMedia localMedia) {
        iFeedbackActivity.showWaitView(true);
        MediaCenterUploadModel.MediaCenterUploadData data = new MediaCenterUploadModel.MediaCenterUploadData();
        data.setFilePath(localMedia.getCompressPath());
        data.setType(MediaCenterUploadModel.TYPE_FEEDBACK_ATTACHMENT);
        MediaCenterUploadModel model =
                new MediaCenterUploadModel(data, new UploadModelCallback(localMedia));
        model.doRequest(iFeedbackActivity.getActivity());
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    /**
     * 提交反馈
     *
     * @param content 内容
     * @param contact 联系人电话
     */
    public void callSubmit(String content, String contact) {
        if (!isComplete(content, R.string.v1010_toast_feedback_error_content_null)) {
            return;
        }
        if (!isComplete(contact, R.string.v1010_toast_feedback_error_contact_null)) {
            return;
        }
        this.contact = contact;
        this.content = content;
        iFeedbackActivity.showWaitView(true);
        if (selectedMedias.isEmpty()) {
            uploadedImages = new ArrayList<>();
            doFeedBack();
        } else {
            solveUploadMediaPool();
            doUploadImages(uploadMediaPool);
        }

    }

    LocalMediasPool uploadMediaPool = new LocalMediasPool();

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

    private ArrayList<String> uploadedImages = new ArrayList<>();

    private void doUploadImages(LocalMediasPool uploadMediaPool) {
        if (uploadMediaPool.hasNext()) {
            LocalMedia localMedia = uploadMediaPool.next();
            doUpload(localMedia);
        } else {
            iFeedbackActivity.cancelWaitView();
            ArrayList<MediaCenterUploadResBean> images
                    = uploadMediaPool.getSavedItems();
            uploadedImages = new ArrayList<>();
            for (MediaCenterUploadResBean bean : images) {
                uploadedImages.add(bean.getId());
            }
            doFeedBack();
        }
    }

    private String content;

    private String contact;

    private boolean isComplete(String text, int toastResid) {
        boolean isEmpty = StringUtil.isEmpty(text);
        if (isEmpty) {
            iFeedbackActivity.showMsg(iFeedbackActivity.getAppResource().getString(toastResid));
        }
        return !isEmpty;
    }

    public void removeSelected(int position) {
        selectedMedias.remove(position);
    }

    private class TextWatcherModelCallbackImpl implements TextWatcherModel
            .TextWatcherModelCallback {

        @Override
        public void onOverLength(int edittextId, int maxLength) {
            iFeedbackActivity.notifyOverLength();
        }

        @Override
        public void onChanged(int edittextId, int currentCount, int remains) {

        }
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
            iFeedbackActivity.cancelWaitView();
            iFeedbackActivity.showDialog(R.string.v1010_dialog_feedback_content_error_upload,
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
            iFeedbackActivity.cancelWaitView();
            iFeedbackActivity.showDialog(R.string.v1010_dialog_feedback_content_error_upload,
                    R.string.v1010_dialog_feedback_positive_reupload,
                    new CustomPopupWindow.OnPositiveClickListener() {
                        @Override
                        public void onPositiveClick() {
                            doUpload(localMedia);
                        }
                    });
        }
    }


    private void doFeedBack() {
        FeedbackModel.FeedbackData data = new FeedbackModel.FeedbackData();
        data.setContent(content);
        data.setMobile_required(true);
        data.setContact(contact);
        IApiRequestModel model = new FeedbackModel(data, uploadedImages, new FeedbackModelCallback());
        model.doRequest(iFeedbackActivity.getActivity());
    }

    private class FeedbackModelCallback
            implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iFeedbackActivity.cancelWaitView();
            iFeedbackActivity.showDialog(R.string.v1010_dialog_feedback_content_success_feedback,
                    R.string.v1010_dialog_feedback_positive_close,
                    new CustomPopupWindow.OnPositiveClickListener() {
                        @Override
                        public void onPositiveClick() {
                            iFeedbackActivity.getActivity().finish();
                        }
                    });
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iFeedbackActivity.cancelWaitView();
            iFeedbackActivity.showMsg(beanContainer.getData().getMessage());
//            if (beanContainer.getData().getRet() == FeedbackModel.RET_ERROR_PHONE_INVALID) {
//            } else {
//                iFeedbackActivity.showDialog(R.string.v1010_dialog_feedback_content_error_feedback,
//                        R.string.v1010_dialog_feedback_positive_resubmit,
//                        new CustomPopupWindow.OnPositiveClickListener() {
//                            @Override
//                            public void onPositiveClick() {
//                                doFeedBack();
//                            }
//                        });
//            }
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iFeedbackActivity.cancelWaitView();
        }
    }
}
