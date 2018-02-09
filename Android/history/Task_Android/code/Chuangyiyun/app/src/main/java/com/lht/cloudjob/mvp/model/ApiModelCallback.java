package com.lht.cloudjob.mvp.model;

import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> ApiModelCallback
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/15.
 */
public interface ApiModelCallback<T> {
    void onSuccess(BaseBeanContainer<T> beanContainer);

    void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer);

    void onHttpFailure(int httpStatus);
}
