package com.lht.cloudjob.mvp.viewinterface;

import com.lht.cloudjob.mvp.model.pojo.IndustryWrapper;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.viewinterface
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> ISubscribeActivity
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/9/19
 */
public interface ISubscribeActivity extends IActivityAsyncProtected {
    void setListData(ArrayList<IndustryWrapper> listData);

    void showErrorMsg(String msg);

    void finishActivity();
}
