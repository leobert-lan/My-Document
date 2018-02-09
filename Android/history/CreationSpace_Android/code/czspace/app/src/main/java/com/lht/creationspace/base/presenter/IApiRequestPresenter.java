package com.lht.creationspace.base.presenter;

import android.content.Context;

/**
 * <p><b>Package</b> com.lht.creationspace.base.presenter
 * <p><b>Project</b> Flag
 * <p><b>Classname</b> IApiRequestPresenter
 * <p><b>Description</b>: 存在网络请求的页面presenter
 * Created by leobert on 2016/7/22.
 */
public interface IApiRequestPresenter {

    /**
     * 页面结束时取消所有相关的回调
     * @param context
     */
    void cancelRequestOnFinish(Context context);
}
