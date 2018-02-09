package com.lht.creationspace.customview.toolBar.msg;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lht.creationspace.R;
import com.lht.creationspace.customview.toolBar.AbsMsgToolBar;
import com.lht.creationspace.listener.ICallback;

import individual.leobert.uilib.numbadge.NumBadge;


/**
 * <p><b>Package</b> com.lht.creationspace.customview.toolBar.msg </p>
 * <p><b>Project</b> czspace </p>
 * <p><b>Classname</b> ToolbarTheme4 </p>
 * <p><b>Description</b>:样式四："message"+text(title)+image </p>
 * <p>
 * Created by leobert on 2017/4/18.
 */
public class ToolbarTheme4 extends AbsMsgToolBar {

    private View mToolbar;
    private TextView tvTitle;
    private View bottomDivider;
    private FrameLayout mMessage;
    private NumBadge mMessageHint;
    protected ImageView mMore;

    public ToolbarTheme4(Context context) {
        super(context);
        init(context);
    }

    public ToolbarTheme4(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ToolbarTheme4(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setContentInsetsAbsolute(0, 0);
        mToolbar = View.inflate(context, R.layout.view_toolbar_theme4, this);

        tvTitle = (TextView) mToolbar.findViewById(R.id.tv_title);
        mMore = (ImageView) mToolbar.findViewById(R.id.nav_more);
        bottomDivider = mToolbar.findViewById(R.id.bottom_divider);

        mMessage = (FrameLayout) mToolbar.findViewById(R.id.nav_msg);
        mMessageHint = (NumBadge) mToolbar.findViewById(R.id.nav_msg_hint);


        mMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onMoreClickListener) {
                    onMoreClickListener.onCallback();
                }
            }
        });
    }

    public void setTitle(String text) {
        tvTitle.setText(text);
    }

    public void setRightImageDrawable(int resDrawable) {
        mMore.setImageResource(resDrawable);
    }

    /**
     * 隐藏标题与标题下面内容的分隔线
     */
    public void hideTitleBottomDividerLine() {
        bottomDivider.setVisibility(GONE);
    }

    /**
     * 设置富文本风格的title
     *
     * @param charSequence title
     */
    public void setTitle(CharSequence charSequence) {
        tvTitle.setText(charSequence);
    }

    public void setTitle(int resId) {
        tvTitle.setText(getResources().getText(resId));
    }

    public void setTitleTextColor(@ColorRes int colorResId) {
        tvTitle.setTextColor(ContextCompat.getColor(getContext(), colorResId));
    }

    /**
     * 设置标题字体大小
     *
     * @param size
     */
    public void setTitleTextSize(int size) {
        tvTitle.setTextSize(size);
    }

    /**
     * 消息的点击事件
     *
     * @param l
     */
    public final void setOnNavMessageClickListener(OnClickListener l) {
        mMessage.setOnClickListener(l);
    }

    private ICallback onMoreClickListener;

    /**
     * 加号点击事件
     *
     * @param callback
     */
    public void setOnRightImageClickListener(ICallback callback) {
        this.onMoreClickListener = callback;
    }

}
