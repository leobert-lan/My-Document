package com.lht.creationspace.customview.toolBar;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Created by chhyu on 2017/4/5.
 * titlebar的父类，包含返回键和标题
 */

public abstract class AbsNavigationToolBar extends AbsLhtToolBar {

    public AbsNavigationToolBar(Context context) {
        super(context);
        setContentInsetsAbsolute(0, 0);
    }

    public AbsNavigationToolBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setContentInsetsAbsolute(0, 0);
    }

    public AbsNavigationToolBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setContentInsetsAbsolute(0, 0);
    }

    /**
     * 获取 title控件
     *
     * @return
     */
    protected abstract TextView getTitleTextView();

    /**
     * 获取 back控件
     *
     * @return
     */
    protected abstract ImageView getBackImageView();

    /**
     * 获取分隔线
     *
     * @return
     */
    protected abstract View getDividerImageView();

    /**
     * 设置title
     *
     * @param s
     */
    public void setTitle(String s) {
        if (getTitleTextView() == null) {
            //
            return;
        }
        getTitleTextView().setText(s);
    }

    /**
     * 设置title
     *
     * @param res
     */
    public void setTitle(int res) {
        if (getTitleTextView() == null) {
            //
            return;
        }
        getTitleTextView().setText(res);
    }

    /**
     * 设置标题颜色
     *
     * @param color
     */
    public void setTitleTextColor(@ColorRes int color) {
        if (getTitleTextView() == null) {
            //
            return;
        }
        getTitleTextView().setTextColor(ContextCompat.getColor(getContext(),color));
    }

    /**
     * 设置标题字体大小
     *
     * @param size
     */
    public void setTitleTextSize(int size) {
        if (getTitleTextView() == null) {
            //
            return;
        }
        getTitleTextView().setTextSize(size);
    }

    /**
     * 隐藏标题与标题下面内容的分隔线
     */
    public void hideTitleBottomDividerLine() {
        if(getDividerImageView()==null){
            return;
        }
        getDividerImageView().setVisibility(GONE);
    }

    /**
     * 返回
     *
     * @param activity
     */
    public void setDefaultOnBackListener(Activity activity) {
        setOnBackListener(new DefaultOnBackClickListener(activity));
    }

    public void setOnBackListener(final ITitleBackListener listener) {
        if (getBackImageView() == null) {
            return;
        }
        getBackImageView().setVisibility(View.VISIBLE);
        if (listener != null) {
            getBackImageView().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onTitleBackClick();
                }
            });
        }
    }

    public interface ITitleBackListener {
        void onTitleBackClick();
    }

    public static class DefaultOnBackClickListener implements ITitleBackListener {
        private WeakReference<Activity> mActivity;

        public DefaultOnBackClickListener(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void onTitleBackClick() {
            if (mActivity.get() != null) {
                mActivity.get().finish();
            }
        }
    }
}
