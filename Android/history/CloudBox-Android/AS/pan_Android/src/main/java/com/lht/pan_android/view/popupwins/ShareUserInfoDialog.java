package com.lht.pan_android.view.popupwins;

import java.util.ArrayList;

import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IPreventPenetrate;
import com.lht.pan_android.bean.MultiUserQueryItemBean;
import com.lht.pan_android.view.AutoArrangeCardLayout;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @ClassName: CustomDialog
 * @Description: TODO
 * @date 2016年1月6日 上午9:50:10
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class ShareUserInfoDialog extends CustomPopupWindow {

	private TextView negativeBtn, positiveBtn;

	private View contentView;

	private LinearLayout ll;

	private AutoArrangeCardLayout aacl;

	private TextView tv;

	private String shareId;

	public ShareUserInfoDialog(final Activity context, IPreventPenetrate ippl) {
		super(context, ippl);
		super.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				if (iPreventPenetrate != null)
					iPreventPenetrate.preventPenetrate(mActivity, false);
			}
		});
		init();
	}

	@Override
	void init() {
		super.doDefaultSetting();

		LayoutInflater inflater = LayoutInflater.from(mActivity);
		contentView = inflater.inflate(R.layout.share_user_info, null);
		setContentView(contentView);
		negativeBtn = (TextView) contentView.findViewById(R.id.share_user_info_cancel);
		positiveBtn = (TextView) contentView.findViewById(R.id.share_user_info_ok);

		positiveBtn.setEnabled(false);

		positiveBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (positiveClickListener != null)
					positiveClickListener.onPositiveClick();
				ShareUserInfoDialog.this.dismiss();
				WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
				lp.alpha = 1f;
				mActivity.getWindow().setAttributes(lp);
			}
		});
		negativeBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (negativeClickListener != null)
					negativeClickListener.onNegativeClick();
				ShareUserInfoDialog.this.dismiss();
				WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
				lp.alpha = 1f;
				mActivity.getWindow().setAttributes(lp);
			}
		});

		tv = (TextView) contentView.findViewById(R.id.share_user_info_path);

		ll = (LinearLayout) contentView.findViewById(R.id.share_user_info_ll);

		aacl = (AutoArrangeCardLayout) contentView.findViewById(R.id.share_user_info_path_aacl);
	}

	/**
	 * @Title: show
	 * @Description: 显示
	 * @author: leobert.lan
	 * @param parent
	 */
	@Override
	public void show(View parent) {
		super.show(parent);
		if (iPreventPenetrate != null)
			iPreventPenetrate.preventPenetrate(mActivity, true);
	}

	@Override
	public void show() {
		this.show(mActivity.getWindow().getDecorView());
	}

	private OnNegativeClickListener negativeClickListener = null;

	private OnPositiveClickListener positiveClickListener = null;

	public OnNegativeClickListener getNegativeClickListener() {
		return negativeClickListener;
	}

	public void setNegativeClickListener(OnNegativeClickListener negativeClickListener) {
		this.negativeClickListener = negativeClickListener;
	}

	public OnPositiveClickListener getPositiveClickListener() {
		return positiveClickListener;
	}

	public void setPositiviveEnable(boolean enable) {
		positiveBtn.setEnabled(enable);
	}

	public void setPositiveClickListener(OnPositiveClickListener positiveClickListener) {
		this.positiveClickListener = positiveClickListener;
	}

	private ArrayList<MultiUserQueryItemBean> data;

	public void setData(ArrayList<MultiUserQueryItemBean> data) {
		this.data = data;
		for (int i = 0; i < data.size(); i++) {
			TextView tv = new TextView(mActivity);
			tv.setPadding(15, 15, 15, 15);
			tv.setTextColor(mActivity.getResources().getColor(R.color.gray_777));
			tv.setText(data.get(i).getNickname());
			tv.setBackgroundResource(R.drawable.corners_bg_white);
			aacl.addView(tv);
		}
		LayoutParams lp = ll.getLayoutParams();
		lp.height = aacl.getMeasuredHeight();
		ll.setLayoutParams(lp);
	}

	public void setNegativeButton(String text) {
		negativeBtn.setText(text);
	}

	public void setNegativeButton(int rid) {
		String s = mActivity.getResources().getString(rid);
		this.setNegativeButton(s);
	}

	public void setPosiviveButton(String text) {
		positiveBtn.setText(text);
	}

	public void setPosiviveButton(int rid) {
		String s = mActivity.getResources().getString(rid);
		this.setPosiviveButton(s);
	}

	@Override
	public void setOnDismissListener(OnDismissListener onDismissListener) {
		throw new IllegalAccessError("never use this method");
	}

	public String getShareId() {
		return shareId;
	}

	public void setShareId(String shareId) {
		this.shareId = shareId;
	}

	public ArrayList<MultiUserQueryItemBean> getData() {
		return data;
	}

}
