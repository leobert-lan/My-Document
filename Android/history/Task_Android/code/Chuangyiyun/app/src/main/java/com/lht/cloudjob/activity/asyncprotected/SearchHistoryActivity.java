package com.lht.cloudjob.activity.asyncprotected;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.adapter.ListAdapter;
import com.lht.cloudjob.adapter.viewprovider.HotSearchItemViewProviderImpl;
import com.lht.cloudjob.adapter.viewprovider.SearchHistoryItemViewProviderImpl;
import com.lht.cloudjob.customview.ConflictGridView;
import com.lht.cloudjob.customview.ConflictListView;
import com.lht.cloudjob.customview.TitleBarSearch;
import com.lht.cloudjob.interfaces.adapter.ICustomizeListItem;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.interfaces.umeng.IUmengEventKey;
import com.lht.cloudjob.mvp.model.bean.HotSearchResBean;
import com.lht.cloudjob.mvp.presenter.SearchHistoryActivityPresenter;
import com.lht.cloudjob.mvp.viewinterface.ISearchHistoryActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class SearchHistoryActivity extends AsyncProtectedActivity implements ISearchHistoryActivity,
        View.OnClickListener {

    private static final String PAGENAME = "SearchHistoryActivity";

    private TitleBarSearch titleBarSearch;

    private ProgressBar progressBar;

    private SearchHistoryActivityPresenter presenter;

    private ConflictListView listHistory;

    private ConflictGridView gridHot;

    private ListAdapter<String> adapter, adapterHot;

    private TextView btnClean;

    private LinearLayout llHis;

    private LinearLayout llHot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_history);
        EventBus.getDefault().register(this);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected String getPageName() {
        return SearchHistoryActivity.PAGENAME;
    }

    @Override
    public UMengActivity getActivity() {
        return SearchHistoryActivity.this;
    }

    @Override
    protected void initView() {
        titleBarSearch = (TitleBarSearch) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        listHistory = (ConflictListView) findViewById(R.id.searchhis_list_history);
        gridHot = (ConflictGridView) findViewById(R.id.searchhis_grid_hot);

        btnClean = (TextView) findViewById(R.id.searchhis_btn_clean);

        llHis = (LinearLayout) findViewById(R.id.searchhis_ll_his);
        llHot = (LinearLayout) findViewById(R.id.searchhis_ll_hot);
    }

    @Override
    protected void initVariable() {
        adapter = new ListAdapter<>(new ArrayList<String>(),
                new SearchHistoryItemViewProviderImpl(getLayoutInflater(), listItemCallback));

        adapterHot = new ListAdapter<>(new ArrayList<String>(),
                new HotSearchItemViewProviderImpl(getLayoutInflater(), gridItemCallback));

        presenter = new SearchHistoryActivityPresenter(this);
    }

    @Override
    protected void initEvent() {
        titleBarSearch.setDefaultOnBackListener(getActivity());
        titleBarSearch.setCloseVisible(false);

        titleBarSearch.setOnSearchCalledListener(new TitleBarSearch.OnSearchCalledListener() {
            @Override
            public void onSearchCalled(String text) {
                callSearch(text);
            }
        });
        listHistory.setAdapter(adapter);

        gridHot.setAdapter(adapterHot);
        presenter.initSearchHistory();
        presenter.initHotSearch();

        btnClean.setOnClickListener(this);
        listHistory.setFocusable(false);
        gridHot.setFocusable(false);
    }

    @Override
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @Subscribe
    public void onEventMainThread(AppEvent.SearchCloseEvent event) {
        finish();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        presenter.callSaveHistory();
        super.onDestroy();
    }

    @Override
    protected IApiRequestPresenter getApiRequestPresenter() {
        return presenter;
    }

    @Override
    public void updateSearchHistory(ArrayList<String> data) {
        //需要反序
        ArrayList<String> trans = new ArrayList<>();
        for (int i = data.size() - 1; i >= 0; i--) {
            trans.add(data.get(i));
        }
        adapter.setLiData(trans);
        setCleanEnabled(data.size() != 0);
        resetHistoryView(data.size() != 0);
    }

    private void resetHistoryView(boolean hasHistory) {
        if (hasHistory) {
            if (llHis.getVisibility()==View.GONE) {
                llHot.setVisibility(View.GONE);//reorder
                llHis.setVisibility(View.VISIBLE);
                llHot.setVisibility(View.VISIBLE);
            }
        } else {
            llHis.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateHotSearch(ArrayList<HotSearchResBean> data) {
        ArrayList<String> trans = new ArrayList<>();
        for (HotSearchResBean bean :data) {
            trans.add(bean.getSearch_key());
        }
        adapterHot.setLiData(trans);
    }

    private void setCleanEnabled(boolean isCleanEnable) {
        if (isCleanEnable) {
            btnClean.setClickable(true);
            btnClean.setText(R.string.v1010_default_searchh_btn_clean);
        } else {
            btnClean.setClickable(false);
            btnClean.setText(R.string.v1010_default_searchh_hint_nullhistory);
        }
    }


    //历史
    private ICustomizeListItem<SearchHistoryItemViewProviderImpl.ViewHolder> listItemCallback
            = new ICustomizeListItem<SearchHistoryItemViewProviderImpl.ViewHolder>() {
        @Override
        public void customize(int position, View convertView, SearchHistoryItemViewProviderImpl.ViewHolder viewHolder) {
            viewHolder.getTvSearchKey().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //统计 通过历史数据查询 -计数
                    reportCountEvent(IUmengEventKey.KEY_SECRCH_BY_HISTORY);
                    String text = ((TextView) v).getText().toString();
                    titleBarSearch.setSearchKey(text);
                    callSearch(text);
                }
            });
        }
    };

    //热门
    private ICustomizeListItem<HotSearchItemViewProviderImpl.ViewHolder> gridItemCallback =
            new ICustomizeListItem<HotSearchItemViewProviderImpl.ViewHolder>() {
                @Override
                public void customize(int position, View convertView, HotSearchItemViewProviderImpl.ViewHolder viewHolder) {
                    viewHolder.getTvSearchKey().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //统计 通过推荐数据查询 -计数
                            reportCountEvent(IUmengEventKey.KEY_SEARCH_BY_RECOMMEND);

                            String text = ((TextView) v).getText().toString();
                            titleBarSearch.setSearchKey(text);
                            callSearch(text);
                        }
                    });
                }
            };

    private void callSearch(String key) {
        // TODO: 2016/8/2
        presenter.callSearch(key);
    }

    @Override
    public void jumpSearchActivity(String searchKey) {
        start(SearchActivity.class,SearchActivity.KEY_DATA,searchKey);
    }

    @Override
    public void showErrorMsg(String msg) {
        showMsg(msg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchhis_btn_clean:
                presenter.callCleanHistory();
                break;
            default:
                break;
        }
    }
}
