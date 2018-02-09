package com.lht.creationspace.module.user.security;

import android.content.Context;
import android.content.SharedPreferences;

import com.lht.creationspace.R;
import com.lht.creationspace.base.model.TimerClockModel;
import com.lht.creationspace.base.model.apimodel.ApiModelCallback;
import com.lht.creationspace.base.model.apimodel.BaseBeanContainer;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.cfg.SPConstants;
import com.lht.creationspace.module.user.security.model.SendSmsByUserModel;
import com.lht.creationspace.module.user.security.model.VerifyCodeCheckWithUserModel;
import com.lht.creationspace.module.user.security.ui.BindPhoneActivity;
import com.lht.creationspace.module.user.security.ui.IVerifyOldPhoneActivity;
import com.lht.creationspace.util.internet.HttpUtil;
import com.lht.creationspace.util.string.StringUtil;

/**
 * Created by chhyu on 2017/2/22.
 */

public class VerifyOldPhoneActivityPresenter implements
        IApiRequestPresenter, TimerClockModel.ISharedPreferenceProvider {
    private final TimerClockModel mTimerClockModel;
    private IVerifyOldPhoneActivity iVerifyOldPhoneActivity;

    public VerifyOldPhoneActivityPresenter(IVerifyOldPhoneActivity iVerifyOldPhoneActivity) {
        this.iVerifyOldPhoneActivity = iVerifyOldPhoneActivity;
        mTimerClockModel = new TimerClockModel(this, new TimeLapseListenerImpl());
        mTimerClockModel.setPeriod(120000);
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    /**
     * 发送验证码
     */
    public void callSendSmsVerifyCode(SendSmsByUserModel.SendSmsByUserData data) {

        iVerifyOldPhoneActivity.showWaitView(true);
        SendSmsByUserModel model = new SendSmsByUserModel(data, new SendSmsCallbackImpl());
        model.doRequest(iVerifyOldPhoneActivity.getActivity());
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return iVerifyOldPhoneActivity.getActivity()
                .getSharedPreferences(SPConstants.Timer.SP_TIMER, Context.MODE_PRIVATE);
    }

    @Override
    public String getRecordTag() {
        return getClass().getSimpleName();
    }

    public void resumeTimer() {
        mTimerClockModel.resume(1000);
    }

    public void jump2BindPhoneActivity(String validCode) {
        BindPhoneActivity.BindPhoneActivityData data = new BindPhoneActivity.BindPhoneActivityData();
        data.setUpdate(true);
        data.setVerifyCode(validCode);
        BindPhoneActivity.getLauncher(iVerifyOldPhoneActivity.getActivity())
                .injectData(data)
                .launch();
    }

    private final class SendSmsCallbackImpl
            implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iVerifyOldPhoneActivity.cancelWaitView();
            iVerifyOldPhoneActivity.showMsg(beanContainer.getData().getMessage());

            //cd
            mTimerClockModel.updateTimeStamp();
            mTimerClockModel.getTimeClock(1000).start();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iVerifyOldPhoneActivity.cancelWaitView();
            iVerifyOldPhoneActivity.showMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iVerifyOldPhoneActivity.cancelWaitView();
        }
    }

    /**
     * 倒计时callback实现类
     */
    private final class TimeLapseListenerImpl
            implements TimerClockModel.OnTimeLapseListener {

        @Override
        public void onFinish() {
            mTimerClockModel.updateTimeStamp();
            iVerifyOldPhoneActivity.initVCGetter(R.string.v1000_default_register_btn_getverify);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            iVerifyOldPhoneActivity.showCDRemaining(millisUntilFinished / 1000 + "s");
            iVerifyOldPhoneActivity.setVCGetterActiveStatus(false);
        }
    }

    /**
     * 验证
     */
    public void callVerifyOldPhone(String username, String validCode) {
        if (StringUtil.isEmpty(validCode)) {
            iVerifyOldPhoneActivity.showMsg("请填写验证码");
            return;
        }
        iVerifyOldPhoneActivity.showWaitView(true);
        VerifyCodeCheckWithUserModel.VerifyCodeCheckWithUserData data = new VerifyCodeCheckWithUserModel.VerifyCodeCheckWithUserData();
        data.setUsername(username);
        data.setCode(validCode);
        //验证成功之后
        VerifyCodeCheckWithUserModel model =
                new VerifyCodeCheckWithUserModel(data, new checkVerifyCodeModelCallback(validCode));
        model.doRequest(iVerifyOldPhoneActivity.getActivity());
    }

    private class checkVerifyCodeModelCallback
            implements ApiModelCallback<BaseVsoApiResBean> {
        private String validCode;

        public checkVerifyCodeModelCallback(String validCode) {
            this.validCode = validCode;
        }

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iVerifyOldPhoneActivity.cancelWaitView();
            //验证码正确，跳转到绑定手机页面
            iVerifyOldPhoneActivity.showMsg("验证码正确");
            jump2BindPhoneActivity(validCode);
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iVerifyOldPhoneActivity.cancelWaitView();
            iVerifyOldPhoneActivity.showMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iVerifyOldPhoneActivity.cancelWaitView();
        }
    }
}
