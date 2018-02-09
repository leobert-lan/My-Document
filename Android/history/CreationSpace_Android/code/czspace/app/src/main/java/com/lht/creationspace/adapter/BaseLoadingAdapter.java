package com.lht.creationspace.adapter;

import android.support.v4.util.CircularArray;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;

import java.util.ArrayList;

public abstract class BaseLoadingAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "BaseLoadingAdapter";

    //是否正在加载

    private boolean mIsLoading = false;

    //正常条目
    private static final int TYPE_NORMAL_ITEM = 0;
//    //加载条目
//    private static final int TYPE_LOADING_ITEM = 1;

    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    //数据集

    protected CircularArray<T> mDataSet;


    public BaseLoadingAdapter(RecyclerView recyclerView, CircularArray<T> ts) {
        mDataSet = ts;

        setSpanCount(recyclerView);

        setScrollListener(recyclerView);

    }

    public void addDatas(ArrayList<T> _datas) {
        if (mDataSet == null)
            mDataSet = new CircularArray<>();
        if (_datas == null)
            return;
        for (T bean : _datas) {
            mDataSet.addLast(bean);
        }
        notifyDataSetChanged();
    }

    private OnLoadingListener mOnLoadingListener;

    /**
     * 加载更多接口
     */
    public interface OnLoadingListener {
        void loading();
    }

    /**
     * 设置监听接口
     *
     * @param onLoadingListener onLoadingListener
     */
    public void setOnLoadingListener(OnLoadingListener onLoadingListener) {
        mOnLoadingListener = onLoadingListener;

    }

    /**
     * 加载完成
     */
    public void setLoadingComplete() {
        mIsLoading = false;
//        if (mDataSet.size() > 0) { // 防止数组越界问题
//            mDataSet.removeFromEnd(1);
//            notifyItemRemoved(mDataSet.size() - 1);
//        }

    }


    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll up. Override this if the child view is a custom view.
     */
    private boolean canScrollDown(RecyclerView recyclerView) {
        return ViewCompat.canScrollVertically(recyclerView, 1);

    }

    /**
     * 设置每个条目占用的列数
     *
     * @param recyclerView recycleView
     */
    private void setSpanCount(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = getItemViewType(position);
                    if (type == TYPE_NORMAL_ITEM) {
                        return 1;

                    } else {
                        return gridLayoutManager.getSpanCount();

                    }
                }
            });
        }
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            mStaggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
        }
    }

    /**
     * 监听滚动事件
     *
     * @param recyclerView recycleView
     */
    private void setScrollListener(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!canScrollDown(recyclerView)) {
                    if (!mIsLoading) {
                        mIsLoading = true;
//                        mDataSet.addLast(null);
//                        notifyItemInserted(mDataSet.size() - 1);
                        if (mOnLoadingListener != null) {
                            mOnLoadingListener.loading();

                        }
                    }
                }
            }
        });
    }

    /**
     * 创建viewHolder
     *
     * @param parent viewGroup
     * @return viewHolder
     */
    public abstract RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup parent);

    /**
     * 绑定viewHolder
     *
     * @param holder   viewHolder
     * @param position position
     */
    public abstract void onBindNormalViewHolder(RecyclerView.ViewHolder holder, int position);


//    /**
//     * 加载布局
//     */
//    private class LoadingViewHolder extends RecyclerView.ViewHolder {
//
//        public ProgressBar progressBar;
//
//        public TextView tvLoading;
//
//        public LinearLayout llyLoading;
//
//        public LoadingViewHolder(View view) {
//            super(view);
//
//            progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
//            tvLoading = (TextView) view.findViewById(R.id.tv_loading);
//            llyLoading = (LinearLayout) view.findViewById(R.id.lly_loading);
//
//        }
//    }

    @Override
    public int getItemViewType(int position) {
//        T t = mDataSet.get(position);
        return TYPE_NORMAL_ITEM;
//        if (t == null) {
//            return TYPE_LOADING_ITEM;
//
//        } else {
//            return TYPE_NORMAL_ITEM;
//
//        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (viewType == TYPE_NORMAL_ITEM) {
        return onCreateNormalViewHolder(parent);
//        } else {
//            View view = LayoutInflater.from(parent.getContext()).inflate(
//                    R.layout.loading_view, parent, false);
//            mLoadingViewHolder = new LoadingViewHolder(view);
//            return mLoadingViewHolder;
//
//        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        int type = getItemViewType(position);
//        if (type == TYPE_NORMAL_ITEM) {
        onBindNormalViewHolder(holder, position);

//        } else {
//            if (mStaggeredGridLayoutManager != null) {
//                StaggeredGridLayoutManager.LayoutParams layoutParams =
//                        new StaggeredGridLayoutManager.LayoutParams(
//                                ViewGroup.LayoutParams.MATCH_PARENT,
//                                ViewGroup.LayoutParams.WRAP_CONTENT);
//                layoutParams.setFullSpan(true);
//                mLoadingViewHolder.llyLoading.setLayoutParams(layoutParams);
//            }
//        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();

    }
}