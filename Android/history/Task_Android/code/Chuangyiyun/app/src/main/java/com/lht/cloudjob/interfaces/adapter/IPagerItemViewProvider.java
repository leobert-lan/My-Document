package com.lht.cloudjob.interfaces.adapter;

import android.view.View;
import android.view.ViewGroup;

public interface IPagerItemViewProvider<D> {
    /**
     * desc: 提供每个item的View
     *
     * @param position    位置
     * @param item        原始数据,泛化数据模型
     * @param container   容器
     * @return item视图
     */
    View getView(final int position, D item, ViewGroup container);
}
