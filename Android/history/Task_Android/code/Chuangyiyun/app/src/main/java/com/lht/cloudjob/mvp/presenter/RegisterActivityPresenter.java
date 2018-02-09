package com.lht.cloudjob.mvp.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;

import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.MainApplication;
import com.lht.cloudjob.R;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.interfaces.keys.SPConstants;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.interfaces.umeng.IUmengEventKey;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.BasicInfoModel;
import com.lht.cloudjob.mvp.model.CheckPhoneModel;
import com.lht.cloudjob.mvp.model.LoginModel;
import com.lht.cloudjob.mvp.model.RegisterModel;
import com.lht.cloudjob.mvp.model.SendSmsModel;
import com.lht.cloudjob.mvp.model.TextWatcherModel;
import com.lht.cloudjob.mvp.model.TimerClockModel;
import com.lht.cloudjob.mvp.model.VerifyCodeCheckModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.BasicInfoResBean;
import com.lht.cloudjob.mvp.model.bean.LoginResBean;
import com.lht.cloudjob.mvp.model.bean.RegisterResBean;
import com.lht.cloudjob.mvp.model.pojo.LoginInfo;
import com.lht.cloudjob.mvp.viewinterface.IRegisterActivity;
import com.lht.cloudjob.util.CheckPwdUtil;
import com.lht.cloudjob.util.SPUtil;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.lht.cloudjob.util.string.StringUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> RegisterActivityPresenter
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/5/5.
 */
