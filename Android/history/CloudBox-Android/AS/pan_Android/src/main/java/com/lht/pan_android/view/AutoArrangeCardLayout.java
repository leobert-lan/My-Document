package com.lht.pan_android.view;

import java.util.HashMap;

import com.lht.pan_android.R;
import com.lht.pan_android.view.ShareSelectView.ISSV;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * @ClassName: AutoArrangeCardLayout
 * @Description: TODO
 * @date 2016年1月24日 下午5:32:19
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class AutoArrangeCardLayout extends ViewGroup implements ISSV {

	private static final int DEFAULT_HORIZONTAL_SPACING = 5;
	private static final int DEFAULT_VERTICAL_SPACING = 5;

	private int mVerticalSpacing;
	private int mHorizontalSpacing;

	private final Context mContext;

	int line_height = 0;

	public AutoArrangeCardLayout(Context context) {
		super(context);
		mContext = context;
		setHorizontalSpacing(DEFAULT_HORIZONTAL_SPACING);
		setVerticalSpacing(DEFAULT_VERTICAL_SPACING);
	}

	public AutoArrangeCardLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoArrangeCardLayout);
		try {
			mHorizontalSpacing = a.getDimensionPixelSize(R.styleable.AutoArrangeCardLayout_horizontal_spacing,
					DEFAULT_HORIZONTAL_SPACING);
			mVerticalSpacing = a.getDimensionPixelSize(R.styleable.AutoArrangeCardLayout_vertical_spacing,
					DEFAULT_VERTICAL_SPACING);
		} finally {
			a.recycle();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// assert (MeasureSpec.getMode(widthMeasureSpec) !=
		// MeasureSpec.UNSPECIFIED);

		final int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
		int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
		final int count = getChildCount();
		int line_height = 0;

		int xpos = getPaddingLeft();
		int ypos = getPaddingTop();

		int childHeightMeasureSpec;
		if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
			childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
		} else {
			childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}

		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), childHeightMeasureSpec);
				final int childw = child.getMeasuredWidth();
				line_height = Math.max(line_height, child.getMeasuredHeight() + mVerticalSpacing);

				if (xpos + childw > width) {
					xpos = getPaddingLeft();
					ypos += line_height;
				}

				xpos += childw + mHorizontalSpacing;
			}
		}
		this.line_height = line_height;

		if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
			height = ypos + line_height;

		} else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
			if (ypos + line_height < height) {
				height = ypos + line_height;
			}
		}
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int count = getChildCount();
		final int width = r - l;
		int xpos = getPaddingLeft();
		int ypos = getPaddingTop();

		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				final int childw = child.getMeasuredWidth();
				final int childh = child.getMeasuredHeight();
				if (xpos + childw > width) {
					xpos = getPaddingLeft();
					ypos += line_height;
				}
				child.layout(xpos, ypos, xpos + childw, ypos + childh);
				xpos += childw + mHorizontalSpacing;
			}
		}
	}

	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		return p instanceof LayoutParams;
	}

	@Override
	protected LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new LayoutParams(getContext(), attrs);
	}

	@Override
	protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
		return new LayoutParams(p.width, p.height);
	}

	public void setHorizontalSpacing(int pixelSize) {
		mHorizontalSpacing = pixelSize;
	}

	public void setVerticalSpacing(int pixelSize) {
		mVerticalSpacing = pixelSize;
	}

	// ///////////////////////////////////////////////////////

	private SSVCardAdapter ssvCardAdapter;

	@Override
	public void setAdapter(SSVAdapter adapter) {
		if (adapter instanceof SSVCardAdapter) {
			ssvCardAdapter = (SSVCardAdapter) adapter;
			ssvCardAdapter.notifyDataSetChanged();
		} else {
			throw new IllegalArgumentException("give me a instance of ssvcardadapter");
		}
	}

	@Override
	public SSVAdapter getSSVAdapter() {
		return ssvCardAdapter;
	}

	@Override
	public void addView(View child) {
		super.addView(child);
		Animation showAnimation = AnimationUtils.loadAnimation(mContext, R.anim.scale_show);
		child.startAnimation(showAnimation);
	}

	@Override
	public void removeView(View view) {
		Animation dismissAnimation = AnimationUtils.loadAnimation(mContext, R.anim.scale_dismiss);
		view.startAnimation(dismissAnimation);
		super.removeView(view);
	}

	@SuppressLint("UseSparseArrays")
	private HashMap<String, View> viewMap = new HashMap<String, View>();

	public void callAddView(View v, String key) {
		viewMap.put(key, v);
		addView(v);
	}

	// public boolean check

	public void callRemoveViewAt(String key) {
		if (viewMap.containsKey(key)) {
			View v = viewMap.get(key);
			removeView(v);
			viewMap.remove(v);
		}
	}

	public void callRemoveAll() {
		for (String k : viewMap.keySet()) {
			View v = viewMap.get(k);
			removeView(v);
			viewMap.remove(v);
		}
	}

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		// TODO Auto-generated method stub
		return super.drawChild(canvas, child, drawingTime);
	}
}
