package com.lht.creationspace.module.topic.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.CircularArray;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.R;
import com.lht.creationspace.adapter.BaseLoadingAdapter;
import com.lht.creationspace.adapter.RcvTopicAdapter;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.customview.MaskView;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme7;
import com.lht.creationspace.listener.ICallback;
import com.lht.creationspace.module.article.model.ArticlePublishModel;
import com.lht.creationspace.module.topic.ChooseTopicActivityPresenter;
import com.lht.creationspace.module.topic.model.pojo.TopicResBean;

/**
 * 发布文章--选择圈子
 */
public class ChooseTopicActivity extends AsyncProtectedActivity
        implements IChooseTopicActivity {
    private static final String PAGENAME = "ArticlePublishChooseTopicActivity";
    private ToolbarTheme7 titleBar;
    private ProgressBar progressBar;
    private RecyclerView rcvAllTopic;
    private RcvTopicAdapter adapter;
    private ChooseTopicActivityPresenter presenter;
    private MaskView maskView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_publish_choose_topic);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected void initView() {
        titleBar = (ToolbarTheme7) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        rcvAllTopic = (RecyclerView) findViewById(R.id.rcv_all_topic);
        maskView = (MaskView) findViewById(R.id.maskview);

        initRecycleView();
    }

    @Override
    protected void initVariable() {
        presenter = new ChooseTopicActivityPresenter(this);
        ArticlePublishModel.ArticleData articleData = AbsActivityLauncher.parseData(getIntent(), ArticlePublishModel.ArticleData.class);
        presenter.setArticleData(articleData);
    }

    @Override
    protected void initEvent() {
        titleBar.setTitle(R.string.v1000_title_activity_article_publish);
        titleBar.setDefaultOnBackListener(this);
        titleBar.setOpText(getString(R.string.v1000_default_articlepublish_text_publish));
        titleBar.setOpTextColor(R.color.main_green_dark);
        titleBar.setOpOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.callPublish();
            }
        });

        setSupportActionBar(titleBar);

        presenter.getTopicInfo();
    }


    private void initRecycleView() {

        adapter = new RcvTopicAdapter(this, listener, rcvAllTopic,
                new CircularArray<TopicResBean.TopicDetailInfoResBean>());
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        //设置布局管理器
        rcvAllTopic.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        rcvAllTopic.setAdapter(adapter);
        adapter.setOnLoadingListener(new BaseLoadingAdapter.OnLoadingListener() {
            @Override
            public void loading() {
                presenter.addTopicInfo(adapter.getItemCount());
            }
        });
    }

    @Override
    protected IApiRequestPresenter getApiRequestPresenter() {
        return presenter;
    }

    @Override
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    protected String getPageName() {
        return ChooseTopicActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return ChooseTopicActivity.this;
    }

    @Override
    public void addTopicData(TopicResBean data) {
        adapter.setLoadingComplete();
        adapter.addDatas(data.getCircles());
    }

    @Override
    public void setTopicData(TopicResBean bean) {
        adapter.setLoadingComplete();
        adapter.addDatas(bean.getCircles());
    }

    @Override
    public void showLoadingFailedView() {
        maskView.setVisibility(View.VISIBLE);
        maskView.showLoadFailMask(new ICallback() {
            @Override
            public void onCallback() {
                presenter.getTopicInfo();
            }
        });
    }

    @Override
    public void hideLoadingFailedView() {
        maskView.setVisibility(View.GONE);
    }

    RcvTopicAdapter.OnItemClickListener listener =
            new RcvTopicAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position,
                                        TopicResBean.TopicDetailInfoResBean bean) {
                    int oldIndex = adapter.getSelectedIndex();
                    adapter.setSelectedIndex(position);
                    if (oldIndex >= 0)
                        adapter.notifyItemChanged(oldIndex);
                    adapter.notifyItemChanged(position);
                    presenter.updateCircleId(bean.getId());
                }
            };


    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
    }

    public static class Launcher extends AbsActivityLauncher<ArticlePublishModel.ArticleData> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, ChooseTopicActivity.class);
        }

        @Override
        public AbsActivityLauncher<ArticlePublishModel.ArticleData> injectData(ArticlePublishModel.ArticleData data) {
            launchIntent.putExtra(data.getClass().getSimpleName(), JSON.toJSONString(data));
            return this;
        }
    }
}
