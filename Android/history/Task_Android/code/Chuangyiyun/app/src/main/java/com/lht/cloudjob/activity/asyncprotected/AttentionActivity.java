package com.lht.cloudjob.activity.asyncprotected;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.adapter.ListAdapter;
import com.lht.cloudjob.adapter.viewprovider.AttentionItemViewProviderImpl;
import com.lht.cloudjob.customview.TitleBar;
import com.lht.cloudjob.interfaces.adapter.ICustomizeListItem;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.bean.FavorListItemResBean;
import com.lht.cloudjob.mvp.presenter.AttentionActivityPresenter;
import com.lht.cloudjob.mvp.viewinterface.IAttentionActivity;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.ptrlib.library.PullToRefreshBase;
import com.lht.ptrlib.library.PullToRefreshListView;
import com.lht.ptrlib.library.PullToRefreshUtil;

import java.util.ArrayList;

/**
 * 我的关注页面
 */
public class AttentionActivity extends AsyncProtectedActivity implements IAttentionActivity,
        ICustomizeListItem<AttentionItemViewProviderImpl.ViewHolder> {

    private static final String PAGENAME = "AttentionActivity";

    private TitleBar mTitleBar;

    private ProgressBar progressBar;

    private AttentionActivityPresenter presenter;

    public static final String KEY_DATA = "data_username";

    private ListAdapter<FavorListItemResBean> listAdapter;

    private AttentionItemViewProviderImpl itemViewProvider;

    private PullToRefreshListView listView;

    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention);

        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected IApiRequestPresenter getApiRequestPresenter() {
        return presenter;
    }

    @Override
    protected String getPageName() {
        return AttentionActivity.PAGENAME;
    }

    @Override
    public UMengActivity getActivity() {
        return AttentionActivity.this;
    }

    @Override
    protected void initView() {
        mTitleBar = (TitleBar) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        emptyView = findViewById(R.id.attention_view_empty);
        listView = (PullToRefreshListView) findViewById(R.id.attention_list_favor);
    }

    @Override
    protected void initVariable() {
        presenter = new AttentionActivityPresenter(this);
        itemViewProvider = new AttentionItemViewProviderImpl(getLayoutInflater(), this);
        listAdapter = new ListAdapter<>(new ArrayList<FavorListItemResBean>(), itemViewProvider);
    }

    @Override
    protected void initEvent() {
        mTitleBar.setDefaultOnBackListener(getActivity());
        mTitleBar.setTitle(R.string.title_activity_attention);

        listView.setAdapter(listAdapter);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(refreshListener);
        presenter.callRefreshList();
    }

    @Override
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    public void setFavorListItems(ArrayList<FavorListItemResBean> items) {
        DLog.e(getClass(), "refresh on set");
        emptyView.setVisibility(View.GONE);
        listAdapter.setLiData(items);
        checkWhetherEmptyList();
    }

    @Override
    public void addFavorListItems(ArrayList<FavorListItemResBean> items) {
        DLog.e(getClass(), "refresh on add");
        emptyView.setVisibility(View.GONE);
        listAdapter.addLiData(items);
        checkWhetherEmptyList();
    }

    private void checkWhetherEmptyList() {
        if (listAdapter.getCount() == 0) {
            showEmptyView();
        }
    }

    @Override
    public void showEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
        emptyView.bringToFront();
    }

    @Override
    public void finishListFresh() {
        listView.onRefreshComplete();
    }

    @Override
    public void showErrorMsg(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void removeItem(int index, FavorListItemResBean listItemBean) {
        listAdapter.removeItem(index);
// TODO: 2016/8/12
        checkWhetherEmptyList();
    }

    @Override
    public void customize(final int position, View convertView, AttentionItemViewProviderImpl.ViewHolder viewHolder) {
        viewHolder.cancelAttention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.callUnfollow(position,listAdapter.getItem(position));
            }
        });
    }

    private PullToRefreshBase.OnRefreshListener2 refreshListener = new PullToRefreshBase.OnRefreshListener2() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            PullToRefreshUtil.updateLastFreshTime(getActivity(), refreshView);
            presenter.callRefreshList();
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            PullToRefreshUtil.updateLastFreshTime(getActivity(), refreshView);
            presenter.callAddNextPage(listAdapter.getCount());

        }
    };
}
