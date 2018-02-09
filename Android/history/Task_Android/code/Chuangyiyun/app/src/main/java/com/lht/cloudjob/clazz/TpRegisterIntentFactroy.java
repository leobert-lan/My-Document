package com.lht.cloudjob.clazz;

import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.activity.asyncprotected.TpRegisterActivity;
import com.lht.cloudjob.interfaces.ITriggerCompare;
import com.lht.cloudjob.mvp.model.pojo.LoginInfo;

/**
 * <p><b>Package</b> com.lht.cloudjob.clazz
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> TpRegisterIntentFactroy
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/15.
 */
public class TpRegisterIntentFactroy {

    public static Intent create(Context context, LoginInfo info, ITriggerCompare iTriggerCompare) {
        Intent intent = new Intent(context, TpRegisterActivity.class);
        intent.putExtra(TpRegisterActivity.VERIFYINFOKEY, JSON.toJSONString(info));
        intent.putExtra(TpRegisterActivity.TRIGGERKEY,iTriggerCompare.getSerializable());
        return intent;
    }
}
