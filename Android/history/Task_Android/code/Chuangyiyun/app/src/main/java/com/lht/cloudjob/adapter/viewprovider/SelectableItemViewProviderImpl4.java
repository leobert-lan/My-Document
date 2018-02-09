package com.lht.cloudjob.adapter.viewprovider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lht.cloudjob.R;
import com.lht.cloudjob.interfaces.adapter.IListItemViewProvider;
import com.lht.cloudjob.interfaces.adapter.ICustomizeListItem;
import com.lht.cloudjob.mvp.model.bean.CategoryResBean;

import java.util.HashSet;
import java.util.Set;

/**
 * <p><b>Package</b> com.lht.cloudjob.adapter.viewprovider
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> SelectableItemViewProviderImpl
 * <p><b>Description</b>: 选择标签
 * Created by leobert on 2016/8/12.
 */
public class SelectableItemViewProviderImpl4 implements IListItemViewProvider<CategoryResBean> {

    private final LayoutInflater mInflater;

    private final ICustomizeListItem<ViewHolder> iCustomizeListItem;

    public SelectableItemViewProviderImpl4(
            LayoutInflater inflater, ICustomizeListItem<ViewHolder> iCustomizeListItem) {

        this.mInflater = inflater;
        this.iCustomizeListItem = iCustomizeListItem;

        selectedItems = new HashSet<>();
    }

    private Set<Integer> selectedItems;

    public Set<Integer> getSelectedItems() {
        return selectedItems;
    }

    public boolean addSelectedItem(int position) {
        if (selectedItems.size() >= 3) {
            return false;
        }
        return selectedItems.add(position);
    }

    public boolean removeSelectedItem(int position) {
        return selectedItems.remove(position);
    }


    @Override
    public View getView(int position, CategoryResBean item, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null && convertView.getTag() != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_grid_selectabletype4, null);
            //TODO bind holder
            holder.root = (RelativeLayout) convertView.findViewById(R.id.rl_root);
            holder.name = (TextView) convertView.findViewById(R.id.item_name);
            holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);
            convertView.setTag(holder);
        }

        holder.name.setText(item.getName());
        holder.cb.setOnCheckedChangeListener(null); //clear to init

        boolean isSelected = selectedItems.contains(position);

        holder.cb.setChecked(isSelected);

        if (isSelected) {
            holder.root.setBackgroundResource(R.color.primary_background);
        } else {
            holder.root.setBackgroundResource(R.color.bg_white);
        }

        if (iCustomizeListItem != null)
            iCustomizeListItem.customize(position, convertView, holder);
        return convertView;
    }


    public class ViewHolder {
        public CheckBox cb;
        public RelativeLayout root;

        public TextView name;

    }
}
