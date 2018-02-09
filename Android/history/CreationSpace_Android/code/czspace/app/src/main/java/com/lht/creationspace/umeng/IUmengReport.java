package com.lht.creationspace.umeng;

import com.lht.creationspace.base.activity.BaseActivity;

import java.util.HashMap;

/**
 * @package com.lht.vsocyy.interfaces
 * @project AndroidBase
 * @classname IUmengReport
 * @description: 进行友盟统计的接口，实现于activity、fragment，调用于presenter，presenter提供model需要的回调
 * Created by leobert on 2016/4/1.
 */
public interface IUmengReport extends IUmengEventKey {
    /**
     * @param pageName
     */
    void reportPageStart(String pageName);

    /**
     * @param pageName
     */
    void reportPageEnd(String pageName);

    /**
     * desc: 友盟统计-报告计数事件
     *
     * @param eventKey
     */
    void reportCountEvent(String eventKey);

    /**
     * desc:友盟统计-报告计数事件
     *
     * @param eventKey 事件key
     * @param attrMap  属性集<br>
     *                 <String Key,String Value>: key:属性key，value：属性集
     */
    void reportCountEvent(String eventKey,
                          HashMap<String, String> attrMap);


    /**
     * @param eventKey
     * @param attrMap
     * @param du
     */
    void reportCalcEvent(String eventKey,
                         HashMap<String, String> attrMap, int du);

    BaseActivity getActivity();
}
