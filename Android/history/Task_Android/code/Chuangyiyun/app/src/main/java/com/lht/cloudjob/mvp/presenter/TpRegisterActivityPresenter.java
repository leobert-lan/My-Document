package com.lht.cloudjob.mvp.presenter;

import android.content.Context;

import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.MainApplication;
import com.lht.cloudjob.interfaces.ITriggerCompare;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.ResetPwdByUsernameModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.pojo.LoginInfo;
import com.lht.cloudjob.mvp.model.pojo.LoginType;
import com.lht.cloudjob.mvp.viewinterface.ITpRegisterActivity;
import com.lht.cloudjob.util.CheckPwdUtil;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.lht.cloudjob.util.string.StringUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> ITpRegisterActivityPresenter
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/9/23
 */
public class TpRegisterActivityPresenter implements IApiRequestPresenter {

    private ITpRegisterActivity iTpRegisterActivity;

    public TpRegisterActivityPresenter(ITpRegisterActivity iTpRegisterActivity) {
        this.iTpRegisterActivity = iTpRegisterActivity;
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

    public void callSetPwd(LoginInfo verifyInfo, ITriggerCompare trigger, String pwd, String check) {
        if (StringUtil.isEmpty(pwd)) {
            iTpRegisterActivity.showMsg("请输入密码");
            return;
        }
        if (!pwd.equals(check)) {
            iTpRegisterActivity.showMsg("两次输入密码不一致");
            return;
        }
        //校验密码长度
        if (!CheckPwdUtil.checkPwdLengthLegal(pwd, iTpRegisterActivity)) {
            return;
        }
        doSetPwd(verifyInfo, trigger, pwd);

    }

    private void doSetPwd(LoginInfo verifyInfo, ITriggerCompare trigger, String pwd) {
        iTpRegisterActivity.showWaitView(true);
        ResetPwdByUsernameModel model = new ResetPwdByUsernameModel(verifyInfo.getUsername(),
                pwd, new ResetPwdByUsernameCallback(verifyInfo, trigger));

        model.doRequest(iTpRegisterActivity.getActivity());
    }

    class ResetPwdByUsernameCallback implements ApiModelCallback<BaseVsoApiResBean> {
        private LoginInfo verifyInfo;

        private ITriggerCompare trigger;

        public ResetPwdByUsernameCallback(LoginInfo verifyInfo, ITriggerCompare trigger) {
            this.verifyInfo = verifyInfo;
            this.trigger = trigger;
        }

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iTpRegisterActivity.cancelWaitView();
            verifyInfo.setType(LoginType.UnVerified);

//            @Deprecated 1.0.42不会从引导页进入
//            if (((ITriggerCompare) iTpRegisterActivity.getLoginTrigger()).equals(GuideActivity
//                    .LoginTrigger.GuideAccess)) {
//                iTpRegisterActivity.jump2SubscribeActivity(verifyInfo);//去订阅
//            } else {
            AppEvent.LoginSuccessEvent event = new AppEvent.LoginSuccessEvent(verifyInfo);
            event.setTrigger(trigger);
            EventBus.getDefault().post(event);
            iTpRegisterActivity.jump2SubscribeActivity(verifyInfo);//去订阅
//            }

            //绑定设备
            MainApplication.getOurInstance().bindDevice();


            iTpRegisterActivity.getActivity().finish();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iTpRegisterActivity.cancelWaitView();
            iTpRegisterActivity.showMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iTpRegisterActivity.cancelWaitView();
            // TODO: 2016/9/23
        }
    }
}
