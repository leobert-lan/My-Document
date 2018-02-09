package com.lht.lhtwebviewlib.base;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.widget.FrameLayout;

import com.lht.lhtwebviewlib.base.Interface.IMediaTrans;

public class IMediaTransImpl implements IMediaTrans {

	private final Activity mActivity;
	private View mCustomView;
	private int mOriginalSystemUiVisibility;
	private int mOriginalOrientation;
	private CustomViewCallback mCustomViewCallback;
	protected FrameLayout mFullscreenContainer;

	public IMediaTransImpl(Activity activity,FrameLayout fullContainer) {
		this.mActivity = activity;
		this.mFullscreenContainer = fullContainer;
	}

	@Override
	public Activity getActivity() {
		return mActivity;
	}

	@Override
	public FrameLayout getFullViewContainer() {
		return mFullscreenContainer;
	}

	@Override
	public void onHideCustomView() {
		// 1. Remove the custom view
		FrameLayout decor = (FrameLayout) getActivity().getWindow().getDecorView();
		decor.removeView(mCustomView);
		mCustomView = null;

		// 2. Restore the state to it's original form
		getActivity().getWindow().getDecorView()
				.setSystemUiVisibility(mOriginalSystemUiVisibility);
		getActivity().setRequestedOrientation(mOriginalOrientation);

		// 3. Call the custom view callback
		mCustomViewCallback.onCustomViewHidden();
		mCustomViewCallback = null;
	}

	@Override
	public void onShowCustomView(View view, CustomViewCallback callback) {
		// if a view already exists then immediately terminate the new one
		if (mCustomView != null) {
			onHideCustomView();
			return;
		}

		// 1. Stash the current state
		mCustomView = view;
		mOriginalSystemUiVisibility = getActivity().getWindow().getDecorView().getSystemUiVisibility();
		mOriginalOrientation = getActivity().getRequestedOrientation();

		// 2. Stash the custom view callback
		mCustomViewCallback = callback;

		// 3. Add the custom view to the view hierarchy
		FrameLayout decor = (FrameLayout) getActivity().getWindow().getDecorView();
		decor.addView(mCustomView, new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));


		// 4. Change the state of the window
		getActivity().getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
						View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
						View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
						View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
						View.SYSTEM_UI_FLAG_FULLSCREEN |
						View.SYSTEM_UI_FLAG_IMMERSIVE);
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

//	private void change2Full() {
//		WindowManager.LayoutParams params = getActivity().getWindow()
//				.getAttributes();
//		params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//		getActivity().getWindow().setAttributes(params);
//		getActivity().getWindow().addFlags(
//				WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//
//		getActivity().getActionBar().hide();
//	}
//
//	private WindowManager.LayoutParams defaultLayoutParams;
//
//	private boolean defaultTitleBarVisibility = true;
//
//	private void change2Normal() {
//		getActivity().getWindow().setAttributes(defaultLayoutParams);
//		getActivity().getWindow().clearFlags(
//				WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//
//		if (defaultTitleBarVisibility) {
//			getActivity().getActionBar().show();
//		}
//	}

}
