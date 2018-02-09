package com.lht.creationspace.customview.toast;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lht.creationspace.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * <p><b>Package:</b> com.lht.creationspace.customview </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> HeadUpToast </p>
 * <p><b>Description:</b> 自定义toast </p>
 * Created by leobert on 2017/3/7.
 */

public class HeadUpToast {

    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILURE = 1;

    private Toast toast;

    public HeadUpToast(Context context) {
        toast = new Toast(context);
        init(context);
    }

    private View contentView;

    private ImageView icon;

    private TextView tvContent;

    private void init(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        contentView = layoutInflater.inflate(R.layout.view_toast_headup, null);

        icon = (ImageView) contentView.findViewById(R.id.toast_icon);
        tvContent = (TextView) contentView.findViewById(R.id.toast_content);

        toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.TOP, 0, 0);
        toast.setView(contentView);
        toast.setDuration(Toast.LENGTH_SHORT);
        setAnimations(R.style.SlideIn_SlideOut);
        initTN();
    }

    public void show() {
        toast.show();
    }

    public HeadUpToast setContent(int type, String content) {
        icon.setImageResource(getIconByType(type));
        tvContent.setText(content);
        return this;

    }

    private int getIconByType(int type) {
        int drawableResId;
        switch (type) {
            case TYPE_SUCCESS: // 勾
                drawableResId = R.drawable.v1000_drawable_guanzcg;
                break;
            case TYPE_FAILURE: // 叉
                drawableResId = R.drawable.v1000_drawable_toast_failure;
                break;
            default:
                drawableResId = R.drawable.v1000_drawable_guanzcg;
                break;
        }
        return drawableResId;
    }

    public Toast getToast() {
        return toast;
    }

    private int animations = -1;

    private Object mTN;

    private Method show;
    private Method hide;

    public void setAnimations(int animations) {
        this.animations = animations;
    }

    private void initTN() {
        try {
            Field tnField = toast.getClass().getDeclaredField("mTN");
            tnField.setAccessible(true);
            mTN = tnField.get(toast);
            show = mTN.getClass().getMethod("show");
            hide = mTN.getClass().getMethod("hide");

            Field tnParamsField = mTN.getClass().getDeclaredField("mParams");
            tnParamsField.setAccessible(true);
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) tnParamsField.get(mTN);

            /**设置动画*/
            if (animations != -1) {
                params.windowAnimations = animations;
            }
            params.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

            /**调用tn.show()之前一定要先设置mNextView*/
            Field tnNextViewField = mTN.getClass().getDeclaredField("mNextView");
            tnNextViewField.setAccessible(true);
            tnNextViewField.set(mTN, toast.getView());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
