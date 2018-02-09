package com.lht.cloudjob.mvp.model.bean;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.bean
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> LetterBean
 * <p><b>Description</b>: TODO
 * <p>Created by Administrator on 2016/9/8.
 * 投稿--稿件
 */

public class LetterBean {

    /**
     * 需求编号
     */
    private String task_bn;
    /**
     * 用户名
     */
    private String username;
    /***
     * 投稿描述
     */
    private String description;
    /**
     * 附件
     */
    private String attachments;
    /**
     * 附件原始文件名
     */
    private String attachment_name;
    /**
     * 预计完成天数
     */
    private String days;
    /**
     * 报价
     */
    private String price;

    /**
     * 是否显示稿件
     */
    private boolean is_mark;
    /**
     * 投稿成功之后是否发送站内信
     */
    private boolean sms_flag;

    public String getTask_bn() {
        return task_bn;
    }

    public void setTask_bn(String task_bn) {
        this.task_bn = task_bn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAttachments() {
        return attachments;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }

    public String getAttachment_name() {
        return attachment_name;
    }

    public void setAttachment_name(String attachment_name) {
        this.attachment_name = attachment_name;
    }

    /**
     * @return true to display，false to hide
     */
    public boolean is_mark() {
        return is_mark;
    }

    /**
     * 是否显示稿件
     * @param is_mark true to display，false to hide，default is false：hide
     */
    public void setIs_mark(boolean is_mark) {
        this.is_mark = is_mark;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isSms_flag() {
        return sms_flag;
    }

    public void setSms_flag(boolean sms_flag) {
        this.sms_flag = sms_flag;
    }
}
