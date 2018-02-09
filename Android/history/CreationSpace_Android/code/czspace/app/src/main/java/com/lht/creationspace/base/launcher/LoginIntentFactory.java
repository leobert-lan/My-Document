package com.lht.creationspace.base.launcher;

import android.content.Context;
import android.content.Intent;

import com.lht.creationspace.module.user.login.ui.LoginActivity;

/**
 * <p><b>Package</b> com.lht.vsocyy.clazz
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> LoginIntentFactory
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/5/6.
 */
public class LoginIntentFactory {

    public static Intent create(Context context, ITriggerCompare iTriggerCompare) {
//        Intent intent = new Intent(context, LoginActivity.class);
//        intent.putExtra(LoginActivity.TRIGGERKEY,iTriggerCompare.getSerializable());
//        return intent;
        return new LoginIntent(context, iTriggerCompare);
    }

    public static class LoginIntent extends Intent {
        public LoginIntent(Context context,ITriggerCompare iTriggerCompare) {
            super(context, LoginActivity.class);
            putExtra(LoginActivity.TRIGGERKEY,iTriggerCompare.getSerializable());
        }
    }
}
