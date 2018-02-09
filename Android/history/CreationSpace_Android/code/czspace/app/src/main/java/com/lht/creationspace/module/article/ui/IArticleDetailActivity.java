package com.lht.creationspace.module.article.ui;

import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;

/**
 * <p><b>Package:</b> com.lht.creationspace.mvp.viewinterface </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> IArticleDetailActivity </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/3/13.
 */

public interface IArticleDetailActivity extends IActivityAsyncProtected {

    void manualSetCollectState(boolean isCollected);
}
