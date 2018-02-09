package com.lht.cloudjob.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lht.cloudjob.R;
import com.lht.cloudjob.ThemeManager;
import com.lht.cloudjob.customview.zdepth.ZDepthShadowLayout;
import com.lht.cloudjob.interfaces.bars.OnToggleListener;
import com.lht.cloudjob.util.DisplayUtils;


public class FgMessageTitleBar extends ZDepthShadowLayout
        implements ThemeManager.IThemeListener {

    private Context context;
    private RelativeLayout mTitleBar;
    private View mLeftView;

    private RoundImageView avatar;

    private ImageView imgNotify;

    public FgMessageTitleBar(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public FgMessageTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        mTitleBar = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout
                .view_bar_message, null);
        mLeftView = mTitleBar.findViewById(R.id.toolbar_left);
        avatar = (RoundImageView) mTitleBar.findViewById(R.id.toolbar_avatar);
        imgNotify = (ImageView) mTitleBar.findViewById(R.id.toolbar_notify);


        float height = DisplayUtils.convertDpToPx(context, 56f);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) height);
        addView(mTitleBar, params);

        mTitleBar.setBackgroundColor(ThemeManager.with(getContext()).getCurrentColor());

        ThemeManager.with(getContext()).registerListener(this);
        bringToFront();
    }

    public void setOnToggleListener(final OnToggleListener listener) {
        mLeftView.setVisibility(View.VISIBLE);
        if (listener != null) {
            mLeftView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //永远不可能用它来关闭
                    listener.onOpenMore();
                }
            });
        }
    }


    @Override
    public void onThemeChange(int color) {
        mTitleBar.setBackgroundColor(color);
    }

    public ImageView getAvatarView() {
        return avatar;
    }
}
