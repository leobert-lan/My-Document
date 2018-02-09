package com.lht.creationspace.module.user.login;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.MainApplication;
import com.lht.creationspace.base.launcher.ITriggerCompare;
import com.lht.creationspace.base.model.apimodel.ApiModelCallback;
import com.lht.creationspace.base.model.apimodel.BaseBeanContainer;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.base.model.pojo.LoginInfo;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.cfg.SPConstants;
import com.lht.creationspace.checkable.AbsOnAllCheckLegalListener;
import com.lht.creationspace.checkable.CheckableJobs;
import com.lht.creationspace.checkable.jobs.UnCompetedInputCheckJob;
import com.lht.creationspace.module.user.info.ui.ac.UserInfoCreateActivity;
import com.lht.creationspace.module.user.login.model.LoginModel;
import com.lht.creationspace.module.user.login.model.TpLoginModel;
import com.lht.creationspace.module.user.login.model.pojo.LoginResBean;
import com.lht.creationspace.module.user.login.model.pojo.TpLoginResBean;
import com.lht.creationspace.module.user.login.ui.ILoginActivity;
import com.lht.creationspace.module.user.register.ui.ac.AccountCombineActivity;
import com.lht.creationspace.module.user.register.ui.ac.RoleChooseActivity;
import com.lht.creationspace.social.oauth.IOauthPresenter;
import com.lht.creationspace.social.oauth.QQOAuth;
import com.lht.creationspace.social.oauth.QQUserQueryCallback;
import com.lht.creationspace.social.oauth.SinaOAuth;
import com.lht.creationspace.social.oauth.TPOauthUserBean;
import com.lht.creationspace.social.oauth.WeChatOAuth;
import com.lht.creationspace.umeng.IUmengEventKey;
import com.lht.creationspace.util.SPUtil;
import com.lht.creationspace.util.debug.DLog;
import com.lht.creationspace.util.internet.HttpUtil;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.tauth.IUiListener;

import org.greenrobot.eventbus.EventBus;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.presenter
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> LoginActivityPresenter
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/5/5.
 */
public class LoginActivityPresenter implements IOauthPresenter, IApiRequestPresenter {

    private final ILoginActivity iLoginActivity;

    public LoginActivityPresenter(ILoginActivity iLoginActivity) {
        this.iLoginActivity = iLoginActivity;
    }

    public void callLogin(String usr, String pwd) {
        CheckableJobs.getInstance()
                .next(new UnCompetedInputCheckJob(usr,
                        R.string.v1000_default_login_null_account, iLoginActivity))
                .next(new UnCompetedInputCheckJob(pwd,
                        R.string.v1000_default_login_null_pwd, iLoginActivity))
//                .next(new PasswordCheckJob(pwd, iLoginActivity)) 不做本地规则校验
                .onAllCheckLegal(new AbsOnAllCheckLegalListener<STRU_LoginProof>(new STRU_LoginProof(usr, pwd)) {
                    @Override
                    public void onAllCheckLegal() {

                        iLoginActivity.showWaitView(true);
                        final String u = getSavedParam().getUsr();
                        final String p = getSavedParam().getPwd();
                        IApiRequestModel mLoginModel = new LoginModel(u, p,
                                new LoginModelCallbackImpl());
                        //        //TODO event key
                        //        iLoginActivity.reportCountEvent("");
                        mLoginModel.doRequest(iLoginActivity.getActivity());
                        SharedPreferences sp = iLoginActivity.getTokenPreferences();
                        SPUtil.modifyString(sp, SPConstants.Token.KEY_ACCOUNT, u);
                    }
                }).start();

    }

    public void jump2AccountCombineActivity(TPOauthUserBean oauthBean) {
        AccountCombineActivity.getLauncher(iLoginActivity.getActivity())
                .injectData(oauthBean)
                .launch();
    }

    public void jump2RoleChooseActivty(int valueSourceLogin) {
        RoleChooseActivity.getLauncher(iLoginActivity.getActivity())
                .injectData(transData(valueSourceLogin))
                .launch();
    }

    private RoleChooseActivity.RoleChooseActivityData transData(int valueSourceLogin) {
        RoleChooseActivity.RoleChooseActivityData data = new RoleChooseActivity.RoleChooseActivityData();
        data.setSourceTag(valueSourceLogin);
        return data;
    }

    public void jump2UserInfoCreateActivity(int valueSourceLogin) {
        UserInfoCreateActivity.getLauncher(iLoginActivity.getActivity())
                .injectData(tranData(valueSourceLogin))
                .launch();
    }

    private UserInfoCreateActivity.UserInfoCreateActivityData tranData(int valueSourceLogin) {
        UserInfoCreateActivity.UserInfoCreateActivityData data = new UserInfoCreateActivity.UserInfoCreateActivityData();
        data.setSourceTag(valueSourceLogin);
        return data;
    }

    private final class STRU_LoginProof {
        private final String usr;
        private final String pwd;

        public STRU_LoginProof(String usr, String pwd) {
            this.usr = usr;
            this.pwd = pwd;
        }

        public String getUsr() {
            return usr;
        }

        public String getPwd() {
            return pwd;
        }
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
        iLoginActivity.cancelSoftInputPanel();
        iLoginActivity.cancelWaitView();
    }

