package com.lht.creationspace.customview.toolBar.navigation;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lht.creationspace.R;
import com.lht.creationspace.customview.toolBar.AbsNavigationToolBar;
import com.lht.creationspace.listener.ICallback;

/**
 * <p><b>Package</b> com.lht.creationspace.customview.toolBar.msg </p>
 * <p><b>Project</b> czspace </p>
 * <p><b>Classname</b> ToolbarTheme2 </p>
 * <p><b>Description</b>:样式2：back+text(title)+image </p>
 * <p>
 * Created by leobert on 2017/4/18.
 */
public class ToolbarTheme2 extends AbsNavigationToolBar {

    private View mToolbar;
    private ImageView ivBack;
    private TextView tvTitle;
    private View bottomDivider;
    private ImageView imgRightOp;

    public ToolbarTheme2(Context context) {
        super(context);
        init(context);
    }

    public ToolbarTheme2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ToolbarTheme2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mToolbar = View.inflate(context, R.layout.view_toolbar_theme2, this);
        ivBack = (ImageView) mToolbar.findViewById(R.id.iv_back);
        tvTitle = (TextView) mToolbar.findViewById(R.id.tv_title);
        bottomDivider = mToolbar.findViewById(R.id.bottom_divider);
        imgRightOp = (ImageView) mToolbar.findViewById(R.id.img_right_op);

        imgRightOp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iOperateCallback != null) {
                    iOperateCallback.onCallback();
                }
            }
        });
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

    private ICallback iOperateCallback;

    public void setiOperateCallback(ICallback iOperateCallback) {
        this.iOperateCallback = iOperateCallback;
    }

    public void hideIvBack() {
        ivBack.setVisibility(GONE);
    }

    public void setRightImageResource(int resImage) {
        imgRightOp.setImageResource(resImage);
    }

    public void setRightImageDrawable(Drawable drawable) {
        imgRightOp.setImageDrawable(drawable);
    }

    public void setBackground(@ColorRes int resColor) {
        mToolbar.setBackgroundColor(ContextCompat.getColor(getContext(), resColor));
    }

    public void enableRightOperate() {
        imgRightOp.setVisibility(VISIBLE);
    }

    public void disableRightOperate() {
        imgRightOp.setVisibility(INVISIBLE);
    }
}
