package com.lht.creationspace.customview.toolBar.navigation;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by chhyu on 2017/4/19.
 */

public class ToolbarTheme3Async extends ToolbarTheme3 {
    public ToolbarTheme3Async(Context context) {
        super(context);
    }

    public ToolbarTheme3Async(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ToolbarTheme3Async(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void proxyRightOpRules() {
        cbCollectionState.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iCbOperateListener != null) {
                    if (rightOpState)
                        iCbOperateListener.onStateWillBeFalse();
                    else
                        iCbOperateListener.onStateWillBeTrue();
                }
                //keep checked state
                manualSetRightOpStatus(rightOpState);
            }
        });
    }
}
