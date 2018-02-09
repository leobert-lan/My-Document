package com.lht.pan_android.mpv.model;

import android.widget.EditText;

/**
 * @ClassName: ITextWatcher
 * @Description: TODO
 * @date 2016年5月26日 上午9:57:39
 * 
 * @author leobert.lan
 * @version 1.0
 */
public interface ITextWatcher {
	void doWatcher(EditText editText, int maxLength);

	void doWatcher(EditText editText, int minLength, int maxLength);
}
