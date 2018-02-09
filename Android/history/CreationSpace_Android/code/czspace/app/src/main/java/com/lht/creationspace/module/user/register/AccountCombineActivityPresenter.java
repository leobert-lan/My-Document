package com.lht.creationspace.module.user.register;

import android.content.Context;
import android.content.SharedPreferences;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.MainApplication;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.base.model.apimodel.ApiModelCallback;
import com.lht.creationspace.base.model.apimodel.BaseBeanContainer;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.base.model.pojo.LoginInfo;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.cfg.SPConstants;
import com.lht.creationspace.checkable.AbsOnAllCheckLegalListener;
import com.lht.creationspace.checkable.CheckableJobs;
import com.lht.creationspace.checkable.jobs.UnCompetedInputCheckJob;
import com.lht.creationspace.module.user.info.model.ThirdAccountBindModel;
import com.lht.creationspace.module.user.login.model.LoginModel;
import com.lht.creationspace.module.user.login.model.pojo.LoginResBean;
import com.lht.creationspace.module.user.register.ui.IAccountCombineActivity;
import com.lht.creationspace.module.user.register.ui.ac.FastBindActivity;
import com.lht.creationspace.social.oauth.TPOauthUserBean;
import com.lht.creationspace.util.SPUtil;
import com.lht.creationspace.util.internet.HttpUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * <p><b>Package:</b> com.lht.creationspace.mvp.presenter </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> AccountCombineActivityPresenter </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/3/22.
 */

public class AccountCombineActivityPresenter
        implements IApiRequestPresenter {

    private TPOauthUserBean oauthBean;

    private IAccountCombineActivity iAccountCombineActivity;

    public AccountCombineActivityPresenter(IAccountCombineActivity iAccountCombineActivity) {
        this.iAccountCombineActivity = iAccountCombineActivity;

        oauthBean = AbsActivityLauncher.parseData(iAccountCombineActivity.getActivity().getIntent(),
                TPOauthUserBean.class);
//        oauthBean = JSON.parseObject(_data, TPOauthUserBean.class);
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    public void callAccountCombine(String account, String pwd) {
        LoginModel.ModelRequestData data = new LoginModel.ModelRequestData(account, pwd);
        CheckableJobs.getInstance()
                .next(new UnCompetedInputCheckJob(account, R.string.v1000_default_accountcontact_hint_account,
                        iAccountCombineActivity))
//                .next(new PasswordCheckJob(pwd, iAccountCombineActivity))
                .onAllCheckLegal(new AbsOnAllCheckLegalListener<LoginModel.ModelRequestData>(data) {
                    @Override
                    public void onAllCheckLegal() {
                        iAccountCombineActivity.showWaitView(true);
                        LoginModel model = new LoginModel(getSavedParam(),
                                new LoginModelCallbackImpl(oauthBean));
                        model.doRequest(iAccountCombineActivity.getActivity());
                    }
                }).start();

    }

    public void callFastBind() {
        FastBindActivity.getLauncher(iAccountCombineActivity.getActivity())
                .injectData(oauthBean)
                .launch();
    }

    private void doBind(LoginResBean loginResBean, TPOauthUserBean data) {
        iAccountCombineActivity.showWaitView(true);
        ThirdAccountBindModel model = new ThirdAccountBindModel(loginResBean.getUsername(),
                data.getType(), data.getUniqueId(), loginResBean, new ThirdAccountBindModelCallback(loginResBean));
        model.doRequest(iAccountCombineActivity.getActivity());
    }

    private class LoginModelCallbackImpl implements ApiModelCallback<LoginResBean> {

        private TPOauthUserBean oauthUserBean;

        public LoginModelCallbackImpl(TPOauthUserBean oauthUserBean) {
            this.oauthUserBean = oauthUserBean;
        }

        @Override
        public void onSuccess(BaseBeanContainer<LoginResBean> beanContainer) {
            LoginResBean data = beanContainer.getData();
            doBind(data, oauthUserBean);
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iAccountCombineActivity.cancelWaitView();
            String msg = LoginResBean.parseMsgByRet(beanContainer.getData().getRet(), beanContainer.getData());
            iAccountCombineActivity.showMsg(msg);
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iAccountCombineActivity.cancelWaitView();
        }
    }

    private class ThirdAccountBindModelCallback
            implements ApiModelCallback<BaseVsoApiResBean> {
        private LoginResBean loginResBean;

        public ThirdAccountBindModelCallback(LoginResBean loginResBean) {
            this.loginResBean = loginResBean;
        }


        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iAccountCombineActivity.cancelWaitView();
            iAccountCombineActivity.showMsg("绑定成功");

            LoginInfo info = new LoginInfo();

            info.setAccessId(loginResBean.getUid());
            info.setAccessToken(loginResBean.getVso_token());
            info.setUsername(loginResBean.getUsername());
            info.setNickname(loginResBean.getNickname());
            info.setAvatar(loginResBean.getAvatar());
            info.setHasChooseRole(loginResBean.getChoose_role_source() != 0);
            info.setLoginResBean(loginResBean);

            IVerifyHolder.mLoginInfo.copy(info);

            SharedPreferences sp = iAccountCombineActivity.getTokenPreferences();
            SPUtil.modifyString(sp, SPConstants.Token.KEY_ACCESS_ID, info.getAccessId());
            SPUtil.modifyString(sp, SPConstants.Token.KEY_ACCESS_TOKEN, info.getAccessToken());
            SPUtil.modifyString(sp, SPConstants.Token.KEY_USERNAME, info.getUsername());

            //绑定设备
            MainApplication.getOurInstance().bindDevice();

            AppEvent.TpRegSilentLoginSuccessEvent event = new AppEvent.TpRegSilentLoginSuccessEvent(info);
            EventBus.getDefault().post(event);
            iAccountCombineActivity.getActivity().finish();

        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iAccountCombineActivity.cancelWaitView();
            iAccountCombineActivity.showMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iAccountCombineActivity.cancelWaitView();
            iAccountCombineActivity.showMsg("绑定失败");
        }
    }
}
