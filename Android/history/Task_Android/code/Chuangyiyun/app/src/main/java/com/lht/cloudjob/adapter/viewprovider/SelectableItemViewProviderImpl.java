package com.lht.cloudjob.adapter.viewprovider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.lht.cloudjob.R;
import com.lht.cloudjob.interfaces.adapter.IListItemViewProvider;
import com.lht.cloudjob.interfaces.adapter.ICustomizeListItem;

/**
 * <p><b>Package</b> com.lht.cloudjob.adapter.viewprovider
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> SelectableItemViewProviderImpl
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/8/12.
 */
public class SelectableItemViewProviderImpl implements IListItemViewProvider<String> {

    private final LayoutInflater mInflater;

    private final ICustomizeListItem<ViewHolder> iCustomizeListItem;

    public SelectableItemViewProviderImpl(
            LayoutInflater inflater,
            ICustomizeListItem<ViewHolder> iCustomizeListItem) {

        this.mInflater = inflater;
        this.iCustomizeListItem = iCustomizeListItem;
    }

    @Override
    public View getView(int position, String item, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null && convertView.getTag() != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_grid_selectabletype, null);
            //TODO bind holder

            holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);
            convertView.setTag(holder);
        }
        holder.cb.setText(item);
        holder.cb.setOnCheckedChangeListener(null); //clear to init
        holder.cb.setChecked(position == getSelectedIndex());
        if (iCustomizeListItem != null)
            iCustomizeListItem.customize(position, convertView, holder);
        return convertView;
    }

    //默认选中第一项
    private int selectedIndex = 0;

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public class ViewHolder {
        public CheckBox cb;

    }
}
