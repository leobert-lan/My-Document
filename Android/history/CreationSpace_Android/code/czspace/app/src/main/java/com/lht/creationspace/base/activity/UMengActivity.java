package com.lht.creationspace.base.activity;

import android.os.Bundle;

import com.lht.creationspace.umeng.IUmengReport;
import com.lht.creationspace.util.debug.DLog;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;


/**
 *
 */
public abstract class UMengActivity extends ExclusivePopupWinsActivity implements IUmengReport {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 友盟统计 页面统计-页面打开
        reportPageStart(getPageName());
        MobclickAgent.onResume(getActivity());
        DLog.d(MobclickAgent.class, new DLog.LogLocation(), "onResume pagename:" + getPageName());
    }


    @Override
    protected void onPause() {
        super.onPause();
        // 友盟统计 页面统计-页面关闭
        reportPageEnd(getPageName());
        MobclickAgent.onPause(getActivity());
        DLog.d(MobclickAgent.class, new DLog.LogLocation(), "onpause pagename:" + getPageName());
    }

    /**
     * 报告页面打开
     *
     * @param pageName the page name
     */
    public void reportPageStart(String pageName) {
        MobclickAgent.onPageStart(pageName);
    }

    /**
     * desc: 报告页面关闭
     *
     * @param pageName the page name
     */
    public void reportPageEnd(String pageName) {
        MobclickAgent.onPageEnd(pageName);
    }

    /**
     * desc: 获取页面名称
     *
     * @return String
     */
    protected abstract String getPageName();

    /**
     * desc: 获取activity
     */
    @Override
    public abstract BaseActivity getActivity();

    /**
     * desc: 友盟统计-报告计数事件
     *
     * @param eventKey 事件key
     */
    public void reportCountEvent(String eventKey) {
        MobclickAgent.onEvent(getActivity(), eventKey);
    }

    @Override
    public void reportCountEvent(String eventKey,
                                 HashMap<String, String> attrMap) {
        MobclickAgent.onEvent(getActivity(), eventKey, attrMap);
    }

    @Override
    public void reportCalcEvent(String eventKey,
                                HashMap<String, String> attrMap, int du) {
        MobclickAgent.onEventValue(getActivity(), eventKey, attrMap, du);
    }
}
