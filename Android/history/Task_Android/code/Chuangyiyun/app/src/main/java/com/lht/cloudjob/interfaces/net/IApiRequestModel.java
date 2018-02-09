package com.lht.cloudjob.interfaces.net;

import android.content.Context;

/**
 * <p><b>Package</b> com.lht.cloudjob.interfaces.net
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IApiRequestModel
 * <p><b>Description</b>: 所有的网络请求model实现该接口
 * Created by leobert on 2016/7/7.
 */
public interface IApiRequestModel extends ICancelRequest {
    /**
     * 执行请求
     * TODO: 合理的处理请求失败情况，
     * 当前的接口出现非200都是网络状态问题，没有赋予特殊含义。
     * 有三层可以处理失败情况：
     * 1.model层内部处理，实质是在Composite中的default实现中处理
     * 2.回调接口的缺省适配器，暂未实现之
     * 3.回调接口实现类单独处理。
     * @param context    发起请求的对象的上下文
     */
    void doRequest(Context context);
}
