package com.lht.creationspace.customview.toolBar;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

/**
 * <p><b>Package:</b> com.lht.creationspace.customview.toolBar </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> AbsLhtToolBar </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/4/18.
 */

abstract class AbsLhtToolBar extends Toolbar{
    public AbsLhtToolBar(Context context) {
        super(context);
    }

    public AbsLhtToolBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AbsLhtToolBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
