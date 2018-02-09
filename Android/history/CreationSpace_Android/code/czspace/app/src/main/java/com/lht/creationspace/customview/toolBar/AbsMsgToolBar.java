package com.lht.creationspace.customview.toolBar;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * <p><b>Package:</b> com.lht.creationspace.customview.toolBar </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> AbsMsgToolBar </p>
 * <p><b>Description:</b> 抽象包含通知的toolbar，包含4、6、8 todo 丰富doc </p>
 * Created by leobert on 2017/4/18.
 */
public abstract class AbsMsgToolBar extends AbsLhtToolBar {
    public AbsMsgToolBar(Context context) {
        super(context);
    }

    public AbsMsgToolBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AbsMsgToolBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
