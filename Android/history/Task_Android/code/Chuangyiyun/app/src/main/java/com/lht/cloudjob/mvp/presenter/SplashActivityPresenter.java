package com.lht.cloudjob.mvp.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.interfaces.ITriggerCompare;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.interfaces.keys.SPConstants;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.BasicInfoModel;
import com.lht.cloudjob.mvp.model.CheckLoginStateModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.BasicInfoResBean;
import com.lht.cloudjob.mvp.model.pojo.LoginInfo;
import com.lht.cloudjob.mvp.viewinterface.ISplashActivity;
import com.lht.cloudjob.util.SPUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;

public class SplashActivityPresenter implements IApiRequestPresenter {


    private final String username;
    private final String accessToken;
    private final String accessId;
    private final ISplashActivity iSplashActivity;


    public SplashActivityPresenter(ISplashActivity iSplashActivity) {
        this.iSplashActivity = iSplashActivity;
        SharedPreferences tokens = iSplashActivity.getTokenPreferences();

        username = tokens.getString(SPConstants.Token.KEY_USERNAME, "");
        accessToken = tokens.getString(SPConstants.Token.KEY_ACCESS_TOKEN, "");
        accessId = tokens.getString(SPConstants.Token.KEY_ACCESS_ID, "");

    }


    public void startSplash(int duration) {
        SharedPreferences basicSharedPreferences = iSplashActivity.getBasicPreferences();
        int count = basicSharedPreferences.getInt(SPConstants.Basic.KEY_STARTCOUNT, 0);
        if (count == 0) {
            doGuide(duration);
        } else {
            doBackgroundLogin(duration);
        }
        SPUtil.modifyInt(basicSharedPreferences, SPConstants.Basic.KEY_STARTCOUNT, count + 1);
    }

    private boolean isLogined;

    private void doBackgroundLogin(int duration) {
        IApiRequestModel model = new CheckLoginStateModel(username, accessToken, new
                LoginStateCallImpl());
        model.doRequest(iSplashActivity.getActivity());

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                iSplashActivity.jump2Main(isLogined);
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

    class LoginStateCallImpl implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            LoginInfo info = new LoginInfo(username, accessToken, accessId);
            //仅当信息获取到才认为是登录成功
            isLogined = false;
            doBasicUserInfoQuery(info);
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            isLogined = false;
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            isLogined = false;
        }
    }

    private void doBasicUserInfoQuery(LoginInfo info) {
        IApiRequestModel mUserinfoModel = new BasicInfoModel(info.getUsername(), new
                BasicInfoModelCallback(info));
        mUserinfoModel.doRequest(iSplashActivity.getActivity());
    }

    private final class BasicInfoModelCallback implements ApiModelCallback<BasicInfoResBean> {

        private final LoginInfo info;

        BasicInfoModelCallback(LoginInfo info) {
            this.info = info;
        }

        @Override
        public void onSuccess(BaseBeanContainer<BasicInfoResBean> beanContainer) {
            BasicInfoResBean data = beanContainer.getData();
            info.setType(data.getLoginType());
            info.setBasicUserInfo(data);
            info.setNickname(data.getNickname());
            info.setAvatar(data.getAvatar());

            IVerifyHolder.mLoginInfo.copy(info);

            //section---------------------------------
            //onDestroy发生在新Activity onCreate之后，可能出现这样的情况：页面跳转了，走未登录的逻辑，
            // 但onDestroy还未回调，没有取消网络请求的回调，而此时请求已经成功，执行了回调中的代码，
            // 更新了登录状态，这样侧边栏得不到立即刷新，需要重新发出事件以刷新侧边栏
            //否则表现为：侧边栏展示未登录、需求管理、消息页面已处于登录  v1.0.12 leobert
            AppEvent.LoginSuccessEvent event = new AppEvent.LoginSuccessEvent(info);
            event.setTrigger(LoginTrigger.BackgroundLogin);
            EventBus.getDefault().post(event);
            //----------------------------------------

            isLogined = true;
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iSplashActivity.cancelWaitView();
            isLogined = false;
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iSplashActivity.cancelWaitView();
            isLogined = false;
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
