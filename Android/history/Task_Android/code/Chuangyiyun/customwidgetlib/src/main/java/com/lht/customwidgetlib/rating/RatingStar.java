package com.lht.customwidgetlib.rating;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.lht.customwidgetlib.DisplayUtils;
import com.lht.customwidgetlib.R;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.customwidgetlib.rating
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> RatingStar
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/9/9
 */
public class RatingStar extends FrameLayout {

    private static final int DEFAULT_COUNT = 5;

    public RatingStar(Context context) {
        this(context, null);
    }

    public RatingStar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatingStar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(defStyleAttr);
    }

    private void init(int attr) {
        inflate(getContext(), R.layout.view_rating_star, this);
    }

    private LinearLayout linearLayout;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        linearLayout = (LinearLayout) findViewById(R.id.rating_star_ll);
        setStarNums(DEFAULT_COUNT);
        resetStarState(0);
    }

    private int nums;

    public void setStarNums(int nums) {
        if (nums <= 0) {
            setStarNums(DEFAULT_COUNT);
            return;
        }
        this.nums = nums;
        fillStarsOnLayout();
    }

    public int getStarNums() {
        return this.nums;
    }

    private ArrayList<CheckBox> checkBoxes = new ArrayList<>();

    private void fillStarsOnLayout() {
        linearLayout.removeAllViews();
        for (int i = 0; i < nums; i++) {
            CheckBox cb = getStar();
            cb.setTag(i);
            cb.setOnClickListener(onClickListener);
            linearLayout.addView(cb, getMyLayoutParams());
            checkBoxes.add(cb);
        }
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Object tag = v.getTag();
            if (tag == null || !(tag instanceof Integer)) {
                return;
            }
            Integer i = (Integer) tag;
            resetStarState(i);

            if (getOnRatingChangedListener() != null) {
                getOnRatingChangedListener().onRatingLevel(i + 1);
            }

            setLevel(i + 1);
        }
    };

    private int level = 1;

    public int getLevel() {
        return level;
    }

    private void setLevel(int level) {
        this.level = level;
    }

    private void resetStarState(Integer pCurrent) {
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (i <= pCurrent) {
                checkBoxes.get(i).setChecked(true);
            } else {
                checkBoxes.get(i).setChecked(false);
            }
        }
    }

    private CheckBox getStar() {
        CheckBox checkBox = new CheckBox(getContext());
        checkBox.setButtonDrawable(R.drawable.selector_rating_star);
        int padding = DisplayUtils.convertDpToPx(getContext(),3f);
        checkBox.setPadding(padding,padding,padding,padding);
        return checkBox;
    }

    private LinearLayout.LayoutParams getMyLayoutParams() {
        LinearLayout.LayoutParams params;
        params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        return params;
    }

    private OnRatingChangedListener onRatingChangedListener;

    public RatingStar.OnRatingChangedListener getOnRatingChangedListener() {
        return onRatingChangedListener;
    }

    public void setOnRatingChangedListener(RatingStar.OnRatingChangedListener
                                                   onRatingChangedListener) {
        this.onRatingChangedListener = onRatingChangedListener;
    }

    public interface OnRatingChangedListener {
        /**
         * only when you want to realize a auto-change listener
         * you can relay on this,it's better to get the level when you want to know it
         *
         * @param level started from 1
         */
        void onRatingLevel(int level);
    }
}
