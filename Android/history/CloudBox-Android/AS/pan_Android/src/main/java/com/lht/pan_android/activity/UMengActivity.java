package com.lht.pan_android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import com.lht.pan_android.Interface.IUmengEventKey;
import com.lht.pan_android.R;
import com.lht.pan_android.util.CloudBoxApplication;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.DLog.LogLocation;
import com.lht.pan_android.view.popupwins.CustomPopupWindow;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

public abstract class UMengActivity extends Activity implements IUmengEventKey {

    protected CustomPopupWindow pw = null;

    public final static String tag = "umeng";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CloudBoxApplication.addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 友盟统计 页面统计-页面打开
        reportPageStart(getPageName());

        MobclickAgent.onResume(getActivity());

        DLog.d(getClass(), new LogLocation(),
                "onresume pagename:" + getPageName() + "; check:" + getActivity().getPageName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 友盟统计 页面统计-页面关闭
        reportPageEnd(getPageName());

        MobclickAgent.onPause(getActivity());

        DLog.d(getClass(), new LogLocation(),
                "onpause pagename:" + getPageName() + "; check:" + getActivity().getPageName());
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            if (PlatformUtils.isXiaoMi()) {
//                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
//                return;
//            }
//        }
//        if (!PlatformUtils.isXiaoMi()) {
        overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_out_to_left);
//        }
    }

    @Override
    public void finish() {
        super.finish();
//        if (!PlatformUtils.isXiaoMi()) {
        restoreFinishAnim();
//        }
    }

    public void finishWithoutOverrideAnim() {
        super.finish();
    }

    protected void restoreFinishAnim() {
        overridePendingTransition(R.anim.activity_in_from_left, R.anim.activity_out_to_right);
    }

    /**
     * @param pageName
     * @Title: reportPageStart
     * @Description: 报告页面打开
     * @author: leobert.lan
     */
    protected void reportPageStart(String pageName) {
        MobclickAgent.onPageStart(pageName);
    }

    /**
     * @param pageName
     * @Title: reportPageEnd
     * @Description: 报告页面关闭
     * @author: leobert.lan
     */
    protected void reportPageEnd(String pageName) {
        MobclickAgent.onPageEnd(pageName);
    }

    /**
     * @return
     * @Title: getPageName
     * @Description: 获取页面名称
     * @author: leobert.lan
     */
    protected abstract String getPageName();

    /**
     * @return
     * @Title: getActivity
     * @Description: 获取activity
     * @author: leobert.lan
     */
    protected abstract UMengActivity getActivity();

    /**
     * @param eventKey 事件key
     * @Title: reportCountEvent
     * @Description: 友盟统计-报告计数事件
     * @author: leobert.lan
     */
    public void reportCountEvent(String eventKey) {
        MobclickAgent.onEvent(getActivity(), eventKey);
    }

    /**
     * @param eventKey 事件key
     * @param attrMap  属性集<br>
     *                 <String Key,String Value>: key:属性key，value：属性集
     * @Title: reportCountEvent
     * @Description: 友盟统计-报告计数事件
     * @author: leobert.lan
     */
    public void reportCountEvent(String eventKey, HashMap<String, String> attrMap) {
        MobclickAgent.onEvent(getActivity(), eventKey, attrMap);
    }

    /**
     * @param eventKey
     * @param attrMap
     * @param du
     * @Title: reportCalcEvent
     * @Description: TODO
     * @author: leobert.lan
     */
    protected void reportCalcEvent(String eventKey, HashMap<String, String> attrMap, int du) {
        MobclickAgent.onEventValue(getActivity(), eventKey, attrMap, du);
    }

    protected void hideSoftInput() {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void setLatestPopupWindow(CustomPopupWindow cpw) {
        DLog.d(getClass(), "设置最终的弹出窗");
        this.pw = cpw;
    }

}
