package com.lht.cloudjob.clazz;

import java.util.ArrayList;
import java.util.List;

/**
 * <p><b>Package</b> com.lht.cloudjob.clazz
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> SelectableDataWrapper
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/9/19
 */
public class SelectableDataWrapper<D> {
    private final D data;

    private boolean isSelected = false;

    public SelectableDataWrapper(D data) {
        this.data = data;
    }

    public D getData() {
        return data;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public static <Data> SelectableDataWrapper<Data> wrapObject(Data object) {
        return new SelectableDataWrapper<>(object);
    }

    public static <Data> ArrayList<SelectableDataWrapper<Data>> wrapList(List<Data> list) {
        ArrayList<SelectableDataWrapper<Data>> ret = new ArrayList<>();
        if (list==null) {
            return ret;
        }
        for (Data data:list) {
            ret.add(wrapObject(data));
        }
        return ret;
    }

}
