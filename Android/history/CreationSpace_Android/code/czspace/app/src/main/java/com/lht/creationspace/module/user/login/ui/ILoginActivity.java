package com.lht.creationspace.module.user.login.ui;

import android.content.SharedPreferences;

import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;
import com.lht.creationspace.social.oauth.TPOauthUserBean;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.viewinterface
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> ILoginActivity
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/5/5.
 */
public interface ILoginActivity extends IActivityAsyncProtected {
    void jump2RegisterActivity();

    void jump2ResetPwdActivity();

    SharedPreferences getTokenPreferences();

    void finishActivity();

    Object getLoginTrigger();

    void showErrorMsg(String msg);

    void showRegisterGuideDialog();

//    /**
//     * 跳转到快速绑定页面
//     */ deprecated
//    void jump2FastBindActivity(TPOauthUserBean oauthBean);

//    /**
//     * 跳转到账号关联页面
//     */
//    void jump2AccountCombineActivity(TPOauthUserBean data);

    void cancelSoftInputPanel();
}
