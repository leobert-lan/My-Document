package com.lht.creationspace.adapter.locationpicker;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.lht.creationspace.R;
import com.lht.creationspace.base.model.pojo.ParentEntity;

import java.util.ArrayList;

public class ChildAdapter extends BaseExpandableListAdapter {

    private Context mContext;// 上下文

    private ArrayList<ParentEntity.ChildEntity> mChildren;// 数据源

    public ChildAdapter(Context context, ArrayList<ParentEntity.ChildEntity> chileren) {
        this.mContext = context;
        this.mChildren = chileren;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildren.get(groupPosition).getChildNames() != null ? mChildren
                .get(groupPosition).getChildNames().size() : 0;
    }

    @Override
    public String getChild(int groupPosition, int childPosition) {
        if (mChildren.get(groupPosition).getChildNames() != null
                && mChildren.get(groupPosition).getChildNames().size() > 0) {
            return mChildren.get(groupPosition).getChildNames()
                    .get(childPosition);
        }
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isExpanded, View convertView, ViewGroup parent) {
        ChildHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.child_child_item, null);
            holder = new ChildHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }
        holder.update(getChild(groupPosition, childPosition));
        return convertView;
    }

    /**
     * Holder优化
     */
    class ChildHolder {

        private TextView childChildTV;

        public ChildHolder(View v) {
            childChildTV = (TextView) v.findViewById(R.id.childChildTV);
        }

        public void update(String str) {
            childChildTV.setText(str);
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        if (mChildren != null && mChildren.size() > 0)
            return mChildren.get(groupPosition);
        return null;
    }

    @Override
    public int getGroupCount() {
        return mChildren != null ? mChildren.size() : 0;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        GroupHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.child_group_item, null);
            holder = new GroupHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (GroupHolder) convertView.getTag();
        }
        holder.update(mChildren.get(groupPosition));
        return convertView;
    }

    /**
     * @author Apathy、恒
     *         <p>
     *         Holder优化
     */
    class GroupHolder {

        private TextView childGroupTV;

        public GroupHolder(View v) {
            childGroupTV = (TextView) v.findViewById(R.id.childGroupTV);
        }

        public void update(ParentEntity.ChildEntity model) {
            childGroupTV.setText(model.getGroupName());
        }
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        /**
         * ==============================================
         * 此处必须返回true，否则无法响应子项的点击事件===============
         * ==============================================
         **/
        return true;
    }

}
