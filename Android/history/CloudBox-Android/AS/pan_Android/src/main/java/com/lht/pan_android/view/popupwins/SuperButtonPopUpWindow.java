package com.lht.pan_android.view.popupwins;

import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IPreventPenetrate;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

/**
 * @ClassName: CustomDialog
 * @Description: TODO
 * @date 2016年1月6日 上午9:50:10
 * @author leobert.lan
 * @version 1.0
 */
public class SuperButtonPopUpWindow extends CustomPopupWindow {

	private View contentView;

	public SuperButtonPopUpWindow(final Activity activity, IPreventPenetrate ippl) {
		super(activity, ippl);
		init();
	}

	@Override
	void init() {
		contentView = LayoutInflater.from(mActivity).inflate(R.layout.activity_super_center, null);
		this.setContentView(contentView);

		this.setHeight(LayoutParams.MATCH_PARENT);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setFocusable(true);
		this.setOutsideTouchable(true);

		ColorDrawable dw = new ColorDrawable(0x00000000);
		this.setBackgroundDrawable(dw);

		LinearLayout linearPic = (LinearLayout) contentView.findViewById(R.id.linear_super_center_picture);
		LinearLayout linearVedio = (LinearLayout) contentView.findViewById(R.id.linear_super_center_vedio);
		LinearLayout linearFolder = (LinearLayout) contentView.findViewById(R.id.linear_super_center_folder);

		contentView.findViewById(R.id.bigblue_dismiss).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SuperButtonPopUpWindow.this.dismiss();
			}
		});

		linearPic.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (pictureClickListener != null)
					pictureClickListener.onPictureClick();
				SuperButtonPopUpWindow.this.dismiss();
				backgroundAplha(1.0f);
			}
		});
		linearVedio.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onVedioClickListener != null)
					onVedioClickListener.onVedioClick();
				SuperButtonPopUpWindow.this.dismiss();
				backgroundAplha(1.0f);
			}
		});
		linearFolder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onFolderClickListener != null)
					onFolderClickListener.onClickFolder();
				SuperButtonPopUpWindow.this.dismiss();
				backgroundAplha(1.0f);
			}
		});
	}

	@Override
	public void show() {
		this.showAsDropDown(contentView);
	}

	@Override
	public void dismiss() {
		backgroundAplha(1.0f);
		super.dismiss();
	}

	private OnPictureClickListener pictureClickListener = null;
	private OnVedioClickListener onVedioClickListener = null;
	private OnFolderClickListener onFolderClickListener = null;

	public OnPictureClickListener getPictureClickListener() {
		return pictureClickListener;
	}

	public OnVedioClickListener getVedioClickListener() {
		return onVedioClickListener;
	}

	public OnFolderClickListener getFolderClickListener() {
		return onFolderClickListener;
	}

	public void setPictureClickListener(OnPictureClickListener pictureClickListener) {
		this.pictureClickListener = pictureClickListener;
	}

	public void setVedioClickListener(OnVedioClickListener onVedioClickListener) {
		this.onVedioClickListener = onVedioClickListener;
	}

	public void setFloderClickListener(OnFolderClickListener onFolderClickListener) {
		this.onFolderClickListener = onFolderClickListener;
	}

	/**
	 * @ClassName: OnPositiveClickListener
	 * @Description: 中间按钮上传图片的点击事件
	 * @date 2016年1月19日 下午5:52:59
	 * @author zhangbin
	 * @version 1.0
	 * @since JDK 1.6
	 */
	public interface OnPictureClickListener {
		void onPictureClick();
	}

	/**
	 * @ClassName: OnVedioClickListener
	 * @Description: 中间按钮上传视频的点击事件
	 * @date 2016年3月3日 上午11:04:54
	 * 
	 * @author zhangbin
	 * @version 1.0
	 * @since JDK 1.6
	 */
	public interface OnVedioClickListener {
		void onVedioClick();
	}

	/**
	 * @ClassName: OnClickFolderClickListener
	 * @Description: 中间按钮新建文件夹的点击事件
	 * @date 2016年3月3日 上午10:59:51
	 * 
	 * @author zhangbin
	 * @version 1.0
	 * @since JDK 1.6
	 */
	public interface OnFolderClickListener {
		void onClickFolder();
	}
}
