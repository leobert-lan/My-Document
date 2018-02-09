package com.lht.creationspace.customview.popup.dialog;

import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lht.creationspace.R;
import com.lht.creationspace.customview.popup.CustomPopupWindow;
import com.lht.creationspace.customview.popup.IPopupHolder;

/**
 * @author leobert.lan
 * @version 1.0
 * @ClassName: CustomDialog
 * @Description: TODO
 * @date 2016年1月6日 上午9:50:10
 */
public class CustomDialog extends CustomPopupWindow {

    private TextView content;

    private TextView negativeBtn;

    private TextView positiveBtn;

    private TextView txtTitle;

    private View contentView;

    private FrameLayout contentContainer;

    public CustomDialog(IPopupHolder iPopupHolder) {
        super(iPopupHolder);
    }

    protected void init() {
        super.doDefaultSetting();

        setyOffset(-100);

        LayoutInflater inflater = LayoutInflater.from(mActivity);
        //TODO
        contentView = inflater.inflate(R.layout.dialog_double, null);

        setContentView(contentView);

        ColorDrawable cd = new ColorDrawable(0x000000);
        this.setBackgroundDrawable(cd);

        negativeBtn = (TextView) contentView
                .findViewById(R.id.dialog2_negativebtn);
        positiveBtn = (TextView) contentView
                .findViewById(R.id.dialog2_positivebtn);
        contentContainer = (FrameLayout) contentView
                .findViewById(R.id.dialog2_content_containner);
        content = (TextView) contentView.findViewById(R.id.dialog2_content);
        txtTitle = (TextView) contentView.findViewById(R.id.dialog2_title);
        negativeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                CustomDialog.this.dismiss();
                if (negativeClickListener != null)
                    negativeClickListener.onNegativeClick();
            }
        });

        positiveBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CustomDialog.this.dismiss();
                if (positiveClickListener != null)
                    positiveClickListener.onPositiveClick();
            }
        });
    }

    @Override
    protected int getMyAnim() {
        return R.style.customdialogAnim;
    }

    /**
     * @Title: showSingle
     * @Description: 只显示确定键的视图
     * @author: leobert.lan
     */
    public void showSingle() {
        changeView2Single();
        this.show();
    }

    private boolean isSingleView = false;

    /**
     * @return
     * @Title: isSingleView
     * @Description: 是否是一个按键（取消）的视图
     * @author: leobert.lan
     */
    public boolean isSingleView() {
        return isSingleView;
    }

    public void changeView2Single() {
        isSingleView = true;
        negativeBtn.setVisibility(View.GONE);
    }

    /**
     * @Title: cancel
     * @Description: 相当于点击取消
     * @author: leobert.lan
     */
    public void cancel() {
        negativeBtn.performClick();
    }

    private OnPositiveClickListener positiveClickListener = null;

    private OnNegativeClickListener negativeClickListener = null;

    public OnPositiveClickListener getPositiveClickListener() {
        return positiveClickListener;
    }

    public void setPositiveClickListener(
            OnPositiveClickListener positiveClickListener) {
        this.positiveClickListener = positiveClickListener;
    }

    public OnNegativeClickListener getNegativeClickListener() {
        return negativeClickListener;
    }

    public void setNegativeClickListener(
            OnNegativeClickListener negativeClickListener) {
        this.negativeClickListener = negativeClickListener;
    }

    public void setTitle(String s) {
        txtTitle.setVisibility(View.VISIBLE);
        txtTitle.setText(s);
    }

    public void setTitle(int rid) {
        String s = mActivity.getResources().getString(rid);
        this.setTitle(s);
    }

    public void setContent(String s) {
        content.setText(s);
    }

    public void setContent(int rid) {
        String s = mActivity.getResources().getString(rid);
        this.setContent(s);
    }

    public void setPositiveButton(String text) {
        positiveBtn.setText(text);
    }

    public void setPositiveButton(int rid) {
        String s = mActivity.getResources().getString(rid);
        this.setPositiveButton(s);
    }

    public void setNegativeButton(String text) {
        negativeBtn.setText(text);
    }

    public void setNegativeButton(int rid) {
        String s = mActivity.getResources().getString(rid);
        this.setNegativeButton(s);
    }

    @Override
    public void setOnDismissListener(OnDismissListener onDismissListener) {
        throw new IllegalAccessError("never use this method");
    }

    /**
     * @param v
     * @Title: changeInnerContent
     * @Description: 默认的弹窗内部是一个文本框，可以使用布局或者View替换，
     * @author: leobert.lan
     */
    public void changeInnerContent(View v) {
        contentContainer.removeAllViews();
        contentContainer.addView(v);
    }

    public static class Builder {
        IPopupHolder iPopupHolder;

        String content;

        String positiveStr;

        String negativeStr;

        CustomPopupWindow.OnPositiveClickListener positiveClickListener = null;

        CustomPopupWindow.OnNegativeClickListener negativeClickListener = null;

        View contentWaitChange = null;

        public IPopupHolder getiPopupHolder() {
            return iPopupHolder;
        }

        public void setiPopupHolder(IPopupHolder iPopupHolder) {
            this.iPopupHolder = iPopupHolder;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getPositiveStr() {
            return positiveStr;
        }

        public void setPositiveStr(String positiveStr) {
            this.positiveStr = positiveStr;
        }

        public String getNegativeStr() {
            return negativeStr;
        }

        public void setNegativeStr(String negativeStr) {
            this.negativeStr = negativeStr;
        }

        public CustomPopupWindow.OnPositiveClickListener getPositiveClickListener() {
            return positiveClickListener;
        }

        public void setPositiveClickListener(
                CustomPopupWindow.OnPositiveClickListener positiveClickListener) {
            this.positiveClickListener = positiveClickListener;
        }

        public CustomPopupWindow.OnNegativeClickListener getNegativeClickListener() {
            return negativeClickListener;
        }

        public void setNegativeClickListener(
                CustomPopupWindow.OnNegativeClickListener negativeClickListener) {
            this.negativeClickListener = negativeClickListener;
        }

        public View getContentWaitChange() {
            return contentWaitChange;
        }

        public void setContentWaitChange(View contentWaitChange) {
            this.contentWaitChange = contentWaitChange;
        }

        public CustomDialog create() {
            CustomDialog dialog = new CustomDialog(this.getiPopupHolder());
            dialog.setContent(this.getContent());
            dialog.setNegativeButton(this.getNegativeStr());
            dialog.setPositiveButton(this.getPositiveStr());
            dialog.setPositiveClickListener(this.getPositiveClickListener());
            dialog.setNegativeClickListener(this.getNegativeClickListener());
            if (getContentWaitChange() != null)
                dialog.changeInnerContent(contentWaitChange);
            return dialog;
        }
    }

}

