package com.lht.ptrlib.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lht.creationspace.R;
import com.lht.ptrlib.library.internal.AbsLoadingLayout;

/**
 * <p><b>Package:</b> com.lht.ptrlib.library </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> PtrLoadingLayoutTheme1 </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/3/26.
 */

public class PtrLoadingLayoutTheme1 extends AbsLoadingLayout {
    static final int FLIP_ANIMATION_DURATION = 150;
    private final Animation mRotateAnimation;
    private final Animation mResetRotateAnimation;
    private ImageView mHeaderImage;
    private ProgressBar mHeaderProgress;

    private final TextView mHeaderText;
    private final TextView mSubHeaderText;

    private PullToRefreshBase.Orientation mScrollDirection;

    private boolean mUseIntrinsicAnimation;


    private FrameLayout mInnerLayout;

    private PullToRefreshBase.Mode mMode;

    private CharSequence mPullLabel;
    private CharSequence mRefreshingLabel;
    private CharSequence mReleaseLabel;

    public PtrLoadingLayoutTheme1(Context context, PullToRefreshBase.Mode mode,
                                  PullToRefreshBase.Orientation scrollDirection,
                                  TypedArray attrs) {
        super(context);
// super(context, mode, scrollDirection, attrs);
        this.mMode = mode;
        this.mScrollDirection = scrollDirection;
        LayoutInflater.from(context).inflate(R.layout.view_ptr_head, this);


        mHeaderImage = (ImageView) findViewById(R.id.head_arrowImageView);
        mHeaderProgress = (ProgressBar) findViewById(R.id.head_progressBar);
        mInnerLayout = (FrameLayout) findViewById(R.id.head_contentLayout);
        mHeaderText = (TextView) findViewById(R.id.head_tipsTextView);
        mSubHeaderText = (TextView) findViewById(R.id.head_lastUpdatedTextView);

        int rotateAngle = mode == PullToRefreshBase.Mode.PULL_FROM_START ? -180 : 180;
        this.mRotateAnimation = new RotateAnimation(0.0F, (float) rotateAngle, 1, 0.5F, 1, 0.5F);
        this.mRotateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        this.mRotateAnimation.setDuration(FLIP_ANIMATION_DURATION);
        this.mRotateAnimation.setFillAfter(true);

        this.mResetRotateAnimation = new RotateAnimation((float) rotateAngle, 0.0F, 1, 0.5F, 1, 0.5F);
        this.mResetRotateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        this.mResetRotateAnimation.setDuration(FLIP_ANIMATION_DURATION);
        this.mResetRotateAnimation.setFillAfter(true);
        LayoutParams lp = (LayoutParams) this.mInnerLayout.getLayoutParams();
        switch (mode) {
            case PULL_FROM_END:
                lp.gravity = Gravity.TOP;

                // Load in labels
                mPullLabel = context.getString(com.lht.ptrlib.R.string.pull_to_refresh_from_bottom_pull_label);
                mRefreshingLabel = "正在加载…";
                mReleaseLabel = context.getString(com.lht.ptrlib.R.string.pull_to_refresh_from_bottom_release_label);
                break;

            case PULL_FROM_START:
            default:
                lp.gravity = Gravity.BOTTOM;

                // Load in labels
                mPullLabel = context.getString(com.lht.ptrlib.R.string.pull_to_refresh_pull_label);
                mRefreshingLabel = "正在加载…";
                mReleaseLabel = context.getString(com.lht.ptrlib.R.string.pull_to_refresh_release_label);
                break;
        }
    }

    protected void onLoadingDrawableSet(Drawable imageDrawable) {
        if (null != imageDrawable) {
            int dHeight = imageDrawable.getIntrinsicHeight();
            int dWidth = imageDrawable.getIntrinsicWidth();
            ViewGroup.LayoutParams lp = this.mHeaderImage.getLayoutParams();
            lp.width = lp.height = Math.max(dHeight, dWidth);
            this.mHeaderImage.requestLayout();
            this.mHeaderImage.setScaleType(ImageView.ScaleType.MATRIX);
            Matrix matrix = new Matrix();
            matrix.postTranslate((float) (lp.width - dWidth) / 2.0F, (float) (lp.height - dHeight) / 2.0F);
            matrix.postRotate(getDrawableRotationAngle(), (float) lp.width / 2.0F, (float) lp.height / 2.0F);
            this.mHeaderImage.setImageMatrix(matrix);
        }
        //ignore
    }

    protected void onPullImpl(float scaleOfLayout) {
    }

    protected void pullToRefreshImpl() {
        if (this.mRotateAnimation == this.mHeaderImage.getAnimation()) {
            this.mHeaderImage.startAnimation(this.mResetRotateAnimation);
        }
    }

    protected void refreshingImpl() {
        this.mHeaderImage.clearAnimation();
        this.mHeaderImage.setVisibility(INVISIBLE);
        this.mHeaderProgress.setVisibility(VISIBLE);
    }

