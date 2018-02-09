package com.lht.cloudjob.checkable.jobs;


import com.lht.cloudjob.MainApplication;
import com.lht.cloudjob.checkable.AbsCheckJob;
import com.lht.cloudjob.util.string.StringUtil;
import com.lht.cloudjob.util.toast.ToastUtils;

/**
 * 检测手机号是否合法
 * Created by leobert on 2017/2/23.
 */

public class CellPhoneCheckJob extends AbsCheckJob {
    /**
     * 验证手机号码
     * <p>
     * 移动号码段:139、138、137、136、135、134、150、151、152、157、158、159、182、183、187、188、147
     * 联通号码段:130、131、132、136、185、186、145
     * 电信号码段:133、153、180、189
     */
    private static final String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$";

    private final String cellPhone;

    public CellPhoneCheckJob(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    @Override
    public boolean check() {
        if (StringUtil.isEmpty(cellPhone)) {
            ToastUtils.show(MainApplication.getOurInstance(), "请输入手机号", ToastUtils.Duration.s);
            return RESULT_CHECK_ILLEGAL;
        }

        if (cellPhone.length() == 11)
            return RESULT_CHECK_LEGAL;
        //规则不全 如177号段未添加
        if (cellPhone.matches(regex))
            return RESULT_CHECK_LEGAL;
        else {
            ToastUtils.show(MainApplication.getOurInstance(), "请输入正确的手机号码", ToastUtils.Duration.s);
            return RESULT_CHECK_ILLEGAL;
        }
    }


}
