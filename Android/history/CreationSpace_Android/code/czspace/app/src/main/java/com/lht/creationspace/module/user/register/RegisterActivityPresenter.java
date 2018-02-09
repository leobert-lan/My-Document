package com.lht.creationspace.module.user.register;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.base.MainApplication;
import com.lht.creationspace.R;
import com.lht.creationspace.checkable.jobs.CellPhoneCheckJob;
import com.lht.creationspace.module.user.info.ui.ac.UserInfoCreateActivity;
import com.lht.creationspace.module.pub.ui.UserAgreementActivity;
import com.lht.creationspace.checkable.AbsCheckJob;
import com.lht.creationspace.checkable.CheckableJobs;
import com.lht.creationspace.checkable.jobs.PasswordCheckJob;
import com.lht.creationspace.checkable.jobs.UnCompetedInputCheckJob;
import com.lht.creationspace.cfg.IPublicConst;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.cfg.SPConstants;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.umeng.IUmengEventKey;
import com.lht.creationspace.base.model.apimodel.ApiModelCallback;
import com.lht.creationspace.module.user.security.model.CheckPhoneModel;
import com.lht.creationspace.module.user.login.model.LoginModel;
import com.lht.creationspace.module.user.register.model.RegisterModel;
import com.lht.creationspace.module.user.security.model.SendSmsModel;
import com.lht.creationspace.base.model.TextWatcherModel;
import com.lht.creationspace.base.model.TimerClockModel;
import com.lht.creationspace.base.model.apimodel.BaseBeanContainer;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.module.user.login.model.pojo.LoginResBean;
import com.lht.creationspace.module.user.register.model.pojo.RegisterResBean;
import com.lht.creationspace.base.model.pojo.LoginInfo;
import com.lht.creationspace.module.user.register.ui.IRegisterActivity;
import com.lht.creationspace.util.SPUtil;
import com.lht.creationspace.util.internet.HttpUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.presenter
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> RegisterActivityPresenter
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/5/5.
 */
