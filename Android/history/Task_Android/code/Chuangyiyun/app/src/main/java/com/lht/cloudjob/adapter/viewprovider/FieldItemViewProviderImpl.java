package com.lht.cloudjob.adapter.viewprovider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lht.cloudjob.R;
import com.lht.cloudjob.interfaces.adapter.IListItemViewProvider;
import com.lht.cloudjob.interfaces.adapter.ICustomizeListItem;
import com.lht.cloudjob.mvp.model.bean.CategoryResBean;

public class FieldItemViewProviderImpl implements IListItemViewProvider<CategoryResBean> {
    private final LayoutInflater mInflater;

    private ICustomizeListItem<ViewHolder> iCustomizeListItem;

    public FieldItemViewProviderImpl(LayoutInflater mInflater,
                                     ICustomizeListItem<ViewHolder> iCustomizeListItem) {
        this.mInflater = mInflater;
        this.iCustomizeListItem = iCustomizeListItem;
    }

    /**
     * desc: 提供每个item的View
     *
     * @param position    位置
     * @param item        原始数据,泛化数据模型
     * @param convertView 复用视图
     * @param parent      parent
     * @return item视图
     */
    @Override
    public View getView(int position, CategoryResBean item, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null && convertView.getTag() != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_list_vocation_type, null);
            holder.fieldName = (TextView) convertView.findViewById(R.id.tv_field_name);
            convertView.setTag(holder);
        }
        holder.fieldName.setText(item.getName());
        if (iCustomizeListItem != null) {
            iCustomizeListItem.customize(position,convertView,holder);
        }
        return convertView;
    }

    public class ViewHolder {
        private TextView fieldName;
    }
}
