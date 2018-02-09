package com.lht.cloudjob.mvp.presenter;

import android.app.Activity;
import android.content.Context;
import android.widget.EditText;

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
import com.lht.cloudjob.interfaces.umeng.IUmengEventKey;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.ImageCompressModel;
import com.lht.cloudjob.mvp.model.ImageCopyModel;
import com.lht.cloudjob.mvp.model.ImageGetterModel;
import com.lht.cloudjob.mvp.model.MediaCenterUploadModel;
import com.lht.cloudjob.mvp.model.TextWatcherModel;
import com.lht.cloudjob.mvp.model.UndertakeModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.LetterBean;
import com.lht.cloudjob.mvp.model.bean.MediaCenterUploadResBean;
import com.lht.cloudjob.mvp.model.pojo.CaptureUriCompat;
import com.lht.cloudjob.mvp.viewinterface.IUndertakeActivity;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.lht.cloudjob.util.permission.Permissions;
import com.lht.cloudjob.util.string.StringUtil;
import com.lht.customwidgetlib.actionsheet.OnActionSheetItemClickListener;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.Serializable;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> UndertakeActivityPresenter
 * <p><b>Description</b>: 投稿
 * <p>Created by Administrator on 2016/9/2.
 */

public class UndertakeActivityPresenter implements IApiRequestPresenter {

    private final IUndertakeActivity iUndertakeActivity;
    private final LetterBean letterBean;
    public static final int INTENT_CODE_CAPTURE = 1;

    public static final int INTENT_CODE_ALBUM = 2;

    private final ImageGetterModel imageGetterModel;

    private final ImageCopyModel imageCopyModel;

    public static final int MAX_DESC_LENGTH = 50;

    public static final int MAX_PRICE_LENGTH = 10;

    public static final int MAX_CYCLE_LENGTH = 6;

    public static final int MAX_CYCLE_VALUE = 65535;

    private TextWatcherModel textWatcherModel, simpleWatchModel;

    public UndertakeActivityPresenter(IUndertakeActivity iUndertakeActivity) {
        this.iUndertakeActivity = iUndertakeActivity;
        letterBean = new LetterBean();

        imageGetterModel = new ImageGetterModel();
        imageCopyModel = new ImageCopyModel();
        textWatcherModel = new TextWatcherModel(new TextWatcherModelCallbackImpl());
        simpleWatchModel = new TextWatcherModel(new TextWatcherModel.TextWatcherModelCallback.EmptyCallbackImpl());
    }

    public void watchText(EditText editText, int maxLength) {
        textWatcherModel.doWatcher(editText, maxLength);
    }

