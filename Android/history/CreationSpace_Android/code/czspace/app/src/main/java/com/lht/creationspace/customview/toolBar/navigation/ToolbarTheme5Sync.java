package com.lht.creationspace.customview.toolBar.navigation;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * <p><b>Package</b> com.lht.creationspace.customview.toolBar.msg </p>
 * <p><b>Project</b> czspace </p>
 * <p><b>Classname</b> ToolbarTheme5Sync </p>
 * <p><b>Description</b>:样式5：back+text(title)+cb </p>
 * a sync mode for checkbox operate
 * <p>
 * Created by leobert on 2017/4/18.
 */
public class ToolbarTheme5Sync extends ToolbarTheme5 {

    public ToolbarTheme5Sync(Context context) {
        super(context);
    }

    public ToolbarTheme5Sync(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ToolbarTheme5Sync(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void proxyRightOpRules() {
        cbRightOp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean state = !rightOpState;//立即改变
                if (iCbOperateListener != null) {
                    if (state)
                        iCbOperateListener.onStateWillBeTrue();
                    else
                        iCbOperateListener.onStateWillBeFalse();
                }
                manualSetRightOpStatus(state);
            }
        });
    }
}
