package com.lht.cloudjob.clazz;

import android.content.Context;
import android.content.Intent;

import com.lht.cloudjob.activity.asyncprotected.LoginActivity;
import com.lht.cloudjob.interfaces.ITriggerCompare;

/**
 * <p><b>Package</b> com.lht.cloudjob.clazz
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> LoginIntentFactory
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/5/6.
 */
public class LoginIntentFactory {

    public static Intent create(Context context, ITriggerCompare iTriggerCompare) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(LoginActivity.TRIGGERKEY,iTriggerCompare.getSerializable());
        return intent;
    }
}
