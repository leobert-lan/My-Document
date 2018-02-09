package com.lht.cloudjob.mvp.presenter;

import android.app.Activity;
import android.content.Context;
import android.widget.EditText;

import com.anthonycr.grant.PermissionsResultAction;
import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.MainApplication;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.BaseActivity;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.checkable.CheckableJobs;
import com.lht.cloudjob.checkable.jobs.CellPhoneCheckJob;
import com.lht.cloudjob.checkable.jobs.UnCompetedInputCheckJob;
import com.lht.cloudjob.clazz.Lmsg;
import com.lht.cloudjob.customview.CustomDialog;
import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.cloudjob.interfaces.ITriggerCompare;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.ImageCompressModel;
import com.lht.cloudjob.mvp.model.ImageCopyModel;
import com.lht.cloudjob.mvp.model.ImageGetterModel;
import com.lht.cloudjob.mvp.model.MediaCenterUploadModel;
import com.lht.cloudjob.mvp.model.PublishDemandModel;
import com.lht.cloudjob.mvp.model.TextWatcherModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.MediaCenterUploadResBean;
import com.lht.cloudjob.mvp.model.pojo.CaptureUriCompat;
import com.lht.cloudjob.mvp.model.pojo.DemandAttachFile;
import com.lht.cloudjob.mvp.viewinterface.IPublishDemandActivity;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.lht.cloudjob.util.permission.Permissions;
import com.lht.customwidgetlib.actionsheet.OnActionSheetItemClickListener;
import com.yalantis.ucrop.entity.LocalMedia;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import thirdparty.leobert.pvselectorlib.PVSelector;
import thirdparty.leobert.pvselectorlib.model.PictureConfig;

/**
 * 一键发需求-presenter
 * Created by chhyu on 2017/2/6.
 */

public class PublishDemandActivityPresenter implements IApiRequestPresenter {

    public static final int INTENT_CODE_CAPTURE = 1;
    public static final int INTENT_CODE_ALBUM = 2;
    private IPublishDemandActivity iPublishDemandActivity;

    private TextWatcherModel textWatcherModel;
    private List<LocalMedia> selectedMedia;

    private final ImageCopyModel imageCopyModel;

    private HashSet<String> imagesSelectedToUpload;

