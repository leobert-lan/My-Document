package com.lht.cloudjob.mvp.viewinterface;

import android.content.SharedPreferences;

import com.lht.cloudjob.mvp.model.bean.CommentConfigResBean;
import com.lht.cloudjob.mvp.model.bean.DemandInfoResBean;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.viewinterface
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> ICommentActivity
 * <p><b>Description</b>: TODO
 * <p>Created by Administrator on 2016/9/8.
 */

public interface ICommentActivity extends IActivityAsyncProtected {

    /**
     * 获取token相关的存储文件
     *
     * @return
     */
    SharedPreferences getTokenPreferences();

    void showErrorMsg(String message);

    /**
     * 更新评论配置项
     * @param data ...
     */
  void updateCommentItemData(ArrayList<CommentConfigResBean> data);

    void updatePublisher(DemandInfoResBean.Publisher publisher);
}
