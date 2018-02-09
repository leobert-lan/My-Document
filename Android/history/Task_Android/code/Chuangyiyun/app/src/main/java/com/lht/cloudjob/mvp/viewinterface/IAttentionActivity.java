package com.lht.cloudjob.mvp.viewinterface;

import com.lht.cloudjob.mvp.model.bean.FavorListItemResBean;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.viewinterface
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IAttentionActivity
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/8/11.
 */
public interface IAttentionActivity extends IActivityAsyncProtected {

    void setFavorListItems(ArrayList<FavorListItemResBean> items);

    void addFavorListItems(ArrayList<FavorListItemResBean> items);

    void showEmptyView();

    void finishListFresh();

    void showErrorMsg(String message);

    void removeItem(int index, FavorListItemResBean listItemBean);
}