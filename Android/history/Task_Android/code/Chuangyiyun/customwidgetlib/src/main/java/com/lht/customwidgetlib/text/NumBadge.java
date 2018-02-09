package com.lht.customwidgetlib.text;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lht.customwidgetlib.R;

/**
 * <p><b>Package</b> com.lht.customwidgetlib.text
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> NumBadge
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/12/12.
 */

public class NumBadge extends FrameLayout implements INumBadge {

    public static int RECOMMEND_MAX = 99;

    public NumBadge(Context context) {
        this(context, null);
    }

    public NumBadge(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumBadge(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private View contentView;

    private TextView badge;


    private int shape;

    private void init(AttributeSet attrs, int defStyle) {
        contentView = inflate(getContext(), R.layout.view_num_badge, this);

        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.NumBadge, defStyle, 0);
        shape = array.getInt(R.styleable.NumBadge_backgroundShape, NumBadge.Shape.oval.ordinal());

        array.recycle();
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        badge = (TextView) contentView.findViewById(R.id.numbadge_tv_badge);
    }

    @Override
    public void clear() {
        badge.setText(null);
        badge.setBackgroundResource(R.color.transparent);
    }

    @Override
    public void updateWithPointMode() {
        badge.setText(null);

        badge.setMinWidth(getWidthPixel(0));//为了区分特殊的1，当取实际1时传1，取点时1为0
        badge.setHeight(getHeightPixelPointMode());

        badge.setBackgroundResource(R.drawable.bg_notify_number_point);
    }

    @Override
    public void updateWithActualMode(int num) {
        badge.setText(String.valueOf(num));

        badge.setMinWidth(getWidthPixel(num));
        badge.setHeight(getHeightPixelNormalMode());
        badge.setBackgroundResource(getBackgroundShapeResource(num));
    }

    @Override
    public void updateWithFriendlyMode(int num, int max) {
        badge.setText(formatFriendlyNum(num, max));

        badge.setMinWidth(getWidthPixel(num));
        badge.setHeight(getHeightPixelNormalMode());
        badge.setBackgroundResource(getBackgroundShapeResource(num));
    }

    @Override
    public void setBackgroundShape(Shape bgShape) {
        shape = bgShape.ordinal();
        requestLayout();
    }

    private int getBackgroundShapeResource(int num) {
        if (num < 10 && num > 0) { // 强制返回圆形
            return R.drawable.bg_notify_number_oval;
        }

        if (shape == Shape.rectAngle.ordinal()) {
            return R.drawable.bg_notify_number_ra;
        } else {
            return R.drawable.bg_notify_number_oval;
        }
    }

    /**
     * @param num 为了区分特殊的1，当取实际1时传1，取点时1为0
     * @return
     */
    private int getWidthPixel(int num) {
        return (int) getResources().getDimension(getWidthDimenResource(num));
    }

    private int getHeightPixelPointMode() {
        return (int) getResources().getDimension(R.dimen.badge_height_point);
    }

    private int getHeightPixelNormalMode() {
        return (int) getResources().getDimension(R.dimen.badge_height_normal);
    }

    /**
     * @param num 为了区分特殊的1，当取实际1时传1，取点时1为0
     * @return
     */
    private int getWidthDimenResource(int num) {
        if (num < 10 && num > 0) { // 强制返回圆形
            return R.dimen.badge_minwidth_small;
        } else if (num >= 10) {
            return R.dimen.badge_minwidth_big;
        } else {
            return R.dimen.badge_minwidth_point;
        }
    }

    private String formatFriendlyNum(int num, int max) {
        if (num <= max) {
            return String.valueOf(num);
        } else {
            return String.valueOf(max) + "+";
        }
    }
}
