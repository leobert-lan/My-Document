package com.lht.cloudjob.adapter.viewprovider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lht.cloudjob.MainApplication;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.BaseActivity;
import com.lht.cloudjob.interfaces.adapter.IListItemViewProvider;
import com.lht.cloudjob.interfaces.adapter.ICustomizeListItem;
import com.lht.cloudjob.mvp.model.bean.FavorListItemResBean;
import com.squareup.picasso.Picasso;

/**
 * <p><b>Package</b> com.lht.cloudjob.adapter.viewprovider
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> AttentionItemViewProviderImpl
 * <p><b>Description</b>: 我的关注列表视图delegate
 * Created by leobert on 2016/8/11.
 */
public class AttentionItemViewProviderImpl implements IListItemViewProvider<FavorListItemResBean> {

    private final LayoutInflater mInflater;

    private final ICustomizeListItem<ViewHolder> iCustomizeListItem;

    public AttentionItemViewProviderImpl(
            LayoutInflater inflater,
            ICustomizeListItem<ViewHolder> iCustomizeListItem) {

        this.mInflater = inflater;
        this.iCustomizeListItem = iCustomizeListItem;
    }

    @Override
    public View getView(int position, FavorListItemResBean item, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null && convertView.getTag() != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_list_attention_v1010, null);

            holder.avatar = (ImageView) convertView.findViewById(R.id.attentionlist_item_img_avatar);
//            holder.madel = (ImageView) convertView.findViewById(R.id.attentionlist_item_img_madel);
            holder.name = (TextView) convertView.findViewById(R.id.attentionlist_item_tv_name);
            holder.summary = (TextView) convertView.findViewById(R.id.attentionlist_item_tv_summary);
            holder.cancelAttention = (Button) convertView.findViewById(R.id.attentionlist_item_btn_cancel);
            convertView.setTag(holder);
        }
        holder.name.setText(item.getNickname());
        holder.summary.setText(item.getIndus_name());

        Picasso.with(MainApplication.getOurInstance()).load(item.getAvatar())
                .diskCache(BaseActivity.getLocalImageCache())
                .placeholder(R.drawable.v1010_drawable_avatar_default)
                .error(R.drawable.v1010_drawable_avatar_default)
                .fit()
                .into(holder.avatar);

        if (iCustomizeListItem != null)
            iCustomizeListItem.customize(position, convertView, holder);
        return convertView;
    }

    public class ViewHolder {
        public ImageView avatar;
        public ImageView madel;
        public TextView name;
        public TextView summary;
        public Button cancelAttention;
    }
}
