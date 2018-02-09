package com.lht.cloudjob.mvp.model.bean;

/**
 * 精彩活动列表ResBean
 * Created by chhyu on 2016/12/15.
 */

public class VsoActivitiesResBean {


    private String title;
    private String content;
    private String activity_url;
    private long send_time;

    public long getSend_time() {
        return send_time;
    }

    public void setSend_time(long send_time) {
        this.send_time = send_time * 1000;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getActivity_url() {
        return activity_url;
    }

    public void setActivity_url(String activity_url) {
        this.activity_url = activity_url;
    }

}
