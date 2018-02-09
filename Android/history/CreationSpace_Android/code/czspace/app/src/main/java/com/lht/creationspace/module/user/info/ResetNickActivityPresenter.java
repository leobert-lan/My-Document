package com.lht.creationspace.module.user.info;

import android.content.Context;
import android.widget.EditText;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.checkable.jobs.NicknameCheckJob;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.base.model.apimodel.ApiModelCallback;
import com.lht.creationspace.module.user.security.model.CheckNicknameModel;
import com.lht.creationspace.module.user.info.model.InfoModifyModel;
import com.lht.creationspace.base.model.TextWatcherModel;
import com.lht.creationspace.base.model.apimodel.BaseBeanContainer;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.module.user.info.ui.IResetNicknameActivity;
import com.lht.creationspace.util.internet.HttpUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.presenter
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> ResetNickActivityPresenter
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/27.
 */
public class ResetNickActivityPresenter implements IApiRequestPresenter {

    private IResetNicknameActivity iResetNicknameActivity;

    private InfoModifyModel infoModifyModel;

    private IApiRequestModel checkNicknameModel;
    private String nickname;

    public ResetNickActivityPresenter(IResetNicknameActivity iResetNicknameActivity) {
        this.iResetNicknameActivity = iResetNicknameActivity;
        textWatcherModel = new TextWatcherModel(new TextWatcherCallback());
    }

    private String username;

    private TextWatcherModel textWatcherModel;

    public void callModifyNickname(String username, String nickname) {
        this.username = username;
        this.nickname = nickname;
        NicknameCheckJob nicknameCheckJob = new NicknameCheckJob(nickname);
        if (!nicknameCheckJob.check()) {
//            ToastUtils.show(MainApplication.getOurInstance(), "昵称只能是汉字、字母（含大小写）、数字、空格及其组合，最长20个字符", ToastUtils.Duration.s);
//            iResetNicknameActivity.cancelWaitView();
            return;
        }
        iResetNicknameActivity.showWaitView(true);
        checkNicknameModel = new CheckNicknameModel(nickname, new CheckNicknameModelCallback());
        checkNicknameModel.doRequest(iResetNicknameActivity.getActivity());
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    public void watchEditLength(EditText etNickname, int maxNickLength) {
        textWatcherModel.doWatcher(etNickname, maxNickLength);
    }

    private class CheckNicknameModelCallback
            implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            doNicknameModify(username, nickname);
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iResetNicknameActivity.cancelWaitView();
            iResetNicknameActivity.showErrorMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iResetNicknameActivity.cancelWaitView();

            // TODO: 2016/7/26
        }
    }

    private void doNicknameModify(String username, String nickname) {
        iResetNicknameActivity.showWaitView(true);
        infoModifyModel = new InfoModifyModel(username, new ModifyInfoModelCallback());
        infoModifyModel.modifyNickname(nickname);

        infoModifyModel.doRequest(iResetNicknameActivity.getActivity());
    }

    private class ModifyInfoModelCallback implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            AppEvent.ModifyNicknameEvent event = new AppEvent.ModifyNicknameEvent();
            event.setNickname(nickname);
            EventBus.getDefault().post(event);

            iResetNicknameActivity.showMsg(beanContainer.getData().getMessage());
            iResetNicknameActivity.getActivity().finish();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iResetNicknameActivity.cancelWaitView();
            iResetNicknameActivity.showErrorMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iResetNicknameActivity.cancelWaitView();

            // TODO: 2016/7/26
        }
    }

    private class TextWatcherCallback implements TextWatcherModel.TextWatcherModelCallback {

        @Override
        public void onOverLength(int edittextId, int maxLength) {
            iResetNicknameActivity.showErrorMsg(iResetNicknameActivity.getAppResource().getString
                    (R.string.v1000_toast_warm_enter_text_overlength));
        }

        @Override
        public void onChanged(int edittextId, int currentCount, int remains) {

        }
    }
}
