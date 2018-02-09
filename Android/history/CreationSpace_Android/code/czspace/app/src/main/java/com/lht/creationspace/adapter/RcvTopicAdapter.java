package com.lht.creationspace.adapter;

import android.content.Context;
import android.support.v4.util.CircularArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lht.creationspace.R;
import com.lht.creationspace.module.topic.model.pojo.TopicResBean;

/**
 * Created by chhyu on 2017/3/3.
 */

public class RcvTopicAdapter extends BaseLoadingAdapter<TopicResBean.TopicDetailInfoResBean> {
    private Context context;
    private OnItemClickListener listener;

    public RcvTopicAdapter(Context context, OnItemClickListener l, RecyclerView recyclerView, CircularArray<TopicResBean.TopicDetailInfoResBean> ts) {
        super(recyclerView, ts);
        this.context = context;
        this.listener = l;
    }

    @Override
    public RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_topic_type, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindNormalViewHolder(RecyclerView.ViewHolder viewHolder,
                                       final int position) {
        final MyViewHolder holder = (MyViewHolder) viewHolder;
        final TopicResBean.TopicDetailInfoResBean bean = mDataSet.get(position);
        Glide.with(context).load(bean.getCircle_icon())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.icon_launcher)
                .placeholder(R.drawable.icon_launcher)
                .into(holder.ivTypeImage);
        holder.tvTypeName.setText(bean.getCircle_name());
//        //TODO: 2017/3/7 接口信息没有返回人气数量
//        holder.tv_attention_num.setText("人气" + "123456");
        if (position == selectedIndex) {
            holder.ivSelected.setVisibility(View.VISIBLE);
            holder.ivSelected.bringToFront();
        } else
            holder.ivSelected.setVisibility(View.INVISIBLE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(holder.tvTypeName, position, bean);
                }
            }
        });
    }

    private int selectedIndex = -1;

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    @Override
    public int getItemCount() {
        return mDataSet == null ? 0 : mDataSet.size();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivTypeImage;
        TextView tvTypeName;
//        TextView tv_attention_num;
        ImageView ivSelected;

        MyViewHolder(View itemView) {
            super(itemView);
            ivTypeImage = (ImageView) itemView.findViewById(R.id.iv_type_image);
            tvTypeName = (TextView) itemView.findViewById(R.id.tv_type_name);
//            tv_attention_num = (TextView) itemView.findViewById(R.id.tv_attention_num);
            ivSelected = (ImageView) itemView.findViewById(R.id.iv_select);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position,
                         TopicResBean.TopicDetailInfoResBean bean);
    }
}
