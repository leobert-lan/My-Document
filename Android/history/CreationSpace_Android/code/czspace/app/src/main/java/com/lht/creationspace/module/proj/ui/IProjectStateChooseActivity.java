package com.lht.creationspace.module.proj.ui;

import com.lht.creationspace.module.proj.model.pojo.ProjectStateResBean;
import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;

import java.util.ArrayList;

/**
 * Created by chhyu on 2017/3/8.
 */

public interface IProjectStateChooseActivity extends IActivityAsyncProtected {
    void updateProjectStateDate(ArrayList<ProjectStateResBean> bean);
}
