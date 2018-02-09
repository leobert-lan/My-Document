package com.lht.cloudjob.fragment;


import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.asyncprotected.DemandInfoActivity;
import com.lht.cloudjob.adapter.DMOListAdapter;
import com.lht.cloudjob.adapter.viewprovider.SimpleTaskItemViewProviderImpl;
import com.lht.cloudjob.customview.DemandItemView;
import com.lht.cloudjob.customview.FgDemandTitleBar;
import com.lht.cloudjob.customview.MaskView;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.interfaces.umeng.IUmengEventKey;
import com.lht.cloudjob.mvp.model.pojo.DemandInfoActivityData;
import com.lht.cloudjob.mvp.model.pojo.DemandItemData;
import com.lht.cloudjob.mvp.model.pojo.LoginInfo;
import com.lht.cloudjob.mvp.presenter.CollectTaskFragmentPresenter;
import com.lht.cloudjob.mvp.viewinterface.ICollectTaskFragment;
import com.lht.cloudjob.mvp.viewinterface.IHomeActivity;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.string.StringUtil;
import com.lht.ptrlib.library.PullToRefreshBase;
import com.lht.ptrlib.library.PullToRefreshListView;
import com.lht.ptrlib.library.PullToRefreshUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FgDemandCollected extends BaseFragment implements CompoundButton
        .OnCheckedChangeListener, IVerifyHolder,
        ICollectTaskFragment, SimpleTaskItemViewProviderImpl.ISetCallbackForDemandItem {

    private static final String PAGENAME = "FgDemandCollected";

    private PullToRefreshListView listView;

    private Button btnDelete;

    private IHomeActivity parent;

    private MaskView maskView;

    private CollectTaskFragmentPresenter presenter;

    private PullToRefreshListView listRecommend;

    private View emptyHeader;

    private ImageView imgEmpty;

    public FgDemandCollected() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fg_demand_collected, container, false);
        initView(contentView);
        initVariable();
        initEvent();
        return contentView;
    }

    @Override
    protected void initView(View view) {
        listView = (PullToRefreshListView) view.findViewById(R.id.fgdc_list_tasks);
        btnDelete = (Button) view.findViewById(R.id.fgdc_btn_delete);
        maskView = (MaskView) view.findViewById(R.id.fgdc_mask);
        listRecommend = (PullToRefreshListView) view.findViewById(R.id.emptyr_list_recommend);

        emptyHeader = getActivity().getLayoutInflater().inflate(R.layout.view_empty_listhead, null);
        imgEmpty = (ImageView) emptyHeader.findViewById(R.id.empty_icon);
        imgEmpty.setImageResource(R.drawable.v1011_drawable_mask_empty);

    }

    private DMOListAdapter adapter;

    private SimpleTaskItemViewProviderImpl itemViewProvider;

    private SimpleTaskItemViewProviderImpl itemViewProvider2;

    @Override
    protected void initVariable() {
        parent = (IHomeActivity) getActivity();
        presenter = new CollectTaskFragmentPresenter(this);
        itemViewProvider = new SimpleTaskItemViewProviderImpl(getActivity(), this);
        itemViewProvider2 = new SimpleTaskItemViewProviderImpl(getActivity(), this);

        adapter = new DMOListAdapter(new ArrayList<DemandItemData>(), itemViewProvider);
        recommendAdapter = new DMOListAdapter(new ArrayList<DemandItemData>(), itemViewProvider2);

        listView.getRefreshableView().setAdapter(adapter);
        listView.setOnRefreshListener(refreshListener);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listRecommend.setMode(PullToRefreshBase.Mode.DISABLED);
        listRecommend.setOnRefreshListener(refreshListener2);

        ListView listView2 = listRecommend.getRefreshableView();
        listView2.addHeaderView(emptyHeader);
        listRecommend.getRefreshableView().setAdapter(recommendAdapter);
    }

    @Override
    protected void initEvent() {
        presenter.callGetCollectedTaskList(mLoginInfo.getUsername());

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<DemandItemData> datas = adapter.getAll();
                ArrayList<String> tasks = new ArrayList<>();
                for (DemandItemData data : datas) {
                    if (data.isSelected()) {
                        tasks.add(data.getUniqueId());
                    }
                }

                if (tasks.isEmpty()) {

                    showErrorMsg(getString(R.string.v1010_default_text_select_task));
                    return;
                }

                presenter.callDiscollectTasks(mLoginInfo.getUsername(), tasks);
            }
        });
    }

    public void updateLoginInfo(LoginInfo loginInfo) {
        this.mLoginInfo.copy(loginInfo);
        if (presenter != null) {
            presenter.setLoginStatus(!StringUtil.isEmpty(mLoginInfo.getUsername()));
        }
        checkLoginStatus();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            showEditView();
            EventBus.getDefault().post(new AppEvent.HomeTabDisplayEvent(false));
        } else {
            showNormalView();
            EventBus.getDefault().post(new AppEvent.HomeTabDisplayEvent(true));
        }
    }

    private void showDelete() {
        btnDelete.setVisibility(View.VISIBLE);
        btnDelete.bringToFront();
    }

    private void hideDelete() {
        btnDelete.setVisibility(View.GONE);
    }

    private void showEditView() {
        showDelete();
        itemViewProvider.setSelectedModeEnabled(true);
        adapter.notifyDataSetChanged();
    }

    private void showNormalView() {
        hideDelete();
        itemViewProvider.setSelectedModeEnabled(false);
        ArrayList<DemandItemData> datas = adapter.getAll();
        for (DemandItemData data : datas) {
            data.setIsSelected(false);
        }
        adapter.setLiDatas(datas);
//        adapter.notifyDataSetChanged();
    }

    private PullToRefreshBase.OnRefreshListener2 refreshListener = new PullToRefreshBase
            .OnRefreshListener2() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            PullToRefreshUtil.updateLastFreshTime(getActivity(), refreshView);
            presenter.callGetCollectedTaskList(mLoginInfo.getUsername());
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            PullToRefreshUtil.updateLastFreshTime(getActivity(), refreshView);
            presenter.callAddCollectedTaskList(mLoginInfo.getUsername(), adapter.getCount());
        }
    };

    private PullToRefreshBase.OnRefreshListener2 refreshListener2 = new PullToRefreshBase
            .OnRefreshListener2() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            PullToRefreshUtil.updateLastFreshTime(getActivity(), refreshView);
            //先获取收藏，如不存在会再获取推荐
            presenter.callGetCollectedTaskList(mLoginInfo.getUsername());
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            PullToRefreshUtil.updateLastFreshTime(getActivity(), refreshView);
            presenter.callAddRecommendTaskList(mLoginInfo.getUsername(), recommendAdapter
                    .getDefaultPagedOffset());
        }
    };

    private boolean hasCollections;

    public boolean hasCollections() {
        return hasCollections;
    }

    public void setHasCollections(boolean hasCollections) {
        this.hasCollections = hasCollections;
    }

    @Override
    public void setCollectedListItems(ArrayList<DemandItemData> datas) {
        if (datas == null || datas.isEmpty()) {
            setHasCollections(false);
        } else {
            setHasCollections(true);
        }
        adapter.setLiDatas(datas);
        listView.onRefreshComplete();
    }

    @Override
    public void addCollectedListItems(ArrayList<DemandItemData> datas) {
        setHasCollections(true);
        adapter.addLiDatas(datas);
        listView.onRefreshComplete();
    }

    private DMOListAdapter recommendAdapter;

    @Override
    public void showEmptyView() {
        listRecommend.setVisibility(View.VISIBLE);
        listRecommend.bringToFront();
        listRecommend.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        listView.setVisibility(View.GONE);

        presenter.callGetRecommendTaskList(mLoginInfo.getUsername());
    }

    @Override
    public void hideEmptyView() {
        listRecommend.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        listView.bringToFront();
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listRecommend.setMode(PullToRefreshBase.Mode.DISABLED);
    }

    @Override
    public void finishRefresh() {
        PullToRefreshUtil.updateLastFreshTime(getActivity(), listView);
        listView.onRefreshComplete();
        listRecommend.onRefreshComplete();
        EventBus.getDefault().post(new FgDemandTitleBar.TryEnableRightOperateEvent());
    }

    /**
     * desc: 显示等待窗
     *
     * @param isProtectNeed 是否需要屏幕防击穿保护
     */
    @Override
    public void showWaitView(boolean isProtectNeed) {
        parent.showWaitView(isProtectNeed);
    }

    /**
     * desc: 取消等待窗
     */
    @Override
    public void cancelWaitView() {
        parent.cancelWaitView();
    }

    @Override
    public Resources getAppResource() {
        return getResources();
    }

    @Override
    public void showMsg(String msg) {
        parent.showMsg(msg);
    }

    @Override
    public void showErrorMsg(String msg) {
        this.showMsg(msg);
    }

    /**
     * desc: 为item设置各种回调，
     *
     * @param position
     * @param data
     * @param view
     */
    @Override
    public void setCallBack(final int position, final DemandItemData data, final DemandItemView
            view) {

        view.setOnSelectChangedListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                data.setIsSelected(isChecked);
                adapter.replace(position, data);
                adapter.notifyDataSetChanged();
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemViewProvider.isSelectedMode()) {
                    CheckBox checkBox = view.getViewHolder().cbSelected;
                    checkBox.setChecked(!checkBox.isChecked());
                } else {
                    //统计 通过收藏查看详情 -计数
                    parent.reportCountEvent(IUmengEventKey.KEY_CLICK_TASKFAVO_ITEM);

                    DemandInfoActivityData _d = new DemandInfoActivityData();
                    _d.setDemandId(data.getUniqueId());
                    _d.setLoginInfo(mLoginInfo);
                    Intent intent = new Intent(getActivity(), DemandInfoActivity.class);
                    intent.putExtra(DemandInfoActivity.KEY_DATA, JSON.toJSONString(_d));
                    getActivity().startActivity(intent);
                }
            }
        });
    }

    public void selectedAll() {
        ArrayList<DemandItemData> datas = adapter.getAll();
        for (DemandItemData data : datas) {
            data.setIsSelected(true);
        }
        adapter.setLiDatas(datas);
    }

    public void deSelectedAll() {
        ArrayList<DemandItemData> datas = adapter.getAll();
        for (DemandItemData data : datas) {
            data.setIsSelected(false);
        }
        adapter.setLiDatas(datas);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkLoginStatus();
        EventBus.getDefault().post(new FgDemandTitleBar.TryEnableRightOperateEvent());
    }

    private void checkLoginStatus() {
        String username = mLoginInfo.getUsername();
        if (StringUtil.isEmpty(username)) {
            showAsUnlogin();
        } else {
            showAsNormal();
        }
    }

    /**
     * 显示为登录视图，未登录、登出情况下
     */
    private void showAsUnlogin() {
        if (maskView == null) {
            //视图还未创建
            return;
        }
        maskView.showAsUnlogin(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.callLogin(CollectTaskFragmentPresenter.LoginTrigger.Mask);
            }
        });
    }

    /**
     * 显示正常视图，登录状态下
     */
    private void showAsNormal() {
        if (maskView == null) {
            //视图还未创建
            return;
        }
        maskView.hide();
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected String getPageName() {
        return PAGENAME;
    }

    /**
     * desc: 主线程回调登录成功
     * 需要处理：成员对象的更新、界面的更新
     *
     * @param event 登录成功事件，包含信息
     */
    @Override
    @Subscribe
    public void onEventMainThread(AppEvent.LoginSuccessEvent event) {
        presenter.setLoginStatus(true);
        mLoginInfo.copy(event.getLoginInfo());
        presenter.identifyTrigger(event.getTrigger());
    }

    /**
     * desc: 未进行登录的事件订阅
     *
     * @param event 手动关闭登录页事件
     */
    @Override
    @Subscribe
    public void onEventMainThread(AppEvent.LoginCancelEvent event) {
        //empty
    }

    @Override
    public LoginInfo getLoginInfo() {
        return mLoginInfo;
    }

    /**
     * 没有数据时接口总是这样返回，所以要判断是列表到底了，还是根本没数据
     */
    @Override
    public void checkListData() {
        if (adapter.getCount() == 0) {
            showEmptyView();
        }
    }

    @Override
    public void finishRefresh2() {
        listRecommend.onRefreshComplete();
        EventBus.getDefault().post(new FgDemandTitleBar.TryEnableRightOperateEvent());
    }

    @Override
    public void setRecommendListItems(ArrayList<DemandItemData> liData) {
        setHasCollections(false);
        recommendAdapter.setLiDatas(liData);
    }

    @Override
    public void addRecommentListItems(ArrayList<DemandItemData> liData) {
        setHasCollections(false);
        recommendAdapter.addLiDatas(liData);
    }
}
