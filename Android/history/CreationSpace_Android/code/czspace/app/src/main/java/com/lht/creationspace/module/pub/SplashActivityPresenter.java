package com.lht.creationspace.module.pub;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.base.MainApplication;
import com.lht.creationspace.base.launcher.ITriggerCompare;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.cfg.SPConstants;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.base.model.apimodel.ApiModelCallback;
import com.lht.creationspace.module.user.info.model.QueryBasicInfoModel;
import com.lht.creationspace.base.model.apimodel.BaseBeanContainer;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.module.user.info.model.pojo.BasicInfoResBean;
import com.lht.creationspace.base.model.pojo.LoginInfo;
import com.lht.creationspace.module.pub.ui.ISplashActivity;
import com.lht.creationspace.util.SPUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;

public class SplashActivityPresenter implements IApiRequestPresenter {


    private final String username;
    private final String accessToken;
    private final ISplashActivity iSplashActivity;


    public SplashActivityPresenter(ISplashActivity iSplashActivity) {
        this.iSplashActivity = iSplashActivity;
        SharedPreferences tokens = iSplashActivity.getTokenPreferences();

        username = tokens.getString(SPConstants.Token.KEY_USERNAME, "");
        accessToken = tokens.getString(SPConstants.Token.KEY_ACCESS_TOKEN, "");
    }


    public void startSplash(int duration) {
        SharedPreferences basicSharedPreferences = iSplashActivity.getBasicPreferences();
        int count = basicSharedPreferences.getInt(SPConstants.Basic.KEY_STARTCOUNT, 0);
        if (count == 0) {
            IVerifyHolder.mLoginInfo.copy(new LoginInfo());
            doGuide(duration);
        } else {
            doBackgroundLogin(duration);
        }
        SPUtil.modifyInt(basicSharedPreferences, SPConstants.Basic.KEY_STARTCOUNT, count + 1);
    }

    private void doBackgroundLogin(int duration) {
        QueryBasicInfoModel model = new QueryBasicInfoModel(username, accessToken, new
                LoginStateCallImpl());
        model.doRequest(iSplashActivity.getActivity(), false);

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                iSplashActivity.jump2Main();
                iSplashActivity.finishActivity();
            }
        }, duration);
    }

    private void doGuide(int duration) {
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                iSplashActivity.jump2Guide();
                iSplashActivity.finishActivity();
            }
        }, duration);
    }

    @Override
    public void cancelRequestOnFinish(Context context) {

    }

    private class LoginStateCallImpl
            implements ApiModelCallback<BasicInfoResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BasicInfoResBean> beanContainer) {
            BasicInfoResBean data = beanContainer.getData();
            LoginInfo info = new LoginInfo();

            info.setAccessId(data.getUid());
            info.setAccessToken(accessToken);
            info.setUsername(data.getUsername());
            info.setNickname(data.getNickname());
            info.setAvatar(data.getAvatar());
            info.setHasChooseRole(data.getChoose_role_source() != 0);

            info.setLoginResBean(BasicInfoResBean.trans2LoginResBean(data));

            IVerifyHolder.mLoginInfo.copy(info);

            SharedPreferences sp = iSplashActivity.getTokenPreferences();
            SPUtil.modifyString(sp, SPConstants.Token.KEY_ACCESS_ID, info.getAccessId());
            SPUtil.modifyString(sp, SPConstants.Token.KEY_ACCESS_TOKEN, info.getAccessToken());
            SPUtil.modifyString(sp, SPConstants.Token.KEY_USERNAME, info.getUsername());

            //绑定设备
            MainApplication.getOurInstance().bindDevice();

            //section---------------------------------
            //onDestroy发生在新Activity onCreate之后，可能出现这样的情况：页面跳转了，走未登录的逻辑，
            // 但onDestroy还未回调，没有取消网络请求的回调，而此时请求已经成功，执行了回调中的代码，
            // 更新了登录状态，这样侧边栏得不到立即刷新，需要重新发出事件以刷新侧边栏
            //否则表现为：侧边栏展示未登录、需求管理、消息页面已处于登录  v1.0.12 leobert
            AppEvent.LoginSuccessEvent event = new AppEvent.LoginSuccessEvent(info);
            event.setTrigger(LoginTrigger.BackgroundLogin);
            EventBus.getDefault().postSticky(event);

        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            IVerifyHolder.mLoginInfo.copy(new LoginInfo());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            IVerifyHolder.mLoginInfo.copy(new LoginInfo());
        }
    }

    /**
     * splash页面登录触发
     */
    public enum LoginTrigger implements ITriggerCompare {
        BackgroundLogin(1);

        private final int tag;

        LoginTrigger(int i) {
            tag = i;
        }

        @Override
        public boolean equals(ITriggerCompare compare) {
            boolean b1 = compare.getClass().getName().equals(getClass().getName());
            boolean b2 = compare.getTag().equals(getTag());
            return b1 & b2;
        }

        @Override
        public Object getTag() {
            return tag;
        }

        @Override
        public Serializable getSerializable() {
            return this;
        }

    }
}
