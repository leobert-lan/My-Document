package com.lht.cloudjob.activity.asyncprotected;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.adapter.ListAdapter2;
import com.lht.cloudjob.adapter.viewprovider.VsoAcItemViewProviderImpl;
import com.lht.cloudjob.customview.MaskView;
import com.lht.cloudjob.customview.TitleBar;
import com.lht.cloudjob.customview.TitleBarModify;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.bean.VsoActivitiesResBean;
import com.lht.cloudjob.mvp.presenter.VsoAcListActivityPresenter;
import com.lht.cloudjob.mvp.viewinterface.IVsoAcListActivity;
import com.lht.ptrlib.library.PullToRefreshBase;
import com.lht.ptrlib.library.PullToRefreshListView;
import com.lht.ptrlib.library.PullToRefreshUtil;

import java.util.ArrayList;

public class VsoAcListActivity extends AsyncProtectedActivity implements
        IVsoAcListActivity, ListAdapter2.ICustomizeListItem2<VsoActivitiesResBean,
        VsoAcItemViewProviderImpl.ViewHolder> {

    private static final String PAGENAME = "VsoAcListActivity";
    private MaskView maskView;
    private TitleBar titleBar;
    private ProgressBar progressBar;
    private PullToRefreshListView pullToRefreshListView;
    private VsoAcListActivityPresenter presenter;
    private VsoAcItemViewProviderImpl itemViewProvider;
    private ListAdapter2<VsoActivitiesResBean> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vso_ac_list);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected void initView() {
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        titleBar = (TitleBar) findViewById(R.id.titlebar);
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.vso_activities_list);
        maskView = (MaskView) findViewById(R.id.mask);
    }

    @Override
    protected void initVariable() {
        presenter = new VsoAcListActivityPresenter(this);
        itemViewProvider = new VsoAcItemViewProviderImpl(getLayoutInflater(), this);
        listAdapter = new ListAdapter2<>(new ArrayList<VsoActivitiesResBean>(), itemViewProvider);
        pullToRefreshListView.getRefreshableView().setAdapter(listAdapter);
    }

    @Override
    protected void initEvent() {
        titleBar.setDefaultOnBackListener(getActivity());
        titleBar.setTitle(R.string.title_activity_vso_activities);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        pullToRefreshListView.setOnRefreshListener(refreshListener);
        presenter.callRefreshMessages();
    }

    private PullToRefreshBase.OnRefreshListener2<ListView> refreshListener = new
            PullToRefreshBase.OnRefreshListener2<ListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    PullToRefreshUtil.updateLastFreshTime(getActivity(), refreshView);
                    presenter.callRefreshMessages();
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                    PullToRefreshUtil.updateLastFreshTime(getActivity(), refreshView);
                    presenter.callAddMessages(listAdapter.getCount());
                }
            };

    @Override
    protected String getPageName() {
        return VsoAcListActivity.PAGENAME;
    }

    @Override
    public UMengActivity getActivity() {
        return VsoAcListActivity.this;
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
    public void showEmptyView() {
//        titleBar.setHideRightToggle();
        maskView.showAsEmpty(getString(R.string.v1010_mask_empty_msg));
    }

    @Override
    public void setMessageListData(ArrayList<VsoActivitiesResBean> data) {
        maskView.hide();
        listAdapter.setLiData(data);
    }

    @Override
    public void addMessageListData(ArrayList<VsoActivitiesResBean> data) {
        maskView.hide();
        listAdapter.addLiData(data);
    }

    @Override
    public void finishRefresh() {
        PullToRefreshUtil.updateLastFreshTime(this, pullToRefreshListView);
        pullToRefreshListView.onRefreshComplete();
    }

    @Override
    public void showRetryView(View.OnClickListener onClickListener) {
        pullToRefreshListView.onRefreshComplete();
        maskView.showAsNetErrorRetry(onClickListener);
    }

    @Override
    public void customize(int position, final VsoActivitiesResBean data, View convertView,
                          VsoAcItemViewProviderImpl.ViewHolder viewHolder) {
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BannerInfoActivity.class);
                intent.putExtra(BannerInfoActivity.KEY_DATA, data.getActivity_url());
                intent.putExtra(UMengActivity.KEY_ORIGINCLASS_SHOULDRESTRICTONFINISH,
                        getActivity().getClass().getName());
                startActivity(intent);
            }
        });
    }
}
