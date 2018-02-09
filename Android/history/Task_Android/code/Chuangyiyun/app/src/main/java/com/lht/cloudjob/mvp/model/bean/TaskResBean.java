package com.lht.cloudjob.mvp.model.bean;

import com.lht.cloudjob.mvp.model.pojo.DemandItemData;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.bean
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> TaskResBean
 * <p><b>Description</b>: 最火、最壕等基础数据模型
 * <p/>
 * <p/>
 * int status;
 * <p/>
 * String status_alias;
 * <p/>
 * 需求编号
 * String task_bn;
 * <p/>
 * 任务金额
 * double task_cash;
 * <p/>
 * 需求标题
 * String title;
 * <p/>
 * 需求类型（1=>悬赏，2=>招标，3=>雇佣）
 * int model;
 * <p/>
 * 招标类型（1=>明标，2=>暗标）
 * int is_mark;
 * <p/>
 * 总投稿数
 * int total_bids;
 * <p/>
 * 创建时间，时间戳 millis
 * long create_time;
 * <p/>
 * 交稿截止时间，时间戳 millis
 * long sub_end_time;
 * <p/>
 * 置顶次数，大于0表示有购买置顶服务
 * int top;
 * <p/>
 * 加急次数，大于0表示有购买加急服务
 * int urgent;
 * <p/>
 * 交稿剩余时间格式转换
 * String sub_end_time_alias;
 * <p> Create by Leobert on 2016/8/22
 */
public class TaskResBean {
    /**
     * 需求类型 1=>悬赏
     */
    public static final int MODEL_REWARD = 1;

    /**
     * 需求类型 2=>招标
     */
    public static final int MODEL_BID = 2;

    private int status;

    private String status_alias;

    /**
     * 需求编号
     */
    private String task_bn;

    /**
     * 任务金额
     */
    private double task_cash;

    /**
     * 需求标题
     */
    private String title;

    /**
     * 需求类型（1=>悬赏，2=>招标，3=>雇佣）
     */
    private int model;

    /**
     * 需求别名
     */
    private String model_name;

    /**
     * 招标类型（1=>明标，2=>暗标）
     */
    private int is_mark;

    /**
     * 总投稿数
     */
    private int total_bids;

    /**
     * 创建时间，时间戳 millis
     */
    private long create_time;

    /**
     * 交稿截止时间，时间戳 millis
     */
    private long sub_end_time;

    /**
     * 置顶次数，大于0表示有购买置顶服务
     */
    private int top;

    /**
     * 加急次数，大于0表示有购买加急服务
     */
    private int urgent;

    /**
     * 交稿剩余时间格式转换
     */
    private String sub_end_time_alias;

    /**
     * 最火最壕无此字段，主要用于检索之类的
     * 1   发布
     * 2   投标
     * 3   选稿
     * 4   公示
     * 5   制作
     * 6   结束
     */
    private int step;

    public String getTask_bn() {
        return task_bn;
    }

    public void setTask_bn(String task_bn) {
        this.task_bn = task_bn;
    }

    public double getTask_cash() {
        return task_cash;
    }

    public void setTask_cash(double task_cash) {
        this.task_cash = task_cash;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }

    public String getModel_name() {
        return model_name;
    }

    public void setModel_name(String model_name) {
        this.model_name = model_name;
    }

    public int getTotal_bids() {
        return total_bids;
    }

    public void setTotal_bids(int total_bids) {
        this.total_bids = total_bids;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time * 1000;
    }

    public long getSub_end_time() {
        return sub_end_time;
    }

    public void setSub_end_time(long sub_end_time) {
        this.sub_end_time = sub_end_time * 1000;
    }

    public String getSub_end_time_alias() {
        return sub_end_time_alias;
    }

    public void setSub_end_time_alias(String sub_end_time_alias) {
        this.sub_end_time_alias = sub_end_time_alias;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getUrgent() {
        return urgent;
    }

    public void setUrgent(int urgent) {
        this.urgent = urgent;
    }

    public int getIs_mark() {
        return is_mark;
    }

    public boolean isHiddenTender() {
        return is_mark == 2;
    }

    public void setIs_mark(int is_mark) {
        this.is_mark = is_mark;
    }

    /**
     * 优先级
     * 优先级1：置顶+加急
     * 优先级2：置顶
     * 优先级3：加急
     * 优先级4：截稿时间临近的排在前面
     */
    public int getPriority() {
        if (top > 0 && urgent > 0) {
            return 1;
        } else if (top > 0) {
            return 2;
        } else if (urgent > 0) {
            return 3;
        } else {
            return 4;
        }
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatus_alias() {
        return status_alias;
    }

    public void setStatus_alias(String status_alias) {
        this.status_alias = status_alias;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public void copyTo(DemandItemData data) {
        data.setStatus(getStatus());
        data.setStep(getStep());
        data.setStatusAlias(getStatus_alias());
        data.setUniqueId(getTask_bn());
        data.setDemandName(getTitle());
        data.setIsHideTender(isHiddenTender());
        data.setParticipater(getTotal_bids());
        data.setPrice(getTask_cash());
        data.setPriority(getPriority());
        data.setRemainMillis(getSub_end_time() - System.currentTimeMillis());
        data.setModel(getModel());
        data.setRemainTimeAlias(getSub_end_time_alias());
    }
}
