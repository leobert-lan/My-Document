package com.lht.cloudjob.mvp.model.bean;

import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.MessageListModel;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.bean
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> MessageListItemResBean
 * <p><b>Description</b>: 消息列表item数据模型，注意列表
 * [
 * {
 * "msg_id": "211326",
 * "view_status": "0",
 * "title": "稿件中标",
 * "content": "",
 * "on_time": "1441516787",
 * "avatar": "http://static.vsochina.com/data/avatar/000/00/40/99_avatar_middle.jpg"
 * },
 * {},
 * ...
 * ]
 * <p> Create by Leobert on 2016/8/19
 * <p/>
 * to see API at{@link IRestfulApi.MessageListApi}
 * to see Model at{@link MessageListModel}
 */
public class MessageListItemResBean extends UnreadMsgResBean.VsoMessage {

    /**
     * 消息编号
     */
    private String msg_id;

    public static final int STATUS_READ = 1;

    /**
     * 消息状态（0=>未读，1=>已读）
     */
    private int view_status;

    /**
     * 标题
     */
    private String title;


    /**
     * 内容
     */
    private String content;

    /**
     * 发送消息时间，时间戳
     * in second ,should trans e.g.1470281502
     */
    private long on_time;

    /**
     * 消息发送者用户头像，仅在私人消息类型下起作用
     */
    private String avatar;

    /**
     * 额外添加
     */
    private boolean isSelected;

    @Override
    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    @Override
    public int getView_status() {
        return view_status;
    }

    @Override
    public void setView_status(int view_status) {
        this.view_status = view_status;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public long getOn_time() {
        return on_time;
    }

    /**
     * @param on_time time stamp in second
     */
    @Override
    public void setOn_time(long on_time) {
        this.on_time = on_time * 1000;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
