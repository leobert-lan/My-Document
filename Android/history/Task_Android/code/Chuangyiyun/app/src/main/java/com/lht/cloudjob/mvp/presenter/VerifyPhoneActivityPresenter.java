package com.lht.cloudjob.mvp.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;

import com.lht.cloudjob.R;
import com.lht.cloudjob.interfaces.IPublicConst;
import com.lht.cloudjob.interfaces.keys.SPConstants;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.SendSmsModel;
import com.lht.cloudjob.mvp.model.TimerClockModel;
import com.lht.cloudjob.mvp.model.VerifyCodeCheckModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.viewinterface.IVerifyPhoneActivity;
import com.lht.cloudjob.util.internet.HttpUtil;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> VerifyPhoneActivityPresenter
 * <p><b>Description</b>: 绑定新手机-校验老手机页面presenter
 * <p>Created by Administrator on 2016/8/31.
 */
public class VerifyPhoneActivityPresenter implements IApiRequestPresenter,
        TimerClockModel.ISharedPreferenceProvider {

    private IVerifyPhoneActivity iVerifyPhoneActivity;

    private TimerClockModel mTimerClockModel;

    public VerifyPhoneActivityPresenter(IVerifyPhoneActivity iVerifyPhoneActivity) {
        this.iVerifyPhoneActivity = iVerifyPhoneActivity;

        mTimerClockModel = new TimerClockModel(this, new TimeLapseListenerImpl());
        mTimerClockModel.setPeriod(120000);
    }

    public void callSendSmsVerifyCode(String bindedPhone) {
        IApiRequestModel mSendSmsModel = new SendSmsModel(SendSmsModel.SmsRequestType.Normal,
                bindedPhone, new SendSmsCallbackImpl());
        mSendSmsModel.doRequest(iVerifyPhoneActivity.getActivity());
    }

    private String phone = "";

    public void callVerify(String bindedPhone) {
        this.phone = bindedPhone;
        if (TextUtils.isEmpty(iVerifyPhoneActivity.getVerifyCode())) {
            iVerifyPhoneActivity.showErrorMsg(iVerifyPhoneActivity.getActivity().getString(R.string.v1010_default_bindphone_enter_verificationvode));
            return;
        }

        iVerifyPhoneActivity.showWaitView(true);
        IApiRequestModel mCheckVerifyCodeModel = new VerifyCodeCheckModel(bindedPhone,
                iVerifyPhoneActivity.getVerifyCode(), new VerifyCodeCheckModelCallback());
        mCheckVerifyCodeModel.doRequest(iVerifyPhoneActivity.getActivity());
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return iVerifyPhoneActivity.getActivity()
                .getSharedPreferences(SPConstants.Timer.SP_TIMER, Context.MODE_PRIVATE);
    }

    @Override
    public String getRecordTag() {
        return "verifyOldPhone" + phone;
    }

    public void resumeTimer() {
        mTimerClockModel.resume(1000);
    }

    public void callUs() {
        String telPhone = IPublicConst.TEL;
        Uri uri = Uri.parse("tel:" + telPhone);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        intent.setData(uri);
        iVerifyPhoneActivity.getActivity().startActivity(intent);
    }

    private final class VerifyCodeCheckModelCallback implements
            ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            // old phone check success,jump to bind a new phone
            iVerifyPhoneActivity.cancelWaitView();
            iVerifyPhoneActivity.jump2BindPhone();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iVerifyPhoneActivity.cancelWaitView();
            iVerifyPhoneActivity.showErrorMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iVerifyPhoneActivity.cancelWaitView();

            // TODO: 2016/9/1
        }
    }


    private final class SendSmsCallbackImpl implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            mTimerClockModel.cancel();
            mTimerClockModel.updateTimeStamp();
            mTimerClockModel.getTimeClock(1000).start();
            iVerifyPhoneActivity.cancelWaitView();

        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iVerifyPhoneActivity.cancelWaitView();
            iVerifyPhoneActivity.showErrorMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iVerifyPhoneActivity.cancelWaitView();
            // TODO: 2016/9/23
           
        }
    }

    /**
     * 倒计时callback实现类
     */
    private final class TimeLapseListenerImpl implements TimerClockModel.OnTimeLapseListener {

        @Override
        public void onFinish() {
            mTimerClockModel.updateTimeStamp();
            iVerifyPhoneActivity.initVCGetter(R.string.v1010_default_register_btn_getverify);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            iVerifyPhoneActivity.showCDRemaining(millisUntilFinished / 1000 + "s");
            iVerifyPhoneActivity.setVCGetterActiveStatus(false);
        }
    }

}
