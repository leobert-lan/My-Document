package com.lht.cloudjob.mvp.model.bean;

import com.lht.cloudjob.mvp.model.pojo.DemandItemData;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.bean
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> OrderedTaskResBean
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/8/23
 */
public class OrderedTaskResBean extends TaskResBean {

    /**
     * 投稿编号
     */
    private String id;
    /**
     * 需求编号
     */
    private String task_bn;
    /**
     * 稿件状态
     */
    private int work_status;
    /**
     * 需求名称
     */
    private String title;
    /**
     * 需求类型（1=>悬赏，2=>招标，3=>雇佣）
     */
    private int model;

//    /**
//     * 招标类型（1=>明标，2=>暗标）
//     * in super
//     */
//    private int is_mark;

    /**
     * 阶段
     * 1	发布
     * 2	投标
     * 3	选稿
     * 4	公示
     * 5    制作
     * 6	结束
     */
    private int step;

    /**
     * 需求状态
     */
    private String task_status;
    /**
     * 需求发布者用户名
     */
    private String publisher;
    /**
     * 发布者联系方式
     */
    private String mobile;
    /**
     * 预算金额
     */
    private double task_cash;
    /**
     * 预算最小金额
     */
    private double min_cash;
    /**
     * 预算最大金额
     */
    private double max_cash;
    /**
     * 实际成交金额
     */
    private double real_cash;


//    /**
//     *in super
//     */
//    private int total_bids;

//    /**
//     *in super
//     */
//    private long sub_end_time;
    /**
     * 招标
     */
    private String model_name;
    /**
     * 投标中
     */
    private String task_status_alias;
    /**
     * 未中标
     */
    private String work_status_alias;


//    /**
//     * in super
//     */
//    private String sub_end_time_alias;
    /**  */
    private ArrayList<Integer> operate;


    @Override
    public String getTask_bn() {
        return super.getTask_bn();
    }

    @Override
    public void setTask_bn(String task_bn) {
        super.setTask_bn(task_bn);
    }

    @Override
    public double getTask_cash() {
        return super.getTask_cash();
    }

    @Override
    public void setTask_cash(double task_cash) {
        super.setTask_cash(task_cash);
    }

    @Override
    public String getTitle() {
        return super.getTitle();
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
    }

    @Override
    public int getModel() {
        return model;
    }

    @Override
    public void setModel(int model) {
        this.model = model;
    }

    @Override
    public int getTotal_bids() {
        return super.getTotal_bids();
    }

    @Override
    public void setTotal_bids(int total_bids) {
        super.setTotal_bids(total_bids);
    }

    @Override
    public long getCreate_time() {
        return super.getCreate_time();
    }

    @Override
    public void setCreate_time(long create_time) {
        super.setCreate_time(create_time);
    }

    @Override
    public long getSub_end_time() {
        return super.getSub_end_time();
    }

    @Override
    public void setSub_end_time(long sub_end_time) {
        super.setSub_end_time(sub_end_time);
    }

    @Override
    public String getSub_end_time_alias() {
        return super.getSub_end_time_alias();
    }

    @Override
    public void setSub_end_time_alias(String sub_end_time_alias) {
        super.setSub_end_time_alias(sub_end_time_alias);
    }

    @Override
    public int getTop() {
        return super.getTop();
    }

    @Override
    public void setTop(int top) {
        super.setTop(top);
    }

    @Override
    public int getUrgent() {
        return super.getUrgent();
    }

    @Override
    public void setUrgent(int urgent) {
        super.setUrgent(urgent);
    }

    @Override
    public int getIs_mark() {
        return super.getIs_mark();
    }

    @Override
    public boolean isHiddenTender() {
        return super.isHiddenTender();
    }

    @Override
    public void setIs_mark(int is_mark) {
        super.setIs_mark(is_mark);
    }

    /**
     * 优先级
     * 优先级1：置顶+加急
     * 优先级2：置顶
     * 优先级3：加急
     * 优先级4：截稿时间临近的排在前面
     */
    @Override
    public int getPriority() {
        return super.getPriority();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getTask_status() {
        return task_status;
    }

    public void setTask_status(String task_status) {
        this.task_status = task_status;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public double getMin_cash() {
        return min_cash;
    }

    public void setMin_cash(double min_cash) {
        this.min_cash = min_cash;
    }

    public double getMax_cash() {
        return max_cash;
    }

    public void setMax_cash(double max_cash) {
        this.max_cash = max_cash;
    }

    public double getReal_cash() {
        return real_cash;
    }

    public void setReal_cash(double real_cash) {
        this.real_cash = real_cash;
    }

    @Override
    public String getModel_name() {
        return model_name;
    }

    @Override
    public void setModel_name(String model_name) {
        this.model_name = model_name;
    }

    public String getTask_status_alias() {
        return task_status_alias;
    }

    public void setTask_status_alias(String task_status_alias) {
        this.task_status_alias = task_status_alias;
    }

    public int getWork_status() {
        return work_status;
    }

    public void setWork_status(int work_status) {
        this.work_status = work_status;
    }

    public String getWork_status_alias() {
        return work_status_alias;
    }

    public void setWork_status_alias(String work_status_alias) {
        this.work_status_alias = work_status_alias;
    }

    public ArrayList<Integer> getOperate() {
        return operate;
    }

    public void setOperate(ArrayList<Integer> operate) {
        this.operate = operate;
    }

    @Override
    public void copyTo(DemandItemData data) {
        super.copyTo(data);
        data.setModelName(getModel_name());
        data.setOperates(getOperate());
        data.setPublisherMobile(getMobile());
        data.setWork_status(getWork_status());
    }
}
