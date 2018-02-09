package com.lht.cloudjob.mvp.model.pojo;

import com.lht.cloudjob.util.numeric.NumericalUtils;
import com.lht.cloudjob.util.string.StringUtil;
import com.lht.cloudjob.util.time.TimeUtil;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.pojo
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> DemandItemData
 * <p><b>Description</b>: 后台暂未设计，将bean和pojo分开，避免大幅调整
 * Created by leobert on 2016/7/20.
 */
public class DemandItemData {

    /**
     * 优先级
     * 优先级1：置顶+加急
     * 优先级2：置顶
     * 优先级3：加急
     * 优先级4：截稿时间临近的排在前面
     */
    public static final int PRIORITY_TOP_URGENT = 1;

    public static final int PRIORITY_TOP = 2;

    public static final int PRIORITY_URGENT = 3;

    public static final int PRIORITY_NORMAL = 4;

    /**
     * 编辑标记
     */
    private boolean isSelected;

    /**
     * 1 未中标
     * 2 备选
     * 3 中标
     * 4 被举报
     */
    private int work_status;

    private int status;

    private String statusAlias;

    /**
     * 是否暗标，暗标不显示价格
     */
    private boolean isHideTender;

    /**
     * 需求ID
     */
    private String uniqueId;

    /**
     * 价格 ￥
     */
    private double price;

    /**
     * 需求名
     */
    private String demandName;

    /**
     * 优先级
     * 优先级1：置顶+加急
     * 优先级2：置顶
     * 优先级3：加急
     * 优先级4：截稿时间临近的排在前面
     */
    private int priority;

    /**
     * 参与人数
     */
    private int participater;

    /**
     * 剩余的时间
     */
    private long remainMillis;

    /**
     * 状态码,此字段我预留
     */
    private int statecode;

    /**
     * 需求类型（1=>悬赏，2=>招标，3=>雇佣）
     */
    private int model;

    public boolean isReward() {
        return getModel() == 1;
    }

    private String modelName;

    /**
     * 剩余时间别名
     */
    private String remainTimeAlias;

    private ArrayList<Integer> operates;

    /**
     * 扩展属性，发布者联系方式
     */
    private String publisherMobile;

    /**
     * 1   发布
     * 2   投标
     * 3   选稿
     * 4   公示
     * 5   制作
     * 6   结束
     */
    private int step;

    public int getWork_status() {
        return work_status;
    }

    public void setWork_status(int work_status) {
        this.work_status = work_status;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isWorkSelected() {
        return getWork_status() == 3;
    }

    public boolean isWorkOptional() {
        return getWork_status() == 2;
    }

    public boolean isHideTender() {
        return isHideTender;
    }

    public void setIsHideTender(boolean isHideTender) {
        this.isHideTender = isHideTender;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public double getPrice() {
        return price;
    }

    public String getPriceString() {
        if (isHideTender) {
            return "暗标";
        }
        String _p = NumericalUtils.integerFormat(getPrice());
        return new StringBuilder("￥").append(_p).toString();
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDemandName() {
        return demandName;
    }

    public void setDemandName(String demandName) {
        this.demandName = demandName;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getParticipater() {
        return participater;
    }

    public int getModel() {
        return model;
    }

    public String getModelString() {
        String s = getModelName();
        if (!StringUtil.isEmpty(s)) {
            return s;
        }
        /**
         * 未提供时自己处理
         * 需求类型（1=>悬赏，2=>招标，3=>雇佣）
         */
        if (getModel() == 1) {
            return "悬赏";
        } else if (getModel() == 2) {
            return "招标";
        } else {
            return "雇佣";
        }
    }


    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setModel(int model) {
        this.model = model;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusAlias() {
        return statusAlias;
    }

    public void setStatusAlias(String statusAlias) {
        this.statusAlias = statusAlias;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    /**
     * 1   发布
     * 2   投标
     * 3   选稿
     * 4   公示
     * 5   制作
     * 6   结束
     *
     * @param step step
     * @return alias
     */
    private static String trans2StepAlias(int step) {
        switch (step) {
            case 1:
                return "发布";
            case 2:
                return "投稿中";
            case 3:
                return "选稿中";
            case 4:
                return "公示中";
            case 5:
                return "制作中";
            case 6:
                return "结束";
            default:
                return "";
        }
    }

    /**
     * 格式化投稿人数
     *
     * @return
     */
    public String getParticipaterString() {
        String s = "%d人参与";
        return String.format(s, getParticipater());
    }

    public void setParticipater(int participater) {
        this.participater = participater;
    }

    public long getRemainMillis() {
        return remainMillis;
    }

    public String getRemainTimeString() {
        // TODO: 2016/9/26
        //认为投稿中，取接口格式化的，如果是空，那就是已经过了投稿，否则就是接口错了或者脏数据，自己动手丰衣足食

        //全部自己格式化
        if (getStep() == 2 || getStep() == 0) { //hot & rich don't have key step,will return 0 on getStep()
            long temp = getRemainMillis();
            int d = (int) (temp / TimeUtil.DAY_MILLIS);
            long _millis = temp % TimeUtil.DAY_MILLIS;
            int h = (int) (_millis / TimeUtil.HOUR_MILLIS);
            if (d > 0) {
                return String.format("%d天%d小时后截稿", d, h);
            } else if (h > 0) {
                return String.format("%d小时后截稿", h);
            } else {
                long _millis2 = _millis % TimeUtil.HOUR_MILLIS;
                int minute = (int) (_millis2 / TimeUtil.MINUTE_MILLIS);
                if (minute > 0) {
                    return String.format("%d分钟后截稿", minute);
                } else if (minute < 0) {
                    return "过期";
                } else {
                    return "马上截稿";
                }
            }
        } else {
            //根据step判断
            String stepAlias = trans2StepAlias(getStep());
            if (!StringUtil.isEmpty(stepAlias)) {
                return stepAlias;
            }
            //脏数据

            return "";
        }
    }

    public String getRemainTimeAlias() {
        return remainTimeAlias;
    }

    public void setRemainTimeAlias(String remainTimeAlias) {
        this.remainTimeAlias = remainTimeAlias;
    }

    public void setRemainMillis(long remainMillis) {
        this.remainMillis = remainMillis;
    }

    public int getStatecode() {
        return statecode;
    }

    public void setStatecode(int statecode) {
        this.statecode = statecode;
    }


    public ArrayList<Integer> getOperates() {
        return operates;
    }

    public void setOperates(ArrayList<Integer> operates) {
        this.operates = operates;
    }

    public String getPublisherMobile() {
        return publisherMobile;
    }

    public void setPublisherMobile(String publisherMobile) {
        this.publisherMobile = publisherMobile;
    }
}
