package com.lht.creationspace.module.user.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;

import com.lht.creationspace.R;
import com.lht.creationspace.base.model.TextWatcherModel;
import com.lht.creationspace.base.model.TimerClockModel;
import com.lht.creationspace.base.model.apimodel.ApiModelCallback;
import com.lht.creationspace.base.model.apimodel.BaseBeanContainer;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.cfg.SPConstants;
import com.lht.creationspace.checkable.AbsOnAllCheckLegalListener;
import com.lht.creationspace.checkable.CheckableJobs;
import com.lht.creationspace.checkable.jobs.CellPhoneCheckJob;
import com.lht.creationspace.checkable.jobs.UnCompetedInputCheckJob;
import com.lht.creationspace.module.user.security.model.CheckPhoneModel;
import com.lht.creationspace.module.user.security.model.SendSmsModel;
import com.lht.creationspace.module.user.security.model.VerifyCodeCheckModel;
import com.lht.creationspace.module.user.security.ui.IResetPwdVerifyActivity;
import com.lht.creationspace.module.user.security.ui.ResetPwdActivity;
import com.lht.creationspace.util.debug.DLog;
import com.lht.creationspace.util.internet.HttpUtil;
import com.lht.creationspace.util.string.StringUtil;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.presenter
 * <p><b>Project</b> VsoCyy
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
//        this.account = targetPhone;
        int phoneLength = 11;
        if (targetPhone.length() != phoneLength) {
            iResetPwdVerifyActivity.showErrorMsg(iResetPwdVerifyActivity.getActivity().getString
                    (R.string.v1010_default_bindphone_phonestyle_error));
            iResetPwdVerifyActivity.cancelWaitView();
            return;
        }
        iResetPwdVerifyActivity.showWaitView(true);
        IApiRequestModel model = new CheckPhoneModel(targetPhone, new PhoneCheckModelCallback(targetPhone));
        model.doRequest(iResetPwdVerifyActivity.getActivity());

    }

    public void callCheck(String phone, String verifyCode) {
        //check pwd seted
        VerifyCodeCheckModel.VerifyCodeCheckData data = new VerifyCodeCheckModel.VerifyCodeCheckData();
        data.setPhone(phone);
        data.setCode(verifyCode);

        CheckableJobs.getInstance()
                .next(new CellPhoneCheckJob(phone))
                .next(new UnCompetedInputCheckJob(verifyCode,
                        R.string.v1010_default_bindphone_enter_verificationvode,
                        iResetPwdVerifyActivity))
                .onAllCheckLegal(new AbsOnAllCheckLegalListener<VerifyCodeCheckModel.VerifyCodeCheckData>(data) {
                    @Override
                    public void onAllCheckLegal() {
                        iResetPwdVerifyActivity.showWaitView(true);
                        IApiRequestModel mCheckVerifyCodeModel = new VerifyCodeCheckModel(getSavedParam(),
                                new VerifyCodeCheckModelCallback(getSavedParam().getPhone(),
                                        getSavedParam().getCode()));
                        mCheckVerifyCodeModel.doRequest(iResetPwdVerifyActivity.getActivity());
                    }
                }).start();
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
        return getClass().getSimpleName();
    }

//    private String account;

    public void watchLength(EditText editText, int maxLength) {
        textWatcherModel.doWatcher(editText, maxLength);
    }

    public void jump2ResetPwdActivity(String account, String validCode) {
        DLog.d(DLog.Lmsg.class, " [onNavigation]: acc:" + account + ";vc:" + validCode);

        ResetPwdActivity.getLauncher(iResetPwdVerifyActivity.getActivity())
                .injectData(transData(account, validCode))
                .launch();
    }

    private ResetPwdActivity.ResetPwdActivityData transData(String account, String validCode) {
        ResetPwdActivity.ResetPwdActivityData data = new ResetPwdActivity.ResetPwdActivityData();
        data.setAccount(account);
        data.setValidCode(validCode);
        return data;
    }

    private final class VerifyCodeCheckModelCallback
            implements ApiModelCallback<BaseVsoApiResBean> {

        private String account;

        private String validCode;

        public VerifyCodeCheckModelCallback(String account, String validCode) {
            this.account = account;
            this.validCode = validCode;
        }

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iResetPwdVerifyActivity.showMsg("验证成功!");
            iResetPwdVerifyActivity.cancelWaitView();
            iResetPwdVerifyActivity.jump2SetPwd(account, validCode);
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
            iResetPwdVerifyActivity.initVCGetter(R.string.v1000_default_register_btn_getverify);
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
            iResetPwdVerifyActivity.cancelWaitView();
            iResetPwdVerifyActivity.showMsg(beanContainer.getData().getMessage());

        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iResetPwdVerifyActivity.cancelWaitView();
            iResetPwdVerifyActivity.showErrorMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iResetPwdVerifyActivity.cancelWaitView();
        }
    }

    private class TextWatcherModelCallbackImpl implements TextWatcherModel
            .TextWatcherModelCallback {

        @Override
        public void onOverLength(int edittextId, int maxLength) {
            iResetPwdVerifyActivity.showErrorMsg(iResetPwdVerifyActivity.getAppResource()
                    .getString(R.string.v1000_toast_warm_enter_text_overlength));
        }

        @Override
        public void onChanged(int edittextId, int currentCount, int remains) {

        }
    }

    private final class PhoneCheckModelCallback implements ApiModelCallback<BaseVsoApiResBean> {

        private String phone;

        public PhoneCheckModelCallback(String phone) {
            this.phone = phone;
        }

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iResetPwdVerifyActivity.cancelWaitView();
            //提示手机号未注册
            iResetPwdVerifyActivity.showErrorMsg("该号码未注册");
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            if (beanContainer.getData().getRet() == CheckPhoneModel.RET_EXIST) {
                //手机号存在发送验证码
                IApiRequestModel mSendSmsModel = new SendSmsModel(SendSmsModel.SmsRequestType.ResetPwd,
                        phone, new SendSmsCallbackImpl());
                mSendSmsModel.doRequest(iResetPwdVerifyActivity.getActivity());
            } else {
                iResetPwdVerifyActivity.showErrorMsg(beanContainer.getData().getMessage());
                iResetPwdVerifyActivity.cancelWaitView();
            }
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iResetPwdVerifyActivity.cancelWaitView();
        }
    }
}
