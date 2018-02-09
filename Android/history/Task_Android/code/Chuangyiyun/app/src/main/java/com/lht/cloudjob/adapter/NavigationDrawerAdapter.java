package com.lht.cloudjob.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lht.cloudjob.R;
import com.lht.cloudjob.interfaces.bars.OnNavigationDrawerItemSelectedListener;
import com.lht.cloudjob.mvp.model.pojo.NavigationItem;
import com.lht.cloudjob.util.SideBarNavigationItemDatas;

import java.util.List;


public class NavigationDrawerAdapter extends
        RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder> {

    private List<NavigationItem> mData;
    private OnNavigationDrawerItemSelectedListener itemSelectedListener;
    //    private int mSelectedPosition;
    private int mTouchedPosition = -1;

    public NavigationDrawerAdapter(List<NavigationItem> data) {
        mData = data;
    }

    public OnNavigationDrawerItemSelectedListener getItemSelectedListener() {
        return itemSelectedListener;
    }

    public void setItemSelectedListener(OnNavigationDrawerItemSelectedListener itemSelectedListener) {
        this.itemSelectedListener = itemSelectedListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(
            ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.drawer_row, viewGroup, false);
        final ViewHolder viewholder = new ViewHolder(v);
        viewholder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchPosition(viewholder.getPosition());
                        return false;
                    case MotionEvent.ACTION_CANCEL:
                        touchPosition(-1);
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        return false;
                    case MotionEvent.ACTION_UP:
                        touchPosition(-1);
                        return false;
                }
                return true;
            }
        });
        viewholder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemSelectedListener != null)
                    //不需要处理返回值,不需要传递登录类型
                    itemSelectedListener
                            .onNavigationDrawerItemSelected(null, viewholder
                                    .getPosition());
            }
        });


        return viewholder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder,
                                 final int i) {
        NavigationItem item = mData.get(i);
        if (item.getStyle() == SideBarNavigationItemDatas.Style.DEFAULT) {
            viewHolder.view.setVisibility(View.GONE);
            viewHolder.name.setText(mData.get(i).getText());
            viewHolder.sub.setText(mData.get(i).getSub());
            viewHolder.name.setCompoundDrawablesWithIntrinsicBounds(mData
                    .get(i).getDrawable(), null, null, null);
            viewHolder.itemView.setEnabled(true);
            viewHolder.name.setEnabled(true);
            viewHolder.sub.setEnabled(true);
        } else if (item.getStyle() == SideBarNavigationItemDatas.Style.NO_ICON) {
            viewHolder.view.setVisibility(View.GONE);
            viewHolder.name.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            viewHolder.name.setText(mData.get(i).getText());
            viewHolder.sub.setText(mData.get(i).getSub());
            viewHolder.itemView.setEnabled(true);
            viewHolder.name.setEnabled(true);
            viewHolder.sub.setEnabled(true);
        } else if (item.getStyle() == SideBarNavigationItemDatas.Style.HASLINE) {
            viewHolder.view.setVisibility(View.VISIBLE);
            viewHolder.name.setText(mData.get(i).getText());
            viewHolder.name.setCompoundDrawablesWithIntrinsicBounds(mData
                    .get(i).getDrawable(), null, null, null);
            viewHolder.sub.setText(mData.get(i).getSub());
            viewHolder.itemView.setEnabled(true);
            viewHolder.name.setEnabled(true);
            viewHolder.sub.setEnabled(true);
        } else {
            //disable
            viewHolder.view.setVisibility(View.GONE);
            viewHolder.name.setText(mData.get(i).getText());
            viewHolder.sub.setText(mData.get(i).getSub());
            viewHolder.name.setCompoundDrawablesWithIntrinsicBounds(mData
                    .get(i).getDrawable(), null, null, null);
            viewHolder.itemView.setEnabled(false);
            viewHolder.name.setEnabled(false);
            viewHolder.sub.setEnabled(false);
        }


        // TODO: selected menu position, change layout accordingly
        viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
//        if (mSelectedPosition == i || mTouchedPosition == i) {
//            viewHolder.itemView.setBackgroundColor(viewHolder.itemView
//                    .getContext().getResources()
//                    .getColor(R.color.selected_gray));
//        } else {
//            viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
//        }
    }

    private void touchPosition(int position) {
        int lastPosition = mTouchedPosition;
        mTouchedPosition = position;
        if (lastPosition >= 0)
            notifyItemChanged(lastPosition);
        if (position >= 0)
            notifyItemChanged(position);
    }

    public void selectPosition(int position) {
//        int lastPosition = mSelectedPosition;
//        mSelectedPosition = position;
//        notifyItemChanged(lastPosition);
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView sub;
        public View view;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.item_name);
            sub = (TextView) itemView.findViewById(R.id.item_sub);
            view = itemView.findViewById(R.id.view);
        }

    }
}
