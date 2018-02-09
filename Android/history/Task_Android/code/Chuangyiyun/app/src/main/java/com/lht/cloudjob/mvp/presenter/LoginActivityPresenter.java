package com.lht.cloudjob.mvp.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.MainApplication;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.asyncprotected.LoginActivity;
import com.lht.cloudjob.clazz.TpRegisterIntentFactroy;
import com.lht.cloudjob.interfaces.ITriggerCompare;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.interfaces.keys.SPConstants;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.interfaces.umeng.IUmengEventKey;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.BasicInfoModel;
import com.lht.cloudjob.mvp.model.LoginModel;
import com.lht.cloudjob.mvp.model.TpLoginModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.BasicInfoResBean;
import com.lht.cloudjob.mvp.model.bean.LoginResBean;
import com.lht.cloudjob.mvp.model.bean.TpLoginResBean;
import com.lht.cloudjob.mvp.model.pojo.LoginInfo;
import com.lht.cloudjob.mvp.model.pojo.LoginType;
import com.lht.cloudjob.mvp.viewinterface.ILoginActivity;
import com.lht.cloudjob.tplogin.ITPLoginViewPresenter;
import com.lht.cloudjob.tplogin.QQOAuth;
import com.lht.cloudjob.tplogin.SinaOAuth;
import com.lht.cloudjob.tplogin.TPLoginVerifyBean;
import com.lht.cloudjob.tplogin.WeChatOAuth;
import com.lht.cloudjob.util.SPUtil;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.tauth.IUiListener;

import org.greenrobot.eventbus.EventBus;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> LoginActivityPresenter
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/5/5.
 */
public class LoginActivityPresenter implements ITPLoginViewPresenter, IApiRequestPresenter {

    private final ILoginActivity iLoginActivity;

    public LoginActivityPresenter(ILoginActivity iLoginActivity) {
        this.iLoginActivity = iLoginActivity;

    }

    public void callLogin(Context context, String usr, String pwd) {
        iLoginActivity.showWaitView(true);
        IApiRequestModel mLoginModel = new LoginModel(usr, pwd, new LoginModelCallbackImpl());
        //TODO event key
        iLoginActivity.reportCountEvent("");
        mLoginModel.doRequest(context);
        SharedPreferences sp = iLoginActivity.getTokenPreferences();
        SPUtil.modifyString(sp, SPConstants.Token.KEY_ACCOUNT, usr);
    }

    public void callQQLogin(Activity activity, IUiListener qqLoginListener) {
        QQOAuth.login(activity, qqLoginListener, this);
    }

    @Override
    public void onTPPullUp() {
        iLoginActivity.showWaitView(true);
    }

    @Override
    public void onTPPullUpFinish() {
        iLoginActivity.cancelWaitView();
    }

    @Override
    public void onVarifyDecoded(TPLoginVerifyBean bean) {
        //三方授权后的逻辑
        if (!bean.isSuccess()) {
            iLoginActivity.cancelWaitView();
        } else {
            iLoginActivity.showWaitView(true);//because the fucking wechat,we resume an
            // async-protect

            //login on background
            TpLoginModel model = new TpLoginModel(bean.getType(),
                    bean.getUniqueId(), new TpLoginModelCallbackImpl());
            model.doRequest(iLoginActivity.getActivity());
        }

    }

    public void callSinaLogin(Activity activity, SsoHandler sinaSsoHandler) {
        SinaOAuth.login(activity, sinaSsoHandler, this);
    }

