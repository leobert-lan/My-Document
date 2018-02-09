package com.lht.creationspace.module.home.ui;

/**
 * 具有编辑状态的混合fragment
 * Created by leobert on 2017/3/2.
 */

public interface IHybridBatchOpFragment {
    /**
     * 关闭编辑状态
     */
    void closeBatchOpState();

    /**
     * 打开编辑状态
     */
    void openBatchOpState();
}
