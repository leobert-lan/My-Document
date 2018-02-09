package com.lht.creationspace.customview.popup.dialog;

import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lht.creationspace.R;
import com.lht.creationspace.customview.popup.IPopupHolder;

/**
 * @author leobert.lan
 * @version 1.0
 *          CustomDialog
 *          TODO
 *          2016年1月6日 上午9:50:10
 */
public class CustomDialog3 extends CustomDialog {

    private TextView content;

    private TextView negativeBtn;

    private TextView milldeBtn;

    private TextView positiveBtn;

    private TextView txtTitle;

    private View contentView;

    private FrameLayout contentContainer;

    public CustomDialog3(IPopupHolder iPopupHolder) {
        super(iPopupHolder);
    }

    @Override
    protected void init() {
        super.doDefaultSetting();

        setyOffset(-100);

        LayoutInflater inflater = LayoutInflater.from(mActivity);

        contentView = inflater.inflate(R.layout.view_dialog_three, null);

        setContentView(contentView);

        ColorDrawable cd = new ColorDrawable(0x000000);
        this.setBackgroundDrawable(cd);

        negativeBtn = (TextView) contentView
                .findViewById(R.id.dialog3_negativebtn);
        milldeBtn = (TextView) contentView.findViewById(R.id.dialog3_middlebtn);
        positiveBtn = (TextView) contentView
                .findViewById(R.id.dialog3_positivebtn);

        contentContainer = (FrameLayout) contentView
                .findViewById(R.id.dialog3_content_containner);
        content = (TextView) contentView.findViewById(R.id.dialog3_content);
        txtTitle = (TextView) contentView.findViewById(R.id.dialog3_title);
        negativeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                CustomDialog3.this.dismiss();
                if (negativeClickListener != null)
                    negativeClickListener.onNegativeClick();
            }
        });

        milldeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog3.this.dismiss();
                if (onMiddleClickListener != null) {
                    onMiddleClickListener.onMiddleClick();
                }
            }
        });

        positiveBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CustomDialog3.this.dismiss();
                if (positiveClickListener != null)
                    positiveClickListener.onPositiveClick();
            }
        });
    }

    @Override
    @Deprecated
    public void showSingle() {
    }

    @Override
    public boolean isSingleView() {
        return false;
    }

    @Override
    public void changeView2Single() {
    }

    private OnPositiveClickListener positiveClickListener = null;

    private OnNegativeClickListener negativeClickListener = null;

    private OnMiddleClickListener onMiddleClickListener = null;

    @Override
    public OnPositiveClickListener getPositiveClickListener() {
        return positiveClickListener;
    }

    @Override
    public void setPositiveClickListener(
            OnPositiveClickListener positiveClickListener) {
        this.positiveClickListener = positiveClickListener;
    }

    @Override
    public OnNegativeClickListener getNegativeClickListener() {
        return negativeClickListener;
    }

    @Override
    public void setNegativeClickListener(
            OnNegativeClickListener negativeClickListener) {
        this.negativeClickListener = negativeClickListener;
    }

    public OnMiddleClickListener getOnMiddleClickListener() {
        return onMiddleClickListener;
    }

    public void setOnMiddleClickListener(OnMiddleClickListener onMiddleClickListener) {
        this.onMiddleClickListener = onMiddleClickListener;
    }

    @Override
    public void setTitle(String s) {
        txtTitle.setVisibility(View.VISIBLE);
        txtTitle.setText(s);
    }

    @Override
    public void setTitle(int rid) {
        String s = mActivity.getResources().getString(rid);
        this.setTitle(s);
    }

    @Override
    public void setContent(String s) {
        content.setText(s);
    }

    @Override
    public void setContent(int rid) {
        String s = mActivity.getResources().getString(rid);
        this.setContent(s);
    }

    @Override
    public void setPositiveButton(String text) {
        positiveBtn.setText(text);
    }

    @Override
    public void setPositiveButton(int rid) {
        String s = mActivity.getResources().getString(rid);
        this.setPositiveButton(s);
    }

    public void setMiddleButton(String text) {
        milldeBtn.setText(text);
    }

    public void setMiddleButton(int rid) {
        String s = mActivity.getResources().getString(rid);
        this.setMiddleButton(s);
    }

    @Override
    public void setNegativeButton(String text) {
        negativeBtn.setText(text);
    }

    @Override
    public void setNegativeButton(int rid) {
        String s = mActivity.getResources().getString(rid);
        this.setNegativeButton(s);
    }

    @Override
    public void setOnDismissListener(OnDismissListener onDismissListener) {
        throw new IllegalAccessError("never use this method");
    }

    /**
     * @param v changeInnerContent
     *          默认的弹窗内部是一个文本框，可以使用布局或者View替换，
     *          leobert.lan
     */
    public void changeInnerContent(View v) {
        contentContainer.removeAllViews();
        contentContainer.addView(v);
    }

    public interface OnMiddleClickListener {
        void onMiddleClick();
    }
}

