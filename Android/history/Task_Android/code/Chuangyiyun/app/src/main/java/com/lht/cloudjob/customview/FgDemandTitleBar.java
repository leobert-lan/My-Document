package com.lht.cloudjob.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.lht.cloudjob.R;
import com.lht.cloudjob.ThemeManager;
import com.lht.cloudjob.customview.zdepth.ZDepthShadowLayout;
import com.lht.cloudjob.interfaces.bars.OnToggleListener;
import com.lht.cloudjob.util.DisplayUtils;


public class FgDemandTitleBar extends ZDepthShadowLayout
        implements ThemeManager.IThemeListener {

    private Context context;
    private RelativeLayout mTitleBar;
    private View mLeftView;

    private RoundImageView avatar;

    private ImageView imgNotify;

    private RadioGroup radioGroup;

    private RadioButton rbOrdered, rbCollected;

    private CheckBox rightOp;

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener;
    /**
     * 全选按钮
     */
    private CheckBox leftOp;

    public FgDemandTitleBar(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public RadioGroup.OnCheckedChangeListener getOnCheckedChangeListener() {
        return onCheckedChangeListener;
    }

    public void setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
        OnCheckedChangeListenerWrapper wrapper = new OnCheckedChangeListenerWrapper(onCheckedChangeListener);
        radioGroup.setOnCheckedChangeListener(wrapper);
        radioGroup.check(rbOrdered.getId());
        //手动回调一次
        wrapper.onCheckedChanged(radioGroup, rbOrdered.getId());
    }

    public FgDemandTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        mTitleBar = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_bar_demand, null);
        mLeftView = mTitleBar.findViewById(R.id.toolbar_left);
        avatar = (RoundImageView) mTitleBar.findViewById(R.id.toolbar_avatar);
        imgNotify = (ImageView) mTitleBar.findViewById(R.id.toolbar_notify);

        radioGroup = (RadioGroup) mTitleBar.findViewById(R.id.fgdemand_tabs);
        rbOrdered = (RadioButton) mTitleBar.findViewById(R.id.fgdemand_rb_ordered);
        rbCollected = (RadioButton) mTitleBar.findViewById(R.id.fgdemand_rb_collected);

        radioGroup.check(rbOrdered.getId());

        rightOp = (CheckBox) mTitleBar.findViewById(R.id.fgdemand_bar_rightop);
        leftOp = (CheckBox) mTitleBar.findViewById(R.id.fgdemand_bar_leftop);

        showLeftToggle();

        float height = DisplayUtils.convertDpToPx(context, 56f);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) height);
        addView(mTitleBar, params);

        mTitleBar.setBackgroundColor(ThemeManager.with(getContext()).getCurrentColor());

        ThemeManager.with(getContext()).registerListener(this);
        bringToFront();
    }

    private void showLeftOp() {
        mLeftView.setVisibility(INVISIBLE);
        leftOp.setVisibility(View.VISIBLE);
        leftOp.bringToFront();
    }

    private void showLeftToggle() {
        leftOp.setVisibility(INVISIBLE);
        mLeftView.setVisibility(View.VISIBLE);
        mLeftView.bringToFront();
    }

    public void closeModifyMode() {
        rightOp.setChecked(false);
    }

    public void setOnToggleListener(final OnToggleListener listener) {
        mLeftView.setVisibility(View.VISIBLE);
        if (listener != null) {
            mLeftView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //永远不可能用它来关闭
                    listener.onOpenMore();
                }
            });
        }
    }

    /**
     * desc: 设置右侧按钮的点击回掉
     */
    public void setOpOnToggleListener(CompoundButton.OnCheckedChangeListener l) {
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
    public void setSelectOnToggleListener(CompoundButton.OnCheckedChangeListener l) {
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

    public ImageView getAvatarView() {
        return avatar;
    }

    public void enableRightOperation() {
        if (!rbCollected.isChecked()) {
            return;
        }
        if (rightOp.getVisibility() == VISIBLE) {
            return;
        }

        rightOp.setVisibility(View.VISIBLE);
        if (rightOp.isChecked()) {
            showLeftOp();
        } else {
            showLeftToggle();
        }
    }

    public void disableRightOperation() {
        if (rightOp.getVisibility() == INVISIBLE) {
            return;
        }

        rightOp.setVisibility(View.INVISIBLE);
        showLeftToggle();
        //右侧按钮置为默认状态
        rightOp.setChecked(false);
    }

    /**
     * 对中间的tab回调包裹一层，用以内部处理右侧操作按钮的显示,
     */
    private final class OnCheckedChangeListenerWrapper implements RadioGroup.OnCheckedChangeListener {

        private RadioGroup.OnCheckedChangeListener listener;


        OnCheckedChangeListenerWrapper(RadioGroup.OnCheckedChangeListener listener) {
            this.listener = listener;
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.fgdemand_rb_ordered:
                    //承接，尝试关闭右侧操作
                    disableRightOperation();
                    break;
                case R.id.fgdemand_rb_collected:
                    //收藏、尝试打开右侧操作
                    enableRightOperation();
                    break;
                default:
                    break;
            }
            listener.onCheckedChanged(group, checkedId);
        }
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

    public static class CloseModifyModeEvent {
    }

    /**
     * 尝试打开右侧操作事件
     */
    public static class TryEnableRightOperateEvent{}
}
