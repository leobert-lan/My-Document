package com.lht.creationspace.customview.popup;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.PopupWindow;

import com.lht.creationspace.R;
import com.lht.creationspace.util.debug.DLog;

import java.util.ArrayList;

/**
 * @author leobert.lan
 * @version 1.0
 * @ClassName: CustomPopupWindow
 * @Description: TODO
 * @date 2016年3月1日 上午9:09:46
 */
public abstract class CustomPopupWindow extends PopupWindow {

    protected final Activity mActivity;

    protected final IPopupHolder iPopupHolder;

    public CustomPopupWindow(final IPopupHolder iPopupHolder) {
        this.iPopupHolder = iPopupHolder;
        mActivity = iPopupHolder.getHolderActivity();
        super.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                if (iPopupHolder != null) {
                    iPopupHolder.setPenetrable(mActivity, false);
                }

                for (int i = 0; i < addedOnDismissListeners.size(); i++) {
                    OnDismissListener dismissListener = addedOnDismissListeners.get(i);
                    dismissListener.onDismiss();
                    if (dismissListener instanceof OneTimeOnDismissListener) {
                        addedOnDismissListeners.remove(dismissListener);
                    }
                }
            }
        });
        init();
    }

    private final ArrayList<OnDismissListener> addedOnDismissListeners = new ArrayList<>();

    public void addOnDissmissListener(OnDismissListener onDismissListener) {
        addedOnDismissListeners.add(onDismissListener);
    }

//    protected ArrayList<OnDismissListener> getAddedOnDissmissListener() {
//        return addedOnDismissListeners;
//    }

    public Activity getmActivity() {
        return mActivity;
    }


    protected abstract void init();

    protected void doDefaultSetting() {
        this.setHeight(getMyHeight());
        this.setWidth(getMyWidth());
        this.setFocusable(false);
        this.setOutsideTouchable(false);
        this.setAnimationStyle(getMyAnim());
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        ColorDrawable dw = new ColorDrawable(getCustomBackgroundDrawableColor());
        this.setBackgroundDrawable(dw);
    }

    protected int getMyAnim() {
        return R.style.iOSActionSheet;
    }

    protected int getMyWidth() {
        return mActivity.getWindowManager().getDefaultDisplay()
                .getWidth() * 3 / 4 + 100;
    }

    protected int getMyHeight() {
        return LayoutParams.WRAP_CONTENT;
    }

    /**
     * @Title: setOutsideClickDismiss
     * @Description: 设置点击外部消失
     * @author: leobert.lan
     */
    public void setOutsideClickDismiss() {
        if (this.isShowing()) {
            DLog.e(getClass(), new DLog.LogLocation(), "该方法需要在显示前调用");
        } else {
            this.setFocusable(true);
            ColorDrawable dw = new ColorDrawable(getCustomBackgroundDrawableColor());
            this.setBackgroundDrawable(dw);
        }
    }

    protected int getCustomBackgroundDrawableColor() {
        return 0xb0000000;
    }

    public IPopupHolder getiPopupHolder() {
        return iPopupHolder;
    }

    protected void backgroundAlpha(float alpha) {
//        if (alpha<1) {
//            mActivity.getWindow().getDecorView().setAlpha(0.8f);
//        } else {
//            mActivity.getWindow().getDecorView().setAlpha(alpha);
//        }

        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = alpha;
        mActivity.getWindow().setAttributes(lp);
    }

    private int yOffset = 0;

    public int getyOffset() {
        return yOffset;
    }

    public void setyOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    public void show(View parent) {
        backgroundAlpha(0.6f);
        if (!this.isShowing())
            this.showAtLocation(parent, defaultGravity, 0, yOffset);
        if (iPopupHolder != null) {
            iPopupHolder.setPenetrable(mActivity, true);
        }
    }

    public void showBelow(View target) {
//        backgroundAlpha(0.6f);
        if (!this.isShowing())
            this.showAsDropDown(target, 0, 0, defaultGravity);
        if (iPopupHolder != null) {
            iPopupHolder.setPenetrable(mActivity, true);
        }
    }

    public void showAsDropDown(final View anchor, final int xoff, final int yoff, final int gravity) {
        if (hasPrevious()) {
            iPopupHolder.getLatestPopupWindow().addOnDissmissListener(new OneTimeOnDismissListener() {
                @Override
                public void onDismiss() {
//                    backgroundAlpha(0.6f);
                    callSuperShowAsDropDown(anchor, xoff, yoff, gravity);
                    iPopupHolder.setLatestPopupWindow(CustomPopupWindow.this);
                    onShow();
                }
            });
            iPopupHolder.closePopupWindow();
        } else {
//            backgroundAlpha(0.6f);
            super.showAsDropDown(anchor, xoff, yoff, gravity);
            iPopupHolder.setLatestPopupWindow(this);
            onShow();
        }
    }

    private void callSuperShowAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        super.showAsDropDown(anchor, xoff, yoff, gravity);
    }

    private boolean hasPrevious() {
        return iPopupHolder.getLatestPopupWindow() != null;
    }

    private int defaultGravity = Gravity.CENTER;

    public void setGravity(int gravity) {
        this.defaultGravity = gravity;
    }

    public int getGravity() {
        return defaultGravity;
    }

    @Override
    public void showAtLocation(final View parent,
                               final int gravity, final int x, final int y) {
        if (hasPrevious()) {
            iPopupHolder.getLatestPopupWindow().addOnDissmissListener(new OneTimeOnDismissListener() {
                @Override
                public void onDismiss() {
                    backgroundAlpha(0.6f);
                    iPopupHolder.setLatestPopupWindow(CustomPopupWindow.this);
                    onShow();
                    callSuperShowAtLocation(parent, gravity, x, y);
                }
            });
            iPopupHolder.closePopupWindow();
        } else {
            backgroundAlpha(0.6f);
            iPopupHolder.setLatestPopupWindow(this);
            onShow();
            super.showAtLocation(parent, gravity, x, y);
        }

    }

    private void callSuperShowAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
    }


    protected void onShow() {

    }

    protected Animation dropdownAnimation(long animDuration) {
        Animation anim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation
                .RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        anim.setDuration(animDuration);
        anim.setInterpolator(new LinearInterpolator());
        return anim;
    }


    protected Animation slideUpAnimation(long animDuration) {
        Animation anim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation
                .RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        anim.setDuration(animDuration);
        anim.setInterpolator(new LinearInterpolator());
        return anim;
    }


    @Override
    public void dismiss() {
        iPopupHolder.setLatestPopupWindow(null);
        backgroundAlpha(1f);
        if (iPopupHolder != null) {
            iPopupHolder.setPenetrable(mActivity, false);
        }
        super.dismiss();
    }

    public void show() {
        show(mActivity.getWindow().getDecorView());
    }

    public interface OnPositiveClickListener {
        void onPositiveClick();
    }

    public interface OnNegativeClickListener {
        void onNegativeClick();
    }

    public abstract class OneTimeOnDismissListener implements OnDismissListener {

    }

}
