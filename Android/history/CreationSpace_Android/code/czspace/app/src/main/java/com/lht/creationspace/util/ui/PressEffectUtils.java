package com.lht.creationspace.util.ui;

import android.view.MotionEvent;
import android.view.View;

import com.daimajia.androidanimations.library.BaseViewAnimator;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * <p><b>Package</b> com.lht.vsocyy.util.ui
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> HoverEffectUtils
 * <p><b>Description</b>: 悬停效果助手
 * <p>Created by leobert on 2017/2/14.
 */

public class PressEffectUtils {
    public static void bindDefaultPressEffect(View view) {
        view.setOnTouchListener(new DefaultPressEffect());

    }


    private static final class DefaultPressEffect implements View.OnTouchListener {

        YoYo.YoYoString yoYoString;
        boolean resetOnFinish = false;

        private View.OnTouchListener downListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (yoYoString != null) {
                        return false;
                    }
                    yoYoString = YoYo.with(new ZoomAnimator()).duration(300)
                            .withListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    resetOnFinish = false;
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    if (resetOnFinish) {
                                        yoYoString.stop(true);
                                        yoYoString = null;
                                    }
                                    resetOnFinish = false;
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                    yoYoString = null;
                                    resetOnFinish = false;
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {
                                }
                            })
                            .playOn(v);
                }
                return false;
            }
        };

        private View.OnTouchListener upListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (yoYoString != null) {
                        if (yoYoString.isRunning()) {
                            resetOnFinish = true;
                        } else {
                            resetOnFinish = false;
                            yoYoString.stop(true);
                            yoYoString = null;
                        }
                    }
                }
                return false;
            }
        };

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                downListener.onTouch(v, event);
            if (event.getAction() == MotionEvent.ACTION_UP)
                upListener.onTouch(v, event);
            return false;
        }
    }


    private static class ZoomAnimator extends BaseViewAnimator {
        @Override
        protected void prepare(View target) {
            getAnimatorAgent().playTogether(
                    ObjectAnimator.ofFloat(target, "scaleX", 1, 0.9f, 0.8f),
                    ObjectAnimator.ofFloat(target, "scaleY", 1, 0.9f, 0.8f)
            );
        }
    }
}
