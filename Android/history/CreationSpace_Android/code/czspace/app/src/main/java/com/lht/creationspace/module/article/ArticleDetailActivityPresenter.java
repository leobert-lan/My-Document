package com.lht.creationspace.module.article;

import android.content.Context;
import android.content.Intent;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.base.launcher.LoginIntentFactory;
import com.lht.creationspace.customview.toast.HeadUpToast;
import com.lht.creationspace.base.launcher.ITriggerCompare;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.module.article.model.ArticleCollectModel;
import com.lht.creationspace.module.article.model.ArticleDisCollectModel;
import com.lht.creationspace.module.article.model.QueryArticleCollectStateModel;
import com.lht.creationspace.base.model.apimodel.RestfulApiModelCallback;
import com.lht.creationspace.module.article.ui.IArticleDetailActivity;
import com.lht.creationspace.util.internet.HttpUtil;
import com.lht.creationspace.util.string.StringUtil;

import java.io.Serializable;

/**
 * <p><b>Package:</b> com.lht.creationspace.mvp.presenter </p>
 * <p><b>Article:</b> czspace </p>
 * <p><b>Classname:</b> ArticleDetailActivityPresenter </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/3/13.
 */

public class ArticleDetailActivityPresenter implements IApiRequestPresenter {
    private IArticleDetailActivity iArticleDetailActivity;

    public ArticleDetailActivityPresenter(IArticleDetailActivity iArticleDetailActivity) {
        this.iArticleDetailActivity = iArticleDetailActivity;
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }


    public void queryCollectState(String targetArticle, boolean needDiscollect) {
        if (!IVerifyHolder.mLoginInfo.isLogin())
            return;
        if (needDiscollect)
            iArticleDetailActivity.showWaitView(true);
        QueryArticleCollectStateModel.QueryArticleCollectStateData data =
                new QueryArticleCollectStateModel.QueryArticleCollectStateData();
        data.setUser(IVerifyHolder.mLoginInfo.getUsername());
        data.setTarget(targetArticle);
        QueryArticleCollectStateModel model =
                new QueryArticleCollectStateModel(data,
                        new QueryArticleCollectStateCallback(needDiscollect));
        model.doRequest(iArticleDetailActivity.getActivity());
    }


    private String operateId;


    private String targetArticle;

    public void callCollect(String targetArticle) {
        this.targetArticle = targetArticle;
        ArticleCollectModel.ArticleCollectData data = new ArticleCollectModel.ArticleCollectData();
        if (!IVerifyHolder.mLoginInfo.isLogin()) {
            Intent intent = LoginIntentFactory.create(iArticleDetailActivity.getActivity(),
                    LoginTrigger.CollectArticle);
            iArticleDetailActivity.getActivity().startActivity(intent);
            return;
        }
        iArticleDetailActivity.showWaitView(true);
        data.setUser(IVerifyHolder.mLoginInfo.getUsername());
        data.setTarget(targetArticle);
        ArticleCollectModel model = new ArticleCollectModel(data, new CollectArticleCallback());
        model.doRequest(iArticleDetailActivity.getActivity());
    }

    public void disCollect(String targetArticle) {
        this.targetArticle = targetArticle;
        if (StringUtil.isEmpty(operateId)) {
            queryCollectState(targetArticle, true);
            return;
        }
        iArticleDetailActivity.showWaitView(true);
        ArticleDisCollectModel model =
                new ArticleDisCollectModel(operateId, new DisCollectArticleCallback());
        model.doRequest(iArticleDetailActivity.getActivity());
    }

    public void handleLoginSuccessEvent(AppEvent.LoginSuccessEvent event) {
        if (LoginTrigger.CollectArticle.equals(event.getTrigger())) {
            callCollect(targetArticle);
        }
    }

    private class QueryArticleCollectStateCallback
            implements RestfulApiModelCallback<QueryArticleCollectStateModel.ModelResBean> {

        private final boolean needDiscollect;

        public QueryArticleCollectStateCallback(boolean needDiscollect) {
            this.needDiscollect = needDiscollect;
        }

        @Override
        public void onSuccess(QueryArticleCollectStateModel.ModelResBean bean) {
            iArticleDetailActivity.cancelWaitView();
            operateId = bean.getData().getId();
            if (needDiscollect) {
                disCollect(targetArticle);
            } else {
                iArticleDetailActivity.manualSetCollectState(true);
            }
        }

        @Override
        public void onFailure(int restCode, String msg) {
            iArticleDetailActivity.cancelWaitView();
            if (!needDiscollect)
                iArticleDetailActivity.manualSetCollectState(false);
            else
                iArticleDetailActivity.manualSetCollectState(true);
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iArticleDetailActivity.cancelWaitView();
            if (!needDiscollect)
                iArticleDetailActivity.manualSetCollectState(false);
            else
                iArticleDetailActivity.manualSetCollectState(true);
        }
    }


    private class CollectArticleCallback
            implements RestfulApiModelCallback<String> {

        @Override
        public void onSuccess(String string) {
            iArticleDetailActivity.cancelWaitView();
            iArticleDetailActivity.manualSetCollectState(true);
            iArticleDetailActivity.showHeadUpMsg(HeadUpToast.TYPE_SUCCESS, "成功收藏");
        }

        @Override
        public void onFailure(int restCode, String msg) {
            iArticleDetailActivity.cancelWaitView();
            iArticleDetailActivity.manualSetCollectState(false);
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iArticleDetailActivity.cancelWaitView();
            iArticleDetailActivity.manualSetCollectState(false);

        }
    }


    private class DisCollectArticleCallback
            implements RestfulApiModelCallback<String> {

        @Override
        public void onSuccess(String string) {
            iArticleDetailActivity.cancelWaitView();
            iArticleDetailActivity.manualSetCollectState(false);
            operateId = null;
            iArticleDetailActivity.showHeadUpMsg(HeadUpToast.TYPE_FAILURE, "取消收藏");
        }

        @Override
        public void onFailure(int restCode, String msg) {
            iArticleDetailActivity.cancelWaitView();
            iArticleDetailActivity.manualSetCollectState(true);
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iArticleDetailActivity.cancelWaitView();
            iArticleDetailActivity.manualSetCollectState(true);

        }
    }

    public enum LoginTrigger implements ITriggerCompare {
        CollectArticle(1);

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
