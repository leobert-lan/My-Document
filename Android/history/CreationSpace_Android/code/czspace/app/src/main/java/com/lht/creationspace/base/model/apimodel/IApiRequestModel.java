package com.lht.creationspace.base.model.apimodel;

import android.content.Context;

/**
 * <p><b>Package</b> com.lht.vsocyy.interfaces.net
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> IApiRequestModel
 * <p><b>Description</b>: 所有的网络请求model实现该接口
 *
 * added at 2017年06月27日: api&apinew 的接口是非restful的，而且面向"行为"设计的，
 * 大体变现为：url是表达了一个操作，qs或form包含了业务数据。
 * 所以，用DataModel or DataRepository implement IOperations to operate Data
 * 的形式去设计model层会显得很恶心（需要花费相当多的心思去将操作同一资源的不同接口进行整理，
 * 不同的原始数据进行整理，不同的返回数据进行整理...）。
 * 故而，model层仅仅对这些接口请求行为进行了封装和业务实现。
 *
 * 而创意空间的WebServer，大体上是RestFul-Api，所以可以不再沿用这一低劣的设计
 *
 * Created by leobert on 2016/7/7.
 */
public interface IApiRequestModel extends ICancelRequest {
    /**
     * 执行请求
     *
     * 当前的接口出现非200都是网络状态问题，没有赋予特殊含义。
     * 有三层可以处理失败情况：
     * 1.model层内部处理，实质是在Composite中的default实现中处理
     * 2.回调接口的缺省适配器，暂未实现之
     * 3.回调接口实现类单独处理。
     * @param context    发起请求的对象的上下文
     */
    void doRequest(Context context);
}
