package com.lht.cloudjob.mvp.viewinterface;

import android.app.Activity;

import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.pojo.DemandItemData;
import com.lht.cloudjob.mvp.model.pojo.LoginInfo;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.viewinterface
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IOrderedTaskFragment
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/8/24
 */
public interface IOrderedTaskFragment extends IAsyncProtectedFragent {
    void setOrderedListItems(ArrayList<DemandItemData> datas);

    void addOrderedListItems(ArrayList<DemandItemData> datas);

    Activity getActivity();

    void showEmptyView();

    void hideEmptyView();

    void finishRefresh();

    LoginInfo getLoginInfo();

    void finishRefresh2();

    void setRecommendListItems(ArrayList<DemandItemData> liData);

    void addRecommendListItems(ArrayList<DemandItemData> liData);

    IRestfulApi.OrderedTaskListApi.Type getCurrentType();

    /**
     * 重新投稿
     * @param item 元数据
     */
    void reContribute(DemandItemData item);

    /**
     * 联系ta
     * @param item 元数据
     */
    void callPublisher(DemandItemData item);

    /**
     * 签署协议
     * @param item 元数据
     */
    void signAgreement(DemandItemData item);

    /**
     * 跳转到评价
     * @param item 元数据
     */
    void jump2Evaluate(DemandItemData item);
}