    public void watchTextWithSimpleWatcher(EditText editText, int maxLength) {
        simpleWatchModel.doWatcher(editText, maxLength);
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    public void callAddPicture() {
        String[] data = new String[]{"拍照", "从相册选择"};
        iUndertakeActivity.showImageGetActionSheet(data, new OnActionSheetItemClickListener() {
            @Override
            public void onActionSheetItemClick(int position) {
                switch (position) {
                    case 0:
                        callCaptureForImage(iUndertakeActivity.getActivity());
                        break;
                    case 1:
                        callAlbumForImage(iUndertakeActivity.getActivity());
                        break;
                    default:
                        break;
                }
            }
        }, true);
    }

    private CaptureUriCompat captureUriHolder;

    private CaptureUriCompat getTempUri() {
        return MainApplication.getOurInstance().newCaptureIntentUri();
    }

    private void callCaptureForImage(Activity activity) {
        boolean hasPermission = iUndertakeActivity.getActivity().checkCameraPermission();
        if (hasPermission) {
            captureUriHolder = getTempUri();
            imageGetterModel.startCapture(activity, INTENT_CODE_CAPTURE, captureUriHolder.getUri());
        } else {
            iUndertakeActivity.getActivity().grantCameraPermission(new PermissionsResultAction() {
                @Override
                public void onGranted() {
                    captureUriHolder = getTempUri();
                    imageGetterModel.startCapture(iUndertakeActivity.getActivity(), INTENT_CODE_CAPTURE, captureUriHolder.getUri());
                }

                @Override
                public void onDenied(String permission) {
                    iUndertakeActivity.showMsg("缺少相机权限，无法调用相机");
                    CustomDialog dialog = Permissions.CAMERA.newPermissionGrantReqAlert(iUndertakeActivity.getActivity());
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

    private String image2Upload;

    /**
     * 处理图片获取后(copy)的事件
     *
     * @param event 图片本地处理完成事件
     */
    public void callResolveEvent(AppEvent.ImageGetEvent event) {
        ITriggerCompare compare = event.getTrigger();
        if (ImageGetTrigger.Capture.equals(compare) || ImageGetTrigger.Album.equals(compare)) {
            image2Upload = event.getPath();
            iUndertakeActivity.showWaitView(true);
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
                    iUndertakeActivity.addWorkPicture(imageFilePath);
                    iUndertakeActivity.cancelWaitView();
                }
            });
            model.doCompress(new File(event.getPath()));
        } else {
            iUndertakeActivity.cancelWaitView();
        }
    }

    private void doUpload(String path) {
        DLog.d(getClass(), "doupload");
        iUndertakeActivity.showWaitView(true);
        IApiRequestModel model = new MediaCenterUploadModel(path, new MediaCenterUploadCallback
                (path));
        model.doRequest(iUndertakeActivity.getActivity());
    }


    /**
     * 投稿-- 悬赏
     *
     * @param task_bn
     * @param description 稿件说明、描述
     * @param isShow      是否显示稿件
     * @param sms_flag    投稿成功之后是否发送站内信
     */
    public void doLetterSubmit(String task_bn, String description, boolean isShow, boolean
            sms_flag) {

        if (!isComplete(task_bn, R.string.v1010_default_undertake_task_bn_isempty)) {
            return;
        }

        if (!isComplete(description, R.string.v1010_default_undertake_description_isempty)) {
            return;
        }

        if (!isComplete(image2Upload, R.string.v1010_default_undertake_attachments_isempty)) {
            return;
        }

        iUndertakeActivity.showWaitView(true);

        letterBean.setUsername(getUsername());
        letterBean.setTask_bn(task_bn);
        letterBean.setDescription(description);

        letterBean.setAttachment_name(getAttachmentName(image2Upload));

        letterBean.setIs_mark(isShow);
        letterBean.setSms_flag(sms_flag);

        doUpload(image2Upload);
    }

    private String getAttachmentName(String path) {
        if (path != null && path.contains("/")) {
            int index = path.lastIndexOf("/") + 1;
            if (index < path.length())
                return path.substring(index);
        }
        return "UnKnown";
    }

    /**
     * 稿件提交--明标
     * days 必须 ,v1041 版本加上报价
     *
     * @param etPrice     明标项目报价，报价区间仅为明标价格的50%—200%中间四舍五入取整数
     * @param task_bn
     * @param description 稿件说明
     * @param days        工期
     * @param isShow      是否显示稿件
     * @param sms_flag    投稿成功之后是否发送站内信
     */
    public void doOpenBidLetterSubmit(Long ownerPrice, String etPrice, String task_bn, String description, String days, boolean isShow,
                                      boolean sms_flag) {
        //报价完整性
        if (!isComplete(etPrice, R.string.v1041_undertakeopenbid_text_otherprice_isempty))
            return;

        //报价合法性校验
        long ep;
        try {
            ep = Long.parseLong(etPrice);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return;
        }

        if (!isPriceLegal(ownerPrice, ep))
            return;

        //逻辑安全校验
        if (!isComplete(task_bn, R.string.v1010_default_undertake_task_bn_isempty))
            return;

        //项目工期完整性、合法性校验
        if (!isComplete(days, R.string.v1010_default_undertake_timecycle_isempty)) {
            return;
        } else if (!isTimeCycleLegal(days)) {
            return;
        }

        //描述完整性校验
        if (!isComplete(description, R.string.v1010_default_undertake_description_isempty))
            return;

        iUndertakeActivity.showWaitView(true);

        letterBean.setUsername(getUsername());
        letterBean.setTask_bn(task_bn);
        //乙方报价
        letterBean.setPrice(String.valueOf(ep));
        letterBean.setDescription(description);
        letterBean.setDays(days);
        letterBean.setIs_mark(isShow);
        letterBean.setSms_flag(sms_flag);

        if (StringUtil.isEmpty(image2Upload)) {
            doUndertake();
        } else {
            letterBean.setAttachment_name(getAttachmentName(image2Upload));
            doUpload(image2Upload);
        }
    }

    private boolean isPriceLegal(Long ownerPrice, long ep) {
        double op = ownerPrice;

        long lowPrice = (long) Math.ceil(op / 2);
        long highPrice = (long) Math.ceil(op * 2);

        //范围：50%（除2再向上取整）--200%
        if (ep < lowPrice | ep > highPrice) {
            DLog.d(Lmsg.class, "报价下限：" + Math.ceil(op / 2));
            DLog.d(Lmsg.class, "报价上限：" + op * 2);

            iUndertakeActivity.showMsg("报价请在" + lowPrice + "元--" + highPrice + "元之间");
            return false;
        }
        return true;
    }

    /**
     * 检查周期是否超过上限
     *
     * @param days
     */
    private boolean isTimeCycleLegal(String days) {
        int timeCycle = Integer.valueOf(days);
        if (timeCycle <= 0) {
            iUndertakeActivity.showMsg(iUndertakeActivity.getActivity().getString(R.string.v1041_undertakeopenbid_toast_timecycle_invalid));
            return false;
        } else if (timeCycle > MAX_CYCLE_VALUE) {
            iUndertakeActivity.showMsg(iUndertakeActivity.getActivity().getString(R.string.v1041_undertakeopenbid_toast_timecycle_out_of_range));
            return false;
        }
        return true;
    }

    private String getUsername() {
        return IVerifyHolder.mLoginInfo.getUsername();
    }

    /**
     * 稿件提交--暗标
     * 天数、价格必须
     *
     * @param task_bn
     * @param description 稿件说明
     * @param days        工期
     * @param price       项目报价
     * @param isShow      是否显示稿件
     * @param sms_flag    投稿成功之后是否发送站内信
     */
    public void doLetterSubmit(String task_bn, String description, String days,
                               String price, boolean isShow, boolean sms_flag) {

        if (!isComplete(price, R.string.v1010_default_undertake_price_isempty))
            return;

        try {
            long ep = Long.parseLong(price);
            if (ep <= 0) {
                iUndertakeActivity.showMsg(iUndertakeActivity.getActivity().getString(R.string.v1041_undertakeopenbid_toast_price_invalid));
                return;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            iUndertakeActivity.showMsg(iUndertakeActivity.getActivity().getString(R.string.v1041_undertakeopenbid_toast_price_invalid));
            return;
        }

        if (!isComplete(days, R.string.v1010_default_undertake_timecycle_isempty))
            return;

        if (!isTimeCycleLegal(days))
            return;

        if (!isComplete(task_bn, R.string.v1010_default_undertake_task_bn_isempty))
            return;

        if (!isComplete(description, R.string.v1010_default_undertake_description_isempty))
            return;

        iUndertakeActivity.showWaitView(true);
        letterBean.setUsername(getUsername());
        letterBean.setTask_bn(task_bn);
        letterBean.setDescription(description);
        letterBean.setDays(days);
        letterBean.setPrice(price);
        letterBean.setIs_mark(isShow);
        letterBean.setSms_flag(sms_flag);

        if (StringUtil.isEmpty(image2Upload)) {
            doUndertake();
        } else {
            letterBean.setAttachment_name(getAttachmentName(image2Upload));
            doUpload(image2Upload);
        }

    }

    private boolean isComplete(String s, int toastResId) {
        if (StringUtil.isEmpty(s)) {
            iUndertakeActivity.showMsg(iUndertakeActivity.getActivity().getString(toastResId));
            return false;
        }
        return true;
    }

    public void removePicture() {
        image2Upload = null;
    }

    private class UndertakeImpl implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iUndertakeActivity.showMsg(iUndertakeActivity.getActivity().getString(R.string
                    .v1010_default_undertake_success));
            iUndertakeActivity.cancelWaitView();
            EventBus.getDefault().post(new AppEvent.UnderTakeSuccessEvent());
            iUndertakeActivity.finishActivity();

        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iUndertakeActivity.cancelWaitView();
            iUndertakeActivity.showMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iUndertakeActivity.cancelWaitView();

            // TODO: 2016/9/13  

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


    private void doUndertake() {
        //统计 投稿成功 -计数
        iUndertakeActivity.reportCountEvent(IUmengEventKey.KEY_TASK_SUBMISSION);
        IApiRequestModel model = new UndertakeModel(letterBean, new UndertakeImpl());
        model.doRequest(iUndertakeActivity.getActivity());
    }

    private class MediaCenterUploadCallback implements ApiModelCallback<MediaCenterUploadResBean> {
        private String path;

        MediaCenterUploadCallback(String path) {
            this.path = path;
        }

        @Override
        public void onSuccess(BaseBeanContainer<MediaCenterUploadResBean> beanContainer) {
            String attachments = beanContainer.getData().getPath();
            letterBean.setAttachments(attachments);
            doUndertake();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iUndertakeActivity.cancelWaitView();
            iUndertakeActivity.showDialog(R.string.v1010_dialog_feedback_content_error_upload,
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
            iUndertakeActivity.cancelWaitView();
        }
    }

    private class TextWatcherModelCallbackImpl implements TextWatcherModel
            .TextWatcherModelCallback {

        @Override
        public void onOverLength(int edittextId, int maxLength) {
            iUndertakeActivity.notifyOverLength();
        }

        @Override
        public void onChanged(int edittextId, int currentCount, int remains) {

        }
    }

}
