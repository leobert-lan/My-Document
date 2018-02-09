package com.lht.creationspace.module.topic;

import android.content.Context;
import android.content.Intent;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.base.launcher.LoginIntentFactory;
import com.lht.creationspace.customview.popup.CustomPopupWindow;
import com.lht.creationspace.base.launcher.ITriggerCompare;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.module.topic.model.CircleJoinStateModel;
import com.lht.creationspace.module.topic.model.JoinCircleModel;
import com.lht.creationspace.base.model.apimodel.RestfulApiModelCallback;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.module.topic.model.pojo.CircleJoinStateResBean;
import com.lht.creationspace.base.model.pojo.LoginInfo;
import com.lht.creationspace.module.topic.ui.IHybridCircleDetailActivity;
import com.lht.creationspace.util.internet.HttpUtil;
import com.lht.creationspace.hybrid.webclient.LhtWebviewClient;

import java.io.Serializable;

import static com.lht.creationspace.module.topic.HybridCircleDetailActivityPresenter.LoginTrigger.articlePublish;


/**
 * Created by chhyu on 2017/3/31.
 */

public class HybridCircleDetailActivityPresenter implements IApiRequestPresenter {

    private IHybridCircleDetailActivity iHybridCircleDetailActivity;

    public HybridCircleDetailActivityPresenter(IHybridCircleDetailActivity iHybridCircleDetailActivity) {
        this.iHybridCircleDetailActivity = iHybridCircleDetailActivity;
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    public void callLogin() {
        Intent intent = LoginIntentFactory.create(iHybridCircleDetailActivity.getActivity(), articlePublish);
        iHybridCircleDetailActivity.getActivity().startActivity(intent);
    }

    public void identifyTrigger(AppEvent.LoginSuccessEvent event) {
        if (event.getTrigger().equals(articlePublish)) {
            LoginInfo info = event.getLoginInfo();
            IVerifyHolder.mLoginInfo.copy(info);
            //登录成功之后判断是否已经加入这个圈子
            iHybridCircleDetailActivity.checkUserJoinState();
        }
    }

    /**
     * 检查用户是否已经加入这个圈子
     */
    public void checkUserIsJoined(CircleJoinStateModel.CheckUserIsJoinedCircleData data) {
        iHybridCircleDetailActivity.showWaitView(true);
        CircleJoinStateModel model = new CircleJoinStateModel(data, new ModelCallback(data.getId()));
        model.doRequest(iHybridCircleDetailActivity.getActivity());
    }

    class ModelCallback implements RestfulApiModelCallback<CircleJoinStateResBean> {
        private String circleId;

        public ModelCallback(String circleId) {
            this.circleId = circleId;
        }

        @Override
        public void onSuccess(CircleJoinStateResBean bean) {
            iHybridCircleDetailActivity.cancelWaitView();
            if (bean.is_joined()) {
                //已经加入，直接跳到发布文章
                iHybridCircleDetailActivity.jump2ArticlePublishActivity();
            } else {
                //未加入，弹出对话框提醒
                iHybridCircleDetailActivity.showJoinCircleAlert(new CustomPopupWindow.OnPositiveClickListener() {
                    @Override
                    public void onPositiveClick() {
                        doJoinCircle(circleId);
                    }
                });
            }
        }

        @Override
        public void onFailure(int restCode, String msg) {
            iHybridCircleDetailActivity.cancelWaitView();
            iHybridCircleDetailActivity.showMsg(msg);
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iHybridCircleDetailActivity.cancelWaitView();
        }
    }

    public void doJoinCircle(String circleId) {
        iHybridCircleDetailActivity.showWaitView(true);
        //加入圈子
        JoinCircleModel.JoinCircleData data = new JoinCircleModel.JoinCircleData();
        data.setUsername(IVerifyHolder.mLoginInfo.getUsername());
        data.setCircle_id(circleId);
        JoinCircleModel model = new JoinCircleModel(data, new JoinCircleModelCallback());
        model.doRequest(iHybridCircleDetailActivity.getActivity());
    }

    private class JoinCircleModelCallback
            implements RestfulApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseVsoApiResBean bean) {
            iHybridCircleDetailActivity.cancelWaitView();
            LhtWebviewClient.OneTimeOnLoadFinishListener listener =
                    new LhtWebviewClient.OneTimeOnLoadFinishListener() {
                        @Override
                        public void onLoadFinish() {
                            iHybridCircleDetailActivity.jump2ArticlePublishActivity();
                        }
                    };
            iHybridCircleDetailActivity.notifyCircleJoined(listener);
        }

        @Override
        public void onFailure(int restCode, String msg) {
            iHybridCircleDetailActivity.cancelWaitView();
            iHybridCircleDetailActivity.showMsg(msg);
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iHybridCircleDetailActivity.cancelWaitView();
        }
    }

    public enum LoginTrigger implements ITriggerCompare {
        articlePublish(1);

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
