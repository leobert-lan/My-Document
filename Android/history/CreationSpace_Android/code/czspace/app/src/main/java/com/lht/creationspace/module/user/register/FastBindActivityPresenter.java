package com.lht.creationspace.module.user.register;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.base.MainApplication;
import com.lht.creationspace.R;
import com.lht.creationspace.checkable.AbsCheckJob;
import com.lht.creationspace.checkable.AbsOnAllCheckLegalListener;
import com.lht.creationspace.checkable.CheckableJobs;
import com.lht.creationspace.checkable.jobs.CellPhoneCheckJob;
import com.lht.creationspace.checkable.jobs.PasswordCheckJob;
import com.lht.creationspace.checkable.jobs.UnCompetedInputCheckJob;
import com.lht.creationspace.customview.popup.CustomPopupWindow;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.cfg.SPConstants;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.base.model.apimodel.ApiModelCallback;
import com.lht.creationspace.module.user.security.model.CheckPhoneModel;
import com.lht.creationspace.module.user.security.model.SendSmsModel;
import com.lht.creationspace.base.model.TextWatcherModel;
import com.lht.creationspace.base.model.TimerClockModel;
import com.lht.creationspace.module.user.login.model.TpLoginModel;
import com.lht.creationspace.module.user.register.model.TpRegWithOauthBindModel;
import com.lht.creationspace.base.model.apimodel.BaseBeanContainer;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.module.user.login.model.pojo.TpLoginResBean;
import com.lht.creationspace.base.model.pojo.LoginInfo;
import com.lht.creationspace.module.user.register.ui.IFastBandActivity;
import com.lht.creationspace.social.oauth.TPOauthUserBean;
import com.lht.creationspace.util.internet.HttpUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * 三方授权->不存在 快速绑定注册 presenter
 * Created by chhyu on 2017/2/17.
 */

