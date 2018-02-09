package com.lht.ptrlib.library;

import android.content.Context;
import android.text.format.DateUtils;

/**
 * <p><b>Package</b> com.lht.ptrlib.library
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> PullToRefreshUtil
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/8/19
 */
public class PullToRefreshUtil {
    public static void updateLastFreshTime(Context context, PullToRefreshBase refreshView) {
        String label = DateUtils.formatDateTime(context, System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils
                        .FORMAT_ABBREV_ALL);
        refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
    }
}
