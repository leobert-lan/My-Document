package com.lht.creationspace.module.proj.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.adapter.AbsListAdapter;
import com.lht.creationspace.adapter.ListAdapter;
import com.lht.creationspace.adapter.viewprovider.ProjectStateProviderImpl;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme1;
import com.lht.creationspace.module.proj.model.pojo.ProjectStateResBean;
import com.lht.creationspace.module.proj.presenter.ProjectStateChooseActivityPresenter;
import com.lht.ptrlib.library.PullToRefreshBase;
import com.lht.ptrlib.library.PullToRefreshListView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class ProjectStateChooseActivity extends AsyncProtectedActivity
        implements IProjectStateChooseActivity {
    private static final String PAGENAME = "ProjectStateChooseActivity";
    private ToolbarTheme1 titleBar;
    private ProgressBar progressBar;
    private ProjectStateChooseActivityPresenter presenter;
    private PullToRefreshListView pullToRefreshListView;
    private ListAdapter<ProjectStateResBean> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_state_choose);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected void initView() {
        titleBar = (ToolbarTheme1) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_project_state);
        pullToRefreshListView.getRefreshableView().setFooterDividersEnabled(true);
        pullToRefreshListView.getRefreshableView().addFooterView(new View(getActivity()));

    }

    @Override
    protected void initVariable() {
        presenter = new ProjectStateChooseActivityPresenter(this);
    }

    @Override
    protected void initEvent() {
        titleBar.setTitle(getString(R.string.v1000_title_activity_choose_project_state));
        titleBar.setDefaultOnBackListener(this);

        setSupportActionBar(titleBar);

        //获取项目状态
        presenter.callGetProjectState();
        initAdapter();
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                presenter.callGetProjectState();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {

            }
        });
    }

    private void initAdapter() {
        adapter = new ListAdapter<>(new ArrayList<ProjectStateResBean>(),
                new ProjectStateProviderImpl(getLayoutInflater(), new ProjectStateProviderImplCallback()));
        pullToRefreshListView.getRefreshableView().setAdapter(adapter);
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
        return ProjectStateChooseActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return ProjectStateChooseActivity.this;
    }

    @Override
    public void updateProjectStateDate(ArrayList<ProjectStateResBean> bean) {
        adapter.setLiData(bean);
        pullToRefreshListView.onRefreshComplete();
    }

    class ProjectStateProviderImplCallback implements AbsListAdapter.ICustomizeListItem<ProjectStateResBean, ProjectStateProviderImpl.ViewHolder> {

        @Override
        public void customize(int position, final ProjectStateResBean data,
                              View convertView,
                              ProjectStateProviderImpl.ViewHolder viewHolder) {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppEvent.SetProjectStateEvent event = new AppEvent.SetProjectStateEvent();
                    event.setBean(data);
                    EventBus.getDefault().post(event);
                    finish();
                }
            });
        }
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
            return new LhtActivityLauncherIntent(context, ProjectStateChooseActivity.class);
        }

        @Override
        public AbsActivityLauncher<Void> injectData(Void data) {
            return Launcher.this;
        }
    }
}