    public PublishDemandActivityPresenter(IPublishDemandActivity iPublishDemandActivity) {
        this.iPublishDemandActivity = iPublishDemandActivity;
        imageCopyModel = new ImageCopyModel();
        imageGetterModel = new ImageGetterModel();
        textWatcherModel = new TextWatcherModel(new TextWatcherModelCallbackImpl());
        selectedMedia = new ArrayList<>();
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    public void watchText(EditText editText, int maxLength) {
        textWatcherModel.doWatcher(editText, maxLength);
    }

    /**
     * this string will be combined in the name of the image
     */
    private static final String UPLOAD_IMAGE_ATTEMPT = "publishDemand";

    public void callTransCapture() {
        File f1 = new File(captureUriHolder.getFilePath());
        String filename = ImageGetterModel.NameUtils.generateName(IVerifyHolder.mLoginInfo.getUsername(),
                UPLOAD_IMAGE_ATTEMPT)
                .replaceAll("\r|\n", "");
        String path2Copy = BaseActivity.getLocalImageCachePath() + "/" + filename;
        //不需要检验是否添加过，照片都是新拍的
        LocalMedia localMedia = new LocalMedia();
        localMedia.setPath(path2Copy);
        localMedia.setCompressed(true);
        localMedia.setCompressPath(path2Copy);
        localMedia.setType(1);
        selectedMedia.add(localMedia);
        iPublishDemandActivity.updateCurrentPicCount(selectedMedia.size(), 5);

        File f2 = new File(path2Copy);
        imageCopyModel.doCopy(f1, f2, PublishDemandActivityPresenter.ImageGetTrigger.Capture);
    }

    public void callResolveEvent(AppEvent.ImageGetEvent event) {
        ITriggerCompare compare = event.getTrigger();
        if (PublishDemandActivityPresenter.ImageGetTrigger.Capture.equals(compare)
                || PublishDemandActivityPresenter.ImageGetTrigger.Album.equals(compare)) {
            iPublishDemandActivity.showWaitView(true);
            handleAfterImageCopy(event);
        }
    }

    /**
     * 处理图片
     * - 压缩
     *
     * @param event 图片本地处理完成事件
     */
    private void handleAfterImageCopy(AppEvent.ImageGetEvent event) {
        //仅处理压缩，不处理上传
        if (event.isSuccess()) {
            ImageCompressModel model = new ImageCompressModel(new ImageCompressModel
                    .IImageCompressCallback() {
                @Override
                public void onCompressed(String imageFilePath) {
                    iPublishDemandActivity.addFeedbackImage(imageFilePath);
                    iPublishDemandActivity.cancelWaitView();
                }
            });
            model.doCompress(new File(event.getPath()));
        } else {
            DLog.i(Lmsg.class, "copy failure");
            iPublishDemandActivity.cancelWaitView();
        }
    }

    public void callRemovePicture(String tag) {
        for (int i = 0; i < selectedMedia.size(); i++) {
            LocalMedia localMedia = selectedMedia.get(i);
            if (localMedia.getPath().equals(tag)) {
                selectedMedia.remove(i);
            }
        }
        iPublishDemandActivity.updateCurrentPicCount(selectedMedia.size(), 5);
    }

    private class TextWatcherModelCallbackImpl implements TextWatcherModel.TextWatcherModelCallback {

        @Override
        public void onOverLength(int edittextId, int maxLength) {
            iPublishDemandActivity.notifyOverLength();
        }

        @Override
        public void onChanged(int edittextId, int currentCount, int remains) {

            iPublishDemandActivity.updateCurrentLength(currentCount);
        }
    }

    private String demandDescribe;

    private String price;

    private String contact;

    public void callPublish(String _demandDescribe, String _price, String _contact) {
        this.demandDescribe = _demandDescribe;
        this.price = _price;
        this.contact = _contact;
        CheckableJobs.getInstance()
                .next(new UnCompetedInputCheckJob(demandDescribe,
                        R.string.v1042_demand_publish_toast_null_desc, iPublishDemandActivity))
                .next(new UnCompetedInputCheckJob(price,
                        R.string.v1042_demand_publish_hint_demand_price, iPublishDemandActivity))
                .next(new UnCompetedInputCheckJob(contact,
                        R.string.v1042_demand_publish_hint_contact_number, iPublishDemandActivity))
                .next(new CellPhoneCheckJob(contact))
                .onAllCheckLegal(new CheckableJobs.OnAllCheckLegalListener() {
                    @Override
                    public void onAllCheckLegal() {
                        iPublishDemandActivity.showWaitView(true);
                        if (selectedMedia.isEmpty()) {
                            doPulishDemand(demandDescribe, price, contact);
                        } else {
                            imagesSelectedToUpload = new HashSet<>();
                            for (LocalMedia localMedia : selectedMedia) {
                                imagesSelectedToUpload.add(localMedia.getCompressPath());
                            }
                            doUpload(imagesSelectedToUpload.iterator().next());
                        }
                    }
                }).start();
    }

    /**
     * 提交
     */
    private void doPulishDemand(String demandDescribe, String price, String contact) {
        iPublishDemandActivity.showWaitView(true);
        PublishDemandModel model = new PublishDemandModel(demandDescribe, price, contact, uploadedImages,
                new PublishDemandModelCallback());
        model.doRequest(iPublishDemandActivity.getActivity());
    }

    private void doUpload(String path) {
        IApiRequestModel uploadModel = new MediaCenterUploadModel(path, new UploadModelCallback(path));
        uploadModel.doRequest(iPublishDemandActivity.getActivity());
    }

    private class PublishDemandModelCallback implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iPublishDemandActivity.cancelWaitView();
            iPublishDemandActivity.showDialog(R.string.v1042_demand_publish_dialog_title,
                    R.string.v1042_demand_publish_dialog_content,
                    R.string.v1042_demand_publish_dialog_pbtn,
                    new CustomPopupWindow.OnPositiveClickListener() {
                        @Override
                        public void onPositiveClick() {
                            iPublishDemandActivity.getActivity().finish();
                        }
                    });
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iPublishDemandActivity.cancelWaitView();
            iPublishDemandActivity.showMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iPublishDemandActivity.cancelWaitView();

        }
    }

    /**
     * 选择图片
     */
    public void doGetPic() {
        String[] data = new String[]{"拍照", "从相册中选择"};
        iPublishDemandActivity.showPicSelectActionsheet(data, new OnActionSheetItemClickListener() {
            @Override
            public void onActionSheetItemClick(int position) {
                switch (position) {
                    case 0:
                        callCaptureForImage(iPublishDemandActivity.getActivity(), PublishDemandActivityPresenter.INTENT_CODE_CAPTURE);
                        break;
                    case 1:
                        callAlbumForImage(iPublishDemandActivity.getActivity(), PublishDemandActivityPresenter.INTENT_CODE_ALBUM);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 拍照
     *
     * @param activity
     */
    private final ImageGetterModel imageGetterModel;

    private void callCaptureForImage(UMengActivity activity, final int code) {
        boolean hasPermission = iPublishDemandActivity.getActivity().checkCameraPermission();
        if (hasPermission) {
            captureUriHolder = getTempUri();
            imageGetterModel.startCapture(activity, code, captureUriHolder.getUri());
        } else {
            iPublishDemandActivity.getActivity().grantCameraPermission(new PermissionsResultAction() {
                @Override
                public void onGranted() {
                    captureUriHolder = getTempUri();
                    imageGetterModel.startCapture(iPublishDemandActivity.getActivity(), code, captureUriHolder.getUri());
                }

                @Override
                public void onDenied(String permission) {
                    iPublishDemandActivity.showMsg("缺少相机权限，无法调用相机");
                    CustomDialog dialog = Permissions.CAMERA.newPermissionGrantReqAlert(iPublishDemandActivity.getActivity());
                    dialog.show();
                }
            });
        }
    }

    private void callAlbumForImage(Activity activity, int code) {
//        imageGetterModel.startSelect(activity, code);
        PVSelector.getPhotoSelector(activity).setCompleteText("选好了")
                .multiSelect(5).setSelectedMedia(selectedMedia)
                .useSystemCompressOnCrop(true, false)
                .launch(new PictureConfig.OnSelectResultCallback() {

                    class ItemComparator implements Comparator<LocalMedia> {

                        @Override
                        public int compare(LocalMedia o1, LocalMedia o2) {
                            if (o1 == null || o2 == null)
                                return 0;
                            if (o1 == o2 || o1.equals(o2))
                                return 1;
                            if (o1.getPath().equals(o2.getPath()))
                                return 1;
                            return 0;
                        }
                    }

                    @Override
                    public void onSelectSuccess(List<LocalMedia> resultList) {
                        Set<LocalMedia> removed = getRemovedItems(selectedMedia, resultList);
                        selectedMedia = resultList;
                        iPublishDemandActivity.updateCurrentPicCount(resultList.size(), 5);
                        iPublishDemandActivity.updateSelectedPic(selectedMedia, removed);
                    }

                    private Set<LocalMedia> getRemovedItems(List<LocalMedia> old, List<LocalMedia> now) {
                        Set<LocalMedia> ret = new HashSet<>();
                        if (now == null || now.isEmpty()) {
                            ret.addAll(old);
                            return ret;
                        }

                        for (LocalMedia oldItem : old) {
                            if (oldItem == null)
                                continue;
                            if (!inCollection(oldItem, now))
                                ret.add(oldItem);
                        }
                        return ret;
                    }

                    private boolean inCollection(LocalMedia localMedia, List<LocalMedia> collection) {
                        if (localMedia == null)
                            return false;
                        if (collection == null || collection.isEmpty())
                            return false;
                        Comparator<LocalMedia> comparator = new ItemComparator();
                        for (LocalMedia item : collection) {
                            if (1 == comparator.compare(localMedia, item))
                                return true;
                        }
                        return false;
                    }
                });
    }


    private CaptureUriCompat captureUriHolder;

    private CaptureUriCompat getTempUri() {
        return MainApplication.getOurInstance().newCaptureIntentUri();
    }

    public enum ImageGetTrigger implements ITriggerCompare {
        Capture(1), Album(2);
        private final int tag;

        ImageGetTrigger(int i) {
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

    /**
     * 已上传的附件编号！
     */
    private ArrayList<DemandAttachFile> uploadedImages = new ArrayList<>();

    private class UploadModelCallback implements ApiModelCallback<MediaCenterUploadResBean> {
        private final String path;

        public UploadModelCallback(String path) {
            this.path = path;
        }

        @Override
        public void onSuccess(BaseBeanContainer<MediaCenterUploadResBean> beanContainer) {
            DemandAttachFile file = DemandAttachFile.fromLocal(path);
            file.setFile_path(beanContainer.getData().getPath());
            uploadedImages.add(file);
            imagesSelectedToUpload.remove(path);
            Iterator<String> iterator = imagesSelectedToUpload.iterator();
            if (iterator.hasNext()) {
                doUpload(iterator.next());
            } else {
                doPulishDemand(demandDescribe, price, contact);
            }
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iPublishDemandActivity.cancelWaitView();
            iPublishDemandActivity.showDialog(R.string.v1010_dialog_feedback_content_error_upload,
                    R.string.v1010_dialog_feedback_positive_reupload,
                    new CustomPopupWindow.OnPositiveClickListener() {
                        @Override
                        public void onPositiveClick() {
                            doUpload(path);
                        }
                    });
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iPublishDemandActivity.cancelWaitView();
            iPublishDemandActivity.showDialog(R.string.v1010_dialog_feedback_content_error_upload,
                    R.string.v1010_dialog_feedback_positive_reupload,
                    new CustomPopupWindow.OnPositiveClickListener() {
                        @Override
                        public void onPositiveClick() {
                            doUpload(path);
                        }
                    });

        }
    }
}
