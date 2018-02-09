package com.lht.creationspace.module.home.ui;

import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;
import com.lht.creationspace.module.home.model.QuerySocialInfoModel;
import com.lht.creationspace.module.home.ui.fg.FgHomeMine;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.viewinterface
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> IHomeActivity
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/11.
 */
public interface IHomeActivity extends IActivityAsyncProtected {

    void jump2ArticlePublishActivity();

    void jump2ProjectPublishActivity();

    void updateTabMineNotifyBadge(boolean needNotify);
}
