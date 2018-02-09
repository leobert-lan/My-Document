package com.lht.creationspace.module.user.info;

import android.content.Context;
import android.widget.EditText;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.model.TextWatcherModel;
import com.lht.creationspace.base.model.apimodel.ApiModelCallback;
import com.lht.creationspace.base.model.apimodel.BaseBeanContainer;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.base.model.apimodel.RestfulApiModelCallback;
import com.lht.creationspace.customview.popup.CustomPopupWindow;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.module.pub.model.MediaCenterUploadModel;
import com.lht.creationspace.module.user.info.model.InfoModifyModel;
import com.lht.creationspace.module.user.info.model.IntroduceModifyModel;
import com.lht.creationspace.module.user.info.model.IntroduceQueryModel;
import com.lht.creationspace.module.user.info.model.QueryBasicInfoModel;
import com.lht.creationspace.module.user.info.ui.IPersonalInfoActivity;
import com.lht.creationspace.module.user.info.ui.ac.ResetNicknameActivity;
import com.lht.creationspace.module.user.info.model.pojo.BasicInfoResBean;
import com.lht.creationspace.module.pub.model.pojo.MediaCenterUploadResBean;
import com.lht.creationspace.util.debug.DLog;
import com.lht.creationspace.util.internet.HttpUtil;
import com.yalantis.ucrop.entity.LocalMedia;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import individual.leobert.uilib.actionsheet.OnActionSheetItemClickListener;
import thirdparty.leobert.pvselectorlib.PVSelector;
import thirdparty.leobert.pvselectorlib.model.PictureConfig;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.presenter
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> PersonalInfoActivityPresenter
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/11.
 */
public class PersonalInfoActivityPresenter implements IApiRequestPresenter {

    private IPersonalInfoActivity iPersonalInfoActivity;

    private IApiRequestModel basicInfoModel;

    private InfoModifyModel infoModifyModel;
    private final TextWatcherModel textWatcherModel;


    public PersonalInfoActivityPresenter(IPersonalInfoActivity iPersonalInfoActivity) {
        this.iPersonalInfoActivity = iPersonalInfoActivity;
        textWatcherModel = new TextWatcherModel(new TextWatcherModelCallbackImpl());
    }

    public void watchText(EditText editText, int maxLength) {
        textWatcherModel.doWatcher(editText, maxLength);
    }

    private String oldIntroduce;

    /**
     * 简介
     *
     * @param newIntroduce
     */
    public void callModifyIntroduce(String newIntroduce) {
        iPersonalInfoActivity.showWaitView(true);
        IntroduceModifyModel.IntroduceModifyData data = new IntroduceModifyModel.IntroduceModifyData();
        data.setUser(IVerifyHolder.mLoginInfo.getUsername());
        data.setIntroduce(newIntroduce);
        IntroduceModifyModel model =
                new IntroduceModifyModel(data, new IntroModifyModelCallback(newIntroduce));
        model.doRequest(iPersonalInfoActivity.getActivity());
    }

    public void jump2ModifyNiclname() {
        ResetNicknameActivity.getLauncher(iPersonalInfoActivity.getActivity())
                .launch();
    }


    private class TextWatcherModelCallbackImpl
            implements TextWatcherModel.TextWatcherModelCallback {

        @Override
        public void onOverLength(int edittextId, int maxLength) {
            iPersonalInfoActivity.showMsg(iPersonalInfoActivity.getActivity().getString(R.string.v1000_default_personalinfo_enter_text_toolong));
        }

        @Override
        public void onChanged(int edittextId, int currentCount, int remains) {
            iPersonalInfoActivity.updateCurrentnum(edittextId, currentCount, remains);
            if (currentCount == 0)
                iPersonalInfoActivity.setIntroCompleteEnable(false);

            if (iPersonalInfoActivity.getIntro().equalsIgnoreCase(oldIntroduce))
                iPersonalInfoActivity.setIntroCompleteEnable(false);
            else
                iPersonalInfoActivity.setIntroCompleteEnable(true);
        }
    }

