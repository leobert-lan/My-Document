package com.lht.creationspace.module.article.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.adapter.GridImageAdapter;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.customview.popup.CustomPopupWindow;
import com.lht.creationspace.customview.popup.dialog.CustomDialog;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme7;
import com.lht.creationspace.layoutmanager.FullyGridLayoutManager;
import com.lht.creationspace.module.article.ArticlePublishActivityPresenter;
import com.yalantis.ucrop.entity.LocalMedia;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class ArticlePublishActivity extends AsyncProtectedActivity
        implements IArticlePublishActivity {

    private static final String PAGENAME = "ArticlePublishActivity";
    private ToolbarTheme7 mTitleBar;
    private ProgressBar progressBar;
    private ArticlePublishActivityPresenter presenter;
    private EditText etTitle;
    private TextView tvCurrentCount;
    private EditText etContent;
    private static final int TITLE_MAX_LENGTH = 30;
    private RecyclerView rvImages;
    private GridImageAdapter rvAdapter;
    private StartConfig startConfig;

//    public static final String KEY_DATA = "_key_data";

    public static class StartConfig {
        private boolean hasSetCircle;

        private String CircleId;

        public boolean isHasSetCircle() {
            return hasSetCircle;
        }

        public void setHasSetCircle(boolean hasSetCircle) {
            this.hasSetCircle = hasSetCircle;
        }

        public String getCircleId() {
            return CircleId;
        }

        public void setCircleId(String circleId) {
            CircleId = circleId;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_publish);
        EventBus.getDefault().register(this);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected String getPageName() {
        return ArticlePublishActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return ArticlePublishActivity.this;
    }

    @Override
    protected void initView() {
        mTitleBar = (ToolbarTheme7) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        etTitle = (EditText) findViewById(R.id.et_title);
        tvCurrentCount = (TextView) findViewById(R.id.tv_current_count);
        etContent = (EditText) findViewById(R.id.et_articlr_content);
        rvImages = (RecyclerView) findViewById(R.id.publish_article_images);


        FullyGridLayoutManager manager = new FullyGridLayoutManager(getActivity(),
                4, GridLayoutManager.VERTICAL, false);
        rvImages.setLayoutManager(manager);

    }

    @Override
    protected void initVariable() {
        presenter = new ArticlePublishActivityPresenter(this);

        rvAdapter = new GridImageAdapter(getActivity(), new GridImageAdapter.onPicClickListener() {
            @Override
            public void onAddPicClick(int type, int position) {
                if (type == GridImageAdapter.TYPE_ADD)
                    presenter.callAddPhoto();
            }

            @Override
            public void onDelete(int position) {
                presenter.removeSelected(position);
                rvAdapter.notifyDataSetChanged();
            }
        });

        try {
            startConfig = AbsActivityLauncher.parseData(getIntent(),StartConfig.class);
        } catch (Exception e) {
            startConfig = new StartConfig();
        } finally {
            if (startConfig == null)
                startConfig = new StartConfig();
        }


    }


    @Override
    protected void initEvent() {
        mTitleBar.setTitle(getString(R.string.v1000_title_activity_article_publish));
        mTitleBar.setOpTextColor(R.color.main_green_dark);
        if (hasCircleSet()) {
            mTitleBar.setOpText(R.string.v1000_default_articlepublish_text_publish);
        } else {
            mTitleBar.setOpText(R.string.v1000_default_article_publish_text_next);
        }
        mTitleBar.setDefaultOnBackListener(getActivity());

        setSupportActionBar(mTitleBar);
        presenter.watchText(etTitle, TITLE_MAX_LENGTH);
        mTitleBar.setOpOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.callPrepareArticlePublish(etTitle.getText().toString(),
                        etContent.getText().toString());
            }
        });
        rvImages.setAdapter(rvAdapter);
    }

    @Subscribe
    public void onEventMainThread(AppEvent.ArticlePublishSuccessEvent event) {
        presenter.callGC();
        finishWithoutOverrideAnim();
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
    public void updateCurrentTitleLength(int currentCount) {
        tvCurrentCount.setText(String.valueOf(currentCount));
    }

    @Override
    public void updateSelectedImages(List<LocalMedia> selectedImages) {
        rvAdapter.setList(selectedImages);
        rvAdapter.notifyDataSetChanged();
    }

    @Override
    public void showDialog(int contentResId, int positiveBtnResId,
                           CustomPopupWindow.OnPositiveClickListener onPositiveClickListener) {
        CustomDialog dialog = new CustomDialog(this);
        dialog.changeView2Single();
        dialog.setContent(contentResId);
        dialog.setPositiveButton(positiveBtnResId);
        dialog.setPositiveClickListener(onPositiveClickListener);
        dialog.show();
    }

    @Override
    public boolean hasCircleSet() {
        return startConfig.isHasSetCircle();
    }

    @Override
    public String getCircleId() {
        return startConfig.getCircleId();
    }

    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
    }

    public static final class Launcher extends AbsActivityLauncher<StartConfig> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, ArticlePublishActivity.class);
        }

        @Override
        public AbsActivityLauncher<StartConfig> injectData(StartConfig data) {
            doInject(data);
            return Launcher.this;
        }

    }

}
