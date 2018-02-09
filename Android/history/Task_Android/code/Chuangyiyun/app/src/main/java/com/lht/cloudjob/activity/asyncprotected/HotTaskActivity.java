package com.lht.cloudjob.activity.asyncprotected;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.adapter.DMOListAdapter;
import com.lht.cloudjob.adapter.viewprovider.SimpleTaskItemViewProviderImpl;
import com.lht.cloudjob.customview.DemandItemView;
import com.lht.cloudjob.customview.MaskView;
import com.lht.cloudjob.customview.TitleBar;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.pojo.DemandInfoActivityData;
import com.lht.cloudjob.mvp.model.pojo.DemandItemData;
import com.lht.cloudjob.mvp.presenter.HotTaskPresenter;
import com.lht.cloudjob.mvp.viewinterface.IHotTaskActivity;
import com.lht.ptrlib.library.PullToRefreshBase;
import com.lht.ptrlib.library.PullToRefreshListView;
import com.lht.ptrlib.library.PullToRefreshUtil;

import java.util.ArrayList;

public class HotTaskActivity extends AsyncProtectedActivity implements IHotTaskActivity,
        SimpleTaskItemViewProviderImpl.ISetCallbackForDemandItem {

    private static final String PAGENAME = "HotTaskActivity";

    private PullToRefreshListView pullToRefreshListView;

    private TitleBar titleBar;

    private ProgressBar progressBar;

    private HotTaskPresenter presenter;

    private DMOListAdapter mAdapter;

    private MaskView maskView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_task);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected String getPageName() {
        return HotTaskActivity.PAGENAME;
    }

    @Override
    public UMengActivity getActivity() {
        return HotTaskActivity.this;
    }

    @Override
    protected void initView() {
        titleBar = (TitleBar) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.hottask_list_tasks);

        maskView = (MaskView) findViewById(R.id.mask);
    }

    @Override
    protected void initVariable() {
        presenter = new HotTaskPresenter(this);

        mAdapter = new DMOListAdapter(new ArrayList<DemandItemData>(),
                new SimpleTaskItemViewProviderImpl(getActivity(), this));
        pullToRefreshListView.getRefreshableView().setAdapter(mAdapter);
        pullToRefreshListView.setOnRefreshListener(refreshListener);
    }

    @Override
    protected void initEvent() {
        titleBar.setDefaultOnBackListener(getActivity());
        titleBar.setTitle(R.string.title_activity_hottask);
        presenter.callRefreshList();
    }

    @Override
    protected IApiRequestPresenter getApiRequestPresenter() {
        return presenter;
    }

    @Override
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    private PullToRefreshBase.OnRefreshListener refreshListener = new PullToRefreshBase
            .OnRefreshListener() {
        @Override
        public void onRefresh(PullToRefreshBase refreshView) {
            PullToRefreshUtil.updateLastFreshTime(getActivity(), refreshView);
            presenter.callRefreshList();
        }
    };

    @Override
    public void showErrorMsg(String msg) {
        showMsg(msg);
    }

    @Override
    public void updateList(ArrayList<DemandItemData> datas) {
        maskView.hide();
        mAdapter.setLiDatas(datas);
        pullToRefreshListView.onRefreshComplete();
    }

    @Override
    public void showRetryView(View.OnClickListener listener) {
        pullToRefreshListView.onRefreshComplete();
        maskView.showAsNetErrorRetry(listener);
    }

    /**
     * 结束刷新、加载
     */
    @Override
    public void finishRefresh() {
        PullToRefreshUtil.updateLastFreshTime(getActivity(), pullToRefreshListView);
        pullToRefreshListView.onRefreshComplete();
    }

    @Override
    public void showEmptyView() {
        pullToRefreshListView.onRefreshComplete();
        maskView.showAsEmpty(getString(R.string.v1010_mask_empty));
    }

    /**
     * desc: 为item设置各种回调，
     *
     * @param position
     * @param data
     * @param view
     */
    @Override
    public void setCallBack(final int position, final DemandItemData data, DemandItemView view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DemandInfoActivityData _d = new DemandInfoActivityData();
                _d.setDemandId(data.getUniqueId());
                _d.setLoginInfo(IVerifyHolder.mLoginInfo);
                Intent intent = new Intent(getActivity(), DemandInfoActivity.class);
                intent.putExtra(DemandInfoActivity.KEY_DATA, JSON.toJSONString(_d));
                getActivity().startActivity(intent);
            }
        });
    }
}
