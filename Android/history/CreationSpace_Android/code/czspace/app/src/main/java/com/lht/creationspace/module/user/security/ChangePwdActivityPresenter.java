package com.lht.creationspace.module.user.security;

import android.content.Context;
import android.content.res.Resources;

import com.lht.creationspace.base.MainApplication;
import com.lht.creationspace.R;
import com.lht.creationspace.checkable.AbsCheckJob;
import com.lht.creationspace.checkable.AbsOnAllCheckLegalListener;
import com.lht.creationspace.checkable.CheckableJobs;
import com.lht.creationspace.checkable.jobs.EqualCheckJob;
import com.lht.creationspace.checkable.jobs.NotEqualCheckJob;
import com.lht.creationspace.checkable.jobs.PasswordCheckJob;
import com.lht.creationspace.checkable.jobs.UnCompetedInputCheckJob;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.base.model.apimodel.ApiModelCallback;
import com.lht.creationspace.module.user.security.model.ChangePwdModel;
import com.lht.creationspace.module.user.login.model.LogoutModel;
import com.lht.creationspace.base.model.apimodel.BaseBeanContainer;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.module.user.security.ui.IChangePwdActivity;
import com.lht.creationspace.util.CheckPwdUtil;
import com.lht.creationspace.util.internet.HttpUtil;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.presenter
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> ChangePwdActivityPresenter
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/8/5.
 */
public class ChangePwdActivityPresenter implements IApiRequestPresenter {

    private IChangePwdActivity iChangePwdActivity;

    private IApiRequestModel changePwdModel;

    public ChangePwdActivityPresenter(IChangePwdActivity iChangePwdActivity) {
        this.iChangePwdActivity = iChangePwdActivity;
    }

    public void callChangePwd(boolean isnewpwd, String usr, String oldPwd, String newPwd, String check) {

        ChangePwdModel.ChangePwdData data = new ChangePwdModel.ChangePwdData();
        data.setUsername(usr);
        data.setOldPwd(oldPwd);
        data.setNewPwd(newPwd);


        CheckableJobs.getInstance()
                //手机非空
                .next(new UnCompetedInputCheckJob(oldPwd,
                        R.string.v1000_default_changepwd_empty_oldpwd, iChangePwdActivity))
                //验证码非空
                .next(new UnCompetedInputCheckJob(newPwd,
                        R.string.v1000_default_changepwd_hint_newpwd, iChangePwdActivity))
                //新密码二次验证 非空
                .next(new UnCompetedInputCheckJob(check,
                        R.string.v1000_default_changepwd_hint_checkpwd, iChangePwdActivity))
                //校验不一致，需要一致
                .next(new EqualCheckJob(newPwd,check,
                        R.string.v1000_default_changepwd_text_newpwd_disaffinity,
                        iChangePwdActivity))
                //新旧密码一致校验，需要不一致
                .next(new NotEqualCheckJob(oldPwd,newPwd,
                        R.string.v1000_default_changepwd_text_pwdissame,
                        iChangePwdActivity))
                //密码规则校验
                .next(new PasswordCheckJob(newPwd, iChangePwdActivity))
                .onAllCheckLegal(new AbsOnAllCheckLegalListener<ChangePwdModel.ChangePwdData>(data) {
                    @Override
                    public void onAllCheckLegal() {
                        iChangePwdActivity.showWaitView(true);

                        changePwdModel = new ChangePwdModel(getSavedParam(), new ChangePwdModelCallback());
                        changePwdModel.doRequest(iChangePwdActivity.getActivity());
                    }
                }).start();
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    private Resources getResource() {
        return iChangePwdActivity.getAppResource();
    }

    class ChangePwdModelCallback implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iChangePwdActivity.cancelWaitView();
            iChangePwdActivity.showMsg(iChangePwdActivity.getActivity().getString(R.string.v1000_default_changepwd_text_changepwd_success));
            logout();
            iChangePwdActivity.onResetSuccess();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iChangePwdActivity.cancelWaitView();
            iChangePwdActivity.showErrorMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iChangePwdActivity.cancelWaitView();

            // TODO: 2016/8/5
        }
    }

    private void logout() {
        IApiRequestModel model = new LogoutModel(new LogoutModelCallback());
        model.doRequest(MainApplication.getOurInstance());
    }

    private class LogoutModelCallback implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            //empty
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            //empty
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            //empty
        }
    }
}
