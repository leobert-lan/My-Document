package com.lht.pan_android.view.popupwins;

import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IPreventPenetrate;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @ClassName: CustomDialog
 * @Description: 暂不修改
 * @date 2016年1月6日 上午9:50:10
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class AlertDialogWithImageCreator extends CustomDialogCreator {

	private final View defContentView;

	private final ImageView img;

	private final TextView txt;

	public AlertDialogWithImageCreator(final Activity activity, IPreventPenetrate ippl) {
		super(activity, ippl);
		LayoutInflater inflater = LayoutInflater.from(activity);
		defContentView = inflater.inflate(R.layout.alertdialog_withimg_convert, null);

		txt = (TextView) defContentView.findViewById(R.id.imgdialog_txt);
		img = (ImageView) defContentView.findViewById(R.id.imgdialog_img);
	}

	public Builder setImgRes(int imgRes) {
		img.setImageResource(imgRes);
		builder.setContentWaitChange(defContentView);
		return builder;
	}

	public Builder setContentRes(int contentRes) {
		txt.setText(contentRes);
		builder.setContentWaitChange(defContentView);
		return builder;
	}

	@Override
	@Deprecated
	public Builder setContent(int rid) {
		return null;
	}

	public Builder setPositiveStrRes(int positiveStrRes) {
		return super.setPositiveButton(positiveStrRes);
	}

	@Override
	void setDefault() {
		changeInnerContent(defContentView);
	}

	@Override
	public CustomDialog create() {
		CustomDialog dialog = super.create();
		dialog.changeView2Single();
		return dialog;
	}
}
