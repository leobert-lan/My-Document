package com.lht.creationspace.base.model.apimodel;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.model
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> ApiModelCallback
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/15.
 */
public interface ApiModelCallback<T> {
    void onSuccess(BaseBeanContainer<T> beanContainer);

    void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer);

    void onHttpFailure(int httpStatus);
}
