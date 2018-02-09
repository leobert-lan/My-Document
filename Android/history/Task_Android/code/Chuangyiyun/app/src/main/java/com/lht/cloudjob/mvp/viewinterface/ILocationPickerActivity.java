package com.lht.cloudjob.mvp.viewinterface;

import com.lht.cloudjob.mvp.model.pojo.ParentEntity;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.viewinterface
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> ILocationPickerActivity
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/9/9
 */
public interface ILocationPickerActivity extends IActivityAsyncProtected {
    void updateList(ArrayList<ParentEntity> parents);
}
