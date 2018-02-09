package com.lht.cloudjob.mvp.viewinterface;

import android.app.Activity;

import com.lht.cloudjob.mvp.model.pojo.DemandItemData;
import com.lht.cloudjob.mvp.model.pojo.LoginInfo;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.viewinterface
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> ICollectTaskFragment
 * <p><b>Description</b>: 收藏需求列表页面
 * <p> Create by Leobert on 2016/8/23
 */
public interface ICollectTaskFragment extends IAsyncProtectedFragent {
    void setCollectedListItems(ArrayList<DemandItemData> datas);

    void addCollectedListItems(ArrayList<DemandItemData> datas);

    Activity getActivity();

    void showEmptyView();

    void hideEmptyView();

    void finishRefresh();

    LoginInfo getLoginInfo();

    void checkListData();

    void finishRefresh2();

    void setRecommendListItems(ArrayList<DemandItemData> liData);

    void addRecommentListItems(ArrayList<DemandItemData> liData);
}
