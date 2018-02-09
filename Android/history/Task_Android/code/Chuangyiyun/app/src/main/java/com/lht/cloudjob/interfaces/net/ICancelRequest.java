package com.lht.cloudjob.interfaces.net;

import android.content.Context;

/**
 * <p><b>Package</b> com.lht.cloudjob.interfaces.net
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> ICancelRequest
 * <p><b>Description</b>: 取消网络请求（回掉）
 * Created by leobert on 2016/5/4.
 */
public interface ICancelRequest {
    void cancelRequestByContext(Context context);
}
