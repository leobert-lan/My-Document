package com.lht.cloudjob.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.lht.cloudjob.R;
import com.lht.cloudjob.util.debug.DLog;


/**
 *  <p><b>Package</b> com.lht.cloudjob.customview
 * <p><b>Project</b>
 * <p><b>Classname</b> AutoArrangeCardLayout
 * <p><b>Description</b>: 自适应排列
 *
 * Created by leobert on 2016/4/5.
 */
public class AutoArrangeCardLayout extends ViewGroup {

    private static final int DEFAULT_HORIZONTAL_SPACING = 10;
    private static final int DEFAULT_VERTICAL_SPACING = 10;

    private int mVerticalSpacing;
    private int mHorizontalSpacing;

    int line_height = 0;

    public AutoArrangeCardLayout(Context context) {
        super(context);
        setHorizontalSpacing(DEFAULT_HORIZONTAL_SPACING);
        setVerticalSpacing(DEFAULT_VERTICAL_SPACING);
    }

    public AutoArrangeCardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.AutoArrangeCardLayout);
        try {
            mHorizontalSpacing = a.getDimensionPixelSize(
                    R.styleable.AutoArrangeCardLayout_horizontal_spacing,
                    DEFAULT_HORIZONTAL_SPACING);
            mVerticalSpacing = a.getDimensionPixelSize(
                    R.styleable.AutoArrangeCardLayout_vertical_spacing,
                    DEFAULT_VERTICAL_SPACING);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        assert (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED);

        final int width = MeasureSpec.getSize(widthMeasureSpec)
                - getPaddingLeft() - getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop()
                - getPaddingBottom();
        final int count = getChildCount();
        int line_height = 0;

        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();

        int childHeightMeasureSpec;
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                    MeasureSpec.AT_MOST);
        } else {
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
        }

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                child.measure(
                        MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
                        childHeightMeasureSpec);
                final int childw = child.getMeasuredWidth();
                line_height = Math.max(line_height, child.getMeasuredHeight()
                        + mVerticalSpacing);

                if (xpos + childw > width) {
                    xpos = getPaddingLeft();
                    ypos += line_height;
                }

                xpos += childw + mHorizontalSpacing;
            }
        }
        this.line_height = line_height;

        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            height = ypos + line_height;

        } else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            if (ypos + line_height < height) {
                height = ypos + line_height;
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        final int width = r - l;
        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final int childw = child.getMeasuredWidth();
                final int childh = child.getMeasuredHeight();
                if (xpos + childw > width) {
                    xpos = getPaddingLeft();
                    ypos += line_height;
                }
                child.layout(xpos, ypos, xpos + childw, ypos + childh);
                xpos += childw + mHorizontalSpacing;
            }
        }
    }

    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new LayoutParams(p.width, p.height);
    }

    public void setHorizontalSpacing(int pixelSize) {
        mHorizontalSpacing = pixelSize;
    }

    public void setVerticalSpacing(int pixelSize) {
        mVerticalSpacing = pixelSize;
    }


    private static final String TAG_END = "end";

    public void addAtLast(View view) {
        view.setTag(TAG_END);
        addView(view);
    }

    public void addBeforeLast(View view) {
        View last = findViewWithTag(TAG_END);
        if (last != null) {
            int index = indexOfChild(last);
            addView(view, index);
        } else {
            DLog.e(getClass(), "not yet set a last,we just use addview to add this one");
            addView(view);
        }
    }

    public void addBeforeLastIfNecessary(View view) {
        Object tag = view.getTag();
        if (findViewWithTag(tag) != null)
            return;
        View last = findViewWithTag(TAG_END);
        if (last != null) {
            int index = indexOfChild(last);
            addView(view, index);
        } else {
            DLog.e(getClass(), "not yet set a last,we just use addview to add this one");
            addView(view);
        }
    }

    public int getChildrenCountWithoutAdd() {
        return getChildCount() - 1;
    }

    public void onFull() {
        View last = findViewWithTag(TAG_END);
        if (last != null) {
            if (last.getVisibility() == VISIBLE) {
                last.setVisibility(GONE);
            }
        }
    }

    public void onNotFull() {
        View last = findViewWithTag(TAG_END);
        if (last != null) {
            if (last.getVisibility() != VISIBLE) {
                last.setVisibility(VISIBLE);
            }
        }
    }


    public void removeViewByTag(Object tag) {
        View view = findViewWithTag(tag);
        if (view != null) {
            removeView(view);
        }
    }
}