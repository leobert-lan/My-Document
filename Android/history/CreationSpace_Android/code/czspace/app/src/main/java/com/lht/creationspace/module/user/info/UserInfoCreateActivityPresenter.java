package com.lht.creationspace.module.user.info;

import android.content.Context;
import android.widget.EditText;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.checkable.jobs.NicknameCheckJob;
import com.lht.creationspace.customview.popup.CustomPopupWindow;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.base.model.apimodel.ApiModelCallback;
import com.lht.creationspace.module.user.info.model.InfoModifyModel;
import com.lht.creationspace.module.user.info.model.IntroduceModifyModel;
import com.lht.creationspace.module.pub.model.MediaCenterUploadModel;
import com.lht.creationspace.base.model.apimodel.RestfulApiModelCallback;
import com.lht.creationspace.base.model.TextWatcherModel;
import com.lht.creationspace.base.model.apimodel.BaseBeanContainer;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.module.user.info.model.pojo.BasicInfoResBean;
import com.lht.creationspace.module.user.login.model.pojo.LoginResBean;
import com.lht.creationspace.module.pub.model.pojo.MediaCenterUploadResBean;
import com.lht.creationspace.base.model.pojo.LoginInfo;
import com.lht.creationspace.module.user.info.ui.IUserInfoCreateActivity;
import com.lht.creationspace.util.debug.DLog;
import com.lht.creationspace.util.internet.HttpUtil;
import com.lht.creationspace.util.string.StringUtil;
import com.yalantis.ucrop.entity.LocalMedia;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import thirdparty.leobert.pvselectorlib.PVSelector;
import thirdparty.leobert.pvselectorlib.model.PictureConfig;

/**
 * 用户信息填写页面
 * Created by chhyu on 2017/2/20.
 */
public class UserInfoCreateActivityPresenter implements IApiRequestPresenter {

    private IUserInfoCreateActivity iUserInfoCreateActivity;

    private InfoModifyModel infoModifyModel;

    private final TextWatcherModel textWatcherModel;

    /**
     * 保密
     */
    public static final int SEX_UNSPECIFIED = 2;

    private UserinfoModifyModelCallback userinfoModifyModelCallback;

    public UserInfoCreateActivityPresenter(IUserInfoCreateActivity iUserInfoCreateActivity) {
        this.iUserInfoCreateActivity = iUserInfoCreateActivity;

        userinfoModifyModelCallback = new UserinfoModifyModelCallback();
        infoModifyModel = new InfoModifyModel(IVerifyHolder.mLoginInfo.getUsername(),
                null);

        textWatcherModel = new TextWatcherModel(new TextWatcherModelCallbackImpl());
    }

    public void watchText(EditText editText, int maxLength) {
        textWatcherModel.doWatcher(editText, maxLength);
    }

