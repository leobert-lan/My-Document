package com.lht.creationspace.module.user.security;

import android.content.Context;
import android.content.SharedPreferences;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.cfg.SPConstants;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.base.model.apimodel.ApiModelCallback;
import com.lht.creationspace.checkable.AbsOnAllCheckLegalListener;
import com.lht.creationspace.checkable.CheckableJobs;
import com.lht.creationspace.checkable.jobs.CellPhoneCheckJob;
import com.lht.creationspace.checkable.jobs.UnCompetedInputCheckJob;
import com.lht.creationspace.module.user.security.model.CheckPhoneModel;
import com.lht.creationspace.module.user.info.model.CreateBindPhoneModel;
import com.lht.creationspace.module.user.security.model.SendSmsModel;
import com.lht.creationspace.base.model.TimerClockModel;
import com.lht.creationspace.module.user.info.model.UpdateBindPhoneModel;
import com.lht.creationspace.base.model.apimodel.BaseBeanContainer;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.module.user.security.ui.IBindPhoneActivity;
import com.lht.creationspace.util.internet.HttpUtil;
import com.lht.creationspace.util.string.StringUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.presenter
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> BindPhoneActivityPresenter
 * <p><b>Description</b>: TODO
 * <p>Created by Administrator on 2016/8/31.
 */

public class BindPhoneActivityPresenter implements IApiRequestPresenter,
        TimerClockModel.ISharedPreferenceProvider {

    private final IBindPhoneActivity iBindPhoneActivity;

    private String phone;
    private TimerClockModel mTimerClockModel;


    public BindPhoneActivityPresenter(IBindPhoneActivity iBindPhoneActivity) {
        this.iBindPhoneActivity = iBindPhoneActivity;

        mTimerClockModel = new TimerClockModel(this, new TimeLapseListenerImpl());
        mTimerClockModel.setPeriod(120000);
    }

    /**
     * 发送验证码
     *
     * @param targetPhone 目标手机号
     */
    public void callSendSmsVerifyCode(String targetPhone, String username) {
        //check phone num
        this.phone = targetPhone;
        this.username = username;

        CheckableJobs.getInstance()
                .next(new CellPhoneCheckJob(targetPhone))
                .onAllCheckLegal(new AbsOnAllCheckLegalListener<String>(targetPhone) {
                    @Override
                    public void onAllCheckLegal() {
                        iBindPhoneActivity.showWaitView(true);
                        IApiRequestModel mCheckPhoneModel = new CheckPhoneModel(getSavedParam(), new CheckPhoneModelCallback());
                        mCheckPhoneModel.doRequest(iBindPhoneActivity.getActivity());
                    }
                }).start();

    }

    /**
     * 页面结束时取消所有相关的回调
     *
     * @param context
     */
    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    /**
     * @return 记录文件
     */
    @Override
    public SharedPreferences getSharedPreferences() {
        return iBindPhoneActivity.getActivity()
                .getSharedPreferences(SPConstants.Timer.SP_TIMER, Context.MODE_PRIVATE);
    }

    /**
     * @return 记录的tag
     */
    @Override
    public String getRecordTag() {
        return getClass().getSimpleName();
    }

    public void resumeTimer() {
        mTimerClockModel.resume(1000);
    }

    private final class CheckPhoneModelCallback implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            IApiRequestModel mSendSmsModel = new SendSmsModel(SendSmsModel.SmsRequestType
                    .BindPhone, phone, new SendSmsCallbackImpl());
            mSendSmsModel.doRequest(iBindPhoneActivity.getActivity());
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iBindPhoneActivity.cancelWaitView();
            iBindPhoneActivity.showErrorMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iBindPhoneActivity.cancelWaitView();
            // TODO: 2016/7/21
        }
    }

    private final class SendSmsCallbackImpl implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iBindPhoneActivity.cancelWaitView();
            iBindPhoneActivity.showMsg(beanContainer.getData().getMessage());

            //cd
            mTimerClockModel.updateTimeStamp();
            mTimerClockModel.getTimeClock(1000).start();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            //TODO
            iBindPhoneActivity.cancelWaitView();
            iBindPhoneActivity.showErrorMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            //TODO
            iBindPhoneActivity.cancelWaitView();
        }
    }

    public boolean isPhoneNull() {
        String phone = iBindPhoneActivity.getPhone();
        if (StringUtil.isEmpty(phone)) {
            return true;
        }
        return false;
    }

    private String username;

//    private boolean isUpdate = false;

    /**
     * @param username
     * @param isUpdate
     */
    public void callBindPhone(String username, boolean isUpdate) {
        this.username = username;

        CheckableJobs.getInstance()
                .next(new CellPhoneCheckJob(iBindPhoneActivity.getPhone()))
                .next(new UnCompetedInputCheckJob(iBindPhoneActivity.getVerifyCode(),
                        R.string.v1010_default_bindphone_enter_verificationvode,
                        iBindPhoneActivity))
                .onAllCheckLegal(new AbsOnAllCheckLegalListener<Boolean>(isUpdate) {
                    @Override
                    public void onAllCheckLegal() {
                        iBindPhoneActivity.showWaitView(true);
                        //校验 成功后调用更新
                        if (getSavedParam()) {
                            // do update
                            //需要取old_valid_code  String username, String phone, String validCode, String oldValidCode
                            updateBindingPhone();
                        } else {
                            // do create
                            createBindingPhone();
                        }
                    }
                }).start();
    }

    private void updateBindingPhone() {
        UpdateBindPhoneModel.ModelRequestData data =
                new UpdateBindPhoneModel.ModelRequestData(
                        IVerifyHolder.mLoginInfo.getUsername(),
                        iBindPhoneActivity.getPhone(),
                        iBindPhoneActivity.getVerifyCode(),
                        iBindPhoneActivity.getOldVerifyCode());

        IApiRequestModel bindPhoneModel = new UpdateBindPhoneModel(data,
                new BindPhoneModelCallback());
        bindPhoneModel.doRequest(iBindPhoneActivity.getActivity());
    }

    private void createBindingPhone() {
        CreateBindPhoneModel.CreateBindPhoneData data = new CreateBindPhoneModel.CreateBindPhoneData();
        data.setUsername(IVerifyHolder.mLoginInfo.getUsername());
        data.setPhone(iBindPhoneActivity.getPhone());
        data.setValidCod(iBindPhoneActivity.getVerifyCode());
        IApiRequestModel bindPhoneModel = new CreateBindPhoneModel(data, new BindPhoneModelCallback());
        bindPhoneModel.doRequest(iBindPhoneActivity.getActivity());

    }


    private final class BindPhoneModelCallback
            implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iBindPhoneActivity.cancelWaitView();
            iBindPhoneActivity.showMsg(beanContainer.getData().getMessage());
            EventBus.getDefault().post(new AppEvent.PhoneBindEvent());
            iBindPhoneActivity.getActivity().finish();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iBindPhoneActivity.cancelWaitView();
            iBindPhoneActivity.showErrorMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iBindPhoneActivity.cancelWaitView();
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
            iBindPhoneActivity.initVCGetter(R.string.v1000_default_register_btn_getverify);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            iBindPhoneActivity.showCDRemaining(millisUntilFinished / 1000 + "s");
            iBindPhoneActivity.setVCGetterActiveStatus(false);
        }
    }

}
