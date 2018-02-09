package com.lht.cloudjob.mvp.viewinterface;

import com.lht.cloudjob.mvp.model.bean.RegisterResBean;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.viewinterface
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IRegisterActivity
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/7.
 */
public interface IRegisterActivity extends IActivityAsyncProtected {

    void showErrorMsg(String message);

    /**
     * enable or disable the input of verify-code,when the verify-code has been
     * checked as a correct one, we should disable the edittext to avoid meaningless
     * modifying,especially cause from a mis-touching
     * @param isEnable false while checked,true while get verify-code again or checked to be wrong
     */
    void setVCInputActiveStatus(boolean isEnable);

    /**
     * when verify-code has been sent,a cool time needed,disable the button while cooling
     * @param isEnable false while on cd,true otherwise
     */
    void setVCGetterActiveStatus(boolean isEnable);

    void showCDRemaining(String formatTime);

    void initVCGetter(int textResId);

    String getVerifyCode();

    String getPwd();

    String getPhone();

    void jump2Subscribe(RegisterResBean data);

    void showLoginGuideDialog();

    void notifyOverLength();
}
