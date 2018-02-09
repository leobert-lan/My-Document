package com.lht.cloudjob.adapter.viewprovider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.lht.cloudjob.R;
import com.lht.cloudjob.adapter.ListAdapter2;
import com.lht.cloudjob.clazz.SelectableDataWrapper;
import com.lht.cloudjob.interfaces.adapter.IListItemViewProvider;
import com.lht.cloudjob.mvp.model.bean.CategoryResBean;

/**
 * <p><b>Package</b> com.lht.cloudjob.adapter.viewprovider
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> SubscribeItemViewProviderImpl
 * <p><b>Description</b>: 订阅组视图中的gridviewitem视图提供器
 * Created by leobert on 2016/8/12.
 */
public class SubscribeItemViewProviderImpl implements
        IListItemViewProvider<SelectableDataWrapper<CategoryResBean>> {

    private final LayoutInflater mInflater;

    private final ListAdapter2.ICustomizeListItem2<SelectableDataWrapper<CategoryResBean>,
            ViewHolder> iSetCallbackForListItem;

    public SubscribeItemViewProviderImpl(
            LayoutInflater inflater,
            ListAdapter2.ICustomizeListItem2<SelectableDataWrapper<CategoryResBean>,
                    ViewHolder> iSetCallbackForListItem) {

        this.mInflater = inflater;
        this.iSetCallbackForListItem = iSetCallbackForListItem;
    }

    @Override
    public View getView(int position, SelectableDataWrapper<CategoryResBean> item, View
            convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null && convertView.getTag() != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_grid_subscribe, null);
            //TODO bind holder

            holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);
            convertView.setTag(holder);
        }
        holder.cb.setText(item.getData().getName());
        holder.cb.setOnCheckedChangeListener(null); //clear to init
        holder.cb.setChecked(item.isSelected());
        if (iSetCallbackForListItem != null)
            iSetCallbackForListItem.customize(position, item, convertView, holder);
        return convertView;
    }

    public class ViewHolder {
        public CheckBox cb;

    }
}
