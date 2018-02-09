package com.lht.cloudjob.adapter.viewprovider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lht.cloudjob.R;
import com.lht.cloudjob.adapter.ListAdapter2;
import com.lht.cloudjob.customview.ConflictGridView;
import com.lht.cloudjob.interfaces.adapter.IListItemViewProvider;
import com.lht.cloudjob.mvp.model.pojo.IndustryWrapper;
import com.lht.cloudjob.util.debug.DLog;

/**
 * <p><b>Package</b> com.lht.cloudjob.adapter.viewprovider
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> SubscribeGroupViewProviderImpl
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/9/19
 */
public class SubscribeGroupViewProviderImpl implements IListItemViewProvider<IndustryWrapper> {


    private final LayoutInflater mInflater;

    private final ListAdapter2.ICustomizeListItem2<IndustryWrapper, ViewHolder>
            iSetCallbackForListItem;

    public SubscribeGroupViewProviderImpl(
            LayoutInflater inflater,
            ListAdapter2.ICustomizeListItem2<IndustryWrapper, ViewHolder>
                    iSetCallbackForListItem) {

        this.mInflater = inflater;
        this.iSetCallbackForListItem = iSetCallbackForListItem;
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
    public View getView(int position, IndustryWrapper item, View convertView, ViewGroup
            parent) {
        DLog.e(getClass(), "getview" + position);
        ViewHolder holder;
        if (convertView != null && convertView.getTag() != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_list_subscribe, null);
            holder.tvField = (TextView) convertView.findViewById(R.id.subscribe_listgroup_tv_field);
            holder.gvLabels = (ConflictGridView) convertView.findViewById(R.id
                    .subscribe_listgroup_gv_labels);

            convertView.setTag(holder);
        }
        holder.tvField.setText(item.getField().getName());
        if (iSetCallbackForListItem != null)
            iSetCallbackForListItem.customize(position, item, convertView, holder);
        return convertView;
    }

    public class ViewHolder {
        public TextView tvField;
        public ConflictGridView gvLabels;
    }
}