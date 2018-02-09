package com.lht.cloudjob.util;

import com.lht.cloudjob.R;
import com.lht.cloudjob.mvp.viewinterface.IActivityAsyncProtected;


public class CheckPwdUtil {

    private static final String REGIX = "^[a-zA-Z0-9-`=\\[\\]{};',./~!@#$%^&*()_+|:\"<>?]+$";

    public static boolean checkPwdLengthLegal(String password,IActivityAsyncProtected iActivityAsyncProtected){
        if (password.length() < 6) {
            iActivityAsyncProtected.showMsg(iActivityAsyncProtected.getAppResource().getString(
                    R.string.v1010_toast_register_text_pwd_minlength));
            return false;
        } else if (password.length() > 20) {
            iActivityAsyncProtected.showMsg(iActivityAsyncProtected.getAppResource().getString(
                    R.string.v1010_toast_register_text_pwd_maxlength));
            return false;
        } else if (!password.matches(REGIX)){
            iActivityAsyncProtected.showMsg(iActivityAsyncProtected.getAppResource().getString(
                    R.string.v1010_toast_register_text_pwd_illegal));
            return false;
        } else {
            return true;
        }
    }
}
