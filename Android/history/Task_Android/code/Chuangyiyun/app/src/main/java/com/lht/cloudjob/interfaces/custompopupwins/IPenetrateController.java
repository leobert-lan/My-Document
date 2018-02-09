package com.lht.cloudjob.interfaces.custompopupwins;

import android.app.Activity;

/**
 * <p><b>Package</b> com.lht.cloudjob.interfaces.custompopupwins
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IPenetrateController
 * <p><b>Description</b>: 展现弹窗时，控制防止点击穿透控制接口
 * Created by leobert on 2016/5/11.
 */
public interface IPenetrateController {

    /**
     * 设置是否可穿透，就实现而言，仅实现不可穿透的代码
     *
     * 一般activity类实现
     * @param activity
     *     需要作用的activity，预留
     * @param isProtectNeed
     *     true if prevent any touch event of the activity，false otherwise
     */
    void setPenetrable(Activity activity, boolean isProtectNeed);
}
