package com.lht.cloudjob.mvp.model.pojo;

import com.lht.cloudjob.clazz.SelectableDataWrapper;
import com.lht.cloudjob.mvp.model.bean.CategoryResBean;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.pojo
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IndustryWrapper
 * <p><b>Description</b>: 订阅行业数据包裹，包含一级parent:Field、二级list:Label
 * <p> Create by Leobert on 2016/9/19
 */
public class IndustryWrapper {
    private CategoryResBean field;
    private ArrayList<SelectableDataWrapper<CategoryResBean>> labels;

    public CategoryResBean getField() {
        return field;
    }

    public void setField(CategoryResBean field) {
        this.field = field;
    }

    public ArrayList<SelectableDataWrapper<CategoryResBean>> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<SelectableDataWrapper<CategoryResBean>> labels) {
        this.labels = labels;
    }
}
