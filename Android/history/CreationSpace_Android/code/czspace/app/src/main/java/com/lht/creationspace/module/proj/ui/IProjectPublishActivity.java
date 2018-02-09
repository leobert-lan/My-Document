package com.lht.creationspace.module.proj.ui;

import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;

/**
 * Created by chhyu on 2017/2/20.
 */

public interface IProjectPublishActivity extends IActivityAsyncProtected {
    void notifyProjectNameOverLength();

    void updateCurrentLength(int edittextId, int currentCount, int remains);
}
