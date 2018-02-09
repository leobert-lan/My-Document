package com.lht.cloudjob.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.lht.cloudjob.util.listview.ConflictListUtil;

/**
 * <p><b>Package</b> com.lht.cloudjob.customview
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> ConflictListview
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/28.
 */
public class ConflictListView extends ListView {

//    private boolean isOnConflictPeriod = false;
//
//    public boolean isOnConflictPeriod() {
//        return isOnConflictPeriod;
//    }
//
//    public void setOnConflictPeriod(boolean isOnConflictPeriod) {
//        this.isOnConflictPeriod = isOnConflictPeriod;
//    }

    public ConflictListView(Context context) {
        super(context);
        setFocusable(false);
    }

    public ConflictListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(false);
    }

    public ConflictListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFocusable(false);
    }

//    private OnConflictListLayoutListener onConflictListLayoutListener;
//
//    public OnConflictListLayoutListener getOnConflictListLayoutListener() {
//        return onConflictListLayoutListener;
//    }
//
//    public void setOnConflictListLayoutListener(OnConflictListLayoutListener onConflictListLayoutListener) {
//        this.onConflictListLayoutListener = onConflictListLayoutListener;
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

//        if (onConflictListLayoutListener != null) {
//            onConflictListLayoutListener.preMeasure(this);
//        }
//        setOnConflictPeriod(true);

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
//        if (onConflictListLayoutListener != null) {
//            onConflictListLayoutListener.finishLayout(this, changed);
//        }
//        setOnConflictPeriod(false);
    }

//    public interface OnConflictListLayoutListener {
//        void preMeasure(ConflictListView view);
//
//        void finishLayout(ConflictListView view, boolean changed);
//    }
}
