package com.lht.creationspace.module.home;

import android.content.Context;
import android.content.Intent;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.module.user.info.ui.ac.AccountManageActivity;
import com.lht.creationspace.module.user.info.ui.ac.PersonalInfoActivity;
import com.lht.creationspace.module.user.social.HybridMyArticleActivity;
import com.lht.creationspace.module.user.social.HybridMyAttentionActivity;
import com.lht.creationspace.module.user.social.HybridMyCircleActivity;
import com.lht.creationspace.module.user.social.HybridMyFansActivity;
import com.lht.creationspace.module.user.social.HybridMyCollectionActivity;
import com.lht.creationspace.module.user.social.HybridMyProjectActivity;
import com.lht.creationspace.base.launcher.LoginIntentFactory;
import com.lht.creationspace.customview.share.ThirdPartySharePopWins;
import com.lht.creationspace.cfg.IPublicConst;
import com.lht.creationspace.base.launcher.ITriggerCompare;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.module.user.info.model.IntroduceQueryModel;
import com.lht.creationspace.module.home.model.QuerySocialInfoModel;
import com.lht.creationspace.base.model.apimodel.RestfulApiModelCallback;
import com.lht.creationspace.module.home.ui.IFgHomeMine;
import com.lht.creationspace.base.presenter.ABSVerifyNeedPresenter;
import com.lht.creationspace.service.notice.OnNoticeCheckedListener;
import com.lht.creationspace.service.notice.SocialInfoNotifyCheckManager;
import com.lht.creationspace.util.internet.HttpUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;


/**
 * Created by chhyu on 2017/2/23.
 */

public class FgHomeMinePresenter extends ABSVerifyNeedPresenter
        implements IApiRequestPresenter {
    private IFgHomeMine iFgHomeMine;
    private OnNoticeCheckedListener<QuerySocialInfoModel.ModelResBean>
            socialInfoCheckListener;

    public FgHomeMinePresenter(IFgHomeMine iFgHomeMine) {
        this.iFgHomeMine = iFgHomeMine;
        socialInfoCheckListener = new SocialInfoCheckedListener();
        SocialInfoNotifyCheckManager.getInstance()
                .addOnNoticeCheckedListener(socialInfoCheckListener);
    }

    public void onFinish() {
        SocialInfoNotifyCheckManager.getInstance()
                .removeNoticeCheckedListener(socialInfoCheckListener);
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    public void callShare() {
        ThirdPartySharePopWins.UrlShareData shareData =
                new ThirdPartySharePopWins.UrlShareData();
        shareData.setOpenUrl(IPublicConst.SHARE_APP_LINK);
        iFgHomeMine.showSharePopwins(shareData);
    }

    public void callGetBrief() {
        if (IVerifyHolder.mLoginInfo.isLogin()) {
            IntroduceQueryModel model = new IntroduceQueryModel(IVerifyHolder.mLoginInfo.getUsername(),
                    new IntroQueryModelCallback());
            model.doRequest(iFgHomeMine.getActivity());
        }
    }

    public void callLogin(LoginTrigger loginTrigger) {
        Intent intent = LoginIntentFactory.create(iFgHomeMine.getActivity(), loginTrigger);
        iFgHomeMine.getActivity().startActivity(intent);
    }

    public void identifyTrigger(ITriggerCompare trigger) {
        if (trigger.equals(LoginTrigger.Attention)) {
            HybridMyAttentionActivity.getLauncher(iFgHomeMine.getActivity()).launch();
        } else if (trigger.equals(LoginTrigger.Fans)) {
            HybridMyFansActivity.getLauncher(iFgHomeMine.getActivity()).launch();
        } else if (trigger.equals(LoginTrigger.Collection)) {
            HybridMyCollectionActivity.getLauncher(iFgHomeMine.getActivity()).launch();
        } else if (trigger.equals(LoginTrigger.MyProject)) {
            HybridMyProjectActivity.getLauncher(iFgHomeMine.getActivity()).launch();
        } else if (trigger.equals(LoginTrigger.MyArticle)) {
            HybridMyArticleActivity.getLauncher(iFgHomeMine.getActivity()).launch();
        } else if (trigger.equals(LoginTrigger.MyTopic)) {
            HybridMyCircleActivity.getLauncher(iFgHomeMine.getActivity()).launch();
        } else if (trigger.equals(LoginTrigger.AccountManage)) {
            AccountManageActivity.getLauncher(iFgHomeMine.getActivity()).launch();
        } else if (trigger.equals(LoginTrigger.PersonalInfo)) {
            PersonalInfoActivity.getLauncher(iFgHomeMine.getActivity()).launch();
        }
    }

    public void callGetSocialInfo() {
        //获取关注等信息
        SocialInfoNotifyCheckManager.getInstance().check();
    }

    public enum LoginTrigger implements ITriggerCompare {
        Attention(1), Fans(2), Collection(3), MyProject(4),
        MyArticle(5), MyTopic(6), AccountManage(7), PersonalInfo(8),
        /**
         * 头像处点击登录
         */
        Login(9);

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

    private final class IntroQueryModelCallback
            implements RestfulApiModelCallback<IntroduceQueryModel.ModelResBean> {

        @Override
        public void onSuccess(IntroduceQueryModel.ModelResBean bean) {
            EventBus.getDefault().post(new AppEvent.BriefSetEvent(bean.getBrief()));
        }

        @Override
        public void onFailure(int restCode, String msg) {
        }

        @Override
        public void onHttpFailure(int httpStatus) {
        }
    }

    private class SocialInfoCheckedListener
            extends OnNoticeCheckedListener<QuerySocialInfoModel.ModelResBean> {

        @Override
        public void onNoticeChecked(QuerySocialInfoModel.ModelResBean bean) {
            iFgHomeMine.updateSocialInfo(bean);
        }
    }
}
