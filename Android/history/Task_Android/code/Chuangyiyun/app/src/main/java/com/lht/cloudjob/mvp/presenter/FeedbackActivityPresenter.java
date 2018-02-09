package com.lht.cloudjob.mvp.presenter;

import android.app.Activity;
import android.content.Context;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.anthonycr.grant.PermissionsResultAction;
import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.MainApplication;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.BaseActivity;
import com.lht.cloudjob.customview.CustomDialog;
import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.cloudjob.interfaces.ITriggerCompare;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.FeedbackModel;
import com.lht.cloudjob.mvp.model.ImageCompressModel;
import com.lht.cloudjob.mvp.model.ImageCopyModel;
import com.lht.cloudjob.mvp.model.ImageGetterModel;
import com.lht.cloudjob.mvp.model.TextWatcherModel;
import com.lht.cloudjob.mvp.model.UploadModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.UploadResBean;
import com.lht.cloudjob.mvp.model.pojo.CaptureUriCompat;
import com.lht.cloudjob.mvp.viewinterface.IFeedbackActivity;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.lht.cloudjob.util.permission.Permissions;
import com.lht.cloudjob.util.string.StringUtil;
import com.lht.customwidgetlib.actionsheet.OnActionSheetItemClickListener;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> FeedbackActivityPresenter
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/5/1.
 */
public class FeedbackActivityPresenter implements IApiRequestPresenter {
    private final TextWatcherModel textWatcherModel;

    private final IFeedbackActivity iFeedbackActivity;

    public static final int INTENT_CODE_CAPTURE = 1;

    public static final int INTENT_CODE_ALBUM = 2;

    private final ImageGetterModel imageGetterModel;

    private final ImageCopyModel imageCopyModel;

    private HashSet<String> imagesSelectedToUpload;

    public FeedbackActivityPresenter(IFeedbackActivity iFeedbackActivity) {
        this.iFeedbackActivity = iFeedbackActivity;
        imagesSelectedToUpload = new HashSet<>();
        textWatcherModel = new TextWatcherModel(new TextWatcherModelCallbackImpl());
        imageGetterModel = new ImageGetterModel();
        imageCopyModel = new ImageCopyModel();
    }

    public void watchInputLength(EditText editText, int maxLength) {
        textWatcherModel.doWatcher(editText, maxLength);
    }

    public static final int MAX_IMAGE_COUNT = 5;

    public void callAddPicture(int currentCount) {

        if (currentCount > MAX_IMAGE_COUNT) { // we have a add-image-cell,so currentCount is 6
            // when imagesCount is 5
            iFeedbackActivity.showMsg(iFeedbackActivity.getAppResource()
                    .getString(R.string.v1010_toast_feedback_error_images_over_count));
            return;
        }

        String[] data = new String[]{"拍照", "从相册选择"};
        iFeedbackActivity.showImageGetActionSheet(data, new OnActionSheetItemClickListener() {
            @Override
            public void onActionSheetItemClick(int position) {
                switch (position) {
                    case 0:
                        callCaptureForImage(iFeedbackActivity.getActivity());
                        break;
                    case 1:
                        callAlbumForImage(iFeedbackActivity.getActivity());
                        break;
                    default:
                        break;
                }
            }
        }, true);
    }

    private void callCaptureForImage(Activity activity) {
        boolean hasPermission = iFeedbackActivity.getActivity().checkCameraPermission();
        if (hasPermission) {
            captureUriHolder = getTempUri();
            imageGetterModel.startCapture(activity, INTENT_CODE_CAPTURE, captureUriHolder.getUri());
        } else {
            iFeedbackActivity.getActivity().grantCameraPermission(new PermissionsResultAction() {
                @Override
                public void onGranted() {
                    captureUriHolder = getTempUri();
                    imageGetterModel.startCapture(iFeedbackActivity.getActivity(), INTENT_CODE_CAPTURE, captureUriHolder.getUri());
                }

                @Override
                public void onDenied(String permission) {
                    iFeedbackActivity.showMsg("缺少相机权限，无法调用相机");
                    CustomDialog dialog = Permissions.CAMERA.newPermissionGrantReqAlert(iFeedbackActivity.getActivity());
                    dialog.show();
                }
            });
        }
    }

    private void callAlbumForImage(Activity activity) {
        imageGetterModel.startSelect(activity, INTENT_CODE_ALBUM);
    }

    /**
     * this string will be combined in the name of the image
     */
    private static final String UPLOAD_IMAGE_ATTEMPT = "feedback";

    public void callTransCapture() {
        File f1 = new File(captureUriHolder.getFilePath());
        String filename = ImageGetterModel.NameUtils.generateName(getUsername(),
                UPLOAD_IMAGE_ATTEMPT)
                .replaceAll("\r|\n", "");
        String path2Copy = BaseActivity.getLocalImageCachePath() + "/" + filename;
        //不需要检验是否添加过，照片都是新拍的

        imagesSelectedToUpload.add(path2Copy);
        File f2 = new File(path2Copy);
        imageCopyModel.doCopy(f1, f2, ImageGetTrigger.Capture);
    }

