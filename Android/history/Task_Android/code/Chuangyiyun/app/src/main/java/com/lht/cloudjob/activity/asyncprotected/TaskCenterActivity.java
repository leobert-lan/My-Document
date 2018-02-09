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
import com.lht.cloudjob.mvp.viewinterface.ITaskCenterActivity;
import com.lht.ptrlib.library.PullToRefreshBase;
import com.lht.ptrlib.library.PullToRefreshListView;
import com.lht.ptrlib.library.PullToRefreshUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.activity.asyncprotected
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> TaskCenterActivity
 * <p><b>Description</b>: 任务大厅
 * <p> Create by Leobert on 2016/8/17
 */
public class TaskCenterActivity extends AsyncProtectedActivity implements ITaskCenterActivity,
        TypeSheet.IDataProvider, SimpleTaskItemViewProviderImpl.ISetCallbackForDemandItem {

    private static final String PAGENAME = "TaskCenterActivity";

    private TitleBarSearch titleBarSearch;

    private ProgressBar progressBar;

    public static final String KEY_DATA = "_data";

    private String searchKey = "";

    private CheckBox cbType, cbFilter, cbSort;

    private RelativeLayout rlPanelSection;

    private SearchActivityPresenter presenter;

    private PullToRefreshListView pullToRefreshListView;

    private DMOListAdapter mAdapter;

    private MaskView maskView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_center);
        searchKey = getIntent().getStringExtra(KEY_DATA);
        presenter = new SearchActivityPresenter(this);
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
        return TaskCenterActivity.PAGENAME;
    }

    @Override
    public UMengActivity getActivity() {
        return TaskCenterActivity.this;
    }

    @Override
    protected void initView() {

        titleBarSearch = (TitleBarSearch) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        cbType = (CheckBox) findViewById(R.id.taskcenter_cb_type);
        cbFilter = (CheckBox) findViewById(R.id.taskcenter_cb_filter);
        cbSort = (CheckBox) findViewById(R.id.taskcenter_cb_sort);
        typeSheet = (TypeSheet) findViewById(R.id.taskcenter_typesheet);
        sortSheet = (SortSheet) findViewById(R.id.taskcenter_sortsheet);
        filterSheet = (FilterSheet) findViewById(R.id.taskcenter_filtersheet);
        rlPanelSection = (RelativeLayout) findViewById(R.id.taskcenter_rl_pannel);
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.taskcenter_list_tasks);

        maskView = (MaskView) findViewById(R.id.mask);
    }

    @Override
    protected void initVariable() {
        presenter = new SearchActivityPresenter(this);
        mAdapter = new DMOListAdapter(new ArrayList<DemandItemData>(),
                new SimpleTaskItemViewProviderImpl(getActivity(), this));
        pullToRefreshListView.getRefreshableView().setAdapter(mAdapter);
    }

    @Override
    protected void initEvent() {
        pullToRefreshListView.setOnRefreshListener(refreshListener);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        pullToRefreshListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL || scrollState == SCROLL_STATE_FLING) {
                    hideSoftInputPanel();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        typeSheet.setOnSelectedListener(presenter);
        filterSheet.setOnSelectedListener(presenter);
        sortSheet.setOnSelectedListener(presenter);


        titleBarSearch.setOnBackListener(new TitleBarSearch.ITitleBackListener() {
            @Override
            public void onTitleBackClick() {
                onLastChainInBackPressedWillCall();
                finish();
            }
        });
        titleBarSearch.setCloseVisible(false);
        titleBarSearch.setOnCloseClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new AppEvent.SearchCloseEvent());
                finish();
            }
        });

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

        presenter.doSearch("");

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

    private TypeSheet typeSheet;

    private SortSheet sortSheet;

    private FilterSheet filterSheet;

    private void callSearch(String text) {
        // TODO: 2016/8/3

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

    /**
     * 结束刷新，加载
     */
    @Override
    public void finishRefresh() {
        PullToRefreshUtil.updateLastFreshTime(this, pullToRefreshListView);
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

    private PullToRefreshBase.OnRefreshListener2 refreshListener = new PullToRefreshBase
            .OnRefreshListener2() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            presenter.doSearch(searchKey);
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            presenter.addSearchData(searchKey,mAdapter.getDefaultPagedOffset());
        }

    };

    @Override
    public void setCallBack(int position, final DemandItemData data, DemandItemView view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DemandInfoActivityData demandInfo = new DemandInfoActivityData();
                demandInfo.setDemandId(data.getUniqueId());
                demandInfo.setLoginInfo(IVerifyHolder.mLoginInfo);
                Intent intent = new Intent(getActivity(), DemandInfoActivity.class);
                intent.putExtra(DemandInfoActivity.KEY_DATA, JSON.toJSONString(demandInfo));
                getActivity().startActivity(intent);
            }
        });
    }

}