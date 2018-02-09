package com.lht.cloudjob.interfaces.net;

import android.content.Context;

/**
 * <p><b>Package</b> com.lht.cloudjob.interfaces.net
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IApiRequestPresenter
 * <p><b>Description</b>: 每一个进行网络请求的页面都有共同的行为：
 * 1.发起请求，
 * 2.关闭时取消请求的回调。
 * 第一点不适合做行为抽象。
 * 第二点是必须要做得，而且很容易抽象。
 * 定义成接口是因为这些presenter已经是具体的业务，表现基本不一，难以做出有价值的抽象，
 * 且很容易陷入无法调节的继承关系中。
 *
 * TODO 给所有的有网络请求的页面presenter实现该接口
 * Created by leobert on 2016/7/22.
 */
public interface IApiRequestPresenter {

    /**
     * 页面结束时取消所有相关的回调
     * @param context
     */
    void cancelRequestOnFinish(Context context);
}
