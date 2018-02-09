package com.lht.cloudjob.mvp.presenter;

import android.content.Context;
import android.content.Intent;

import com.lht.cloudjob.R;
import com.lht.cloudjob.clazz.LoginIntentFactory;
import com.lht.cloudjob.customview.ThirdPartyShareItemClickListenerImpl;
import com.lht.cloudjob.interfaces.ITriggerCompare;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.interfaces.umeng.IUmengEventKey;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.CollectTaskModel;
import com.lht.cloudjob.mvp.model.DemandInfoModel;
import com.lht.cloudjob.mvp.model.DiscollectTaskModel;
import com.lht.cloudjob.mvp.model.UndertakeCheckPermissionModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.CheckUndertakePermissionResBean;
import com.lht.cloudjob.mvp.model.bean.DemandInfoResBean;
import com.lht.cloudjob.mvp.viewinterface.IDemandInfoActivity;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.lht.cloudjob.util.phonebasic.PhoneCallUtil;
import com.lht.cloudjob.util.string.StringUtil;

import java.io.Serializable;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> DemandInfoActivityPresenter
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/8/22
 */
public class DemandInfoActivityPresenter extends ABSVerifyNeedPresenter implements
        IApiRequestPresenter {

    private IDemandInfoActivity iDemandInfoActivity;

    private IApiRequestModel collectTaskModel;

    public DemandInfoActivityPresenter(IDemandInfoActivity iDemandInfoActivity) {
        this.iDemandInfoActivity = iDemandInfoActivity;
        setLoginStatus(!StringUtil.isEmpty(IVerifyHolder.mLoginInfo.getUsername()));
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

    public void callGetDemandInfo(String demandId, String username) {
        iDemandInfoActivity.showWaitView(true);
        IApiRequestModel model = new DemandInfoModel(username, demandId, new
                DemandInfoModelCallback());
        model.doRequest(iDemandInfoActivity.getActivity());
    }

    private String demandId;

    /**
     * collect the task
     *
     * @param username username of the user
     * @param demandId unique id of the task to collect
     */
    public void callCollectTask(String username, String demandId) {
        this.demandId = demandId;
        if (isLogin()) {
            iDemandInfoActivity.showWaitView(true);
            collectTaskModel = new CollectTaskModel(username, demandId, new
                    CollectTaskModelCallback());

            collectTaskModel.doRequest(iDemandInfoActivity.getActivity());
        } else {
            Intent intent = LoginIntentFactory.create(iDemandInfoActivity.getActivity(),
                    LoginTrigger.Collect);
            iDemandInfoActivity.getActivity().startActivity(intent);
        }
    }

    /**
     * discollect the task
     *
     * @param username username of the user
     * @param demandId unique id of the task to discollect
     */
    public void callDiscollectTask(String username, String demandId) {
        iDemandInfoActivity.showWaitView(true);
        IApiRequestModel discollectTaskModel = new DiscollectTaskModel(username, demandId, new
                DiscollectTaskModelCallback());
        discollectTaskModel.doRequest(iDemandInfoActivity.getActivity());
    }

    public void callPublisher() {
        PhoneCallUtil.makePhoneCall(iDemandInfoActivity.getActivity(),
                getDemandInfoResBean().getMobile());
    }

    private DemandInfoResBean demandInfoResBean;

    private DemandInfoResBean getDemandInfoResBean() {
        if (demandInfoResBean == null) {
            demandInfoResBean = new DemandInfoResBean();
        }
        return demandInfoResBean;
    }

    /**
     * 投稿-jump
     */
    public void callContribute() {
        DemandInfoResBean bean = getDemandInfoResBean();
        final int MODEL_REWARD = 1; // 悬赏类型 招标为2
        final int MARK_HIDE = 2;//暗标，明标为1
        if (bean.getModel() == MODEL_REWARD) { //悬赏类
            iDemandInfoActivity.jump2UndertakeReward(bean.getTask_bn());
        } else { //招标类
            if (bean.getIs_mark() == MARK_HIDE) { //暗标类
                iDemandInfoActivity.jump2UndertakeHideBid(bean.getTask_bn());
            } else { //明标类
                final long desiredPrice = (long) bean.getTask_cash();//甲方报价
                iDemandInfoActivity.jump2UndertakeOpenBid(bean.getTask_bn(), desiredPrice);
            }
        }
    }

    /**
     * 签署协议-jump
     */
    public void callSignAgreement() {
        iDemandInfoActivity.jump2SignAgreement(getDemandInfoResBean());
    }

    /**
     * 评价-jump
     */
    public void callEvaluate() {
        //置信服务端检验了username 并返回了正确的operate
        // 未登录的不可能进行评价
        // 2016-9-21 11:36:16
        iDemandInfoActivity.jump2Evaluate(getDemandInfoResBean());
    }


    @Override
    public void identifyTrigger(ITriggerCompare trigger) {
        if (LoginTrigger.Collect.equals(trigger)) {
            iDemandInfoActivity.showWaitView(true);
            collectTaskModel = new CollectTaskModel(IVerifyHolder.mLoginInfo.getUsername(),
                    demandId, new CollectTaskModelCallback());
            collectTaskModel.doRequest(iDemandInfoActivity.getActivity());
        } else if (LoginTrigger.Undertake.equals(trigger)) {
            callCheckUndertakePermission(IVerifyHolder.mLoginInfo.getUsername());
        }
    }

    @Override
    public void identifyCanceledTrigger(ITriggerCompare trigger) {
        super.identifyCanceledTrigger(trigger);
        if (LoginTrigger.Collect.equals(trigger)) {
            iDemandInfoActivity.setTaskCollected(false);
        }
    }

    /**
     * desc: check if login
     *
     * @return true while login,false otherwise
     */
    @Override
    protected boolean isLogin() {
        return isLogin;
    }

    private boolean isLogin;

    /**
     * desc: update status,implement the method with an appropriate design
     *
     * @param isLogin
     */
    @Override
    public void setLoginStatus(boolean isLogin) {
        this.isLogin = isLogin;
    }

    /**
     * 投稿的权限校验
     *
     * @param username
     */
    public void callCheckUndertakePermission(String username) {
        if (!isLogin()) {
            Intent intent = LoginIntentFactory.create(iDemandInfoActivity.getActivity(),
                    LoginTrigger.Undertake);
            iDemandInfoActivity.getActivity().startActivity(intent);
            return;
        }

        if (username.equals(getPublisherUser())) {
            iDemandInfoActivity.showErrorMsg("无法承接自己发布的需求");
            return;
        }
        UndertakeCheckPermissionModel model = new UndertakeCheckPermissionModel(username, new
                UndertakePermissionCallback());
        model.doRequest(iDemandInfoActivity.getActivity());
    }

    private String getPublisherUser() {
        DemandInfoResBean.Publisher publisher = getDemandInfoResBean().getPublisher();
        if (publisher != null)
            return publisher.getUsername();
        return "";
    }

    /**
     * 分享
     */
    public void call2Share() {
        //统计 点击需求分享功能 - 计数 忽略取消的
        iDemandInfoActivity.reportCountEvent(IUmengEventKey.KEY_TASK_SHARE);
        ThirdPartyShareItemClickListenerImpl l = new ThirdPartyShareItemClickListenerImpl
                (iDemandInfoActivity.getActivity());
        l.setTitle(iDemandInfoActivity.getAppResource().getString(R.string.task_share_title));
        l.setSummary(getDemandInfoResBean().getTitle());
        iDemandInfoActivity.showSharePopwins(getDemandInfoResBean().getLink(), l);
    }

    private class UndertakePermissionCallback implements ApiModelCallback<BaseVsoApiResBean> {
        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iDemandInfoActivity.cancelWaitView();
            //只有权限校验成功才跳转到投稿
            callContribute();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iDemandInfoActivity.cancelWaitView();

            int ret = beanContainer.getData().getRet();

            if (CheckUndertakePermissionResBean.RETS_NEEDAUTH_P.contains(ret)) { //需要个人认证
//                v1.0.50提示错误
                iDemandInfoActivity.showMsg(iDemandInfoActivity.getAppResource().getString(R
                        .string.v1050_default_demandinfo_text_nopermission));
            } else if (CheckUndertakePermissionResBean.RETS_NEEDAUTH_E.contains(ret)) { //需要企业认证
                // v1.0.50 提示错误
                iDemandInfoActivity.showMsg(iDemandInfoActivity.getAppResource().getString(R
                        .string.v1050_default_demandinfo_text_nopermission));
            } else if (CheckUndertakePermissionResBean.RETS_NEEDAUTH_PORE.contains(ret)) { //需要个人或者企业认证
                // v1.0.50 提示错误
                iDemandInfoActivity.showMsg(iDemandInfoActivity.getAppResource().getString(R
                        .string.v1050_default_demandinfo_text_nopermission));
            } else if (CheckUndertakePermissionResBean.RETS_NEEDAUTH_MOBILE.contains(ret)) {  //需要绑定手机号
                iDemandInfoActivity.showBindPhoneDialog();
            } else if (ret == CheckUndertakePermissionResBean.UNDERTAKE_COUNT_FULLED) { //投稿达到上限
                iDemandInfoActivity.showMsg(iDemandInfoActivity.getAppResource().getString(R
                        .string.v1010_default_demandinfo_text_countout));
            } else { //全部当做 RETS_NEEDBIND_MAILORICCARD 内容处理
                iDemandInfoActivity.showMsg(iDemandInfoActivity.getAppResource().getString(R
                        .string.v1010_default_demandinfo_text_bindpemail));
            }
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iDemandInfoActivity.cancelWaitView();
            iDemandInfoActivity.showMsg(iDemandInfoActivity.getActivity().getString(R.string
                    .v1010_default_error_text_netnr2));
        }
    }

    public void callSafePageRefresh(String demandId) {
        iDemandInfoActivity.showWaitView(true);
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if(iDemandInfoActivity!= null) {
//                    iDemandInfoActivity.cancelWaitView();
//                    iDemandInfoActivity.updateView(demandInfoResBean);
//                }
//            }
//        },500);
        callGetDemandInfo(demandId, IVerifyHolder.mLoginInfo.getUsername());
    }


    private class DemandInfoModelCallback implements ApiModelCallback<DemandInfoResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<DemandInfoResBean> beanContainer) {
            iDemandInfoActivity.cancelWaitView();
            demandInfoResBean = beanContainer.getData();
            iDemandInfoActivity.updateView(demandInfoResBean);
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iDemandInfoActivity.cancelWaitView();
            iDemandInfoActivity.showErrorMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iDemandInfoActivity.cancelWaitView();

            // TODO: 2016/8/23
        }
    }

    private class DiscollectTaskModelCallback implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iDemandInfoActivity.cancelWaitView();
            iDemandInfoActivity.setTaskCollected(false);
            iDemandInfoActivity.showMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iDemandInfoActivity.cancelWaitView();
            iDemandInfoActivity.showErrorMsg(beanContainer.getData().getMessage());
            iDemandInfoActivity.setTaskCollected(true);
            iDemandInfoActivity.showMsg(beanContainer.getData().getMessage());
            // TODO: 2016/8/25 toast?
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iDemandInfoActivity.cancelWaitView();
            iDemandInfoActivity.setTaskCollected(true);
            // TODO: 2016/8/25
        }
    }

    private class CollectTaskModelCallback implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iDemandInfoActivity.cancelWaitView();
            iDemandInfoActivity.setTaskCollected(true);
            iDemandInfoActivity.showMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            if (beanContainer.getData().getRet() != CollectTaskModel.ERROR_RECOLLECT) {
                iDemandInfoActivity.showErrorMsg(beanContainer.getData().getMessage());
                iDemandInfoActivity.setTaskCollected(false);
            } else {
                iDemandInfoActivity.setTaskCollected(true);
            }
            iDemandInfoActivity.cancelWaitView();
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iDemandInfoActivity.cancelWaitView();
            iDemandInfoActivity.setTaskCollected(false);
            // TODO: 2016/8/25
        }
    }

    public enum LoginTrigger implements ITriggerCompare {
        Collect(1), Undertake(2);

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
