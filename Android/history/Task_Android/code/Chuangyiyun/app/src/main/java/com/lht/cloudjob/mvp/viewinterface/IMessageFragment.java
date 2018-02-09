package com.lht.cloudjob.mvp.viewinterface;

import android.app.Activity;

import com.lht.cloudjob.mvp.model.bean.UnreadMsgResBean;
import com.lht.cloudjob.mvp.model.bean.VsoActivitiesResBean;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.viewinterface
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IMessageFragment
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/8/18.
 */
public interface IMessageFragment {
    /**
     * 按照接口内容，一次性更新两种类型消息的内容，不再拆分
     */
    void updateMessage(UnreadMsgResBean bean);

    Activity getActivity();

    void updateVsoActivitiesMsg(VsoActivitiesResBean vsoActivitiesResBean);

}
