package com.lht.cloudjob.customview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lht.cloudjob.R;
import com.lht.cloudjob.ThemeManager;
import com.lht.cloudjob.util.DisplayUtils;

import java.lang.ref.WeakReference;


public class TitleBar2 extends FrameLayout {

    protected Context context;
    protected RelativeLayout mTitleBar;
    protected ImageView mLeftView;
    protected TextView mTitleText;

    public TitleBar2(Context context) {
        super(context);
        this.context = context;
        inflateView();
        init();
    }

    public TitleBar2(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflateView();
        init();
    }

    protected void inflateView() {
        mTitleBar = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_bar_suspended, null);
    }

    protected void init() {
        mLeftView = (ImageView) mTitleBar.findViewById(R.id.nav_back);
        mTitleText = (TextView) mTitleBar.findViewById(R.id.nav_title);
        float height = DisplayUtils.convertDpToPx(context, 56f);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) height);
        addView(mTitleBar, params);

        mLeftView.setVisibility(INVISIBLE);
//        mTitleBar.setBackgroundColor(ThemeManager.with(getContext()).getCurrentColor());

        bringToFront();
    }

    public void setOnBackListener(final ITitleBackListener listener) {
        if (listener != null) {
            mLeftView.setVisibility(View.VISIBLE);
            mLeftView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onTitleBackClick();
                }
            });
        } else {
            mLeftView.setVisibility(View.GONE);
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
