package com.lht.cloudjob.interfaces.adapter;

import android.view.View;

/**
 * package com.lht.cloudjob.interfaces.adapter
 * project AndroidBase
 * classname ISetCallbackForListItem
 * description: 设计目的：减少adapter和activity之间的依赖，为item中的内容设置回调
 * viewprovider已知ViewHolder模型，声明依赖时处理反泛化
 * Created by leobert on 2016/4/5.
 */
public interface ICustomizeListItem<V> {
    /**
     * desc: 定制化item，
     *
     * @param position     位置
     * @param convertView  视图
     * @param viewHolder   holder
     */
    void customize(int position,View convertView, V viewHolder);
}
