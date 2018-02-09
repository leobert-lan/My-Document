package com.lht.cloudjob.mvp.model.bean;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.bean
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> DemandWorksResBean
 * <p><b>Description</b>: 稿件列表接口返回数据模型
 * <p> Create by Leobert on 2016/8/29
 */
public class DemandWorksResBean {
    private int total_bids;

    private ArrayList<DemandInfoResBean.Work> works;


    public int getTotal_bids() {
        return total_bids;
    }

    public void setTotal_bids(int total_bids) {
        this.total_bids = total_bids;
    }

    public ArrayList<DemandInfoResBean.Work> getWorks() {
        return works;
    }

    public void setWorks(ArrayList<DemandInfoResBean.Work> works) {
        this.works = works;
    }
}
