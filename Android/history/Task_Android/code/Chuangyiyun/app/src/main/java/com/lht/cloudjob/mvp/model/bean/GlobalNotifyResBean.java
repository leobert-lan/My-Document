package com.lht.cloudjob.mvp.model.bean;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.bean
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> GlobalNotifyResBean
 * <p><b>Description</b>: 注意数组
 * <p> Create by Leobert on 2016/9/7
 */
public class GlobalNotifyResBean {
    //        需求编号
    private String task_bn;
//
//        title	String
//        需求标题
    private String title;
//
//        real_cash	Number
//        显示金额
    private String real_cash;

    /**
     * 成交人 username
     */
    private String bidder_username;

    public String getTask_bn() {
        return task_bn;
    }

    public void setTask_bn(String task_bn) {
        this.task_bn = task_bn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReal_cash() {
        return real_cash;
    }

    public void setReal_cash(String real_cash) {
        this.real_cash = real_cash;
    }

    public String getBidder_username() {
        return bidder_username;
    }

    public void setBidder_username(String bidder_username) {
        this.bidder_username = bidder_username;
    }
}
