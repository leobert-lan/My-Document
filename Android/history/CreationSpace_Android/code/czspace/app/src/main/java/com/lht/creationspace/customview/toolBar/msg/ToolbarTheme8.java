package com.lht.creationspace.customview.toolBar.msg;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lht.creationspace.R;
import com.lht.creationspace.customview.toolBar.AbsMsgToolBar;
import com.lht.creationspace.listener.ICallback;

import individual.leobert.uilib.numbadge.NumBadge;

/**
 * <p><b>Package</b> com.lht.creationspace.customview.toolBar.msg </p>
 * <p><b>Project</b> czspace </p>
 * <p><b>Classname</b> ToolbarTheme8 </p>
 * <p><b>Description</b>:样式8："message"+image(icon)+image </p>
 * <p>
 * Created by leobert on 2017/4/18.
 */
public class ToolbarTheme8 extends AbsMsgToolBar {

    private View mToolbar;
    private ImageView ivTitleImage;
    private View bottomDivider;
    private FrameLayout mMessage;
    private NumBadge mMessageHint;
    protected ImageView mMore;

    public ToolbarTheme8(Context context) {
        super(context);
        init(context);
    }

    public ToolbarTheme8(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ToolbarTheme8(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setContentInsetsAbsolute(0, 0);
        mToolbar = View.inflate(context, R.layout.view_toolbar_theme8, this);

        ivTitleImage = (ImageView) mToolbar.findViewById(R.id.iv_title_image);
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


    /**
     * 隐藏标题与标题下面内容的分隔线
     */
    public void hideTitleBottomDividerLine() {
        bottomDivider.setVisibility(GONE);
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
