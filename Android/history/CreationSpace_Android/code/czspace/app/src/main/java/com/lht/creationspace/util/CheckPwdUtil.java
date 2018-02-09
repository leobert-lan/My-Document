package com.lht.creationspace.util;

import com.lht.creationspace.R;
import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;
import com.lht.creationspace.util.string.StringUtil;


public class CheckPwdUtil {

    private static final String REGEX = "^[a-zA-Z0-9-`=\\[\\]{};',./~!@#$%^&*()_+|:\"<>?]+$";

    private static final String REGEX_V2 = "^(?!\\d+$)^[0-9a-zA-Z_`~!@#$%^&*()+=\\[\\]{}|;,.:<>?]{6,20}$";

    public static boolean isPasswordLegal(String password, IActivityAsyncProtected iActivityAsyncProtected) {
        if (StringUtil.isEmpty(password)) {
            iActivityAsyncProtected.showMsg(iActivityAsyncProtected.getAppResource().getString(
                    R.string.v1000_toast_error_null_pwd));
            return false;
        }

        if (!isPasswordLegal(password)) {
            iActivityAsyncProtected.showMsg(iActivityAsyncProtected.getAppResource().getString(
                    R.string.v1000_toast_register_text_pwd_illegal));
            return false;
        }
        return true;
    }

    public static boolean isPasswordLegal(String password) {
        return !StringUtil.isEmpty(password) && password.matches(REGEX_V2);
    }
}
