package com.lht.customwidgetlib.actionsheet;

import android.view.View;
import android.view.ViewGroup;

/**
 * @classname IActionSheetItemViewProvider
 * @description: 抽象BaseAdapter的getView过程，减少Adapter和其他的依赖
 * Created by leobert on 2016/4/1.
 */
public interface IActionSheetItemViewProvider<T> {
    /**
     * desc: 提供每个item的View
     *
     * @param position    位置
     * @param item        原始数据
     * @param convertView
     * @param parent
     * @return
     */
     View getView(final int position, T item, View convertView, ViewGroup parent);
}
