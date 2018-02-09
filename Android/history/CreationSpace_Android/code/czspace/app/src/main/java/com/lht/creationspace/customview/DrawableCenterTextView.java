package com.lht.creationspace.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * @ClassName: DrawableCenterTextView
 * @Description: TODO
 * @date 2016年5月31日 上午9:43:04
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class DrawableCenterTextView extends AppCompatTextView {

	public DrawableCenterTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public DrawableCenterTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DrawableCenterTextView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Drawable[] drawables = getCompoundDrawables();
		if (drawables != null) {
			Drawable drawableLeft = drawables[0];
			if (drawableLeft != null) {
				float textWidth = getPaint().measureText(getText().toString());
				int drawablePadding = getCompoundDrawablePadding();
				int drawableWidth = drawableLeft.getIntrinsicWidth();
				float bodyWidth = textWidth + drawableWidth + drawablePadding;

				// int drawableHeight = 0;
				// drawableHeight = drawableLeft.getIntrinsicHeight();
				//
				// float bodyHeight = drawableHeight;// + drawablePadding;

				canvas.translate((getWidth() - bodyWidth) / 2, 0);
			}
		}
		super.onDraw(canvas);
	}
}
