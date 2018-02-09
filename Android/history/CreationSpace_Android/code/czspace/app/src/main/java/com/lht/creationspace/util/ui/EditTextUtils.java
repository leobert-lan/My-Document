package com.lht.creationspace.util.ui;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;

import com.lht.creationspace.util.string.StringUtil;

/**
 * <p><b>Package:</b> com.lht.creationspace.util.ui </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> EditTextUtils </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/3/21.
 */

public class EditTextUtils {

    /**
     * move the input index to the end of the target
     * @param target the input EditText widget
     */
    public static final void moveIndex2End(EditText target) {
        if (target == null)
            return;
        String input = target.getText().toString();
        int end = StringUtil.nullStrToEmpty(input).length();
        target.setSelection(end);
    }

    public static final void togglePwdDisplay(boolean display, EditText et) {
        if (display) {
            et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            et.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        EditTextUtils.moveIndex2End(et);
    }
}
