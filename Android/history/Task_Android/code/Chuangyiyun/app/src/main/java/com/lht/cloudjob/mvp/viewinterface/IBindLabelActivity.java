package com.lht.cloudjob.mvp.viewinterface;

import com.lht.cloudjob.mvp.model.bean.CategoryResBean;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.viewinterface
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IBindLabelActivity
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/8/30
 */
public interface IBindLabelActivity extends IActivityAsyncProtected {

    void setGridData(ArrayList<CategoryResBean> data);

    void showErrorMsg(String msg);
}
