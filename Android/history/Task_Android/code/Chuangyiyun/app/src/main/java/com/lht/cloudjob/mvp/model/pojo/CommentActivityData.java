package com.lht.cloudjob.mvp.model.pojo;

import com.lht.cloudjob.mvp.model.bean.DemandInfoResBean;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.pojo
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> CommentActivityData
 * <p><b>Description</b>: TODO
 * <p>Created by Administrator on 2016/9/26.
 */

public class CommentActivityData {

    private String username;

    private String task_bn;

    public DemandInfoResBean.Publisher publisher;

    /**
     * 是否需要重新获取需求信息
     * */
    private boolean needQueryInfo;

    public boolean isNeedQueryInfo() {
        return needQueryInfo;
    }

    public void setNeedQueryInfo(boolean needQueryInfo) {
        this.needQueryInfo = needQueryInfo;
    }

    public CommentActivityData(){}

    public CommentActivityData(String username, String task_bn, DemandInfoResBean.Publisher publisher) {
        this.username = username;
        this.task_bn = task_bn;
        this.publisher = publisher;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTask_bn() {
        return task_bn;
    }

    public void setTask_bn(String task_bn) {
        this.task_bn = task_bn;
    }

    public DemandInfoResBean.Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(DemandInfoResBean.Publisher publisher) {
        this.publisher = publisher;
    }
}
