package com.lht.cloudjob.interfaces.bars;

/**
 * <p><b>Package</b> com.lht.cloudjob.interfaces.bars
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> OnToggleListener
 * <p><b>Description</b>: toolbar，more键指令回调接口
 * 注意，当前的UI情况不可能点击more键切换到关闭状态
 * Created by leobert on 2016/7/11.
 */
public interface OnToggleListener {

    /**
     * more键，关闭切换到打开状态
     */
    void onOpenMore();

    /**
     * more键，打开切换到关闭状态
     */
    void onCloseMore();

}
