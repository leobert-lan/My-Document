package com.lht.cloudjob.interfaces.adapter;

import android.view.View;
import android.view.ViewGroup;

/**
 * classname IListItemViewProvider
 * description: 抽象BaseAdapter的getView过程，减少Adapter和其他的依赖
 * adapter依赖本接口，数据模型由adapter传递
 * Created by leobert on 2016/4/1.
 */
public interface IListItemViewProvider<D> {
    /**
     * desc: 提供每个item的View
     *
     * @param position    位置
     * @param item        原始数据,泛化数据模型
     * @param convertView 复用视图
     * @param parent parent
     * @return item视图
     */
    View getView(final int position, D item, View convertView, ViewGroup parent);
}
