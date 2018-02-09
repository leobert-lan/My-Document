package com.lht.creationspace.hybrid.native4js.expandreqbean;

/**
 * <p><b>Package:</b> com.lht.creationspace.native4js.expandreqbean </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> NF_CustomToastReqBean </p>
 * <p><b>Description:</b> 自定义通知请求 </p>
 * Created by leobert on 2017/3/8.
 */

public class NF_CustomToastReqBean {
    /**
     * 类型，0代表显示勾，1代表显示叉
     */
    private int type;

    private String content;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
