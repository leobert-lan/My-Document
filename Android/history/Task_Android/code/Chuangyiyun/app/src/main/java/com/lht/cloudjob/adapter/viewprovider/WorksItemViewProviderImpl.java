package com.lht.cloudjob.adapter.viewprovider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lht.cloudjob.R;
import com.lht.cloudjob.customview.WorkItemView;
import com.lht.cloudjob.interfaces.adapter.IListItemViewProvider;
import com.lht.cloudjob.interfaces.adapter.ICustomizeListItem;
import com.lht.cloudjob.mvp.model.bean.DemandInfoResBean;

/**
 * <p><b>Package</b> com.lht.cloudjob.adapter.viewprovider
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> WorksItemViewProviderImpl
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/8/26
 */
public class WorksItemViewProviderImpl implements IListItemViewProvider<DemandInfoResBean.Work> {


    private final LayoutInflater mInflater;

    private final ICustomizeListItem<ViewHolder> iCustomizeListItem;

    public WorksItemViewProviderImpl(
            LayoutInflater inflater,
            ICustomizeListItem<ViewHolder> iCustomizeListItem) {

        this.mInflater = inflater;
        this.iCustomizeListItem = iCustomizeListItem;
    }

    private WorkItemView.Type type = WorkItemView.Type.unset;

    public void setType(WorkItemView.Type type) {
        this.type = type;
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
    public View getView(int position, DemandInfoResBean.Work item, View convertView, ViewGroup
            parent) {
        ViewHolder holder;
        if (convertView != null && convertView.getTag() != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_list_works, null);
            holder.workItemView = (WorkItemView) convertView.findViewById(R.id.workslist_item_work);
            convertView.setTag(holder);
        }
        holder.workItemView.setType(type);
        holder.workItemView.setWorkData(item);
        if (iCustomizeListItem != null)
            iCustomizeListItem.customize(position, convertView, holder);
        return convertView;
    }

    public class ViewHolder {
        public WorkItemView workItemView;

    }
}
