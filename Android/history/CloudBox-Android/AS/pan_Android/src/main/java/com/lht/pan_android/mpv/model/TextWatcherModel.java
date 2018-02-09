package com.lht.pan_android.mpv.model;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * <p>
 * <b>Package</b> com.lht.chuangyiyun.mvp.model
 * <p>
 * <b>Project</b> Chuangyiyun
 * <p>
 * <b>Classname</b> TextWatcherModel
 * <p>
 * <b>Description</b>: TODO Created by leobert on 2016/5/11.
 */
public class TextWatcherModel implements ITextWatcher {

	private final TextWatcherModelCallback modelCallback;

	public TextWatcherModel(TextWatcherModelCallback modelCallback) {
		this.modelCallback = modelCallback;
	}

	@Override
	public void doWatcher(EditText editText, int maxLength) {
		editText.addTextChangedListener(new WatcherImpl(editText, maxLength));
	}

	@Override
	public void doWatcher(EditText editText, int minLength, int maxLength) {
		editText.addTextChangedListener(new WatcherImpl(editText, minLength, maxLength));
	}

	public interface TextWatcherModelCallback {
		/**
		 * @Title: onOverLength
		 * @Description: 通知超长
		 * @author: leobert.lan
		 * @param edittextId
		 * @param maxLength
		 */
		void onOverLength(int edittextId, int maxLength);

		/**
		 * @Title: onChanged
		 * @Description: 通知变化情况
		 * @author: leobert.lan
		 * @param edittextId
		 * @param currentCount
		 * @param remains
		 */
		void onChanged(int edittextId, int currentCount, int remains);

		void onShort(int edittextId, int minLength);
	}

	private final class WatcherImpl implements TextWatcher {

		private final EditText editText;

		private final int maxLength;

		private final int minLength;

		WatcherImpl(EditText editText, int maxLength) {
			this(editText, 0, maxLength);
		}

		WatcherImpl(EditText editText, int minLength, int maxLength) {
			this.editText = editText;
			this.minLength = minLength;
			this.maxLength = maxLength;
		}

		private CharSequence temp;
		private int editStart;
		private int editEnd;

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			temp = s;
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			editStart = editText.getSelectionStart();
			editEnd = editText.getSelectionEnd();
			if (temp.length() > maxLength) {
				s.delete(editStart - 1, editEnd);
				int tempSelection = editStart;
				editText.setText(s);
				editText.setSelection(tempSelection);
				modelCallback.onOverLength(editText.getId(), maxLength);
			} else {
				modelCallback.onChanged(editText.getId(), temp.length(), maxLength - temp.length());
				if (temp.length() < minLength) {
					modelCallback.onShort(editText.getId(), minLength);
				}
			}
		}
	}

}
