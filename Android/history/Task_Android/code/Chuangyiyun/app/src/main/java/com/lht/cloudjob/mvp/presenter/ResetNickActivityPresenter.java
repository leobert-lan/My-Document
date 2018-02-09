package com.lht.cloudjob.mvp.presenter;

import android.content.Context;
import android.widget.EditText;

import com.lht.cloudjob.R;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.CheckNicknameModel;
import com.lht.cloudjob.mvp.model.InfoModifyModel;
import com.lht.cloudjob.mvp.model.TextWatcherModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.viewinterface.IResetNicknameActivity;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.lht.cloudjob.util.string.StringUtil;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
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
        iResetNicknameActivity.showWaitView(true);
        this.nickname = nickname;
        if (StringUtil.isEmpty(nickname)) {
            iResetNicknameActivity.showErrorMsg(iResetNicknameActivity.getActivity().getString(R
                    .string.v1010_default_resetnick_enter_nickname));
            iResetNicknameActivity.cancelWaitView();
            return;
        } else if (nickname.length() > 20) {
            iResetNicknameActivity.showErrorMsg(iResetNicknameActivity.getActivity().getString(R
                    .string.v1010_default_resetnick_nicklenth_over20));
            iResetNicknameActivity.cancelWaitView();
            return;
        }

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

    private class CheckNicknameModelCallback implements ApiModelCallback<BaseVsoApiResBean> {

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
        infoModifyModel = new InfoModifyModel(username, new ModifyInfoModelCallback());
        infoModifyModel.modifyNickname(nickname);
        infoModifyModel.doRequest(iResetNicknameActivity.getActivity());
    }

    private class ModifyInfoModelCallback implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
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
                    (R.string.v1010_toast_warm_enter_text_overlength));
        }

        @Override
        public void onChanged(int edittextId, int currentCount, int remains) {

        }
    }
}
