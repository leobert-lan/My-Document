package com.lht.pan_android.activity.asyncProtected;

import com.lht.pan_android.Interface.IAsyncWithProgressbar;
import com.lht.pan_android.activity.BaseActivity;
import com.lht.pan_android.util.CloudBoxApplication;
import com.lht.pan_android.util.DLog;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

public abstract class AsyncProtectedActivity extends BaseActivity implements IAsyncWithProgressbar {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CloudBoxApplication.addActivity(this);
	}

	private boolean needDispatch = true;

	protected abstract ProgressBar getProgressBar();

	public void setActiveStateOfDispatchOnTouch(boolean b) {
		needDispatch = b;
	}

	public void showWaitView(boolean isProtectNeed) {
		if (getProgressBar() == null) {
			DLog.d(getClass(), "getProgressBar returns null check it");
			return;
		}
		getProgressBar().setVisibility(View.VISIBLE);
		getProgressBar().bringToFront();
		if (isProtectNeed)
			setActiveStateOfDispatchOnTouch(false);
	}

	public void cancelWaitView() {
		getProgressBar().setVisibility(View.GONE);
		// TODO 2.5.0
		if (pw == null)
			setActiveStateOfDispatchOnTouch(true);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (needDispatch) {
			return super.dispatchTouchEvent(ev);
		} else {
			return false;
		}
	}

	@Override
	public void onBackPressed() {
		if (pw != null)
			pw.dismiss();
		else
			super.onBackPressed();
	}

	@Override
	public void showProgressBarOnAsync(boolean isProtectedNeed) {
		showWaitView(isProtectedNeed);
	}

	@Override
	public void cancelProgressBarOnAsyncFinish() {
		cancelWaitView();
	}

	@Override
	public void preventPenetrate(Activity activity, boolean isProtectNeed) {
		if (activity instanceof AsyncProtectedActivity)
			setActiveStateOfDispatchOnTouch(!isProtectNeed);
	}

}