    private class TextWatcherModelCallbackImpl
            implements TextWatcherModel.TextWatcherModelCallback {

        @Override
        public void onOverLength(int edittextId, int maxLength) {
            iUserInfoCreateActivity.showMsg("最大长度：" + maxLength);
        }

        @Override
        public void onChanged(int edittextId, int currentCount, int remains) {
        }
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    public void callInfoCreate(int sex, String nickname, String introduce) {
        //头像必选，昵称必选<= 20 简介无所谓，性别无所谓
        if (!avatarModified) {
            iUserInfoCreateActivity.showMsg(
                    iUserInfoCreateActivity.getAppResource()
                            .getString(R.string.v1000_toast_error_nullavatar));
            return;
        }

        NicknameCheckJob nicknameCheckJob = new NicknameCheckJob(nickname);
        if (!nicknameCheckJob.check()) { //昵称规则校验不通过，内部处理提示
            return;
        }

        iUserInfoCreateActivity.showWaitView(true);

        LoginInfo loginInfo = IVerifyHolder.mLoginInfo;
        LoginResBean userInfoBean = loginInfo.getLoginResBean();

        infoModifyModel.modifyNickname(nickname);
        loginInfo.setNickname(nickname);
        userInfoBean.setNickname(nickname);

        AppEvent.UserInfoUpdatedEvent event =
                new AppEvent.UserInfoUpdatedEvent(
                        BasicInfoResBean.transFromLoginResBean(userInfoBean));


        if (sex != 2) {
            infoModifyModel.modifySex(sex == 0);
            //不需要本地修改性别
        }

        //不需要本地修改简介
        if (StringUtil.isEmpty(introduce)) {
            userinfoModifyModelCallback.setEvent(event);
            infoModifyModel.doRequest(iUserInfoCreateActivity.getActivity(),
                    userinfoModifyModelCallback);
        } else {
            event.setBrief(introduce);
            userinfoModifyModelCallback.setEvent(event);

            //修改简介
            IntroduceModifyModel.IntroduceModifyData data = new IntroduceModifyModel.IntroduceModifyData();
            data.setUser(loginInfo.getUsername());
            data.setIntroduce(introduce);
            IntroduceModifyModel model = new IntroduceModifyModel(data, new IntroModifyModelCallback());
            model.doRequest(iUserInfoCreateActivity.getActivity());

        }

//        EventBus.getDefault().post(event);
    }

    /**
     * 选取图片
     */
    public void callSelectAvatar() {
        PVSelector.getPhotoSelector(iUserInfoCreateActivity.getActivity())
                .enableCamera().useSystemCompressOnCrop(true, false)
                .singleSelect().enablePreview().launch(new PictureConfig.OnSelectResultCallback() {
            @Override
            public void onSelectSuccess(List<LocalMedia> resultList) {
                if (resultList == null || resultList.isEmpty()) {
                    DLog.e(getClass(), "选取有bug");
                    return;
                }
                iUserInfoCreateActivity.showWaitView(true);
                doUpload(resultList.get(0));
            }
        });
    }

    private void doUpload(LocalMedia localMedia) {
        iUserInfoCreateActivity.showWaitView(true);
        MediaCenterUploadModel.MediaCenterUploadData data = new MediaCenterUploadModel.MediaCenterUploadData();
        data.setFilePath(localMedia.getCompressPath());
        data.setType(MediaCenterUploadModel.TYPE_AVATAR_ATTACHMENT);
        MediaCenterUploadModel model = new MediaCenterUploadModel(data, new UploadModelCallback(localMedia));
        model.doRequest(iUserInfoCreateActivity.getActivity());
    }

    /**
     * 用户信息修改接口
     */
    private class UserinfoModifyModelCallback
            implements ApiModelCallback<BasicInfoResBean> {

        private AppEvent.UserInfoUpdatedEvent event;

        public void setEvent(AppEvent.UserInfoUpdatedEvent event) {
            this.event = event;
        }

        @Override
        public void onSuccess(BaseBeanContainer<BasicInfoResBean> beanContainer) {
            iUserInfoCreateActivity.cancelWaitView();

            BasicInfoResBean resBean = beanContainer.getData();

            if (resBean != null && !StringUtil.isEmpty(resBean.getAvatar())) {
                event.getBasicInfoResBean().setAvatar(resBean.getAvatar());
            }
            EventBus.getDefault().post(event);
            //成功后跳转到角色选择
            iUserInfoCreateActivity.jump2RoleSelect();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iUserInfoCreateActivity.cancelWaitView();
            iUserInfoCreateActivity.showMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iUserInfoCreateActivity.cancelWaitView();
        }
    }

    /**
     * 简介修改接口回调
     */
    private class IntroModifyModelCallback
            implements RestfulApiModelCallback<String> {

        @Override
        public void onSuccess(String bean) {
            //避免修改个人信息有误后修改导致简介没有被上传，所以不做减少访问的策略
//            iUserInfoCreateActivity.cancelWaitView();
            iUserInfoCreateActivity.showWaitView(true);
            infoModifyModel.doRequest(iUserInfoCreateActivity.getActivity(),
                    userinfoModifyModelCallback);
        }

        @Override
        public void onFailure(int restCode, String msg) {
            iUserInfoCreateActivity.cancelWaitView();
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iUserInfoCreateActivity.cancelWaitView();
        }
    }

    private boolean avatarModified;

    private class UploadModelCallback implements
            RestfulApiModelCallback<MediaCenterUploadResBean> {
        private final LocalMedia localMedia;

        UploadModelCallback(LocalMedia localMedia) {
            this.localMedia = localMedia;
        }

        @Override
        public void onSuccess(MediaCenterUploadResBean bean) {
            iUserInfoCreateActivity.cancelWaitView();// no async,so cancel
            infoModifyModel.modifyAvatar(bean.getVcs_url());
            iUserInfoCreateActivity.updateLocalAvatar(bean.getVcs_url());
            //不需要本地更新头像
            avatarModified = true;
        }

        @Override
        public void onFailure(int restCode, String msg) {
            iUserInfoCreateActivity.cancelWaitView();
            iUserInfoCreateActivity.showDialog(R.string.v1010_dialog_feedback_content_error_upload,
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
            iUserInfoCreateActivity.cancelWaitView();
            iUserInfoCreateActivity.showDialog(R.string.v1010_dialog_feedback_content_error_upload,
                    R.string.v1010_dialog_feedback_positive_reupload,
                    new CustomPopupWindow.OnPositiveClickListener() {
                        @Override
                        public void onPositiveClick() {
                            doUpload(localMedia);
                        }
                    });
        }
    }

}
