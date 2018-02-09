package com.lht.creationspace.customview.tab;

import android.view.View;
import android.widget.CompoundButton;

import java.util.HashMap;
import java.util.Set;

/**
 * <p><b>Package</b> com.lht.vsocyy.clazz
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> OnCheckedChangeListenerImpl
 * <p><b>Description</b>: 自定义的tabhost控制RadioButton只有一个被选中的监听器类
 * Created by leobert on 2016/7/19.
 */
public class OnCheckedChangeListenerImpl implements View.OnClickListener {

    private HashMap<CompoundButton, CompoundButton.OnCheckedChangeListener> callbacks;

    public OnCheckedChangeListenerImpl(HashMap<CompoundButton, CompoundButton.OnCheckedChangeListener> callbacks) {
        this.callbacks = callbacks;
    }

    @Override
    public void onClick(View v) {
        Set<CompoundButton> keys = callbacks.keySet();
        for (CompoundButton key : keys) {
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