public class RegisterActivityPresenter implements TimerClockModel.ISharedPreferenceProvider,
        IApiRequestPresenter {

    private TimerClockModel mTimerClockModel;

    private IRegisterActivity iRegisterActivity;

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
        //check pwd seted
        if (isPhoneNull()) {
            iRegisterActivity.showErrorMsg(iRegisterActivity.getActivity().getString(R.string
                    .v1010_default_bindphone_enter_phonenumber));
            return;
        }

        if (StringUtil.isEmpty(iRegisterActivity.getVerifyCode())) {

            iRegisterActivity.showErrorMsg(iRegisterActivity.getActivity().getString(R.string
                    .v1010_default_bindphone_enter_verificationvode));
            return;
        }
        //check agree protocol
        if (!isProtocolAgreed) {
            iRegisterActivity.showErrorMsg(iRegisterActivity.getActivity().getString(R.string
                    .v1010_default_register_toast_read_agreement));
            return;
        }
        String pwd = iRegisterActivity.getPwd();
        //校验密码长度
        if (!CheckPwdUtil.checkPwdLengthLegal(pwd, iRegisterActivity)) {
            return;
        }
        iRegisterActivity.showWaitView(true);
        IApiRequestModel mCheckVerifyCodeModel = new VerifyCodeCheckModel(iRegisterActivity
                .getPhone(),
                iRegisterActivity.getVerifyCode(), new VerifyCodeCheckModelCallback());
        mCheckVerifyCodeModel.doRequest(iRegisterActivity.getActivity());
    }

    public void resumeTimer() {
        mTimerClockModel.resume(1000);
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return iRegisterActivity.getActivity()
                .getSharedPreferences(SPConstants.Timer.SP_TIMER, Context.MODE_PRIVATE);
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
        return "register";
    }

    public void cancelTimer() {
        mTimerClockModel.cancel();
    }

    public void setIsProtocolAgreed(boolean isProtocolAgreed) {
        this.isProtocolAgreed = isProtocolAgreed;
    }

    public boolean isPhoneNull() {
        String phone = iRegisterActivity.getPhone();
        if (StringUtil.isEmpty(phone)) {
            return true;
        }
        return false;
    }

    public boolean isPhoneChanged() {
        String phone = iRegisterActivity.getPhone();
        return !phone.equals(this.phone);
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

    private void doBasicUserInfoQuery(LoginInfo info) {
        IApiRequestModel mUserinfoModel = new BasicInfoModel(info.getUsername(), new
                BasicInfoModelCallback(info));
        mUserinfoModel.doRequest(iRegisterActivity.getActivity());
    }

    private final class TimeLapseListenerImpl implements TimerClockModel.OnTimeLapseListener {

        @Override
        public void onFinish() {
            mTimerClockModel.updateTimeStamp();
            iRegisterActivity.initVCGetter(R.string.v1010_default_register_btn_getverify);
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

    private final class VerifyCodeCheckModelCallback implements
            ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            // do register
            IApiRequestModel mRegisterModel = new RegisterModel(iRegisterActivity.getPhone(),
                    iRegisterActivity.getPwd(), new RegisterModelCallback(iRegisterActivity
                    .getPhone(), iRegisterActivity.getPwd()));
            mRegisterModel.doRequest(iRegisterActivity.getActivity());
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

    private RegisterResBean registerResBean;

    private final class RegisterModelCallback implements ApiModelCallback<RegisterResBean> {
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
            registerResBean = beanContainer.getData();
            doBackgroundLogin(user, pwd);
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
            final int RET_EXIST = 13183;
            if (beanContainer.getData().getRet() == RET_EXIST) {
                iRegisterActivity.showLoginGuideDialog();
            } else {
                iRegisterActivity.showErrorMsg(beanContainer.getData().getMessage());
            }
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iRegisterActivity.cancelWaitView();
        }
    }

    private final class LoginModelCallbackImpl implements ApiModelCallback<LoginResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<LoginResBean> beanContainer) {
            LoginResBean data = beanContainer.getData();
            LoginInfo info = new LoginInfo();

            info.setAccessId(String.valueOf(data.getId()));
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

            doBasicUserInfoQuery(info);
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            // 通过关闭回到登录页，提示后台登录失败，手动登录
            iRegisterActivity.cancelWaitView();
            iRegisterActivity.showErrorMsg(iRegisterActivity.getAppResource().getString(R.string
                    .v1010_toast_register_error_login_onback));
            iRegisterActivity.getActivity().finish();
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            // 通过关闭回到登录页，提示后台登录失败，手动登录
            iRegisterActivity.cancelWaitView();
            iRegisterActivity.showErrorMsg(iRegisterActivity.getAppResource().getString(R.string
                    .v1010_toast_register_error_login_onback));
            iRegisterActivity.getActivity().finish();
        }
    }

    private final class BasicInfoModelCallback implements ApiModelCallback<BasicInfoResBean> {

        private final LoginInfo info;

        BasicInfoModelCallback(LoginInfo info) {
            this.info = info;
        }

        @Override
        public void onSuccess(BaseBeanContainer<BasicInfoResBean> beanContainer) {
            iRegisterActivity.cancelWaitView();
            BasicInfoResBean data = beanContainer.getData();
            info.setType(data.getLoginType());
            info.setBasicUserInfo(data);

            AppEvent.RegisterBackgroundLoginSuccessEvent event = new AppEvent
                    .RegisterBackgroundLoginSuccessEvent(info);
            EventBus.getDefault().post(event);

            //统计 从注册进入订阅 -计数
            iRegisterActivity.reportCountEvent(IUmengEventKey.KEY_SUBSCRIBE_ATREG);
            // 跳转到订阅页
            iRegisterActivity.jump2Subscribe(registerResBean);

            //绑定设备
            MainApplication.getOurInstance().bindDevice();

            iRegisterActivity.getActivity().finish();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            // 通过关闭回到登录页，提示后台登录失败，手动登录
            iRegisterActivity.cancelWaitView();
            iRegisterActivity.showErrorMsg(iRegisterActivity.getAppResource().getString(R.string
                    .v1010_toast_register_error_login_onback));
            iRegisterActivity.getActivity().finish();
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            //通过关闭回到登录页，提示后台登录失败，手动登录
            iRegisterActivity.cancelWaitView();
            iRegisterActivity.showErrorMsg(iRegisterActivity.getAppResource().getString(R.string
                    .v1010_toast_register_error_login_onback));
            iRegisterActivity.getActivity().finish();
        }
    }
}
