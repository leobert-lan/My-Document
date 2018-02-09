package com.lht.customwidgetlib.nestedscroll;

import android.support.v7.widget.RecyclerView;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ScrollView;

/**
 * <p><b>Package</b> com.lht.customwidgetlib.nestedscroll
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> AttachUtil
 * <p><b>Description</b>: to help for judge whether some scrollable widget is attached
 * <p> Create by Leobert on 2016/8/29
 */
public class AttachUtil {
    public static boolean isAdapterViewAttach(AbsListView listView){
        if (listView != null && listView.getChildCount() > 0) {
            if (listView.getChildAt(0).getTop() < 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isRecyclerViewAttach(RecyclerView recyclerView){
        if (recyclerView != null && recyclerView.getChildCount() > 0) {
            if (recyclerView.getChildAt(0).getTop() < 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isScrollViewAttach(ScrollView scrollView){
        if (scrollView != null) {
            if (scrollView.getScrollY() > 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isWebViewAttach(WebView webView){
        if (webView != null) {
            if (webView.getScrollY() > 0) {
                return false;
            }
        }
        return true;
    }
}
