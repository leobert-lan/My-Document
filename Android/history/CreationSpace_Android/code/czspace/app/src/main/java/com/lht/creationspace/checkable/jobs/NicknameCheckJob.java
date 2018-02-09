package com.lht.creationspace.checkable.jobs;

import com.lht.creationspace.base.MainApplication;
import com.lht.creationspace.R;
import com.lht.creationspace.checkable.AbsCheckJob;
import com.lht.creationspace.util.string.StringUtil;
import com.lht.creationspace.util.toast.ToastUtils;

/**
 * Created by chhyu on 2017/4/7.
 * 昵称校验
 * 校验规则：汉字、字母（含大小写）、数字、空格及其组合，最长20个字符
 */

public class NicknameCheckJob extends AbsCheckJob {

    private static final String regex = "^[\\u4E00-\\u9FA5A-Za-z0-9\\s]+$";

    private String nickname;

    public NicknameCheckJob(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public boolean check() {
        if (StringUtil.isEmpty(nickname)) {
            ToastUtils.show(MainApplication.getOurInstance(), MainApplication.getOurInstance().getString(R.string.v1000_default_modifynickname_toast_nickname_remind), ToastUtils.Duration.s);
            return RESULT_CHECK_ILLEGAL;
        }

        if (StringUtil.isBlank(nickname)) {
            ToastUtils.show(MainApplication.getOurInstance(), MainApplication.getOurInstance().getString(R.string.v1000_default_modifynickname_toast_nickname_illegal_remind), ToastUtils.Duration.s);
            return RESULT_CHECK_ILLEGAL;
        }

        if (nickname.length() > 20) {
            ToastUtils.show(MainApplication.getOurInstance(), MainApplication.getOurInstance().getString(R.string.v1000_default_modifynickname_toast_nickname_illegal_remind), ToastUtils.Duration.s);
            return RESULT_CHECK_ILLEGAL;
        }

        if (nickname.matches(regex)) {
            return RESULT_CHECK_LEGAL;
        } else {
            ToastUtils.show(MainApplication.getOurInstance(), MainApplication.getOurInstance().getString(R.string.v1000_default_modifynickname_toast_nickname_illegal_remind), ToastUtils.Duration.s);
            return RESULT_CHECK_ILLEGAL;
        }
    }
}
