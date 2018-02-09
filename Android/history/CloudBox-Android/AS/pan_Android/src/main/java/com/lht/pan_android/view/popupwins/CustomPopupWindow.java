package com.lht.pan_android.view.popupwins;

import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IPreventPenetrate;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.util.DLog;
import com.lht.pan_android.util.DLog.LogLocation;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * @ClassName: CustomPopupWindow
 * @Description: TODO
 * @date 2016年3月1日 上午9:09:46
 * 
 * @author leobert.lan
 * @version 1.0
 */
public abstract class CustomPopupWindow extends PopupWindow {

	protected final Activity mActivity;

	protected final IPreventPenetrate iPreventPenetrate;

	public CustomPopupWindow(Activity activity, IPreventPenetrate ippl) {
		mActivity = activity;
		iPreventPenetrate = ippl;
		super.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				if (iPreventPenetrate != null)
					iPreventPenetrate.preventPenetrate(mActivity, false);
			}
		});
		init();
	}

	public Activity getmActivity() {
		return mActivity;
	}

	abstract void init();

	protected void doDefaultSetting() {
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setWidth(mActivity.getWindowManager().getDefaultDisplay().getWidth() * 3 / 4 + 100);
		this.setFocusable(false);
		this.setOutsideTouchable(false);
		this.setAnimationStyle(R.style.AnimationPreview);
		this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		// ColorDrawable dw = new ColorDrawable(0xb0000000);
		ColorDrawable dw = new ColorDrawable(0x000000);
		this.setBackgroundDrawable(dw);
	}

	/**
	 * @Title: setOutsideClickDismiss
	 * @Description: 设置点击外部消失
	 * @author: leobert.lan
	 */
	public void setOutsideClickDismiss() {
		if (this.isShowing()) {
			DLog.e(getClass(), new LogLocation(), "该方法需要在显示前调用");
		} else {
			this.setFocusable(true);
			ColorDrawable dw = new ColorDrawable(0xb0000000);
			this.setBackgroundDrawable(dw);
		}
	}

	public IPreventPenetrate getiPreventPenetrate() {
		return iPreventPenetrate;
	}

	protected void backgroundAplha(float alpha) {
		WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
		lp.alpha = alpha;
		mActivity.getWindow().setAttributes(lp);
	}

	public void show(View parent) {
		backgroundAplha(0.4f);
		if (!this.isShowing())
			this.showAtLocation(parent, defaultGravity, 0, 0);
		if (iPreventPenetrate != null) {
			iPreventPenetrate.preventPenetrate(mActivity, true);
		}
	}

	private int defaultGravity = Gravity.CENTER;

	public void setGravity(int gravity) {
		this.defaultGravity = gravity;
	}

	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		((UMengActivity) mActivity).setLatestPopupWindow(this);
		super.showAtLocation(parent, gravity, x, y);
	}

	@Override
	public void dismiss() {
		if (isShowing()) {
			((UMengActivity) mActivity).setLatestPopupWindow(null);
			backgroundAplha(1f);
			if (iPreventPenetrate != null) {
				iPreventPenetrate.preventPenetrate(mActivity, false);
			}
		}
		super.dismiss();
	}

	public void show() {
		show(mActivity.getWindow().getDecorView());
	}

	public interface OnPositiveClickListener {
		void onPositiveClick();
	}

	public interface OnNegativeClickListener {
		void onNegativeClick();
	}

}