public class RegisterActivityPresenter implements TimerClockModel.ISharedPreferenceProvider,
        IApiRequestPresenter {

    private TimerClockModel mTimerClockModel;

    private IRegisterActivity iRegisterActivity;

    //存储一下目标手机，检验可用再发验证码
    private String phone;

    private final TextWatcherModel textWatcherModel;

    //是否同意协议
    private boolean isProtocolAgreed = false;

    public RegisterActivityPresenter(IRegisterActivity iRegisterActivity) {
        this.iRegisterActivity = iRegisterActivity;
        textWatcherModel = new TextWatcherModel(new TextWatcherModelCallbackImpl());
        mTimerClockModel = new TimerClockModel(this, new TimeLapseListenerImpl());
        mTimerClockModel.setPeriod(120000);
    }

    public void callSendSmsVerifyCode(String targetPhone) {
        this.phone = targetPhone;
        iRegisterActivity.showWaitView(true);
        int phoneLength = 11;
        if (targetPhone.length() != phoneLength) {
            iRegisterActivity.showErrorMsg(iRegisterActivity.getAppResource().getString(R.string
                    .v1010_default_bindphone_phonestyle_error));
            iRegisterActivity.cancelWaitView();
            return;
        }
        //use api to check phone
        IApiRequestModel mCheckPhoneModel = new CheckPhoneModel(targetPhone, new
                CheckPhoneModelCallback());
        mCheckPhoneModel.doRequest(iRegisterActivity.getActivity());
        //Callback中处理发送验证码

    }

    public void callRegister() {
        CheckableJobs.getInstance()
                //手机非空
                .next(new UnCompetedInputCheckJob(iRegisterActivity.getPhone(),
                        R.string.v1010_default_empty_phonenumber, iRegisterActivity))
                .next(new CellPhoneCheckJob(iRegisterActivity.getPhone()))
                //验证码非空
                .next(new UnCompetedInputCheckJob(iRegisterActivity.getVerifyCode(),
                        R.string.v1010_default_bindphone_enter_verificationvode, iRegisterActivity))
                //校验密码合法
                .next(new PasswordCheckJob(iRegisterActivity.getPwd(), iRegisterActivity))
                //check agree protocol
                .next(new AbsCheckJob() {
                    @Override
                    public boolean check() {
                        return isProtocolAgreed;
                    }

                    @Override
                    public void onCheckIllegal() {
                        iRegisterActivity.showErrorMsg(iRegisterActivity.getActivity().getString(R.string
                                .v1000_default_register_toast_read_agreement));
                    }
                })
                .onAllCheckLegal(new CheckableJobs.OnAllCheckLegalListener() {
                    @Override
                    public void onAllCheckLegal() {
                        iRegisterActivity.showWaitView(true);
                        RegisterModel.ModelRequestData data =
                                new RegisterModel.ModelRequestData(iRegisterActivity.getPhone(),
                                        iRegisterActivity.getPwd(),
                                        iRegisterActivity.getVerifyCode());
                        IApiRequestModel mRegisterModel = new RegisterModel(data, new RegisterModelCallback(iRegisterActivity
                                .getPhone(), iRegisterActivity.getPwd()));
                        mRegisterModel.doRequest(iRegisterActivity.getActivity());
                    }
                }).start();
    }

    public void resumeTimer() {
        mTimerClockModel.resume(1000);
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return iRegisterActivity.getActivity()
                .getSharedPreferences(SPConstants.Timer.SP_TIMER, Context.MODE_PRIVATE);
    }

    public void jump2RoleChooseActivty(int valueSourceRegister) {
        UserInfoCreateActivity.getLauncher(iRegisterActivity.getActivity())
                .injectData(tranData(valueSourceRegister))
                .launch();
    }

    private UserInfoCreateActivity.UserInfoCreateActivityData tranData(int valueSourceRegister) {
        UserInfoCreateActivity.UserInfoCreateActivityData data = new UserInfoCreateActivity.UserInfoCreateActivityData();
        data.setSourceTag(valueSourceRegister);
        return data;
    }

    public void jump2UserAgreement() {
        UserAgreementActivity.getLauncher(iRegisterActivity.getActivity())
                .injectData(transData(IPublicConst.SIMPLIFIED_AGREEMENT))
                .launch();
    }

    private UserAgreementActivity.UserAgreementActivityData transData(String simplifiedAgreement) {
        UserAgreementActivity.UserAgreementActivityData data = new UserAgreementActivity.UserAgreementActivityData();
        data.setTitle(iRegisterActivity.getActivity().getString(R.string.title_activity_user_agreement));
        data.setUserAgreementUrl(simplifiedAgreement);
        return data;
    }

    private class TextWatcherModelCallbackImpl implements TextWatcherModel
            .TextWatcherModelCallback {

        @Override
        public void onOverLength(int edittextId, int maxLength) {
            iRegisterActivity.notifyOverLength();
        }

        @Override
        public void onChanged(int edittextId, int currentCount, int remains) {

        }
    }

    @Override
    public String getRecordTag() {
        return getClass().getSimpleName();
    }

    public void cancelTimer() {
        mTimerClockModel.cancel();
    }

    public void setIsProtocolAgreed(boolean isProtocolAgreed) {
        this.isProtocolAgreed = isProtocolAgreed;
    }

    public void watchInputLength(EditText editText, int maxLength) {
        textWatcherModel.doWatcher(editText, maxLength);
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    private void doBackgroundLogin(String usr, String pwd) {
        iRegisterActivity.showWaitView(true);
        IApiRequestModel mLoginModel = new LoginModel(usr, pwd, new LoginModelCallbackImpl());

        mLoginModel.doRequest(iRegisterActivity.getActivity());
        SharedPreferences sp = iRegisterActivity.getActivity().getSharedPreferences(SPConstants
                .Token.SP_TOKEN, Context.MODE_PRIVATE);
        SPUtil.modifyString(sp, SPConstants.Token.KEY_ACCOUNT, usr);
    }

    private final class TimeLapseListenerImpl implements TimerClockModel.OnTimeLapseListener {

        @Override
        public void onFinish() {
            mTimerClockModel.updateTimeStamp();
            iRegisterActivity.initVCGetter(R.string.v1000_default_register_btn_getverify);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            iRegisterActivity.showCDRemaining(millisUntilFinished / 1000 + "s");
            iRegisterActivity.setVCGetterActiveStatus(false);
        }
    }

    private final class SendSmsCallbackImpl implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            mTimerClockModel.cancel();
            mTimerClockModel.updateTimeStamp();
            mTimerClockModel.getTimeClock(1000).start();
            iRegisterActivity.showMsg(beanContainer.getData().getMessage());
            iRegisterActivity.cancelWaitView();

        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iRegisterActivity.cancelWaitView();
            iRegisterActivity.showErrorMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iRegisterActivity.cancelWaitView();
        }
    }

    private final class RegisterModelCallback
            implements ApiModelCallback<RegisterResBean> {
        private final String user;

        private final String pwd;

        public RegisterModelCallback(String user, String pwd) {
            this.user = user;
            this.pwd = pwd;
        }

        @Override
        public void onSuccess(BaseBeanContainer<RegisterResBean> beanContainer) {
            //统计 注册成功事件 - 计数
            iRegisterActivity.reportCountEvent(IUmengEventKey.KEY_USER_REGIST_ISSUCCESS);

            //do login on background
            doBackgroundLogin(user, pwd);
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iRegisterActivity.cancelWaitView();
            String msg = RegisterResBean.parseMsgByRet(beanContainer.getData().getRet(), beanContainer.getData());
            iRegisterActivity.showErrorMsg(msg);
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iRegisterActivity.cancelWaitView();
        }
    }

    private final class CheckPhoneModelCallback implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            //  发送验证码
            IApiRequestModel mSendSmsModel = new SendSmsModel(SendSmsModel.SmsRequestType
                    .Register, phone, new SendSmsCallbackImpl());
            mSendSmsModel.doRequest(iRegisterActivity.getActivity());
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iRegisterActivity.cancelWaitView();
            if (beanContainer.getData().getRet() == RegisterResBean.RET_MOBILE_CONFLICT) {
                iRegisterActivity.showLoginGuideDialog();
            } else {
                String msg = RegisterResBean.parseMsgByRet(beanContainer.getData().getRet(), beanContainer.getData());
                iRegisterActivity.showErrorMsg(msg);
            }
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iRegisterActivity.cancelWaitView();
        }
    }

    /**
     * 静默登录请求回调
     */
    private final class LoginModelCallbackImpl
            implements ApiModelCallback<LoginResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<LoginResBean> beanContainer) {
            LoginResBean data = beanContainer.getData();
            LoginInfo info = new LoginInfo();

            info.setAccessId(data.getUid());
            info.setAccessToken(data.getVso_token());
            info.setUsername(data.getUsername());
            info.setNickname(data.getNickname());
            info.setAvatar(data.getAvatar());
            info.setLoginResBean(data);

            IVerifyHolder.mLoginInfo.copy(info);

            SharedPreferences sp = iRegisterActivity.getActivity().getSharedPreferences(SPConstants
                    .Token.SP_TOKEN, Context.MODE_PRIVATE);
            SPUtil.modifyString(sp, SPConstants.Token.KEY_ACCESS_ID, info.getAccessId());
            SPUtil.modifyString(sp, SPConstants.Token.KEY_ACCESS_TOKEN, info.getAccessToken());
            SPUtil.modifyString(sp, SPConstants.Token.KEY_USERNAME, info.getUsername());

            iRegisterActivity.cancelWaitView();

            AppEvent.RegisterBackgroundLoginSuccessEvent event = new AppEvent
                    .RegisterBackgroundLoginSuccessEvent(info);
            EventBus.getDefault().post(event);

            //统计 从注册进入订阅 -计数
            iRegisterActivity.reportCountEvent(IUmengEventKey.KEY_SUBSCRIBE_ATREG);
            // 跳转到用户信息完善
            iRegisterActivity.jump2UserInfoCreate();

            //绑定设备
            MainApplication.getOurInstance().bindDevice();

            iRegisterActivity.getActivity().finish();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            // 通过关闭回到登录页，提示后台登录失败，手动登录
            iRegisterActivity.cancelWaitView();
            iRegisterActivity.showErrorMsg(iRegisterActivity.getAppResource().getString(R.string
                    .v1000_toast_register_error_login_onback));
            iRegisterActivity.getActivity().finish();
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            // 通过关闭回到登录页，提示后台登录失败，手动登录
            iRegisterActivity.cancelWaitView();
            iRegisterActivity.showErrorMsg(iRegisterActivity.getAppResource().getString(R.string
                    .v1000_toast_register_error_login_onback));
            iRegisterActivity.getActivity().finish();
        }
    }

}
