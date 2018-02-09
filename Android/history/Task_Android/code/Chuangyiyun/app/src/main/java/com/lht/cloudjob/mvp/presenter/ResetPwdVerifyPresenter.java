package com.lht.cloudjob.mvp.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;

import com.lht.cloudjob.R;
import com.lht.cloudjob.interfaces.keys.SPConstants;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.SendSmsModel;
import com.lht.cloudjob.mvp.model.TextWatcherModel;
import com.lht.cloudjob.mvp.model.TimerClockModel;
import com.lht.cloudjob.mvp.model.VerifyCodeCheckModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.viewinterface.IResetPwdVerifyActivity;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.lht.cloudjob.util.string.StringUtil;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> ResetPwdVerifyPresenter
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/25.
 */
public class ResetPwdVerifyPresenter implements TimerClockModel.ISharedPreferenceProvider,
        IApiRequestPresenter {

    private TimerClockModel mTimerClockModel;

    private IResetPwdVerifyActivity iResetPwdVerifyActivity;

    private TextWatcherModel textWatcherModel;

    public ResetPwdVerifyPresenter(IResetPwdVerifyActivity iResetPwdVerifyActivity) {
        this.iResetPwdVerifyActivity = iResetPwdVerifyActivity;

        mTimerClockModel = new TimerClockModel(this, new TimeLapseListenerImpl());
        mTimerClockModel.setPeriod(120000);

        textWatcherModel = new TextWatcherModel(new TextWatcherModelCallbackImpl());
    }

    public void resumeTimer() {
        mTimerClockModel.resume(1000);
    }

    public void callSendSmsVerifyCode(String targetPhone) {
        this.account = targetPhone;
        iResetPwdVerifyActivity.showWaitView(true);
        int phoneLength = 11;
        if (targetPhone.length() != phoneLength) {
            iResetPwdVerifyActivity.showErrorMsg(iResetPwdVerifyActivity.getActivity().getString
                    (R.string.v1010_default_bindphone_phonestyle_error));
            iResetPwdVerifyActivity.cancelWaitView();
            return;
        }
        IApiRequestModel mSendSmsModel = new SendSmsModel(SendSmsModel.SmsRequestType.ResetPwd,
                targetPhone, new SendSmsCallbackImpl());
        mSendSmsModel.doRequest(iResetPwdVerifyActivity.getActivity());
    }

    public void callCheck(String phone, String verifyCode) {
        //check pwd seted
        if (StringUtil.isEmpty(phone)) {

            iResetPwdVerifyActivity.showErrorMsg(iResetPwdVerifyActivity.getActivity().getString
                    (R.string.v1010_default_bindphone_enter_phonenumber));
            return;
        }

        if (StringUtil.isEmpty(verifyCode)) {

            iResetPwdVerifyActivity.showErrorMsg(iResetPwdVerifyActivity.getActivity().getString
                    (R.string.v1010_default_bindphone_enter_verificationvode));
            return;
        }
        iResetPwdVerifyActivity.showWaitView(true);
        IApiRequestModel mCheckVerifyCodeModel = new VerifyCodeCheckModel(phone, verifyCode,
                new VerifyCodeCheckModelCallback());
        mCheckVerifyCodeModel.doRequest(iResetPwdVerifyActivity.getActivity());
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return iResetPwdVerifyActivity.getActivity()
                .getSharedPreferences(SPConstants.Timer.SP_TIMER, Context.MODE_PRIVATE);
    }

    @Override
    public String getRecordTag() {
        return "resetPwd";
    }

    private String account;

    public void watchLength(EditText editText, int maxLength) {
        textWatcherModel.doWatcher(editText,maxLength);
    }

    private final class VerifyCodeCheckModelCallback implements
            ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iResetPwdVerifyActivity.cancelWaitView();
            iResetPwdVerifyActivity.jump2SetPwd(account);
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iResetPwdVerifyActivity.cancelWaitView();
            iResetPwdVerifyActivity.showErrorMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iResetPwdVerifyActivity.cancelWaitView();
            // TODO: 2016/7/21
        }
    }

    private final class TimeLapseListenerImpl implements TimerClockModel.OnTimeLapseListener {

        @Override
        public void onFinish() {
            mTimerClockModel.updateTimeStamp();
            iResetPwdVerifyActivity.initVCGetter(R.string.v1010_default_register_btn_getverify);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            iResetPwdVerifyActivity.showCDRemaining(millisUntilFinished / 1000 + "s");
            iResetPwdVerifyActivity.setVCGetterActiveStatus(false);
        }
    }

    private final class SendSmsCallbackImpl implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            mTimerClockModel.cancel();
            mTimerClockModel.updateTimeStamp();
            mTimerClockModel.getTimeClock(1000).start();
            //TODO
            iResetPwdVerifyActivity.cancelWaitView();

        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            //TODO
            iResetPwdVerifyActivity.cancelWaitView();
            iResetPwdVerifyActivity.showErrorMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            //TODO
            iResetPwdVerifyActivity.cancelWaitView();
        }
    }

    private class TextWatcherModelCallbackImpl implements TextWatcherModel
            .TextWatcherModelCallback {

        @Override
        public void onOverLength(int edittextId, int maxLength) {
            iResetPwdVerifyActivity.showErrorMsg(iResetPwdVerifyActivity.getAppResource()
                    .getString(R.string.v1010_toast_warm_enter_text_overlength));
        }

        @Override
        public void onChanged(int edittextId, int currentCount, int remains) {

        }
    }
}
