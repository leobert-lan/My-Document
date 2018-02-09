package com.lht.creationspace.customview.toolBar.navigation;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lht.creationspace.R;
import com.lht.creationspace.customview.toolBar.AbsNavigationToolBar;

/**
 * <p><b>Package</b> com.lht.creationspace.customview.toolBar.msg </p>
 * <p><b>Project</b> czspace </p>
 * <p><b>Classname</b> ToolbarTheme1 </p>
 * <p><b>Description</b>:样式1：back+text(title) </p>
 * <p>
 * Created by leobert on 2017/4/18.
 */
public class ToolbarTheme1 extends AbsNavigationToolBar {

    private View mToolbar;
    private ImageView ivBack;
    private TextView tvTitle;
    private View bottomDivider;

    public ToolbarTheme1(Context context) {
        super(context);
        init(context);
    }

    public ToolbarTheme1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ToolbarTheme1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mToolbar = View.inflate(context, R.layout.view_toolbar_theme1, this);
        ivBack = (ImageView) mToolbar.findViewById(R.id.iv_back);
        tvTitle = (TextView) mToolbar.findViewById(R.id.tv_title);
        bottomDivider = mToolbar.findViewById(R.id.bottom_divider);
    }

    @Override
    protected TextView getTitleTextView() {
        return tvTitle;
    }

    @Override
    protected ImageView getBackImageView() {
        return ivBack;
    }

    @Override
    protected View getDividerImageView() {
        return bottomDivider;
    }
}
