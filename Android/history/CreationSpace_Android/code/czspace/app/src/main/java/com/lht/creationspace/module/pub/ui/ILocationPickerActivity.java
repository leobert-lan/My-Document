package com.lht.creationspace.module.pub.ui;

import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;
import com.lht.creationspace.base.model.pojo.ParentEntity;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.viewinterface
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> ILocationPickerActivity
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/9/9
 */
public interface ILocationPickerActivity extends IActivityAsyncProtected {
    void updateList(ArrayList<ParentEntity> parents);
}
