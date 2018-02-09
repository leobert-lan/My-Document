package com.lht.creationspace.customview.toolBar.navigation;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.lht.creationspace.R;
import com.lht.creationspace.customview.toolBar.AbsNavigationToolBar;
import com.lht.creationspace.listener.ICallback;
import com.lht.creationspace.util.debug.DLog;

/**
 * <p><b>Package</b> com.lht.creationspace.customview.toolBar.msg </p>
 * <p><b>Project</b> czspace </p>
 * <p><b>Classname</b> ToolbarTheme3 </p>
 * <p><b>Description</b>:样式3：back+text(title)+cb+image </p>
 * <p>
 * Created by leobert on 2017/4/18.
 */
public abstract class ToolbarTheme3 extends AbsNavigationToolBar {

    private View mToolbar;
    private ImageView ivBack;
    private TextView tvTitle;
    private View bottomDivider;
    private ImageView imgShare;
    protected CheckBox cbCollectionState;
    protected boolean rightOpState;

    public ToolbarTheme3(Context context) {
        super(context);
        init(context);
    }

    public ToolbarTheme3(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ToolbarTheme3(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mToolbar = View.inflate(context, R.layout.view_toolbar_theme3, this);
        ivBack = (ImageView) mToolbar.findViewById(R.id.iv_back);
        tvTitle = (TextView) mToolbar.findViewById(R.id.tv_title);
        bottomDivider = mToolbar.findViewById(R.id.bottom_divider);
        imgShare = (ImageView) mToolbar.findViewById(R.id.iv_img_share);
        cbCollectionState = (CheckBox) mToolbar.findViewById(R.id.cb_collection_state);

        proxyRightOpRules();
    }

    protected abstract void proxyRightOpRules();

    protected ICbOperateListener iCbOperateListener;

    public void setRightOpListener(ICbOperateListener l) {
        iCbOperateListener = l;
    }

    public interface ICbOperateListener {
        void onStateWillBeTrue();

        void onStateWillBeFalse();
    }


    protected ISurfaceConfig iSurfaceConfig;

    public void useISurfaceConfig(ISurfaceConfig iSurfaceConfig) {
        this.iSurfaceConfig = iSurfaceConfig;
        manualSetRightOpStatus(false); //init state
    }

    public void manualSetRightOpStatus(boolean checked) {
        cbCollectionState.setChecked(checked);
        rightOpState = checked;
        if (iSurfaceConfig == null) {
            DLog.e(getClass(), "useISurfaceConfig at first,null is not allowed");
            //just throw
        }
        ISurfaceConfig.Config config = iSurfaceConfig.getConfigByState(checked);
        changeOpSurfaceByConfig(config);
    }

    protected void changeOpSurfaceByConfig(ISurfaceConfig.Config config) {
        if (!config.isEnableDrawable()) {
            cbCollectionState.setButtonDrawable(null);
        } else {
            cbCollectionState.setButtonDrawable(getContext().getDrawable(config.getDrawableRes()));
        }
    }

    public interface ISurfaceConfig {
        ISurfaceConfig.Config getConfigByState(boolean checked);

        class Config {
            boolean enableDrawable;
            boolean enableText;
            @DrawableRes
            int drawableRes;
            String text;
            @ColorRes
            int textColor = R.color.main_green_dark; //default

            public boolean isEnableDrawable() {
                return enableDrawable;
            }

            public void setEnableDrawable(boolean enableDrawable) {
                this.enableDrawable = enableDrawable;
            }

            public boolean isEnableText() {
                return enableText;
            }

            public void setEnableText(boolean enableText) {
                this.enableText = enableText;
            }

            public int getDrawableRes() {
                return drawableRes;
            }

            public void setDrawableRes(int drawableRes) {
                this.drawableRes = drawableRes;
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

    public void setOnShareClickListener(final ICallback callback) {
        imgShare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null)
                    callback.onCallback();
            }
        });
    }
}
