package com.lht.cloudjob.customview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lht.cloudjob.R;
import com.lht.cloudjob.interfaces.custompopupwins.IPopupHolder;

import java.util.Locale;

/**
 * @author leobert.lan
 * @version 1.0
 * @ClassName: CustomDialog
 * @Description: 自定义的进度控件
 * @date 2016年1月6日 上午9:50:10
 */
public class CustomProgressView extends CustomPopupWindow {

    private TextView tvProgress;


    private ProgressBar progressBar;

    private View contentView;


    public CustomProgressView(IPopupHolder iPopupHolder) {
        super(iPopupHolder);
    }

    protected void init() {
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(false);
        this.setOutsideTouchable(false);
        this.setAnimationStyle(R.style.iOSActionSheet);
        setyOffset(-100);

        LayoutInflater inflater = LayoutInflater.from(mActivity);
        contentView = inflater.inflate(R.layout.view_custom_progress, null);
        setContentView(contentView);

        progressBar = (ProgressBar) contentView.findViewById(R.id.vcp_pb);
        tvProgress = (TextView) contentView.findViewById(R.id.vcp_tv);
    }

    @Override
    protected void backgroundAlpha(float alpha) {
        // ignore background change
    }

    private static final String DEFAULT_FORMAT = "正在加载(%d%%)";

    public void setProgress(long current, long total) {
        tvProgress.setText(calcProgress(current, total));
    }

    /**
     * for test
     */
    public static String calcProgress(long current, long total) {
        int percent = (int) (current * 100 / total);
        return String.format(Locale.ENGLISH,DEFAULT_FORMAT,percent);
    }

    /**
     * @Title: cancel
     * @Description: 相当于点击取消
     * @author: leobert.lan
     */
    public void cancel() {
        dismiss();
    }

    @Override
    public void setOnDismissListener(OnDismissListener onDismissListener) {
        throw new IllegalAccessError("never use this method");
    }

}