    private void callGetInfo(String username, boolean isInit) {
        iPersonalInfoActivity.showWaitView(true);
        basicInfoModel = new QueryBasicInfoModel(username, new BasicInfoModelCallback(isInit));
        basicInfoModel.doRequest(iPersonalInfoActivity.getActivity());
    }

    private void callGetIntro(String username) {
        iPersonalInfoActivity.showWaitView(true);
        IntroduceQueryModel model = new IntroduceQueryModel(username,
                new IntroQueryModelCallback());
        model.doRequest(iPersonalInfoActivity.getActivity());

    }

    public void init(String username) {
        callGetInfo(username, true);
        callGetIntro(username);
    }

    public void callModifyAvatar() {
        PVSelector.getPhotoSelector(iPersonalInfoActivity.getActivity())
                .enableCamera().singleSelect()
                .useSystemCompressOnCrop(true, false)
                .launch(new PictureConfig.OnSelectResultCallback() {
                    @Override
                    public void onSelectSuccess(List<LocalMedia> resultList) {
                        if (resultList == null || resultList.isEmpty()) {
                            DLog.e(getClass(), "选取有bug");
                            return;
                        }
                        iPersonalInfoActivity.showWaitView(true);
                        doUpload(resultList.get(0));
                    }
                });
    }


