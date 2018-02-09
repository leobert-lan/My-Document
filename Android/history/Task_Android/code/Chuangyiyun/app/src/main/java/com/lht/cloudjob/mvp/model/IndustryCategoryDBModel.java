package com.lht.cloudjob.mvp.model;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IndustryCategoryDBModel
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/8/16.
 */
public class IndustryCategoryDBModel {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;

    private String data;

    private long createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
