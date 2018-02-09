package com.lht.cloudjob.customview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.lht.cloudjob.R;

/**
 * <p><b>Package</b> com.lht.cloudjob.customview
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> TitleBarModify
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/8/18.
 */
public class TitleBarModify extends TitleBar {

    private CheckBox rightOp;

    /**
     * 全选按钮
     */
    private CheckBox leftOp;

    public TitleBarModify(Context context) {
        super(context);
    }

    public TitleBarModify(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void inflateView() {
        mTitleBar = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_bar_modify, null);
    }

    private void showLeftToggle() {
        leftOp.setVisibility(INVISIBLE);
        mLeftView.setVisibility(View.VISIBLE);
        mLeftView.bringToFront();
    }

    @Override
    protected void init() {
        super.init();
        rightOp = (CheckBox) mTitleBar.findViewById(R.id.modify_bar_rightop);
        leftOp = (CheckBox) mTitleBar.findViewById(R.id.modify_bar_leftop);
        showLeftToggle();
    }


    private void showLeftOp() {
        mLeftView.setVisibility(INVISIBLE);
        leftOp.setVisibility(View.VISIBLE);
        leftOp.bringToFront();
    }

    public void setOnBackListener(final TitleBar.ITitleBackListener listener) {
        mLeftView.setVisibility(View.VISIBLE);
        if (listener != null) {
            mLeftView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onTitleBackClick();
                }
            });
        }
    }

    public void setDefaultOnBackListener(Activity activity) {
        setOnBackListener(new TitleBar.DefaultOnBackClickListener(activity));
    }

    public void setTitle(String text) {
        mTitleText.setText(text);
    }

    public void setTitle(int resId) {
        mTitleText.setText(getResources().getText(resId));
    }

    public void setTitleTextColor(int colorResid) {
        mTitleText.setTextColor(colorResid);
    }

    /**
     * desc: 设置右侧按钮的点击回掉
     */
    public void setRightOnToggleListener(CompoundButton.OnCheckedChangeListener l) {
        if (l == null) {
            rightOp.setOnCheckedChangeListener(null);
            rightOp.setChecked(false);
            rightOp.setText(R.string.v1010_default_demandmanager_text_modify);
            return;
        }
        OnCheckedChangeListenerWrapper2 wrapper = new OnCheckedChangeListenerWrapper2(l);
        rightOp.setOnCheckedChangeListener(wrapper);
        //不可以手动回调一次
    }

    /**
     * desc: 设置左侧按钮的点击回掉
     */
    public void setLeftOnToggleListener(CompoundButton.OnCheckedChangeListener l) {
        if (l == null) {
            leftOp.setOnCheckedChangeListener(null);
            leftOp.setChecked(false);
            leftOp.setText(R.string.v1010_default_demandmanager_text_selectall);
            return;
        }

        OnCheckedChangeListenerWrapper3 wrapper = new OnCheckedChangeListenerWrapper3(l);
        leftOp.setOnCheckedChangeListener(wrapper);
    }


    @Override
    public void onThemeChange(int color) {
        mTitleBar.setBackgroundColor(color);
    }

    /**
     * 取消编辑状态
     */
    public void finishModify() {
        rightOp.setChecked(false);
    }


    /**
     * 包裹一层，用以内部切换右侧操作按钮的显示文字
     */
    private final class OnCheckedChangeListenerWrapper2 implements CompoundButton.OnCheckedChangeListener {

        private CompoundButton.OnCheckedChangeListener listener;


        OnCheckedChangeListenerWrapper2(CompoundButton.OnCheckedChangeListener listener) {
            this.listener = listener;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!isChecked) {
                rightOp.setText(R.string.v1010_default_demandmanager_text_modify);
                //切换到显示头像
                showLeftToggle();
            } else {
                rightOp.setText(R.string.v1010_default_demandmanager_text_complete);
                //切换到显示全选
                showLeftOp();
            }
            listener.onCheckedChanged(buttonView, isChecked);
        }
    }

    /**
     * 包裹一层，用以内部切换左侧操作按钮的显示文字
     */
    private final class OnCheckedChangeListenerWrapper3 implements CompoundButton.OnCheckedChangeListener {

        private CompoundButton.OnCheckedChangeListener listener;


        OnCheckedChangeListenerWrapper3(CompoundButton.OnCheckedChangeListener listener) {
            this.listener = listener;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//            if (!isChecked) {
//                leftOp.setText(R.string.v1010_default_demandmanager_text_selectall);
//            } else {
//                leftOp.setText(R.string.v1010_default_demandmanager_text_deselectall);
//            }
//            listener.onCheckedChanged(buttonView, isChecked);
            //Attention: 仅显示全选，仅当做全选操作
            leftOp.setText(R.string.v1010_default_demandmanager_text_selectall);
            listener.onCheckedChanged(buttonView, true);
        }
    }

    /**
     * 显示右边的编辑按钮
     */
    public void setShowRightToggle() {
        if(rightOp.getVisibility() == VISIBLE) {
            return;
        }
        rightOp.setVisibility(VISIBLE);
    }

    /**
     * 隐藏右边的编辑按钮
     */
    public void setHideRightToggle() {
        if(rightOp.getVisibility() == INVISIBLE) {
            return;
        }
        rightOp.setVisibility(INVISIBLE);
    }
}