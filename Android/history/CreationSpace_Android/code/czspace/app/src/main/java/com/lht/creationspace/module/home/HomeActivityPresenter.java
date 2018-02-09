package com.lht.creationspace.module.home;

import android.content.Context;
import android.content.Intent;

import com.lht.creationspace.base.MainApplication;
import com.lht.creationspace.base.launcher.ITriggerCompare;
import com.lht.creationspace.base.launcher.LoginIntentFactory;
import com.lht.creationspace.base.presenter.ABSVerifyNeedPresenter;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.customview.popup.PopupPublishTypeChooseWin;
import com.lht.creationspace.module.article.ui.ArticlePublishActivity;
import com.lht.creationspace.module.home.model.QuerySocialInfoModel;
import com.lht.creationspace.module.home.ui.IHomeActivity;
import com.lht.creationspace.module.proj.ui.ProjectPublishActivity;
import com.lht.creationspace.module.setting.model.CheckVersionUpdateModel;
import com.lht.creationspace.service.notice.OnNoticeCheckedListener;
import com.lht.creationspace.service.notice.SocialInfoNotifyCheckManager;
import com.lht.creationspace.util.VersionUtil;
import com.lht.creationspace.util.internet.HttpUtil;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.presenter
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> HomeActivityPresenter
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/11.
 */
public class HomeActivityPresenter extends ABSVerifyNeedPresenter
        implements IApiRequestPresenter {

    private IHomeActivity iHomeActivity;

    private OnNoticeCheckedListener<QuerySocialInfoModel.ModelResBean>
            tabMineBadgeCheckedListener;

    public HomeActivityPresenter(IHomeActivity iHomeActivity) {
        this.iHomeActivity = iHomeActivity;
        tabMineBadgeCheckedListener = new TabMineBadgeCheckedListener();
        SocialInfoNotifyCheckManager.getInstance()
                .addOnNoticeCheckedListener(tabMineBadgeCheckedListener);
    }

    public void onFinish() {
        SocialInfoNotifyCheckManager.getInstance()
                .removeNoticeCheckedListener(tabMineBadgeCheckedListener);
    }

    @Override
    public void identifyTrigger(ITriggerCompare trigger) {
        if (trigger.equals(PopupPublishTypeChooseWin.LoginTrigger.ProjectPublish)) {
            iHomeActivity.jump2ProjectPublishActivity();
        } else if (trigger.equals(PopupPublishTypeChooseWin.LoginTrigger.ArticlePublish)) {
            iHomeActivity.jump2ArticlePublishActivity();
        }
    }


    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    /**
     * 检查版本更新
     */
    public void checkVersionUpdate() {
        Context context = MainApplication.getOurInstance();
        CheckVersionUpdateModel model = new CheckVersionUpdateModel(
                VersionUtil.getVersion(context), new VersionUtil.VersionCheckCallback(context));
        model.doRequest(iHomeActivity.getActivity());
    }


    /**
     * 发布项目
     */
    public void callPublishProject() {
        if (isLogin()) {
            ProjectPublishActivity.getLauncher(iHomeActivity.getActivity()).launch();
        } else {
            Intent intent = LoginIntentFactory.create(iHomeActivity.getActivity(),
                    PopupPublishTypeChooseWin.LoginTrigger.ProjectPublish);
            iHomeActivity.getActivity().startActivity(intent);
        }
    }


    /**
     * 发布文章
     */
    public void callPublishArticle() {
        if (isLogin()) {
            ArticlePublishActivity.getLauncher(iHomeActivity.getActivity()).launch();
        } else {
            Intent intent = LoginIntentFactory.create(iHomeActivity.getActivity(),
                    PopupPublishTypeChooseWin.LoginTrigger.ArticlePublish);
            iHomeActivity.getActivity().startActivity(intent);
        }
    }

    public void jump2ArticlePublishActivity() {
        ArticlePublishActivity.getLauncher(iHomeActivity.getActivity()).launch();
    }

    public void jump2ProjectPublishActivity() {
        ProjectPublishActivity.getLauncher(iHomeActivity.getActivity()).launch();
    }

    public void queryUnreadMsg() {
        SocialInfoNotifyCheckManager.getInstance().check();
    }

    private class TabMineBadgeCheckedListener
            extends OnNoticeCheckedListener<QuerySocialInfoModel.ModelResBean> {

        @Override
        public void onNoticeChecked(QuerySocialInfoModel.ModelResBean bean) {
            iHomeActivity.updateTabMineNotifyBadge(bean.isHas_unread_favorite());
        }
    }

}