    public void callModifyNickname() {
        iPersonalInfoActivity.jumpToModifyNickname();
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

    private BasicInfoResBean basicInfoResBean;

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    private String getUsername() {
        return IVerifyHolder.mLoginInfo.getUsername();
    }


    private void doAvatarModify(String path) {
        iPersonalInfoActivity.showWaitView(true);

        AvatarPlaceHolder.setOnModify(true);

        infoModifyModel = new InfoModifyModel(getUsername(),
                new ModifyInfoModelCallback());
        infoModifyModel.modifyAvatar(path);
        infoModifyModel.doRequest(iPersonalInfoActivity.getActivity());
    }

    private void doUpload(LocalMedia localMedia) {
        iPersonalInfoActivity.showWaitView(true);
        MediaCenterUploadModel.MediaCenterUploadData data = new MediaCenterUploadModel.MediaCenterUploadData();
        data.setFilePath(localMedia.getCompressPath());
        data.setType(MediaCenterUploadModel.TYPE_AVATAR_ATTACHMENT);
        MediaCenterUploadModel model = new MediaCenterUploadModel(data, new UploadModelCallback(localMedia));
        model.doRequest(iPersonalInfoActivity.getActivity());
    }

    private class UploadModelCallback implements
            RestfulApiModelCallback<MediaCenterUploadResBean> {
        private final LocalMedia localMedia;

        UploadModelCallback(LocalMedia localMedia) {
            this.localMedia = localMedia;
        }

        @Override
        public void onSuccess(MediaCenterUploadResBean bean) {
            iPersonalInfoActivity.showWaitView(true);
            doAvatarModify(bean.getVcs_url());
        }

        @Override
        public void onFailure(int restCode, String msg) {
            iPersonalInfoActivity.cancelWaitView();
            iPersonalInfoActivity.showDialog(R.string.v1010_dialog_feedback_content_error_upload,
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
            iPersonalInfoActivity.cancelWaitView();
            iPersonalInfoActivity.showDialog(R.string.v1010_dialog_feedback_content_error_upload,
                    R.string.v1010_dialog_feedback_positive_reupload,
                    new CustomPopupWindow.OnPositiveClickListener() {
                        @Override
                        public void onPositiveClick() {
                            doUpload(localMedia);
                        }
                    });
        }
    }

    private final class BasicInfoModelCallback
            implements ApiModelCallback<BasicInfoResBean> {
        private boolean isInit;

        public BasicInfoModelCallback(boolean isInit) {
            this.isInit = isInit;
        }

        @Override
        public void onSuccess(BaseBeanContainer<BasicInfoResBean> beanContainer) {
            iPersonalInfoActivity.cancelWaitView();
            basicInfoResBean = beanContainer.getData();
            if (AvatarPlaceHolder.isOnModify()) {
                String newAvatar = beanContainer.getData().getAvatar();
//                AvatarPlaceHolder.setNewPath(newAvatar);
                //清理老头像cache
//                Picasso.with(iPersonalInfoActivity.getActivity()).invalidateMemoryAndDisk
//                        (IVerifyHolder.mLoginInfo.getAvatar());
                IVerifyHolder.mLoginInfo.setAvatar(newAvatar);
                AvatarPlaceHolder.setOnModify(false);
                AvatarPlaceHolder.markModifySuccess();
            }
            if (isInit)
                iPersonalInfoActivity.loadAvatar(beanContainer.getData().getAvatar());
            iPersonalInfoActivity.updateUserInfo(basicInfoResBean);
            EventBus.getDefault().post(new AppEvent.UserInfoUpdatedEvent(basicInfoResBean));
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iPersonalInfoActivity.cancelWaitView();
            iPersonalInfoActivity.showErrorMsg(beanContainer.getData().getMessage());
            AvatarPlaceHolder.setOnModify(false);
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iPersonalInfoActivity.cancelWaitView();
            AvatarPlaceHolder.setOnModify(false);
        }
    }

    private void doSexModify(boolean isMale) {
        iPersonalInfoActivity.showWaitView(true);
        infoModifyModel = new InfoModifyModel(IVerifyHolder.mLoginInfo.getUsername(), new ModifyInfoModelCallback());
        infoModifyModel.modifySex(isMale);
        infoModifyModel.doRequest(iPersonalInfoActivity.getActivity());
    }

    private class ModifyInfoModelCallback
            implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            //清理延迟到获取新的信息之后
            iPersonalInfoActivity.showMsg(beanContainer.getData().getMessage());
            callGetInfo(IVerifyHolder.mLoginInfo.getUsername(), false);
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


    public static class AvatarPlaceHolder {
        private static boolean onModify = false;

        private static boolean modifySuccess = false;

        public static void markModifySuccess() {
            modifySuccess = true;
        }

        public static boolean isOnModify() {
            return onModify;
        }

        public static void setOnModify(boolean onModify) {
            AvatarPlaceHolder.onModify = onModify;
            if (onModify)
                modifySuccess = false;
        }

        public static boolean hasModified() {
            return modifySuccess;
        }
    }

    /**
     * 简介修改接口回调
     */
    private class IntroModifyModelCallback
            implements RestfulApiModelCallback<String> {

        private final String intro;

        public IntroModifyModelCallback(String intro) {
            this.intro = intro;
        }

        @Override
        public void onSuccess(String bean) {
            iPersonalInfoActivity.cancelWaitView();
            oldIntroduce = intro;
            EventBus.getDefault().post(new AppEvent.BriefSetEvent(oldIntroduce));
            iPersonalInfoActivity.setIntroCompleteEnable(false);
        }

        @Override
        public void onFailure(int restCode, String msg) {
            iPersonalInfoActivity.cancelWaitView();
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iPersonalInfoActivity.cancelWaitView();
        }
    }

    private final class IntroQueryModelCallback
            implements RestfulApiModelCallback<IntroduceQueryModel.ModelResBean> {

        @Override
        public void onSuccess(IntroduceQueryModel.ModelResBean bean) {
            iPersonalInfoActivity.cancelWaitView();
            oldIntroduce = bean.getBrief();
            iPersonalInfoActivity.updateUserIntroduce(oldIntroduce);
        }

        @Override
        public void onFailure(int restCode, String msg) {
            iPersonalInfoActivity.cancelWaitView();
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iPersonalInfoActivity.cancelWaitView();
        }
    }


}