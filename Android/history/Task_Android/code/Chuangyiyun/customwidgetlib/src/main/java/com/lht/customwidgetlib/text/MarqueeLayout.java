package com.lht.customwidgetlib.text;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * @author leobert
 * @version 1.0
 * date 2016/8/10
 */
public class MarqueeLayout extends FrameLayout implements Runnable {
    public MarqueeLayout(Context context) {
        super(context);
    }

    public MarqueeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private CharSequence text;

    private long speed = 100; //pixel per second

    public void setSpeed(long speed) {
        if (speed > 0)
            this.speed = speed;
        else
            throw new IllegalArgumentException("you should provide a number greater than zero");
    }

    public void setMarqueeText(CharSequence text) {
        if (text == null || text.length() == 0) {
            this.text = "  ";
            return;
        }
        this.text = text;
    }

    private boolean isStarted = false;

    public void start() {
        if (isStarted) {
            return;
        }
        measure(MeasureSpec.AT_MOST, MeasureSpec.AT_MOST);
        this.isStarted = true;
        createNew();
    }


    public void stop() {
        if (!isStarted) {
            return;
        }
        this.isStarted = false;
        removeCallbacks(this);
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v != null) {
                v.clearAnimation();
            }
        }
        removeAllViews();
    }

    private int getTextLength(TextView tv) {
        Paint paint = tv.getPaint();
        if (text == null) {
            return 0;
        }
        return (int) paint.measureText(text.toString()) + 20;
    }

    private void createNew() {
        final int mWidth = getWidth();
        if (mWidth == 0) {
            long _t = 200;
            postDelayed(MarqueeLayout.this, _t);
            return;
        }
        final TextView tv = new TextView(getContext());

        final int length = getTextLength(tv);
        tv.setLayoutParams(new LayoutParams(length, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setSingleLine();
        tv.setText(text);
        addView(tv);


        Animation animation = inFromRightAnimation(length, mWidth);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                long _t = animation.getDuration() * (length) / (length +
                        mWidth) + 200;
                postDelayed(MarqueeLayout.this, _t);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                removeView(tv);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        tv.startAnimation(animation);
    }


    protected Animation inFromRightAnimation(int length, int width) {
        float _relativeX = -1.0f;
        if (length != 0 && width != 0) {
            _relativeX = -length / width - 1;
        }
        Animation inFromRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, _relativeX,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);


        long duration = (length + width) / speed * 1000;

        inFromRight.setDuration(duration);
//        inFromRight.setDuration(animDuration);
        inFromRight.setInterpolator(new LinearInterpolator());
        return inFromRight;
    }

    @Override
    public void run() {
        if (isStarted) {
            createNew();
        }
    }
}
