package com.lht.creationspace.hybrid.native4js.expandreqbean;

/**
 * Created by leobert on 2017/3/1.
 */
public class NF_InputReqBean {
    private String text_cancel;

    private String text_submit;

    private String text_hint;

    private String text_title;

    private int max_length = 200;//default 200

    private String key_cache;

    public String getText_cancel() {
        return text_cancel;
    }

    public void setText_cancel(String text_cancel) {
        this.text_cancel = text_cancel;
    }

    public String getText_submit() {
        return text_submit;
    }

    public void setText_submit(String text_submit) {
        this.text_submit = text_submit;
    }

    public String getText_hint() {
        return text_hint;
    }

    public void setText_hint(String text_hint) {
        this.text_hint = text_hint;
    }

    public String getText_title() {
        return text_title;
    }

    public void setText_title(String text_title) {
        this.text_title = text_title;
    }

    public int getMax_length() {
        return max_length;
    }

    public void setMax_length(int max_length) {
        this.max_length = max_length;
    }

    public String getKey_cache() {
        return key_cache;
    }

    public void setKey_cache(String key_cache) {
        this.key_cache = key_cache;
    }

    //    - text_cancel  取消按钮的文字，不传默认为取消
//    - text_submit  提交按钮的文字，不传默认为完成
//    - text_hint    原生输入框的输入提示，不传默认为空
//    - text_title   仿照网易输入框上部中间的title，默认为空
//    - max_length   最大输入长度，默认200
//
//    //暂不使用
//    key_cache    如果该文字需要做一个缓存，缓存的key，该缓存仅仅存储在内存中，使用取消、完成均会清除，

}
