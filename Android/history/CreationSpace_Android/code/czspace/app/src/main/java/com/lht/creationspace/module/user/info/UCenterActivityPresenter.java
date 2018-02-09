package com.lht.creationspace.module.user.info;

import android.content.Context;
import android.content.Intent;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.launcher.LoginIntentFactory;
import com.lht.creationspace.customview.toast.HeadUpToast;
import com.lht.creationspace.base.launcher.ITriggerCompare;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.module.user.social.model.QueryUserSubscribeStateModel;
import com.lht.creationspace.base.model.apimodel.RestfulApiModelCallback;
import com.lht.creationspace.module.user.social.model.UserSubscribeModel;
import com.lht.creationspace.module.user.social.model.UserUnSubscribeModel;
import com.lht.creationspace.module.user.info.ui.IUcenterActivity;
import com.lht.creationspace.util.internet.HttpUtil;
import com.lht.creationspace.util.string.StringUtil;

import java.io.Serializable;

/**
 * <p><b>Package:</b> com.lht.creationspace.mvp.presenter </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> UCenterActivityPresenter </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/3/14.
 */

public class UCenterActivityPresenter implements IApiRequestPresenter {
    private IUcenterActivity iUcenterActivity;

    public UCenterActivityPresenter(IUcenterActivity iUcenterActivity) {
        this.iUcenterActivity = iUcenterActivity;
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    private String operateId;

    public void querySubscribeState(String targetUser, boolean needDescribe) {
        if (!IVerifyHolder.mLoginInfo.isLogin())
            return;
        if (targetUser.equals(IVerifyHolder.mLoginInfo.getUsername())) {
            iUcenterActivity.setSubscribeEnable(false);
            return;
        }

        if (needDescribe) {
            iUcenterActivity.showWaitView(true);
        }
        QueryUserSubscribeStateModel.QueryUserSubscribeStateData data = new QueryUserSubscribeStateModel.QueryUserSubscribeStateData();
        data.setUser(IVerifyHolder.mLoginInfo.getUsername());
        data.setTarget(targetUser);
        QueryUserSubscribeStateModel model =
                new QueryUserSubscribeStateModel(data, new QueryUserSubscribeStateCallback(needDescribe));
        model.doRequest(iUcenterActivity.getActivity());
    }

    private String targetUser;

    public void callSubscribe(String targetUser) {
        this.targetUser = targetUser;
        if (!IVerifyHolder.mLoginInfo.isLogin()) {
            Intent intent = LoginIntentFactory.create(iUcenterActivity.getActivity(),
                    LoginTrigger.SubscribeUser);
            iUcenterActivity.getActivity().startActivity(intent);
            return;
        }
        if (targetUser.equals(IVerifyHolder.mLoginInfo.getUsername())) {
            iUcenterActivity.showMsg(iUcenterActivity.getAppResource()
                    .getString(R.string.v1000_toast_error_subscribemyself));
            iUcenterActivity.setSubscribeEnable(false);
            return;
        }
        UserSubscribeModel.UserSubscribeData data = new UserSubscribeModel.UserSubscribeData();
        data.setUser(IVerifyHolder.mLoginInfo.getUsername());
        data.setTarget(targetUser);
        iUcenterActivity.showWaitView(true);
        UserSubscribeModel model = new UserSubscribeModel(data, new SubscribeUserCallback());
        model.doRequest(iUcenterActivity.getActivity());
    }

    public void deSubscribe(String targetUser) {
        this.targetUser = targetUser;
        if (StringUtil.isEmpty(operateId)) {
            querySubscribeState(targetUser, true);
            return;
        }
        if (targetUser.equals(IVerifyHolder.mLoginInfo.getUsername())) {
            iUcenterActivity.setSubscribeEnable(false);
            return;
        }
        iUcenterActivity.showWaitView(true);
        UserUnSubscribeModel model =
                new UserUnSubscribeModel(operateId, new DeSubscribeUserCallback());
        model.doRequest(iUcenterActivity.getActivity());
    }

    public void handleLoginSuccessEvent(AppEvent.LoginSuccessEvent event) {
        if (LoginTrigger.SubscribeUser.equals(event.getTrigger())) {
            callSubscribe(targetUser);
        }
    }

    private class QueryUserSubscribeStateCallback
            implements RestfulApiModelCallback<QueryUserSubscribeStateModel.ModelResBean> {

        private final boolean needUnsubscribe;

        public QueryUserSubscribeStateCallback(boolean needUnsubscribe) {
            this.needUnsubscribe = needUnsubscribe;
        }

        @Override
        public void onSuccess(QueryUserSubscribeStateModel.ModelResBean bean) {
            iUcenterActivity.cancelWaitView();
            operateId = bean.getData().getId();
            if (needUnsubscribe) {
                deSubscribe(targetUser);
            } else {
                iUcenterActivity.manualSetSubscribeState(true);
            }
        }

        @Override
        public void onFailure(int restCode, String msg) {
            iUcenterActivity.cancelWaitView();
            if (!needUnsubscribe) //已登录情况下的初始化查询
                iUcenterActivity.manualSetSubscribeState(false);
            else
                iUcenterActivity.manualSetSubscribeState(true);

            //else 刚登录并收藏，进行取消收藏，查询失败，重置为已收藏
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iUcenterActivity.cancelWaitView();
            if (!needUnsubscribe)
                iUcenterActivity.manualSetSubscribeState(false);
            else
                iUcenterActivity.manualSetSubscribeState(true);

        }
    }


    private class SubscribeUserCallback
            implements RestfulApiModelCallback<String> {

        @Override
        public void onSuccess(String string) {
            iUcenterActivity.cancelWaitView();
            iUcenterActivity.manualSetSubscribeState(true);
            iUcenterActivity.showHeadUpMsg(HeadUpToast.TYPE_SUCCESS, "成功关注");
        }

        @Override
        public void onFailure(int restCode, String msg) {
            iUcenterActivity.cancelWaitView();
            iUcenterActivity.manualSetSubscribeState(false);
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iUcenterActivity.cancelWaitView();
            iUcenterActivity.manualSetSubscribeState(false);

        }
    }


    private class DeSubscribeUserCallback
            implements RestfulApiModelCallback<String> {

        @Override
        public void onSuccess(String string) {
            iUcenterActivity.cancelWaitView();
            iUcenterActivity.manualSetSubscribeState(false);
            operateId = null;
            iUcenterActivity.showHeadUpMsg(HeadUpToast.TYPE_SUCCESS, "取消关注");
        }

        @Override
        public void onFailure(int restCode, String msg) {
            iUcenterActivity.cancelWaitView();
            iUcenterActivity.manualSetSubscribeState(true);
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iUcenterActivity.cancelWaitView();
            iUcenterActivity.manualSetSubscribeState(true);

        }
    }

    public enum LoginTrigger implements ITriggerCompare {
        SubscribeUser(1);

        private final int tag;

        LoginTrigger(int i) {
            tag = i;
        }

        @Override
        public boolean equals(ITriggerCompare compare) {
            if (compare == null) {
                return false;
            }
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
