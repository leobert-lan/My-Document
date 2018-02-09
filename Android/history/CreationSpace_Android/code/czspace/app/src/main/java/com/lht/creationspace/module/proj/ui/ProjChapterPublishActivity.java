package com.lht.creationspace.module.proj.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.lht.creationspace.R;
import com.lht.creationspace.adapter.GridImageAdapter;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.customview.popup.CustomPopupWindow;
import com.lht.creationspace.customview.popup.dialog.CustomDialog;
import com.lht.creationspace.customview.toolBar.AbsNavigationToolBar;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme7;
import com.lht.creationspace.layoutmanager.FullyGridLayoutManager;
import com.lht.creationspace.module.projchapter.ui.presenter.ProjChapterPublishActivityPresenter;
import com.lht.creationspace.module.projchapter.ui.IProjChapterPublishActivity;
import com.yalantis.ucrop.entity.LocalMedia;

import java.util.List;


/**
 * 项目更新编辑页面
 */
public class ProjChapterPublishActivity extends AsyncProtectedActivity
        implements IProjChapterPublishActivity {

    private static final String PAGENEMA = "ProjChapterPublishActivity";
    private ToolbarTheme7 titlebar;
    private ProgressBar progressBar;
    private EditText etTitle;
    private TextView tvCurrentCount;
    private EditText etUpdateContent;
    private RecyclerView rvImages;
    private ProjChapterPublishActivityPresenter presenter;
    private GridImageAdapter rvAdapter;
    private static final int TITLE_MAX_LENGTH = 30;
    private LaunchData launchData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proj_chapter_publish);

        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected void initView() {
        titlebar = (ToolbarTheme7) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        etTitle = (EditText) findViewById(R.id.et_title);
        tvCurrentCount = (TextView) findViewById(R.id.tv_current_count);
        etUpdateContent = (EditText) findViewById(R.id.et_update_content);
        rvImages = (RecyclerView) findViewById(R.id.projContent_publish_images);

        FullyGridLayoutManager manager = new FullyGridLayoutManager(getActivity(),
                4, GridLayoutManager.VERTICAL, false);
        rvImages.setLayoutManager(manager);
    }

    @Override
    protected void initVariable() {
        presenter = new ProjChapterPublishActivityPresenter(this);
        launchData = AbsActivityLauncher.parseData(getIntent(), LaunchData.class);

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

    }

    @Override
    protected void initEvent() {
        titlebar.setTitle(getString(R.string.v1013_title_project_publish_update));
        titlebar.setTitleTextColor(R.color.main_green_dark);
        titlebar.setOpTextColor(R.color.main_green_dark);
        titlebar.setOpText(R.string.v1013_default_project_update_publish);

        titlebar.setOnBackListener(new AbsNavigationToolBar.ITitleBackListener() {
            @Override
            public void onTitleBackClick() {
                onBackPressed();
            }
        });

        setSupportActionBar(titlebar);

        presenter.watchText(etTitle, TITLE_MAX_LENGTH);
        titlebar.setOpOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.Pulse).duration(300).playOn(titlebar.getOpTextView());

                String title = etTitle.getText().toString();
                String content = etUpdateContent.getText().toString();

                presenter.callPublish(title, content, launchData.getProjectId() /*,pushEnabled*/);
            }
        });

        rvImages.setAdapter(rvAdapter);
    }

    @Override
    protected IApiRequestPresenter getApiRequestPresenter() {
        return presenter;
    }

    @Override
    protected String getPageName() {
        return ProjChapterPublishActivity.PAGENEMA;
    }

    @Override
    public BaseActivity getActivity() {
        return ProjChapterPublishActivity.this;
    }

    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
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
    public void updateSelectedImages(List<LocalMedia> selectedMedias) {
        rvAdapter.setList(selectedMedias);
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

    public static class Launcher extends AbsActivityLauncher<LaunchData> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, ProjChapterPublishActivity.class);
        }

        @Override
        public AbsActivityLauncher<LaunchData> injectData(LaunchData data) {
            launchIntent.putExtra(data.getClass().getSimpleName(), JSON.toJSONString(data));
            return this;
        }
    }


    public static class LaunchData {

        private String projectId;

        public String getProjectId() {
            return projectId;
        }

        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }
    }
}
