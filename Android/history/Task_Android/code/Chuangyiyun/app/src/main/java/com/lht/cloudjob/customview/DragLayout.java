package com.lht.cloudjob.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.location.Location;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.lht.cloudjob.util.debug.DLog;

import java.util.HashMap;
import java.util.HashSet;

/**
 * package com.lht.cloudjob.customview
 * project AndroidBase
 * classname DragLayout
 * description: TODO
 *
 * Created by leobert on 2016/4/5.
 */
public class DragLayout extends FrameLayout {

    private View view;

    private int width, heigh;

    private int screenWid, screenHei;

    private boolean isClickDrag = false;

    private boolean isTouchDrag = false;

    private float startX, startY;

    private CheckClick checkClick = new CheckClick();

    private HashMap<View, OnDragChildClickListener> clickListeners = new HashMap<View, OnDragChildClickListener>();

    public void addDragImageListener(View v,
                                     OnDragChildClickListener dragChildClickListener) {
        clickListeners.put(v, dragChildClickListener);
    }

    public OnDragChildClickListener getDragImageListener(View v) {
        if (clickListeners.containsKey(v))
            return clickListeners.get(v);
        return null;
    }

    /**
     * @ClassName: OnDragChildClickListener
     * @Description: 该接口用于内部view的onclick回调，仅限于view，不作用于viewgroup
     * @date 2016年3月23日 上午9:51:16
     *
     * @author leobert.lan
     * @version 1.0
     * @since JDK 1.7
     */
    public interface OnDragChildClickListener {
        void onClick(View v);
    }

    private class CheckClick implements Runnable {

        @Override
        public void run() {
            isClickDrag = false;
        }

    }

    public DragLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void dragInit(View view) {
        screenWid = getWidth();
        screenHei = getHeight();
        width = view.getWidth();
        heigh = view.getHeight();
    }

    private HashSet<View> childrenViews = new HashSet<View>();

    private void findChildren(ViewGroup vg) {
        int childCount = vg.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (vg.getChildAt(i) instanceof ViewGroup) {
                findChildren((ViewGroup) vg.getChildAt(i));
            } else {
                childrenViews.add(vg.getChildAt(i));
            }
        }

    }

    public View getHitView(float x, float y) {
        for (View v : childrenViews) {
            if (v == null)
                // 习惯性
                continue;
            Rect frame = new Rect();
            v.getHitRect(frame);
            if (frame.contains((int) (x), (int) (y))) {
                return v;
            }
        }
        return null;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:
                DLog.d(getClass(), "intercept");
                float x = ev.getX();
                float y = ev.getY();
                Rect frame = new Rect();
                view = getHitView(x, y);
                if (view == null) {
                    DLog.d(getClass(), "inter null");
                    return false;
                }
                dragInit(view);
                // TODO 判断略显冗余
                view.getHitRect(frame);
                if (frame.contains((int) (x), (int) (y))) {
                    isTouchDrag = true;
                    startX = x;
                    startY = y;
                    return true;
                }
                break;

        }
        return false;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        findChildren(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        Rect frame = new Rect();

        // Drawable background = null;

        // TODO

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                if (view == null)
                    return false;
                view.getHitRect(frame);
                if (frame.contains((int) (x), (int) (y))) {
                    startX = x;
                    startY = y;
                    isTouchDrag = true;
                    isClickDrag = true;
                    postDelayed(checkClick, ViewConfiguration.getTapTimeout());
                    // background = view.getBackground();
                    view.setPressed(true);
                    // view.setBackgroundResource(R.color.g2);
                }
                break;
            case MotionEvent.ACTION_MOVE:

                float distanX = Math.abs(x - startX);
                float distanY = Math.abs(y - startY);

                if (Math.sqrt(distanY * distanY + distanX * distanX) > 10) {
                    isClickDrag = false;
                }
                move(x, y);
                break;

            case MotionEvent.ACTION_CANCEL:
                isClickDrag = false;
                isTouchDrag = false;
                break;
            case MotionEvent.ACTION_UP:
                if (isClickDrag == true) {
                    OnDragChildClickListener listener = getDragImageListener(view);
                    if (listener != null)
                        listener.onClick(view);
                    removeCallbacks(checkClick);
                }
                isClickDrag = false;
                isTouchDrag = false;

                // TODO
                view.setPressed(false);
                view = null;

                // 这段是把控件吸附四周
                // if (x > width && x < screenWid - width && y > heigh
                // && y < screenHei - heigh) {
                // int minType = minDistance(x, y);
                // Log.i("tags", screenHei + "==mintype=" + minType);
                // switch (minType) {
                // case LEFT:
                // x = width;
                // break;
                // case RIGHT:
                // x = screenWid - width;
                // break;
                // case TOP:
                // y = heigh;
                // break;
                // case BOTTOM:
                // y = screenHei - heigh;
                // break;
                // default:
                // break;
                // }
                // move(x, y);
                // }
                break;
            case MotionEvent.ACTION_OUTSIDE:
                isClickDrag = false;
                isTouchDrag = false;
                break;
        }
        return true;
    }

    private static final int LEFT = 1;
    private static final int RIGHT = 2;
    private static final int TOP = 3;
    private static final int BOTTOM = 4;

    @SuppressWarnings("unused")
    private int minDistance(float x, float y) {
        DLog.i(getClass(), "x=" + x + "==y=" + y);
        boolean left, top;

        if (x <= (screenWid - x)) {
            left = true;
        } else {
            left = false;
        }
        if (y <= (screenHei - y)) {
            top = true;
        } else {
            top = false;
        }

        if (left && top) {
            if (x <= y) {
                return LEFT;
            } else {
                return TOP;
            }
        }
        if (left && (!top)) {
            if (x <= (screenHei - y)) {
                return LEFT;
            } else {
                return BOTTOM;
            }
        }

        if ((!left) & top) {
            if ((screenWid - x) <= y) {
                return RIGHT;
            } else {
                return TOP;
            }
        }

        if ((!left) & (!top)) {
            if ((screenWid - x) <= (screenHei - y)) {
                return RIGHT;
            } else {
                return BOTTOM;
            }
        }
        return 0;

    }

    /**
     * @Title: move
     * @Description: 很想做一次平滑处理，但是很容易出bug
     * @author: leobert.lan
     * @param x
     * @param y
     */
    private void move(float x, float y) {
        int left = (int) (x - width / 2);
        int top = (int) (y - heigh / 2);
        if (left <= 0)
            left = 0;
        if (top <= 0)
            top = 0;

        if (left > screenWid - width)
            left = screenWid - width;
        if (top > screenHei - heigh)
            top = screenHei - heigh;

        LayoutParams params = (LayoutParams) view
                .getLayoutParams();

        params.setMargins(left, top, (screenWid - left - width), (screenHei
                - top - heigh));

        view.setLayoutParams(params);
        requestLayout();
    }

    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0];
    }

}