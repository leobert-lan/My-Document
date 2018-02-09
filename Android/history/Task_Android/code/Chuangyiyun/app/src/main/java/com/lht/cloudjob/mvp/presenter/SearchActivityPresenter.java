package com.lht.cloudjob.mvp.presenter;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.R;
import com.lht.cloudjob.clazz.LRTree;
import com.lht.cloudjob.customview.FilterSheet;
import com.lht.cloudjob.customview.SortSheet;
import com.lht.cloudjob.customview.TypeSheet;
import com.lht.cloudjob.interfaces.keys.DBConfig;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.DbCURDModel;
import com.lht.cloudjob.mvp.model.IndustryCategoryDBModel;
import com.lht.cloudjob.mvp.model.SearchModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.CategoryResBean;
import com.lht.cloudjob.mvp.model.bean.SearchResBean;
import com.lht.cloudjob.mvp.model.pojo.DemandItemData;
import com.lht.cloudjob.mvp.viewinterface.ISearchActivity;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.litesuits.orm.LiteOrm;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> SearchActivityPresenter
 * <p><b>Description</b>: 搜索、需求大厅的presenter
 * Created by leobert on 2016/8/15.
 */
public class SearchActivityPresenter implements IApiRequestPresenter,
        TypeSheet.OnSelectedListener, FilterSheet.OnSelectedListener, SortSheet.OnSelectedListener {

    private ISearchActivity iSearchActivity;

    public SearchActivityPresenter(ISearchActivity iSearchActivity) {
        this.iSearchActivity = iSearchActivity;
    }


    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    private LRTree lrTree;

    public void callGetCategoryData() {
        DbCURDModel<IndustryCategoryDBModel> model = new DbCURDModel<>(new
                CategoryModelDbCallback());
        model.doRequest();
    }

    private TypeSheet.SelectedItems selectedItems;
    private FilterSheet.FilterSelectedItems filterSelectedItems;
    private SortSheet.SortRules sortRules;

    @Override
    public void onTypeSelected(TypeSheet.SelectedItems selectedItems) {
        this.selectedItems = selectedItems;
        doSearch(keyword);
    }

    @Override
    public void onFilterSelected(FilterSheet.FilterSelectedItems filterSelectedItems) {
        this.filterSelectedItems = filterSelectedItems;
        doSearch(keyword);
    }

    @Override
    public void onSortRuleSelected(SortSheet.SortRules sortRules) {
        this.sortRules = sortRules;
        doSearch(keyword);
    }

    private String keyword = null;

    private boolean isRefreshOperate;

    public void doSearch(String keyword) {
        isRefreshOperate = true;
//        if (StringUtil.isEmpty(keyword)) {
//
//            iSearchActivity.showMsg(iSearchActivity.getActivity().getString(R.string.v1010_default_search_enter_search_word));
//            return;
//        }
        iSearchActivity.showWaitView(true);
        if (isResetNeed(keyword)) {
            iSearchActivity.resetAll();
            selectedItems = null;
            filterSelectedItems = null;
            sortRules = null;
        }
        doSearch(keyword, 0);
        this.keyword = keyword;
    }

    private void doSearch(String keyword, int offset) {
        SearchModel model = new SearchModel(new SearchModelCallback());
        CategoryResBean industry = null;
        if (selectedItems != null) {
            industry = selectedItems.getsClassItem();
        }
        model.setParams(keyword, industry, filterSelectedItems, sortRules, offset);
        model.doRequest(iSearchActivity.getActivity());
    }

    public void addSearchData(String keyword, int offset) {
        isRefreshOperate = false;
//        if (StringUtil.isEmpty(keyword)) {
//
//            iSearchActivity.showMsg(iSearchActivity.getActivity().getString(R.string.v1010_default_search_enter_search_word));
//            return;
//        }
        iSearchActivity.showWaitView(true);
        if (isResetNeed(keyword)) {
            iSearchActivity.resetAll();
            selectedItems = null;
            filterSelectedItems = null;
            sortRules = null;
        }
        doSearch(keyword, offset);
        this.keyword = keyword;
    }

    private boolean isResetNeed(String keyword) {
        if (keyword == null) {
            return false;
        }
        return !keyword.equals(this.keyword);
    }

    private class SearchModelCallback implements ApiModelCallback<ArrayList<SearchResBean>> {

        @Override
        public void onSuccess(BaseBeanContainer<ArrayList<SearchResBean>> beanContainer) {
            ArrayList<SearchResBean> data = beanContainer.getData();
            if (data == null || data.isEmpty()) {
                iSearchActivity.showEmptyView();
            } else {
                iSearchActivity.hideEmptyView();
                ArrayList<DemandItemData> liData = new ArrayList<>();
                for (SearchResBean bean : data) {
                    DemandItemData itemData = new DemandItemData();
                    bean.copyTo(itemData);
                    liData.add(itemData);
                }
                if (isRefreshOperate) {
                    iSearchActivity.setListData(liData);
                } else {
                    iSearchActivity.addListData(liData);
                }

            }
            iSearchActivity.cancelWaitView();
            iSearchActivity.finishRefresh();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iSearchActivity.cancelWaitView();

            if (isRefreshOperate) {
                //将列表数据清空
                iSearchActivity.setListData(new ArrayList<DemandItemData>());

                //显示空视图布局
                iSearchActivity.showEmptyView();
            }else{
                //已经到底了
                iSearchActivity.showErrorMsg(iSearchActivity.getActivity().getString(R.string.v1010_toast_list_all_data_added));
            }
            iSearchActivity.finishRefresh();
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iSearchActivity.cancelWaitView();
            iSearchActivity.finishRefresh();
        }
    }

    private class CategoryModelDbCallback implements DbCURDModel.IDbCURD<IndustryCategoryDBModel> {

        @Override
        public IndustryCategoryDBModel doCURDRequest() {
            LiteOrm liteOrm = LiteOrm.newSingleInstance(iSearchActivity.getActivity(), DBConfig
                    .BasicDb.DB_NAME);
            ArrayList<IndustryCategoryDBModel> data = liteOrm.query(IndustryCategoryDBModel.class);
            int size = data.size();
            if (size == 0)
                return null;
            return data.get(size - 1);
        }

        @Override
        public void onCURDFinish(IndustryCategoryDBModel industryCategoryDBModel) {
            ArrayList<CategoryResBean> data =
                    (ArrayList<CategoryResBean>) JSON.parseArray(industryCategoryDBModel.getData(),
                            CategoryResBean.class);
            if (data != null && data.size() > 0) {
                lrTree = new LRTree(data);
                iSearchActivity.updateCategoryData(lrTree);
            }
        }
    }

}
