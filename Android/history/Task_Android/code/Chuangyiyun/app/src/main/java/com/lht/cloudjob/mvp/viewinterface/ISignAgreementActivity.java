package com.lht.cloudjob.mvp.viewinterface;

import com.lht.cloudjob.customview.CustomPopupWindow;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.viewinterface
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> ISignAgreementActivity
 * <p><b>Description</b>: TODO
 * <p>Created by Administrator on 2016/9/20.
 */

public interface ISignAgreementActivity extends IActivityAsyncProtected {

    void showErrorMsg(String message);

    void finishActivity();

    /**
     * @param price                   最终确认金额,格式化的
     * @param onPositiveClickListener 确认签署按钮
     */
    void showConfirmDialog(String price, CustomPopupWindow.OnPositiveClickListener onPositiveClickListener);

//    /**
//     * 设置实际签署协议时的价格
//     * @param real_cash
//     */
//    void setRealCash(int real_cash);
}
