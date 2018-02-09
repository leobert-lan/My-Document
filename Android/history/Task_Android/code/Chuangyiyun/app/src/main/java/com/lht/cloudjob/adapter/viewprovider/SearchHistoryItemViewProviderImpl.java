package com.lht.cloudjob.adapter.viewprovider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lht.cloudjob.R;
import com.lht.cloudjob.interfaces.adapter.IListItemViewProvider;
import com.lht.cloudjob.interfaces.adapter.ICustomizeListItem;

/**
 * <p><b>Package</b> com.lht.cloudjob.adapter.viewprovider
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> SearchHistoryItemViewProviderImpl
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/8/2.
 */
public class SearchHistoryItemViewProviderImpl implements IListItemViewProvider<String> {

    private final LayoutInflater mInflater;

    private final ICustomizeListItem<ViewHolder> iCustomizeListItem;

    public SearchHistoryItemViewProviderImpl(
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
            convertView = mInflater.inflate(R.layout.item_list_searchhistory, null);
            //TODO bind holder

            holder.tvSearchKey = (TextView) convertView.findViewById(R.id.shlist_item_tv_key);
            convertView.setTag(holder);
        }
        holder.tvSearchKey.setText(item);
        if (iCustomizeListItem != null)
            iCustomizeListItem.customize(position,convertView, holder);
        return convertView;
    }

    public class ViewHolder {
        TextView tvSearchKey;

        public TextView getTvSearchKey() {
            return tvSearchKey;
        }
    }
}
