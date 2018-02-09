package com.lht.cloudjob.mvp.presenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.asyncprotected.PublishDemandActivity;
import com.lht.cloudjob.clazz.LoginIntentFactory;
import com.lht.cloudjob.interfaces.ITriggerCompare;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.interfaces.net.IPagedApiRequestModel;
import com.lht.cloudjob.mvp.BannerDataModel;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.GlobalNotifyModel;
import com.lht.cloudjob.mvp.model.RecommendTaskListModel;
import com.lht.cloudjob.mvp.model.bean.BannerResBean;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.BasicInfoResBean;
import com.lht.cloudjob.mvp.model.bean.GlobalNotifyResBean;
import com.lht.cloudjob.mvp.model.bean.RecommendTaskResBean;
import com.lht.cloudjob.mvp.model.pojo.DemandItemData;
import com.lht.cloudjob.mvp.viewinterface.IIndexFragment;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.lht.cloudjob.util.string.StringUtil;
import com.lht.customwidgetlib.banner.AutoLooperBanner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IndexFragmentPresenter
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/8/24
 */
public class IndexFragmentPresenter extends ABSVerifyNeedPresenter implements IApiRequestPresenter,
        AutoLooperBanner.OnBannerItemClickListener {

    private IIndexFragment iIndexFragment;

    private IPagedApiRequestModel recommendTaskListModel;

    public IndexFragmentPresenter(IIndexFragment iIndexFragment) {
        this.iIndexFragment = iIndexFragment;
        recommendTaskListModel = new RecommendTaskListModel(new RecommendTaskListModelCallback());
    }

    private boolean isRefreshOperate;

    public void callGetBannerData() {
        //不单独显示刷新等待
        IApiRequestModel model = new BannerDataModel(new BannerDataModelCallback());
        model.doRequest(iIndexFragment.getActivity());
    }

    public void callGetGlobalNotify() {
        IApiRequestModel model = new GlobalNotifyModel(new GlobalNotifyModelCallback());
        model.doRequest(iIndexFragment.getActivity());
    }

    public void callGetRecommendTaskList(String user) {
        iIndexFragment.showWaitView(true);
        isRefreshOperate = true;
        recommendTaskListModel.setParams(user, 0);
        recommendTaskListModel.doRequest(iIndexFragment.getActivity());
    }

    public void callAddRecommendTaskList(String user, int offset) {
        iIndexFragment.showWaitView(true);
        isRefreshOperate = false;
        recommendTaskListModel.setParams(user, offset);
        recommendTaskListModel.doRequest(iIndexFragment.getActivity());
    }

    /**
     * desc: 页面接收到订阅事件后，调用presenter#identifyTrigger，执行逻辑，需要区分触发事件是不是登录事件
     *
     * @param trigger an  interface to identify trigger,use equal(ITriggerCompare compare)
     */
    @Override
    public void identifyTrigger(ITriggerCompare trigger) {
        //refresh recommend task list always
        callGetRecommendTaskList(iIndexFragment.getLoginInfo().getUsername());
        if (LoginTrigger.Subscribe.equals(trigger)) {
            //订阅触发
            callSubscribe();
        } else if (LoginTrigger.PublishDemand.equals(trigger)) {
            //发布需求触发
            iIndexFragment.jump2PublishDemand();
        }
    }

    private boolean isLogin;

    /**
     * desc: check if login
     *
     * @return true while login,false otherwise
     */
    @Override
    protected boolean isLogin() {
        return isLogin;
    }

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
     * 页面结束时取消所有相关的回调
     *
     * @param context
     */
    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    /**
     * 轮播图item点击回调
     *
     * @param position 点击位置
     */
    @Override
    public void onItemClick(int position) {
        iIndexFragment.jump2RecomInfoActivity(bannerResBeans.get(position));
    }

    public void callSubscribe() {
        if (!isLogin()) {
            Intent intent = LoginIntentFactory.create(iIndexFragment.getActivity(), LoginTrigger
                    .Subscribe);
            iIndexFragment.getActivity().startActivity(intent);
        } else {
            iIndexFragment.jump2Subscribe();
        }
    }

    private class RecommendTaskListModelCallback implements
            ApiModelCallback<ArrayList<RecommendTaskResBean>> {

        @Override
        public void onSuccess(BaseBeanContainer<ArrayList<RecommendTaskResBean>> beanContainer) {
            ArrayList<DemandItemData> liData = new ArrayList<>();
            ArrayList<RecommendTaskResBean> data = beanContainer.getData();
            if (data == null) {
                data = new ArrayList<>();
            }
            for (RecommendTaskResBean bean : data) {
                DemandItemData itemData = new DemandItemData();
                bean.copyTo(itemData);
                liData.add(itemData);
            }
            if (isRefreshOperate) {
                iIndexFragment.setRecommendListItems(liData);
            } else {
                iIndexFragment.addRecommendListItems(liData);
            }
            iIndexFragment.finishRefresh();
            iIndexFragment.cancelWaitView();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iIndexFragment.cancelWaitView();
            if (isRefreshOperate) {
                //ignore
            } else {
                iIndexFragment.showErrorMsg(iIndexFragment.getActivity().getString(R.string.v1010_toast_list_all_data_added));
            }

            iIndexFragment.finishRefresh();
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iIndexFragment.cancelWaitView();
            iIndexFragment.finishRefresh();
        }
    }

    private class GlobalNotifyModelCallback implements
            ApiModelCallback<ArrayList<GlobalNotifyResBean>> {


        @Override
        public void onSuccess(BaseBeanContainer<ArrayList<GlobalNotifyResBean>> beanContainer) {
            ArrayList<GlobalNotifyResBean> data = beanContainer.getData();
            if (data == null) {
                return;
            }
            SpannableStringBuilder builder = new SpannableStringBuilder();
            for (GlobalNotifyResBean bean : data) {
                SpannableStringBuilder sBuilder = new SpannableStringBuilder();
                sBuilder.append("·");
                sBuilder.append(StringUtil.nullStrToEmpty(bean.getBidder_username()));
                sBuilder.append(" ");
                final int s = sBuilder.length();
                sBuilder.append("中标了");
                sBuilder.setSpan(new ForegroundColorSpan(Color.RED), s, sBuilder.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


                sBuilder.append(bean.getTitle());
                sBuilder.append(" ");

                final int s2 = sBuilder.length();
                sBuilder.append("￥" + bean.getReal_cash());
                sBuilder.setSpan(new ForegroundColorSpan(Color.RED), s2, sBuilder.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                sBuilder.append("    ");

                builder.append(sBuilder);
            }

            iIndexFragment.updateGlobalNotify(builder);
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            //empty
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            //empty
        }
    }

    private ArrayList<BannerResBean> bannerResBeans;

    private class BannerDataModelCallback implements ApiModelCallback<ArrayList<BannerResBean>> {

        @Override
        public void onSuccess(BaseBeanContainer<ArrayList<BannerResBean>> beanContainer) {
            excute(beanContainer.getData());
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            excute(null);
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            excute(null);
        }

        private void excute(ArrayList<BannerResBean> beans) {
            bannerResBeans = beans;
            if (bannerResBeans == null) {
                bannerResBeans = new ArrayList<>();
            }
            List<String> images = new ArrayList<>();
            for (BannerResBean bean : bannerResBeans) {
                images.add(bean.getImage());
            }
            iIndexFragment.showBanner(images, IndexFragmentPresenter.this);
        }
    }

    public enum LoginTrigger implements ITriggerCompare {
        Subscribe(1),PublishDemand(2);

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
