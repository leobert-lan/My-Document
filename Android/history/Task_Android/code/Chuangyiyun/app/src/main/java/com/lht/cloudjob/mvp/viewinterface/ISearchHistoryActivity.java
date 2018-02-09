package com.lht.cloudjob.mvp.viewinterface;

import com.lht.cloudjob.mvp.model.bean.HotSearchResBean;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.viewinterface
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> ISearchHistoryActivity
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/8/1.
 */
public interface ISearchHistoryActivity extends IActivityAsyncProtected {

    /**
     * 更新历史数据
     *
     * @param data 数据
     */
    void updateSearchHistory(ArrayList<String> data);

    /**
     * 更新热门搜索
     *
     * @param data 数据
     */
    void updateHotSearch(ArrayList<HotSearchResBean> data);

    void jumpSearchActivity(String searchKey);

    void showErrorMsg(String msg);
}
