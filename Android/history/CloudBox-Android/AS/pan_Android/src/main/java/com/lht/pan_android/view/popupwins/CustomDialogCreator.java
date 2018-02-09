package com.lht.pan_android.view.popupwins;

import com.lht.pan_android.Interface.IPreventPenetrate;
import com.lht.pan_android.view.popupwins.CustomPopupWindow.OnNegativeClickListener;
import com.lht.pan_android.view.popupwins.CustomPopupWindow.OnPositiveClickListener;

import android.app.Activity;
import android.view.View;

/**
 * @ClassName: CustomDialogCreator
 * @Description: TODO
 * @date 2016年3月30日 下午1:28:45
 * 
 * @author leobert.lan
 * @version 1.0
 */
public abstract class CustomDialogCreator {

	final Builder builder;

	CustomDialogCreator(Activity activity, IPreventPenetrate ippl) {
		builder = new Builder();
		builder.setActivity(activity);
		builder.setIppl(ippl);
		setDefault();
	}

	abstract void setDefault();

	public Builder setContent(int rid) {
		String s = builder.getActivity().getResources().getString(rid);
		builder.setContent(s);
		return builder;
	}

	public Builder setPositiveButton(String text) {
		builder.setPositiveStr(text);
		return builder;
	}

	public Builder setPositiveButton(int rid) {
		String s = builder.getActivity().getResources().getString(rid);
		builder.setPositiveStr(s);
		return builder;
	}

	public Builder setNegativeButton(String text) {
		builder.setNegativeStr(text);
		return builder;
	}

	public Builder setNegativeButton(int rid) {
		String s = builder.getActivity().getResources().getString(rid);
		builder.setNegativeStr(s);
		return builder;
	}

	public Builder setPositiveClickListener(OnPositiveClickListener positiveClickListener) {
		builder.setPositiveClickListener(positiveClickListener);
		return builder;
	}

	public Builder setNegativeClickListener(OnNegativeClickListener negativeClickListener) {
		builder.setNegativeClickListener(negativeClickListener);
		return builder;
	}

	public Builder changeInnerContent(View v) {
		builder.setContentWaitChange(v);
		return builder;
	}

	public CustomDialog create() {
		return builder.create();
	}

	public void show() {
		builder.create().show();
	}

}
