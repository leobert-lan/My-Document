package com.lht.cloudjob.mvp.presenter;

import android.content.Context;
import android.content.SharedPreferences;

import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.R;
import com.lht.cloudjob.interfaces.keys.SPConstants;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.CreateBindPhoneModel;
import com.lht.cloudjob.mvp.model.SendSmsModel;
import com.lht.cloudjob.mvp.model.UpdateBindPhoneModel;
import com.lht.cloudjob.mvp.model.CheckPhoneModel;
import com.lht.cloudjob.mvp.model.SendSmsOnBindPhoneModel;
import com.lht.cloudjob.mvp.model.TimerClockModel;
import com.lht.cloudjob.mvp.model.VerifyCodeCheckModel;
import com.lht.cloudjob.mvp.model.VerifyCodeCheckWithUserModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.viewinterface.IBindPhoneActivity;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.lht.cloudjob.util.string.StringUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
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
    public void callSendSmsVerifyCode(String targetPhone, String username, boolean isUpdate) {
        //check phone num
        this.phone = targetPhone;
        this.username = username;
        this.isUpdate = isUpdate;
        iBindPhoneActivity.showWaitView(true);
        int phoneLength = 11;
        if (targetPhone.length() != phoneLength) {
            iBindPhoneActivity.showErrorMsg(iBindPhoneActivity.getActivity().getString(R.string
                    .v1010_default_bindphone_phonestyle_error));
            iBindPhoneActivity.cancelWaitView();
            return;
        }
        //use api to check whether phone usable
        IApiRequestModel mCheckPhoneModel = new CheckPhoneModel(targetPhone, new
                CheckPhoneModelCallback());
        mCheckPhoneModel.doRequest(iBindPhoneActivity.getActivity());
        //Callback中处理发送验证码

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
        return "bindPhone";
    }

    public void resumeTimer() {
        mTimerClockModel.resume(1000);
    }

    private final class CheckPhoneModelCallback implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            // 发送验证码
            if (isUpdate) {
                IApiRequestModel mSendSmsModel = new SendSmsOnBindPhoneModel(
                        phone, username, new SendSmsCallbackImpl());
                mSendSmsModel.doRequest(iBindPhoneActivity.getActivity());
            } else {
                IApiRequestModel mSendSmsModel = new SendSmsModel(SendSmsModel.SmsRequestType
                        .Normal, phone, new SendSmsCallbackImpl());
                mSendSmsModel.doRequest(iBindPhoneActivity.getActivity());
            }
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

    private boolean isUpdate = false;

    public void callBindPhone(String username, boolean isUpdate) {
        this.username = username;
        this.isUpdate = isUpdate;
        if (isPhoneNull()) {
            iBindPhoneActivity.showErrorMsg(iBindPhoneActivity.getAppResource().getString(R
                    .string.v1010_default_bindphone_enter_phonenumber));
            return;
        }

        if (StringUtil.isEmpty(iBindPhoneActivity.getVerifyCode())) {
            iBindPhoneActivity.showErrorMsg(iBindPhoneActivity.getAppResource().getString(R
                    .string.v1010_default_bindphone_enter_verificationvode));
            return;
        }

        iBindPhoneActivity.showWaitView(true);
        //校验 成功后调用更新

        if (isUpdate) {
            //更新使用带username的检验
            IApiRequestModel mCheckVerifyCodeModel = new VerifyCodeCheckWithUserModel(username,
                    iBindPhoneActivity.getVerifyCode(), new VerifyCodeCheckModelCallback());
            mCheckVerifyCodeModel.doRequest(iBindPhoneActivity.getActivity());
        } else {
            IApiRequestModel mCheckVerifyCodeModel = new VerifyCodeCheckModel(iBindPhoneActivity
                    .getPhone(),
                    iBindPhoneActivity.getVerifyCode(), new VerifyCodeCheckModelCallback());
            mCheckVerifyCodeModel.doRequest(iBindPhoneActivity.getActivity());
        }
    }

    private final class VerifyCodeCheckModelCallback implements
            ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            if (isUpdate) {
                // do update
                IApiRequestModel bindPhoneModel = new UpdateBindPhoneModel(username,
                        iBindPhoneActivity.getPhone(),
                        new BindPhoneModelCallback());
                bindPhoneModel.doRequest(iBindPhoneActivity.getActivity());
            } else {
                // do create
                IApiRequestModel bindPhoneModel = new CreateBindPhoneModel(username,
                        iBindPhoneActivity.getPhone(),
                        new BindPhoneModelCallback());
                bindPhoneModel.doRequest(iBindPhoneActivity.getActivity());
            }
            iBindPhoneActivity.cancelWaitView();
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

    private final class BindPhoneModelCallback implements ApiModelCallback<BaseVsoApiResBean> {

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
    private final class TimeLapseListenerImpl implements TimerClockModel.OnTimeLapseListener {

        @Override
        public void onFinish() {
            mTimerClockModel.updateTimeStamp();
            iBindPhoneActivity.initVCGetter(R.string.v1010_default_register_btn_getverify);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            iBindPhoneActivity.showCDRemaining(millisUntilFinished / 1000 + "s");
            iBindPhoneActivity.setVCGetterActiveStatus(false);
        }
    }

}
