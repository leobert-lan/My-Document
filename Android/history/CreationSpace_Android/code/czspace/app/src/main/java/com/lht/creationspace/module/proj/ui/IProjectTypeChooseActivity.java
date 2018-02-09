package com.lht.creationspace.module.proj.ui;

import com.lht.creationspace.module.proj.model.pojo.ProjectTypeResBean;
import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;

import java.util.ArrayList;

/**
 * Created by chhyu on 2017/3/7.
 */

public interface IProjectTypeChooseActivity extends IActivityAsyncProtected {
    void setParentData(ArrayList<ProjectTypeResBean> data);
}