    public void callWechatLogin(Activity activity) {
        WeChatOAuth.login(activity, this);
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    class LoginModelCallbackImpl implements ApiModelCallback<LoginResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<LoginResBean> beanContainer) {
            //统计 手动登录成功-计数 忽略查询信息失败的
            iLoginActivity.reportCountEvent(IUmengEventKey.KEY_USER_LOGIN);

            LoginResBean data = beanContainer.getData();
            LoginInfo info = new LoginInfo();

            info.setAccessId(String.valueOf(data.getId()));
            info.setAccessToken(data.getVso_token());
            info.setUsername(data.getUsername());
            info.setNickname(data.getNickname());
            info.setAvatar(data.getAvatar());
            info.setLoginResBean(data);

            IVerifyHolder.mLoginInfo.copy(info);

            SharedPreferences sp = iLoginActivity.getTokenPreferences();
            SPUtil.modifyString(sp, SPConstants.Token.KEY_ACCESS_ID, info.getAccessId());
            SPUtil.modifyString(sp, SPConstants.Token.KEY_ACCESS_TOKEN, info.getAccessToken());
            SPUtil.modifyString(sp, SPConstants.Token.KEY_USERNAME, info.getUsername());

            doBasicUserInfoQuery(info);

        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            if (beanContainer.getData().getRet() == LoginResBean.RET_UNEXIST) {
                iLoginActivity.showRegisterGuideDialog();
            } else {
                iLoginActivity.showErrorMsg(beanContainer.getData().getMessage());
            }
            iLoginActivity.cancelWaitView();
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iLoginActivity.cancelWaitView();
            // TODO: 2016/7/28
        }
    }

    /**
     * 查询用户基本但不敏感信息，包括认证状态
     *
     * @param info 最包含最基本信息的数据模型
     */
    private void doBasicUserInfoQuery(LoginInfo info) {
        IApiRequestModel mUserinfoModel = new BasicInfoModel(info.getUsername(), new
                BasicInfoModelCallback(info));
        mUserinfoModel.doRequest(iLoginActivity.getActivity());
    }

    class TpLoginModelCallbackImpl implements ApiModelCallback<TpLoginResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<TpLoginResBean> beanContainer) {
            LoginInfo info = new LoginInfo();
            TpLoginResBean bean = beanContainer.getData();
            info.setAccessToken(bean.getVso_token());
            info.setAccessId(bean.getId());
            info.setUsername(bean.getUsername());
            info.setNickname(bean.getNickname());
            info.setAvatar(bean.getAvatar());

            info.setLoginResBean(TpLoginResBean.copy2LoginResBean(bean));

            if (bean.isResetNeed()) {
                info.setType(LoginType.Unlogin);
                IVerifyHolder.mLoginInfo.copy(info);

                Intent intent = TpRegisterIntentFactroy.create(iLoginActivity.getActivity(),
                        info, (ITriggerCompare) iLoginActivity.getLoginTrigger());
                iLoginActivity.getActivity().startActivity(intent);
                iLoginActivity.finishActivity();
            } else {
                IVerifyHolder.mLoginInfo.copy(info);
                //老用户，获取验证信息
                doBasicUserInfoQuery(info);
            }
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iLoginActivity.cancelWaitView();
            iLoginActivity.showErrorMsg(iLoginActivity.getAppResource()
                    .getString(R.string.v1012_toast_error_text_timeout));
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iLoginActivity.cancelWaitView();
            iLoginActivity.showErrorMsg(iLoginActivity.getAppResource()
                    .getString(R.string.v1012_toast_error_text_timeout));
        }
    }

    private final class BasicInfoModelCallback implements ApiModelCallback<BasicInfoResBean> {

        private final LoginInfo info;

        BasicInfoModelCallback(LoginInfo info) {
            this.info = info;
        }

        @Override
        public void onSuccess(BaseBeanContainer<BasicInfoResBean> beanContainer) {
            iLoginActivity.cancelWaitView();
            BasicInfoResBean data = beanContainer.getData();
            info.setType(data.getLoginType());
            info.setBasicUserInfo(data);

            //1.0.42 已不从引导页进入
//            if (((ITriggerCompare) iLoginActivity.getLoginTrigger()).equals(GuideActivity
//                    .LoginTrigger.GuideAccess)) {
//                iLoginActivity.jump2MainActivity(info);
//            } else {
            AppEvent.LoginSuccessEvent event = new AppEvent.LoginSuccessEvent(info);
            event.setTrigger((ITriggerCompare) iLoginActivity.getLoginTrigger());
            EventBus.getDefault().post(event);

            if (iLoginActivity.getActivity().getIntent().getBooleanExtra(LoginActivity
                    .KEY_START_HOME_ON_FINISH, false)) {
                iLoginActivity.jump2MainActivity(info);
            }
//            }

            //绑定设备
            MainApplication.getOurInstance().bindDevice();

            iLoginActivity.finishActivity();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iLoginActivity.cancelWaitView();
            iLoginActivity.showErrorMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iLoginActivity.cancelWaitView();

            // TODO: 2016/7/26
        }
    }
}
