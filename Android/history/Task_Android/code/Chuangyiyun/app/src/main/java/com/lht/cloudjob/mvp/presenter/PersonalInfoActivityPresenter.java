package com.lht.cloudjob.mvp.presenter;

import android.app.Activity;
import android.content.Context;

import com.anthonycr.grant.PermissionsResultAction;
import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.MainApplication;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.BaseActivity;
import com.lht.cloudjob.clazz.Lmsg;
import com.lht.cloudjob.customview.CustomDialog;
import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.cloudjob.interfaces.ITriggerCompare;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.BasicInfoModel;
import com.lht.cloudjob.mvp.model.ImageCompressModel;
import com.lht.cloudjob.mvp.model.ImageCopyModel;
import com.lht.cloudjob.mvp.model.ImageGetterModel;
import com.lht.cloudjob.mvp.model.InfoModifyModel;
import com.lht.cloudjob.mvp.model.UploadModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.BasicInfoResBean;
import com.lht.cloudjob.mvp.model.bean.UploadResBean;
import com.lht.cloudjob.mvp.model.pojo.CaptureUriCompat;
import com.lht.cloudjob.mvp.viewinterface.IPersonalInfoActivity;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.lht.cloudjob.util.permission.Permissions;
import com.lht.customwidgetlib.actionsheet.OnActionSheetItemClickListener;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.Serializable;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> PersonalInfoActivityPresenter
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/11.
 */
public class PersonalInfoActivityPresenter implements IApiRequestPresenter {

    private IPersonalInfoActivity iPersonalInfoActivity;

    private IApiRequestModel basicInfoModel;

    private InfoModifyModel infoModifyModel;

    public static final int INTENT_CODE_CAPTURE = 1;

    public static final int INTENT_CODE_ALBUM = 2;

    private final ImageGetterModel imageGetterModel;

    private final ImageCopyModel imageCopyModel;

    public PersonalInfoActivityPresenter(IPersonalInfoActivity iPersonalInfoActivity) {
        this.iPersonalInfoActivity = iPersonalInfoActivity;
        imageGetterModel = new ImageGetterModel();
        imageCopyModel = new ImageCopyModel();
    }

    private String username;

    private void callGetInfo(String username) {
        this.username = username;
        iPersonalInfoActivity.showWaitView(true);
        basicInfoModel = new BasicInfoModel(username, new BasicInfoModelCallback());
        basicInfoModel.doRequest(iPersonalInfoActivity.getActivity());
    }

    public void init(String username) {
        this.username = username;
        callGetInfo(username);
    }

