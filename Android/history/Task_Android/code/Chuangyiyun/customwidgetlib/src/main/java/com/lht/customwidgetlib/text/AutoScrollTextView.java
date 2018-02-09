package com.lht.customwidgetlib.text;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * <p><b>Package</b> com.lht.customwidgetlib.text
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> AutoScrollTextView
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/28.
 */
public class AutoScrollTextView extends TextView implements View.OnClickListener {
    public static final String TAG = AutoScrollTextView.class.getSimpleName();

    private float textLength = 0f;//文本长度
    private float viewWidth = 0f;
    private float step = 0f;//文字的横坐标
    private float y = 0f;//文字的纵坐标
    public boolean isStarting = false;//是否开始滚动
    private TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);//绘图样式
    private SpannableString text = new SpannableString("");//= "";//文本内容

    private static final float BASIC_PARA_PADDING = 20f;

    public void setSpannaleText(SpannableString text) {
        this.text = text;
    }


    public AutoScrollTextView(Context context) {
        super(context);
        initView();
    }

    public AutoScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public AutoScrollTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        setOnClickListener(this);
    }

    /**
     * 文本初始化，每次更改文本内容或者文本效果等之后都需要重新初始化一下
     */
    public void init(WindowManager windowManager) {
        paint = getPaint();
        String text = getText().toString();
        textLength = paint.measureText(text.toString());
        viewWidth = getWidth();
        if (viewWidth == 0) {
            if (windowManager != null) {
                Display display = windowManager.getDefaultDisplay();
                viewWidth = display.getWidth();
            }
        }
        step = textLength;
        y = getTextSize() + getPaddingTop();

        calcData();
    }

    private void calcData() {
        float textWidth = paint.measureText(text.toString());
        if (textWidth <= 0) {
            return;
        }
        _ParaWidth = textWidth + BASIC_PARA_PADDING; //加上基础的段落留白
        float _doubleTextWithPadding = calcDoubleWidth(_ParaWidth);
        totalNewTextWidth = Math.max(calcDoubleWidth(viewWidth), _doubleTextWithPadding);

        count = ((int) totalNewTextWidth / (int) _ParaWidth);
//        while (count < 2) {
//            totalNewTextWidth = calcDoubleWidth(totalNewTextWidth);
//            count = ((int) totalNewTextWidth / (int) _ParaWidth);
//        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);

        ss.step = step;
        ss.isStarting = isStarting;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        step = ss.step;
        isStarting = ss.isStarting;

    }

    public static class SavedState extends BaseSavedState {
        public boolean isStarting = false;
        public float step = 0.0f;

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeBooleanArray(new boolean[]{isStarting});
            out.writeFloat(step);
        }


        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }
        };

        private SavedState(Parcel in) {
            super(in);
            boolean[] b = null;
            in.readBooleanArray(b);
            if (b != null && b.length > 0)
                isStarting = b[0];
            step = in.readFloat();
        }
    }

    /**
     * 开始滚动
     */
    public void startScroll() {
        isStarting = true;
        startFresh();
    }

    private class FreshTask implements Runnable {

        @Override
        public void run() {
            invalidate();
            startFresh();
        }
    }

    private void startFresh() {
        postDelayed(new FreshTask(), 25);
    }

    /**
     * 停止滚动
     */
    public void stopScroll() {
        isStarting = false;
        invalidate();
    }


    private float calcDoubleWidth(float width) {
        return width * 2;
    }

    float totalNewTextWidth;

    float _ParaWidth; //加上基础的段落留白

    int count;

    @Override
    public void onDraw(Canvas canvas) {
        paint.drawableState = getDrawableState();

        float textWidth = paint.measureText(text.toString());
        if (textWidth <= 0) {
            return;
        }

        int tempLength;

        float paraWidth = totalNewTextWidth / count;
        for (int round = 0; round < count; round++) {
            tempLength = 0;
            for (int i = 0; i < text.length(); i++) {
                String s = text.subSequence(i, i + 1).toString();
                tempLength += paint.measureText(s);
                float _x = viewWidth + paraWidth * round + tempLength - step;


                if (-10 < _x && _x < viewWidth + 10) {
                    ForegroundColorSpan[] spans = text.getSpans(i, i + 1, ForegroundColorSpan.class);
                    if (spans.length > 0) {
                        paint.setColor(spans[0].getForegroundColor());
                    } else {
                        paint.setColor(Color.argb(255, 12, 12, 12));
                    }
                    canvas.drawText(s, _x, y, paint);
                }
            }
        }

        canvas.save();
        canvas.restore();
        if (!isStarting) {
            return;
        }
        step += 2;// 0.5;
        if (step > viewWidth * 2 + paraWidth - textWidth) {
            step = viewWidth;// - paraWidth + textLength;//+ paraWidth * (count - 1);
        }
    }

    @Override
    public void onClick(View v) {
        if (isStarting)
            stopScroll();
        else
            startScroll();
    }

}