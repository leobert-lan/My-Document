package com.lht.creationspace.module.proj.model;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

/**
 * <p><b>Package:</b> com.lht.creationspace.module.proj.model </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> ProjTypeDbModel </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/6/15.
 */

public class ProjTypeDbModel {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private long id;

    private String data;

    private long createTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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


