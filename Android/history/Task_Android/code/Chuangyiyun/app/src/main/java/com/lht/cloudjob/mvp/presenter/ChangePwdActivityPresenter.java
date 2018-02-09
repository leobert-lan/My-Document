package com.lht.cloudjob.mvp.presenter;

import android.content.Context;
import android.content.res.Resources;

import com.lht.cloudjob.MainApplication;
import com.lht.cloudjob.R;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.ChangePwdModel;
import com.lht.cloudjob.mvp.model.LogoutModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.pojo.LoginInfo;
import com.lht.cloudjob.mvp.viewinterface.IChangePwdActivity;
import com.lht.cloudjob.util.CheckPwdUtil;
import com.lht.cloudjob.util.VsoUtil;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.lht.cloudjob.util.string.StringUtil;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
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

    public void callChangePwd(String usr, String oldPwd, String newPwd, String check) {
        if (StringUtil.isEmpty(oldPwd)) {
            iChangePwdActivity.showErrorMsg(getResource().getString(R.string.v1010_default_changepwd_hint_oldpwd));
            return;
        }
        if (StringUtil.isEmpty(newPwd)) {
            iChangePwdActivity.showErrorMsg(getResource().getString(R.string.v1010_default_changepwd_hint_newpwd));
            return;
        }
        if (StringUtil.isEmpty(check)) {
            iChangePwdActivity.showErrorMsg(getResource().getString(R.string.v1010_default_changepwd_hint_checkpwd));
            return;
        }
        if (!newPwd.equals(check)) {
            iChangePwdActivity.showErrorMsg(getResource().getString(R.string.v1010_default_changepwd_text_newpwd_disaffinity));
            return;
        }
        if (oldPwd.equals(newPwd)) {
            iChangePwdActivity.showErrorMsg(getResource().getString(R.string.v1010_default_changepwd_text_pwdissame));
            return;
        }
        //校验密码长度
        if(!CheckPwdUtil.checkPwdLengthLegal(newPwd, iChangePwdActivity)){
            return;
        }
        iChangePwdActivity.showWaitView(true);

        changePwdModel = new ChangePwdModel(usr, oldPwd, newPwd, new ChangePwdModelCallback());
        changePwdModel.doRequest(iChangePwdActivity.getActivity());
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
            iChangePwdActivity.showMsg(iChangePwdActivity.getActivity().getString(R.string.v1010_default_changepwd_text_changepwd_success));
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
        LoginInfo info = IVerifyHolder.mLoginInfo;
        String sess = VsoUtil.createVsoSessionCode(info.getUsername(), info.getAccessId());
        IApiRequestModel model = new LogoutModel(info.getAccessId(), sess,
                info.getAccessToken(), new LogoutModelCallback());
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
