package com.lht.pan_android.view;

import com.lht.pan_android.activity.ViewPagerItem.TransportManagerActivity;
import com.lht.pan_android.activity.asyncProtected.MainActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

/**
 * @ClassName: LinearListWithAnim
 * @Description: TODO
 * @date 2016年1月10日 上午11:17:54
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class LinearListWithAnim extends LinearLayout {

	private final static int ANIMATION_DURATION = 500;

	public LinearListWithAnim(Context context) {
		super(context);
	}

	public LinearListWithAnim(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LinearListWithAnim(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * @Title: removeOneCell
	 * @Description: 动画移除一个元素
	 * @author: leobert.lan
	 * @param v
	 * @param al
	 */
	public void removeOneCell(final View v, AnimationListener al) {
		this.collapse(v, al);
	}

	/**
	 * @Title: removeOneCell
	 * @Description: 动画移除一个元素
	 * @author: leobert.lan
	 * @param v
	 */
	public void removeOneCell(final View v) {
		this.collapse(v, new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				getIPP().preventPenetrate(getIPP(), true);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				removeView(v);
				getIPP().preventPenetrate(getIPP(), false);
			}
		});
	}

	private MainActivity getIPP() {
		return (MainActivity) ((TransportManagerActivity) getContext()).getParent();
	}

	private void collapse(final View v, AnimationListener al) {
		final int initialHeight = v.getMeasuredHeight();

		Animation anim = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				if (interpolatedTime == 1) {
					v.setVisibility(View.GONE);
				} else {
					v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
					v.requestLayout();
				}
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		if (al != null) {
			anim.setAnimationListener(al);
		}
		anim.setDuration(ANIMATION_DURATION);
		v.startAnimation(anim);
	}

}
