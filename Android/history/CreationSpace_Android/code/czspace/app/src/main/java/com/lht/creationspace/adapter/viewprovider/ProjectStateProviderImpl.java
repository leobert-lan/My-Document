package com.lht.creationspace.adapter.viewprovider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lht.creationspace.R;
import com.lht.creationspace.adapter.AbsListAdapter;
import com.lht.creationspace.adapter.interfaces.IListItemViewProvider;
import com.lht.creationspace.module.proj.model.pojo.ProjectStateResBean;

/**
 * Created by chhyu on 2017/3/8.
 */

public class ProjectStateProviderImpl implements IListItemViewProvider<ProjectStateResBean> {
    private LayoutInflater mInflater;
    private AbsListAdapter.ICustomizeListItem<ProjectStateResBean, ViewHolder> iCustomizeListItem;

    public ProjectStateProviderImpl(LayoutInflater inflater, AbsListAdapter.ICustomizeListItem<ProjectStateResBean, ViewHolder> iCustomizeListItem) {
        this.mInflater = inflater;
        this.iCustomizeListItem = iCustomizeListItem;
    }

    @Override
    public View getView(int position, ProjectStateResBean item, View convertView,
                        ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null && convertView.getTag() != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.project_state_item, parent,false);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_state_name);
        }
        holder.tvName.setText(item.getName());

        if (iCustomizeListItem != null) {
            iCustomizeListItem.customize(position, item, convertView, holder);
        }
        return convertView;
    }

    public class ViewHolder {
        private TextView tvName;
    }
}
