package com.lht.creationspace.customview.toolBar.navigation;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * <p><b>Package</b> com.lht.creationspace.customview.toolBar.msg </p>
 * <p><b>Project</b> czspace </p>
 * <p><b>Classname</b> ToolbarTheme5Async </p>
 * <p><b>Description</b>:样式5："message"+text(title)+cb </p>
 * <p>异步操作方式
 * Created by leobert on 2017/4/18.
 */
public class ToolbarTheme5Async extends ToolbarTheme5 {

    public ToolbarTheme5Async(Context context) {
        super(context);
    }

    public ToolbarTheme5Async(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ToolbarTheme5Async(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void proxyRightOpRules() {
        cbRightOp.setOnClickListener(new OnClickListener() {
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

    public void setSubscribeEnable(boolean enabled) {
        if (enabled)
            cbRightOp.setVisibility(VISIBLE);
        else
            cbRightOp.setVisibility(GONE);
    }


}
