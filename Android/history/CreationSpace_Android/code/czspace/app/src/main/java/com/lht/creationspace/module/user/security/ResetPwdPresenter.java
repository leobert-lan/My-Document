package com.lht.creationspace.module.user.security;

import android.content.Context;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.base.model.apimodel.ApiModelCallback;
import com.lht.creationspace.checkable.AbsOnAllCheckLegalListener;
import com.lht.creationspace.checkable.CheckableJobs;
import com.lht.creationspace.checkable.jobs.CellPhoneCheckJob;
import com.lht.creationspace.checkable.jobs.PasswordCheckJob;
import com.lht.creationspace.checkable.jobs.UnCompetedInputCheckJob;
import com.lht.creationspace.module.user.security.model.ResetPwdByAccountModel;
import com.lht.creationspace.base.model.apimodel.BaseBeanContainer;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.module.user.security.ui.IResetPwdActivity;
import com.lht.creationspace.util.CheckPwdUtil;
import com.lht.creationspace.util.internet.HttpUtil;
import com.lht.creationspace.util.string.StringUtil;
import com.lht.creationspace.util.toast.ToastUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.presenter
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> ResetPwdPresenter
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/25.
 */
public class ResetPwdPresenter implements IApiRequestPresenter {

    private IResetPwdActivity iResetPwdActivity;

    public ResetPwdPresenter(IResetPwdActivity iResetPwdActivity) {
        this.iResetPwdActivity = iResetPwdActivity;
    }


    public void callResetPwd(String account, String pwd, String validCode) {
        ResetPwdByAccountModel.ModelRequestData data =
                new ResetPwdByAccountModel.ModelRequestData(account, pwd, validCode);

        CheckableJobs.getInstance()
                .next(new UnCompetedInputCheckJob(pwd,
                        R.string.v1000_default_changepwd_hint_newpwd,
                        iResetPwdActivity))
                .next(new PasswordCheckJob(pwd, iResetPwdActivity))
                .onAllCheckLegal(new AbsOnAllCheckLegalListener<ResetPwdByAccountModel.ModelRequestData>(data) {
                    @Override
                    public void onAllCheckLegal() {
                        iResetPwdActivity.showWaitView(true);
                        IApiRequestModel resetPwdModel =
                                new ResetPwdByAccountModel(getSavedParam(), new ResetPwdModelCallback());
                        resetPwdModel.doRequest(iResetPwdActivity.getActivity());
                    }
                }).start();
    }


    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    private final class ResetPwdModelCallback
            implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iResetPwdActivity.cancelWaitView();

            ToastUtils.show(iResetPwdActivity.getActivity(), R.string
                    .v1000_default_changepwd_text_resetpwd_success, ToastUtils.Duration.s);

            //logout on service
//            logout();

            iResetPwdActivity.getActivity().finish();
            EventBus.getDefault().post(new AppEvent.PwdResettedEvent());
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iResetPwdActivity.cancelWaitView();
            iResetPwdActivity.showErrorMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iResetPwdActivity.cancelWaitView();
        }
    }

}