    @Override
    public void onOauthFinish(TPOauthUserBean bean) {
        DLog.d(getClass(), "oauth info:" + JSON.toJSONString(bean));
        //三方授权后的逻辑
        if (!bean.isSuccess()) { //三方授权失败
            iLoginActivity.cancelWaitView();
        } else {
            iLoginActivity.showWaitView(true);//because the fucking wechat,we resume an
            // async-protect
            TpLoginModel.TpLoginData data = new TpLoginModel.TpLoginData();
            data.setPlatform(bean.getType());
            data.setOpenId(bean.getUniqueId());
            TpLoginModel model = new TpLoginModel(data, new TpLoginModelCallbackImpl(bean));
            model.doRequest(iLoginActivity.getActivity());
        }
    }

    private void getTpUserInfo(TPOauthUserBean bean) {
        switch (bean.getType()) {
            case TPOauthUserBean.TYPE_QQ:
                IUiListener callback = new QQUserQueryCallback(bean, this);
                QQOAuth.getQAuthInfo(iLoginActivity.getActivity(), callback);
                break;
            case TPOauthUserBean.TYPE_SINA:
                SinaOAuth.getUserInfo(iLoginActivity.getActivity(), bean, this);
                break;
            case TPOauthUserBean.TYPE_WECHAT:
                WeChatOAuth.getUserInfo(bean, this);
                break;
            default:
                break;
        }
    }

    @Override
    public void onUserInfoGet(TPOauthUserBean bean) {
        iLoginActivity.cancelWaitView();
        jump2AccountCombineActivity(bean);
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

    private class LoginModelCallbackImpl implements ApiModelCallback<LoginResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<LoginResBean> beanContainer) {
            //统计 手动登录成功-计数 忽略查询信息失败的
            iLoginActivity.reportCountEvent(IUmengEventKey.KEY_USER_LOGIN);

            LoginResBean data = beanContainer.getData();
            LoginInfo info = new LoginInfo();

            info.setAccessId(data.getUid());
            info.setAccessToken(data.getVso_token());
            info.setUsername(data.getUsername());
            info.setNickname(data.getNickname());
            info.setAvatar(data.getAvatar());
            info.setHasChooseRole(data.getChoose_role_source() != 0);
            info.setLoginResBean(data);

            IVerifyHolder.mLoginInfo.copy(info);

            SharedPreferences sp = iLoginActivity.getTokenPreferences();
            SPUtil.modifyString(sp, SPConstants.Token.KEY_ACCESS_ID, info.getAccessId());
            SPUtil.modifyString(sp, SPConstants.Token.KEY_ACCESS_TOKEN, info.getAccessToken());
            SPUtil.modifyString(sp, SPConstants.Token.KEY_USERNAME, info.getUsername());

            AppEvent.LoginSuccessEvent event = new AppEvent.LoginSuccessEvent(info);
            event.setTrigger((ITriggerCompare) iLoginActivity.getLoginTrigger());
            EventBus.getDefault().post(event);

//            doBasicUserInfoQuery(info);
            iLoginActivity.cancelWaitView();
            //绑定设备
            MainApplication.getOurInstance().bindDevice();

            iLoginActivity.finishActivity();

        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            if (beanContainer.getData().getRet() == LoginResBean.RET_UNEXIST) {
                iLoginActivity.showRegisterGuideDialog();
            } else {
                String msg = LoginResBean.parseMsgByRet(beanContainer.getData().getRet(), beanContainer.getData());
                iLoginActivity.showErrorMsg(msg);
            }
            iLoginActivity.cancelWaitView();
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iLoginActivity.cancelWaitView();
            // TODO: 2016/7/28
        }
    }

    private class TpLoginModelCallbackImpl
            implements ApiModelCallback<TpLoginResBean> {

        private TPOauthUserBean oauthBean;

        public TpLoginModelCallbackImpl(TPOauthUserBean oauthBean) {
            this.oauthBean = oauthBean;
        }

        @Override
        public void onSuccess(BaseBeanContainer<TpLoginResBean> beanContainer) {
            iLoginActivity.cancelWaitView();
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

            SharedPreferences sp = iLoginActivity.getTokenPreferences();
            SPUtil.modifyString(sp, SPConstants.Token.KEY_ACCESS_ID, info.getAccessId());
            SPUtil.modifyString(sp, SPConstants.Token.KEY_ACCESS_TOKEN, info.getAccessToken());
            SPUtil.modifyString(sp, SPConstants.Token.KEY_USERNAME, info.getUsername());

            AppEvent.LoginSuccessEvent event = new AppEvent.LoginSuccessEvent(info);
            event.setTrigger((ITriggerCompare) iLoginActivity.getLoginTrigger());
            EventBus.getDefault().post(event);

            //绑定设备
            MainApplication.getOurInstance().bindDevice();
            iLoginActivity.finishActivity();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iLoginActivity.cancelWaitView();
            if (beanContainer.getData().getRet() == TpLoginResBean.RET_ERROR_UNREGISTER)
                getTpUserInfo(oauthBean);
            else
                iLoginActivity.showErrorMsg(beanContainer.getData().getMsg());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iLoginActivity.cancelWaitView();
            iLoginActivity.showErrorMsg(iLoginActivity.getAppResource()
                    .getString(R.string.v1012_toast_error_text_timeout));
        }
    }
}
