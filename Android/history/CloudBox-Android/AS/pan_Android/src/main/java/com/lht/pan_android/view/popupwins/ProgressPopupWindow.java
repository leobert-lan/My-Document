package com.lht.pan_android.view.popupwins;

import com.lht.pan_android.R;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @ClassName: ProgressPopupWindow
 * @Description: TODO
 * @date 2016年2月29日 上午11:23:08
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class ProgressPopupWindow extends PopupWindow {

	private Activity mContext;

	private View contentView;

	private ProgressBar progressBar;
	private TextView txtMax, txtProgress;

	private TextView title;

	private RelativeLayout rlOperate;

	private TextView btnCancel;

	public ProgressPopupWindow(final Activity mContext2) {
		mContext = mContext2;
		init();
	}

	private void init() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		contentView = inflater.inflate(R.layout.popwindow_progress, null);
		this.setContentView(contentView);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setWidth(mContext.getWindowManager().getDefaultDisplay().getWidth() * 3 / 4 + 100);
		this.setFocusable(true);
		this.setOutsideTouchable(false);
		this.setAnimationStyle(R.style.AnimationPreview);

		txtProgress = (TextView) contentView.findViewById(R.id.pop_tv_progress);
		progressBar = (ProgressBar) contentView.findViewById(R.id.popup_pb_progress);

		title = (TextView) contentView.findViewById(R.id.popup_pb_title);
		rlOperate = (RelativeLayout) contentView.findViewById(R.id.popup_progerss_rl);

		btnCancel = (TextView) contentView.findViewById(R.id.popup_progress_cancle);

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (cancelListener != null) {
					cancelListener.onCancel();
				}
			}
		});
	}

	private void backgroundAplha(float alpha) {
		WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
		lp.alpha = alpha;
		mContext.getWindow().setAttributes(lp);
	}

	public void show() {
		this.show(contentView);
	}

	public void show(View parent) {
		backgroundAplha(0.4f);
		if (!this.isShowing())
			this.showAtLocation(parent, Gravity.CENTER, 0, 0);
	}

	@Override
	public void dismiss() {
		backgroundAplha(1.0f);
		super.dismiss();
	}

	private double max, progress;

	public void setMax(double max) {
		this.max = max;
	}

	public void setProgress(double progress) {
		this.progress = progress;
		progressBar.setProgress((int) (100 * progress / max));
		txtProgress.setText(String.valueOf(Math.round(100 * (progress / max))) + "%");
	}

	public void setTitle(String title) {
		this.title.setText(title);
	}

	private ICancelListener cancelListener;

	public void setICancelListener(ICancelListener cancelListener) {
		rlOperate.setVisibility(View.VISIBLE);
		this.cancelListener = cancelListener;
	}

	public void setCancelBtn(int resId) {
		btnCancel.setText(resId);
	}

	public void setCancelBtn(String str) {
		btnCancel.setText(str);
	}

	public interface ICancelListener {
		void onCancel();
	}
}
