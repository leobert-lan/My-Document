package com.lht.creationspace.module.proj.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.CircularArray;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.adapter.RecyclerViewAdapter;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme1;
import com.lht.creationspace.module.proj.model.pojo.ProjectTypeResBean;
import com.lht.creationspace.module.proj.presenter.ProjectTypeChooseActivityPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class ProjectTypeChooseActivity extends AsyncProtectedActivity implements IProjectTypeChooseActivity {

    private static final String PAGENAME = "ProjectTypeChooseActivity";
    private ToolbarTheme1 titleBar;
    private ProgressBar progressBar;
    private RecyclerView rcvProjectType;
    private RecyclerViewAdapter adapter;
    private ProjectTypeChooseActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_type_choose);
        EventBus.getDefault().register(this);
        initView();
        initVariable();
        initEvent();

    }

    @Override
    protected String getPageName() {
        return ProjectTypeChooseActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return ProjectTypeChooseActivity.this;
    }

    @Override
    protected void initView() {
        titleBar = (ToolbarTheme1) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        rcvProjectType = (RecyclerView) findViewById(R.id.rcv_project_type);
    }

    @Override
    protected void initVariable() {
        presenter = new ProjectTypeChooseActivityPresenter(this);

    }

    @Override
    protected void initEvent() {
        titleBar.setTitle(getString(R.string.v1000_title_activity_choose_project_type));
        titleBar.setDefaultOnBackListener(this);

        setSupportActionBar(titleBar);

        initRcv();
        presenter.getParentType();
    }

    private void initRcv() {
        adapter = new RecyclerViewAdapter(this, listener, rcvProjectType, new CircularArray<ProjectTypeResBean>());
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        //设置布局管理器
        rcvProjectType.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        rcvProjectType.setAdapter(adapter);
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
    public void setParentData(ArrayList<ProjectTypeResBean> datas) {
        adapter.addDatas(datas);
    }

    RecyclerViewAdapter.OnItemClickListener listener = new RecyclerViewAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int postion, ProjectTypeResBean bean) {
            if (bean.getChildren().isEmpty() || bean.getChildren() == null) {
                //携带数据返回上一级
                AppEvent.SetProjectTypeEvent event = new AppEvent.SetProjectTypeEvent();
                event.setBean(bean, null);
                EventBus.getDefault().post(event);
                finish();
            } else {
                ProjectChildTypeChooseActivity.getLauncher(ProjectTypeChooseActivity.this)
                        .injectData(bean)
                        .launch();
            }
        }
    };


    @Subscribe
    public void onEventMainThread(AppEvent.SetProjectTypeEvent event) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
    }

    public static final class Launcher extends AbsActivityLauncher<Void> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, ProjectTypeChooseActivity.class);
        }

        @Override
        public AbsActivityLauncher<Void> injectData(Void data) {
            return Launcher.this;
        }
    }
}
