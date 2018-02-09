package com.lht.creationspace.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lht.creationspace.R;
import com.lht.creationspace.listener.ICallback;
import com.lht.creationspace.util.debug.DLog;

/**
 * <p><b>Package</b> com.lht.vsocyy.customview
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> MaskView
 * <p><b>Description</b>: 用于空数据视图、请求失败等
 * <p> Create by Leobert on 2016/8/17
 */
public class MaskView extends FrameLayout implements IDisplayMask {

    private View contentView;

    private ImageView mHintPic;

    private TextView mHintText;

    private TextView mOperateText;

    private final Animation fadeInAnim;

    private final Animation fadeOutAnim;

    static final int ANIMATION_DURATION = 150;

    public MaskView(Context context) {
        super(context);
        fadeInAnim = new AlphaAnimation(0.3f, 1f);
        fadeOutAnim = new AlphaAnimation(1f, 0f);
        init();
    }

    public MaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        fadeInAnim = new AlphaAnimation(0.3f, 1f);
        fadeOutAnim = new AlphaAnimation(1f, 0f);
        init();
    }

    public MaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        fadeInAnim = new AlphaAnimation(0.3f, 1f);
        fadeOutAnim = new AlphaAnimation(1f, 0f);
        init();
    }

    private void init() {
        contentView = inflate(getContext(), R.layout.view_mask, this);

        mHintPic = (ImageView) contentView.findViewById(R.id.mask_icon);
        mHintText = (TextView) contentView.findViewById(R.id.mask_hint);
        mOperateText = (TextView) contentView.findViewById(R.id.mask_op);

        this.fadeInAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        this.fadeInAnim.setDuration(ANIMATION_DURATION);
        this.fadeInAnim.setFillAfter(false);

        this.fadeOutAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        this.fadeOutAnim.setDuration(ANIMATION_DURATION);
        this.fadeOutAnim.setFillAfter(false);

        fadeInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(VISIBLE);
                bringToFront();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fadeOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

//    @Deprecated
//    private void showAsUnlogin(OnClickListener listener) {
//        setVisibility(VISIBLE);
//        bringToFront();
////        mImageView.setImageResource(R.drawable.v1011_drawable_mask_unlogin);
////        v1010_view_mask_text_unlogin 您还未登录，去登录
//        String s = getResources().getString(R.string.v1010_view_mask_text_unlogin);
//        SpannableStringBuilder builder = new SpannableStringBuilder(s);
//        builder.setSpan(newClickSpan(listener), 6, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        int color = Color.argb(255, 45, 126, 226);
//        builder.setSpan(new ForegroundColorSpan(color), 6, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        builder.setSpan(new UnderlineSpan(), 6, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        mTextView.setText(builder);
//        mTextView.setMovementMethod(LinkMovementMethod.getInstance());
//    }
//
//    private ClickableSpan newClickSpan(final OnClickListener listener) {
//        return new ClickableSpan() {
//            @Override
//            public void onClick(View widget) {
//                if (listener != null)
//                    listener.onClick(widget);
//            }
//        };
//    }

    private void show() {
        if (getAnimation() == fadeInAnim && !getAnimation().hasEnded()) {
            //on shown return
            return;
        }

        if (getVisibility() == VISIBLE) {
            setAlpha(1f);
            bringToFront();
            return;
        }

        try {
            contentView.setOnClickListener(null);
            if (getAnimation() != null)
                getAnimation().cancel();
            startAnimation(fadeInAnim);
            bringToFront();
        } catch (Exception e) {
            setVisibility(VISIBLE);
            e.printStackTrace();
        }
    }

    @Override
    public void showLoadingMask() {
        try {
            show();
            Glide.with(getContext()).load(R.drawable.temp_loading_1).asGif().into(mHintPic);
            mHintText.setText(R.string.v1000_default_mask_loading);
            mOperateText.setText("");
        } catch (Exception e) {
            DLog.e(DLog.Lmsg.class, new DLog.LogLocation(), " exception," + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void showPoolNetMask() {
        try {
            show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showLoadFailMask(final ICallback onRetry) {
        try {
            show();
            contentView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRetry.onCallback();
                }
            });
            Glide.with(getContext()).load(R.drawable.v1000_drawable_load_fail)
                    .asBitmap().into(mHintPic);
            mHintText.setText(R.string.v1000_default_mask_load_fail);
            mOperateText.setText(R.string.v1000_default_mask_op_retry);
        } catch (Exception e) {
            DLog.e(DLog.Lmsg.class, new DLog.LogLocation(), " exception," + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void showUnLoginMask(CharSequence loginAccessHint) {
        try {
            show();
            Glide.with(getContext()).load(R.drawable.v1000_drawable_unauth)
                    .asBitmap().into(mHintPic);
            mHintText.setText(R.string.v1000_default_mask_unauth);
            mOperateText.setText(R.string.v1000_default_mask_op_auth);
        } catch (Exception e) {

            DLog.e(DLog.Lmsg.class, new DLog.LogLocation(), " exception," + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (getVisibility() == GONE) {
            DLog.w(getClass(), "not shown,ignore close request");
            return;
        }
        try {
            if (getAnimation() != null)
                getAnimation().cancel();
            startAnimation(fadeOutAnim);
        } catch (Exception e) {

            DLog.e(DLog.Lmsg.class, new DLog.LogLocation(), " exception," + e.getMessage());
            setVisibility(GONE);
            e.printStackTrace();
        }
    }


}

/**
 *
 */
interface IDisplayMask {
    /**
     * loading中
     */
    void showLoadingMask();

    /**
     * 无网,先统一走加载失败
     */
    void showPoolNetMask();

    /**
     * 加载失败
     */
    void showLoadFailMask(ICallback onRetryCallback);

    /**
     * 未登录，原生内容部分，使用概率小
     *
     * @param loginAccessHint 引导登录入口
     */
    void showUnLoginMask(CharSequence loginAccessHint);

    /**
     * 关闭
     */
    void close();
}