    protected void releaseToRefreshImpl() {
        this.mHeaderImage.startAnimation(this.mRotateAnimation);
    }

    protected void resetImpl() {
        this.mHeaderImage.clearAnimation();
        this.mHeaderProgress.setVisibility(GONE);
        this.mHeaderImage.setVisibility(VISIBLE);
    }

    @Override
    public final void setHeight(int height) {
        android.view.ViewGroup.LayoutParams lp = this.getLayoutParams();
        lp.height = height;
        this.requestLayout();
    }

    @Override
    public final void setWidth(int width) {
        android.view.ViewGroup.LayoutParams lp = this.getLayoutParams();
        lp.width = width;
        this.requestLayout();
    }

    @Override
    public int getContentSize() {
        return this.mInnerLayout.getHeight();
    }

    @Override
    public void hideAllViews() {

    }

    @Override
    public void onPull(float scaleOfLayout) {
        if (!this.mUseIntrinsicAnimation) {
            this.onPullImpl(scaleOfLayout);
        }
    }

    public final void pullToRefresh() {
        if (null != mHeaderText) {
            mHeaderText.setText(mPullLabel);
        }

        // Now call the callback
        pullToRefreshImpl();
    }

    public final void refreshing() {
        if (null != mHeaderText) {
            mHeaderText.setText(mRefreshingLabel);
        }

        if (mUseIntrinsicAnimation) {
            ((AnimationDrawable) mHeaderImage.getDrawable()).start();
        } else {
            // Now call the callback
            refreshingImpl();
        }

        if (null != mSubHeaderText) {
            mSubHeaderText.setVisibility(View.GONE);
        }
    }

    public final void releaseToRefresh() {
        if (null != mHeaderText) {
            mHeaderText.setText(mReleaseLabel);
        }

        // Now call the callback
        releaseToRefreshImpl();
    }

    public final void reset() {
        if (null != mHeaderText) {
            mHeaderText.setText(mPullLabel);
        }
        mHeaderImage.setVisibility(View.VISIBLE);

        if (mUseIntrinsicAnimation) {
            ((AnimationDrawable) mHeaderImage.getDrawable()).stop();
        } else {
            // Now call the callback
            resetImpl();
        }

        if (null != mSubHeaderText) {
            if (TextUtils.isEmpty(mSubHeaderText.getText())) {
                mSubHeaderText.setVisibility(View.GONE);
            } else {
                mSubHeaderText.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void showInvisibleViews() {
        if(INVISIBLE == this.mHeaderText.getVisibility()) {
            this.mHeaderText.setVisibility(VISIBLE);
        }

        if (INVISIBLE == this.mHeaderProgress.getVisibility()) {
            this.mHeaderProgress.setVisibility(VISIBLE);
        }

        if (INVISIBLE == this.mHeaderImage.getVisibility()) {
            this.mHeaderImage.setVisibility(VISIBLE);
        }

        if(INVISIBLE == this.mSubHeaderText.getVisibility()) {
            this.mSubHeaderText.setVisibility(VISIBLE);
        }

    }

    protected int getDefaultDrawableResId() {
        //ignore
        return 0;
    }

    @Override
    public void setLastUpdatedLabel(CharSequence charSequence) {
        this.setSubHeaderText(charSequence);
    }

    public final void setLoadingDrawable(Drawable imageDrawable) {
        this.mHeaderImage.setImageDrawable(imageDrawable);
        this.mUseIntrinsicAnimation = imageDrawable instanceof AnimationDrawable;
        this.onLoadingDrawableSet(imageDrawable);
    }

    public void setPullLabel(CharSequence pullLabel) {
        this.mPullLabel = pullLabel;
    }

    public void setRefreshingLabel(CharSequence refreshingLabel) {
        this.mRefreshingLabel = refreshingLabel;
    }

    public void setReleaseLabel(CharSequence releaseLabel) {
        this.mReleaseLabel = releaseLabel;
    }

    public void setTextTypeface(Typeface tf) {
        this.mHeaderText.setTypeface(tf);
    }



    private float getDrawableRotationAngle() {
        float angle = 0f;
        switch (mMode) {
            case PULL_FROM_END:
                if (mScrollDirection == PullToRefreshBase.Orientation.HORIZONTAL) {
                    angle = 90f;
                } else {
                    angle = 180f;
                }
                break;

            case PULL_FROM_START:
                if (mScrollDirection == PullToRefreshBase.Orientation.HORIZONTAL) {
                    angle = 270f;
                }
                break;

            default:
                break;
        }

        return angle;
    }

    private void setSubHeaderText(CharSequence label) {
        if(null != this.mSubHeaderText) {
            if(TextUtils.isEmpty(label)) {
                this.mSubHeaderText.setVisibility(GONE);
            } else {
                this.mSubHeaderText.setText(label);
                this.mSubHeaderText.setVisibility(VISIBLE);
            }
        }

    }
}
