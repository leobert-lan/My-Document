package com.lht.creationspace.module.proj.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.module.pub.ui.LocationPickerActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme7;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.module.proj.model.ProjectPublishModel;
import com.lht.creationspace.module.proj.model.pojo.ProjectStateResBean;
import com.lht.creationspace.module.proj.model.pojo.ProjectTypeResBean;
import com.lht.creationspace.module.proj.presenter.ProjectPublishActivityPresenter;
import com.lht.creationspace.util.string.StringUtil;
import com.lht.creationspace.util.toast.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ProjectPublishActivity extends AsyncProtectedActivity implements IProjectPublishActivity, View.OnClickListener {

    private static final String PAGENAME = "ProjectPublishActivity";
    private static final int PROJECT_NAME_MAX_LENGTH = 30;
    private ToolbarTheme7 titleBar;
    private ProgressBar progressBar;
    private EditText etProjectName;
    private TextView tvCurrentCount;
    private TextView tvProjectAddress;
    private EditText etQQ;
    private TextView tvProjectType;
    private TextView tvProjectState;
    private ProjectPublishActivityPresenter presenter;
    private EditText etPublisherPhone;
    private static final int TITLE_MAX_LENGTH = 30;
    private ProjectPublishModel.ProjectData projectData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_publish);
        EventBus.getDefault().register(this);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected String getPageName() {
        return ProjectPublishActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return ProjectPublishActivity.this;
    }

    @Override
    protected void initView() {
        titleBar = (ToolbarTheme7) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        etProjectName = (EditText) findViewById(R.id.et_project_name);
        tvCurrentCount = (TextView) findViewById(R.id.tv_current_count);
        tvProjectAddress = (TextView) findViewById(R.id.tv_project_address);
        etQQ = (EditText) findViewById(R.id.et_publisher_qq);
        tvProjectType = (TextView) findViewById(R.id.tv_project_type);
        tvProjectState = (TextView) findViewById(R.id.tv_project_state);
        etPublisherPhone = (EditText) findViewById(R.id.projectpublish_et_phone);

    }

    @Override
    protected void initVariable() {
        presenter = new ProjectPublishActivityPresenter(this);
        presenter.watchInputLength(etProjectName, PROJECT_NAME_MAX_LENGTH);
        projectData = new ProjectPublishModel.ProjectData();
    }

    @Override
    protected void initEvent() {
        titleBar.setTitle(R.string.v1000_title_activity_project_publish);
        titleBar.setOpText(R.string.v1000_default_projectpublish_text_publish);
        titleBar.setOpTextColor(R.color.main_green_dark);
        titleBar.setDefaultOnBackListener(this);
        if (null != IVerifyHolder.mLoginInfo.getLoginResBean()) {
            etPublisherPhone.setText(IVerifyHolder.mLoginInfo.getLoginResBean().getMobile());
        }
        titleBar.setOpOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wrapProjectData();
                presenter.callPublish(projectData);
            }
        });

        setSupportActionBar(titleBar);

        tvProjectType.setOnClickListener(this);
        tvProjectState.setOnClickListener(this);
        tvProjectAddress.setOnClickListener(this);
        presenter.watchInputLength(etProjectName, TITLE_MAX_LENGTH);
    }

    private void wrapProjectData() {
        projectData.setProjectName(etProjectName.getText().toString());
        projectData.setQq(etQQ.getText().toString());
        projectData.setMobile(etPublisherPhone.getText().toString());
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
    public void notifyProjectNameOverLength() {
        ToastUtils.show(getActivity(),
                R.string.v1000_default_projectpublish_toast_project_too_long,
                ToastUtils.Duration.s);
    }

    @Override
    public void updateCurrentLength(int edittextId, int currentCount, int remains) {
        tvCurrentCount.setText(String.valueOf(currentCount));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_project_type:
                ProjectTypeChooseActivity.getLauncher(this).launch();
                break;
            case R.id.tv_project_address:
                LocationPickerActivity.getLauncher(this).launch();
                break;
            case R.id.tv_project_state:
                ProjectStateChooseActivity.getLauncher(this).launch();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventMainThread(AppEvent.SetProjectTypeEvent event) {
        ProjectTypeResBean bean = event.getBean();
        ProjectTypeResBean.ProjectChildTypeResBean childTypeResBean = event.getChildTypeResBean();
        if (childTypeResBean != null) {
            tvProjectType.setText(bean.getName() + "--" + childTypeResBean.getName());
            projectData.setSecondaryProType(childTypeResBean.getId());
        } else {
            tvProjectType.setText(bean.getName());
            projectData.setSecondaryProType(-1);
        }
        projectData.setPrimaryProType(bean.getId());
    }

    @Subscribe
    public void onEventMainThread(AppEvent.SetProjectStateEvent event) {
        ProjectStateResBean bean = event.getBean();
        tvProjectState.setText(bean.getName());
        projectData.setProjectState(String.valueOf(bean.getId()));
    }

    /**
     * 选择省市区的回调
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(AppEvent.LocationPickedEvent event) {
        if (event.isEmpty()) {
            return;
        }
        if (StringUtil.isEmpty(event.getCity())) {
            tvProjectAddress.setText(event.getProvince());
        } else {
            String format = "%s-%s";
            String location = String.format(format, event.getProvince(), event.getCity());
            tvProjectAddress.setText(location);
        }

        projectData.setProvince(event.getProvince());
        projectData.setCity(event.getCity());
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
            return new LhtActivityLauncherIntent(context, ProjectPublishActivity.class);
        }

        @Override
        public AbsActivityLauncher<Void> injectData(Void data) {
            return Launcher.this;
        }
    }
}
