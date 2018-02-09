package com.lht.creationspace.adapter.locationpicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;

import com.lht.creationspace.R;
import com.lht.creationspace.base.model.pojo.ParentEntity;

import java.util.ArrayList;


public class ParentAdapter extends BaseExpandableListAdapter {

    private Context mContext;// 上下文

    private ArrayList<ParentEntity> mParents;// 数据源

    private OnChildTreeViewClickListener mTreeViewClickListener;// 点击子ExpandableListView子项的监听

    public ParentAdapter(Context context, ArrayList<ParentEntity> parents) {
        this.mContext = context;
        this.mParents = parents;
    }

    @Override
    public ParentEntity.ChildEntity getChild(int groupPosition, int childPosition) {
        return mParents.get(groupPosition).getChilds().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mParents.get(groupPosition).getChilds() != null ? mParents
                .get(groupPosition).getChilds().size() : 0;
    }

//    private ExpandableListView openedSubExpandListView = null;

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isExpanded, View convertView, ViewGroup parent) {

        final ExpandableListView subExpandListView = getExpandableListView();
        ArrayList<ParentEntity.ChildEntity> children = new ArrayList<>();
        final ParentEntity.ChildEntity child = getChild(groupPosition, childPosition);
        children.add(child);
        final ChildAdapter childAdapter = new ChildAdapter(this.mContext,
                children);
        subExpandListView.setAdapter(childAdapter);

        subExpandListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int lv2GroupPosition,// in this case always 0,only one group
                                        long id) {
                if (mParents.get(groupPosition).getChilds() == null ||
                        mParents.get(groupPosition).getChilds().isEmpty()) {
                    //this can't happen
                    if (mTreeViewClickListener != null) {
                        mTreeViewClickListener.onClickPosition(groupPosition, -1, -1);
                        return true;
                    }
                } else {
                    ParentEntity.ChildEntity entity = mParents.get(groupPosition).getChilds().get
                            (childPosition);
                    if (entity == null // couldn't
                            || entity.getChildNames() == null //
                            || entity.getChildNames().isEmpty()) {//
                        if (mTreeViewClickListener != null) {
                            mTreeViewClickListener.onClickPosition(groupPosition, childPosition,
                                    -1);
                            return true;
                        }
                    }
                }
                return false;
            }
        });
        /**
         *only callback on the 3rd-level item be clicked
         * */
        subExpandListView.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent,
                                        View v,//
                                        int groupIndex,//lvl 2 id
                                        int childIndex, //lvl 3 id
                                        long id) {

                if (mTreeViewClickListener != null) {
                    mTreeViewClickListener.onClickPosition(groupPosition,
                            childPosition, childIndex);
                }
                return false;
            }
        });


        /**
         * 2nd-level items on click to open the sub-ExpandableListView
         * 展开时，因为group只有一项，所以子ExpandableListView的总高度=
         * （sub-ExpandableListView的child数量 + 1 ）* 每一项的高度
         * */
        subExpandListView.setOnGroupExpandListener(new OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

//                if (subExpandListView != openedSubExpandListView && openedSubExpandListView !=
//                        null) {
//                    openedSubExpandListView.collapseGroup(0);
//                }
//
//                openedSubExpandListView = subExpandListView;

                LayoutParams lp = new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, (child
                        .getChildNames().size() + 1)
                        * (int) mContext.getResources().getDimension(
                        R.dimen.parent_expandable_list_height));
                subExpandListView.setLayoutParams(lp);
                subExpandListView.setSelectedGroup(groupPosition);
            }
        });

        /**
         *
         * 子ExpandableListView关闭时，此时只剩下group这一项，
         * 所以子ExpandableListView的总高度即为一项的高度
         * */
        subExpandListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {

                LayoutParams lp = new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, (int) mContext
                        .getResources().getDimension(
                                R.dimen.parent_expandable_list_height));
                subExpandListView.setLayoutParams(lp);
            }
        });
        return subExpandListView;

    }

    /**
     * 动态创建子ExpandableListView
     */
    public ExpandableListView getExpandableListView() {
        ExpandableListView mExpandableListView = new ExpandableListView(
                mContext);
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, (int) mContext
                .getResources().getDimension(
                        R.dimen.parent_expandable_list_height));
        mExpandableListView.setLayoutParams(lp);
        mExpandableListView.setDividerHeight(0);// 取消group项的分割线
        mExpandableListView.setChildDivider(null);// 取消child项的分割线
        mExpandableListView.setGroupIndicator(null);// 取消展开折叠的指示图标
        return mExpandableListView;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mParents.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mParents != null ? mParents.size() : 0;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        GroupHolder holder;
        if (convertView != null && convertView.getTag() instanceof GroupHolder) {
            holder = (GroupHolder) convertView.getTag();

        } else {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.parent_group_item, null);
            holder = new GroupHolder(convertView);
            convertView.setTag(holder);
        }
        holder.update(mParents.get(groupPosition));
        return convertView;
    }

    /**
     * Holder优化
     */
    class GroupHolder {

        private TextView parentGroupTV;

        public GroupHolder(View v) {
            parentGroupTV = (TextView) v.findViewById(R.id.parentGroupTV);
        }

        public void update(ParentEntity model) {
            if (model == null) {
                return;
            }
            parentGroupTV.setText(model.getGroupName());
        }
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    /**
     * 设置点击子ExpandableListView子项的监听
     */
    public void setOnChildTreeViewClickListener(OnChildTreeViewClickListener
                                                        treeViewClickListener) {
        this.mTreeViewClickListener = treeViewClickListener;
    }

    /**
     * 点击子ExpandableListView子项的回调接口
     */
    public interface OnChildTreeViewClickListener {
        void onClickPosition(int parentPosition, int groupPosition, int childPosition);
    }

}
