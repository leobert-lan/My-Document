package com.lht.creationspace.module.proj.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.adapter.AbsListAdapter;
import com.lht.creationspace.adapter.viewprovider.ProjectChildTypeProviderImpl;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.UMengActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme1;
import com.lht.creationspace.module.proj.model.pojo.ProjectTypeResBean;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by chhyu on 2017/3/7.
 */

public class ProjectChildTypeChooseActivity extends UMengActivity {
    private static final String PAGENAME = "ProjectChildTypeChooseActivity";
    private ToolbarTheme1 titleBar;
    private ListView lvChildType;
    private ProjectTypeResBean bean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_type);
        bean = AbsActivityLauncher.parseData(getIntent(), ProjectTypeResBean.class);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected String getPageName() {
        return ProjectChildTypeChooseActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return ProjectChildTypeChooseActivity.this;
    }

    @Override
    protected void initView() {
        titleBar = (ToolbarTheme1) findViewById(R.id.titlebar);
        lvChildType = (ListView) findViewById(R.id.lv_child_type);
    }

    @Override
    protected void initVariable() {

    }

    @Override
    protected void initEvent() {
        titleBar.setTitle("选择子类型");
        titleBar.setDefaultOnBackListener(this);

        setSupportActionBar(titleBar);

        AbsListAdapter<ProjectTypeResBean.ProjectChildTypeResBean> adapter = new com.lht.creationspace.adapter.ListAdapter<>(bean.getChildren(),
                new ProjectChildTypeProviderImpl(getLayoutInflater(), null));
        lvChildType.setAdapter(adapter);
        lvChildType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ProjectTypeResBean.ProjectChildTypeResBean childTypeResBean = ProjectChildTypeChooseActivity.this.bean.getChildren().get(i);
                AppEvent.SetProjectTypeEvent event = new AppEvent.SetProjectTypeEvent();
                event.setBean(bean, childTypeResBean);
                EventBus.getDefault().post(event);
                finish();
            }
        });
    }

    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
    }

    public static class Launcher extends AbsActivityLauncher<ProjectTypeResBean> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, ProjectChildTypeChooseActivity.class);
        }

        @Override
        public AbsActivityLauncher<ProjectTypeResBean> injectData(ProjectTypeResBean data) {
            launchIntent.putExtra(data.getClass().getSimpleName(), JSON.toJSONString(data));
            return this;
        }
    }
}
