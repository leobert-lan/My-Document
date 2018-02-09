package com.lht.creationspace.module.topic.ui;

import com.lht.creationspace.module.topic.model.pojo.TopicResBean;
import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;

/**
 * Created by chhyu on 2017/3/3.
 */

public interface IChooseTopicActivity extends IActivityAsyncProtected {

    void addTopicData(TopicResBean data);

    void setTopicData(TopicResBean bean);

    void showLoadingFailedView();

    void hideLoadingFailedView();
}
