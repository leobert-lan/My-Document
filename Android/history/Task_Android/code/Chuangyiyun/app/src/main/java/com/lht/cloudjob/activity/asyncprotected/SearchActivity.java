package com.lht.cloudjob.activity.asyncprotected;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.adapter.DMOListAdapter;
import com.lht.cloudjob.adapter.viewprovider.SimpleTaskItemViewProviderImpl;
import com.lht.cloudjob.clazz.LRTree;
import com.lht.cloudjob.customview.DemandItemView;
import com.lht.cloudjob.customview.FilterSheet;
import com.lht.cloudjob.customview.MaskView;
import com.lht.cloudjob.customview.SortSheet;
import com.lht.cloudjob.customview.TitleBarSearch;
import com.lht.cloudjob.customview.TypeSheet;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.pojo.DemandInfoActivityData;
import com.lht.cloudjob.mvp.model.pojo.DemandItemData;
import com.lht.cloudjob.mvp.presenter.SearchActivityPresenter;
import com.lht.cloudjob.mvp.viewinterface.ISearchActivity;
import com.lht.ptrlib.library.PullToRefreshBase;
import com.lht.ptrlib.library.PullToRefreshListView;
import com.lht.ptrlib.library.PullToRefreshUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class SearchActivity extends AsyncProtectedActivity implements ISearchActivity, TypeSheet
        .IDataProvider, SimpleTaskItemViewProviderImpl.ISetCallbackForDemandItem {

    private static final String PAGENAME = "SearchActivity";

    private TitleBarSearch titleBarSearch;

    private ProgressBar progressBar;

    public static final String KEY_DATA = "_data";

    private String searchKey;

    private CheckBox cbType, cbFilter, cbSort;

    private RelativeLayout rlPanelSection;

    private SearchActivityPresenter presenter;

    private PullToRefreshListView pullToRefreshListView;

    private DMOListAdapter mAdapter;

    private MaskView maskView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchKey = getIntent().getStringExtra(KEY_DATA);
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
        return SearchActivity.PAGENAME;
    }

    @Override
    public UMengActivity getActivity() {
        return SearchActivity.this;
    }

    @Override
    protected void initView() {
        titleBarSearch = (TitleBarSearch) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        cbType = (CheckBox) findViewById(R.id.search_cb_type);
        cbFilter = (CheckBox) findViewById(R.id.search_cb_filter);
        cbSort = (CheckBox) findViewById(R.id.search_cb_sort);
        typeSheet = (TypeSheet) findViewById(R.id.search_typesheet);
        sortSheet = (SortSheet) findViewById(R.id.search_sortsheet);
        filterSheet = (FilterSheet) findViewById(R.id.search_filtersheet);
        rlPanelSection = (RelativeLayout) findViewById(R.id.search_rl_pannel);

        maskView = (MaskView) findViewById(R.id.mask);

        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.search_list_tasks);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
    }

    @Override
    protected void initVariable() {
        presenter = new SearchActivityPresenter(this);
        mAdapter = new DMOListAdapter(new ArrayList<DemandItemData>(),
                new SimpleTaskItemViewProviderImpl(getActivity(), this));
        pullToRefreshListView.getRefreshableView().setAdapter(mAdapter);
        pullToRefreshListView.setOnRefreshListener(refreshListener);
        pullToRefreshListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL||scrollState== SCROLL_STATE_FLING) {
                    hideSoftInputPanel();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @Override
    protected void initEvent() {
        maskView.hide();
        typeSheet.setOnSelectedListener(presenter);
        filterSheet.setOnSelectedListener(presenter);
        sortSheet.setOnSelectedListener(presenter);


        titleBarSearch.setDefaultOnBackListener(getActivity());
        titleBarSearch.setCloseVisible(true);
        titleBarSearch.setOnCloseClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new AppEvent.SearchCloseEvent());
                finish();
            }
        });
        //初始化列表
        setListData(new ArrayList<DemandItemData>());
        titleBarSearch.setSearchKey(searchKey);
        titleBarSearch.setOnSearchCalledListener(new TitleBarSearch.OnSearchCalledListener() {
            @Override
            public void onSearchCalled(String text) {
                searchKey = text;
                callSearch(text);
            }
        });


        cbType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showCategoryPanel();
                } else {
                    hideCategoryPanel();
                }
            }
        });

        cbFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showFilterPanel();
                } else {
                    hideFilterPanel();
                }
            }
        });

        cbSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showSortPanel();
                } else {
                    hideSortPanel();
                }
            }
        });

        typeSheet.setOnDismissListener(new TypeSheet.OnDismissListener() {
            @Override
            public void onDismiss() {
                cbType.setChecked(false);
            }
        });

        filterSheet.setOnDismissListener(new FilterSheet.OnDismissListener() {
            @Override
            public void onDismiss() {
                cbFilter.setChecked(false);
            }
        });

        sortSheet.setOnDismissListener(new SortSheet.OnDismissListener() {
            @Override
            public void onDismiss() {
                cbSort.setChecked(false);
            }
        });

        typeSheet.setOnSelectedListener(presenter);
        filterSheet.setOnSelectedListener(presenter);
        sortSheet.setOnSelectedListener(presenter);

        callSearch(searchKey);

        titleBarSearch.getEtSearch().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (filterSheet.isShown() || sortSheet.isShown() || typeSheet.isShown()) {
                    filterSheet.dismiss();
                    sortSheet.dismiss();
                    typeSheet.dismiss();
                }
                return false;
            }
        });
    }

    private PullToRefreshBase.OnRefreshListener2 refreshListener = new PullToRefreshBase
            .OnRefreshListener2() {
        /**
         * onPullDownToRefresh will be called only when the user has Pulled from
         * the start, and released.
         *
         * @param refreshView
         */
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            PullToRefreshUtil.updateLastFreshTime(getActivity(), refreshView);
            presenter.doSearch(searchKey);
        }

        /**
         * onPullUpToRefresh will be called only when the user has Pulled from
         * the end, and released.
         *
         * @param refreshView
         */
        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            PullToRefreshUtil.updateLastFreshTime(getActivity(), refreshView);
            presenter.addSearchData(searchKey, mAdapter.getDefaultPagedOffset());
        }
    };

    private TypeSheet typeSheet;

    private SortSheet sortSheet;

    private FilterSheet filterSheet;

    private void callSearch(String text) {
        hideSoftInputPanel();
        presenter.doSearch(text);
    }

    @Override
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    public void showCategoryPanel() {
        rlPanelSection.bringToFront();
        typeSheet.show();
        hideFilterPanel();
        hideSortPanel();

        hideSoftInputPanel();
    }

    @Override
    public void hideCategoryPanel() {
        typeSheet.dismiss();
    }

    @Override
    public void showFilterPanel() {
        rlPanelSection.bringToFront();
        filterSheet.show();
        hideCategoryPanel();
        hideSortPanel();

        hideSoftInputPanel();
    }

    @Override
    public void hideFilterPanel() {
        filterSheet.dismiss();
    }

    @Override
    public void showSortPanel() {
        rlPanelSection.bringToFront();
        sortSheet.show();
        hideCategoryPanel();
        hideFilterPanel();

        hideSoftInputPanel();
    }

    @Override
    public void hideSortPanel() {
        sortSheet.dismiss();
    }

    @Override
    public void resetAll() {
        typeSheet.reset();
        filterSheet.resetWithoutCallback();
        sortSheet.reset();
    }

    @Override
    public void updateCategoryData(LRTree lrTree) {
        typeSheet.setLrTree(lrTree);
    }

    @Override
    public void showErrorMsg(String msg) {
        showMsg(msg);
    }

    @Override
    public void showEmptyView() {
        maskView.showAsCustom(R.drawable.v1011_drawable_mask_empty_search, getString(R.string
                .v1010_default_search_no_content));
    }

    @Override
    public void hideEmptyView() {
        maskView.hide();
    }

    @Override
    public void addListData(ArrayList<DemandItemData> liData) {
        mAdapter.addLiDatas(liData);
    }

    @Override
    public void setListData(ArrayList<DemandItemData> liData) {
        pullToRefreshListView.getRefreshableView().smoothScrollToPosition(0);
        mAdapter.setLiDatas(liData);
    }

    @Override
    public void finishRefresh() {
        pullToRefreshListView.onRefreshComplete();
    }

    @Override
    public void callProvideDataAsync() {
        if (presenter != null)
            presenter.callGetCategoryData();
    }

    @Override
    public void onBackPressed() {
        if (filterSheet.isShown() || sortSheet.isShown() || typeSheet.isShown()) {
            filterSheet.dismiss();
            sortSheet.dismiss();
            typeSheet.dismiss();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void setCallBack(int position, final DemandItemData data, DemandItemView view) {
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
