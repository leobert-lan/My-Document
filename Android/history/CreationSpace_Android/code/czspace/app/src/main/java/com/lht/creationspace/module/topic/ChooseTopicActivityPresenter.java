package com.lht.creationspace.module.topic;

import android.content.Context;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.module.topic.ui.HybridCircleDetailActivity;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.module.article.model.ArticlePublishModel;
import com.lht.creationspace.module.topic.model.JoinedTopicListModel;
import com.lht.creationspace.base.model.apimodel.RestfulApiModelCallback;
import com.lht.creationspace.module.article.model.pojo.NewArticleInfoResBean;
import com.lht.creationspace.module.topic.model.pojo.TopicResBean;
import com.lht.creationspace.module.topic.ui.IChooseTopicActivity;
import com.lht.creationspace.util.internet.HttpUtil;
import com.lht.creationspace.util.string.StringUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by chhyu on 2017/3/3.
 */

public class ChooseTopicActivityPresenter implements IApiRequestPresenter {
    private IChooseTopicActivity iChooseTopicActivity;

    public ChooseTopicActivityPresenter(IChooseTopicActivity iChooseTopicActivity) {
        this.iChooseTopicActivity = iChooseTopicActivity;
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    /**
     * 获取第一页圈子数据
     */
    public void getTopicInfo() {
        iChooseTopicActivity.showWaitView(true);
        iChooseTopicActivity.hideLoadingFailedView();
        JoinedTopicListModel.JoinedTopicListData data = new JoinedTopicListModel.JoinedTopicListData();
        data.setUsername(getUsername());
        data.setOffset(0);
        JoinedTopicListModel model = new JoinedTopicListModel(data, new ChooseTopicModelCallback(true));
        model.doRequest(iChooseTopicActivity.getActivity());
    }

    private String getUsername() {
        return IVerifyHolder.mLoginInfo.getUsername();
    }

    /**
     * 加载更多圈子数据
     *
     * @param offset 偏移量，已有的数据长度
     */
    public void addTopicInfo(int offset) {
        if (offset >= totalTopicCountOnServer)
            return;
        iChooseTopicActivity.showWaitView(true);
        JoinedTopicListModel.JoinedTopicListData data = new JoinedTopicListModel.JoinedTopicListData();
        data.setUsername(getUsername());
        data.setOffset(offset);
        JoinedTopicListModel model = new JoinedTopicListModel(data, new ChooseTopicModelCallback(false));
        model.doRequest(iChooseTopicActivity.getActivity());
    }

    private long totalTopicCountOnServer;

    private ArticlePublishModel.ArticleData articleData;

    public void setArticleData(ArticlePublishModel.ArticleData articleData) {
        this.articleData = articleData;
    }

    public void updateCircleId(String id) {
        articleData.setCircleId(id);
    }

    private class ChooseTopicModelCallback implements RestfulApiModelCallback<TopicResBean> {

        private final boolean isRefreshOperate;

        public ChooseTopicModelCallback(boolean isRefreshOperate) {
            this.isRefreshOperate = isRefreshOperate;
        }

        @Override
        public void onSuccess(TopicResBean bean) {
            iChooseTopicActivity.cancelWaitView();
            if (isRefreshOperate) {
                totalTopicCountOnServer = bean.getTotal();
                iChooseTopicActivity.setTopicData(bean);
            } else
                iChooseTopicActivity.addTopicData(bean);
        }

        @Override
        public void onFailure(int restCode, String msg) {
            iChooseTopicActivity.cancelWaitView();
            iChooseTopicActivity.showLoadingFailedView();
            iChooseTopicActivity.showMsg("获取圈子列表失败");
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iChooseTopicActivity.cancelWaitView();
            iChooseTopicActivity.showLoadingFailedView();
        }
    }

    /**
     * 新建文章
     */
    public void callPublish() {
        if (StringUtil.isEmpty(articleData.getCircleId())) {
            iChooseTopicActivity.showMsg(iChooseTopicActivity.getAppResource()
                    .getString(R.string.v1000_toast_error_nullcircle));
            return;
        }

        iChooseTopicActivity.showWaitView(true);
        ArticlePublishModel model = new ArticlePublishModel(articleData,
                new ArticlePublishModelCallback());
        model.doRequest(iChooseTopicActivity.getActivity());
    }

    private class ArticlePublishModelCallback
            implements RestfulApiModelCallback<NewArticleInfoResBean> {

        @Override
        public void onSuccess(NewArticleInfoResBean bean) {
            iChooseTopicActivity.cancelWaitView();
            iChooseTopicActivity.showMsg("文章发布成功");
            EventBus.getDefault().post(new AppEvent.ArticlePublishSuccessEvent());

//            Intent intent = new Intent(iChooseTopicActivity.getActivity(), HybridCircleDetailActivity.class);
//            intent.putExtra(HybridCircleDetailActivity.KEY_DATA, bean.getCircle_detail_url());
//            intent.putExtra(HybridCircleDetailActivity.KEY_OID, bean.getCircle_id());
//            iChooseTopicActivity.getActivity().startActivity(intent);
            HybridCircleDetailActivity.getLauncher(iChooseTopicActivity.getActivity())
                    .injectData(transData(bean))
                    .launch();

            iChooseTopicActivity.getActivity().finishWithoutOverrideAnim();
        }

        @Override
        public void onFailure(int restCode, String msg) {
            iChooseTopicActivity.cancelWaitView();
            iChooseTopicActivity.showMsg("文章发布失败");
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iChooseTopicActivity.cancelWaitView();
        }
    }

    private HybridCircleDetailActivity.CircleDetailActivityData transData(NewArticleInfoResBean bean) {
        HybridCircleDetailActivity.CircleDetailActivityData data = new HybridCircleDetailActivity.CircleDetailActivityData();
        data.setUrl(bean.getCircle_detail_url());
        data.setOid(bean.getCircle_id());
        return data;
    }

}
