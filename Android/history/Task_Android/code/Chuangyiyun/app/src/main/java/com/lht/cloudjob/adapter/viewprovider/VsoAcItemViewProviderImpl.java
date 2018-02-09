package com.lht.cloudjob.adapter.viewprovider;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.BaseActivity;
import com.lht.cloudjob.adapter.ListAdapter2;
import com.lht.cloudjob.customview.RoundImageView;
import com.lht.cloudjob.interfaces.adapter.IListItemViewProvider;
import com.lht.cloudjob.mvp.model.bean.VsoActivitiesResBean;
import com.lht.cloudjob.util.time.TimeUtil;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

/**
 * <p><b>Package</b> com.lht.cloudjob.adapter.viewprovider
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> VsoAcItemViewProviderImpl
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/8/19
 */
public class VsoAcItemViewProviderImpl implements IListItemViewProvider<VsoActivitiesResBean> {

    private final LayoutInflater mInflater;

    private final ListAdapter2.ICustomizeListItem2<VsoActivitiesResBean, ViewHolder>
            iSetCallbackForListItem;

    private SimpleDateFormat format;

    public VsoAcItemViewProviderImpl(LayoutInflater inflater,
                                     ListAdapter2.ICustomizeListItem2<VsoActivitiesResBean, ViewHolder> iSetCallbackForListItem) {

        this.mInflater = inflater;
        this.iSetCallbackForListItem = iSetCallbackForListItem;
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
    public View getView(int position, VsoActivitiesResBean item, View convertView, ViewGroup
            parent) {
        ViewHolder holder;
        if (convertView != null && convertView.getTag() != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_list_vsoactivities, null);
            holder.imgAvatar = (RoundImageView) convertView.findViewById(R.id.vsoac_item_img);
            holder.unreadTag = (ImageView) convertView.findViewById(R.id.vsoac_item_unreadtag);
            holder.rlRoot = (RelativeLayout) convertView.findViewById(R.id.vsoac_item_rl_root);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.vsoac_item_tv_title);
            holder.tvTime = (TextView) convertView.findViewById(R.id.vsoac_item_tv_time);
            holder.tvContent = (TextView) convertView.findViewById(R.id.vsoac_item_tv_content);
            holder.line = convertView.findViewById(R.id.line);
            holder.tvSeeDetail = (TextView) convertView.findViewById(R.id.vsoac_item_seedetail);
            convertView.setTag(holder);
        }

        holder.tvTitle.setText(item.getTitle());
        holder.tvContent.setText(Html.fromHtml(item.getContent()).toString());
        holder.tvTime.setText(TimeUtil.getTime(item.getSend_time(), format));

        if (iSetCallbackForListItem != null)
            iSetCallbackForListItem.customize(position, item, convertView, holder);

        //头像处理
        Picasso.with(parent.getContext()).load(R.drawable.v1030_drawable_avater_vsosys).diskCache(BaseActivity
                .getLocalImageCache()).placeholder(R.drawable.v1010_drawable_avatar_default)
                .error(R.drawable.v1010_drawable_avatar_default).fit().into(holder.imgAvatar);

        return convertView;
    }


    public class ViewHolder {
        public RelativeLayout rlRoot;
        ImageView unreadTag;
        TextView tvTitle;
        TextView tvTime;
        TextView tvContent;
        public View line;
        RoundImageView imgAvatar;
        public TextView tvSeeDetail;
    }
}
