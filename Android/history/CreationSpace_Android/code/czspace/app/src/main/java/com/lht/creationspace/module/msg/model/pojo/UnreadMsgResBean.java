package com.lht.creationspace.module.msg.model.pojo;

import com.lht.creationspace.module.api.IRestfulApi;
import com.lht.creationspace.module.msg.model.UnreadMessageModel;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.model.bean
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> UnreadMsgResBean
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/8/18.
 *
 *  to see Model at{@link UnreadMessageModel}
 *  to see API at{@link IRestfulApi.UnreadMessageApi}
 */
public class UnreadMsgResBean {

    /**
     * 系统消息
     */
    private MsgGroup system;

    /**
     * 通知类
     */
    private MsgGroup appmsg;

//        "inbox": 私人暂不解析

    public MsgGroup getSystem() {
        return system;
    }

    public void setSystem(MsgGroup system) {
        this.system = system;
    }

    public MsgGroup getAppmsg() {
        return appmsg;
    }

    public void setAppmsg(MsgGroup appmsg) {
        this.appmsg = appmsg;
    }

    public static class MsgGroup {
        /**
         * 未读总量
         */
        private int count;
        /**
         * 消息列表，取第一个显示
         */
//        private ArrayList<VsoMessage> message;

        private VsoMessage[] message;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

//        public ArrayList<VsoMessage> getMessage() {
//            return message;
//        }
//
//        public void setMessage(ArrayList<VsoMessage> message) {
//            this.message = message;
//        }


        public VsoMessage[] getMessage() {
            return message;
        }

        public void setMessage(VsoMessage[] message) {
            this.message = message;
        }
    }

    public static class VsoMessage {
        /**
         * 消息编号
         */
        private String msg_id;
        /**
         * 消息状态（0=>未读，1=>已读）
         */
        private int view_status;

        private String title;

        private String content;

        /**
         * in second ,should trans e.g.1470281502
         */
        private long on_time;

        public String getMsg_id() {
            return msg_id;
        }

        public void setMsg_id(String msg_id) {
            this.msg_id = msg_id;
        }

        public int getView_status() {
            return view_status;
        }

        public void setView_status(int view_status) {
            this.view_status = view_status;
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

        public long getOn_time() {
            return on_time;
        }

        /**
         * @param on_time time stamp in second
         */
        public void setOn_time(long on_time) {
            this.on_time = on_time * 1000;
        }
    }

}
