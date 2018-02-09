package com.lht.cloudjob.customview;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lht.cloudjob.R;

/**
 * <p><b>Package</b> com.lht.cloudjob.customview
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> MaskView
 * <p><b>Description</b>: 用于空数据视图、请求失败等
 * <p> Create by Leobert on 2016/8/17
 */
public class MaskView extends FrameLayout {

    private View contentView;

    private ImageView mImageView;

    private TextView mTextView;

    public MaskView(Context context) {
        super(context);
        init();
    }

    public MaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        contentView = inflate(getContext(), R.layout.view_mask, this);

        mImageView = (ImageView) contentView.findViewById(R.id.mask_icon);
        mTextView = (TextView) contentView.findViewById(R.id.mask_hint);
    }

    /**
     * 通用的空数据列表mask
     * @param hint hint文字
     */
    public void showAsEmpty(CharSequence hint) {
        setVisibility(VISIBLE);
        bringToFront();
        mImageView.setImageResource(R.drawable.v1011_drawable_mask_empty);
        mTextView.setText(hint);
    }

    /**
     * 显示为网络错误的类型
     * @param hint hint文字
     */
    public void showAsNetError(CharSequence hint) {
        setVisibility(VISIBLE);
        bringToFront();
        mImageView.setImageResource(R.drawable.v1011_drawable_mask_refuse);
        mTextView.setText(hint);
    }

    /**
     * 显示网络错误重试视图
     *
     * @param listener 重试事件
     */
    public void showAsNetErrorRetry(OnClickListener listener) {
        setVisibility(VISIBLE);
        bringToFront();
        mImageView.setImageResource(R.drawable.v1011_drawable_mask_refuse);

        //v1010_mask_neterror_retry 网络异常、点击重试
        String s = getResources().getString(R.string.v1010_mask_neterror_retry);
        SpannableStringBuilder builder = new SpannableStringBuilder(s);
        builder.setSpan(newClickSpan(listener), 7, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        int color = Color.argb(255, 45, 126, 226);
        builder.setSpan(new ForegroundColorSpan(color), 7, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        builder.setSpan(new UnderlineSpan(), 7, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTextView.setText(builder);
        mTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void showAsUnLogin(CharSequence hint) {
        setVisibility(VISIBLE);
        bringToFront();
        mImageView.setImageResource(R.drawable.v1011_drawable_mask_unlogin);
        mTextView.setText(hint);
    }

    /**
     * 自己填充
     * @param resid 填充图片
     * @param hint 填充说明文字
     */
    public void showAsCustom(int resid,CharSequence hint) {
        setVisibility(VISIBLE);
        bringToFront();
        mImageView.setImageResource(resid);
        mTextView.setText(hint);
    }

    public void showAsUnlogin(OnClickListener listener) {
        setVisibility(VISIBLE);
        bringToFront();
        mImageView.setImageResource(R.drawable.v1011_drawable_mask_unlogin);
//        v1010_view_mask_text_unlogin 您还未登录，去登录
        String s = getResources().getString(R.string.v1010_view_mask_text_unlogin);
        SpannableStringBuilder builder = new SpannableStringBuilder(s);
        builder.setSpan(newClickSpan(listener), 6, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        int color = Color.argb(255, 45, 126, 226);
        builder.setSpan(new ForegroundColorSpan(color), 6, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        builder.setSpan(new UnderlineSpan(), 6, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTextView.setText(builder);
        mTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private ClickableSpan newClickSpan(final OnClickListener listener) {
        return new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (listener != null)
                    listener.onClick(widget);
            }
        };
    }

    public void hide() {
        setVisibility(GONE);
    }
}