    public void callModifyAvatar() {
        String[] data = new String[]{"拍照", "从相册选择"};
        iPersonalInfoActivity.showAvatarSelectActionsheet(data, new
                OnActionSheetItemClickListener() {
                    @Override
                    public void onActionSheetItemClick(int position) {
                        switch (position) {
                            case 0:
                                callCaptureForImage(iPersonalInfoActivity.getActivity());
                                break;
                            case 1:
                                callAlbumForImage(iPersonalInfoActivity.getActivity());
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void callCaptureForImage(Activity activity) {
        boolean hasPermission = iPersonalInfoActivity.getActivity().checkCameraPermission();
        if (hasPermission) {
            captureUriHolder = getTempUri();
            imageGetterModel.startCapture(activity, INTENT_CODE_CAPTURE, captureUriHolder.getUri());
        } else {
            iPersonalInfoActivity.getActivity().grantCameraPermission(new PermissionsResultAction() {
                @Override
                public void onGranted() {
                    captureUriHolder = getTempUri();
                    imageGetterModel.startCapture(iPersonalInfoActivity.getActivity(), INTENT_CODE_CAPTURE, captureUriHolder.getUri());
                }

                @Override
                public void onDenied(String permission) {
                    iPersonalInfoActivity.showMsg("缺少相机权限，无法调用相机");
                    DLog.e(Lmsg.class, "requestPermission: camera refused");
                    CustomDialog dialog = Permissions.CAMERA.newPermissionGrantReqAlert(iPersonalInfoActivity.getActivity());
                    dialog.show();
                }
            });
        }
    }


    private void callAlbumForImage(Activity activity) {
        imageGetterModel.startSelect(activity, INTENT_CODE_ALBUM);
    }

    private CaptureUriCompat captureUriHolder;

    private CaptureUriCompat getTempUri() {
        return MainApplication.getOurInstance().newCaptureIntentUri();
    }

    public void callModifyNickname() {
        iPersonalInfoActivity.jumpToResetNickname();
    }

    public void callModifySex() {
        String[] data = new String[]{"男", "女"};
        iPersonalInfoActivity.showSexSelectActionsheet(data, new OnActionSheetItemClickListener() {
            @Override
            public void onActionSheetItemClick(int position) {
                boolean isMale = (position == 0);
                doSexModify(isMale);
            }
        });
    }


    public void callBindPhone() {
        if (basicInfoResBean.isPhoneBinded()) {
            iPersonalInfoActivity.jumpToShowPhone(basicInfoResBean);
        } else {
            iPersonalInfoActivity.jumpToBindPhone(basicInfoResBean);
        }
    }

    public void callResetPwd() {
        iPersonalInfoActivity.jumpToResetPwd();
    }

    private BasicInfoResBean basicInfoResBean;

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    private String getUsername() {
        return IVerifyHolder.mLoginInfo.getUsername();
    }

    /**
     * this string will be combined in the name of the image
     */
    private static final String UPLOAD_IMAGE_ATTEMPT = "avatar";

    public void callTransCapture() {
        File f1 = new File(captureUriHolder.getFilePath());
        String filename = ImageGetterModel.NameUtils.generateName(getUsername(),
                UPLOAD_IMAGE_ATTEMPT)
                .replaceAll("\r|\n", "");
        String path2Copy = BaseActivity.getLocalImageCachePath() + "/" + filename;
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
        File f2 = new File(path2Copy);
        imageCopyModel.doCopy(f1, f2, ImageGetTrigger.Album);
    }


    /**
     * 处理图片获取后(copy)的事件
     *
     * @param event 图片本地处理完成事件
     */
    public void callResolveEvent(AppEvent.ImageGetEvent event) {
        ITriggerCompare compare = event.getTrigger();
        if (ImageGetTrigger.Capture.equals(compare) || ImageGetTrigger.Album.equals(compare)) {
            iPersonalInfoActivity.showWaitView(true);
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
        if (event.isSuccess()) {
            ImageCompressModel model = new ImageCompressModel(new ImageCompressModel
                    .IImageCompressCallback() {
                @Override
                public void onCompressed(String imageFilePath) {
                    //upload
                    doUpload(imageFilePath);
                }
            });
            model.doCompress(new File(event.getPath()));
        } else {
            iPersonalInfoActivity.cancelWaitView();
            iPersonalInfoActivity.showErrorMsg(iPersonalInfoActivity.getAppResource().getString(R
                    .string.v1010_default_pau_getpic_failure));
        }
    }

    private void doAvatarModify(String path) {
        iPersonalInfoActivity.showWaitView(true);

        AvatarPlaceHolder.setOnModify(true);

        infoModifyModel = new InfoModifyModel(getUsername(), new ModifyInfoModelCallback());
        infoModifyModel.modifyAvatar(path);
        infoModifyModel.doRequest(iPersonalInfoActivity.getActivity());
    }

    private void doUpload(String path) {
        iPersonalInfoActivity.showWaitView(true);
        IApiRequestModel uploadModel = new UploadModel(getUsername(), path, IRestfulApi.UploadApi
                .VALUE_TYPE_DEFAULT,
                new UploadModelCallback(path));
        uploadModel.doRequest(iPersonalInfoActivity.getActivity());
    }

    private class UploadModelCallback implements ApiModelCallback<UploadResBean> {
        private final String path;

        UploadModelCallback(String path) {
            this.path = path;
        }

        @Override
        public void onSuccess(BaseBeanContainer<UploadResBean> beanContainer) {
            iPersonalInfoActivity.showWaitView(true);
            AvatarPlaceHolder.setIsUploaded(true);
            doAvatarModify(beanContainer.getData().getFile_url());
            AvatarPlaceHolder.setIsUploaded(false);
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iPersonalInfoActivity.cancelWaitView();
            AvatarPlaceHolder.setIsUploaded(false);
            iPersonalInfoActivity.showDialog(R.string.v1010_dialog_feedback_content_error_upload,
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
            AvatarPlaceHolder.setIsUploaded(false);
            iPersonalInfoActivity.cancelWaitView();

        }
    }

    private final class BasicInfoModelCallback implements ApiModelCallback<BasicInfoResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BasicInfoResBean> beanContainer) {
            iPersonalInfoActivity.cancelWaitView();
            basicInfoResBean = beanContainer.getData();
            if (AvatarPlaceHolder.isOnModify()) {
                String newAvatar = beanContainer.getData().getAvatar();
//                AvatarPlaceHolder.setNewPath(newAvatar);
                //清理老头像cache
                Picasso.with(iPersonalInfoActivity.getActivity()).invalidateMemoryAndDisk
                        (IVerifyHolder.mLoginInfo.getAvatar());
                IVerifyHolder.mLoginInfo.setAvatar(newAvatar);
            }
            AvatarPlaceHolder.setOnModify(false);
            AvatarPlaceHolder.setIsUploaded(false);
            iPersonalInfoActivity.updateView(basicInfoResBean);
            EventBus.getDefault().post(new AppEvent.UserInfoUpdatedEvent(basicInfoResBean));
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iPersonalInfoActivity.cancelWaitView();
            iPersonalInfoActivity.showErrorMsg(beanContainer.getData().getMessage());
            AvatarPlaceHolder.setOnModify(false);
            AvatarPlaceHolder.setIsUploaded(false);
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iPersonalInfoActivity.cancelWaitView();
            AvatarPlaceHolder.setOnModify(false);
            AvatarPlaceHolder.setIsUploaded(false);

            // TODO: 2016/7/26  
        }
    }

    private void doSexModify(boolean isMale) {
        iPersonalInfoActivity.showWaitView(true);
        infoModifyModel = new InfoModifyModel(username, new ModifyInfoModelCallback());
        infoModifyModel.modifySex(isMale);
        infoModifyModel.doRequest(iPersonalInfoActivity.getActivity());
    }

    private class ModifyInfoModelCallback implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            //清理延迟到获取新的信息之后
            iPersonalInfoActivity.showMsg(beanContainer.getData().getMessage());
            callGetInfo(username);
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iPersonalInfoActivity.cancelWaitView();
            iPersonalInfoActivity.showErrorMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iPersonalInfoActivity.cancelWaitView();
        }
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

    private static class AvatarPlaceHolder {
        static boolean isUploaded = false;
        static boolean onModify = false;
//        @Deprecated
//        static String oldPath = "";
//        static String newPath = "";

        public static boolean isUploaded() {
            return isUploaded;
        }

        public static void setIsUploaded(boolean isUploaded) {
            AvatarPlaceHolder.isUploaded = isUploaded;
        }

        public static boolean isOnModify() {
            return onModify;
        }

        public static void setOnModify(boolean onModify) {
            AvatarPlaceHolder.onModify = onModify;
        }

//        public static String getOldPath() {
//            return oldPath;
//        }
//
//        public static void setOldPath(String oldPath) {
//            AvatarPlaceHolder.oldPath = oldPath;
//        }
//
//        public static String getNewPath() {
//            return newPath;
//        }
//
//        public static void setNewPath(String newPath) {
//            AvatarPlaceHolder.newPath = newPath;
//        }
    }
}