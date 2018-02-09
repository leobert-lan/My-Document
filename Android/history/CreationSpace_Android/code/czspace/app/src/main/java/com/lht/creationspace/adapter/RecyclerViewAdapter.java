package com.lht.creationspace.adapter;

import android.content.Context;
import android.support.v4.util.CircularArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lht.creationspace.R;
import com.lht.creationspace.module.proj.model.pojo.ProjectTypeResBean;

/**
 * Created by chhyu on 2017/2/20.
 */

public class RecyclerViewAdapter extends BaseLoadingAdapter {
    private Context context;
    private OnItemClickListener listener;

    public RecyclerViewAdapter(Context context, OnItemClickListener l, RecyclerView recyclerView, CircularArray<ProjectTypeResBean> ts) {
        super(recyclerView, ts);
        this.context = context;
        this.listener = l;
    }

    @Override
    public RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project_type, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindNormalViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final MyViewHolder holder = (MyViewHolder) viewHolder;
        final ProjectTypeResBean bean = (ProjectTypeResBean) mDataSet.get(position);
        holder.tvTypeName.setText(bean.getName());
        holder.tvTypeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(holder.tvTypeName, position, bean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet == null ? 0 : mDataSet.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTypeName;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTypeName = (TextView) itemView.findViewById(R.id.tv_type_name);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int postion, ProjectTypeResBean bean);
    }
}
