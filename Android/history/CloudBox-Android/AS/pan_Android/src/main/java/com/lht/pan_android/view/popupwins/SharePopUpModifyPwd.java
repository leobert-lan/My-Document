package com.lht.pan_android.view.popupwins;

import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IPreventPenetrate;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @ClassName: CustomDialog
 * @Description: 暂时不改
 * @date 2016年1月6日 上午9:50:10
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class SharePopUpModifyPwd extends CustomDialog {

	private EditText editText;
	private TextView modifyPwd;
	private TextView txtTitle;
	private TextView negativeBtn;
	private TextView positiveBtn;

	private View conentView;

	public SharePopUpModifyPwd(final Activity context, IPreventPenetrate ippl) {
		super(context, ippl);
	}

	@Override
	void init() {
		super.doDefaultSetting();

		this.setFocusable(true);

		LayoutInflater inflater = LayoutInflater.from(mActivity);
		conentView = inflater.inflate(R.layout.popwindow_share_reset_pwd, null);
		setContentView(conentView);
		negativeBtn = (TextView) conentView.findViewById(R.id.share_negativebtn);
		positiveBtn = (TextView) conentView.findViewById(R.id.share_positivebtn);
		modifyPwd = (TextView) conentView.findViewById(R.id.share_modity_pwd);
		editText = (EditText) conentView.findViewById(R.id.share_edit);

		txtTitle = (TextView) conentView.findViewById(R.id.share_title);

		ColorDrawable dw = new ColorDrawable(0x00000000);
		this.setBackgroundDrawable(dw);

		openKeyboard(new Handler(), 500);
		negativeBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SharePopUpModifyPwd.this.dismiss();
				// 注意执行次序
				if (negativeClickListener != null)
					negativeClickListener.onNegativeClick();
			}
		});

		positiveBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO check
				SharePopUpModifyPwd.this.dismiss();
				// backgroundAplha(0.4f);//TODO ??
				// 注意执行次序
				if (positiveClickListener != null)
					positiveClickListener.onPositiveClick();
			}
		});

		modifyPwd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO check
				SharePopUpModifyPwd.this.dismiss();
				backgroundAplha(1.0f);
				// 注意执行次序
				if (modifyClickListener != null) {
					modifyClickListener.onModifyClick();
				}
			}
		});
	}

	@Override
	public void dismiss() {
		super.dismiss();
		modifyPwd.setText("");
	}

	public void showButton() {
		modifyPwd.setVisibility(View.VISIBLE);
	}

	@Override
	public void setTitle(String s) {
		txtTitle.setText(s);
	}

	@Override
	public void setTitle(int rid) {
		String s = mActivity.getResources().getString(rid);
		this.setTitle(s);
	}

	public String getContent() {
		return editText.getText().toString();
	}

	public void setContent(EditText content) {
		this.editText = content;
	}

	@Override
	public void setContent(String s) {
		editText.setText(s);
	}

	@Override
	public void setContent(int rid) {
		String s = mActivity.getResources().getString(rid);
		this.setContent(s);
	}

	public void setModifyPwd(String text) {
		modifyPwd.setText(text);
	}

	public void setModifyPwd(int rid) {
		String s = mActivity.getResources().getString(rid);
		this.setModifyPwd(s);
	}

	@Override
	public void setPositiveButton(String text) {
		positiveBtn.setText(text);
	}

	@Override
	public void setPositiveButton(int rid) {
		String s = mActivity.getResources().getString(rid);
		this.setPositiveButton(s);
	}

	@Override
	public void setNegativeButton(String text) {
		negativeBtn.setText(text);
	}

	@Override
	public void setNegativeButton(int rid) {
		String s = mActivity.getResources().getString(rid);
		this.setNegativeButton(s);
	}

	private OnPositiveClickListener positiveClickListener = null;

	private OnNegativeClickListener negativeClickListener = null;

	private ModifyClickListener modifyClickListener = null;

	@Override
	public OnPositiveClickListener getPositiveClickListener() {
		return positiveClickListener;
	}

	@Override
	public void setPositiveClickListener(OnPositiveClickListener positiveClickListener) {
		this.positiveClickListener = positiveClickListener;
	}

	@Override
	public OnNegativeClickListener getNegativeClickListener() {
		return negativeClickListener;
	}

	@Override
	public void setNegativeClickListener(OnNegativeClickListener negativeClickListener) {
		this.negativeClickListener = negativeClickListener;
	}

	public ModifyClickListener getModifyClickListener() {
		return modifyClickListener;
	}

	public void setModifyClickListener(ModifyClickListener modifyClickListener) {
		this.modifyClickListener = modifyClickListener;
	}

	/**
	 * @ClassName: ModifyPassword
	 * @Description: 修改密码的回调接口
	 * @date 2016年1月27日 下午7:12:06
	 * @author zhangbin
	 * @version 1.0
	 * @since JDK 1.6
	 */
	public interface ModifyClickListener {
		void onModifyClick();
	}

	/**
	 * @Title: openKeyboard
	 * @Description: 延迟弹出软键盘
	 * @author: zhangbin
	 * @param mHandler
	 * @param s
	 */
	private void openKeyboard(Handler mHandler, int second) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager inputMethodManager = (InputMethodManager) mActivity
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}, second);
	}
}
