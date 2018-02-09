package com.lht.pan_android.view.popupwins;

import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IPreventPenetrate;
import com.lht.pan_android.view.popupwins.CustomPopupWindow.OnNegativeClickListener;
import com.lht.pan_android.view.popupwins.CustomPopupWindow.OnPositiveClickListener;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * @ClassName: CustomDialog
 * @Description: TODO
 * @date 2016年1月6日 上午9:50:10
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class CustomDialog extends CustomPopupWindow {

	private TextView content;

	private TextView negativeBtn;

	private TextView positiveBtn;

	private TextView txtTitle;

	private View contentView;

	private FrameLayout contentContainer;

	public CustomDialog(final Activity activity, IPreventPenetrate ippl) {
		super(activity, ippl);
		super.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				if (iPreventPenetrate != null)
					iPreventPenetrate.preventPenetrate(mActivity, false);
			}
		});
	}

	@Override
	void init() {
		super.doDefaultSetting();

		LayoutInflater inflater = LayoutInflater.from(mActivity);
		contentView = inflater.inflate(R.layout.dialog_double, null);

		setContentView(contentView);

		ColorDrawable cd = new ColorDrawable(0x000000);
		this.setBackgroundDrawable(cd);

		negativeBtn = (TextView) contentView.findViewById(R.id.dialog2_negativebtn);
		positiveBtn = (TextView) contentView.findViewById(R.id.dialog2_positivebtn);
		contentContainer = (FrameLayout) contentView.findViewById(R.id.dialog2_content_containner);
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

	/**
	 * @Title: show
	 * @Description: 显示
	 * @author: leobert.lan
	 * @param parent
	 */
	@Override
	public void show(View parent) {
		// 设置背景效果
		super.show(parent);
		if (iPreventPenetrate != null)
			iPreventPenetrate.preventPenetrate(mActivity, true);
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
	 * @Title: isSingleView
	 * @Description: 是否是一个按键（取消）的视图
	 * @author: leobert.lan
	 * @return
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

	public void setPositiveClickListener(OnPositiveClickListener positiveClickListener) {
		this.positiveClickListener = positiveClickListener;
	}

	public OnNegativeClickListener getNegativeClickListener() {
		return negativeClickListener;
	}

	public void setNegativeClickListener(OnNegativeClickListener negativeClickListener) {
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
	 * @Title: changeInnerContent
	 * @Description: 默认的弹窗内部是一个文本框，可以使用布局或者View替换，
	 * @author: leobert.lan
	 * @param v
	 */
	public void changeInnerContent(View v) {
		contentContainer.removeAllViews();
		contentContainer.addView(v);
	}
}

class Builder {
	Activity Activity;
	IPreventPenetrate ippl;

	String content;

	String positiveStr;

	String negativeStr;

	OnPositiveClickListener positiveClickListener = null;

	OnNegativeClickListener negativeClickListener = null;

	View contentWaitChange = null;

	public Activity getActivity() {
		return Activity;
	}

	public void setActivity(Activity activity) {
		Activity = activity;
	}

	public IPreventPenetrate getIppl() {
		return ippl;
	}

	public void setIppl(IPreventPenetrate ippl) {
		this.ippl = ippl;
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

	public OnPositiveClickListener getPositiveClickListener() {
		return positiveClickListener;
	}

	public void setPositiveClickListener(OnPositiveClickListener positiveClickListener) {
		this.positiveClickListener = positiveClickListener;
	}

	public OnNegativeClickListener getNegativeClickListener() {
		return negativeClickListener;
	}

	public void setNegativeClickListener(OnNegativeClickListener negativeClickListener) {
		this.negativeClickListener = negativeClickListener;
	}

	public View getContentWaitChange() {
		return contentWaitChange;
	}

	public void setContentWaitChange(View contentWaitChange) {
		this.contentWaitChange = contentWaitChange;
	}

	public CustomDialog create() {
		CustomDialog dialog = new CustomDialog(this.getActivity(), this.getIppl());
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