    /**
     * 选择相册图片后调用转换
     *
     * @param path 图片路径
     */
    public void callTransAlbum(String path) {
        File f1 = new File(path);
        String filename = ImageGetterModel.NameUtils.generateName(getUsername(),
                UPLOAD_IMAGE_ATTEMPT)
                .replaceAll("\r|\n", "");
        String path2Copy = BaseActivity.getLocalImageCachePath() + "/" + filename;
        if (isSelectedEver(path2Copy)) {  //it will never work because of the NameUtils
            iFeedbackActivity.showMsg(iFeedbackActivity.getAppResource()
                    .getString(R.string.v1010_toast_feedback_error_image_selected_ever));
            return;
        }
        imagesSelectedToUpload.add(path2Copy);
        File f2 = new File(path2Copy);
        imageCopyModel.doCopy(f1, f2, ImageGetTrigger.Album);
    }

    /**
     * 是否已经添加过,注意：永远返回false，因为命名机在命名中添加了时间戳
     *
     * @param path copy到缓存区的图片路径
     * @return true if selected ever,false otherwise;
     */
    private boolean isSelectedEver(String path) {
        return imagesSelectedToUpload.contains(path);
    }

    /**
     * 处理图片获取后(copy)的事件
     *
     * @param event 图片本地处理完成事件
     */
    public void callResolveEvent(AppEvent.ImageGetEvent event) {
        ITriggerCompare compare = event.getTrigger();
        if (ImageGetTrigger.Capture.equals(compare) || ImageGetTrigger.Album.equals(compare)) {
            iFeedbackActivity.showWaitView(true);
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
                    iFeedbackActivity.addFeedbackImage(imageFilePath);
                    iFeedbackActivity.cancelWaitView();
                }
            });
            model.doCompress(new File(event.getPath()));
        } else {
            iFeedbackActivity.cancelWaitView();
        }
    }


    public void doUpload(String path) {
        IApiRequestModel uploadModel = new UploadModel(getUsername(), path, IRestfulApi.UploadApi
                .VALUE_TYPE_DEFAULT,
                new UploadModelCallback(path));
        uploadModel.doRequest(iFeedbackActivity.getActivity());
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    public void callRemovePicture(String path) {
        debugSelected();
//        DLog.i(getClass(), "path:" + path);
        imagesSelectedToUpload.remove(path);
        debugSelected();
    }

    private void debugSelected() {
        DLog.e(getClass(), "count:" + imagesSelectedToUpload.size()
                + "\r\n try to see contents:" + JSON.toJSONString(imagesSelectedToUpload));
    }

    /**
     * 已上传的附件编号！
     */
    private ArrayList<String> uploadedImages = new ArrayList<>();

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
        uploadedImages = new ArrayList<>();
        if (imagesSelectedToUpload.isEmpty()) {
            doFeedBack();
        } else {
            doUpload(imagesSelectedToUpload.iterator().next());
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

    private CaptureUriCompat captureUriHolder;

    private CaptureUriCompat getTempUri() {
        return MainApplication.getOurInstance().newCaptureIntentUri();
    }

    private String getUsername() {
        return IVerifyHolder.mLoginInfo.getUsername();
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

    private class UploadModelCallback implements ApiModelCallback<UploadResBean> {
        private final String path;

        UploadModelCallback(String path) {
            this.path = path;
        }

        @Override
        public void onSuccess(BaseBeanContainer<UploadResBean> beanContainer) {
            uploadedImages.add(beanContainer.getData().getFile_id());
            imagesSelectedToUpload.remove(path);
            Iterator<String> iterator = imagesSelectedToUpload.iterator();
            if (iterator.hasNext()) {
                doUpload(iterator.next());
            } else {
                doFeedBack();
            }
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iFeedbackActivity.cancelWaitView();
            iFeedbackActivity.showDialog(R.string.v1010_dialog_feedback_content_error_upload,
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
            // TODO: 2016/9/13
            iFeedbackActivity.cancelWaitView();

        }
    }


    private void doFeedBack() {
        IApiRequestModel model = new FeedbackModel(getUsername(),
                content, contact, uploadedImages, new FeedbackModelCallback());
        model.doRequest(iFeedbackActivity.getActivity());
    }

    private class FeedbackModelCallback implements ApiModelCallback<BaseVsoApiResBean> {

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
            iFeedbackActivity.showDialog(R.string.v1010_dialog_feedback_content_error_feedback,
                    R.string.v1010_dialog_feedback_positive_resubmit,
                    new CustomPopupWindow.OnPositiveClickListener() {
                        @Override
                        public void onPositiveClick() {
                            doFeedBack();
                        }
                    });
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iFeedbackActivity.cancelWaitView();
            // TODO: 2016/9/13
        }
    }


}
