package com.lht.cloudjob.mvp.presenter;

import android.content.Context;

import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.R;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.ResetPwdByAccountModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.viewinterface.IResetPwdActivity;
import com.lht.cloudjob.util.CheckPwdUtil;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.lht.cloudjob.util.string.StringUtil;
import com.lht.cloudjob.util.toast.ToastUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> ResetPwdPresenter
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/25.
 */
public class ResetPwdPresenter implements IApiRequestPresenter {

    private IResetPwdActivity iResetPwdActivity;

    public ResetPwdPresenter(IResetPwdActivity iResetPwdActivity) {
        this.iResetPwdActivity = iResetPwdActivity;
    }


    public void callResetPwd(String account, String pwd, String check) {
        if (StringUtil.isEmpty(pwd)) {
            iResetPwdActivity.showErrorMsg(iResetPwdActivity.getActivity().getString(R.string
                    .v1010_default_changepwd_hint_newpwd));
            return;
        }

        if (!pwd.equals(check)) {
            iResetPwdActivity.showErrorMsg(iResetPwdActivity.getActivity().getString(R.string
                    .v1010_default_changepwd_text_newpwd_disaffinity));
            return;
        }

        //校验密码长度
        if(!CheckPwdUtil.checkPwdLengthLegal(pwd, iResetPwdActivity)){
            return;
        }
        iResetPwdActivity.showWaitView(true);

        IApiRequestModel resetPwdModel = new ResetPwdByAccountModel(account, pwd, new
                ResetPwdModelCallback());
        resetPwdModel.doRequest(iResetPwdActivity.getActivity());
    }


    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    private final class ResetPwdModelCallback implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iResetPwdActivity.cancelWaitView();

            ToastUtils.show(iResetPwdActivity.getActivity(), R.string
                    .v1010_default_changepwd_text_resetpwd_success, ToastUtils.Duration.s);

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

//    private void logout() {
//        LoginInfo info = IVerifyHolder.mLoginInfo;
//        String sess = VsoUtil.createVsoSessionCode(info.getUsername(), info.getAccessId());
//        IApiRequestModel model = new LogoutModel(info.getAccessId(), sess,
//                info.getAccessToken(), new LogoutModelCallback());
//        model.doRequest(MainApplication.getOurInstance());
//    }
//
//    private class LogoutModelCallback implements ApiModelCallback<BaseVsoApiResBean> {
//
//        @Override
//        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
//            //empty
//        }
//
//        @Override
//        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
//            //empty
//        }
//
//        @Override
//        public void onHttpFailure(int httpStatus) {
//            //empty
//        }
//    }

}
