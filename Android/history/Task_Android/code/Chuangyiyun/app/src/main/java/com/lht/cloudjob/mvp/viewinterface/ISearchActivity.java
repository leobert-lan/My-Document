package com.lht.cloudjob.mvp.viewinterface;

import com.lht.cloudjob.clazz.LRTree;
import com.lht.cloudjob.mvp.model.pojo.DemandItemData;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IearchActivity
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/8/3.
 */
public interface ISearchActivity extends IActivityAsyncProtected{
    void showCategoryPanel();

    void hideCategoryPanel();

    void showFilterPanel();

    void hideFilterPanel();

    void showSortPanel();

    void hideSortPanel();

    void resetAll();

    void updateCategoryData(LRTree lrTree);

    void showErrorMsg(String msg);

    void showEmptyView();

    void hideEmptyView();

    void addListData(ArrayList<DemandItemData> liData);

    void setListData(ArrayList<DemandItemData> liData);

    void finishRefresh();

}
