package com.lht.creationspace.customview.toolBar.navigation;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lht.creationspace.R;
import com.lht.creationspace.customview.toolBar.AbsNavigationToolBar;


/**
 * <p><b>Package</b> com.lht.creationspace.customview.toolBar.msg </p>
 * <p><b>Project</b> czspace </p>
 * <p><b>Classname</b> ToolbarTheme9 </p>
 * <p><b>Description</b>:样式9：back+text(title)+text </p>
 * <p>
 * Created by leobert on 2017/4/18.
 */
public class ToolbarTheme7 extends AbsNavigationToolBar {

    private View mToolbar;
    private ImageView ivBack;
    private TextView tvTitle;
    private View bottomDivider;
    /**
     * 右边的文字
     */
    private TextView tvRightText;

    public ToolbarTheme7(Context context) {
        super(context);
        init(context);
    }

    public ToolbarTheme7(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ToolbarTheme7(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mToolbar = View.inflate(context, R.layout.view_toolbar_theme7, this);
        ivBack = (ImageView) mToolbar.findViewById(R.id.iv_back);
        tvTitle = (TextView) mToolbar.findViewById(R.id.tv_title);
        bottomDivider = mToolbar.findViewById(R.id.bottom_divider);
        tvRightText = (TextView) mToolbar.findViewById(R.id.bar_theme9_text);

//        cbOperate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (iOperateCallback == null)
//                    return;
//                if (isManualSetState) {
//                    isManualSetState = false;
//                    return;
//                }
//                String text;
//                if (isChecked)
//                    text = iOperateCallback.onCheckedTrue();
//                else
//                    text = iOperateCallback.onCheckedFalse();
//                buttonView.setText(text);
//            }
//        });
    }

    public void setTitle(String text) {
        tvTitle.setText(text);
    }

    public void setTitle(int resId) {
        tvTitle.setText(getResources().getText(resId));
    }

    public void setTitleTextColor(@ColorRes int colorResid) {
        tvTitle.setTextColor(ContextCompat.getColor(getContext(), colorResid));
    }

    public void setOpText(int stringResid) {
        tvRightText.setText(stringResid);
    }

    public void setOpText(String stringResid) {
        tvRightText.setText(stringResid);
    }

    public void setOpTextColor(@ColorRes int colorResid) {
        tvRightText.setTextColor(ContextCompat.getColor(getContext(), colorResid));
    }

    public TextView getOpTextView() {
        return tvRightText;
    }

    public void setOpOnClickListener(OnClickListener l) {
        tvRightText.setOnClickListener(l);
    }

    @Override
    protected TextView getTitleTextView() {
        return tvTitle;
    }

    @Override
    protected ImageView getBackImageView() {
        return ivBack;
    }

    @Override
    protected View getDividerImageView() {
        return bottomDivider;
    }


    public void changeOpSurfaceByConfig(ToolbarTheme7.Config config) {
        if (!config.isEnableText()) {
            tvRightText.setText(null);
            tvRightText.setVisibility(INVISIBLE);
        } else {
            tvRightText.setVisibility(VISIBLE);
            tvRightText.setText(config.getText());
            tvRightText.setTextColor(ContextCompat.getColor(getContext(),
                    config.getTextColor()));
        }
    }

    public static class Config {
        boolean enableText;
        String text;
        @ColorRes
        int textColor = R.color.main_green_dark; //default

        public boolean isEnableText() {
            return enableText;
        }

        public void setEnableText(boolean enableText) {
            this.enableText = enableText;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getTextColor() {
            return textColor;
        }

        public void setTextColor(int textColor) {
            this.textColor = textColor;
        }
    }

}
