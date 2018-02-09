package com.lht.creationspace.customview.popup.dialog;

import android.view.View;

import com.lht.creationspace.customview.popup.CustomPopupWindow;
import com.lht.creationspace.customview.popup.IPopupHolder;

/**
 * @ClassName: CustomDialogCreator
 * @Description: TODO
 * @date 2016年3月30日 下午1:28:45
 * 
 * @author leobert.lan
 * @version 1.0
 */
public abstract class CustomDialogCreator {

	final CustomDialog.Builder builder;

	CustomDialogCreator(IPopupHolder iPopupHolder) {
		builder = new CustomDialog.Builder();
		builder.setiPopupHolder(iPopupHolder);
		setDefault();
	}
	
	abstract void setDefault();
	
	public CustomDialog.Builder setContent(int rid) {
		String s = builder.getiPopupHolder().getHolderActivity().getResources().getString(rid);
		builder.setContent(s);
		return builder;
	}

	public CustomDialog.Builder setPositiveButton(String text) {
		builder.setPositiveStr(text);
		return builder;
	}

	public CustomDialog.Builder setPositiveButton(int rid) {
		String s = builder.getiPopupHolder().getHolderActivity().getResources().getString(rid);
		builder.setPositiveStr(s);
		return builder;
	}

	public CustomDialog.Builder setNegativeButton(String text) {
		builder.setNegativeStr(text);
		return builder;
	}

	public CustomDialog.Builder setNegativeButton(int rid) {
		String s = builder.getiPopupHolder().getHolderActivity().getResources().getString(rid);
		builder.setNegativeStr(s);
		return builder;
	}
	
	public CustomDialog.Builder setPositiveClickListener(
			CustomPopupWindow.OnPositiveClickListener positiveClickListener) {
		builder.setPositiveClickListener(positiveClickListener);
		return builder;
	}

	public CustomDialog.Builder setNegativeClickListener(
			CustomPopupWindow.OnNegativeClickListener negativeClickListener) {
		builder.setNegativeClickListener(negativeClickListener);
		return builder;
	}
	
	public CustomDialog.Builder changeInnerContent(View v) {
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
