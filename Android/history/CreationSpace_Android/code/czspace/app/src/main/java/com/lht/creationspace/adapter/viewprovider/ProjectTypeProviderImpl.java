package com.lht.creationspace.adapter.viewprovider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.lht.creationspace.R;
import com.lht.creationspace.adapter.AbsListAdapter;
import com.lht.creationspace.adapter.interfaces.IListItemViewProvider;
import com.lht.creationspace.module.proj.model.pojo.ProjectTypeResBean;

/**
 * Created by chhyu on 2017/3/7.
 */

public class ProjectTypeProviderImpl implements IListItemViewProvider<ProjectTypeResBean> {
    private final LayoutInflater mInflater;
    private final AbsListAdapter.ICustomizeListItem<ProjectTypeResBean, ViewHolder> iSetCallbackForListItem;

    public ProjectTypeProviderImpl(LayoutInflater inflater,
                                   AbsListAdapter.ICustomizeListItem<ProjectTypeResBean,
                                           ViewHolder> iSetCallbackForListItem) {
        this.mInflater = inflater;
        this.iSetCallbackForListItem = iSetCallbackForListItem;
    }

    @Override
    public View getView(int position, ProjectTypeResBean item, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null && convertView.getTag() != null) {
            holder = (ViewHolder) convertView.getTag();

        } else {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_project_type2, parent, false);
            holder.tvChildTypeName = (TextView) convertView.findViewById(R.id.tv_type_name);
            holder.cbChecked = (CheckBox) convertView.findViewById(R.id.cb_checked);
            convertView.setTag(holder);
        }
        holder.cbChecked.setOnCheckedChangeListener(null);
        holder.cbChecked.setChecked(position == selectedIndex);
        holder.tvChildTypeName.setText(item.getName());
        holder.cbChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CheckBox) view).setChecked(true);
            }
        });
        if (holder.cbChecked.isChecked()) {
            holder.tvChildTypeName.setTextColor(parent.getContext().getResources().getColor(R.color.main_green_dark));
        } else {
            holder.tvChildTypeName.setTextColor(parent.getContext().getResources().getColor(R.color.text_gray_content));
        }

        if (iSetCallbackForListItem != null) {
            iSetCallbackForListItem.customize(position, item, convertView, holder);
        }
        return convertView;
    }

    public class ViewHolder {
        private TextView tvChildTypeName;
        public CheckBox cbChecked;
    }

    private int selectedIndex = 0;

    public void notifySelected(int index) {
        this.selectedIndex = index;
    }
}
