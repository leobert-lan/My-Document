package com.lht.cloudjob.mvp.viewinterface;

import com.lht.cloudjob.clazz.LRTree;

/**
 * Created by chhyu on 2017/2/8.
 */

public interface IChooseCategoryActivity extends IActivityAsyncProtected {
    void updateCategoryData(LRTree lrTree);
}
