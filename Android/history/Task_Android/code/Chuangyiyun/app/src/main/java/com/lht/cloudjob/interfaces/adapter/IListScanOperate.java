package com.lht.cloudjob.interfaces.adapter;

/**
 * <p><b>Package</b> com.lht.cloudjob.interfaces.adapter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IListScanOperate
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2017/1/17.
 */

public interface IListScanOperate<T> {
    void onScan(int position,T data);
}
