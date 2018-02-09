package com.lht.cloudjob.customview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lht.cloudjob.R;


public class TitleBarWithOp extends TitleBar {

    private Context context;

    public TitleBarWithOp(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public TitleBarWithOp(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void inflateView() {
        mTitleBar = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_bar_op_right, null);
    }

    @Override
    protected void init() {
        super.init();
        txtOperate = (TextView) mTitleBar.findViewById(R.id.nav_sub);
    }

    private TextView txtOperate;

    public void setOpText(int stringResid) {
        txtOperate.setText(stringResid);
    }

    public void setOpOnClickListener(OnClickListener l) {
        txtOperate.setOnClickListener(l);
    }

    public void setOnBackListener(final ITitleBackListener listener) {
        mLeftView.setVisibility(View.VISIBLE);
        if (listener != null) {
            mLeftView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onTitleBackClick();
                }
            });
        }
    }

    public void setDefaultOnBackListener(Activity activity) {
        setOnBackListener(new DefaultOnBackClickListener(activity));
    }

    public void setTitle(String text) {
        mTitleText.setText(text);
    }

    public void setTitle(int resId) {
        mTitleText.setText(getResources().getText(resId));
    }

    public void setTitleTextColor(int colorResid) {
        mTitleText.setTextColor(colorResid);
    }

    @Override
    public void onThemeChange(int color) {
        mTitleBar.setBackgroundColor(color);
    }

    public interface ITitleBackListener {
        void onTitleBackClick();
    }


}
