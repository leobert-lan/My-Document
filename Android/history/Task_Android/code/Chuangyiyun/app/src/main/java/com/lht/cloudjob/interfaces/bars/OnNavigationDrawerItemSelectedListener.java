package com.lht.cloudjob.interfaces.bars;

import com.lht.cloudjob.mvp.model.pojo.LoginType;

/**
 * <p><b>Package</b> com.lht.cloudjob.interfaces.bars
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> OnNavigationDrawerItemSelectedListener
 * <p><b>Description</b>: 侧边栏项目被点击
 * Created by leobert on 2016/7/1.
 */
public interface OnNavigationDrawerItemSelectedListener {

    /**
     * 侧边栏item被选取事件回调
     * @param position the position of the item that be selected
     * @return if drawer need to be closed,true if need ,false otherwise
     */
    boolean onNavigationDrawerItemSelected(LoginType type,int position);
}
