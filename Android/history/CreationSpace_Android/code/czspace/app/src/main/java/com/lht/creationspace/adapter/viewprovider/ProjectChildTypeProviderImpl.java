package com.lht.creationspace.adapter.viewprovider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lht.creationspace.R;
import com.lht.creationspace.adapter.AbsListAdapter;
import com.lht.creationspace.adapter.interfaces.IListItemViewProvider;
import com.lht.creationspace.module.proj.model.pojo.ProjectTypeResBean;

/**
 * Created by chhyu on 2017/3/7.
 */

public class ProjectChildTypeProviderImpl implements IListItemViewProvider<ProjectTypeResBean.ProjectChildTypeResBean> {
    private final LayoutInflater mInflater;
    private final AbsListAdapter.ICustomizeListItem<ProjectTypeResBean.ProjectChildTypeResBean, ViewHolder> iSetCallbackForListItem;

    public ProjectChildTypeProviderImpl(LayoutInflater inflater,
                                        AbsListAdapter.ICustomizeListItem<ProjectTypeResBean.ProjectChildTypeResBean,
                                                ViewHolder> iSetCallbackForListItem) {
        this.mInflater = inflater;
        this.iSetCallbackForListItem = iSetCallbackForListItem;
    }

    @Override
    public View getView(int position, ProjectTypeResBean.ProjectChildTypeResBean item,
                        View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null && convertView.getTag() != null) {
            holder = (ViewHolder) convertView.getTag();

        } else {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_project_type, parent,false);
            holder.tvChildTypeName = (TextView) convertView.findViewById(R.id.tv_type_name);
            convertView.setTag(holder);
        }
        holder.tvChildTypeName.setText(item.getName());
        return convertView;
    }

    public class ViewHolder {
        private TextView tvChildTypeName;
    }
}
