package com.lht.cloudjob.util;

import android.os.Build;

/**
 * <p><b>Package</b> com.lht.cloudjob.util
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> PlatformUtils
 * <p><b>Description</b>: 手机平台判断
 * <p>Created by leobert on 2016/12/27.
 */

public class PlatformUtils {

    /**
     * 是否是小米手机
     */
    public static boolean isXiaoMi() {
        return Build.MANUFACTURER.equalsIgnoreCase("Xiaomi");
    }
}
