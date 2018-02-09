package com.lht.creationspace.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lht.creationspace.R;
import com.yalantis.ucrop.entity.LocalMedia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * <p><b>Package:</b> com.lht.creationspace.adapter </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> GridImageAdapter </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/3/6.
 */
public class GridImageAdapter extends
        RecyclerView.Adapter<GridImageAdapter.ViewHolder> {
    public static final int TYPE_ADD = 1;
    public static final int TYPE_PICTURE = 2;
    private LayoutInflater mInflater;
    private Context mContext;
    private List<LocalMedia> list = new ArrayList<>();
    private int selectMax = 9;
    /**
     * 点击添加图片跳转
     */
    private onPicClickListener mOnPicClickListener;

    public interface onPicClickListener {
        void onAddPicClick(int type, int position);

        void onDelete(int position);
    }

    public GridImageAdapter(Context context, onPicClickListener mOnPicClickListener) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mOnPicClickListener = mOnPicClickListener;
    }

    public void setSelectMax(int selectMax) {
        this.selectMax = selectMax;
    }

    public void setList(List<LocalMedia> list) {
        this.list = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mImage;
        ImageButton mDelete;

        public ViewHolder(View view) {
            super(view);
            mImage = (ImageView) view.findViewById(R.id.image_content);
            mDelete = (ImageButton) view.findViewById(R.id.image_delete);
        }
    }

    @Override
    public int getItemCount() {
        if (list.size() < selectMax) {
            return list.size() + 1;
        } else {
            return list.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowAddItem(position)) {
            return TYPE_ADD;
        } else {
            return TYPE_PICTURE;
        }
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(getViewHolderLayoutResource(),
                viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        //itemView 的点击事件
        if (mItemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClick(viewHolder.getAdapterPosition(), v);
                }
            });
        }
        return viewHolder;
    }

    protected int getViewHolderLayoutResource() {
        return R.layout.view_image_with_delete_75;
    }

    private boolean isShowAddItem(int position) {
        int size = list.size() == 0 ? 0 : list.size();
        return position == size;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        //少于8张，显示继续添加的图标
        if (getItemViewType(position) == TYPE_ADD) {
            viewHolder.mImage.setImageResource(R.drawable.v1000_drawable_tuptj);
            viewHolder.mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnPicClickListener.onAddPicClick(TYPE_ADD, viewHolder.getAdapterPosition());
                }
            });
            viewHolder.mDelete.setVisibility(View.GONE);
        } else {
            viewHolder.mDelete.setVisibility(View.VISIBLE);
            viewHolder.mDelete.bringToFront();
            viewHolder.mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnPicClickListener.onDelete(viewHolder.getAdapterPosition());
                }
            });
            LocalMedia media = list.get(position);
            int type = media.getType();
            String path;
            if (media.isCompressed())
                path = media.getCompressPath();
            else
                path = media.getPath();
            switch (type) {
                case 1:
                    // 图片
                    if (media.isCompressed()) {
                        Log.i("compress image result", new File(media.getCompressPath()).length() / 1024 + "k");
                    }

                    Glide.with(mContext)
                            .load(path)
                            .asBitmap().centerCrop()
                            .placeholder(R.color.color_f6)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(viewHolder.mImage);
                    break;
                case 2:
                    // 视频
                    Glide.with(mContext).load(path).thumbnail(0.5f).into(viewHolder.mImage);
                    break;
            }

        }
    }

    protected OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }
}