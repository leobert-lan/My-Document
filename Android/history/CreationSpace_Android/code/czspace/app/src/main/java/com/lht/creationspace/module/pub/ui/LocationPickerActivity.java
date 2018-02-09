package com.lht.creationspace.module.pub.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.adapter.locationpicker.ParentAdapter;
import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme1;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.base.model.pojo.ParentEntity;
import com.lht.creationspace.module.pub.LocationPickerActivityPresenter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * 地区选择界面，
 */
public class LocationPickerActivity extends AsyncProtectedActivity implements
        ILocationPickerActivity, ParentAdapter.OnChildTreeViewClickListener {

    private static final String PAGENAME = "LocationPickerActivity";
    private ToolbarTheme1 titleBar;

    private ProgressBar progressBar;

    private LocationPickerActivityPresenter presenter;

    private ExpandableListView expandableListView;

    private ArrayList<ParentEntity> parents;

    private ParentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);
        initView();
        initVariable();
        initEvent();
    }

    /**
     * desc: 获取页面名称
     *
     * @return String
     */
    @Override
    protected String getPageName() {
        return LocationPickerActivity.PAGENAME;
    }

    /**
     * desc: 获取activity
     */
    @Override
    public BaseActivity getActivity() {
        return LocationPickerActivity.this;
    }

    /**
     * desc: 实例化View
     */
    @Override
    protected void initView() {
        titleBar = (ToolbarTheme1) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        expandableListView = (ExpandableListView) findViewById(R.id.location_picker_list);

        expandableListView.setChildDivider(null);
        expandableListView.setDividerHeight(0);
        expandableListView.setGroupIndicator(null);
    }

    /**
     * desc: 实例化必要的参数，以防止initEvent需要的参数空指针
     */
    @Override
    protected void initVariable() {
        presenter = new LocationPickerActivityPresenter(this);
    }

    /**
     * desc: 监听器设置、adapter设置等
     */
    @Override
    protected void initEvent() {
        titleBar.setDefaultOnBackListener(getActivity());
        titleBar.setTitle(R.string.title_activity_loaction_picker);

        setSupportActionBar(titleBar);

        presenter.callGetLocationData();

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition,
                                        long id) {
                if (parents.get(groupPosition).getChilds() == null ||
                        parents.get(groupPosition).getChilds().isEmpty()) {
                    LocationPickerActivity.this.onClickPosition(groupPosition, -1, -1);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected IApiRequestPresenter getApiRequestPresenter() {
        return presenter;
    }

    /**
     * desc: 提供等待窗
     *
     * @return 一个progressBar实例
     */
    @Override
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    public void updateList(ArrayList<ParentEntity> parents) {
        this.parents = parents;
        adapter = new ParentAdapter(this, parents);
        expandableListView.setAdapter(adapter);
        adapter.setOnChildTreeViewClickListener(this);
    }

    @Override
    public void onClickPosition(int provinceIndex, int cityIndex,
                                int areaIndex) {
        AppEvent.LocationPickedEvent event = new AppEvent.LocationPickedEvent();
        if (provinceIndex >= 0) {
            ParentEntity province = parents.get(provinceIndex);
            event.setProvince(province.getGroupName());

            //check city
            if (cityIndex >= 0) {
                ParentEntity.ChildEntity city = province.getChilds().get(cityIndex);
                event.setCity(city.getGroupName());

//                //check area
//                if (areaIndex >= 0) {
//                    event.setArea(city.getChildNames().get(areaIndex));
//                }
            }
        }

        EventBus.getDefault().post(event);
        finish();
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
            return new LhtActivityLauncherIntent(context, LocationPickerActivity.class);
        }

        @Override
        public AbsActivityLauncher<Void> injectData(Void data) {
            return Launcher.this;
        }
    }

}