public class FastBindActivityPresenter implements
        IApiRequestPresenter, TimerClockModel.ISharedPreferenceProvider {

    private IFastBandActivity iFastBandActivity;
    private final TextWatcherModel model;

    private TimerClockModel mTimerClockModel;

    public FastBindActivityPresenter(IFastBandActivity iFastBandActivity) {
        this.iFastBandActivity = iFastBandActivity;
        model = new TextWatcherModel(new TextWatcherModelCallbackImpl());
        mTimerClockModel = new TimerClockModel(this, new TimeLapseListenerImpl());
        mTimerClockModel.setPeriod(120000);
    }

    public void watchText(EditText editText, int maxLength) {
        model.doWatcher(editText, maxLength);
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return iFastBandActivity.getActivity()
                .getSharedPreferences(SPConstants.Timer.SP_TIMER, Context.MODE_PRIVATE);
    }

    public void resumeTimer() {
        mTimerClockModel.resume(1000);
    }

    public void cancelTimer() {
        mTimerClockModel.cancel();
    }

    @Override
    public String getRecordTag() {
        return getClass().getSimpleName();
    }

    private boolean isProtocalAgreed = false;

    public void setIsProtocolAgreed(boolean isChecked) {
        isProtocalAgreed = isChecked;
    }

    /**
     * 只用三方注册，不绑定手机
     */
    public void callSingleRegister(TPOauthUserBean oauthBean) {
        iFastBandActivity.showWaitView(true);//safety
        IApiRequestModel fastBindModel = new TpRegWithOauthBindModel(oauthBean,
                new FastBindModelCallback(oauthBean));
        fastBindModel.doRequest(iFastBandActivity.getActivity());
    }

    private class TextWatcherModelCallbackImpl
            implements TextWatcherModel.TextWatcherModelCallback {

        @Override
        public void onOverLength(int edittextId, int maxLength) {
            iFastBandActivity.showMsg(iFastBandActivity.getActivity()
                    .getString(R.string.v1000_default_fastbind_text_toast_phone_max));
        }

        @Override
        public void onChanged(int edittextId, int currentCount, int remains) {

        }
    }

    private final class TimeLapseListenerImpl implements TimerClockModel.OnTimeLapseListener {

        @Override
        public void onFinish() {
            mTimerClockModel.updateTimeStamp();
            iFastBandActivity.initVCGetter(R.string.v1000_default_register_btn_getverify);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            iFastBandActivity.showCDRemaining(millisUntilFinished / 1000 + "s");
            iFastBandActivity.setVCGetterActiveStatus(false);
        }
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    /**
     * 获取验证码
     *
     * @param phone 用户输入的手机号
     */
    public void callGetVerifyCode(final String phone) {
        CheckableJobs.getInstance()
                .next(new UnCompetedInputCheckJob(phone, R.string.v1000_default_fastbind_text_toast_phone, iFastBandActivity))
                .next(new CellPhoneCheckJob(phone))
                .onAllCheckLegal(new AbsOnAllCheckLegalListener<String>(phone) {
                    @Override
                    public void onAllCheckLegal() {
                        iFastBandActivity.showWaitView(true);
                        //后台检验手机号
                        IApiRequestModel mCheckPhoneModel = new CheckPhoneModel(getSavedParam(),
                                new CheckPhoneModelCallback(getSavedParam()));
                        mCheckPhoneModel.doRequest(iFastBandActivity.getActivity());
                    }
                }).start();
    }

    private final class CheckPhoneModelCallback implements ApiModelCallback<BaseVsoApiResBean> {
        private String phone;

        public CheckPhoneModelCallback(String phone) {
            this.phone = phone;
        }

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iFastBandActivity.cancelWaitView();
            //校验成功之后发送验证码短信  虽然是注册，但先使用普通验证码
            IApiRequestModel mSendSmsModel = new SendSmsModel(SendSmsModel.SmsRequestType
                    .Register, phone, new SendSmsCallbackImpl());
            mSendSmsModel.doRequest(iFastBandActivity.getActivity());
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iFastBandActivity.cancelWaitView();
            if (beanContainer.getData().getRet() == CheckPhoneModel.RET_EXIST) {
                notifyPhoneConflict();
            } else {
                iFastBandActivity.showMsg(beanContainer.getData().getMessage());
            }
        }

        private void notifyPhoneConflict() {
            iFastBandActivity.showPhoneConflictDialog("手机号已注册", "去登录",
                    new CustomPopupWindow.OnPositiveClickListener() {
                        @Override
                        public void onPositiveClick() {
                            //关闭以到登录页
                            EventBus.getDefault().post(new AppEvent.AuthSetAccountEvent(phone));
                            iFastBandActivity.getActivity().finish();
                        }
                    });
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iFastBandActivity.cancelWaitView();
        }
    }

    private final class SendSmsCallbackImpl implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            mTimerClockModel.cancel();
            mTimerClockModel.updateTimeStamp();
            mTimerClockModel.getTimeClock(1000).start();
            iFastBandActivity.showMsg(beanContainer.getData().getMessage());
            iFastBandActivity.cancelWaitView();

        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iFastBandActivity.cancelWaitView();
            iFastBandActivity.showMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iFastBandActivity.cancelWaitView();
        }
    }


    /**
     * 快速绑定
     */
    public void callFastBind(String mobile, String pwd, String verifycode, TPOauthUserBean bean) {
        //本地校验数据完整性
        CheckableJobs.getInstance()
                //非空手机号
                .next(new UnCompetedInputCheckJob(mobile,
                        R.string.v1000_default_fastbind_text_toast_phone, iFastBandActivity))
                //合法手机号
                .next(new CellPhoneCheckJob(mobile))
                //非空验证码
                .next(new UnCompetedInputCheckJob(verifycode,
                        R.string.v1000_default_fastbind_text_toast_verifycode, iFastBandActivity))
                //非空密码
                .next(new UnCompetedInputCheckJob(pwd,
                        R.string.v1000_default_fastbind_text_toast_password, iFastBandActivity))
                //合法密码
                .next(new PasswordCheckJob(pwd, iFastBandActivity))
                //check agree protocol
                .next(new AbsCheckJob() {
                    @Override
                    public boolean check() {
                        return isProtocalAgreed;
                    }

                    @Override
                    public void onCheckIllegal() {
                        iFastBandActivity.showMsg(iFastBandActivity.getActivity().getString(R.string
                                .v1000_default_register_toast_read_agreement));
                    }
                })
                //验证验证码 通过后发送绑定请求
                .onAllCheckLegal(new AbsOnAllCheckLegalListener<STRUC_FastBindData>(
                        new STRUC_FastBindData(mobile, pwd, verifycode, bean)) {
                    @Override
                    public void onAllCheckLegal() {
                        TpRegWithOauthBindModel.TpRegWithOauthBindData data = new TpRegWithOauthBindModel.TpRegWithOauthBindData();
                        data.setMobile(getSavedParam().getMobile());
                        data.setPwd(getSavedParam().getPwd());
                        data.setValidCode(getSavedParam().getVerifycode());
                        data.setBean(getSavedParam().getOauthBean());
                        iFastBandActivity.showWaitView(true);//safety
                        IApiRequestModel fastBindModel = new TpRegWithOauthBindModel(data, new FastBindModelCallback(getSavedParam().getOauthBean()));
                        fastBindModel.doRequest(iFastBandActivity.getActivity());
                    }
                }).start();
    }

    private void doSilentTpLogin(TPOauthUserBean oauthBean) {
        TpLoginModel.TpLoginData data = new TpLoginModel.TpLoginData();
        data.setOpenId(oauthBean.getUniqueId());
        data.setPlatform(oauthBean.getType());
        iFastBandActivity.showWaitView(true);
        TpLoginModel model = new TpLoginModel(data, new TpLoginModelCallbackImpl());
        model.doRequest(iFastBandActivity.getActivity());
    }

    private class FastBindModelCallback implements ApiModelCallback<BaseVsoApiResBean> {

        private final TPOauthUserBean savedBean;

        public FastBindModelCallback(TPOauthUserBean oauthBean) {
            this.savedBean = oauthBean;
        }

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iFastBandActivity.cancelWaitView();
            //静默登录
            doSilentTpLogin(savedBean);
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iFastBandActivity.cancelWaitView();
            iFastBandActivity.showMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iFastBandActivity.cancelWaitView();
        }
    }

    private class TpLoginModelCallbackImpl implements ApiModelCallback<TpLoginResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<TpLoginResBean> beanContainer) {
            iFastBandActivity.cancelWaitView();
            LoginInfo info = new LoginInfo();
            TpLoginResBean bean = beanContainer.getData();
            info.setAccessToken(bean.getVso_token());
            info.setAccessId(bean.getId());
            info.setUsername(bean.getUsername());
            info.setNickname(bean.getNickname());
            info.setAvatar(bean.getAvatar());
            info.setHasChooseRole(bean.getChoose_role_source() != 0);
            info.setLoginResBean(TpLoginResBean.copy2LoginResBean(bean));
            IVerifyHolder.mLoginInfo.copy(info);

            AppEvent.TpRegSilentLoginSuccessEvent event = new AppEvent.TpRegSilentLoginSuccessEvent(info);
            EventBus.getDefault().post(event);

            //绑定设备
            MainApplication.getOurInstance().bindDevice();
            iFastBandActivity.jump2RoleChoose();
            iFastBandActivity.getActivity().finish();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iFastBandActivity.cancelWaitView();
            iFastBandActivity.showMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iFastBandActivity.cancelWaitView();
            iFastBandActivity.showMsg("静默登录失败"); //优化建议：关闭支链回到登录页
            iFastBandActivity.getActivity().finish();
        }

    }

    /**
     * 结构-
     */
    private class STRUC_FastBindData {
        private final String mobile;
        private final String pwd;
        private final String verifycode;
        private final TPOauthUserBean oauthBean;

        public STRUC_FastBindData(String mobile, String pwd, String verifycode, TPOauthUserBean oauthBean) {
            this.mobile = mobile;
            this.pwd = pwd;
            this.verifycode = verifycode;
            this.oauthBean = oauthBean;
        }

        public String getMobile() {
            return mobile;
        }

        public String getPwd() {
            return pwd;
        }

        public String getVerifycode() {
            return verifycode;
        }

        public TPOauthUserBean getOauthBean() {
            return oauthBean;
        }
    }
}
