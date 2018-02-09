package com.lht.creationspace.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * <p><b>Package</b> com.lht.vsocyy.customview
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> ConflictGridView
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/8/12.
 */
public class ConflictGridView extends GridView {
    public ConflictGridView(Context context) {
        super(context);
        setFocusable(false);
    }

    public ConflictGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(false);
    }

    public ConflictGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFocusable(false);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE/2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
