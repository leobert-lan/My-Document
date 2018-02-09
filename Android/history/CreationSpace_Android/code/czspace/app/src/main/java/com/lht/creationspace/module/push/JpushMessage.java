package com.lht.creationspace.module.push;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.model.bean
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> JpushMessage
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/12/14.
 */

public class JpushMessage {

    private AndroidMessage android;

    public AndroidMessage getAndroid() {
        return android;
    }

    public void setAndroid(AndroidMessage android) {
        this.android = android;
    }


    /////////////////////////////////////////////////////////////////////////////////

    public static class AndroidMessage {
        /**
         * 推送的summary，两行式的第二行内容
         */
        private String alert;

        /**
         * 推送的标题，暂不用，写死FLAG社，两行式的第一行
         */
        private String title;

        /**
         * 通知栏样式编号，暂不用
         */
        private int builder_id;

        /**
         * 业务数据
         */
        private MessageExtra extras;

        public String getAlert() {
            return alert;
        }

        public void setAlert(String alert) {
            this.alert = alert;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getBuilder_id() {
            return builder_id;
        }

        public void setBuilder_id(int builder_id) {
            this.builder_id = builder_id;
        }

        public MessageExtra getExtras() {
            return extras;
        }

        public void setExtras(MessageExtra extras) {
            this.extras = extras;
        }
    }

    /////////////////////////////////////////////////////////////////////

    public static class MessageExtra {
        public static final int OBJ_TYPE_VSOACTIVITY = 2;

        public static final int OBJ_TYPE_VSONOTIFY = 1;

        private int obj_type;

        /**
         * title，创建通知时不使用
         */
        private String title;

        /**
         * 创建通知时的
         */
        private String content;

        /**
         * 消息类的消息id
         */
        private String msg_id;

        /**
         * 活动的url
         */
        private String activity_url;

        /**
         * 呈现时间
         */
        private long send_time;


        public int getObj_type() {
            return obj_type;
        }

        public void setObj_type(int obj_type) {
            this.obj_type = obj_type;
        }

        public String getActivity_url() {
            return activity_url;
        }

        public void setActivity_url(String activity_url) {
            this.activity_url = activity_url;
        }

        public String getMsg_id() {
            return msg_id;
        }

        public void setMsg_id(String msg_id) {
            this.msg_id = msg_id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public long getSend_time() {
            return send_time;
        }

        public void setSend_time(long send_time) {
            this.send_time = send_time * 1000; //trans sec to millis sec
        }
    }
}
