package com.lht.cloudjob.clazz;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import java.util.HashMap;
import java.util.Set;

/**
 * <p><b>Package</b> com.lht.cloudjob.clazz
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> OnCheckedChangeListenerImpl
 * <p><b>Description</b>: 自定义的tabhost控制RadioButton只有一个被选中的监听器类
 * Created by leobert on 2016/7/19.
 */
public class OnCheckedChangeListenerImpl implements View.OnClickListener {

    private HashMap<RadioButton, CompoundButton.OnCheckedChangeListener> callbacks;

    public OnCheckedChangeListenerImpl(HashMap<RadioButton, CompoundButton.OnCheckedChangeListener> callbacks) {
        this.callbacks = callbacks;
    }

    @Override
    public void onClick(View v) {
        Set<RadioButton> keys = callbacks.keySet();
        for (RadioButton key : keys) {
            if(key == v) {
                callbacks.get(key).onCheckedChanged(key,true);
                key.setChecked(true);
            } else {
                callbacks.get(key).onCheckedChanged(key,false);
                key.setChecked(false);
            }
        }
    }
}
